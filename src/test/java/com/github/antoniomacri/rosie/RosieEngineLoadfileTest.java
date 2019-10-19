package com.github.antoniomacri.rosie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;


public class RosieEngineLoadfileTest {
    private static final String TEST_DIR = "src/test/resources/tests";

    private RosieEngine rosie;

    @BeforeEach
    public void init() {
        rosie = new RosieEngine();
    }


    @Test
    public void testLoad() {
        String packageName = rosie.loadFile(Paths.get(TEST_DIR, "test.rpl").toString());
        assertThat(packageName).isEqualTo("test");
    }

    @Test
    public void testLoadNoPackage() {
        String packageName = rosie.loadFile(Paths.get(TEST_DIR, "test-no-package.rpl").toString());
        assertThat(packageName).isNull();
    }
}
