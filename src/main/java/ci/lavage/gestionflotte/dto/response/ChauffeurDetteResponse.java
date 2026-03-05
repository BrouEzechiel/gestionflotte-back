package ci.lavage.gestionflotte.dto.response;

import java.math.BigDecimal;

public record ChauffeurDetteResponse(
        Long idChauffeur,
        String nomComplet,
        String telephone,
        BigDecimal montantTotalDette // La somme de tous ses reliquats
) {
}