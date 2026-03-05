package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.response.UtilisateurResponse;
import ci.lavage.gestionflotte.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;

    /**
     * GET /api/utilisateurs
     * Permet à l'administrateur de voir tous les employés enregistrés.
     * C'est ici que ton UtilisateurResponse brille en cachant les mots de passe !
     */
    @GetMapping
    public ResponseEntity<List<UtilisateurResponse>> listerUtilisateurs() {
        List<UtilisateurResponse> liste = utilisateurRepository.findAll()
                .stream()
                .map(UtilisateurResponse::new) // Utilisation de ton super DTO
                .toList();

        return ResponseEntity.ok(liste);
    }
}