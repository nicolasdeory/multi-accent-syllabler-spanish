package com.nicolasdeory.syllabler;

import java.util.List;

public class RemoveAdoIdo implements Rule {

    @Override
    public List<CharSequence> apply(Syllabler word) {

        // Check if is llana (stress on second to last)
        if (word.getStressedPosition() != word.getSyllables().size() - 2) {
            return word.getSyllables();
        }

        String actualWord = word.getWord().toString();
        String wordExceptThreeCharacters = actualWord.substring(0, actualWord.length() - 3);
        String lastThreeCharacters = actualWord.substring(actualWord.length() - 3);
        // remove accents and lowercase
        String lastThreeCharactersNormalized = SyllablerUtils.normalizeWord(lastThreeCharacters);

        // If enfado/enfada, exception, dont process
        if (actualWord.equalsIgnoreCase("enfado") || actualWord.equalsIgnoreCase("enfada")) {
            return word.getSyllables();
        }

        // Check if word ends in -ado
        if (lastThreeCharactersNormalized.equals("ado") || lastThreeCharactersNormalized.equals("ada")) {
            // If syllables >= 3, remove d in last syllable (replace by empty)
            if (word.getSyllables().size() >= 3) {
                lastThreeCharacters = lastThreeCharacters.toLowerCase()
                    .replace("ado", "ao")
                    .replace("ada", "áh");
                actualWord = wordExceptThreeCharacters + lastThreeCharacters;
            }

        }

        if (lastThreeCharactersNormalized.equals("ido") || lastThreeCharactersNormalized.equals("ida")) {
            // If syllables >= 3, remove d in last syllable (replace by empty)
            if (word.getSyllables().size() >= 3) {
                lastThreeCharacters = lastThreeCharacters.toLowerCase()
                    .replace("ido", "ío")
                    .replace("ído", "ío")
                    .replace("ida", "ía")
                    .replace("ída", "ía");
                actualWord = wordExceptThreeCharacters + lastThreeCharacters;
            }
        }

        return Syllabler.process(actualWord).getSyllables();
    }
}
