package com.nicolasdeory.syllabler;

import static org.junit.Assert.assertArrayEquals;

import com.google.gson.Gson;
import com.nicolasdeory.syllabler.andalucia.LiaisonProcessor;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class LiaisonTest {

    private static final String YAML_RESOURCE = "liaison_test.json";

    private LiaisonProcessor subject;

    @Parameterized.Parameter(value = 0)
    public String word1;

    @Parameterized.Parameter(value = 1)
    public String word2;

    @Parameterized.Parameter(value = 2)
    public Collection<Collection<String>> expectedSyllables;

    @Parameterized.Parameters(name = "{0} {1} {2}")
    public static Iterable<Object[]> data() throws Exception {
        Gson gson = new Gson();
        InputStream resourceAsStream = LiaisonTest.class.getClassLoader().getResourceAsStream(YAML_RESOURCE);
        Map<String, Map<String, Object>> topLevelWords = gson.fromJson(new InputStreamReader(resourceAsStream),
            Map.class);
        // The iterable I want to return is of this form
        // [ [word1, word2, [expectedSyllables]], [word1, word2, [expectedSyllables]], ... ]
        // So I need to iterate over the top level words, and for each entry, iterate over the
        // syllables, and for each syllable, iterate over the expected syllables.
        // I can use flatMap to do this.

        // The first step is to iterate over the top level words.

        var set = topLevelWords.entrySet();
        List<Object[]> cases = new ArrayList<>();
        for (var entry : set) {
            String topword = entry.getKey();
            for (var entry2 : entry.getValue().entrySet()) {
                String secondword = entry2.getKey();
                Object[] casee = new Object[3];
                casee[0] = topword;
                casee[1] = secondword;
                casee[2] = entry2.getValue();
                // this will give ["la","nube",[["la"],["nu","be"]]]
                cases.add(casee);
            }
        }

        return cases;
//        return y;
    }

    private static Stream<List<String>> entryData(Map.Entry<String, Map<String, Object>> entry) {
        // The second step is to iterate over the syllables.
        Stream<List<String>> stream = entry.getValue().entrySet().stream().flatMap(syllableData(entry.getKey()));
        return stream;
    }

    private static java.util.function.Function<Map.Entry<String, Object>, Stream<List<String>>> syllableData(
        String word) {
        return (Map.Entry<String, Object> syllableEntry) -> {
            // The third step is to iterate over the expected syllables.
//            List<Object> a = new ArrayList<>(); // Should be string or array of string
//            a.add(word);
//            a.add(syllableEntry.getKey());
//            a.add(syllableEntry.getValue());
            Stream<List<String>> stream = ((List<String>) syllableEntry.getValue()).stream().map(expectedSyllable -> {
                List<String> a = new ArrayList<>();
                a.add(word);
                a.add(syllableEntry.getKey());
                a.add(expectedSyllable);
                return a;
            });
            return stream;
        };
    }

    @Before
    public void before() {
        this.subject = LiaisonProcessor.process(Syllabler.process(word1), Syllabler.process(word2));
    }

    @Test
    public void testSyllables() throws Exception {
        LiaisonedWords result = subject.getResult();
        Collection<String> syllables1 = result.getSyllables1AfterStressed().stream().map(x -> x.toString()).collect(Collectors.toList());
        Collection<String> syllables2 = result.getSyllables2AfterStressed().stream().map(x -> x.toString()).collect(Collectors.toList());
        var exps = expectedSyllables.stream().collect(Collectors.toList());
        assertArrayEquals(exps.get(0).toArray(), syllables1.toArray());
        assertArrayEquals(exps.get(1).toArray(), syllables2.toArray());
    }

}