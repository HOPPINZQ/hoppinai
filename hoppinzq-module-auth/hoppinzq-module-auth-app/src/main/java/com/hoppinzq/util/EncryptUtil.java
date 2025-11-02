package com.hoppinzq.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * @author:ZhangQi 加解密工具类
 **/
public class EncryptUtil {
    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";
    public static final String HmacMD5 = "HmacMD5";
    public static final String HmacSHA1 = "HmacSHA1";
    public static final String DES = "DES";
    public static final String AES = "AES";
    public static final String SM4 = "SM4";
    //返回值格式：可以选择十六进制 Hex
    public static final String RESCODE = "Base64";
    private static final String ALGORITHM_NAME_ECB_PADDING = "/ECB/PKCS5Padding";
    private static final String ALGORITHM_NAME_CBC_PADDING = "/CBC/PKCS5Padding";
    //编码格式
    public static String charset = "utf-8";
    //DES
    public static int keysizeDES = 0;
    //AES
    public static int keysizeAES = 128;
    //SM4
    public static int keysizeSM4 = 128;

    public static EncryptUtil me;

    private EncryptUtil() {

    }

    public static EncryptUtil getInstance() {
        if (me == null) {
            synchronized (EncryptUtil.class) {
                if (me == null) {
                    me = new EncryptUtil();
                }
            }
        }
        return me;
    }

//    static {
//        Security.addProvider(new BouncyCastleProvider());
//    }

