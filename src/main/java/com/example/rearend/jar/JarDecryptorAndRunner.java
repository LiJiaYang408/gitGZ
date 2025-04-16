package com.example.rearend.jar;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class JarDecryptorAndRunner {
    private static final byte KEY = 0x55;

    public static void decryptJar(String inputJarPath, String outputJarPath) throws IOException {
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
            String encryptedJarPath = "target/demo-0.0.1-SNAPSHOT-encrypted.jar";
            String decryptedJarPath = "target/demo-0.0.1-SNAPSHOT-decrypted.jar";
            decryptJar(encryptedJarPath, decryptedJarPath);

            URLClassLoader classLoader = new URLClassLoader(new URL[]{new File(decryptedJarPath).toURI().toURL()});
            Class<?> mainClass = classLoader.loadClass("com.example.demo.DemoApplication");
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
