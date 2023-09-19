package com.nicolasdeory.syllabler;

import java.util.Arrays;
import java.util.List;

public class Muy implements Rule {

    private boolean isMuy(List<CharSequence> syll) {
        return (syll.size() == 1 && (syll.get(0).toString().equalsIgnoreCase("muy")));
    }

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = word.getSyllables();
        if (isMuy(syllables))
            return Arrays.asList("mu");

        return syllables;
    }
}
