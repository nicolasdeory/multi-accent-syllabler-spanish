package com.nicolasdeory.syllabler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Syllabler {

    private final CharSequence word;
    private final ArrayList<Integer> positions;
    private final int wordLength;
    private boolean stressedFound;
    private int stressedIndex;
    private int letterAccent;

    private int currentPosition = 0;

    private Syllabler(CharSequence word) {
        this.stressedFound = false;
        this.stressedIndex = -1;
        this.letterAccent = -1;

        // delete non word characters
        this.word = Pattern.compile("[^\\p{L}]", Pattern.UNICODE_CHARACTER_CLASS).matcher(word).replaceAll("");
        this.wordLength = this.word.length();
        this.positions = new ArrayList<Integer>();
    }

    public char getAccentedCharacter() {
        if (letterAccent > -1) {
            return word.charAt(letterAccent);
        }
        return 0;
    }

    public ArrayList<Integer> getPositions() {
        return positions;
    }

    public int getStressedPosition() {
        return stressedIndex;
    }

    public ArrayList<CharSequence> getSyllables() {
        ArrayList<CharSequence> syllabes = new ArrayList<CharSequence>(positions.size());
        for (int i = 0; i < positions.size(); i++) {
            int start = positions.get(i);
            int end = wordLength;
            if (positions.size() > i + 1) {
                end = positions.get(i + 1);
            }
            CharSequence seq = word.subSequence(start, end);
            syllabes.add(seq);
        }
        return syllabes;
    }

    private int getNumberOfSyllables() {
        return getPositions().size();
    }

    public static Syllabler process(CharSequence seq) {
        Syllabler syllabler = new Syllabler(seq);
        syllabler.process();
        return syllabler;
    }

    public static Syllabler process(Collection<CharSequence> seq) {
        var seqList = seq.stream().collect(Collectors.toList());
        StringBuilder base = new StringBuilder();
        for (var seqs : seqList) {
            base.append(seqs.toString());
        }
        Syllabler syllabler = new Syllabler(base);
        syllabler.process();
        return syllabler;
    }

    private void process() {
        // Look for syllables in the word
        while (currentPosition < wordLength) {
            positions.add(currentPosition);
            onset();
            nucleus();
            coda();

            if (stressedFound && stressedIndex == -1) {
                stressedIndex = getNumberOfSyllables() - 1; // it marks the stressed syllable
            }

        }

        // If the word has not written accent, the stressed syllable is determined
        // according to the stress rules

        if (!stressedFound) {
            findStressedSyllable();
        }
    }

    /**
     * Determines the onset of the current syllable whose begins in pos and pos is changed to the follow position after
     * end of onset.
     *
     * Example: pos = 0, word = "casa", onset = "c", currentPosition is moved to 1
     *
     * @return pos
     */
    private void onset() {
        moveToNextVocalicSound();

        // (q | g) + u (example: queso, gueto)
        if (!isHeadAtLastLetter()) {
            checkOnsetForQueQui();
            checkOnsetForGueGui();
            checkOnsetForGueGuiWithDiaeresis();
        }
    }

    private void findStressedSyllable() {
        if (getNumberOfSyllables() <= 1) {
            stressedIndex = 0;  // Monosyllables
            return;
        }

        // Polysyllables
        if (isWordAccentedOnLastSyllable())
            stressedIndex = getNumberOfSyllables() - 2;  // Stressed penultimate syllable
        else
            stressedIndex = getNumberOfSyllables() - 1;      // Stressed last syllable
    }

    /**
     * Moves the head if the onset is of the form 'qu' Example: queso. Onset = 'qu', Head at 'e'
     */
    private void checkOnsetForQueQui() {
        if (charAt(currentPosition) == 'u' && getPreviousChar() == 'q') {
            moveForward();
        }
    }

    public boolean isWordAccentedOnLastSyllable() {
        char endLetter = charAt(wordLength - 1);
        return (!isConsonant(wordLength - 1) || (endLetter == 'y')) || (((endLetter == 'n')
            || (endLetter == 's') && !isConsonant(wordLength - 2)));
    }

    public boolean isWordAccentedOnFirstSyllable() {
        return stressedIndex == 0;
    }

    public boolean startsWithVocalic() {
        return charAt(0) == 'h' || !SyllablerUtils.isConsonant(charAt(0));
    }

    /**
     * Moves the head if the onset is of the form 'gue' or 'gui' Example: guita. Onset = 'gu', Head at 'i'
     */
    private void checkOnsetForGueGui() {
        if (charAt(currentPosition) == 'u' && getPreviousChar() == 'g') {
            char letter = charAt(currentPosition + 1);
            if (letter == 'e' || letter == 'é' || letter == 'i' || letter == 'í') {
                moveForward();
            }
        }
    }

    /**
     * Moves the head if the onset is of the form 'güe' or 'güi' Example: güero. Onset = 'gü', Head at 'e'
     */
    private void checkOnsetForGueGuiWithDiaeresis() {
        if (charAt(currentPosition) == 'ü' && getPreviousChar() == 'g') {
            // The 'u' with diaeresis is added to the consonant
            moveForward();
        }
    }

    /**
     * Iterate until the end of the word or until we find a vocalic sound (A,E,I,O,U,Y)
     */
    private void moveToNextVocalicSound() {
        while (currentPosition < wordLength && isConsonant(currentPosition) && charAt(currentPosition) != 'y') {
            moveForward();
        }
    }

    private void moveForward() {
        currentPosition++;
    }

    private void moveBackward() {
        currentPosition--;
    }

    private void skipAhead(int count) {
        currentPosition += count;
    }

    private char getPreviousChar() {
        return currentPosition > 0 ? charAt(currentPosition - 1) : '\0';
    }

    private boolean isHeadAtLastLetter() {
        return currentPosition >= wordLength - 1;
    }

    private boolean isHeadAtEndOfWord() {
        return currentPosition >= wordLength;
    }

    /**
     * Determines the nucleus of current syllable whose onset ending on pos - 1 and changes pos to the follow position
     * behind of nucleus
     **/
    private void nucleus() {
        // Saves the type of previous vowel when two vowels together exists
        VowelType previous = VowelType.OPEN;

        if (isHeadAtEndOfWord())
            return; // ¡¿Doesn't it have nucleus?!

        // Jumps a letter 'y' to the starting of nucleus, it is as consonant
        if (charAt(currentPosition) == 'y')
            moveForward();

        // First vowel
        if (!isHeadAtEndOfWord()) {
            VowelType type = SyllablerUtils.getVowelType(charAt(currentPosition));
            if (type == VowelType.CLOSED_WITH_ACCENT || type == VowelType.OPEN_WITH_ACCENT) {
                letterAccent = currentPosition;
                stressedFound = true;
                moveForward();
                return;
            }
            if (type == VowelType.CLOSED || type == VowelType.OPEN) {
                moveForward();
                previous = type;
            }
        }

        // If 'h' has been inserted in the nucleus then it doesn't determine diphthong neither hiatus

        boolean nucleusHasH = false;
        if (!isHeadAtEndOfWord() && charAt(currentPosition) == 'h') {
            moveForward();
            nucleusHasH = true;
        }

        // Second vowel
        if (!isHeadAtEndOfWord()) {
            VowelType type = SyllablerUtils.getVowelType(charAt(currentPosition));
            switch (type) {
                case OPEN_WITH_ACCENT:
                    letterAccent = currentPosition;
                    if (previous != VowelType.OPEN) {
                        stressedFound = true;
                    }
                    if (previous == VowelType.OPEN) {    // Two open-vowels don't form syllable
                        if (nucleusHasH)
                            moveBackward();
                        return;
                    }
                    moveForward();
                    break;
                case OPEN:
                    if (previous == VowelType.OPEN) {    // Two open-vowels don't form syllable
                        if (nucleusHasH)
                            moveBackward();
                        return;
                    }
                    moveForward();
                    break;
                case CLOSED_WITH_ACCENT:
                    letterAccent = currentPosition;
                    if (previous != VowelType.OPEN) {  // Diphthong
                        stressedFound = true;
                        moveForward();
                    } else if (nucleusHasH)
                        moveBackward();
                    return;
                case CLOSED:
                    if (!isHeadAtLastLetter()) { // ¿Is there a third vowel?
                        if (!isConsonant(currentPosition + 1)) {
                            if (charAt(currentPosition - 1) == 'h')
                                moveBackward();
                            return;
                        }
                    }

                    // Two equals close-vowels don't form diphthong
                    if (charAt(currentPosition) != charAt(currentPosition - 1))
                        moveForward();
                    return;  // It is a descendent diphthong

            }

        }

        // Third vowel?

        if (!isHeadAtEndOfWord() && SyllablerUtils.getVowelType(charAt(currentPosition)) == VowelType.CLOSED) {
            moveForward();
            // It is a triphthong
        }
    }

    private void coda() {

        // Syllable hasn't coda?
        if (isHeadAtEndOfWord() || !isConsonant(currentPosition)) {
            return;
        }

        // End of word?
        if (isHeadAtLastLetter()) {
            moveForward();
            return;
        }

        // If there is only a consonant between vowels, it belongs to the following syllable
        if (!isConsonant(currentPosition + 1)) {
            return;
        }

        // Has the syllable a third consecutive consonant?
        if (currentPosition < wordLength - 2) {
            if (isConsonant(currentPosition + 2)) { // There isn't third consonant
                checkThreeConsonantCoda();
                return;
            }

            // There is a third consonant
            checkTwoConsonantCoda();
            return;
        }

        if (charAt(currentPosition + 1) == 'y')
            return;

        skipAhead(2); // The word ends with two consonants
    }

    private void checkTwoConsonantCoda() {
        char c1 = charAt(currentPosition);
        char c2 = charAt(currentPosition + 1);

        // The groups LL, CH and RR begin a syllable
        if ((c1 == 'l') && (c2 == 'l'))
            return;
        if ((c1 == 'c') && (c2 == 'h'))
            return;
        if ((c1 == 'r') && (c2 == 'r'))
            return;

        // A consonant + 'h' begins a syllable, except for groups sh and rh
        if ((c1 != 's') && (c1 != 'r') && (c2 == 'h'))
            return;

        // If the letter 'y' is preceded by the some
        //      letter 's', 'l', 'r', 'n' or 'c' then
        //      a new syllable begins in the previous consonant
        // else it begins in the letter 'y'

        if ((c2 == 'y')) {
            if ((c1 == 's') || (c1 == 'l') || (c1 == 'r') || (c1 == 'n') || (c1 == 'c'))
                return;

            moveForward();
            return;
        }

        if (isAConsonantLGroup(c1, c2) || isAConsonantRGroup(c1, c2)) {
            return;
        }

        moveForward();
    }

    private boolean isAConsonantLGroup(char c1, char c2) {
        // groups: gl - cl - kl - bl - vl - pl - fl - tl
        return Pattern.compile("gl|cl|kl|bl|vl|pl|fl|tl").matcher(c1 + String.valueOf(c2)).matches();
    }

    private boolean isAConsonantRGroup(char c1, char c2) {
        // groups: gr - cr, kr - dr - tr - br - vr - pr - fr
        return Pattern.compile("gr|cr|kr|br|vr|pr|fr|tr").matcher(c1 + String.valueOf(c2)).matches();
    }

    private void checkThreeConsonantCoda() {
        char c1 = charAt(currentPosition);
        char c2 = charAt(currentPosition + 1);
        char c3 = charAt(currentPosition + 2);
        if ((currentPosition + 3) == wordLength) { // Three consonants to the end, foreign words?
            if ((c2 == 'y') && (c1 == 's') || (c1 == 'l') || (c1 == 'r') || (c1 == 'n') || (c1
                == 'c')) {  // 'y' as vowel
                return;
            }

            if (c3 == 'y') { // 'y' at the end as vowel with c2
                moveForward();
            } else {  // Three consonants to the end, foreign words?
                skipAhead(3);
            }
            return;
        }

        if ((c2 == 'y')) { // 'y' as vowel
            if ((c1 == 's') || (c1 == 'l') || (c1 == 'r') || (c1 == 'n') || (c1 == 'c'))
                return;

            moveForward();
            return;
        }

        // The groups pt, ct, cn, ps, mn, gn, ft, pn, cz, tz and ts begin a syllable
        // when preceded by other consonant

        if (isANewSyllableGroup(c2, c3)) {
            moveForward();
            return;
        }

        if ((c3 == 'l') || (c3 == 'r') ||    // The consonantal groups formed by a consonant
            // following the letter 'l' or 'r' can't be
            // separated and they always begin syllable
            ((c2 == 'c') && (c3 == 'h')) ||  // 'ch'
            (c3 == 'y')) {                   // 'y' as vowel
            moveForward();  // Following syllable begins in c2
        } else
            skipAhead(2); // c3 begins the following syllable
    }

    private boolean isANewSyllableGroup(char c1, char c2) {
        return Pattern.compile("pt|ct|cn|ps|mn|gn|ft|pn|cz|tz|ts").matcher(c1 + String.valueOf(c2)).matches();
    }

    private char charAt(int pos) {
        return Character.toLowerCase(word.charAt(pos));
    }

    private boolean isConsonant(int pos) {
        char c = word.charAt(pos);
        return SyllablerUtils.isConsonant(c);
    }

    public CharSequence getLastSyllable() {
        if (getSyllables().isEmpty())
            return null;
        return getSyllables().get(getSyllables().size() - 1);
    }

    public CharSequence getFirstSyllable() {
        if (getSyllables().isEmpty())
            return null;
        return getSyllables().get(0);
    }

//    public void replaceLastSyllable(CharSequence replacement) {
//        if (getSyllables().isEmpty())
//            return;
//        getSyllables().set(getSyllables().size() - 1, replacement);
//    }
//
//    public void replaceFirstSyllable(CharSequence replacement) {
//        if (getSyllables().isEmpty())
//            return;
//        getSyllables().set(0, replacement);
//    }

//    public void removeFirstSyllable() {
//        if (getSyllables().isEmpty())
//            return;
//        getSyllables().remove(0);
//    }

    public Syllabler replaceLastSyllable(CharSequence replacement) {
        // construct new syllabler replacing last syllable
        if (getSyllables().isEmpty())
            throw new IllegalStateException();
        List<CharSequence> newSyll = getSyllables();
        newSyll.set(getSyllables().size() - 1, replacement);
        return Syllabler.process(newSyll);
    }

    public Syllabler removeFirstSyllable() {
        if (getSyllables().isEmpty())
            throw new IllegalStateException();
        List<CharSequence> newSyll = getSyllables();
        newSyll.remove(0);
        return Syllabler.process(newSyll);
    }

    public CharSequence getWord() {
        return word;
    }
}
