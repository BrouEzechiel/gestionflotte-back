package ci.lavage.gestionflotte.dto.request;

import ci.lavage.gestionflotte.enums.EtatVehicule;

import java.time.LocalDate;

public record VehiculeRequest(
        Long idProprietaire,
        String immatriculation,
        String marque,
        String modele,
        LocalDate dateMiseEnCirculation,
        EtatVehicule etat
) {
}
