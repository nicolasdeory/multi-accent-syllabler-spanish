package com.nicolasdeory.syllabler;

import com.nicolasdeory.syllabler.SyllablerUtils.LiaisonResult;

public class LiaisonProcessor {

    Syllabler word1;
    Syllabler word2;

    public LiaisonProcessor(Syllabler word1, Syllabler word2) {
        this.word1 = word1;
        this.word2 = word2;
    }

    public static LiaisonProcessor process(Syllabler word1, Syllabler word2) {
        LiaisonProcessor liaisonProcessor = new LiaisonProcessor(word1, word2);
        liaisonProcessor.process();
        return liaisonProcessor;
    }

    /**
     * Modifies word1 and word2
     */
    public void process() {
        CharSequence w1_lastSyllable = word1.getLastSyllable();
        CharSequence w2_firstSyllable = word2.getFirstSyllable();

        // special case "a dónde" = "ande"
        boolean isADondeCase =
            w1_lastSyllable.toString().toLowerCase().endsWith("a") && (SyllablerUtils.normalizeWord(
                w2_firstSyllable.toString()).contains("don"));
        if (isADondeCase) {
            int oldW1Stressed = word1.getStressedPosition();
            word1 = word1.replaceLastSyllable(w1_lastSyllable.toString() + "n");
            word2 = word2.removeFirstSyllable();
            LiaisonedWords liaisonedWords = LiaisonedWords.of(word1.getSyllables(), word2.getSyllables());
            liaisonedWords.addStressedIndex1(oldW1Stressed);
            liaisonedWords.addStressedIndex1(word1.getSyllables().size() - 1);
            result = liaisonedWords;
            return;
        }

        boolean w2_startsWithVowel = word2.startsWithVocalic();
        if (!w2_startsWithVowel) {
            // Set stresses.
            LiaisonedWords liaisonedWords = LiaisonedWords.of(word1.getSyllables(), word2.getSyllables());
            liaisonedWords.addStressedIndex1(word1.getStressedPosition());
            liaisonedWords.addStressedIndex2(word2.getStressedPosition());
            result = liaisonedWords;
            return;
        }

        char vowel_w1 = w1_lastSyllable.charAt(w1_lastSyllable.length() - 1);
        Character vowel_w2 = w2_startsWithVowel ? w2_firstSyllable.charAt(0) : null;
        if (vowel_w2 != null && vowel_w2 == 'h') {
            vowel_w2 = w2_firstSyllable.charAt(1);
        }

        boolean areSameVowel = vowel_w2 != null && SyllablerUtils.areVowelsEqual(vowel_w1, vowel_w2.charValue());
        int w1_charcount = word1.getWord().length();
        // o hombre, o oscilador
        if (areSameVowel && w1_charcount == 1) {
            // Set stresses.
            LiaisonedWords liaisonedWords = LiaisonedWords.of(word1.getSyllables(), word2.getSyllables());
            liaisonedWords.addStressedIndex1(word1.getStressedPosition());
            liaisonedWords.addStressedIndex2(word2.getStressedPosition());
            result = liaisonedWords;
            return;
        }

        char w1_lastChar = w1_lastSyllable.charAt(w1_lastSyllable.length() - 1);
        if (w1_lastChar != 'y' && w1_lastChar != 'h' && SyllablerUtils.isConsonant(w1_lastChar)) {
            // Set stresses.
            LiaisonedWords liaisonedWords = LiaisonedWords.of(word1.getSyllables(), word2.getSyllables());
            liaisonedWords.addStressedIndex1(word1.getStressedPosition());
            liaisonedWords.addStressedIndex2(word2.getStressedPosition());
            result = liaisonedWords;
            return;
        }
        // W1 ends with a vowel

        LiaisonResult liaisonResult = SyllablerUtils.vowelsDoLiaison(vowel_w1, vowel_w2,
            word1.isWordAccentedOnLastSyllable(), word2.isWordAccentedOnFirstSyllable(), word1.getSyllables().size(),
            word2.getSyllables().size());

        // save stress of w1
        int w1_oldStressedIndex = word1.getStressedPosition();
        // By this time, remove h if existing
        if (w2_firstSyllable.charAt(0) == 'h') {
            w2_firstSyllable = w2_firstSyllable.toString().
                replace("h", "");
        }

        if (w2_startsWithVowel && w1_lastChar != 'y') {
            CharSequence newW1LastSyllable;
            if (liaisonResult.equals(LiaisonResult.REMOVE_LAST_KEEP_FIRST_MERGE)) {
                // Remove w1 last vowel, keep w2 first vowel, merge
                newW1LastSyllable = w1_lastSyllable.toString().subSequence(0, w1_lastSyllable.toString().length() - 1)
                    + w2_firstSyllable.toString();
            } else if (liaisonResult.equals(LiaisonResult.KEEP_LAST_REMOVE_FIRST_MERGE)) {
                // Keep w1 last vowel, remove w2 first vowel, merge
                CharSequence newW2FirstSyllable = w2_firstSyllable.subSequence(1, w2_firstSyllable.length());
                newW1LastSyllable = w1_lastSyllable + newW2FirstSyllable.toString();
            } else if (liaisonResult.equals(LiaisonResult.KEEP_LAST_KEEP_FIRST_MERGE)) {
                // Keep w1 last vowel, keep w2 first vowel, merge
                newW1LastSyllable = w1_lastSyllable + w2_firstSyllable.toString();
                // remove accents
                newW1LastSyllable = SyllablerUtils.normalizeWord(newW1LastSyllable.toString());
            } else if (liaisonResult.equals(LiaisonResult.KEEP_LAST_KEEP_FIRST_NO_MERGE)) {
                // DONT DO ANYTHING
                newW1LastSyllable = w1_lastSyllable;
            } else {
                throw new RuntimeException("Unknown liaison result: " + liaisonResult);
            }
            newW1LastSyllable = newW1LastSyllable.toString().replace("qu", "k").replace("QU", "K");
            word1 = word1.replaceLastSyllable(newW1LastSyllable);
            w1_lastSyllable = newW1LastSyllable;

        } else {
            // Keep w1 last vowel, merge
            CharSequence newW1LastSyllable = w1_lastSyllable + w2_firstSyllable.toString();
            word1 = word1.replaceLastSyllable(newW1LastSyllable);
            w1_lastSyllable = newW1LastSyllable;
        }

        boolean w1_containsQu = SyllablerUtils.normalizeWord(w1_lastSyllable.toString()).contains("qu");
        if (w1_containsQu) {
            // Replace qu with k
            CharSequence newW1LastSyllable = w1_lastSyllable.toString().replace("qu", "k").replace("QU", "K");
            word1 = word1.replaceLastSyllable(newW1LastSyllable);
        }

        // Remove first syllable from w2?
        boolean word2HadFirstStress = word2.isWordAccentedOnFirstSyllable();
//        if (!(liaisonResult.equals(LiaisonResult.KEEP_LAST_REMOVE_FIRST) && word2HadFirstStress)) {
        if (!liaisonResult.equals(LiaisonResult.KEEP_LAST_KEEP_FIRST_NO_MERGE)) {
            word2 = word2.removeFirstSyllable();
        }

        // Set stresses.
        LiaisonedWords liaisonedWords = LiaisonedWords.of(word1.getSyllables(), word2.getSyllables());
        liaisonedWords.addStressedIndex1(w1_oldStressedIndex);
        if (!word2HadFirstStress) {
            // if the stress didn't move to the first word
            liaisonedWords.addStressedIndex2(word2.getStressedPosition());
        } else if (!word2.getSyllables().isEmpty()) {
            if (liaisonResult.equals(LiaisonResult.KEEP_LAST_KEEP_FIRST_NO_MERGE)) {
                // DE OZ
                liaisonedWords.addStressedIndex2(word2.getStressedPosition());
            } else {
                // dónDEN das
                // jeKÁS pero
                liaisonedWords.addStressedIndex1(word1.getSyllables().size() - 1);
            }
        }

        result = liaisonedWords;
    }

    public Syllabler getWord1() {
        return word1;
    }

    public Syllabler getWord2() {
        return word2;
    }

    private LiaisonedWords result;

    public LiaisonedWords getResult() {
        return result;
    }
}
