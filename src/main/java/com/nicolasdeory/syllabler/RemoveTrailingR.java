package com.nicolasdeory.syllabler;

import static com.nicolasdeory.syllabler.SyllablerUtils.accentuateVowel;

import java.util.ArrayList;
import java.util.List;

public class RemoveTrailingR implements Rule {

    private boolean isR(char c) {
        return Character.toLowerCase(c) == 'r';
    }

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = new ArrayList<>();
        for (int k = 0; k < word.getSyllables().size(); k++) {
            if (k != word.getSyllables().size() - 1) {
                syllables.add(word.getSyllables().get(k));
                continue;
            }
            CharSequence s = word.getSyllables().get(k);
            StringBuilder syll = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (i == s.length() - 1) {
                    if (!isR(c)) {
                        syll.append(c);
                    }
                    continue;
                }
                char nextC = s.charAt(i + 1);
                if (isR(nextC) && !SyllablerUtils.isConsonant(c))
                    c = accentuateVowel(c);
                syll.append(c);
            }
            syllables.add(syll.toString());
        }
        return syllables;
    }
}
