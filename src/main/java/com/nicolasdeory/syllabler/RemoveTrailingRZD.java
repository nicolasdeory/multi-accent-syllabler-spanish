package com.nicolasdeory.syllabler;

import static com.nicolasdeory.syllabler.SyllablerUtils.accentuateVowel;

import java.util.ArrayList;
import java.util.List;

public class RemoveTrailingRZD implements LiaisonRule {

    private boolean isRZD(char c) {
        char cLower = Character.toLowerCase(c);
        return (cLower == 'r'|| cLower == 'z' || cLower == 'd');
    }

    @Override
    public List<CharSequence> apply(Syllabler word, Syllabler nextWord) {

        List<CharSequence> syllables = new ArrayList<>();
        if (word.getSyllables().size() == 1 && nextWord.startsWithVocalic()) {
            return word.getSyllables();
        }
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
                    if (!isRZD(c) || (i > 0 && SyllablerUtils.isConsonant(s.charAt(i - 1)))) {
                        syll.append(c);
                    } else if (word.getSyllables().size() == 1) {
                        syll.append('h');
                    }
                    continue;
                }
                char nextC = s.charAt(i + 1);
                if (i == s.length() - 2 && isRZD(nextC) && !SyllablerUtils.isConsonant(c))
                    c = accentuateVowel(c);
                syll.append(c);
            }
            syllables.add(syll.toString());
        }
        return syllables;
    }
}
