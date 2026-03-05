package ci.lavage.gestionflotte.util;

import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;

public class JwtKeyGenerator {

    public static void main(String[] args) {
        // Génère une clé super sécurisée pour l'algorithme HS512
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);

        // La transforme en texte lisible (Base64) pour la copier facilement
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        System.out.println("==================================================");
        System.out.println("VOICI TA CLÉ SECRÈTE (Copie-la) :");
        System.out.println(encodedKey);
        System.out.println("==================================================");
    }
}