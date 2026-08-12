package ci.lavage.gestionflotte.service;

import ci.lavage.gestionflotte.dto.request.VehiculeRequest;
import ci.lavage.gestionflotte.dto.response.VehiculeResponse;
import ci.lavage.gestionflotte.enums.EtatVehicule;
import ci.lavage.gestionflotte.exception.RegleMetierException;
import ci.lavage.gestionflotte.exception.RessourceIntrouvableException;
import ci.lavage.gestionflotte.model.Proprietaire;
import ci.lavage.gestionflotte.model.Vehicule;
import ci.lavage.gestionflotte.repository.ProprietaireRepository;
import ci.lavage.gestionflotte.repository.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final ProprietaireRepository proprietaireRepository;

    // Injection des deux repositories
    public VehiculeService(VehiculeRepository vehiculeRepository, ProprietaireRepository proprietaireRepository) {
        this.vehiculeRepository = vehiculeRepository;
        this.proprietaireRepository = proprietaireRepository;
    }

    // 1. Ajouter un véhicule
    @Transactional
    public VehiculeResponse ajouterVehicule(VehiculeRequest request) {
        // NOUVELLE SÉCURITÉ : Vérification stricte de l'ID du propriétaire avant d'interroger la base
        if (request.idProprietaire() == null || request.idProprietaire() <= 0) {
            throw new RegleMetierException("Veuillez sélectionner un propriétaire valide.");
        }

        if (vehiculeRepository.existsByImmatriculation(request.immatriculation())) {
            throw new RegleMetierException("Un véhicule avec cette immatriculation existe déjà.");
        }

        // Récupération du propriétaire depuis la base de données
        Proprietaire proprietaire = proprietaireRepository.findById(request.idProprietaire())
                .orElseThrow(() -> new RessourceIntrouvableException("Propriétaire introuvable avec l'ID : " + request.idProprietaire()));

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(request.immatriculation());
        vehicule.setMarque(request.marque());
        vehicule.setModele(request.modele());
        vehicule.setDateMiseEnCirculation(request.dateMiseEnCirculation());

        // Liaison du véhicule à son propriétaire
        vehicule.setProprietaire(proprietaire);

        // Par défaut, un nouveau véhicule est ACTIF
        vehicule.setEtat(EtatVehicule.ACTIF);

        Vehicule vehiculeSauvegarde = vehiculeRepository.save(vehicule);
        return new VehiculeResponse(vehiculeSauvegarde);
    }

    // 2. Mettre à jour l'état (ex: Tombe en panne, part au garage...)
    @Transactional
    public VehiculeResponse changerEtat(Long id, EtatVehicule nouvelEtat) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Véhicule introuvable."));

        if (vehicule.getEtat() == EtatVehicule.ARCHIVE) {
            throw new RegleMetierException("Impossible de modifier un véhicule archivé.");
        }

        vehicule.setEtat(nouvelEtat);
        return new VehiculeResponse(vehiculeRepository.save(vehicule));
    }

    // 3. Lister avec filtres
    @Transactional(readOnly = true)
    public List<VehiculeResponse> listerVehicules(EtatVehicule etat, String marque) {
        // CORRECTION : Si la marque est null, on la remplace par une chaîne vide ""
        // Cela empêche PostgreSQL de deviner le type et de planter avec 'bytea'
        String filtreMarque = (marque == null) ? "" : marque;

        return vehiculeRepository.findByFiltres(etat, filtreMarque)
                .stream()
                .map(VehiculeResponse::new)
                .toList();
    }

    // 4. Archiver un véhicule (Soft Delete pour garder l'historique)
    @Transactional
    public void archiverVehicule(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Véhicule introuvable."));

        // Règle métier : On pourrait aussi vérifier ici si le véhicule n'est pas "EN_COURS" d'affectation
        // avant de l'archiver !

        vehicule.setEtat(EtatVehicule.ARCHIVE);
        vehiculeRepository.save(vehicule);
    }
}