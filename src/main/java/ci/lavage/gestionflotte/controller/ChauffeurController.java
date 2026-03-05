package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.ChauffeurRequest;
import ci.lavage.gestionflotte.dto.response.ChauffeurResponse;
import ci.lavage.gestionflotte.dto.response.FicheChauffeurResponse;
import ci.lavage.gestionflotte.enums.StatutChauffeur;
import ci.lavage.gestionflotte.service.ChauffeurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chauffeurs")
public class ChauffeurController {

    private final ChauffeurService chauffeurService;

    public ChauffeurController(ChauffeurService chauffeurService) {
        this.chauffeurService = chauffeurService;
    }

    /**
     * 1. Enregistrer un nouveau chauffeur
     * POST /api/chauffeurs
     */
    @PostMapping
    public ResponseEntity<ChauffeurResponse> enregistrerChauffeur(@RequestBody ChauffeurRequest request) {
        ChauffeurResponse response = chauffeurService.enregistrerChauffeur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. Lister tous les chauffeurs (Aperçu rapide)
     * GET /api/chauffeurs
     */
    @GetMapping
    public ResponseEntity<List<ChauffeurResponse>> listerChauffeurs() {
        List<ChauffeurResponse> liste = chauffeurService.listerChauffeurs();
        return ResponseEntity.ok(liste);
    }

    /**
     * 3. Consulter la FICHE COMPLÈTE d'un chauffeur (avec argent et historique)
     * GET /api/chauffeurs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<FicheChauffeurResponse> consulterFicheChauffeur(@PathVariable Long id) {
        FicheChauffeurResponse fiche = chauffeurService.consulterFicheChauffeur(id);
        return ResponseEntity.ok(fiche);
    }

    /**
     * 4. Modifier les informations de base d'un chauffeur
     * PUT /api/chauffeurs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChauffeurResponse> modifierChauffeur(
            @PathVariable Long id,

            @RequestBody ChauffeurRequest request
    ) {
        ChauffeurResponse response = chauffeurService.modifierChauffeur(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 5. Changer uniquement le statut d'un chauffeur (ex: ACTIF -> SUSPENDU)
     * PATCH /api/chauffeurs/{id}/statut?nouveauStatut=SUSPENDU
     */
    @PatchMapping("/{id}/statut")
    public ResponseEntity<ChauffeurResponse> changerStatut(
            @PathVariable Long id,
            @RequestParam StatutChauffeur nouveauStatut
    ) {
        ChauffeurResponse response = chauffeurService.changerStatut(id, nouveauStatut);
        return ResponseEntity.ok(response);
    }
}