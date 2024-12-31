package com.example.demo1;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;

public class RSAEncryptDecrypt {
    private BigInteger n, e, d;

    public RSAEncryptDecrypt(BigInteger n, BigInteger e, BigInteger d) {
        this.n = n;
        this.e = e;
        this.d = d;
    }

    // Encrypts a BigInteger message
    public BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }

    // Decrypts a BigInteger message and returns the result as a byte array
    public byte[] decrypt(BigInteger encrypted) {
        return encrypted.modPow(d, n).toByteArray();
    }

    // Encrypts a string and returns the encrypted result as a string
    public String encryptText(String input) {
        BigInteger message = new BigInteger(input.getBytes());
        BigInteger encrypted = message.modPow(e, n);
        return encrypted.toString();
    }

    // Decrypts a string and returns the decrypted result as a string
    public String decryptText(String input) {
        BigInteger encrypted = new BigInteger(input);
        BigInteger decrypted = encrypted.modPow(d, n);
        return new String(decrypted.toByteArray());
    }

    // Encrypts a file (e.g., an image) and saves the encrypted data to the output file
    public void encryptFile(File inputFile, File outputFile) throws IOException {
        // Read the file data as bytes (for image or other types of files)
        byte[] data = Files.readAllBytes(inputFile.toPath());
        BigInteger message = new BigInteger(data);

        // Encrypt the data
        BigInteger encrypted = message.modPow(e, n);

        // Write the encrypted data to the output file
        Files.write(outputFile.toPath(), encrypted.toByteArray());
    }

    // Decrypts a file (e.g., an image) and saves the decrypted data to the output file
    public void decryptFile(File inputFile, File outputFile) throws IOException {
        // Read the encrypted file data
        byte[] data = Files.readAllBytes(inputFile.toPath());
        BigInteger encrypted = new BigInteger(data);

        // Decrypt the data
        BigInteger decrypted = encrypted.modPow(d, n);

        // Write the decrypted data back to the output file (preserve original image file format)
        Files.write(outputFile.toPath(), decrypted.toByteArray());
    }

    // Encrypt an image and return the encrypted data as a byte array
    public byte[] encryptImage(byte[] imageData) {
        BigInteger message = new BigInteger(imageData);
        BigInteger encrypted = message.modPow(e, n);
        return encrypted.toByteArray();
    }

    // Decrypt an image and return the decrypted image data as a byte array
    public byte[] decryptImage(byte[] encryptedImageData) {
        BigInteger encrypted = new BigInteger(encryptedImageData);
        byte[] decrypted = encrypted.modPow(d, n).toByteArray();
        return decrypted;
    }
}
