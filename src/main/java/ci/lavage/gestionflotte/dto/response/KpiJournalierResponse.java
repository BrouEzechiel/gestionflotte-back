package ci.lavage.gestionflotte.dto.response;

import java.math.BigDecimal;

public record KpiJournalierResponse(
        BigDecimal recetteAttendueGlobale, // Ce qu'on devrait avoir si tous les chauffeurs payent
        BigDecimal totalEncaisse,          // Ce qu'on a vraiment reçu dans la caisse
        BigDecimal totalImpayes            // L'argent qui manque (Dettes du jour)
) {
}