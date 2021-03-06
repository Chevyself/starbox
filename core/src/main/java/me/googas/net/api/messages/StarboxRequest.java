package me.googas.net.api.messages;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.net.sockets.json.JsonReceptor;

/**
 * This object represents a request which is a message that makes the client which sent it wait for
 * a {@link Response}.
 */
public class StarboxRequest<T> implements Request {

  /** The class that is being requested . */
  @NonNull @Getter private final transient Class<T> clazz;

  /** The id of the request. */
  @NonNull @Getter private final UUID id;

  /** The method which must match a {@link JsonReceptor} to give a response. */
  @NonNull @Getter private final String method;

  /** The parameters that the {@link JsonReceptor} requires to give a response. */
  @NonNull @Getter private final Map<String, ?> parameters;

  /**
   * Create the request.
   *
   * @param clazz the class that is being requested
   * @param id the id of the request
   * @param method the method to get the receptor
   * @param parameters the parameters to execute in the receptor
   */
  public StarboxRequest(
      @NonNull Class<T> clazz,
      @NonNull UUID id,
      @NonNull String method,
      @NonNull Map<String, ?> parameters) {
    this.clazz = clazz;
    this.id = id;
    this.method = method;
    this.parameters = parameters;
  }

  /**
   * Create the request.
   *
   * @param clazz the class that is being requested
   * @param method the method of the request
   * @param parameters the method to get the receptor
   */
  public StarboxRequest(
      @NonNull Class<T> clazz, @NonNull String method, @NonNull Map<String, ?> parameters) {
    this(clazz, UUID.randomUUID(), method, parameters);
  }

  /**
   * Create the request.
   *
   * @param clazz the class that is being requested
   * @param method the id of the request
   */
  public StarboxRequest(@NonNull Class<T> clazz, @NonNull String method) {
    this(clazz, UUID.randomUUID(), method, new HashMap<>());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", StarboxRequest.class.getSimpleName() + "[", "]")
        .add("clazz=" + clazz)
        .add("id=" + id)
        .add("method='" + method + "'")
        .add("parameters=" + parameters)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    StarboxRequest<?> request = (StarboxRequest<?>) o;
    return id.equals(request.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
