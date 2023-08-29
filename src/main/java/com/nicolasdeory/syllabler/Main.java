package com.nicolasdeory.syllabler;

import com.nicolasdeory.syllabler.andalucia.LiaisonProcessor;
import com.nicolasdeory.syllabler.andalucia.rules.AspirateGJ;
import com.nicolasdeory.syllabler.andalucia.rules.DropLetterS;
import com.nicolasdeory.syllabler.andalucia.rules.PuesPara;
import com.nicolasdeory.syllabler.andalucia.rules.Rule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        Translator t = new Translator();
        System.out.println(t.translate("¿A dónde vas? Pues en este día me estoy pensando en si iré al campo o otra cosa. Ya ha amanecido y me he hartado de esperar"));
    }
}