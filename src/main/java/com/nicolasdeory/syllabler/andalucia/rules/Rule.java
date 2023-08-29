package com.nicolasdeory.syllabler.andalucia.rules;

import com.nicolasdeory.syllabler.Syllabler;
import java.util.ArrayList;
import java.util.List;

public interface Rule {

    List<CharSequence> apply(Syllabler word);

    default List<CharSequence> apply(CharSequence word) {
        return apply(Syllabler.process(word));
    }

    default List<CharSequence> apply(List<CharSequence> syllables) {
        return apply(Syllabler.process(syllables));
    }
}
