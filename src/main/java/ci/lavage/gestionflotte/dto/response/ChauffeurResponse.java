package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.StatutChauffeur;
import ci.lavage.gestionflotte.model.Chauffeur;

import java.time.LocalDateTime;

public record ChauffeurResponse(
        Long id,
        String nom,
        String prenoms,
        String telephone,
        String numeroPermis,
        String adresse,
        StatutChauffeur statut,
        LocalDateTime dateEnregistrement
) {
    public ChauffeurResponse(Chauffeur chauffeur) {
        this(
                chauffeur.getId(),
                chauffeur.getNom(),
                chauffeur.getPrenoms(),
                chauffeur.getTelephone(),
                chauffeur.getNumeroPermis(),
                chauffeur.getAdresse(),
                chauffeur.getStatut(),
                chauffeur.getDateEnregistrement()
        );
    }
}
