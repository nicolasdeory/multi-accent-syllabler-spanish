package com.nicolasdeory.syllabler;

import java.util.List;

public interface LiaisonRule {

    List<CharSequence> apply(Syllabler word, Syllabler nextWord);

    default List<CharSequence> apply(CharSequence word, CharSequence nextWord) {
        return apply(Syllabler.process(word), Syllabler.process(nextWord));
    }

    default List<CharSequence> apply(List<CharSequence> syllables, List<CharSequence> nextSyllables) {
        return apply(Syllabler.process(syllables), Syllabler.process(nextSyllables));
    }
}
