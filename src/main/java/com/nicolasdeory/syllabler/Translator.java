package com.nicolasdeory.syllabler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Translator {
    public String translate(String input) {
        String phrase = input;
        List<String> words = Arrays.stream(phrase.split("[ !¡¿?.,;:]+")).filter(x -> x.length() > 0).collect(Collectors.toList());
        List<List<CharSequence>> resultingWords = new ArrayList<>();
        System.out.println("Traduciendo: " + phrase);
        Rule puesPara = new PuesPara();
        Rule removeAdo = new RemoveAdoIdo();
        words = words.stream().map(puesPara::apply).map(removeAdo::apply).map(x-> String.join("", x)).collect(Collectors.toList());
        int i = 0;
        String currentWord = words.get(i);
        String nextWord = null;
        LiaisonedWords result = null;
        if (words.size() == 1) {
            resultingWords.add(Syllabler.process(words.get(0)).getSyllables().stream().map(x->x.toString().toLowerCase()).collect(Collectors.toList()));
        }
        while (i < words.size() - 1) {
            nextWord = words.get(i + 1);
            Syllabler s1 = Syllabler.process(currentWord);
            Syllabler s2 = Syllabler.process(nextWord);
            LiaisonProcessor liaisonProcessor = LiaisonProcessor.process(s1, s2);
            result = liaisonProcessor.getResult();
            if (result.getSyllables2AfterStressed().size() > 0) {
                resultingWords.add(result.getSyllables1AfterStressed());
                currentWord = result.getSyllables2AfterStressed().stream().collect(Collectors.joining());
            } else {
                currentWord = result.getSyllables1AfterStressed().stream().collect(Collectors.joining());
            }

            i++;
        }
        if (result != null) {
            resultingWords.add(result.getSyllables2AfterStressed());
        }

        DropLetterS dropS = new DropLetterS();
        AspirateGJ aspirateGJ = new AspirateGJ();
        StringBuilder finalString = new StringBuilder();
        for (List<CharSequence> s : resultingWords) {
            s = dropS.apply(String.join("", s));
            s = aspirateGJ.apply(String.join("", s));
            finalString.append(String.join("", s)).append(" ");
        }
        System.out.println("Translation is " + finalString);
        return finalString.toString();
    }
}
