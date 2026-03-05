package ci.lavage.gestionflotte.repository;

import ci.lavage.gestionflotte.dto.response.ChauffeurDetteResponse;
import ci.lavage.gestionflotte.enums.StatutVersement;
import ci.lavage.gestionflotte.model.Versement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VersementRepository extends JpaRepository<Versement, Long> {
    // Vous pouvez ajouter des méthodes spécifiques si nécessaire
    List<Versement> findByAffectationIdAndStatutOrderByDateVersementAsc(Long idAffectation, StatutVersement statut);

    @Query("SELECT v FROM Versement v " +
            "WHERE (:date IS NULL OR v.dateVersement = :date) " +
            "AND (:nomChauffeur IS NULL OR LOWER(v.affectation.chauffeur.nom) LIKE LOWER(CONCAT('%', :nomChauffeur, '%'))) " +
            "AND (:marqueVehicule IS NULL OR LOWER(v.affectation.vehicule.marque) LIKE LOWER(CONCAT('%', :marqueVehicule, '%'))) " +
            "ORDER BY v.dateVersement DESC")
    List<Versement> findHistoriqueByFiltres(
            @Param("date") LocalDate date,
            @Param("nomChauffeur") String nomChauffeur,
            @Param("marqueVehicule") String marqueVehicule
    );

    // Calcule le solde total d'un chauffeur (Somme des écarts des versements non soldés)
    // Résultat < 0 : Il doit de l'argent. Résultat > 0 : Il a une avance.
    @Query("SELECT SUM(v.ecart) FROM Versement v " +
            "WHERE v.affectation.chauffeur.id = :idChauffeur " +
            "AND v.statut IN ('RELIQUAT', 'AVANCE')")
    BigDecimal calculerSoldeFinancierChauffeur(@Param("idChauffeur") Long idChauffeur);

    // 1. Calcule le total encaissé pour une date précise
    @Query("SELECT SUM(v.montantVerse) FROM Versement v WHERE v.dateVersement = :date")
    java.math.BigDecimal calculerTotalEncaisseParDate(@Param("date") java.time.LocalDate date);

    // 2. Trouve les chauffeurs ayant des dettes et les classe du plus endetté au moins endetté
    // On utilise notre nouveau DTO directement dans la requête !
    @Query("SELECT new ci.lavage.gestionflotte.dto.response.ChauffeurDetteResponse(" +
            "v.affectation.chauffeur.id, " +
            "CONCAT(v.affectation.chauffeur.nom, ' ', v.affectation.chauffeur.prenoms), " +
            "v.affectation.chauffeur.telephone, " +
            "SUM(v.ecart)) " +
            "FROM Versement v " +
            "WHERE v.statut = 'RELIQUAT' " +
            "GROUP BY v.affectation.chauffeur.id, v.affectation.chauffeur.nom, v.affectation.chauffeur.prenoms, v.affectation.chauffeur.telephone " +
            "ORDER BY SUM(v.ecart) ASC") // ASC car les dettes (écarts) sont des nombres négatifs (-5000 est plus petit que -1000)
    List<ChauffeurDetteResponse> trouverTopChauffeursEndettes();

    // Calcule le total des impayés (seulement les écarts négatifs) pour un jour précis
    @Query("SELECT SUM(v.ecart) FROM Versement v WHERE v.dateVersement = :date AND v.ecart < 0")
    BigDecimal calculerTotalImpayesParDate(@Param("date") LocalDate date);

    // Calcule le chiffre d'affaires sur une période (Rentabilité mensuelle/hebdomadaire)
    @Query("SELECT SUM(v.montantVerse) FROM Versement v WHERE v.dateVersement BETWEEN :debut AND :fin")
    BigDecimal calculerTotalEncaisseSurPeriode(@Param("debut") LocalDate debut, @Param("fin") LocalDate fin);
}
