package org.tsdl.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.tsdl.infrastructure.api.StorageProperty;

class StoragePropertyTest {
  @Test
  void fromIdentifier_nullIdentifier_throws() {
    assertThatThrownBy(() -> StorageProperty.fromIdentifier(null, TestConfigurationProperty.class))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void fromIdentifier_nullClass_throws() {
    assertThatThrownBy(() -> StorageProperty.fromIdentifier("irrelevant", null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void fromIdentifier_nonStoragePropertyClass_throws() {
    assertThatThrownBy(() -> StorageProperty.fromIdentifier("irrelevant", NoStorageProperty.class))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void fromIdentifier_nonExistingIdentifier_throws() {
    assertThatThrownBy(() -> StorageProperty.fromIdentifier("does not exist", TestConfigurationProperty.class))
        .isInstanceOf(NoSuchElementException.class);
  }

  @Test
  void fromIdentifier_existingIdentifier_looksUpCorrectEnumMember() {
    assertThat(StorageProperty.fromIdentifier("prop2", TestConfigurationProperty.class))
        .isEqualTo(TestConfigurationProperty.PROP2);
  }

  enum NoStorageProperty {
    ITEM;

    public String identifier() {
      return "test";
    }
  }

  enum TestConfigurationProperty implements StorageProperty {
    PROP1("prop1", char[].class), PROP2("prop2", String.class), PROP3("prop3", Number.class);

    private final String identifier;
    private final Class<?> type;

    TestConfigurationProperty(String identifier, Class<?> type) {
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
}