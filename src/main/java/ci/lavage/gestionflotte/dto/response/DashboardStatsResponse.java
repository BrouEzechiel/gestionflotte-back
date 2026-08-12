package ci.lavage.gestionflotte.dto.response;

import java.util.List;

public record DashboardStatsResponse(
        long chauffeursActifs,
        long vehiculesTotal,
        long vehiculesEnService,
        long vehiculesEnPanne,
        List<RecetteJournaliere> recettesSur7Jours
) {}