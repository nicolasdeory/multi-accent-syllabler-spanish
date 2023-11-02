package com.nicolasdeory.syllabler;

import java.util.ArrayList;
import java.util.List;

public class AspirateX implements Rule {

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = new ArrayList<>();
        List<CharSequence> wordSyllbs = word.getSyllables();
        for (int i = 0; i < wordSyllbs.size(); i++) {
            CharSequence s = wordSyllbs.get(i);
            if (s.length() < 2) {
                syllables.add(s);
                continue;
            }
            String newS = s.toString();
            if (newS.charAt(newS.length() - 1) == 'x' && i != wordSyllbs.size() - 1 && SyllablerUtils.isConsonant(
                wordSyllbs.get(i + 1).charAt(1))) {
                newS = newS.replace('x', 'h');
            }
            syllables.add(newS);
        }
        return syllables;
    }
}
