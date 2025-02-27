package org.istrfa.utils;


import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.UUID;

@Slf4j
public class EncryptDecryptUtil {

    private EncryptDecryptUtil() {
    }


    public static String decrypt(String encryptedData, UUID secretKey) {
        try {
            byte[] encodedKey = secretKey.toString().getBytes();
            byte[] decodedData = Base64.getDecoder().decode(encryptedData.getBytes());
            byte[] decryptedData = new byte[decodedData.length];
            for (int i = 0; i < decodedData.length; i++) {
                decryptedData[i] = (byte) (decodedData[i] ^ encodedKey[i % encodedKey.length]);
            }
            return new String(decryptedData);
        } catch (Exception e) {
            log.error("ERROR_DECRYPT->" + e);

            return null;
        }
    }

}
