package ci.lavage.gestionflotte.repository;

import ci.lavage.gestionflotte.model.Depense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DepenseRepository extends JpaRepository<Depense, Long> {
    // Lister toutes les dépenses de la plus récente à la plus ancienne
    List<Depense> findAllByOrderByDateDepenseDesc();

    // Si plus tard on veut voir les dépenses d'une seule voiture :
    List<Depense> findByVehiculeIdOrderByDateDepenseDesc(Long vehiculeId);

    @Query("SELECT SUM(d.montant) FROM Depense d WHERE d.dateDepense = :date")
    BigDecimal calculerTotalDepensesParDate(@Param("date") LocalDate date);

    @Query("SELECT SUM(d.montant) FROM Depense d WHERE d.dateDepense BETWEEN :debut AND :fin")
    BigDecimal calculerTotalDepensesSurPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}