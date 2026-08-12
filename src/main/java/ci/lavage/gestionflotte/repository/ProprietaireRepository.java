package ci.lavage.gestionflotte.repository;

import ci.lavage.gestionflotte.dto.response.EtatProprietaireResponse;
import ci.lavage.gestionflotte.model.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProprietaireRepository extends JpaRepository<Proprietaire, Long> {

    // Requête JPQL pour calculer directement les totaux par propriétaire
    @Query("SELECT new ci.lavage.gestionflotte.dto.response.EtatProprietaireResponse(" +
            "p.id, p.nom, p.prenoms, " +
            "(SELECT COALESCE(SUM(v.montantVerse), 0) FROM Versement v WHERE v.affectation.vehicule.proprietaire.id = p.id), " +
            "(SELECT COALESCE(SUM(d.montant), 0) FROM Depense d WHERE d.vehicule.proprietaire.id = p.id)) " +
            "FROM Proprietaire p")
    List<EtatProprietaireResponse> calculerGainsTousProprietaires();

    boolean existsByTelephone(String telephone);
}
