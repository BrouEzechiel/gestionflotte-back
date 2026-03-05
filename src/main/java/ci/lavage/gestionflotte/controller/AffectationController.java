package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.AffectationRequest;
import ci.lavage.gestionflotte.dto.response.AffectationResponse;
import ci.lavage.gestionflotte.service.AffectationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/affectations")
public class AffectationController {

    private final AffectationService affectationService;

    // L'injection de dépendance se fait proprement via le constructeur
    public AffectationController(AffectationService affectationService) {
        this.affectationService = affectationService;
    }

    /**
     * 1. Créer une nouvelle affectation (Contrat)
     * Exemple : POST http://localhost:8080/api/affectations
     */
    @PostMapping
    public ResponseEntity<AffectationResponse> creerAffectation(@RequestBody AffectationRequest request) {
        AffectationResponse response = affectationService.creerAffectation(request);

        // On retourne 201 (CREATED) car une nouvelle ressource a été ajoutée en base
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. Clôturer une affectation existante
     * Exemple : PATCH http://localhost:8080/api/affectations/5/cloturer
     */
    @PatchMapping("/{id}/cloturer")
    public ResponseEntity<AffectationResponse> cloturerAffectation(@PathVariable("id") Long idAffectation) {
        AffectationResponse response = affectationService.cloturerAffectation(idAffectation);

        // On retourne 200 (OK) avec l'affectation mise à jour
        return ResponseEntity.ok(response);
    }
}