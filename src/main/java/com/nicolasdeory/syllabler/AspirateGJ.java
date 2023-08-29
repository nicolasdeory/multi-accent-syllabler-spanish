package com.nicolasdeory.syllabler;

import java.util.ArrayList;
import java.util.List;

public class AspirateGJ implements Rule {

    private boolean isG(char c) {
        return Character.toLowerCase(c) == 'g';
    }

    private boolean isJ(char c) {
        return Character.toLowerCase(c) == 'j';
    }

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = new ArrayList<>();
        for (CharSequence s : word.getSyllables()) {
            if (s.length() < 2) {
                syllables.add(s);
                continue;
            }
            String newS = s.toString().replace('j', 'h');
            if (newS.charAt(0) == 'g' && (newS.charAt(1) == 'e' || newS.charAt(1) == 'i')) {
                newS = newS.replace('g', 'h');
            }
            syllables.add(newS);
        }
        return syllables;
    }
}
