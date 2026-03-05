package ci.lavage.gestionflotte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Intercepte les erreurs métier (ex: Montant négatif, Affectation clôturée)
    @ExceptionHandler(RegleMetierException.class)
    public ResponseEntity<ErreurResponse> handleRegleMetierException(RegleMetierException ex) {
        ErreurResponse response = new ErreurResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erreur Règle Métier",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. Intercepte les éléments introuvables (ex: ID Affectation inexistant)
    @ExceptionHandler(RessourceIntrouvableException.class)
    public ResponseEntity<ErreurResponse> handleRessourceIntrouvableException(RessourceIntrouvableException ex) {
        ErreurResponse response = new ErreurResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Ressource Introuvable",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 3. Gère les erreurs de validation des requêtes (ex: @Valid échoue)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        ErreurResponse response = new ErreurResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Erreur de Validation",
                "Les données de la requête sont invalides.",
                errors // Ajoute les détails des erreurs de validation
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 4. Gère toutes les autres exceptions non capturées
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurResponse> handleGlobalException(Exception ex) {
        ErreurResponse response = new ErreurResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erreur Interne du Serveur",
                "Une erreur inattendue est survenue. Veuillez réessayer plus tard."
        );
        // Pour le débogage, vous pourriez vouloir logger l'exception complète ici
        // ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
