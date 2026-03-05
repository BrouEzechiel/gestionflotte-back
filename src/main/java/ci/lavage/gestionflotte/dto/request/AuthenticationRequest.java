package ci.lavage.gestionflotte.dto.request;

public record AuthenticationRequest(
        String identifiant,
        String motDePasse
) {
}