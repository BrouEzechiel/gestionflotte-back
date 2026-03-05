package ci.lavage.gestionflotte.dto.response;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        String role
) {
}