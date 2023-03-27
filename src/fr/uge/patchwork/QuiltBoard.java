package fr.uge.patchwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.lang.StringBuilder;

/**
 * Representation of a Quiltboard, used by a player of the PatchWork to store
 * all the patches it is collecting
 * 
 * @author VILAYVANH Mickael
 * @author BERNIER Valentin
 * 
 */
public class QuiltBoard {
  /**
   * An LinkedHashMap that represents the grid that each player have in
   * possession.
   */
  private final LinkedHashMap<Coordinate, Boolean> grid;
  /**
   * An HashMap that represents the position of the topleft corner of a specific
   * patch
   */
  private final HashMap<Patch, Coordinate> patchesPosition;

  /**
   * QuiltBoard constructor: Create an initialized quiltboard.
   */
  public QuiltBoard() {
    grid = initGrid();
    patchesPosition = new HashMap<>();
  }

  /**
   * Initialize grid by assigning false to the value of each element.
   * 
   * @return An LinkedHashMap that represents a quiltboard at initialiazation
   *         state.
   */
  private static LinkedHashMap<Coordinate, Boolean> initGrid() {
    var result = new LinkedHashMap<Coordinate, Boolean>();
    int i, j;
    for (i = 0; i < 9; i++) {
      for (j = 0; j < 9; j++) {
        result.put(new Coordinate(i, j), false);
      }
    }
    return result;
  }

  /**
   * Do the sum of each patch's income.
   * 
   * @return An {int} that represents the total income.
   */
  public int getIncome() {
    return patchesPosition.keySet().stream().mapToInt(Patch::income).sum();
  }

  /**
   * Give the number of empty space.
   * 
   * @return An int that represents the total of empty space in the board.
   */
  public int numberOfEmptySpace() {
    return grid.values().stream().filter(value -> value.booleanValue() == false).collect(Collectors.toList()).size();
  }

  /**
   * Check if the patch given in parameter is allowed to be placed.
   * 
   * @param patch   Patch to be placed.
   * @param padding A coordinate used as adding element to each space that
   *                represents the patch.
   */
  private ArrayList<Coordinate> getAreaIfValidPlace(Patch patch, Coordinate padding) {
    Objects.requireNonNull(patch);
    Objects.requireNonNull(padding);

    var patchArea = patch.locationsOfSpaceArea();
    for (var area : patchArea) {
      // reajusting patch shape to the correct area aimed
      area = area.add(padding);
      var value = grid.getOrDefault(area, null);
      // if value is null then out of grid area
      // if value is true then there is already a space allocated
      if (value == null || value == true) {
        return null;
      }
    }
    return patchArea;
  }

  /**
   * Update the grid as the patch is added to it.
   * 
   * @param area    Non-empty area of the patch.
   * @param padding A coordinate used as adding element to each space that
   *                represents the patch.
   * 
   */
  private void updateGrid(ArrayList<Coordinate> area, Coordinate padding) {
    Objects.requireNonNull(area);
    Objects.requireNonNull(padding);

    for (var space : area) {
      // Flipping empty area to filled area
      var newSpace = space.add(padding);
      grid.put(newSpace, true);
    }
  }

  /**
   * Place a patch given in the quiltboard at the given positions. Current
   * positionX and positionY is subject to change in the near future. The position
   * aimed is the top left corner of the patch.
   * 
   * @param patch   Patch to be placed.
   * @param padding Coordinate refered in the board.
   * 
   * @return true if it could be placed.
   * 
   */
  public boolean placePatch(Patch patch, Coordinate padding) {
    Objects.requireNonNull(patch);
    Objects.requireNonNull(padding);
    
    var spacesArea = getAreaIfValidPlace(patch, padding);
    if (spacesArea != null) {
      patchesPosition.putIfAbsent(patch, padding);
      updateGrid(spacesArea, padding);
      return true;
    }
    return false;
  }

  /**
   * Returns true if the very center of the board is at least a filled space.
   * 
   * @return true if the very center of the board is at least a filled space.
   */
  private boolean CenterGridFilled() {
    return grid.get(new Coordinate(4, 4));
  }

  /**
   * Returns true if there is enough filled space.
   * 
   * @return true if there is enough filled space.
   */
  private boolean enoughFilledSpace() {
    return 81 - numberOfEmptySpace() >= 49;
  }

  /**
   * Returns true if the quiltboard has the specificities to start a full check
   * since it is an expensive operation.
   * 
   * @return true if the quiltboard has the specificities to start a full check
   */
  private boolean needToBeChecked() {
    return enoughFilledSpace() && CenterGridFilled();
  }

