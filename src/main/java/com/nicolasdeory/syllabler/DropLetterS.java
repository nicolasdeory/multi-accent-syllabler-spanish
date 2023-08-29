package com.nicolasdeory.syllabler;

import java.util.ArrayList;
import java.util.List;

public class DropLetterS implements Rule {

    private boolean isS(char c) {
        return Character.toLowerCase(c) == 's';
    }

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = new ArrayList<>();
        for (CharSequence s : word.getSyllables()) {
            StringBuilder syll = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (i == s.length() - 1) {
                    if (isS(c)) {
                        c = Character.isUpperCase(c) ? 'H' : 'h';
                    }
                    syll.append(c);
                    continue;
                }
                char nextC = s.charAt(i + 1);
                if (isS(c) && nextC != 'h' && SyllablerUtils.isConsonant(nextC))
                    c = Character.isUpperCase(c) ? 'H' : 'h';
                ;
                syll.append(c);
            }
            syllables.add(syll.toString());
        }
        return syllables;
    }
}
