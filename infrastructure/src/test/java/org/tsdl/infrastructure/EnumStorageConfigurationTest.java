package org.tsdl.infrastructure;

import org.junit.jupiter.api.Test;
import org.tsdl.infrastructure.api.EnumStorageConfiguration;
import org.tsdl.infrastructure.api.StorageProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class EnumStorageConfigurationTest {

    @Test
    void simpleConfiguration_defaultConstructor_noPropertyInitialized() {
        var config = new SimpleStorageConfiguration();

        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP1)).isNull();
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP2)).isNull();
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP3)).isNull();
    }

    @Test
    void simpleConfiguration_parameterizedConstructor_parametersArePreserved() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP2, "test",
          TestStorageConfigurationProperty1.PROP3, 3
        ));

        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP1)).isEqualTo(new char[]{'t', 'e', 's', 't'});
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP2)).isEqualTo("test");
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP3)).isEqualTo(3);
    }

    @Test
    void simpleConfiguration_getProperty_uninitializedPropertyWithoutFlag_doesNotThrow() {
        var config = new SimpleStorageConfiguration(Map.of());
        assertThatCode(() -> config.getProperty(TestStorageConfigurationProperty1.PROP1)).doesNotThrowAnyException();
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP1)).isNull();
    }

    @Test
    void simpleConfiguration_getProperty_validTargetType_works() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP2, "test",
          TestStorageConfigurationProperty1.PROP3, 3
        ));
        Object prop = config.getProperty(TestStorageConfigurationProperty1.PROP1, char[].class);
        assertThat(prop.getClass()).isEqualTo(char[].class);
    }

    @Test
    void simpleConfiguration_getProperty_incompatibleTargetType_throws() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP2, "test",
          TestStorageConfigurationProperty1.PROP3, 3
        ));
        assertThatThrownBy(() -> config.getProperty(TestStorageConfigurationProperty1.PROP1, String.class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void simpleConfiguration_setProperty_previouslyUnset_updatesProperty() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP3, 3
        ));

        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP2)).isNull();
        var oldValue = config.setProperty(TestStorageConfigurationProperty1.PROP2, "test");
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP2)).isEqualTo("test");
        assertThat(oldValue).isNull();
    }

    @Test
    void simpleConfiguration_setProperty_invalidType_throws() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP3, 3
        ));

        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP2)).isNull();
        assertThatThrownBy(() -> config.setProperty(TestStorageConfigurationProperty1.PROP2, 3)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void simpleConfiguration_getSupportedProperties() {
        assertThat(new SimpleStorageConfiguration().getSupportedProperties()).isEqualTo(
          List.of(TestStorageConfigurationProperty1.PROP1,
            TestStorageConfigurationProperty1.PROP2,
            TestStorageConfigurationProperty1.PROP3)
        );
    }

    @Test
    void simpleConfiguration_isPropertySet() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP3, 3
        ));
        assertThat(config.isPropertySet(TestStorageConfigurationProperty1.PROP1)).isTrue();
        assertThat(config.isPropertySet(TestStorageConfigurationProperty1.PROP2)).isFalse();
        assertThat(config.isPropertySet(TestStorageConfigurationProperty1.PROP3)).isTrue();
    }

    @Test
    void simpleConfiguration_getSetProperties_noSetProperty_returnsEmptyMap() {
        var config = new SimpleStorageConfiguration(Map.of());
        assertThat(config.getSetProperties()).usingRecursiveComparison().isEqualTo(new HashMap<>());
    }

    @Test
    void simpleConfiguration_getSetProperties_somePropertiesSet_returnsMapWithSetProperties() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP3, 3
        ));
        assertThat(config.getSetProperties()).usingRecursiveComparison().isEqualTo(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP3, 3
        ));
    }

    @Test
    void simpleConfiguration_unsetProperty() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP3, 3
        ));

        var oldValue = config.unsetProperty(TestStorageConfigurationProperty1.PROP3);
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP3)).isNull();
        assertThat(oldValue).isEqualTo(3);
    }

    @Test
    void simpleConfiguration_getSetProperties_unsetPropertyAfterInit_isNotInReturnValue() {
        var config = new SimpleStorageConfiguration(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray(),
          TestStorageConfigurationProperty1.PROP3, 3
        ));

        config.unsetProperty(TestStorageConfigurationProperty1.PROP3);

        assertThat(config.getSetProperties()).usingRecursiveComparison().isEqualTo(Map.of(
          TestStorageConfigurationProperty1.PROP1, "test".toCharArray()
        ));
    }

    @Test
    void combinedConfiguration_getSupportedProperties_returnsAllProperties() {
        assertThat(new CombinedStorageConfiguration().getSupportedProperties())
          .isEqualTo(List.of(
            TestStorageConfigurationProperty1.PROP1, TestStorageConfigurationProperty1.PROP2,
            TestStorageConfigurationProperty2.PROP3, TestStorageConfigurationProperty2.PROP2
          ));
    }

    @Test
    void combinedConfiguration_differentiatesPropertiesWithSameIdentifier() {
        var config = new CombinedStorageConfiguration();
        config.setProperty(TestStorageConfigurationProperty1.PROP2, "test");
        assertThat(config.getProperty(TestStorageConfigurationProperty1.PROP2)).isEqualTo("test");
        assertThat(config.isPropertySet(TestStorageConfigurationProperty2.PROP2)).isFalse();
    }

    @Test
    void combinedConfiguration_setUnknownProperty_throws() {
        var config = new CombinedStorageConfiguration();
        assertThatThrownBy(() -> config.setProperty(TestStorageConfigurationProperty1.PROP3, 23))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void combinedConfiguration_unsetUnknownProperty_throws() {
        var config = new CombinedStorageConfiguration();
        assertThatThrownBy(() -> config.setProperty(TestStorageConfigurationProperty1.PROP3, 23))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void combinedConfiguration_getUnknownProperty_throws() {
        var config = new CombinedStorageConfiguration();
        assertThatThrownBy(() -> config.getProperty(TestStorageConfigurationProperty1.PROP3))
          .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> config.getProperty(TestStorageConfigurationProperty1.PROP3, Number.class))
          .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void combinedConfiguration_isPropertySetUnknownProperty_throws() {
        var config = new CombinedStorageConfiguration();
        assertThatThrownBy(() -> config.isPropertySet(TestStorageConfigurationProperty1.PROP3))
          .isInstanceOf(IllegalArgumentException.class);
    }

}

enum TestStorageConfigurationProperty1 implements StorageProperty {
    PROP1("prop1", char[].class), PROP2("prop2", String.class), PROP3("prop3", Number.class);

    private final String identifier;
    private final Class<?> type;

    TestStorageConfigurationProperty1(String identifier, Class<?> type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    public Class<?> type() {
        return type;
    }
}

enum TestStorageConfigurationProperty2 implements StorageProperty {
    PROP3("prop3", Double.class), PROP2("prop2", Integer.class);

    private final String identifier;
    private final Class<?> type;

    TestStorageConfigurationProperty2(String identifier, Class<?> type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public String identifier() {
        return this.identifier;
    }

    public Class<?> type() {
        return type;
    }
}

class SimpleStorageConfiguration extends EnumStorageConfiguration {
    public SimpleStorageConfiguration(Map<StorageProperty, Object> properties) {
        super(properties);
    }

    public SimpleStorageConfiguration() {
        super();
    }

    @Override
    public List<StorageProperty> getSupportedProperties() {
        return List.of(TestStorageConfigurationProperty1.values());
    }

}

class CombinedStorageConfiguration extends EnumStorageConfiguration {
    public CombinedStorageConfiguration(Map<StorageProperty, Object> properties) {
        super(properties);
    }

    public CombinedStorageConfiguration() {
        super();
    }

    @Override
    public List<StorageProperty> getSupportedProperties() {
        return List.of(
          TestStorageConfigurationProperty1.PROP1, TestStorageConfigurationProperty1.PROP2,
          TestStorageConfigurationProperty2.PROP3, TestStorageConfigurationProperty2.PROP2
        );
    }

}