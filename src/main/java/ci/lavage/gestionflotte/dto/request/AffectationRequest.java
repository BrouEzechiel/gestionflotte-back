package ci.lavage.gestionflotte.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AffectationRequest(
        Long idChauffeur,
        Long idVehicule,
        BigDecimal recetteAttendueJournaliere,
        LocalDate dateDebut // Si l'utilisateur ne l'envoie pas, on prendra la date du jour
) {
}