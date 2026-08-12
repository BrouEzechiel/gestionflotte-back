package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.ProprietaireRequest;
import ci.lavage.gestionflotte.dto.response.EtatProprietaireResponse;
import ci.lavage.gestionflotte.dto.response.ProprietaireResponse;
import ci.lavage.gestionflotte.service.ExportService;
import ci.lavage.gestionflotte.service.ProprietaireService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proprietaires")
public class ProprietaireController {

    private final ProprietaireService proprietaireService;
    private final ExportService exportService;

    public ProprietaireController(ProprietaireService proprietaireService, ExportService exportService) {
        this.proprietaireService = proprietaireService;
        this.exportService = exportService;
    }

    /**
     * 1. Ajouter un nouveau propriétaire
     * POST /api/proprietaires
     */
    @PostMapping
    public ResponseEntity<ProprietaireResponse> ajouterProprietaire(@RequestBody ProprietaireRequest request) {
        ProprietaireResponse response = proprietaireService.enregistrerProprietaire(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. Lister tous les propriétaires (Infos classiques)
     * GET /api/proprietaires
     */
    @GetMapping
    public ResponseEntity<List<ProprietaireResponse>> listerProprietaires() {
        List<ProprietaireResponse> liste = proprietaireService.listerProprietaires();
        return ResponseEntity.ok(liste);
    }

    /**
     * 3. Obtenir les états financiers (Total versements, dépenses et gains)
     * GET /api/proprietaires/etats
     */
    @GetMapping("/etats")
    public ResponseEntity<List<EtatProprietaireResponse>> obtenirEtatsFinanciers() {
        List<EtatProprietaireResponse> etats = proprietaireService.calculerEtatsFinanciers();
        return ResponseEntity.ok(etats);
    }

    /**
     * 4. Exporter les états financiers en EXCEL
     * GET /api/proprietaires/etats/export/excel
     */
    @GetMapping("/etats/export/excel")
    public ResponseEntity<byte[]> exporterEtatsExcel() {
        List<EtatProprietaireResponse> etats = proprietaireService.calculerEtatsFinanciers();
        byte[] excelContent = exportService.genererEtatsExcel(etats);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etats_financiers.xlsx");
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
    }

    /**
     * 5. Exporter les états financiers en PDF
     * GET /api/proprietaires/etats/export/pdf
     */
    @GetMapping("/etats/export/pdf")
    public ResponseEntity<byte[]> exporterEtatsPdf() {
        List<EtatProprietaireResponse> etats = proprietaireService.calculerEtatsFinanciers();
        byte[] pdfContent = exportService.genererEtatsPdf(etats);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etats_financiers.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }
}