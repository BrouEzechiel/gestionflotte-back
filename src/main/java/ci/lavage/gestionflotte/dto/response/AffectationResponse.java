package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.StatutAffectation;
import ci.lavage.gestionflotte.model.Affectation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AffectationResponse(
        Long id,
        LocalDate dateDebut,
        LocalDate dateFin,
        BigDecimal recetteAttendueJournaliere,
        StatutAffectation statut,
        Long idChauffeur,
        Long idVehicule
) {
    public AffectationResponse(Affectation affectation) {
        this(
                affectation.getId(),
                affectation.getDateDebut(),
                affectation.getDateFin(),
                affectation.getRecetteAttendueJournaliere(),
                affectation.getStatut(),
                affectation.getChauffeur() != null ? affectation.getChauffeur().getId() : null,
                affectation.getVehicule() != null ? affectation.getVehicule().getId() : null
        );
    }
}
