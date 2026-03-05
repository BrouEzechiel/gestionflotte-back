package ci.lavage.gestionflotte.model;

import ci.lavage.gestionflotte.enums.StatutAffectation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "affectation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Affectation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal recetteAttendueJournaliere;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAffectation statut;

    @ManyToOne
    @JoinColumn(name = "idChauffeur", nullable = false)
    private Chauffeur chauffeur;

    @ManyToOne
    @JoinColumn(name = "idVehicule", nullable = false)
    private Vehicule vehicule;

    // Pensez à générer les Getters et Setters
}
