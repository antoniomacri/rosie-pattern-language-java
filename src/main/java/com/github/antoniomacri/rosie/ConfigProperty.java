package com.github.antoniomacri.rosie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigProperty {
    private String name;
    private String description;
    private String value;
    @JsonProperty("set_by")
    private String setBy;


    /**
     * A unique name for this item of the configuration.
     */
    public String getName() {
        return name;
    }

    /**
     * A human-readable description of the item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * The current value for this item.
     */
    public String getValue() {
        return value;
    }

    /**
     * Who set this configuration item.
     * <p>
     * Possible values:
     * <ul>
     * <li>"distribution": if this aspect of the configuration is set by the Rosie distribution that was installed;</li>
     * <li>"build": if set at build-time;</li>
     * <li>"default": if it is a run-time default that can be customized;</li>
     * <li>"rcfile": if set in the Rosie init file .rosierc;</li>
     * <li>"CLI": if set on the command line (CLI only);</li>
     * <li>other values, including the empty string, are possible</li>
     * </ul>
     */
    public String getSetBy() {
        return setBy;
    }


    @Override
    public String toString() {
        return name + "=\"" + value + "\"";
    }
}
