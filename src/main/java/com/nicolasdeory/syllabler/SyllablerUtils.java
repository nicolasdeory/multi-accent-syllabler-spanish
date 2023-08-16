package com.nicolasdeory.syllabler;

public final class SyllablerUtils {

    public static boolean isConsonant(char c) {
        switch (c) {
            // Open-vowel or close-vowel with written accent
            case 'a':
            case 'á':
            case 'A':
            case 'Á':
            case 'à':
            case 'À':
            case 'e':
            case 'é':
            case 'E':
            case 'É':
            case 'è':
            case 'È':
            case 'í':
            case 'Í':
            case 'ì':
            case 'Ì':
            case 'o':
            case 'ó':
            case 'O':
            case 'Ó':
            case 'ò':
            case 'Ò':
            case 'ú':
            case 'Ú':
            case 'ù':
            case 'Ù':
                // Close-vowel
            case 'i':
            case 'I':
            case 'u':
            case 'U':
            case 'ü':
            case 'Ü':
                return false;
        }
        return true;
    }

    public static VowelType getVowelType(char c) {
        switch (c) {
            case 'á', 'à', 'é', 'è', 'ó', 'ò':
                return VowelType.OPEN_WITH_ACCENT;
            case 'a', 'e', 'o':
                return VowelType.OPEN;
            case 'í', 'ì', 'ú', 'ù', 'ü':
                return VowelType.CLOSED_WITH_ACCENT;
            case 'i', 'I', 'u', 'U', 'y', 'Y':
                return VowelType.CLOSED;
        }
        return VowelType.NOT_A_VOWEL;
    }

    public static boolean vowelsDoLiaison(char c1, char c2) {
        // It is hiatus if OPEN-OPEN,
        VowelType t1 = getVowelType(c1);
        VowelType t2 = getVowelType(c2);
        if (t1 == VowelType.CLOSED_WITH_ACCENT || t1 == VowelType.OPEN_WITH_ACCENT || t2 == VowelType.OPEN_WITH_ACCENT
            || t2 == VowelType.CLOSED_WITH_ACCENT) {
            return true;
        }
        if (t1 == VowelType.OPEN && t2 == VowelType.OPEN) {
            return true;
        }
        if (c1 == 'e' && t2 == VowelType.CLOSED) {
            return true;
        }
        if (t1 == VowelType.CLOSED && t2 == VowelType.OPEN) {
            return true;
        }
        if (t1 == VowelType.OPEN && c2 == 'u') {
            return true;
        }

        return false;
    }

    public static boolean areVowelsEqual(char c1, char c2) {
        // normalize accents
        c1 = normalizeVowel(c1);
        c2 = normalizeVowel(c2);
        return c1 == c2;
    }

    public static char normalizeVowel(char c) {
        c = Character.toLowerCase(c);
        switch (c) {
            case 'á':
            case 'à':
            case 'ä':
                return 'a';
            case 'é':
            case 'è':
            case 'ë':
                return 'e';
            case 'í':
            case 'ì':
            case 'ï':
                return 'i';
            case 'ó':
            case 'ò':
            case 'ö':
                return 'o';
            case 'ú':
            case 'ù':
            case 'ü':
                return 'u';
        }
        return c;
    }

    public static String normalizeWord(String word) {
        StringBuilder sb = new StringBuilder();
        for (char c : word.toCharArray()) {
            sb.append(normalizeVowel(c));
        }
        return sb.toString();
    }
}
