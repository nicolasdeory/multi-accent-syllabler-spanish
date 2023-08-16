package com.nicolasdeory.syllabler.andalucia.rules;

import java.util.ArrayList;
import java.util.List;

public class DropLetterS implements Rule {

    @Override
    public ArrayList<String> apply(ArrayList<String> syllables) {
        String lastSyllable = getLastSyllable(syllables);
        if (lastSyllable.endsWith("s")) {
            // Replace with h
            lastSyllable = lastSyllable.substring(0, lastSyllable.length() - 1) + "h";
        }
        List<String> newList = new ArrayList<>(syllables);
        newList.set(newList.size() - 1, lastSyllable);
        return new ArrayList<>(newList);
    }
}
