package ci.lavage.gestionflotte.repository;

import ci.lavage.gestionflotte.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    // Crucial pour charger l'utilisateur par son nom au login
    Optional<Utilisateur> findByIdentifiant(String identifiant);
}