package com.hachther.mesomb.util;

/**
 * Helper class to generate random string for nonce
 */
public class RandomGenerator {
    public static String nonce() {
        return nonce(40);
    }

    /**
     * Generate a random string by the length
     *
     * @param length size of the nonce to generate
     * @return String
     */
    public static String nonce(int length) {
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder s = new StringBuilder(length);
        int i;
        for ( i=0; i<length; i++) {
            int ch = (int)(characters.length() * Math.random());
            s.append(characters.charAt(ch));
        }
        return s.toString();
    }
}
