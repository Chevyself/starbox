package me.googas.reflect.wrappers;

import java.lang.reflect.Constructor;
import lombok.NonNull;
import me.googas.starbox.expressions.HandledExpression;

/** This class wraps a {@link Constructor} to prepare it and create instances of a class. */
public class WrappedConstructor<T> extends LangWrapper<Constructor<T>> {

  private WrappedConstructor(Constructor<T> reference) {
    super(reference);
  }

  private WrappedConstructor() {
    this(null);
  }

  /**
   * Wrap a {@link Constructor} instance.
   *
   * @param constructor the constructor to wrap
   * @return the wrapper of constructor
   * @param <T> the type that the constructor initializes
   */
  @NonNull
  public static <T> WrappedConstructor<T> of(Constructor<T> constructor) {
    if (constructor != null) constructor.setAccessible(true);
    return new WrappedConstructor<>(constructor);
  }

  /**
   * Invoke the constructor to create a new instance of an object.
   *
   * @param args the required arguments to prepare the constructor
   * @return a {@link HandledExpression} that returns the invoked object or handles the exception in
   *     case the constructor could not be invoked
   */
  @NonNull
  public HandledExpression<T> invoke(Object... args) {
    return HandledExpression.using(
        () -> {
          T other = null;
          if (this.reference != null) {
            other = this.reference.newInstance(args);
          }
          return other;
        });
  }

  /**
   * Get the wrapped constructor.
   *
   * @return the wrapped constructor
   */
  public Constructor<T> getConstructor() {
    return reference;
  }
}
