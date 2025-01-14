package org.mateh.simpleelementsrework.interfaces;

public enum ElementType {

    BLAZING_STRIKE("damage.blazing_strike"),
    STORM_CALL("damage.storm_call");

    private final String configPath;

    ElementType(String configPath) {
        this.configPath = configPath;
    }

    public String getConfigPath() {
        return configPath;
    }
}
