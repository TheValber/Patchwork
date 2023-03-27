package fr.uge.patchwork;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

/**
 * Representation of the TimeBoard. Choosing HashMap as position collection so
 * that getting or removing an element is faster.
 * 
 * @author VILAYVANH Mickael
 * @author BERNIER Valentin
 */
public class TimeBoard {
  /**
   * An int that represents the size of the board.
   */
  private final int size;
  /**
   * An HashMap that represents the position of all buttons giving incomes to
   * players.
   */
  private final HashMap<Integer, Integer> buttonsPosition;
  /**
   * An HashMap that represents the position of all special patches being 1x1
   * patch.
   */
  private final HashMap<Integer, Integer> specialPatchesPosition;
  /**
   * An int that represents the number of special patches and buttons giving
   * incomes
   */
  private final int nbIncome;
  /**
   * An int that represents the number of special patches and buttons giving
   * incomes
   */
  private final int nbSpecialPatches;

  /**
   * TimeBoard constructor: Create a new timeboard of size given in parameter. All
   * special patches and buttons position are placed randomly on the board.
   * 
   * @param size     The Time Board size
   * @param gameMode The Game mode : 1 - Basic Game Mode 2 - Complete Game Mode
   */
  public TimeBoard(int size, int gameMode) {
    if (size < 0)
      throw new IllegalArgumentException("size invalid");

    this.size = size;
    nbIncome = size / 6;
    nbSpecialPatches = (gameMode == 1) ? 0 : (size / 10);
    this.buttonsPosition = generateRandomPosition(size, nbIncome);
    this.specialPatchesPosition = generateRandomPosition(size, nbSpecialPatches);
  }

  /**
   * Generate a map of position. Each position are randomly picked to diversify
   * the board.
   * 
   * @param size Board's size.
   * @param nb   Quantity to generate
   * 
   * @return An HashMap that represents the position.
   */
  private static HashMap<Integer, Integer> generateRandomPosition(int size, int nb) {
    if (size < 0)
      throw new IllegalArgumentException("size invalid");
    if (nb < 0)
      throw new IllegalArgumentException("nb invalid");

    var result = new HashMap<Integer, Integer>();
    var random = new Random();
    int i;
    for (i = 0; i < nb; i++) {
      // Starting from 1 to size - 1 to not go out of range and
      // not hitting the first space
      var value = random.nextInt(size - 1) + 1;
      if (result.putIfAbsent(value, value) != null)
        i--;
    }
    return result;
  }

  /**
   * Returns true if position is between 0 and the size of the board
   * 
   * @param position position of the token
   * 
   * @return true if position is between 0 and the size of the board
   */
  private boolean isALegalTokenPosition(int position) {
    return position >= 0 && position <= size;
  }

  /**
   * Returns true if the token's position is on a button space.
   *
   * @param tokenPosition the position of a player's token.
   * @return A true if the token's position is on a button space.
   */
  public boolean isTokenOnButton(int tokenPosition) {
    if (!isALegalTokenPosition(tokenPosition))
      throw new IllegalArgumentException("tokenPosition is out of timeBoard");
    return buttonsPosition.containsKey(tokenPosition);
  }

  /**
   * Returns true if the token's position is on a special patch space (1x1 patch).
   * 
   * @param tokenPosition the position of a player's token.
   * @return A true if the token's position is on a special patch space.
   */
  public boolean isTokenOnSpecialPatch(int tokenPosition) {
    if (!isALegalTokenPosition(tokenPosition))
      throw new IllegalArgumentException("tokenPosition is out of timeBoard");
    // removing specified position if found
    var patch = specialPatchesPosition.remove(tokenPosition);
    if (patch != null)
      return true;
    return false;
  }

