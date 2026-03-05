package ci.lavage.gestionflotte.dto.request;

import java.time.LocalDate;

public record VehiculeRequest(
        String immatriculation,
        String marque,
        String modele,
        LocalDate dateMiseEnCirculation
) {
}