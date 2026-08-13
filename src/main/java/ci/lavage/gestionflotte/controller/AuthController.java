package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.AuthenticationRequest;
import ci.lavage.gestionflotte.dto.request.RegisterRequest;
import ci.lavage.gestionflotte.dto.request.TokenRefreshRequest;
import ci.lavage.gestionflotte.dto.response.AuthenticationResponse;
import ci.lavage.gestionflotte.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Méthode utilitaire pour générer le cookie HttpOnly corrigée pour la production (Vercel/Render)
    private ResponseCookie createCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // OBLIGATOIRE en production (HTTPS sur Render)
                .path("/")
                .maxAge(maxAge)
                .sameSite("None") // OBLIGATOIRE pour les requêtes inter-sites (Vercel vers Render)
                .build();
    }

    // Ajoute les cookies à la réponse
    private ResponseEntity<AuthenticationResponse> buildResponseWithCookies(AuthenticationResponse authData) {
        // Access Token : expire dans 15 minutes (900 secondes)
        ResponseCookie jwtCookie = createCookie("accessToken", authData.accessToken(), 900);
        // Refresh Token : expire dans 7 jours (604800 secondes)
        ResponseCookie refreshCookie = createCookie("refreshToken", authData.refreshToken(), 604800);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authData);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> inscrire(@RequestBody RegisterRequest request) {
        AuthenticationResponse response = authService.inscrire(request);
        return buildResponseWithCookies(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authentifier(@RequestBody AuthenticationRequest request) {
        AuthenticationResponse response = authService.authentifier(request);
        return buildResponseWithCookies(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> rafraichirLeToken(@RequestBody TokenRefreshRequest request) {
        AuthenticationResponse response = authService.rafraichirToken(request);
        return buildResponseWithCookies(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteJwt = createCookie("accessToken", "", 0);
        ResponseCookie deleteRefresh = createCookie("refreshToken", "", 0);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteJwt.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .build();
    }
}