package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.RoleUtilisateur;
import ci.lavage.gestionflotte.model.Utilisateur;

import java.time.LocalDateTime;

public record UtilisateurResponse(
        Long id,
        String identifiant,
        LocalDateTime dateCreation,
        RoleUtilisateur role
) {

    public UtilisateurResponse(Utilisateur utilisateur) {
        this(
                utilisateur.getId(),
                utilisateur.getIdentifiant(),
                utilisateur.getDateCreation(),
                utilisateur.getRole()
        );
    }
}