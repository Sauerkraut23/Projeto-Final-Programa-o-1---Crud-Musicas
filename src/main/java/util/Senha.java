package util;

import org.mindrot.jbcrypt.BCrypt;

public final class Senha {

    private static final int CUSTO = 12;

    private Senha() {}

    public static String gerarHash(String senhaPura) {
        if (senhaPura == null || senhaPura.isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        return BCrypt.hashpw(senhaPura, BCrypt.gensalt(CUSTO));
    }

    public static boolean conferir(String senhaPura, String hash) {
        if (senhaPura == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(senhaPura, hash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
