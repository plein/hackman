package configuration;

/**
 * Created by Plein on 06/12/2016.
 */
public enum Configuration {

    MAX_DISTANCE_IF_SWORD(3);


    private final int value;

    Configuration(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
