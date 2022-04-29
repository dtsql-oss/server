package org.tsdl.storage;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class AbstractStorageConfigurationTest {

    @Test
    void defaultConstructor_noPropertyInitialized() {
        var config = new SimpleStorageConfiguration();

        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP1)).isNull();
        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP2)).isNull();
        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP3)).isNull();
    }

    @Test
    void parameterizedConstructor_parametersArePreserved() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP2, "test",
          SimpleStorageConfigurationProperty.PROP3, 3
        ));

        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP1)).isEqualTo(new char[]{'t', 'e', 's', 't'});
        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP2)).isEqualTo("test");
        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP3)).isEqualTo(3);
    }

    @Test
    void getProperty_uninitializedPropertyWithoutFlag_doesNotThrow() {
        var config = new SimpleStorageConfiguration(Map.of());
        assertThatCode(() -> config.getProperty(SimpleStorageConfigurationProperty.PROP1)).doesNotThrowAnyException();
        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP1)).isNull();
    }

    @Test
    void getProperty_validTargetType_works() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP2, "test",
          SimpleStorageConfigurationProperty.PROP3, 3
        ));
        Object prop = config.getProperty(SimpleStorageConfigurationProperty.PROP1, char[].class);
        assertThat(prop.getClass()).isEqualTo(char[].class);
    }

    @Test
    void getProperty_incompatibleTargetType_throws() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP2, "test",
          SimpleStorageConfigurationProperty.PROP3, 3
        ));
        assertThatThrownBy(() -> config.getProperty(SimpleStorageConfigurationProperty.PROP1, String.class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void setProperty_previouslyUnset_updatesProperty() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP3, 3
        ));

        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP2)).isNull();
        var oldValue = config.setProperty(SimpleStorageConfigurationProperty.PROP2, "test");
        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP2)).isEqualTo("test");
        assertThat(oldValue).isNull();
    }

    @Test
    void setProperty_invalidType_throws() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP3, 3
        ));

        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP2)).isNull();
        assertThatThrownBy(() -> config.setProperty(SimpleStorageConfigurationProperty.PROP2, 3)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getSupportedProperties() {
        assertThat(new SimpleStorageConfiguration().getSupportedProperties()).isEqualTo(
          Map.of(SimpleStorageConfigurationProperty.PROP1, char[].class,
            SimpleStorageConfigurationProperty.PROP2, String.class,
            SimpleStorageConfigurationProperty.PROP3, Number.class)
        );
    }

    @Test
    void isPropertySet() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP3, 3
        ));
        assertThat(config.isPropertySet(SimpleStorageConfigurationProperty.PROP1)).isTrue();
        assertThat(config.isPropertySet(SimpleStorageConfigurationProperty.PROP2)).isFalse();
        assertThat(config.isPropertySet(SimpleStorageConfigurationProperty.PROP3)).isTrue();
    }

    @Test
    void getSetProperties_noSetProperty_returnsEmptyMap() {
        var config = new SimpleStorageConfiguration(Map.of());
        assertThat(config.getSetProperties()).usingRecursiveComparison().isEqualTo(new HashMap<>());
    }

    @Test
    void getSetProperties_somePropertiesSet_returnsMapWithSetProperties() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP3, 3
        ));
        assertThat(config.getSetProperties()).usingRecursiveComparison().isEqualTo(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP3, 3
        ));
    }

    @Test
    void unsetProperty() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP3, 3
        ));

        var oldValue = config.unsetProperty(SimpleStorageConfigurationProperty.PROP3);
        assertThat(config.getProperty(SimpleStorageConfigurationProperty.PROP3)).isNull();
        assertThat(oldValue).isEqualTo(3);
    }

    @Test
    void getSetProperties_unsetPropertyAfterInit_isNotInReturnValue() {
        var config = new SimpleStorageConfiguration(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray(),
          SimpleStorageConfigurationProperty.PROP3, 3
        ));

        config.unsetProperty(SimpleStorageConfigurationProperty.PROP3);

        assertThat(config.getSetProperties()).usingRecursiveComparison().isEqualTo(Map.of(
          SimpleStorageConfigurationProperty.PROP1, "test".toCharArray()
        ));
    }
}

class SimpleStorageConfiguration extends AbstractStorageConfiguration<SimpleStorageConfigurationProperty> {
    private static final Map<SimpleStorageConfigurationProperty, Class<?>> PROPERTY_TYPES = Map.of(
      SimpleStorageConfigurationProperty.PROP1, char[].class,
      SimpleStorageConfigurationProperty.PROP2, String.class,
      SimpleStorageConfigurationProperty.PROP3, Number.class
    );

    public SimpleStorageConfiguration() {
    }

    public SimpleStorageConfiguration(Map<SimpleStorageConfigurationProperty, Object> properties) {
        super(properties);
    }

    @Override
    protected Map<SimpleStorageConfigurationProperty, Class<?>> getPropertyTypes() {
        return PROPERTY_TYPES;
    }

    @Override
    protected Class<SimpleStorageConfigurationProperty> getPropertiesEnumClass() {
        return SimpleStorageConfigurationProperty.class;
    }
}

enum SimpleStorageConfigurationProperty {
    PROP1, PROP2, PROP3
}