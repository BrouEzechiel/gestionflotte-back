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
        utilisateur.setNom(request.nom());
        utilisateur.setPrenom(request.prenom());

        utilisateurRepository.save(utilisateur);

        var jwtToken = jwtService.generateToken(utilisateur);
        var refreshToken = refreshTokenService.creerOuMettreAJourRefreshToken(utilisateur.getId());

        // --- CORRECTION ICI : Ajout du nom et prénom ---
        return new AuthenticationResponse(
                jwtToken,
                refreshToken.getToken(),
                utilisateur.getRole().name(),
                utilisateur.getNom(),
                utilisateur.getPrenom()
        );
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
        var refreshToken = refreshTokenService.creerOuMettreAJourRefreshToken(utilisateur.getId());

        // --- CORRECTION ICI : Ajout du nom et prénom ---
        return new AuthenticationResponse(
                jwtToken,
                refreshToken.getToken(),
                utilisateur.getRole().name(),
                utilisateur.getNom(),
                utilisateur.getPrenom()
        );
    }

    public AuthenticationResponse rafraichirToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh Token introuvable !"));

        refreshTokenService.verifierExpiration(refreshToken);

        Utilisateur utilisateur = refreshToken.getUtilisateur();
        String nouveauJwtToken = jwtService.generateToken(utilisateur);

        // --- CORRECTION ICI : Ajout du nom et prénom ---
        return new AuthenticationResponse(
                nouveauJwtToken,
                refreshToken.getToken(),
                utilisateur.getRole().name(),
                utilisateur.getNom(),
                utilisateur.getPrenom()
        );
    }
}