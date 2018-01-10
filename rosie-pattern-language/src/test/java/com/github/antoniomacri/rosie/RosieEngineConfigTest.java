package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;


public class RosieEngineConfigTest {
    private RosieEngine rosie;

    @Before
    public void init() {
        rosie = new RosieEngine();
    }

    @After
    public void close() {
        rosie.close();
    }


    @Test
    public void testConfig() throws IOException {
        String result = rosie.config();
        assertThat(result, is(notNullValue()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map cfg = objectMapper.readValue(result, Map.class);

        for (Object key : cfg.keySet()) {
            if (cfg.get(key) instanceof Map) {
                Map subMap = (Map) cfg.get(key);
                assertThat("Config key=" + key + " attribute name", subMap.get("name"), is(notNullValue()));
                assertThat("Config key=" + key + " attribute desc", subMap.get("desc"), is(notNullValue()));
                if (subMap.get("value") == null) {
                    System.out.println("NOTE: no value for config key " + subMap.get("name"));
                }
            }
        }
    }
}
