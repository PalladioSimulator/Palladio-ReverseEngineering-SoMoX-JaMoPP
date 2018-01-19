package org.somox.core.configuration;

import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.somox.configuration.SoMoXConfiguration;

/**
 * Asserts correct behavior of {@link SoMoXConfiguration#SoMoXConfiguration(java.util.Map)},
 * {@link SoMoXConfiguration#toMap()} and
 * {@link SoMoXConfiguration#applyAttributeMap(java.util.Map)}. The behavior of a
 * {@link SoMoXConfiguration} is compared by recursively comparing getter return values.
 * 
 * @author Joshua Gleitze
 */
public class SoMoXConfigurationAttributeMapTest {
    /**
     * maps attribute keys to setters. Setters can be executed by providing the instance and the
     * value to set to the {@link BiConsumer}.
     */
    private static final Map<String, BiConsumer<SoMoXConfiguration, Object>> SETTERS = getKeyToSettersMapping();

    /**
     * {@link Supplier}s for values for all attribute keys.
     */
    private static final Map<String, Supplier<Object>> VALUE_SUPPLIERS = getValueSuppliers();

    /**
     * Asserts that converting the default {@link SoMoXConfiguration} into an attribute map and back
     * does not change its behavior.
     */
    @Test
    public void testEqualityWithDefaultValues() {
        SoMoXConfiguration defaultConfiguration = new SoMoXConfiguration();
        Map<String, Object> attributeMap = defaultConfiguration.toMap();
        SoMoXConfiguration afterConversion = new SoMoXConfiguration(attributeMap);
        assertThat("The default configuration is changed on conversion!", afterConversion,
                behavesLike(defaultConfiguration));
        
        afterConversion = new SoMoXConfiguration();
        afterConversion.applyAttributeMap(attributeMap);
        assertThat("The default configuration is changed on conversion!", afterConversion,
                behavesLike(defaultConfiguration));
    }

    /**
     * Asserts that setting values through setters or through an attribute map has the same effect
     * for various combinations of attributes.
     */
    @Test
    public void testEquivalenceOfSetterAndAttributeValue() {
        Set<String> allKeys = SETTERS.keySet();
        Set<Set<String>> attributeKeySetsToCheck = new HashSet<>();

        // all attributes
        attributeKeySetsToCheck.add(allKeys);
        // each single attribute
        for (String attributeKey : allKeys) {
            attributeKeySetsToCheck.add(new HashSet<>(Arrays.asList(attributeKey)));
        }
        // no attributes
        attributeKeySetsToCheck.add(new HashSet<>());

        // 10 random combinations
        int minCount = 2;
        String[] allKeysArray = allKeys.toArray(new String[allKeys.size()]);
        for (int i = 0; i < 10; i++) {
            int numberOfElements = Math.round((float) Math.random() * (allKeys.size() - minCount) + minCount);
            Set<String> nextSet = new HashSet<>(numberOfElements);
            for (int j = 0; j < numberOfElements; j++) {
                int nextElementIndex = Math.round((float) Math.random() * (allKeys.size() - 1));
                nextSet.add(allKeysArray[nextElementIndex]);
            }
            attributeKeySetsToCheck.add(nextSet);
        }

        // for each prepared set of attribut keys: get correct values from the suppliers and use them to set
        for (Set<String> keySet : attributeKeySetsToCheck) {
            SoMoXConfiguration settersConfiguration = new SoMoXConfiguration();
            Map<String, Object> attributeMap = new HashMap<>();

            for (String attributeKey : keySet) {
                Object attributeValue = VALUE_SUPPLIERS.get(attributeKey).get();
                attributeMap.put(attributeKey, attributeValue);
                SETTERS.get(attributeKey).accept(settersConfiguration, attributeValue);
            }

            SoMoXConfiguration mapGeneratedConfiguration = new SoMoXConfiguration(attributeMap);
            assertThat(
                    "The configuration generated by an attribute map doesn’t behave like the one generated with setters",
                    mapGeneratedConfiguration, behavesLike(settersConfiguration));
            
            mapGeneratedConfiguration = new SoMoXConfiguration();
            mapGeneratedConfiguration.applyAttributeMap(attributeMap);
            assertThat(
                    "The configuration generated by an attribute map doesn’t behave like the one generated with setters",
                    mapGeneratedConfiguration, behavesLike(settersConfiguration));
        }
    }

