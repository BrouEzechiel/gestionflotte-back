package ci.lavage.gestionflotte.dto.response;

import ci.lavage.gestionflotte.enums.TypeDepense;
import ci.lavage.gestionflotte.model.Depense;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DepenseResponse {
    private Long id;
    private LocalDate dateDepense;
    private BigDecimal montant;
    private String description;
    private TypeDepense typeDepense;
    private Long idVehicule;
    private String immatriculationVehicule;

    // Constructeur qui transforme le Modèle en DTO
    public DepenseResponse(Depense depense) {
        this.id = depense.getId();
        this.dateDepense = depense.getDateDepense();
        this.montant = depense.getMontant();
        this.description = depense.getDescription();
        this.typeDepense = depense.getTypeDepense();
        if (depense.getVehicule() != null) {
            this.idVehicule = depense.getVehicule().getId();
            this.immatriculationVehicule = depense.getVehicule().getImmatriculation();
        }
    }
}