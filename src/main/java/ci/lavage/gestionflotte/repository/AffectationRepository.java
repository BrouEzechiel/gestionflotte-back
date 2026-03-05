package ci.lavage.gestionflotte.repository;

import ci.lavage.gestionflotte.enums.StatutAffectation;
import ci.lavage.gestionflotte.model.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface AffectationRepository extends JpaRepository<Affectation, Long> {
    // Vous pouvez ajouter des méthodes spécifiques si nécessaire
    boolean existsByVehiculeIdAndStatut(Long idVehicule, StatutAffectation statut);

    // Récupère toutes les affectations d'un chauffeur (son historique de véhicules)
    List<Affectation> findByChauffeurIdOrderByDateDebutDesc(Long idChauffeur);

    // Calcule la recette attendue pour toutes les affectations actives
    @Query("SELECT SUM(a.recetteAttendueJournaliere) FROM Affectation a WHERE a.statut = 'EN_COURS'")
    BigDecimal calculerRecetteAttendueJournaliereGlobale();
}
