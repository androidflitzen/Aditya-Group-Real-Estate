package com.flitzen.adityarealestate_new.Classes;


import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EncodeBased64Binary {
    public static String encodeFileToBase64Binary(String fileName)
            throws IOException {
        String encodedString = "";
        try{
            File file = new File(fileName);
            byte[] bytes = loadFile(file);
            byte[] encoded = Base64.encodeBase64(bytes);
            encodedString = new String(encoded);

        }catch (Exception e){
            e.printStackTrace();
        }

        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        is.close();
        return bytes;
    }
}
