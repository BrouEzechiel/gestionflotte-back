package ci.lavage.gestionflotte.controller;

import ci.lavage.gestionflotte.dto.request.DepenseRequest;
import ci.lavage.gestionflotte.dto.response.DepenseResponse;
import ci.lavage.gestionflotte.service.DepenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depenses")
@RequiredArgsConstructor
public class DepenseController {

    private final DepenseService depenseService;

    @PostMapping
    public ResponseEntity<DepenseResponse> ajouterDepense(@RequestBody DepenseRequest request) {
        DepenseResponse response = depenseService.ajouterDepense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DepenseResponse>> listerDepenses() {
        return ResponseEntity.ok(depenseService.listerDepenses());
    }
}