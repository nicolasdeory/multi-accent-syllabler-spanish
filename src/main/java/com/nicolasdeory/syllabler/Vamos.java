package com.nicolasdeory.syllabler;

import java.util.Arrays;
import java.util.List;

public class Vamos implements Rule {

    private boolean isVamos(List<CharSequence> syll) {
        return(syll.size() == 2 && syll.get(0).toString().equalsIgnoreCase("va")
            && syll.get(1).toString().equalsIgnoreCase("mos"));
    }

    private boolean isVamonos(List<CharSequence> syll) {
        return (syll.size() == 3 && SyllablerUtils.normalizeWord(syll.get(0).toString()).equalsIgnoreCase("va")
            && SyllablerUtils.normalizeWord(syll.get(1).toString()).equalsIgnoreCase("mo")
            && SyllablerUtils.normalizeWord(syll.get(2).toString()).equalsIgnoreCase("nos"));
    }

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = word.getSyllables();
        if (isVamonos(syllables))
            return Arrays.asList("á", "mo", "no");
        if (isVamos(syllables))
            return Arrays.asList("a", "mo");

        return syllables;
    }
}
