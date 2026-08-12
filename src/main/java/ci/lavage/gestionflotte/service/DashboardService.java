package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.response.ChauffeurDetteResponse;
import ci.lavage.gestionflotte.dto.response.DashboardStatsResponse;
import ci.lavage.gestionflotte.dto.response.KpiJournalierResponse;
import ci.lavage.gestionflotte.dto.response.RecetteJournaliere;
import ci.lavage.gestionflotte.enums.EtatVehicule;
import ci.lavage.gestionflotte.enums.StatutChauffeur;
import ci.lavage.gestionflotte.model.Versement;
import ci.lavage.gestionflotte.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final AffectationRepository affectationRepository;
    private final VersementRepository versementRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final VehiculeRepository vehiculeRepository;
    private final DepenseRepository depenseRepository;


    public DashboardService(AffectationRepository affectationRepository, VersementRepository versementRepository, ChauffeurRepository chauffeurRepository, VehiculeRepository vehiculeRepository, DepenseRepository depenseRepository) {
        this.affectationRepository = affectationRepository;
        this.versementRepository = versementRepository;
        this.chauffeurRepository = chauffeurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.depenseRepository = depenseRepository;
    }

    /**
     * 1. Les KPIs (Chiffres clés) d'une journée précise
     */
    @Transactional(readOnly = true)
    public KpiJournalierResponse obtenirKpiJournalier(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }

        BigDecimal recetteAttendue = affectationRepository.calculerRecetteAttendueJournaliereGlobale();
        BigDecimal totalEncaisse = versementRepository.calculerTotalEncaisseParDate(date);
        BigDecimal totalImpayes = versementRepository.calculerTotalImpayesParDate(date);


        BigDecimal totalDepenses = depenseRepository.calculerTotalDepensesParDate(date);

        if (recetteAttendue == null) recetteAttendue = BigDecimal.ZERO;
        if (totalEncaisse == null) totalEncaisse = BigDecimal.ZERO;
        if (totalImpayes == null) totalImpayes = BigDecimal.ZERO;
        if (totalDepenses == null) totalDepenses = BigDecimal.ZERO;

        // Calcul du bénéfice net de la journée (Ce qui est rentré - Ce qui est sorti)
        BigDecimal beneficeNet = totalEncaisse.subtract(totalDepenses);

        return new KpiJournalierResponse(
                recetteAttendue,
                totalEncaisse,
                totalImpayes.abs(),
                totalDepenses, // On envoie les dépenses au frontend
                beneficeNet    // On envoie le bénéfice net au frontend
        );
    }

    /**
     * 2. Le classement des chauffeurs qui doivent de l'argent (Top Dettes)
     */
    @Transactional(readOnly = true)
    public List<ChauffeurDetteResponse> obtenirTopChauffeursEndettes() {
        return versementRepository.trouverTopChauffeursEndettes();
    }

    /**
     * 3. La Vraie Rentabilité (Bénéfice Net) sur une période
     */
    @Transactional(readOnly = true)
    public BigDecimal calculerRentabilite(LocalDate debut, LocalDate fin) {
        BigDecimal recettes = versementRepository.calculerTotalEncaisseSurPeriode(debut, fin);
        BigDecimal depenses = depenseRepository.calculerTotalDepensesSurPeriode(debut, fin);

        if (recettes == null) recettes = BigDecimal.ZERO;
        if (depenses == null) depenses = BigDecimal.ZERO;

        // Rentabilité = Ce qui est rentré MOINS ce qui est sorti
        return recettes.subtract(depenses);
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


    public DashboardStatsResponse getStatsGenerales() {

        long chauffeursActifs = chauffeurRepository.countByStatut(StatutChauffeur.ACTIF);
        long vehiculesTotal = vehiculeRepository.count();
        long vehiculesEnService = vehiculeRepository.countByEtat(EtatVehicule.ACTIF);
        long vehiculesEnPanne = vehiculeRepository.countByEtat(EtatVehicule.EN_PANNE);


        List<RecetteJournaliere> recettesSur7Jours = new ArrayList<>();
        LocalDate today = LocalDate.now();


        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            // On réutilise votre méthode existante !
            KpiJournalierResponse kpi = obtenirKpiJournalier(date);
            recettesSur7Jours.add(new RecetteJournaliere(date, kpi.totalEncaisse()));
        }

        return new DashboardStatsResponse(
                chauffeursActifs,
                vehiculesTotal,
                vehiculesEnService,
                vehiculesEnPanne,
                recettesSur7Jours
        );
    }
}