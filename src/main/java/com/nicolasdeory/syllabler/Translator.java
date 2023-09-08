package com.nicolasdeory.syllabler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Translator {

    public String translate(String input) {
        List<String> segments = new ArrayList<>();
        Matcher m = Pattern.compile("([\\p{IsLatin} ]+)|([!¡¿?.,;:])").matcher(input);
        while (m.find()) {
            segments.add(m.group());
        }

        System.out.println("Traduciendo: " + input);

        Rule puesPara = new PuesPara();
        Rule vamos = new Vamos();
        Rule removeR = new RemoveTrailingR();
        Rule removeAdo = new RemoveAdoIdo();

        List<List<CharSequence>> resultingWords = new ArrayList<>();
        StringBuilder currentSentence = new StringBuilder();
        for (String segment : segments) {
            if ("!¡¿?.,;:".contains(segment)) {
                resultingWords.add(Arrays.asList(segment));
            } else {
//                currentSentence = new StringBuilder(segment);
                if (segment.length() > 0) {
                    processSentence(segment, resultingWords,
                        puesPara, vamos, removeR, removeAdo);
                }
//                currentSentence.append(segment).append(" ");
            }
        }

//        if (currentSentence.length() > 0) {
//            processSentence(currentSentence.toString(), resultingWords,
//                puesPara, vamos, removeR, removeAdo);
//        }

        DropLetterS dropS = new DropLetterS();
        AspirateGJ aspirateGJ = new AspirateGJ();
        StringBuilder finalString = new StringBuilder();
        for (int i = 0; i < resultingWords.size(); i++) {
            List<CharSequence> s = resultingWords.get(i);
            if (s.size() == 1 && "!¡¿?.,;:".contains(s.get(0))) {
                finalString.append(s.get(0));
                // if the next word is not punctuation, add a space
                if (i < resultingWords.size() - 1 && resultingWords.get(i+1).size() > 0 && !"!¡¿?.,;:".contains(resultingWords.get(i + 1).get(0))) {
                    finalString.append(" ");
                }
                continue;
            }
            s = dropS.apply(String.join("", s));
            s = aspirateGJ.apply(String.join("", s));
            finalString.append(String.join("", s));
            // append a space only if the next word is not a punctuation mark (using contains)
            if (i < resultingWords.size() - 1 && resultingWords.get(i+1).size() > 0 && !"!¡¿?.,;:".contains(resultingWords.get(i + 1).get(0))) {
                finalString.append(" ");
            }
        }

        System.out.println("Translation is " + finalString);
        return finalString.toString();
    }

    private void processSentence(String sentence, List<List<CharSequence>> resultingWords,
        Rule puesPara, Rule vamos, Rule removeR, Rule removeAdo) {
        List<String> words = Arrays.stream(sentence.split(" "))
            .filter(x -> x.length() > 0)
            .map(x -> puesPara.apply(x))
            .map(x -> vamos.apply(x))
            .map(x -> removeR.apply(x))
            .map(x -> removeAdo.apply(x))
            .map(x -> String.join("", x))
            .collect(Collectors.toList());

        int i = 0;
        String currentWord = words.get(i);
        String nextWord;
        LiaisonedWords result = null;
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
    }
}
