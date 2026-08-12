package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.response.ChauffeurDetteResponse;
import ci.lavage.gestionflotte.dto.response.DashboardStatsResponse;
import ci.lavage.gestionflotte.dto.response.KpiJournalierResponse;
import ci.lavage.gestionflotte.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // 1. Obtenir les KPIs d'une journée (Aujourd'hui par défaut si pas de date)
    @GetMapping("/kpi")
    public ResponseEntity<KpiJournalierResponse> obtenirKpis(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(dashboardService.obtenirKpiJournalier(date));
    }

    // 2. Obtenir la liste des chauffeurs endettés
    @GetMapping("/alertes-dettes")
    public ResponseEntity<List<ChauffeurDetteResponse>> obtenirAlertesDettes() {
        return ResponseEntity.ok(dashboardService.obtenirTopChauffeursEndettes());
    }

    // 3. Obtenir le chiffre d'affaires sur une période (ex: du 01/10 au 31/10)
    @GetMapping("/rentabilite")
    public ResponseEntity<BigDecimal> obtenirRentabilite(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(dashboardService.calculerRentabilite(debut, fin));
    }

    // 4. Télécharger le rapport Excel (CSV)
    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exporterRapportCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {

        String csvData = dashboardService.genererRapportCsv(debut, fin);
        byte[] output = csvData.getBytes();

        HttpHeaders headers = new HttpHeaders();
        // On indique au navigateur que c'est un fichier à télécharger
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=rapport_versements_" + debut + "_au_" + fin + ".csv");
        headers.setContentType(MediaType.parseMediaType("text/csv"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(output);
    }

    @GetMapping("/stats-generales")
    public ResponseEntity<DashboardStatsResponse> getStatsGenerales() {
        return ResponseEntity.ok(dashboardService.getStatsGenerales());
    }
}