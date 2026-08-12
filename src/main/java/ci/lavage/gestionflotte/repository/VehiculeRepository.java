package ci.lavage.gestionflotte.repository;

import ci.lavage.gestionflotte.enums.EtatVehicule;
import ci.lavage.gestionflotte.model.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    // Pour empêcher les doublons à la création
    boolean existsByImmatriculation(String immatriculation);

    // Pour lister et filtrer (On exclut automatiquement ceux qui sont ARCHIVE par défaut si on veut)
    @Query("SELECT v FROM Vehicule v " +
            "WHERE (:#{#etat == null} = true OR v.etat = :etat) " +
            "AND LOWER(v.marque) LIKE LOWER(CONCAT('%', :marque, '%')) " +
            "ORDER BY v.id DESC")
    List<Vehicule> findByFiltres(@Param("etat") EtatVehicule etat, @Param("marque") String marque);

    long countByEtat(EtatVehicule etat);
}