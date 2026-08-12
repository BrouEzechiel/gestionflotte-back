package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.request.ProprietaireRequest;
import ci.lavage.gestionflotte.dto.response.EtatProprietaireResponse;
import ci.lavage.gestionflotte.dto.response.ProprietaireResponse;
import ci.lavage.gestionflotte.exception.RegleMetierException;
import ci.lavage.gestionflotte.model.Proprietaire;
import ci.lavage.gestionflotte.repository.ProprietaireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProprietaireService {

    private final ProprietaireRepository proprietaireRepository;

    public ProprietaireService(ProprietaireRepository proprietaireRepository) {
        this.proprietaireRepository = proprietaireRepository;
    }

    // 1. Enregistrer un nouveau propriétaire
    @Transactional
    public ProprietaireResponse enregistrerProprietaire(ProprietaireRequest request) {
        // Vérification des doublons sur le numéro de téléphone
        if (proprietaireRepository.existsByTelephone(request.telephone())) {
            throw new RegleMetierException("Un propriétaire avec ce numéro de téléphone existe déjà.");
        }

        Proprietaire proprietaire = new Proprietaire();
        proprietaire.setNom(request.nom());
        proprietaire.setPrenoms(request.prenoms());
        proprietaire.setTelephone(request.telephone());

        Proprietaire proprietaireSauvegarde = proprietaireRepository.save(proprietaire);
        return new ProprietaireResponse(proprietaireSauvegarde);
    }

    // 2. Lister les propriétaires (Informations de base)
    @Transactional(readOnly = true)
    public List<ProprietaireResponse> listerProprietaires() {
        return proprietaireRepository.findAll()
                .stream()
                .map(ProprietaireResponse::new)
                .toList();
    }

    // 3. Consulter les états financiers (Gains par propriétaire)
    @Transactional(readOnly = true)
    public List<EtatProprietaireResponse> calculerEtatsFinanciers() {
        // Cette méthode appelle la super requête JPQL qu'on a créée dans le Repository
        return proprietaireRepository.calculerGainsTousProprietaires();
    }
}