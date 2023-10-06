package com.nicolasdeory.syllabler;

import static com.nicolasdeory.syllabler.SyllablerUtils.accentuateVowel;
import static com.nicolasdeory.syllabler.SyllablerUtils.normalizeWord;

import java.util.ArrayList;
import java.util.List;

public class RemoveTrailingRLZD implements Rule {

    private boolean isRLZD(char c) {
        char cLower = Character.toLowerCase(c);
        return (cLower == 'r' || cLower == 'l' || cLower == 'z' || cLower == 'd');
    }
    private boolean isEl(Syllabler word) {
        if (word.getSyllables().size() == 1 && normalizeWord(word.getSyllables().get(0).toString()).equalsIgnoreCase("el")) {
            return true;
        }
        return false;
    }

    private boolean isAl(Syllabler word) {
        if (word.getSyllables().size() == 1 && normalizeWord(word.getSyllables().get(0).toString()).equalsIgnoreCase("al")) {
            return true;
        }
        return false;
    }

    @Override
    public List<CharSequence> apply(Syllabler word) {

        List<CharSequence> syllables = new ArrayList<>();
        if (isEl(word) || isAl(word)) {
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
                    if (!isRLZD(c) || (i > 0 && SyllablerUtils.isConsonant(s.charAt(i - 1)))) {
                        syll.append(c);
                    } else if (word.getSyllables().size() == 1) {
                        syll.append('h');
                    }
                    continue;
                }
                char nextC = s.charAt(i + 1);
                if (isRLZD(nextC) && !SyllablerUtils.isConsonant(c))
                    c = accentuateVowel(c);
                syll.append(c);
            }
            syllables.add(syll.toString());
        }
        return syllables;
    }
}
