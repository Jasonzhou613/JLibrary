package com.ttsea.jlibrary.common.encryptor;

import android.util.Base64;

import com.ttsea.jlibrary.common.utils.Base64Utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

public class RSAUtils {

    /** 加密方式 */
    private static final String ENCRYPTION_NAME = "RSA";

    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @return
     * @throws Exception 加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKeyByStr(String publicKeyStr)
            throws Exception {

        byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_NAME);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);

        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)
            throws Exception {

        byte[] buffer = Base64Utils.decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_NAME);

        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密过程
     *
     * @param publicKey 公钥
     * @param bytes     明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] bytes)
            throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;

        // 必须使用RSA/ECB/PKCS1Padding而不是RSA，否则每次加密结果一样
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] output = cipher.doFinal(bytes);
        return output;
        // return Base64Utils.encode(output);

    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 私钥解密过程
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData)
            throws Exception {
        if (privateKey == null) {
            throw new Exception("privateKey should not be null");
        }
        Cipher cipher = null;

        // 必须使用RSA/ECB/PKCS1Padding而不是RSA，否则解密会乱码
        cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] output = cipher.doFinal(cipherData);

        return output;
    }

    /**
     * 生成公私钥对
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, String> generateKey()
            throws NoSuchAlgorithmException {
        Map<String, String> keyMap = new HashMap<String, String>();
        KeyPairGenerator keygen = KeyPairGenerator.getInstance(ENCRYPTION_NAME);
        SecureRandom random = new SecureRandom();
        // 初始化加密
        keygen.initialize(1024, random);
        // 取得密钥对
        KeyPair kp = keygen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) kp.getPrivate();
        String privateKeyString = Base64Utils.encode(privateKey.getEncoded());
        RSAPublicKey publicKey = (RSAPublicKey) kp.getPublic();
        String publicKeyString = Base64Utils.encode(publicKey.getEncoded());
        keyMap.put("publicKey", publicKeyString);
        keyMap.put("privateKey", privateKeyString);
        return keyMap;
    }
}