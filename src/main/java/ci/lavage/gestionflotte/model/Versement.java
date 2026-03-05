package ci.lavage.gestionflotte.model;

import ci.lavage.gestionflotte.enums.StatutVersement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Versement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateVersement;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantVerse;

    @Column(precision = 10, scale = 2)
    private BigDecimal ecart;

    @Enumerated(EnumType.STRING)
    private StatutVersement statut;

    @ManyToOne
    @JoinColumn(name = "idAffectation", nullable = false)
    private Affectation affectation;

    /**
     * Auto-référence pour lier ce versement à un ancien versement (dette) qu'il règle.
     */
    @ManyToOne
    @JoinColumn(name = "idReliquatRegle")
    private Versement reliquatRegle;
}
