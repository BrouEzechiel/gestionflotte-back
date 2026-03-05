package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.VehiculeRequest;
import ci.lavage.gestionflotte.dto.response.VehiculeResponse;
import ci.lavage.gestionflotte.enums.EtatVehicule;
import ci.lavage.gestionflotte.service.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicules")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    /**
     * 1. Ajouter un nouveau véhicule
     */
    @PostMapping
    public ResponseEntity<VehiculeResponse> ajouterVehicule(@RequestBody VehiculeRequest request) {
        VehiculeResponse response = vehiculeService.ajouterVehicule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 2. Lister les véhicules avec filtres optionnels (par état ou par marque)
     * Exemple : GET /api/vehicules?etat=ACTIF&marque=Toyota
     */
    @GetMapping
    public ResponseEntity<List<VehiculeResponse>> listerVehicules(
            @RequestParam(required = false) EtatVehicule etat,
            @RequestParam(required = false) String marque
    ) {
        List<VehiculeResponse> liste = vehiculeService.listerVehicules(etat, marque);
        return ResponseEntity.ok(liste);
    }

    /**
     * 3. Mettre à jour l'état d'un véhicule (ex: le passer EN_PANNE)
     * Exemple : PATCH /api/vehicules/1/etat?nouvelEtat=EN_PANNE
     */
    @PatchMapping("/{id}/etat")
    public ResponseEntity<VehiculeResponse> changerEtat(
            @PathVariable Long id,
            @RequestParam EtatVehicule nouvelEtat
    ) {
        VehiculeResponse response = vehiculeService.changerEtat(id, nouvelEtat);
        return ResponseEntity.ok(response);
    }

    /**
     * 4. Archiver un véhicule (Soft Delete)
     * L'URL utilise la méthode DELETE pour respecter les standards REST,
     * mais en base de données, on le passe juste en statut ARCHIVE.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> archiverVehicule(@PathVariable Long id) {
        vehiculeService.archiverVehicule(id);
        return ResponseEntity.ok("Le véhicule a été archivé avec succès.");
    }
}