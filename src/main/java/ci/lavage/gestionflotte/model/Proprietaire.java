package ci.lavage.gestionflotte.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "proprietaire")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proprietaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenoms;

    @Column(nullable = false, unique = true)
    private String telephone;

    // Tu pourras ajouter plus tard un lien OneToOne vers Utilisateur s'ils doivent se connecter
}