package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.response.VersementResponse;
import ci.lavage.gestionflotte.enums.StatutAffectation;
import ci.lavage.gestionflotte.enums.StatutVersement;
import ci.lavage.gestionflotte.exception.RegleMetierException;
import ci.lavage.gestionflotte.exception.RessourceIntrouvableException;
import ci.lavage.gestionflotte.model.Affectation;
import ci.lavage.gestionflotte.model.Versement;
import ci.lavage.gestionflotte.repository.AffectationRepository;
import ci.lavage.gestionflotte.repository.VersementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class VersementService {

    private final VersementRepository versementRepository;
    private final AffectationRepository affectationRepository;

    public VersementService(VersementRepository versementRepository, AffectationRepository affectationRepository) {
        this.versementRepository = versementRepository;
        this.affectationRepository = affectationRepository;
    }

    @Transactional
    public VersementResponse enregistrerVersement(Long idAffectation, BigDecimal montantVerse) {

        // 1. Validations avec nos nouvelles Exceptions Personnalisées !
        if (montantVerse == null || montantVerse.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegleMetierException("Le montant versé doit être strictement supérieur à zéro.");
        }

        Affectation affectation = affectationRepository.findById(idAffectation)
                .orElseThrow(() -> new RessourceIntrouvableException("Affectation introuvable avec l'ID : " + idAffectation));

        if (affectation.getStatut() == StatutAffectation.CLOTUREE) {
            throw new RegleMetierException("Impossible d'ajouter un versement à une affectation clôturée.");
        }

        // 2. Calculs de base
        BigDecimal recetteAttendue = affectation.getRecetteAttendueJournaliere();
        BigDecimal ecart = montantVerse.subtract(recetteAttendue);

        Versement versement = new Versement();
        versement.setAffectation(affectation);
        versement.setDateVersement(LocalDate.now());
        versement.setMontantVerse(montantVerse);
        versement.setEcart(ecart);

        // 3. Gestion des Statuts et Régularisation
        int comparison = ecart.compareTo(BigDecimal.ZERO);

        if (comparison < 0) {
            versement.setStatut(StatutVersement.RELIQUAT);
        } else if (comparison == 0) {
            versement.setStatut(StatutVersement.SOLDE);
        } else {
            BigDecimal surplus = ecart;
            List<Versement> dettes = versementRepository
                    .findByAffectationIdAndStatutOrderByDateVersementAsc(idAffectation, StatutVersement.RELIQUAT);

            for (Versement ancienneDette : dettes) {
                if (surplus.compareTo(BigDecimal.ZERO) <= 0) break;

                BigDecimal montantDette = ancienneDette.getEcart().abs();

                if (surplus.compareTo(montantDette) >= 0) {
                    ancienneDette.setStatut(StatutVersement.SOLDE);
                    ancienneDette.setEcart(BigDecimal.ZERO);
                    surplus = surplus.subtract(montantDette);
                } else {
                    ancienneDette.setEcart(ancienneDette.getEcart().add(surplus));
                    surplus = BigDecimal.ZERO;
                }

                versementRepository.save(ancienneDette);
                versement.setReliquatRegle(ancienneDette);
            }

            if (surplus.compareTo(BigDecimal.ZERO) == 0) {
                versement.setStatut(StatutVersement.SOLDE);
                versement.setEcart(BigDecimal.ZERO);
            } else {
                versement.setStatut(StatutVersement.AVANCE);
                versement.setEcart(surplus);
            }
        }

        Versement versementSauvegarde = versementRepository.save(versement);
        return new VersementResponse(versementSauvegarde);
    }

    @Transactional(readOnly = true)
    public List<VersementResponse> obtenirHistoriqueFiltre(LocalDate date, String nomChauffeur, String marqueVehicule) {
        List<Versement> versements = versementRepository.findHistoriqueByFiltres(date, nomChauffeur, marqueVehicule);
        return versements.stream()
                .map(VersementResponse::new)
                .toList();
    }
}