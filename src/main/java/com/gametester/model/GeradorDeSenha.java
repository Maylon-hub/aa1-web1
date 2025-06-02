import org.mindrot.jbcrypt.BCrypt;

public class GeradorDeSenha {
    public static void main(String[] args) {
        String senhaEmTextoPlano = "admin";
        // Gera o salt e o hash
        String senhaComHash = BCrypt.hashpw(senhaEmTextoPlano, BCrypt.gensalt());

        System.out.println("Senha em texto plano: " + senhaEmTextoPlano);
        System.out.println("Senha com Hash BCrypt: " + senhaComHash);
    }
}