    /**
     * Creates a matcher to compare a {@link SoMoXConfiguration}.
     * 
     * @param configuration
     *            The reference configuration.
     * @return A matcher that will match if all return values of all getters of the reference
     *         configuration and the examined configuration are equal or behave equally.
     */
    private static Matcher<SoMoXConfiguration> behavesLike(final SoMoXConfiguration configuration) {
        return new TypeSafeDiagnosingMatcher<SoMoXConfiguration>() {
            /**
             * Getters that return values that need to be examined on their own. Note that this list
             * applies for any examination level.
             */
            private final Set<String> gettersToExamine = new HashSet<>(
                    Arrays.asList(new String[] { "getFileLocations", "getClusteringConfig", "getBlacklistFilter" }));

            @Override
            public void describeTo(Description description) {
                description.appendText("all methods should return the same values");
            }

            @Override
            protected boolean matchesSafely(SoMoXConfiguration item, Description mismatchDescription) {
                return matches(configuration, item, mismatchDescription, "");
            }

            protected boolean matches(Object reference, Object examined, Description mismatchDescription,
                    String getterPrefix) {
                // find all getters using reflection. This ensures that future modifications will be
                // included
                List<Method> getters = new ArrayList<>();
                for (Method method : reference.getClass().getMethods()) {
                    if (method.getName().startsWith("get")) {
                        getters.add(method);
                    }
                }

                // execute all getters on all objects and compare the results
                for (Method getter : getters) {
                    Object referenceValue;
                    Object examinedValue;

                    String getterName = getterPrefix + getter.getName() + "()";

                    try {
                        referenceValue = getter.invoke(reference);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        mismatchDescription.appendText("unable to invoke getter ").appendValue(getterName)
                                .appendText(" on the reference configuration\n\n").appendText(getStackString(e));
                        return false;
                    }

                    try {
                        examinedValue = getter.invoke(examined);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        mismatchDescription.appendText("unable to invoke getter ").appendValue(getterName)
                                .appendText(" on the examined configuration\n\n").appendText(getStackString(e));
                        return false;
                    }

                    if (gettersToExamine.contains(getter.getName())) {
                        if (!matches(referenceValue, examinedValue, mismatchDescription, getter.getName() + "().")) {
                            return false;
                        }
                    } else if (!(referenceValue == null ? examinedValue == null
                            : referenceValue.equals(examinedValue))) {
                        mismatchDescription.appendValue(getterName).appendText(" returned ").appendValue(examinedValue)
                                .appendText(" instead of ").appendValue(referenceValue);
                        return false;
                    }
                }

                return true;
            }

            /**
             * Get the stack trace of an Exception as a String.
             * 
             * @param e
             *            An exception
             */
            private String getStackString(Exception e) {
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                e.printStackTrace(new PrintStream(byteStream));
                return byteStream.toString();
            }
        };
    }

    /**
     * @return suppliers for all attributes
     */
    private static Map<String, Supplier<Object>> getValueSuppliers() {
        Map<String, Supplier<Object>> valueSuppliers = new HashMap<>();
        Supplier<Object> stringSupplier = () -> {
            return UUID.randomUUID().toString();
        };
        Supplier<Object> booleanSupplier = () -> {
            return Math.random() < 0.5;
        };
        Supplier<Object> double100Supplier = () -> {
            return Math.random() * 100;
        };
        Supplier<Object> double1Supplier = () -> {
            return Math.random();
        };
        valueSuppliers.put(SoMoXConfiguration.BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_ANALYZER_INPUT_FILE, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES,
                booleanSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_ANALYZER_WILDCARD_KEY, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_EXCLUDED_PREFIXES, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_EXCLUDED_SUFFIXES, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_OUTPUT_FOLDER, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_PROJECT_NAME, stringSupplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_DIRECTORY_MAPPING, double1Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_DMS, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_COUPLING, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_SLAQ, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_COUPLING, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_SLAQ, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_MID_NAME_RESEMBLANCE, double100Supplier);
        valueSuppliers.put(SoMoXConfiguration.SOMOX_WEIGHT_PACKAGE_MAPPING, double100Supplier);

        return valueSuppliers;
    }

