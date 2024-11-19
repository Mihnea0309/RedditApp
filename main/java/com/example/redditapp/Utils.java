package com.example.redditapp;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Utils {
    public static String decodeURL(String url) {
        String correctedUrl = url.replace("&amp%3B", "&");

        // Afișăm URL-ul corectat
        System.out.println(correctedUrl);

        // Dacă ai nevoie să decodezi complet URL-ul
        try {
            String decodedUrl = URLDecoder.decode(correctedUrl, "UTF-8");
            System.out.println(decodedUrl);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return correctedUrl;
    }
}
