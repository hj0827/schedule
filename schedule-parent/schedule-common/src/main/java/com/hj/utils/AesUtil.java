package com.hj.utils;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

public class AesUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    //    固定
    private static final String KEY = "cf057768aa974ac3954ccf6ddf27d471"; // 32字节的密钥
    private static final String IV = "dec9be8708474347"; // 16字节的IV
//    随机生成
//    private static final String KEY = generateKey(); // 32字节的密钥
//    private static final String IV = generateIV(); // 16字节的IV

    public static void main(String[] args) {
        System.out.println("Generated KEY: " + KEY);
        System.out.println("Generated IV: " + IV);
    }

//    private static String generateKey() {
//        UUID uuid = UUID.randomUUID();
//        return uuid.toString().replace("-", "").substring(0, 32); // 取前32个字符作为32字节的密钥
//    }
//
//    private static String generateIV() {
//        UUID uuid = UUID.randomUUID();
//        return uuid.toString().replace("-", "").substring(0, 16); // 取前16个字符作为16字节的IV
//    }

    // 其他加密解密方法
    public static String encrypt(String value) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes("UTF-8"));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decrypt(String value) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes("UTF-8"));

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        byte[] decodedValue = Base64.getDecoder().decode(value);
        byte[] decrypted = cipher.doFinal(decodedValue);
        return new String(decrypted, "UTF-8");
    }
}
