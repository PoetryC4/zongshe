package com.example.finance.utils;

public class NumberUtils {
    public static boolean isNumeric(String str) {
        if (str == null) return false;
        for (char c : str.toCharArray ()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