    /**
     * @return a mapping from attribute keys to setters
     */
    private static Map<String, BiConsumer<SoMoXConfiguration, Object>> getKeyToSettersMapping() {
        Map<String, BiConsumer<SoMoXConfiguration, Object>> keysToSetters = new HashMap<>();

        keysToSetters.put(SoMoXConfiguration.BLACKLIST_CONFIGURATION_WILDCARDS_ADDITIONAL,
                (SoMoXConfiguration c, Object s) -> {
                    c.setAdditionalWildcards((String) s);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_ANALYZER_REVERSE_ENGINEER_INTERFACES_NOT_ASSIGNED_TO_INTERFACES,
                (SoMoXConfiguration c, Object b) -> {
                    c.setReverseEngineerInterfacesNotAssignedToComponent((Boolean) b);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_ANALYZER_INPUT_FILE, (SoMoXConfiguration c, Object s) -> {
            c.getFileLocations().setAnalyserInputFile((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_ANALYZER_WILDCARD_KEY, (SoMoXConfiguration c, Object s) -> {
            c.setWildcardKey((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_EXCLUDED_PREFIXES, (SoMoXConfiguration c, Object s) -> {
            c.setExcludedPrefixesForNameResemblance((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_EXCLUDED_SUFFIXES, (SoMoXConfiguration c, Object s) -> {
            c.setExcludedSuffixesForNameResemblance((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_OUTPUT_FOLDER, (SoMoXConfiguration c, Object s) -> {
            c.getFileLocations().setOutputFolder((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_PROJECT_NAME, (SoMoXConfiguration c, Object s) -> {
            c.getFileLocations().setProjectName((String) s);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_COMPOSE,
                (SoMoXConfiguration c, Object d) -> {
                    c.getClusteringConfig().setClusteringComposeThresholdDecrement((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_DECREMENT_MERGE,
                (SoMoXConfiguration c, Object d) -> {
                    c.getClusteringConfig().setClusteringMergeThresholdDecrement((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_COMPOSE,
                (SoMoXConfiguration c, Object d) -> {
                    c.getClusteringConfig().setMaxComposeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MAX_MERGE,
                (SoMoXConfiguration c, Object d) -> {
                    c.getClusteringConfig().setMaxMergeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_COMPOSE,
                (SoMoXConfiguration c, Object d) -> {
                    c.getClusteringConfig().setMinComposeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_CLUSTERING_THRESHOLD_MIN_MERGE,
                (SoMoXConfiguration c, Object d) -> {
                    c.getClusteringConfig().setMinMergeClusteringThreshold((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_DIRECTORY_MAPPING, (SoMoXConfiguration c, Object d) -> {
            c.setWeightDirectoryMapping((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_DMS, (SoMoXConfiguration c, Object d) -> {
            c.setWeightDMS((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_COUPLING, (SoMoXConfiguration c, Object d) -> {
            c.setWeightHighCoupling((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_NAME_RESEMBLANCE, (SoMoXConfiguration c, Object d) -> {
            c.setWeightHighNameResemblance((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGH_SLAQ, (SoMoXConfiguration c, Object d) -> {
            c.setWeightHighSLAQ((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_HIGHEST_NAME_RESEMBLANCE,
                (SoMoXConfiguration c, Object d) -> {
                    c.setWeightHighestNameResemblance((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_IRRELEVANT,
                (SoMoXConfiguration c, Object d) -> {
                    c.setWeightInterfaceViolationIrrelevant((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_INTERFACE_VIOLATION_RELEVANT,
                (SoMoXConfiguration c, Object d) -> {
                    c.setWeightInterfaceViolationRelevant((Double) d);
                });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_COUPLING, (SoMoXConfiguration c, Object d) -> {
            c.setWeightLowCoupling((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_NAME_RESEMBLANCE, (SoMoXConfiguration c, Object d) -> {
            c.setWeightLowNameResemblance((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_LOW_SLAQ, (SoMoXConfiguration c, Object d) -> {
            c.setWeightLowSLAQ((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_MID_NAME_RESEMBLANCE, (SoMoXConfiguration c, Object d) -> {
            c.setWeightMidNameResemblance((Double) d);
        });
        keysToSetters.put(SoMoXConfiguration.SOMOX_WEIGHT_PACKAGE_MAPPING, (SoMoXConfiguration c, Object d) -> {
            c.setWeightPackageMapping((Double) d);
        });

        return keysToSetters;
    }
}