package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.EtatVehicule;
import ci.lavage.gestionflotte.model.Vehicule;

public record VehiculeResponse(
        Long id,
        String immatriculation,
        String marque,
        String modele,
        String dateMiseEnCirculation,
        EtatVehicule etat,
        String dateAjout,
        Long idProprietaire,
        String nomCompletProprietaire
) {
    public VehiculeResponse(Vehicule vehicule) {
        this(
                vehicule.getId(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getDateMiseEnCirculation() != null ? vehicule.getDateMiseEnCirculation().toString() : null,
                vehicule.getEtat(),
                vehicule.getDateAjout() != null ? vehicule.getDateAjout().toString() : null,
                vehicule.getProprietaire() != null ? vehicule.getProprietaire().getId() : null,
                vehicule.getProprietaire() != null ? vehicule.getProprietaire().getNom() + " " + vehicule.getProprietaire().getPrenoms() : "Aucun propriétaire"
        );
    }
}