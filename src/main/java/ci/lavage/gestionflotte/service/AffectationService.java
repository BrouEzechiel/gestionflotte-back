package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.request.AffectationRequest;
import ci.lavage.gestionflotte.dto.response.AffectationResponse;
import ci.lavage.gestionflotte.enums.StatutAffectation;
import ci.lavage.gestionflotte.exception.RegleMetierException;
import ci.lavage.gestionflotte.exception.RessourceIntrouvableException;
import ci.lavage.gestionflotte.model.Affectation;
import ci.lavage.gestionflotte.model.Chauffeur;
import ci.lavage.gestionflotte.model.Vehicule;
import ci.lavage.gestionflotte.repository.AffectationRepository;
import ci.lavage.gestionflotte.repository.ChauffeurRepository;
import ci.lavage.gestionflotte.repository.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AffectationService {

    private final AffectationRepository affectationRepository;
    private final ChauffeurRepository chauffeurRepository;
    private final VehiculeRepository vehiculeRepository;

    public AffectationService(AffectationRepository affectationRepository,
                              ChauffeurRepository chauffeurRepository,
                              VehiculeRepository vehiculeRepository) {
        this.affectationRepository = affectationRepository;
        this.chauffeurRepository = chauffeurRepository;
        this.vehiculeRepository = vehiculeRepository;
    }

    @Transactional
    public AffectationResponse creerAffectation(AffectationRequest request) {
        // 1. Validations des données d'entrée
        if (request.recetteAttendueJournaliere() == null || request.recetteAttendueJournaliere().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegleMetierException("La recette attendue doit être strictement supérieure à zéro.");
        }

        // 2. On vérifie que le chauffeur et le véhicule existent
        Chauffeur chauffeur = chauffeurRepository.findById(request.idChauffeur())
                .orElseThrow(() -> new RessourceIntrouvableException("Chauffeur introuvable avec l'ID : " + request.idChauffeur()));

        Vehicule vehicule = vehiculeRepository.findById(request.idVehicule())
                .orElseThrow(() -> new RessourceIntrouvableException("Véhicule introuvable avec l'ID : " + request.idVehicule()));

        // 3. On vérifie que le véhicule n'est pas déjà occupé
        boolean vehiculeDejaPris = affectationRepository.existsByVehiculeIdAndStatut(
                request.idVehicule(), StatutAffectation.EN_COURS
        );

        if (vehiculeDejaPris) {
            throw new RegleMetierException("Ce véhicule est déjà affecté à un chauffeur (statut EN_COURS).");
        }

        // 4. On crée la nouvelle affectation
        Affectation affectation = new Affectation();
        affectation.setChauffeur(chauffeur);
        affectation.setVehicule(vehicule);
        affectation.setRecetteAttendueJournaliere(request.recetteAttendueJournaliere());
        affectation.setDateDebut(request.dateDebut() != null ? request.dateDebut() : LocalDate.now());
        affectation.setStatut(StatutAffectation.EN_COURS);

        Affectation affectationSauvegardee = affectationRepository.save(affectation);

        return new AffectationResponse(affectationSauvegardee);
    }

    @Transactional
    public AffectationResponse cloturerAffectation(Long idAffectation) {
        Affectation affectation = affectationRepository.findById(idAffectation)
                .orElseThrow(() -> new RessourceIntrouvableException("Affectation introuvable."));

        if (affectation.getStatut() == StatutAffectation.CLOTUREE) {
            throw new RegleMetierException("Cette affectation est déjà clôturée.");
        }

        // On clôture et on marque la date de fin
        affectation.setStatut(StatutAffectation.CLOTUREE);
        affectation.setDateFin(LocalDate.now());

        Affectation affectationCloturee = affectationRepository.save(affectation);

        return new AffectationResponse(affectationCloturee);
    }

    /**
     * Lister toutes les affectations existantes.
     */
    @Transactional(readOnly = true)
    public List<AffectationResponse> listerAffectations() {
        return affectationRepository.findAllByOrderByIdDesc()
                .stream()
                .map(AffectationResponse::new)
                .toList();
    }
}