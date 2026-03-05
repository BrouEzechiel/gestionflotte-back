package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.response.ChauffeurDetteResponse;
import ci.lavage.gestionflotte.dto.response.KpiJournalierResponse;
import ci.lavage.gestionflotte.model.Versement;
import ci.lavage.gestionflotte.repository.AffectationRepository;
import ci.lavage.gestionflotte.repository.VersementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {

    private final AffectationRepository affectationRepository;
    private final VersementRepository versementRepository;

    public DashboardService(AffectationRepository affectationRepository, VersementRepository versementRepository) {
        this.affectationRepository = affectationRepository;
        this.versementRepository = versementRepository;
    }

    /**
     * 1. Les KPIs (Chiffres clés) d'une journée précise
     */
    @Transactional(readOnly = true)
    public KpiJournalierResponse obtenirKpiJournalier(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        // Récupération des données brutes avec gestion des valeurs nulles (si aucun versement ce jour-là)
        BigDecimal recetteAttendue = affectationRepository.calculerRecetteAttendueJournaliereGlobale();
        BigDecimal totalEncaisse = versementRepository.calculerTotalEncaisseParDate(date);
        BigDecimal totalImpayes = versementRepository.calculerTotalImpayesParDate(date);

        // Remplacement des null par 0 pour éviter les bugs d'affichage sur le frontend
        if (recetteAttendue == null) recetteAttendue = BigDecimal.ZERO;
        if (totalEncaisse == null) totalEncaisse = BigDecimal.ZERO;
        if (totalImpayes == null) totalImpayes = BigDecimal.ZERO;

        return new KpiJournalierResponse(recetteAttendue, totalEncaisse, totalImpayes.abs()); // .abs() pour afficher un nombre positif
    }

    /**
     * 2. Le classement des chauffeurs qui doivent de l'argent (Top Dettes)
     */
    @Transactional(readOnly = true)
    public List<ChauffeurDetteResponse> obtenirTopChauffeursEndettes() {
        return versementRepository.trouverTopChauffeursEndettes();
    }

    /**
     * 3. La Rentabilité (Chiffre d'affaires) sur une période
     */
    @Transactional(readOnly = true)
    public BigDecimal calculerRentabilite(LocalDate debut, LocalDate fin) {
        BigDecimal rentabilite = versementRepository.calculerTotalEncaisseSurPeriode(debut, fin);
        return rentabilite != null ? rentabilite : BigDecimal.ZERO;
    }

    /**
     * 4. Générer un rapport CSV (Excel) des versements pour une période
     * C'est une chaîne de caractères formatée que le frontend transformera en fichier téléchargeable.
     */
    @Transactional(readOnly = true)
    public String genererRapportCsv(LocalDate debut, LocalDate fin) {
        // On réutilise la méthode d'historique qu'on avait créée dans VersementRepository !
        // En passant null pour nom et marque, on récupère tout sur la période.
        List<Versement> versements = versementRepository.findAll().stream()
                .filter(v -> !v.getDateVersement().isBefore(debut) && !v.getDateVersement().isAfter(fin))
                .toList();

        StringBuilder csvBuilder = new StringBuilder();
        // En-tête du fichier Excel
        csvBuilder.append("ID Versement;Date;Chauffeur;Vehicule;Montant Verse;Ecart;Statut\n");

        // Remplissage des lignes
        for (Versement v : versements) {
            csvBuilder.append(v.getId()).append(";")
                    .append(v.getDateVersement()).append(";")
                    .append(v.getAffectation().getChauffeur().getNom()).append(" ").append(v.getAffectation().getChauffeur().getPrenoms()).append(";")
                    .append(v.getAffectation().getVehicule().getImmatriculation()).append(";")
                    .append(v.getMontantVerse()).append(";")
                    .append(v.getEcart()).append(";")
                    .append(v.getStatut()).append("\n");
        }

        return csvBuilder.toString();
    }
}