    /**
     * 使用MessageDigest进行单向加密（无密码）
     *
     * @param res       被加密的文本
     * @param algorithm 加密算法名称
     * @return
     */
    private static String messageDigest(String res, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] resBytes = charset == null ? res.getBytes() : res.getBytes(charset);
            return base64ToString(md.digest(resBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 使用KeyGenerator进行单向/双向加密（可设密码）
     *
     * @param res       被加密的原文
     * @param algorithm 加密使用的算法名称
     * @param key       加密使用的秘钥
     * @return
     */
    private static String keyGeneratorMac(String res, String algorithm, String key) {
        try {
            SecretKey sk = null;
            if (key == null) {
                KeyGenerator kg = KeyGenerator.getInstance(algorithm);
                sk = kg.generateKey();
            } else {
                byte[] keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
                sk = new SecretKeySpec(keyBytes, algorithm);
            }
            Mac mac = Mac.getInstance(algorithm);
            mac.init(sk);
            byte[] result = mac.doFinal(res.getBytes());
            return base64ToString(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据操作系统初始化不同的SecureRandom
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static SecureRandom osGetSecureRandom(String key) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        SecureRandom secureRandom;
        byte[] keyBytes = charset == null ? key.getBytes() : key.getBytes(charset);
        if (os.contains("windows")) {
            secureRandom = new SecureRandom(keyBytes);
        } else {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(key.getBytes(charset));
        }
        return secureRandom;
    }

    /**
     * 使用KeyGenerator双向加密，DES/AES/SM4
     *
     * @param res       加密的原文
     * @param algorithm 加密使用的算法名称
     * @param key       加密的秘钥
     * @param keysize
     * @param isEncode
     * @return
     */
    private static String keyGeneratorES(String res, String algorithm, String key,
                                         String iv, int keysize, boolean isEncode) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(algorithm);
            if (keysize == 0) {
                kg.init(osGetSecureRandom(key));
            } else if (key == null) {
                kg.init(keysize);
            } else {
                kg.init(keysize, osGetSecureRandom(key));
            }
            SecretKey sk = kg.generateKey();
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(charset), algorithm);
            Cipher cipher = Cipher.getInstance(iv == null ? algorithm + ALGORITHM_NAME_ECB_PADDING : algorithm + ALGORITHM_NAME_CBC_PADDING);
            if (isEncode) {
                if (iv != null) {
                    cipher.init(Cipher.ENCRYPT_MODE, sks, new IvParameterSpec(iv.getBytes()));
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, sks);
                }
                byte[] resBytes = charset == null ? res.getBytes() : res.getBytes(charset);
                if ("Hex".equals(RESCODE)) {
                    return parseByte2HexStr(cipher.doFinal(resBytes));
                } else {
                    return base64ToString(cipher.doFinal(resBytes));
                }
            } else {
                if (iv != null) {
                    cipher.init(Cipher.DECRYPT_MODE, sks, new IvParameterSpec(iv.getBytes()));
                } else {
                    cipher.init(Cipher.DECRYPT_MODE, sks);
                }
                if ("Hex".equals(RESCODE)) {
                    return new String(cipher.doFinal(parseHexStr2Byte(res)));
                } else {
                    return new String(cipher.doFinal(stringToBase64(res)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }


    /**
     * md5加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @return
     */
    public static String MD5(String res) {
        return messageDigest(res, MD5);
    }

    /**
     * md5加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @param key 秘钥
     * @return
     */
    public static String MD5(String res, String key) {
        return keyGeneratorMac(res, HmacMD5, key);
    }

    /**
     * 使用SHA1加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @return
     */
    public static String SHA1(String res) {
        return messageDigest(res, SHA1);
    }

    /**
     * 使用SHA1加密算法进行加密（不可逆）
     *
     * @param res 需要加密的原文
     * @param key 秘钥
     * @return
     */
    public static String SHA1(String res, String key) {
        return keyGeneratorMac(res, HmacSHA1, key);
    }

    /**
     * 使用DES加密算法进行加密（可逆）
     *
     * @param res 需要加密的原文
     * @param key 秘钥
     * @param iv  偏移量
     * @return
     */
    public static String DESEncode(String res, String key, String iv) {
        return keyGeneratorES(res, DES, key, iv, keysizeDES, true);
    }

    /**
     * 使用DES加密算法进行加密（可逆）
     *
     * @param res 需要加密的原文
     * @param key 秘钥
     * @return
     */
    public static String DESEncode(String res, String key) {
        return keyGeneratorES(res, DES, key, null, keysizeDES, true);
    }

    /**
     * 对使用DES加密算法的密文进行解密（可逆）
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @param iv  偏移量
     * @return
     */
    public static String DESDecode(String res, String key, String iv) {
        return keyGeneratorES(res, DES, key, iv, keysizeDES, false);
    }

    /**
     * 对使用DES加密算法的密文进行解密（可逆）
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return
     */
    public static String DESDecode(String res, String key) {
        return keyGeneratorES(res, DES, key, null, keysizeDES, false);
    }


    /**
     * 使用AES加密算法经行加密（可逆）
     *
     * @param res 需要加密的密文
     * @param key 秘钥
     * @param iv  偏移量
     * @return
     */
    public static String AESEncode(String res, String key, String iv) {
        return keyGeneratorES(res, AES, key, iv, keysizeAES, true);
    }

    /**
     * 使用AES加密算法经行加密（可逆）
     *
     * @param res 需要加密的密文
     * @param key 秘钥
     * @return
     */
    public static String AESEncode(String res, String key) {
        return keyGeneratorES(res, AES, key, null, keysizeAES, true);
    }

    /**
     * 对使用AES加密算法的密文进行解密
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @param iv  偏移量
     * @return
     */
    public static String AESDecode(String res, String key, String iv) {
        return keyGeneratorES(res, AES, key, iv, keysizeAES, false);
    }

    /**
     * 对使用AES加密算法的密文进行解密
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return
     */
    public static String AESDecode(String res, String key) {
        return keyGeneratorES(res, AES, key, null, keysizeAES, false);
    }


    /**
     * 对使用SM4加密算法的密文进行加密（可逆）
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @param iv  偏移量
     * @return
     */
    public static String SM4Encode(String res, String key, String iv) {
        return keyGeneratorES(res, SM4, key, iv, keysizeSM4, true);
    }

    /**
     * 对使用SM4加密算法的密文进行加密（可逆）
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return
     */
    public static String SM4Encode(String res, String key) {
        return keyGeneratorES(res, SM4, key, null, keysizeSM4, true);
    }

    /**
     * 对使用SM4加密算法的密文进行解密
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @param iv  偏移量
     * @return
     */
    public static String SM4Decode(String res, String key, String iv) {
        return keyGeneratorES(res, SM4, key, iv, keysizeSM4, false);
    }

    /**
     * 对使用SM4加密算法的密文进行解密
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return
     */
    public static String SM4Decode(String res, String key) {
        return keyGeneratorES(res, SM4, key, null, keysizeSM4, false);
    }


    /**
     * 使用异或进行加密
     *
     * @param res 需要加密的密文
     * @param key 秘钥
     * @return
     */
    public static String XOREncode(String res, String key) {
        byte[] bs = res.getBytes();
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((bs[i]) ^ key.hashCode());
        }
        return parseByte2HexStr(bs);
    }

    /**
     * 使用异或进行解密
     *
     * @param res 需要解密的密文
     * @param key 秘钥
     * @return
     */
    public static String XORDecode(String res, String key) {
        byte[] bs = parseHexStr2Byte(res);
        for (int i = 0; i < bs.length; i++) {
            bs[i] = (byte) ((bs[i]) ^ key.hashCode());
        }
        return new String(bs);
    }

    /**
     * 字符串装换成 Base64
     */
    public static byte[] stringToBase64(String key) {
        return Base64.decodeBase64(key.getBytes());
    }

    /**
     * Base64装换成字符串
     */
    public static String base64ToString(byte[] key) {
        return new Base64().encodeToString(key);
    }

    public static void main(String args[]) throws Exception {
        String str = "HjZKAzjwCDMtStej7a4ybe8wD3GJxBgn4tBeKG/M1jmbTPH+iH8lCg==";
        String KEY = str.substring(0, 16); // 长度必须是 16
        String IV = str.substring(16, 32); // 长度必须是 16
        String content = str.substring(32, str.length());
        //String encrypted = SM4Encode(content, KEY);
        String decrypted = AESDecode(content, KEY, IV);
        //System.out.println("加密前：" + content);
        //System.out.println("加密后：" + encrypted);
        System.out.println("解密后：" + decrypted);

//        String KEY = "abcdef0123456789";
//        String content = "message";
//        String encrypted = SM4encode(content, KEY);
//        String decrypted = SM4decode(encrypted, KEY);
//        System.out.println("加密前：" + content);
//        System.out.println("加密后：" + encrypted);
//        System.out.println("解密后：" + decrypted);
    }

//    /**
//     * 获取私钥与公钥
//     * @return
//     */
//    public static Map<String,String> sm2GengenerateKeys() {
//        AsymmetricCipherKeyPair keyPair = SM2Util.generateKeyPair();
//
//        ECPrivateKeyParameters priKey = (ECPrivateKeyParameters)keyPair.getPrivate();
//        String priKeyStr = ByteUtils.toHexString(priKey.getD().toByteArray());
//
//        ECPublicKeyParameters pubKey = (ECPublicKeyParameters)keyPair.getPublic();
//        String pubKeyStr = ByteUtils.toHexString(pubKey.getQ().getEncoded(false));
//        HashMap<String,String> keys=new HashMap(2);
//        keys.put("priKeyStr",priKeyStr);
//        keys.put("pubKeyStr",pubKeyStr);
//        return keys;
//    }
//
//    /**
//     * 使用公钥加密
//     * @param publicKey 公钥
//     * @param text 原文
//     * @return
//     */
//    public static String sm2Encrypt(String publicKey, String text) {
//        try {
//            ECDomainParameters domainParams = SM2Util.getDomainParams();
//            ECPoint point = domainParams.getCurve().decodePoint(ByteUtils.fromHexString(publicKey));
//            ECPublicKeyParameters ecpKey = new ECPublicKeyParameters(point, domainParams);
//            byte[] encData = SM2Util.encrypt(ecpKey, text.getBytes(charset));
//            return ByteUtils.toHexString(encData);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 使用私钥解密
//     * @param privateKey 私钥
//     * @param cipherText SM2加密后的文本
//     * @return
//     */
//    public static String sm2Decrypt(String privateKey, String cipherText) {
//        try {
//            //16进制转为BigInteger
//            BigInteger biKey = new BigInteger(privateKey, 16);
//            ECPrivateKeyParameters ecpKey = new ECPrivateKeyParameters(biKey, SM2Util.getDomainParams());
//            byte[] decData = SM2Util.decrypt(ecpKey, ByteUtils.fromHexString(cipherText));
//            return new String(decData, charset);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 使用私钥签名
//     * @param privateKey 私钥
//     * @param text 原文
//     * @return
//     */
//    public static String sm2Sign(String privateKey, String text) {
//        try {
//            //16进制转为BigInteger
//            BigInteger biKey = new BigInteger(privateKey, 16);
//            ECPrivateKeyParameters ecpKey = new ECPrivateKeyParameters(biKey, SM2Util.getDomainParams());
//            byte[] signData = SM2Util.sign(ecpKey, null, text.getBytes(charset));
//            return ByteUtils.toHexString(signData);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 使用公钥校验签名
//     * @param publicKey 公钥
//     * @param text 原文
//     * @param sign 签名
//     * @return
//     */
//    public static boolean sm2Verify(String publicKey, String text, String sign) {
//        try {
//            ECDomainParameters domainParams = SM2Util.getDomainParams();
//            ECPoint point = domainParams.getCurve().decodePoint(ByteUtils.fromHexString(publicKey));
//            ECPublicKeyParameters ecpKey = new ECPublicKeyParameters(point, domainParams);
//            return SM2Util.verify(ecpKey, null, text.getBytes(charset), ByteUtils.fromHexString(sign));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * 生成哈希值
//     * @param data
//     * @return
//     */
//    public static byte[] hash(byte[] data) {
//        SM3Digest digest = new SM3Digest();
//        digest.update(data, 0, data.length);
//        byte[] hash = new byte[digest.getDigestSize()];
//        digest.doFinal(hash, 0);
//        return hash;
//    }
//
//    /**
//     * 生成HMAC值
//     * @param key
//     * @param data
//     * @return
//     */
//    public static byte[] hmac(byte[] key, byte[] data) {
//        KeyParameter keyParameter = new KeyParameter(key);
//        SM3Digest digest = new SM3Digest();
//        HMac mac = new HMac(digest);
//        mac.init(keyParameter);
//        mac.update(data, 0, data.length);
//        byte[] result = new byte[mac.getMacSize()];
//        mac.doFinal(result, 0);
//        return result;
//    }

    /**
     * 直接使用异或（第一调用加密，第二次调用解密）
     *
     * @param res 密文
     * @param key 秘钥
     * @return
     */
    public int XOR(int res, String key) {
        return res ^ key.hashCode();
    }

}
