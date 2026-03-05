package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.VersementRequest;
import ci.lavage.gestionflotte.dto.response.VersementResponse;
import ci.lavage.gestionflotte.service.VersementService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/versements")
public class VersementController {

    private final VersementService versementService;

    public VersementController(VersementService versementService) {
        this.versementService = versementService;
    }

    /**
     * Endpoint pour enregistrer un nouveau versement.
     */
    @PostMapping
    public ResponseEntity<VersementResponse> enregistrerVersement(@RequestBody VersementRequest request) {

        // Plus de try/catch ! Si une exception métier est levée dans le service,
        // le @RestControllerAdvice va l'intercepter automatiquement.
        VersementResponse resultat = versementService.enregistrerVersement(
                request.idAffectation(),
                request.montantVerse()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resultat);
    }

    /**
     * Endpoint pour récupérer l'historique des versements avec filtres optionnels.
     */
    @GetMapping
    public ResponseEntity<List<VersementResponse>> obtenirHistorique(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String nomChauffeur,
            @RequestParam(required = false) String marqueVehicule
    ) {
        List<VersementResponse> historique = versementService.obtenirHistoriqueFiltre(date, nomChauffeur, marqueVehicule);
        return ResponseEntity.ok(historique);
    }
}