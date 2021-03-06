package me.googas.starbox.math.geometry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import lombok.NonNull;
import lombok.Setter;
import me.googas.starbox.math.MathUtils;

/** A box. */
public class Box implements Shape {

  private final String id;
  /** The minimum position of the cube. */
  @NonNull @Setter private Point minimum;
  /** The maximum position of the cube. */
  @NonNull @Setter private Point maximum;

  /**
   * Create the box.
   *
   * @param minimum the minimum position of the box
   * @param maximum the maximum position of the box
   * @param id the id of the cube
   */
  public Box(@NonNull Point minimum, @NonNull Point maximum, String id) {
    this.minimum =
        new Point(
            Math.min(minimum.getX(), maximum.getX()),
            Math.min(minimum.getY(), maximum.getY()),
            Math.min(minimum.getZ(), maximum.getZ()));
    this.maximum =
        new Point(
            Math.max(minimum.getX(), maximum.getX()),
            Math.max(minimum.getY(), maximum.getY()),
            Math.max(minimum.getZ(), maximum.getZ()));
    this.id = id;
  }

  /**
   * Get the height of the box.
   *
   * @return the height
   */
  public double getHeight() {
    return this.getMaximum().getY() - this.getMinimum().getY();
  }

  /**
   * Get the width of the box.
   *
   * @return the width
   */
  public double getWidth() {
    return this.getMaximum().getX() - this.getMinimum().getX();
  }

  /**
   * Get the length of the box.
   *
   * @return the length
   */
  public double getLength() {
    return this.getMaximum().getZ() - this.getMinimum().getZ();
  }

  @NonNull
  @Override
  public Point getMinimum() {
    return this.minimum;
  }

  @NonNull
  @Override
  public Point getMaximum() {
    return this.maximum;
  }

  @Override
  public double getVolume() {
    return this.getWidth() * this.getLength() * this.getHeight();
  }

  public Optional<String> getId() {
    return Optional.ofNullable(this.id);
  }

  @NonNull
  @Override
  public Points getPointsInside() {
    Set<Point> points = new HashSet<>();
    for (double x = this.getMinimum().getX(); x < this.getMaximum().getX(); x++) {
      for (double z = this.getMinimum().getZ(); z < this.getMaximum().getZ(); z++) {
        for (double y = this.getMinimum().getY(); y < this.getMaximum().getY(); y++) {
          points.add(new Point(x, y, z));
        }
      }
    }
    return new Points(points);
  }

  @Override
  public @NonNull Point getRandomPoint(@NonNull Random random) {
    return new Point(
        MathUtils.nextDoubleFloor(this.minimum.getX(), this.maximum.getX()),
        MathUtils.nextDoubleFloor(this.minimum.getY(), this.maximum.getY()),
        MathUtils.nextDoubleFloor(this.minimum.getZ(), this.maximum.getZ()));
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Box.class.getSimpleName() + "[", "]")
        .add("id='" + id + "'")
        .add("minimum=" + minimum)
        .add("maximum=" + maximum)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    Box box = (Box) o;
    return id.equals(box.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean contains(@NonNull Point point) {
    return point.getX() >= this.minimum.getX()
        && point.getX() <= this.maximum.getX()
        && point.getY() >= this.minimum.getY()
        && point.getY() <= this.maximum.getY()
        && point.getZ() >= this.minimum.getZ()
        && point.getZ() <= this.maximum.getZ();
  }
}
