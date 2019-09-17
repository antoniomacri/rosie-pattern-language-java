package com.github.antoniomacri.rosie;

import java.util.List;


public class Configuration {
    public static final String ROSIE_VERSION = "ROSIE_VERSION";
    public static final String ROSIE_HOME = "ROSIE_HOME";
    public static final String ROSIE_LIBDIR = "ROSIE_LIBDIR";
    public static final String RPL_VERSION = "RPL_VERSION";
    public static final String ROSIE_LIBPATH = "ROSIE_LIBPATH";

    private final List<ConfigProperty> rosieConfiguration;
    private final List<ConfigProperty> engineConfiguration;
    private final List<ConfigProperty> encoderConfiguration;


    public Configuration(List<ConfigProperty> rosie, List<ConfigProperty> engine, List<ConfigProperty> encoder) {
        this.rosieConfiguration = rosie;
        this.engineConfiguration = engine;
        this.encoderConfiguration = encoder;
    }


    /**
     * The Rosie installation configuration (engine-independent).
     */
    public List<ConfigProperty> getRosieConfiguration() {
        return rosieConfiguration;
    }

    /**
     * The engine configuration.
     */
    public List<ConfigProperty> getEngineConfiguration() {
        return engineConfiguration;
    }

    /**
     * Configuration parameters passed to every output encoder.
     * <p>
     * (An encoder may use any, all, or none of these.)
     */
    public List<ConfigProperty> getEncoderConfiguration() {
        return encoderConfiguration;
    }


    /**
     * Gets the installed Rosie version.
     */
    public String getRosieVersion() {
        return getPropertyValue(rosieConfiguration, ROSIE_VERSION);
    }

    /**
     * Gets Rosie installation directory.
     */
    public String getRosieHome() {
        return getPropertyValue(rosieConfiguration, ROSIE_HOME);
    }

    /**
     * Gets the libdir, that is the <tt>rpl</tt> directory of the Rosie installation directory.
     */
    public String getLibdir() {
        return getPropertyValue(rosieConfiguration, ROSIE_LIBDIR);
    }


    /**
     * Gets the latest RPL version supported.
     */
    public String getRplVersion() {
        return getPropertyValue(engineConfiguration, RPL_VERSION);
    }

    /**
     * Gets the libpath, that is the "search path" (a list of file system directories) used to load packages from.
     */
    public String getLibpath() {
        return getPropertyValue(engineConfiguration, ROSIE_LIBPATH);
    }


    private String getPropertyValue(List<ConfigProperty> list, String propertyName) {
        for (ConfigProperty c : list) {
            if (c.getName().equals(propertyName)) {
                return c.getValue();
            }
        }
        return null;
    }
}
