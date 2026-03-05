package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.EtatVehicule;
import ci.lavage.gestionflotte.model.Vehicule;

public record VehiculeResponse(
        Long id,
        String immatriculation,
        String marque,
        String modele,
        EtatVehicule etat
) {
    public VehiculeResponse(Vehicule vehicule) {
        this(
                vehicule.getId(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getEtat()
        );
    }
}
