package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
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
        List cfg = objectMapper.readValue(result, List.class);

        List list1 = (List) cfg.get(0);
        for (Object elem : list1) {
            Map map = (Map) elem;
            assertThat("Config key=" + "key" + " attribute name", map.get("name"), is(notNullValue()));
            assertThat("Config key=" + "key" + " attribute description", map.get("description"), is(notNullValue()));
            if (map.get("value") == null) {
                System.out.println("NOTE: no value for config key " + map.get("name"));
            }
        }

        List list2 = (List) cfg.get(1);
        String rplVersion = null;
        String libpath = null;
        for (Object elem : list2) {
            Map map = (Map) elem;
            if (map.get("name").equals("RPL_VERSION")) {
                rplVersion = (String) map.get("value");
            }
            if (map.get("name").equals("ROSIE_LIBPATH")) {
                libpath = (String) map.get("value");
            }
        }
        assertThat(rplVersion, is(notNullValue()));
        assertThat(libpath, is(notNullValue()));
    }
}
