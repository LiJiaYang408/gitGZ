package com.example.rearend.jar;

import java.io.*;

public class JarEncryptor {
    private static final byte KEY = 0x55;

    public static void encryptJar(String inputJarPath, String outputJarPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(inputJarPath);
             FileOutputStream fos = new FileOutputStream(outputJarPath)) {
            int data;
            while ((data = fis.read()) != -1) {
                fos.write(data ^ KEY);
            }
        }
    }

    public static void main(String[] args) {
        try {
            encryptJar("target/demo-0.0.1-SNAPSHOT-obfuscated.jar", "target/demo-0.0.1-SNAPSHOT-encrypted.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}