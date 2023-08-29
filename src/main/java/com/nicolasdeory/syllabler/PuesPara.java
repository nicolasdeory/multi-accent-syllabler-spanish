package com.nicolasdeory.syllabler;

import java.util.Arrays;
import java.util.List;

public class PuesPara implements Rule {

    private boolean isPues(List<CharSequence> syll) {
        return (syll.size() == 1 && (syll.get(0).toString().equalsIgnoreCase("pues")));
    }

    private boolean isPara(List<CharSequence> syll) {
        return (syll.size() == 2 && syll.get(0).toString().equalsIgnoreCase("pa")
            && syll.get(1).toString().equalsIgnoreCase("ra"));
    }

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = word.getSyllables();
        if (isPues(syllables))
            return Arrays.asList("po");
        if (isPara(syllables))
            return Arrays.asList("pa");

        return syllables;
    }
}
