package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.AuthenticationRequest;
import ci.lavage.gestionflotte.dto.request.RegisterRequest;
import ci.lavage.gestionflotte.dto.request.TokenRefreshRequest;
import ci.lavage.gestionflotte.dto.response.AuthenticationResponse;
import ci.lavage.gestionflotte.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> inscrire(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.inscrire(request));
    }

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authentifier(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authentifier(request));
    }

    // POST /api/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> rafraichirLeToken(
            @RequestBody TokenRefreshRequest request
    ) {
        return ResponseEntity.ok(authService.rafraichirToken(request));
    }
}