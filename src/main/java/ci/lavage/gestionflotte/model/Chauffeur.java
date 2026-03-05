package ci.lavage.gestionflotte.model;

import ci.lavage.gestionflotte.enums.StatutChauffeur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chauffeur")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chauffeur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenoms;

    private String telephone;

    @Column(unique = true, nullable = false)
    private String numeroPermis;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Enumerated(EnumType.STRING)
    private StatutChauffeur statut;

    private LocalDateTime dateEnregistrement;

    @PrePersist
    protected void onCreate() {
        this.dateEnregistrement = LocalDateTime.now();
    }

    // N'oubliez pas de générer les Getters et Setters
}
