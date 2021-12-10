package org.palladiosimulator.somox.cipm.modelrefinement.parameters;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.palladiosimulator.somox.cipm.modelrefinement.parameters.ServiceParameters;

/**
 * Parsed service parameters.
 * 
 * @author JP
 *
 */
public class ServiceParameters {

    private static final Logger LOGGER = Logger.getLogger(ServiceParameters.class);

    /**
     * Service parameters instance having no parameters set. This instance is shared to reduce memory consumption.
     */
    public static ServiceParameters EMPTY = new ServiceParameters();

    private static final TypeReference<TreeMap<String, Object>> PARSED_PARAMETERS_TYPE_REF = new TypeReference<TreeMap<String, Object>>() {
    };

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.enable(JsonParser.Feature.ALLOW_TRAILING_COMMA);
    }

    private final SortedMap<String, Object> parameters;

    private final SortedMap<String, Object> readOnlyParameters;

    public ServiceParameters() {
        this.parameters = Collections.emptySortedMap();
        this.readOnlyParameters = Collections.unmodifiableSortedMap(this.parameters);
    }

    private ServiceParameters(final Map<String, Object> parameters) {
        this.parameters = new TreeMap<>(parameters);
        this.readOnlyParameters = Collections.unmodifiableSortedMap(this.parameters);
    }

    private ServiceParameters(final String parameters) throws IOException {
        this.parameters = mapper.readValue(parameters, PARSED_PARAMETERS_TYPE_REF);
        this.readOnlyParameters = Collections.unmodifiableSortedMap(this.parameters);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (this.getClass() != obj.getClass()) {
            return false;
        }

        ServiceParameters other = (ServiceParameters) obj;
        return this.parameters.equals(other.parameters);
    }

    /**
     * Gets the service parameters ordered by name.
     * 
     * @return The service parameters.
     */
    public SortedMap<String, Object> getParameters() {
        return this.readOnlyParameters;
    }

    @Override
    public int hashCode() {
        return this.parameters.hashCode();
    }

    /**
     * Returns an instance of {@link ServiceParameters}, containing the passed parameters.
     * 
     * @param parameters
     *            The service parameters.
     * @return An instance of {@link ServiceParameters}, containing the passed parameters.
     */
    public static ServiceParameters build(final Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return EMPTY;
        }
        return new ServiceParameters(parameters);
    }

    /**
     * Returns an instance of {@link ServiceParameters}, containing the passed parameters.
     * 
     * @param parameters
     *            The parameters in json format.
     * @return An instance of {@link ServiceParameters}, containing the passed parameters.
     */
    public static ServiceParameters buildFromJson(final String parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return EMPTY;
        }
        try {
            return new ServiceParameters(parameters);
        } catch (IOException e) {
            LOGGER.warn(String.format("Could not parse parameters %s.", parameters), e);
            return EMPTY;
        }
    }
    
    public ServiceParameters merge(ServiceParameters parametersToAdd) {
    	Map<String, Object> map3 = Stream.of(this.parameters, parametersToAdd.getParameters())
    			  .flatMap(map -> map.entrySet().stream())
    			  .collect(Collectors.toMap(
    			    Map.Entry::getKey,
    			    Map.Entry::getValue,
    			    (v1, v2) -> v1));
    	
    	return new ServiceParameters(map3);

    }
}
