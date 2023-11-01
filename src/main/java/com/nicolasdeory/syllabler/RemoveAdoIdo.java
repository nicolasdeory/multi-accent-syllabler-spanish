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
        String wordExcept4Characters =
            actualWord.length() > 3 ? actualWord.substring(0, actualWord.length() - 4) : wordExceptThreeCharacters;
        String lastThreeCharacters = actualWord.substring(actualWord.length() - 3);
        // remove accents and lowercase
        String lastThreeCharactersNormalized = SyllablerUtils.normalizeWord(lastThreeCharacters);
        String last4characters =
            actualWord.length() > 3 ? actualWord.substring(actualWord.length() - 4) : lastThreeCharacters;
        // remove accents and lowercase
        String last4charactersNormalized = SyllablerUtils.normalizeWord(last4characters);
        // If enfado/enfada, exception, dont process
        if (actualWord.equalsIgnoreCase("enfado") || actualWord.equalsIgnoreCase("enfada")
            || actualWord.equalsIgnoreCase("enfados") || actualWord.equalsIgnoreCase("enfadas")) {
            return word.getSyllables();
        }

        // Check if word ends in -ado
        if (lastThreeCharactersNormalized.equals("ado") || lastThreeCharactersNormalized.equals("ada")
            || last4charactersNormalized.equals("ados") || last4charactersNormalized.equals("adas")) {
            // If syllables >= 3, remove d in last syllable (replace by empty)
            if (word.getSyllables().size() >= 3) {
                last4characters = last4characters.toLowerCase()
                    .replace("ados", "aos")
                    .replace("ado", "ao")
                    .replace("adas", "ás")
                    .replace("ada", "áh");
                actualWord = wordExcept4Characters + last4characters;
            }

        }

        if (lastThreeCharactersNormalized.equals("ido") || lastThreeCharactersNormalized.equals("ida")
            || last4charactersNormalized.equals("idos") || last4charactersNormalized.equals("idas")) {
            // If syllables >= 3, remove d in last syllable (replace by empty)
            if (word.getSyllables().size() >= 3) {
                last4characters = last4characters.toLowerCase()
                    .replace("idos", "íos")
                    .replace("ídos", "íos")
                    .replace("ido", "ío")
                    .replace("ído", "ío")
                    .replace("idas", "ías")
                    .replace("ídas", "ías")
                    .replace("ida", "ía")
                    .replace("ída", "ía")
                ;
                actualWord = wordExcept4Characters + last4characters;
            }
        }

        return Syllabler.process(actualWord).getSyllables();
    }
}
