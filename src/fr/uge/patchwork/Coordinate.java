package fr.uge.patchwork;

import java.util.Objects;

/**
 * Representation of a coordinate. Used to make QuiltBoard's field easier to
 * understand and use.
 * 
 * Two int values have to be specified when created. These two cannot be absent
 * at declaration.
 * 
 * @param line   represents which line this coordinate is refering to.
 * @param column represents which column this coordinate is refering to.
 * 
 * @author VILAYVANH Mickael
 * @author BERNIER Valentin
 */
public record Coordinate(int line, int column) {

  /**
   * Canonical contructor of Coordinate.
   */
  public Coordinate {
    Objects.requireNonNull(line);
    Objects.requireNonNull(column);
  }

  /**
   * Returns if the coordinate is on the starting line of the board.
   * 
   * @return true returns if the coordinate is on the starting line of the board.
   */
  public boolean isStartOfLine() {
    return column == 0 && line <= 8;
  }

  /**
   * Returns if the coordinate is near the border of the board.
   * 
   * @return true returns if the coordinate is near the border of the board.
   */
  public boolean isBorder() {
    return column == 8 || line == 8;
  }

  /**
   * Returns if the coordinate is on the last line of the board.
   * 
   * @return boolean returns if the coordinate is on the last line of the board.
   */
  public boolean isLastLine() {
    return column <= 8 && line == 8;
  }

  /**
   * Adds line and column field of another Coordinate to respective fields.
   * 
   * @param other represents another Coordinate object.
   * @return a Coordinate that is the addition of current and specified
   *         Coordinate.
   */
  public Coordinate add(Coordinate other) {
    Objects.requireNonNull(other);

    return new Coordinate(line + other.line, column + other.column);
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Coordinate location && location.line == line && location.column == column;
  }

  @Override
  public String toString() {
    return "(" + line + ", " + column + ")";
  }
}