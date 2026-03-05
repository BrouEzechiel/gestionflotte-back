package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.model.RefreshToken;
import ci.lavage.gestionflotte.repository.RefreshTokenRepository;
import ci.lavage.gestionflotte.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UtilisateurRepository utilisateurRepository;

    // Durée de vie du Refresh Token : 7 jours (en millisecondes)
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    public RefreshToken creerRefreshToken(Long utilisateurId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .utilisateur(utilisateurRepository.findById(utilisateurId).orElseThrow())
                .token(UUID.randomUUID().toString()) // Génère un token aléatoire unique
                .dateExpiration(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifierExpiration(RefreshToken token) {
        if (token.getDateExpiration().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Le Refresh Token a expiré. Veuillez vous reconnecter.");
        }
        return token;
    }

    public void supprimerParUtilisateur(Long utilisateurId) {
        utilisateurRepository.findById(utilisateurId).ifPresent(refreshTokenRepository::deleteByUtilisateur);
    }
}