package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.model.Proprietaire;

public record ProprietaireResponse(
        Long id,
        String nom,
        String prenoms,
        String telephone
) {
    // Petit constructeur pratique pour transformer l'Entité en DTO facilement
    public ProprietaireResponse(Proprietaire proprietaire) {
        this(
                proprietaire.getId(),
                proprietaire.getNom(),
                proprietaire.getPrenoms(),
                proprietaire.getTelephone()
        );
    }
}