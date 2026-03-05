package ci.lavage.gestionflotte.repository;

import ci.lavage.gestionflotte.model.Chauffeur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long> {
    // Vous pouvez ajouter des méthodes spécifiques si nécessaire
    // Règle métier : Empêcher les doublons
    boolean existsByTelephone(String telephone);
    boolean existsByNumeroPermis(String numeroPermis);
}
