package ci.lavage.gestionflotte.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErreurResponse(
        LocalDateTime timestamp,
        int status,
        String erreur,
        String message,
        Map<String, String> details
) {
    // Constructeur pour les erreurs simples sans détails
    public ErreurResponse(LocalDateTime timestamp, int status, String erreur, String message) {
        this(timestamp, status, erreur, message, null);
    }
}
