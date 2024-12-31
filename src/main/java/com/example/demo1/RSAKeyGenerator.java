package com.example.demo1;

import java.math.BigInteger;
import java.security.SecureRandom;

public class RSAKeyGenerator {
    private BigInteger n, e, d;
    private int bitLength = 2048;  // Recommended bit length for modern security
    private SecureRandom random = new SecureRandom();

    public RSAKeyGenerator() {
        generateKeys();
    }

    private void generateKeys() {
        // Generate two large prime numbers
        BigInteger p = BigInteger.probablePrime(bitLength / 2, random);
        BigInteger q = BigInteger.probablePrime(bitLength / 2, random);

        // Calculate the modulus n = p * q
        n = p.multiply(q);

        // Calculate the totient phi = (p - 1) * (q - 1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Fixed public exponent 65537
        e = BigInteger.valueOf(65537);

        // Calculate the private exponent d as the modular inverse of e modulo phi
        d = e.modInverse(phi);
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }

    public BigInteger getD() {
        return d;
    }
}
