package ci.lavage.gestionflotte.dto.request;

import ci.lavage.gestionflotte.enums.RoleUtilisateur;

public record RegisterRequest(
        String identifiant,
        String motDePasse,
        RoleUtilisateur role,
        String nom,    // <-- Nouveau
        String prenom  // <-- Nouveau
) {}