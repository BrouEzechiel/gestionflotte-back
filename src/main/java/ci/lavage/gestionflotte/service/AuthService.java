package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.request.AuthenticationRequest;
import ci.lavage.gestionflotte.dto.request.RegisterRequest;
import ci.lavage.gestionflotte.dto.request.TokenRefreshRequest;
import ci.lavage.gestionflotte.dto.response.AuthenticationResponse;
import ci.lavage.gestionflotte.model.RefreshToken;
import ci.lavage.gestionflotte.model.Utilisateur;
import ci.lavage.gestionflotte.repository.RefreshTokenRepository;
import ci.lavage.gestionflotte.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // NOUVEAUX SERVICES INJECTÉS
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthenticationResponse inscrire(RegisterRequest request) {
        if (utilisateurRepository.findByIdentifiant(request.identifiant()).isPresent()) {
            throw new RuntimeException("Cet identifiant existe déjà !");
        }

        var utilisateur = new Utilisateur();
        utilisateur.setIdentifiant(request.identifiant());
        utilisateur.setMotDePasseHash(passwordEncoder.encode(request.motDePasse()));
        utilisateur.setRole(request.role());

        utilisateurRepository.save(utilisateur);

        var jwtToken = jwtService.generateToken(utilisateur);
        var refreshToken = refreshTokenService.creerRefreshToken(utilisateur.getId()); // On crée le Refresh Token

        return new AuthenticationResponse(jwtToken, refreshToken.getToken(), utilisateur.getRole().name());
    }

    public AuthenticationResponse authentifier(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.identifiant(),
                        request.motDePasse()
                )
        );

        var utilisateur = utilisateurRepository.findByIdentifiant(request.identifiant())
                .orElseThrow();

        var jwtToken = jwtService.generateToken(utilisateur);
        var refreshToken = refreshTokenService.creerRefreshToken(utilisateur.getId()); // On crée le Refresh Token

        return new AuthenticationResponse(jwtToken, refreshToken.getToken(), utilisateur.getRole().name());
    }

    // NOUVELLE MÉTHODE : Pour générer un nouveau Access Token à partir du Refresh Token
    public AuthenticationResponse rafraichirToken(TokenRefreshRequest request) {
        // 1. On cherche le Refresh Token en base de données
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh Token introuvable !"));

        // 2. On vérifie qu'il n'est pas périmé (sinon une erreur est lancée)
        refreshTokenService.verifierExpiration(refreshToken);

        // 3. Tout est bon ! On récupère l'utilisateur et on lui donne un nouvel Access Token de 15 min
        Utilisateur utilisateur = refreshToken.getUtilisateur();
        String nouveauJwtToken = jwtService.generateToken(utilisateur);

        // 4. On renvoie le nouveau Access Token, en gardant le même Refresh Token
        return new AuthenticationResponse(nouveauJwtToken, refreshToken.getToken(), utilisateur.getRole().name());
    }
}