package com.example.encs5150_project.controller;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordController {//Implemented by hashing because it is one way not as ENC/DEC->no key
    private static final int ITERATIONS = 310000;//Hash many times to make it harder for Trudy
    private static final int SALT_LENGTH_IN_BYTES = 16;
    private static final int KEY_LENGTH_IN_BITS = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    public String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory= SecretKeyFactory.getInstance(ALGORITHM);
        byte[] salt=new byte[SALT_LENGTH_IN_BYTES];//Initialized to zeroes
        SecureRandom secureRandom=new SecureRandom();
        secureRandom.nextBytes(salt);
        PBEKeySpec keySpec=new PBEKeySpec(password.toCharArray(),salt,ITERATIONS,KEY_LENGTH_IN_BITS);
        byte[] hash=secretKeyFactory.generateSecret(keySpec).getEncoded();//to get bytes from hashing
        String encodedSalt= Base64.getEncoder().encodeToString(salt);//convert ro string to store in database
        String encodedHash = Base64.getEncoder().encodeToString(hash);
        return ITERATIONS + ":" + encodedSalt + ":" + encodedHash;
    }
    public boolean verifyPassword(String password,String hashPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory secretKeyFactory= SecretKeyFactory.getInstance(ALGORITHM);
        String[] hashParts=hashPassword.split(":");
        int iterations=Integer.parseInt(hashParts[0]);
        byte[]decodedSalt=Base64.getDecoder().decode(hashParts[1]);//get bytes from String
        byte[]decodedHash=Base64.getDecoder().decode(hashParts[2]);//get bytes from String
        PBEKeySpec keySpec=new PBEKeySpec(password.toCharArray(),decodedSalt,iterations,KEY_LENGTH_IN_BITS);
        byte[] inputPasswordHash=secretKeyFactory.generateSecret(keySpec).getEncoded();
        return MessageDigest.isEqual(inputPasswordHash,decodedHash);
    }
}
