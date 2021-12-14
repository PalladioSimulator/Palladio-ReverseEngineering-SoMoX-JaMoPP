package org.palladiosimulator.somox.cipm.modelrefinement.parameters.monitoring;

/**
 * Service parameter serialisation.
 *
 * @author JP
 *
 */
public class ServiceParameters {

    /**
     * Empty service parameters.
     */
    public static ServiceParameters EMPTY = new ServiceParameters();

    private final StringBuilder stringBuilder;

    /**
     * Initializes a new instance of {@link ServiceParameters} class.
     */
    public ServiceParameters() {
        this.stringBuilder = new StringBuilder();
    }

    /**
     * Appends an float parameter.
     *
     * @param name
     *            Parameter name.
     * @param value
     *            Parameter value.
     */
    public void addFloat(final String name, final double value) {
        this.stringBuilder.append("\"").append(name).append("_VALUE\":").append(value).append(",");
    }

    /**
     * Appends an integer parameter.
     *
     * @param name
     *            Parameter name.
     * @param value
     *            Parameter value.
     */
    public void addInt(final String name, final int value) {
        this.stringBuilder.append("\"").append(name).append("_VALUE\":").append(value).append(",");
    }

    /**
     * Appends an boolean parameter.
     *
     * @param name
     *            Parameter name.
     * @param value
     *            Parameter value.
     */
    public void addBoolean(final String name, final boolean value) {
        this.stringBuilder.append("\"").append(name).append("_VALUE\":").append(value).append(",");
    }

    /**
     * Appends an string parameter.
     *
     * @param name
     *            Parameter name.
     * @param value
     *            Parameter value.
     */
    public void addString(final String name, final String value) {
        if (value == null) {
            this.stringBuilder.append("\"").append(name).append("_VALUE\":null,");
            this.stringBuilder.append("\"").append(name).append("_BYTESIZE\":0,");
        } else {
            this.stringBuilder.append("\"").append(name).append("_VALUE\":\"").append(value).append("\",");
            this.stringBuilder.append("\"").append(name).append("_BYTESIZE\":").append(value.length()).append(",");
        }
    }

    public void addList(final String name, final int size) {
        this.stringBuilder.append("\"").append(name).append("_NUMBER_OF_ELEMENTS\":").append(size).append(",");
    }

    /**
     * Gets the serialized parameters.
     */
    @Override
    public String toString() {
        return "{" + this.stringBuilder.toString() + "}";
    }

}
