package fr.uge.patchwork;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Representation of a patch with its id, shape, cost, time and income
 * 
 * @param id     Id of the patch (useful for graphic display)
 * @param shape  The shape of the patch represented by a 5x5 array of booleans
 * @param cost   The cost of the patch in buttons
 * @param time   The cost of the patch in time
 * @param income The income of the patch in buttons
 * @param width  Width of the patch
 * @param height Height of the patch
 * @param image Image of the patch
 * 
 * @author BERNIER Valentin
 * @author VILAYVANH Mickael
 */
public record Patch(int id, boolean[][] shape, int cost, int time, int income, int width, int height, BufferedImage image) {
  /**
   * Patch constructor : Create a new patch with the given arguments
   * 
   * @param id     Id of the patch (useful for graphic display)
   * @param shape  The shape of the patch represented by a 5x5 array of booleans
   * @param cost   The cost of the patch in buttons
   * @param time   The cost of the patch in time
   * @param income The income of the patch in buttons
   * @param width  Width of the patch
   * @param height Height of the patch
   * @param image Image of the patch
   */
  public Patch {
    if (id < 0)
      throw new IllegalArgumentException("id < 0");
    Objects.requireNonNull(shape, "shape is null");
    for (var element : shape)
      Objects.requireNonNull(element, "shape contains null");
    if (id < 0)
      throw new IllegalArgumentException("id < 0");
    if (cost < 0)
      throw new IllegalArgumentException("cost < 0");
    if (time < 0)
      throw new IllegalArgumentException("time < 0");
    if (income < 0)
      throw new IllegalArgumentException("income < 0");
    if (width <= 0 || width > 5)
      throw new IllegalArgumentException("width incorect");
    if (height <= 0 || height > 5)
      throw new IllegalArgumentException("height incorect");
  }

  /**
   * Static method that create a patch from a textual format in a string.
   * 
   * @param patchDescription The String that contains the patch description
   * @param gameMode game mode
   * @return The new created patch
   */
  public static Patch createPatch(String patchDescription, int gameMode) {
    Objects.requireNonNull(patchDescription, "patchDescription is null");
    // Cut the different parts
    var patchDescriptionSplit = patchDescription.split("/");
    var values = patchDescriptionSplit[0].split(";");
    var shapeText = patchDescriptionSplit[1].split("\n");
    // Initialize id, cost, time and income from the first line of the string
    int id = Integer.parseInt(values[0]);
    int cost = Integer.parseInt(values[1]);
    int time = Integer.parseInt(values[2]);
    int income = Integer.parseInt(values[3]);
    int width = Integer.parseInt(values[4]);
    int height = Integer.parseInt(values[5]);
    // Initialize the shape with the 5 next lines
    boolean[][] shape = new boolean[5][5];
    for (int i = 1; i < 6; i++) {
      for (int j = 0; j < 5; j++) {
        shape[i - 1][j] = (shapeText[i].charAt(j) == '1');
      }
    }
    if (gameMode == 3)
      return new Patch(id, shape, cost, time, income, width, height, UserInterfaceGraphic.loadImage("patches/" + id));
    else
      return new Patch(id, shape, cost, time, income, width, height, null);
  }

  /**
   * Return true if the patch cost more than 10 buttons
   * 
   * @return true if the patch cost more than 10 buttons
   */
  public boolean costMoreThan10() {
    return cost >= 10;
  }

  /**
   * A toString intermediate method that return the string with cost, time and
   * income
   * 
   * @return A String that contain patch cost, time and income
   */
  public String toStringData() {
    var builder = new StringBuilder();
    builder.append(cost).append(" ").append(time).append(" ").append(income);
    return builder.toString();
  }

  /**
   * A toString intermediate method that return the string with the line line of
   * the patch.
   * 
   * @param line      The line to transform into String.
   * @param separator a String that separates each non patch surface element.
   * 
   * @return A String that contain patch cost, time and income
   */
  public String toStringLine(int line, char separator) {
    if (line < 0 || line > 4)
      throw new IllegalArgumentException("line invalid");

    var builder = new StringBuilder();
    for (var square : shape[line]) {
      if (square == true)
        builder.append("#");
      else
        builder.append(separator);
    }
    if (costMoreThan10())
      builder.append(" ");
    return builder.toString();
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    builder.append(toStringData());
    for (int line = 0; line < height; line++) {
      builder.append("\n");
      builder.append(toStringLine(line, '.'));
    }
    return builder.toString();
  }

  /**
   * Search and return a list of coordinate of all empty area of the patch
   * 
   * @return A list of coordinate of all empty area of the patch
   */
  public ArrayList<Coordinate> locationsOfSpaceArea() {
    var spacesArea = new ArrayList<Coordinate>();
    int line, column;

    for (line = 0; line < 5; line++) {
      for (column = 0; column < 5; column++) {
        if (shape[line][column] == true) {
          spacesArea.add(new Coordinate(line, column));
        }
      }
    }
    return spacesArea;
  }

  /**
   * Rotate the patch image 90 degrees clockwise
   * 
   * @return The new rotated patch image
   */
  private BufferedImage rotatePatchImage() {
    int width = image.getWidth();
    int height = image.getHeight();
    var resImage = new BufferedImage(height, width, image.getType());
    var graphics = resImage.createGraphics();
    graphics.translate((height - width) / 2, (height - width) / 2);
    graphics.rotate(Math.toRadians(90), height / 2, width / 2);
    graphics.drawImage(image, 0, 0, null);
    return resImage;
  }
  
  /**
   * Rotate the patch 90 degrees clockwise
   * 
   * @return The new rotated patch
   */
  public Patch rotate() {
    boolean[][] newShape = new boolean[5][5];

    for (int i = 0; i < 5; i++) {
      for (int j = 0; j < 5; j++) {
        if (i < height && j < width)
          newShape[j][height - 1 - i] = shape[i][j];
      }
    }
    
    if (Objects.nonNull(image))
      return new Patch(id, newShape, cost, time, income, height, width, rotatePatchImage());

    return new Patch(id, newShape, cost, time, income, height, width, image);
  }
  
  /**
   * Display the patch
   * 
   * @param graphics Graphics2D object used to display
   * @param x X coordinates
   * @param y Y coordinates
   * @return The width of the patch in pixels
   */
  public int display(Graphics2D graphics, int x, int y) {
    Objects.requireNonNull(graphics);
    if (x < 0 || y < 0)
       throw new IllegalArgumentException("Invalid position x or y : " + x + " " + y);
    graphics.drawImage(image, x, y, null);
    return width * 32;
  }
}
