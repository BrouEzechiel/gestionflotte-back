package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.StatutChauffeur;

import java.math.BigDecimal;
import java.util.List;

public record FicheChauffeurResponse(
        Long id,
        String nomComplet,
        String telephone,
        String numeroPermis,
        StatutChauffeur statut,
        BigDecimal soldeFinancier, // C'est ici qu'on stocke l'argent !
        List<AffectationResponse> historiqueVehicules // C'est ici qu'on stocke la liste !
) {
}
