package ci.lavage.gestionflotte.model;

import ci.lavage.gestionflotte.enums.EtatVehicule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicule")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String immatriculation;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;

    private LocalDate dateMiseEnCirculation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatVehicule etat;

    private LocalDateTime dateAjout;

    @PrePersist
    protected void onCreate() {
        this.dateAjout = LocalDateTime.now();
    }

    // Pensez à générer les Getters et Setters ici

}
