package ci.lavage.gestionflotte.dto.request;

public record ChauffeurRequest(
        String nom,
        String prenoms,
        String telephone,
        String numeroPermis,
        String adresse
) {
}