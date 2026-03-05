package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.request.ChauffeurRequest;
import ci.lavage.gestionflotte.dto.response.AffectationResponse;
import ci.lavage.gestionflotte.dto.response.ChauffeurResponse;
import ci.lavage.gestionflotte.dto.response.FicheChauffeurResponse;
import ci.lavage.gestionflotte.enums.StatutChauffeur;
import ci.lavage.gestionflotte.exception.RegleMetierException;
import ci.lavage.gestionflotte.exception.RessourceIntrouvableException;
import ci.lavage.gestionflotte.model.Affectation;
import ci.lavage.gestionflotte.model.Chauffeur;
import ci.lavage.gestionflotte.repository.AffectationRepository;
import ci.lavage.gestionflotte.repository.ChauffeurRepository;
import ci.lavage.gestionflotte.repository.VersementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ChauffeurService {

    private final ChauffeurRepository chauffeurRepository;
    private final AffectationRepository affectationRepository;
    private final VersementRepository versementRepository;

    public ChauffeurService(ChauffeurRepository chauffeurRepository,
                            AffectationRepository affectationRepository,
                            VersementRepository versementRepository) {
        this.chauffeurRepository = chauffeurRepository;
        this.affectationRepository = affectationRepository;
        this.versementRepository = versementRepository;
    }

    // 1. Enregistrer un nouveau chauffeur
    @Transactional
    public ChauffeurResponse enregistrerChauffeur(ChauffeurRequest request) {
        if (chauffeurRepository.existsByTelephone(request.telephone())) {
            throw new RegleMetierException("Un chauffeur avec ce numéro de téléphone existe déjà.");
        }
        if (request.numeroPermis() != null && chauffeurRepository.existsByNumeroPermis(request.numeroPermis())) {
            throw new RegleMetierException("Ce numéro de permis est déjà enregistré.");
        }

        Chauffeur chauffeur = new Chauffeur();
        chauffeur.setNom(request.nom());
        chauffeur.setPrenoms(request.prenoms());
        chauffeur.setTelephone(request.telephone());
        chauffeur.setNumeroPermis(request.numeroPermis());
        chauffeur.setAdresse(request.adresse());
        chauffeur.setStatut(StatutChauffeur.ACTIF); // Actif par défaut

        return new ChauffeurResponse(chauffeurRepository.save(chauffeur));
    }

    // 2. Mettre à jour les infos d'un chauffeur
    @Transactional
    public ChauffeurResponse modifierChauffeur(Long id, ChauffeurRequest request) {
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Chauffeur introuvable."));

        // On vérifie les doublons uniquement s'il change son numéro
        if (!chauffeur.getTelephone().equals(request.telephone()) && chauffeurRepository.existsByTelephone(request.telephone())) {
            throw new RegleMetierException("Ce numéro de téléphone est déjà pris.");
        }

        chauffeur.setNom(request.nom());
        chauffeur.setPrenoms(request.prenoms());
        chauffeur.setTelephone(request.telephone());
        chauffeur.setNumeroPermis(request.numeroPermis());
        chauffeur.setAdresse(request.adresse());

        return new ChauffeurResponse(chauffeurRepository.save(chauffeur));
    }

    // 3. Modifier le statut (Ex: Suspendre ou Mettre en congé)
    @Transactional
    public ChauffeurResponse changerStatut(Long id, StatutChauffeur nouveauStatut) {
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Chauffeur introuvable."));

        chauffeur.setStatut(nouveauStatut);
        return new ChauffeurResponse(chauffeurRepository.save(chauffeur));
    }

    // 4. Consulter la Fiche Complète (Le point fort de ton application !)
    @Transactional(readOnly = true)
    public FicheChauffeurResponse consulterFicheChauffeur(Long id) {
        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Chauffeur introuvable."));

        // A. Calcul du solde
        BigDecimal solde = versementRepository.calculerSoldeFinancierChauffeur(id);
        if (solde == null) {
            solde = BigDecimal.ZERO; // S'il n'a fait aucun versement, son solde est 0
        }

        // B. Historique des véhicules
        List<Affectation> historiqueBrut = affectationRepository.findByChauffeurIdOrderByDateDebutDesc(id);

        // On transforme les entités en DTO
        List<AffectationResponse> historiqueDTO = historiqueBrut.stream()
                .map(AffectationResponse::new)
                .toList();

        // C. On assemble le tout
        return new FicheChauffeurResponse(
                chauffeur.getId(),
                chauffeur.getNom() + " " + chauffeur.getPrenoms(),
                chauffeur.getTelephone(),
                chauffeur.getNumeroPermis(),
                chauffeur.getStatut(),
                solde,
                historiqueDTO
        );
    }

    // 5. Lister tous les chauffeurs
    @Transactional(readOnly = true)
    public List<ChauffeurResponse> listerChauffeurs() {
        return chauffeurRepository.findAll().stream()
                .map(ChauffeurResponse::new)
                .toList();
    }
}