package ci.lavage.gestionflotte.dto.request;

import java.math.BigDecimal;

public record VersementRequest(
        Long idAffectation,
        BigDecimal montantVerse
) {
}