  /**
   * Checks if the current 7x7 square is full of filled spaces.
   * 
   * @param startingLine   the starting line of the square
   * @param startingColumn the starting column of the square
   * 
   * @return true if the square is full of true values and false otherwise.
   */
  private boolean isACorrectSquare(int startingLine, int startingColumn) {
    var lineLimit = startingLine + 7;
    var columnLimit = startingLine + 7;
    for (var line = startingLine; line < lineLimit; line++) {
      for (var column = startingColumn; column < columnLimit; column++) {
        if (grid.get(new Coordinate(line, column)) == false)
          return false;
      }
    }
    return true;
  }

  /**
   * Computes the checking on squares among all 9 possibilities of 7x7.
   * 
   * @return true if a correct 7x7 square is found.
   */
  private boolean computeChecking() {
    for (var firstLine = 0; firstLine < 3; firstLine++) {
      for (var firstColumn = 0; firstColumn < 3; firstColumn++) {
        if (isACorrectSquare(firstLine, firstColumn))
          return true;
      }
    }
    return false;
  }

  /**
   * Checks if the current quiltboard has a 7x7 area.
   * 
   * @return false if no checking is requiered or {@code computeChecking()} return
   *         value otherwise.
   */
  public boolean check7x7Area() {
    // not necessary to check if there isnt enough filled spaces.
    if (needToBeChecked()) {
      return computeChecking();
    }
    return false;
  }

  /**
   * Print the horizontal axis indexes of the quiltboard.
   * 
   * @param size Maximum size of the quiltboard.
   */
  private String printIndexOfSize(int size) {
    var builder = new StringBuilder();
    builder.append("    ");
    for (var index = 0; index < size; index++) {
      builder.append(index).append(' ');
    }
    builder.append('\n');
    return builder.toString();
  }

  // toString to correct
  // Possibily not respecting poo's programmation norm
  // To shorten later
  @Override
  public String toString() {
    var builder = new StringBuilder();
    int size = 9;
    int index = 0;
    builder.append(printIndexOfSize(size));
    for (var location : grid.entrySet()) {
      if (location.getKey().isStartOfLine()) {
        builder.append(index).append(" | ");
        index++;
      }
      if (location.getValue() == true) {
        builder.append("# ");
      } else {
        builder.append(". ");
      }
      if (location.getKey().isBorder() && !location.getKey().isLastLine()) {
        builder.append('\n');
      }
    }
    builder.append("\nIncome : ").append(getIncome());
    return builder.toString();
  }
  
  /**
   * Display the quilt board
   * 
   * @param graphics Graphics2D object used to display
   */
  public void display(Graphics2D graphics) {
    Objects.requireNonNull(graphics);
    for (var i = 0; i < 9; i++) {
      for (var j = 0; j < 9; j++) {
        graphics.setColor(Color.WHITE);
        graphics.fill(new Rectangle2D.Float(1024 + 32 * i, 170 + 32 * j, 32, 32));
        graphics.setColor(Color.BLACK);
        graphics.draw(new Rectangle2D.Float(1024 + 32 * i, 170 + 32 * j, 32, 32));
      }
    }
    Coordinate coord;
    for (var patches: patchesPosition.entrySet()) {
      coord = patches.getValue();
      patches.getKey().display(graphics, 1024 + 32 * coord.column(), 170 + 32 * coord.line());
    }
  }
  
  /**
   * Return the Coordinates in the grid of the quilt board
   * that correspond to the mouse coordinates
   * 
   * @param x X position
   * @param y Y position
   * @return Coordinates in the grid of the quilt board or
   *    - (-1, -1) if rotate button
   *    - (-2, -2) if throw button
   *    - (-3, -3) if not in the grid
   */
  public Coordinate coordToCoordinate(double x, double y) {
    if (x >= 1024 && x <= 1312 && y >= 170 && y <= 458)
      return new Coordinate(((int)y - 170) / 32, ((int)x - 1024) / 32);
    
    if (x >= 1664 && x <= 1854 && y >= 264 && y <= 304)
      return new Coordinate(-1, -1);
    
    if (x >= 1664 && x <= 1854 && y >= 314 && y <= 354)
      return new Coordinate(-2, -2);
    
    return new Coordinate(-3, -3);
  }
  
  /**
   * Display the quilt board
   * 
   * @param graphics Graphics2D object used to display
   * @param xToAdd The number of pixels to add to the display
   */
  public void displayMini(Graphics2D graphics, int xToAdd) {
    Objects.requireNonNull(graphics);
    Coordinate coord;
    for (var space: grid.entrySet()) {
      coord = space.getKey();
      if (space.getValue())
        graphics.setColor(Color.BLACK);
      else
        graphics.setColor(Color.WHITE);
      graphics.fill(new Rectangle2D.Float(xToAdd + 200 + 16 * coord.column(), 925 + 16 * coord.line(), 16, 16));
      graphics.setColor(Color.BLACK);
      graphics.draw(new Rectangle2D.Float(xToAdd + 200 + 16 * coord.column(), 925 + 16 * coord.line(), 16, 16));
    }
  }
}