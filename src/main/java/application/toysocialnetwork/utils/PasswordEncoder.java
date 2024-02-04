package application.toysocialnetwork.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordEncoder {
    private PasswordEncoder() {}

    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for(byte b: bytes) {
            hexString.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }

        return hexString.toString();
    }

    public static byte[] getSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public static byte[] getSaltFromHexString(String hexSalt) {
        byte[] salt = new byte[hexSalt.length() / 2];

        for(int i=0;i<salt.length; i++) {
            int index = i*2;
            int val = Integer.parseInt(hexSalt.substring(index, index+2), 16);
            salt[i] = (byte) val;
        }

        return salt;
    }
    public static String hashPasswordWithSalt(String password, byte[] salt) {
        String hashedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            byte[] hashedBytes = md.digest(password.getBytes());
            hashedPassword = toHexString(hashedBytes);
        }
        catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }

        return hashedPassword;
    }

    public static String hashPassword(String password) {
        String hashedPassword = null;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            hashedPassword = toHexString(hashedBytes);
        }
        catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }

        return hashedPassword;
    }

    public static void main(String[] args) {
        byte[] salt = getSalt();
        String encoded = hashPassword("vladchiriac03");
        System.out.println(encoded);
        System.out.println(encoded.length());
       // System.out.println(hashPasswordWithSalt("abc", salt));
    }
}