  /**
   * Is an accessor to the field size.
   * 
   * @return An int that represents the size of the board.
   */
  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    int i;
    for (i = 0; i < size; i++) {
      builder.append("[");
      if (buttonsPosition.containsKey(i)) {
        builder.append("B");
      } else {
        builder.append(" ");
      }
      if (specialPatchesPosition.containsKey(i)) {
        builder.append("#");
      } else {
        builder.append(" ");
      }
      builder.append("]");
    }
    return builder.toString();
  }
  
  /**
   * Draw a tile by specifying upper left x and y.
   * 
   * @param graphics 
   * @param x0 upper left x
   * @param y0 upper left y
   * @param tileSize size of a tile
   */
  private void drawTile(Graphics2D graphics, float x0, float y0, float tileSize) {
    graphics.setColor(Color.GRAY);
    graphics.fill(new Rectangle2D.Float(x0, y0, tileSize + 4, tileSize + 4));
    graphics.setColor(Color.WHITE);
    graphics.fill(new Rectangle2D.Float(x0 + 2, y0 + 2, tileSize, tileSize));
  }
  
  /**
   * Draw a button by specifying upper left x and y.
   * @param graphics Graphics2D object used to display
   * @param x upper left x
   * @param y upper left y
   * @param tileSize size of a tile
   */
  private void drawButton(Graphics2D graphics, int x, int y, int tileSize) {
    graphics.setColor(Color.BLUE);
    graphics.fill(new Ellipse2D.Float(x + tileSize / 2 - 6,
        y + tileSize / 2 - 6, 16, 16));
  }
  
  /**
   * Draw a special patch by specifying upper left x and y.
   * @param graphics Graphics2D object used to display
   * @param x upper left x
   * @param y upper left y
   * @param tileSize size of a tile
   */
  private void drawSpecialPatch(Graphics2D graphics, int x, int y, int tileSize) {
    graphics.setColor(Color.BLACK);
    graphics.fill(new Rectangle2D.Float(x + tileSize / 2 - 6,
        y + tileSize / 2 - 6, 16, 16));
  }
  
  /**
   * Draw a link between tile by the direction pointed at.
   * @param graphics Graphics2D object used to display
   * @param x upper left x
   * @param y upper left y
   * @param tileSize size of a tile
   * @param direction direction chosen.
   */
  private void drawLinkByDirection(Graphics2D graphics, float x, float y, int tileSize, Direction direction) {
    graphics.setColor(Color.LIGHT_GRAY);
    switch(direction) {
      case RIGHT -> graphics.fill(new Rectangle2D.Float(x + tileSize / 2,
                    y + tileSize / 2 - 1, tileSize + 5, 5));
      case DOWN -> graphics.fill(new Rectangle2D.Float(x + tileSize / 2 - 1,
                  y + tileSize / 2 - 3, 5, tileSize + 5));
      case LEFT -> graphics.fill(new Rectangle2D.Float(x - tileSize / 2,
          y + tileSize / 2 - 1, tileSize + 5, 5));
      case UP -> graphics.fill(new Rectangle2D.Float(x + tileSize / 2 - 1,
          y - tileSize / 2, 5, tileSize + 5));
    }
  }
  
  /**
   * Draw a link.
   * 
   * @param graphics Graphics2D object used to display
   * @param column current column
   * @param row current row
   * @param offsetX offset between upper left of window and the start of the board
   * @param offsetY offset between upper left of window and the start of the board
   * @param tileSize tile's size
   * @param layer current layer in board by going from outer to inner 
   * @param limit current limit per column or row
   * @param direction direction chosen
   */
  private void drawLink(Graphics2D graphics, int column, int row, int offsetX, int offsetY, int tileSize, int layer, int limit, Direction direction) {
    if (column == limit && row == layer){        
      drawLinkByDirection(graphics, offsetX + column * (tileSize + 20), offsetY + row * (tileSize + 20), tileSize, Direction.DOWN);
    } else if (column == limit && row == limit) {
      drawLinkByDirection(graphics, offsetX + column * (tileSize + 20), offsetY + row * (tileSize + 20), tileSize, Direction.LEFT);
    } else if (column == layer && row == limit) {
      drawLinkByDirection(graphics, offsetX + column * (tileSize + 20), offsetY + row * (tileSize + 20), tileSize, Direction.UP);
    } else {
      drawLinkByDirection(graphics, offsetX + column * (tileSize + 20), offsetY + row * (tileSize + 20), tileSize, direction);
    }
  }
  
  /**
   * Draw the timeboard
   * 
   * @param graphics Graphics2D object used to display
   */
  private void displayBoard(Graphics2D graphics) {
    int col = 0, row = 0, layer = 0, tileSize = 32, borderOffsetX = 400, borderOffsetY = 130, limit = (int)Math.round(Math.sqrt(size));
    var direction = Direction.RIGHT;
    for (var i = 0; i < size; i++) {
      if (col == layer && row == layer + 1) {
        layer++;
        limit--;
        drawLinkByDirection(graphics, borderOffsetX + col * (tileSize + 20), borderOffsetY + row * (tileSize + 20), tileSize, Direction.RIGHT);
      } else {
        drawLink(graphics, col, row, borderOffsetX, borderOffsetY, tileSize, layer, limit, direction);
      }
      drawTile(graphics, borderOffsetX + col * (tileSize + 20), borderOffsetY + row * (tileSize + 20), tileSize);
      if (buttonsPosition.containsKey(i))
        drawButton(graphics, borderOffsetX + col * (tileSize + 20), borderOffsetY +  row * (tileSize + 20), tileSize);
      if (specialPatchesPosition.containsKey(i))
        drawSpecialPatch(graphics, borderOffsetX + col * (tileSize + 20), borderOffsetY +  row * (tileSize + 20), tileSize);
      if (row == layer && col < limit) {
        col++;
        direction = Direction.RIGHT;
      } else if (row < limit && col == limit) {
        row++;
        direction = Direction.DOWN;
      } else if (row == limit && col > layer) {
        col--;
        direction = Direction.LEFT;
      } else if (row > layer && col == layer) {
        row--;
        direction = Direction.UP;
      }
    }
  }
  
  /**
   * Display timeboard in graphic version.
   * 
   * @param graphics Graphics2D object used to display
   */
  public void display(Graphics2D graphics) {
    Objects.requireNonNull(graphics);
    displayBoard(graphics);
  }
}