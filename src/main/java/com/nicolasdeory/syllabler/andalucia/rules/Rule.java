package com.nicolasdeory.syllabler.andalucia.rules;

import java.util.ArrayList;

public interface Rule {

    ArrayList<String> apply(ArrayList<String> syllables);

    default String getLastSyllable(ArrayList<String> syllables) {
        return syllables.get(syllables.size() - 1);
    }
}
