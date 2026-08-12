package ci.lavage.gestionflotte.dto.response;

public record AuthenticationResponse(
        String accessToken,
        String refreshToken,
        String role,
        String nom,    // <-- La 4ème place
        String prenom  // <-- La 5ème place
) {}