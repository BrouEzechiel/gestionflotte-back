package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.StatutVersement;
import ci.lavage.gestionflotte.model.Versement;

import java.math.BigDecimal;
import java.time.LocalDate;


public record VersementResponse(
        Long id,
        LocalDate dateVersement,
        BigDecimal montantVerse,
        BigDecimal ecart,
        StatutVersement statut,
        Long idAffectation,
        Long idReliquatRegle,
        String nomChauffeur,
        String marqueVehicule
) {

    public VersementResponse(Versement versement) {
        this(
                versement.getId(),
                versement.getDateVersement(),
                versement.getMontantVerse(),
                versement.getEcart(),
                versement.getStatut(),

                (versement.getAffectation() != null) ? versement.getAffectation().getId() : null,

                (versement.getReliquatRegle() != null) ? versement.getReliquatRegle().getId() : null,

                (versement.getAffectation() != null && versement.getAffectation().getChauffeur() != null)
                        ? versement.getAffectation().getChauffeur().getNom() : null,

                (versement.getAffectation() != null && versement.getAffectation().getVehicule() != null)
                        ? versement.getAffectation().getVehicule().getMarque() : null
        );
    }
}