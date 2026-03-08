package com.qritiooo.translationagency.model;

import java.util.Arrays;
import java.util.Locale;

public enum Language {
    CN("Chinese"),
    EN("English"),
    RU("Russian"),
    DE("German"),
    FR("French"),
    IT("Italian"),
    SP("Spanish"),
    PL("Polish");

    private final String displayName;

    Language(String displayName) {
        this.displayName = displayName;
    }

    public String getCode() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Language fromCode(String code) {
        String normalizedCode = normalizeCode(code);
        return Arrays.stream(values())
                .filter(language -> language.name().equalsIgnoreCase(normalizedCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown language code: " + code));
    }

    private static String normalizeCode(String code) {
        if (code == null) {
            return null;
        }
        return switch (code.trim().toUpperCase(Locale.ROOT)) {
            case "ENGLISH" -> "EN";
            case "RUSSIAN" -> "RU";
            case "GERMAN" -> "DE";
            case "FRENCH" -> "FR";
            case "ITALIAN" -> "IT";
            case "SPANISH" -> "SP";
            case "POLISH" -> "PL";
            case "CHINESE" -> "CN";
            default -> code.trim().toUpperCase(Locale.ROOT);
        };
    }
}
