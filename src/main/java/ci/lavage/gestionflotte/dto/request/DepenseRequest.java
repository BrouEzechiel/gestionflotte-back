package ci.lavage.gestionflotte.dto.request;

import ci.lavage.gestionflotte.enums.TypeDepense;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DepenseRequest {
    private Long idVehicule;
    private BigDecimal montant;
    private String description;
    private TypeDepense typeDepense;
    private LocalDate dateDepense;
}