package com.nicolasdeory.syllabler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class SyllablerTest {

    private static final String YAML_RESOURCE = "silabas_test.json";
    private static final String[] WORD_EXPECTATIONS = {
        "syllables"
    };

    private Syllabler subject;

    @Parameterized.Parameter(value = 0)
    public String word;

    @Parameterized.Parameter(value = 1)
    public Collection<String> expectedSyllables;


    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() throws Exception {
        Gson gson = new Gson();
        InputStream resourceAsStream = SyllablerTest.class.getClassLoader().getResourceAsStream(YAML_RESOURCE);
        Map<String,Map<String,Object>> testCases =  gson.fromJson(new InputStreamReader(resourceAsStream), Map.class);
        Stream<Object[]> stream = testCases.entrySet().stream().map(SyllablerTest::entryData);
        return stream.collect(Collectors.toList());
    }

    private static Object[] entryData(Map.Entry<String, Map<String,Object>> entry) {
        Stream word = Stream.of(entry.getKey());
        Map<String, Object> map = entry.getValue();
        Stream expectations = Arrays.stream(WORD_EXPECTATIONS).map(map::get);
        return Stream.concat(word, expectations).toArray();
    }

    @Before
    public void before() {
        this.subject = Syllabler.process(word);
    }

    @Test
    public void testSyllables() throws Exception {
        Collection<CharSequence> syllables = subject.getSyllables();
        assertArrayEquals("Syllables for: "+word, expectedSyllables.toArray(), syllables.toArray());
    }

}