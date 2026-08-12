package ci.lavage.gestionflotte.dto.response;

import java.math.BigDecimal;

public record EtatProprietaireResponse(
        Long idProprietaire,
        String nom,
        String prenoms,
        BigDecimal totalVersements,
        BigDecimal totalDepenses
) {
    /**
     * Méthode personnalisée pour obtenir le gain net (Versements - Dépenses).
     * Ton frontend (ou JSON) pourra récupérer cette valeur facilement.
     */
    public BigDecimal getGainNet() {
        BigDecimal versements = (totalVersements != null) ? totalVersements : BigDecimal.ZERO;
        BigDecimal depenses = (totalDepenses != null) ? totalDepenses : BigDecimal.ZERO;
        return versements.subtract(depenses);
    }
}