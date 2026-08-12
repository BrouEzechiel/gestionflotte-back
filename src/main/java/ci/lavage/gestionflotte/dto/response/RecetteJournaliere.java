package ci.lavage.gestionflotte.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecetteJournaliere(
        LocalDate date,
        BigDecimal montant
) {}