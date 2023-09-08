package com.nicolasdeory.syllabler;

import static org.junit.Assert.assertArrayEquals;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class RemoveTrailingRTest {

    private static final String YAML_RESOURCE = "remove_trailing_r.json";
    private static final String[] WORD_EXPECTATIONS = {
        "syllables"
    };

    private Rule rule = new RemoveTrailingR();

    @Parameterized.Parameter(value = 0)
    public String word;

    @Parameterized.Parameter(value = 1)
    public Collection<String> expectedSyllables;

    @Parameterized.Parameters(name = "{0}")
    public static Iterable<Object[]> data() throws Exception {
        Gson gson = new Gson();
        InputStream resourceAsStream = RemoveTrailingRTest.class.getClassLoader().getResourceAsStream(YAML_RESOURCE);
        Map<String, Map<String, Object>> testCases = gson.fromJson(new InputStreamReader(resourceAsStream), Map.class);
        Stream<Object[]> stream = testCases.entrySet().stream().map(RemoveTrailingRTest::entryData);
        return stream.collect(Collectors.toList());
    }

    private static Object[] entryData(Map.Entry<String, Map<String, Object>> entry) {
        Stream word = Stream.of(entry.getKey());
        Map<String, Object> map = entry.getValue();
        Stream expectations = Arrays.stream(WORD_EXPECTATIONS).map(map::get);
        return Stream.concat(word, expectations).toArray();
    }

    @Test
    public void testSyllables() throws Exception {
        Syllabler s = Syllabler.process(word);
        List<CharSequence> res = rule.apply(s);
        assertArrayEquals("Syllables for: " + word, expectedSyllables.toArray(), res.toArray());
    }

}