package com.healthrx.backend.security.aes;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Getter
@Component
public final class AesHandler implements CommandLineRunner {

    @Value("${application.security.aes.secret-key}")
    private String secretKey;

    @Value("${application.security.aes.salt}")
    private String salt;

    private static SecretKey secret;
    private static IvParameterSpec ivParameterSpec;

    @Override
    public void run(String... args) throws Exception {
        secret = getKeyFromPassword(secretKey, salt);
        ivParameterSpec = fixedIv();
    }

    @SneakyThrows
    public static byte[] encrypt(byte[] image) {
        return encrypt("AES/CBC/PKCS5Padding", image, secret, ivParameterSpec);
    }

    @SneakyThrows
    public static byte[] decrypt(byte[] image) {
        return decrypt("AES/CBC/PKCS5Padding", image, secret, ivParameterSpec);
    }

    @SneakyThrows
    public static String encryptString(String input) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(input.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    @SneakyThrows
    public static String decryptString(String cipherText) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret, ivParameterSpec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)));
    }

    public static byte[] encrypt(String algorithm, byte[] input, SecretKey key,
                                 IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] cipherText = cipher.doFinal(input);
        return Base64.getEncoder().encode(cipherText);
    }

    public static byte[] decrypt(String algorithm, byte[] cipherText, SecretKey key,
            IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(Base64.getDecoder().decode(cipherText));
    }

    private SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);

        return new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
    }

    private IvParameterSpec fixedIv() {
        byte[] iv = new byte[16];
        return new IvParameterSpec(iv);
    }
}
