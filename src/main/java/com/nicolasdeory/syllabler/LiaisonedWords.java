package com.nicolasdeory.syllabler;

import java.util.ArrayList;
import java.util.List;

public class LiaisonedWords {

    private List<CharSequence> syllables1;
    private List<CharSequence> syllables2;

    private List<Integer> stressedIndices1 = new ArrayList<>();
    private List<Integer> stressedIndices2 = new ArrayList<>();

    private LiaisonedWords(List<CharSequence> syllables1, List<CharSequence> syllables2) {
        this.syllables1 = syllables1;
        this.syllables2 = syllables2;
    }

    public static LiaisonedWords of(List<CharSequence> syllables1, List<CharSequence> syllables2) {
        return new LiaisonedWords(syllables1, syllables2);
    }

    public List<CharSequence> getSyllables1() {
        return syllables1;
    }

    public List<CharSequence> getSyllables2() {
        return syllables2;
    }

    public void addStressedIndex1(int stressedIndex1) {
        this.stressedIndices1.add(stressedIndex1);
    }

    public void addStressedIndex2(int stressedIndex2) {
        this.stressedIndices2.add(stressedIndex2);
    }

    public void setStressedIndices(List<Integer> stressedIndices1, List<Integer> stressedIndices2) {
        this.stressedIndices1 = stressedIndices1;
        this.stressedIndices2 = stressedIndices2;
    }

    public List<CharSequence> getSyllables1AfterStressed() {
        return getSyllablesAfterStressed(syllables1, stressedIndices1);
    }

    public List<CharSequence> getSyllables2AfterStressed() {
        return getSyllablesAfterStressed(syllables2, stressedIndices2);
    }

    public List<CharSequence> getSyllablesAfterStressed(List<CharSequence> syllables, List<Integer> stressedIndices) {
        // if element contains stressedindex, make that word uppercase
        List<CharSequence> syllablesAfterStressed = new ArrayList<>();
        for (int i = 0; i < syllables.size(); i++) {
            CharSequence syllable = syllables.get(i);
            if (stressedIndices.contains(i)) {
                syllablesAfterStressed.add(syllable.toString().toUpperCase());
            } else {
                syllablesAfterStressed.add(syllable);
            }
        }
        return syllablesAfterStressed;
    }
}
