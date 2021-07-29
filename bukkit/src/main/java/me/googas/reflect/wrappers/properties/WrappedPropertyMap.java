package me.googas.reflect.wrappers.properties;

import lombok.NonNull;
import me.googas.reflect.StarboxWrapper;
import me.googas.reflect.wrappers.WrappedClass;
import me.googas.reflect.wrappers.WrappedConstructor;
import me.googas.reflect.wrappers.WrappedMethod;

public class WrappedPropertyMap extends StarboxWrapper {

  @NonNull
  private static final WrappedClass PROPERTY_MAP =
      WrappedClass.forName("com.mojang.authlib.properties.PropertyMap");

  @NonNull
  private static final WrappedConstructor CONSTRUCTOR =
      WrappedPropertyMap.PROPERTY_MAP.getConstructor();

  @NonNull
  private static final WrappedMethod PUT =
      WrappedPropertyMap.PROPERTY_MAP.getMethod("put", Object.class, Object.class);

  public WrappedPropertyMap(@NonNull Object object) {
    super(object);
    if (!object.getClass().equals(WrappedPropertyMap.PROPERTY_MAP.getClazz())) {
      throw new IllegalArgumentException("Expected a PropertyMap received " + object);
    }
  }

  @NonNull
  public static WrappedPropertyMap construct() {
    return new WrappedPropertyMap(WrappedPropertyMap.CONSTRUCTOR.invoke());
  }

  public boolean put(@NonNull String key, @NonNull WrappedProperty value) {
    Object invoke = WrappedPropertyMap.PUT.invoke(this.get(), key, value.get());
    if (invoke instanceof Boolean) {
      return (boolean) invoke;
    } else {
      return false;
    }
  }
}
