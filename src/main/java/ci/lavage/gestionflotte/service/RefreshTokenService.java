package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.model.RefreshToken;
import ci.lavage.gestionflotte.model.Utilisateur;
import ci.lavage.gestionflotte.repository.RefreshTokenRepository;
import ci.lavage.gestionflotte.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UtilisateurRepository utilisateurRepository;

    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    @Transactional
    public RefreshToken creerOuMettreAJourRefreshToken(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return refreshTokenRepository.findByUtilisateur(utilisateur)
                .map(token -> {
                    token.setToken(UUID.randomUUID().toString());
                    token.setDateExpiration(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION));
                    return refreshTokenRepository.save(token);
                })
                .orElseGet(() -> {
                    RefreshToken newRefreshToken = RefreshToken.builder()
                            .utilisateur(utilisateur)
                            .token(UUID.randomUUID().toString())
                            .dateExpiration(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION))
                            .build();
                    return refreshTokenRepository.save(newRefreshToken);
                });
    }

    public RefreshToken verifierExpiration(RefreshToken token) {
        if (token.getDateExpiration().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Le Refresh Token a expiré. Veuillez vous reconnecter.");
        }
        return token;
    }

    @Transactional
    public void supprimerParUtilisateur(Long utilisateurId) {
        utilisateurRepository.findById(utilisateurId).ifPresent(refreshTokenRepository::deleteByUtilisateur);
    }
}
