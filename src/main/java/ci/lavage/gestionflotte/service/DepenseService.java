package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.request.DepenseRequest;
import ci.lavage.gestionflotte.dto.response.DepenseResponse;
import ci.lavage.gestionflotte.exception.RessourceIntrouvableException;
import ci.lavage.gestionflotte.model.Depense;
import ci.lavage.gestionflotte.model.Vehicule;
import ci.lavage.gestionflotte.repository.DepenseRepository;
import ci.lavage.gestionflotte.repository.VehiculeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepenseService {

    private final DepenseRepository depenseRepository;
    private final VehiculeRepository vehiculeRepository;

    @Transactional
    public DepenseResponse ajouterDepense(DepenseRequest request) {
        Vehicule vehicule = vehiculeRepository.findById(request.getIdVehicule())
                .orElseThrow(() -> new RessourceIntrouvableException("Véhicule introuvable"));

        Depense depense = Depense.builder()
                .vehicule(vehicule)
                .montant(request.getMontant())
                .description(request.getDescription())
                .typeDepense(request.getTypeDepense())
                .dateDepense(request.getDateDepense() != null ? request.getDateDepense() : LocalDate.now())
                .build();

        depense = depenseRepository.save(depense);
        return new DepenseResponse(depense);
    }

    @Transactional(readOnly = true)
    public List<DepenseResponse> listerDepenses() {
        return depenseRepository.findAllByOrderByDateDepenseDesc().stream()
                .map(DepenseResponse::new)
                .toList();
    }
}