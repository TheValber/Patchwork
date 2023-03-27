package fr.uge.patchwork;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

/**
 * Representation of a player
 * 
 * @author BERNIER Valentin
 * @author VILAYVANH Mickael
 */
public class Player {
  /**
   * The quilt board of the player
   */
  private final QuiltBoard quiltBoard;
  /**
   * The number of buttons owned by the player
   */
  private int buttons;
  /**
   * The position of the player token on the time board
   */
  private int tokenPosition;
  /**
   * If the player has the 7x7 bonus tile
   */
  private Boolean has7x7;
  /**
   * Id of the player
   */
  private int id;

  /**
   * Player constructor : Create a new player with an empty quilt board, 0
   * buttons, a token located at the position 0 and no 7x7 tile
   * 
   * @param id indicates which player it is.
   */
  public Player(int id) {
    if (id <= 0)
      throw new IllegalArgumentException("id <= 0.");
    quiltBoard = new QuiltBoard();
    buttons = 5;
    tokenPosition = 0;
    has7x7 = false;
    this.id = id;
  }

  /**
   * Returns true if the tokenPosition is greater than the other player's
   * tokenPosition.
   * 
   * @param other represents the other player.
   * 
   * @return true if the tokenPosition is lesser than the other player's
   *         tokenPosition.
   */
  public boolean isAfter(Player other) {
    Objects.requireNonNull(other);

    return tokenPosition > other.tokenPosition;
  }

  /**
   * Move the player token to the position on the timeboard. If the player move on
   * a income place or on a special 1x1 patch, the related actions are executed
   * 
   * @param position  int value that represent player destination
   * @param timeBoard The time board of the game
   * 
   * @return Number of special patches to place
   */
  public int moveTokenTo(int position, TimeBoard timeBoard) {
    if (position < 0)
      throw new IllegalArgumentException("position < 0");
    if (position <= tokenPosition)
      throw new IllegalArgumentException("position <= tokenPosition");
    Objects.requireNonNull(timeBoard);

    int nb1x1 = 0;
    while (tokenPosition < position && tokenPosition < timeBoard.getSize() - 1) {
      tokenPosition++;
      if (timeBoard.isTokenOnButton(tokenPosition)) {
        buttons += quiltBoard.getIncome();
      }
      if (timeBoard.isTokenOnSpecialPatch(tokenPosition)) {
        nb1x1++;
      }
    }
    return nb1x1;
  }

  /**
   * Move the player token for a distance on the timeBoard.
   * 
   * @param distance  int value that represent the number of moved cases
   * @param timeBoard The time board of the game
   * 
   * @return Number of special patches to place
   */
  public int moveToken(int distance, TimeBoard timeBoard) {
    if (distance < 0)
      throw new IllegalArgumentException("position < 0");
    Objects.requireNonNull(timeBoard);

    return moveTokenTo(tokenPosition + distance, timeBoard);
  }

  /**
   * Check if the player has enough buttons to pay cost.
   * 
   * @param cost The cost to check.
   * 
   * @return true if the player has enough buttons.
   */
  public boolean hasEnoughButtons(int cost) {
    if (cost < 0)
      throw new IllegalArgumentException("cost < 0");
    if (cost > buttons)
      return false;
    return true;
  }

  /**
   * Pay cost buttons
   * 
   * @param cost A cost less that the player buttons
   */
  public void payButtons(int cost) {
    if (cost < 0)
      throw new IllegalArgumentException("cost < 0");
    if (cost > buttons)
      throw new IllegalArgumentException("cost > buttons");
    buttons -= cost;
  }

  /**
   * Move on the other Player tokenPosition + 1 and receive one button per space
   * moved
   * 
   * @param other     The other player
   * @param timeBoard The time board of the game
   * 
   * @return Number of special patches to place
   */
  public int advanceAndReceiveButtons(Player other, TimeBoard timeBoard) {
    Objects.requireNonNull(other);
    Objects.requireNonNull(timeBoard);

    int destination = other.tokenPosition + 1;

    buttons += (destination - tokenPosition);

    if (destination == timeBoard.getSize()) {
      buttons--;
    }
    return moveTokenTo(destination, timeBoard);
  }

  /**
   * Calculate an return the player score
   * 
   * @return An int that contains score
   */
  public int score() {
    int bonus;
    if (has7x7)
      bonus = 7;
    else
      bonus = 0;

    return buttons + bonus - 2 * quiltBoard.numberOfEmptySpace();
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    for (var i = 0; i < tokenPosition * 4 + 1; i++) {
      builder.append(" ");
    }
    builder.append("P").append(id).append(" (").append(buttons).append(" buttons)");
    return builder.toString();
  }

  /**
   * Accessor for quiltBoard.
   * 
   * @return the player's quiltBoard.
   */
  public QuiltBoard getQuiltBoard() {
    return quiltBoard;
  }

  /**
   * Check if the player is on the last case of the timeBoard
   * 
   * @param timeBoard The time board
   * @return true if the player is on the last case of the timeBoard
   */
  public boolean isAtEnd(TimeBoard timeBoard) {
    Objects.requireNonNull(timeBoard);
    return tokenPosition == timeBoard.getSize() - 1;
  }

  /**
   * Updates has7x7 if it is currently not one.
   * 
   * @return true has7x7 current state.
   */
  public boolean updateHas7x7() {
    if (!has7x7)
      has7x7 = quiltBoard.check7x7Area();
    return has7x7;
  }

  /**
   * Accessor for has7x7
   * 
   * @return has7x7
   */
  public boolean getHas7x7() {
    return has7x7;
  }
  
  /**
   * Display player statistics at the bottom of the screen
   * 
   * @param graphics Graphics2D objects used to display
   * @param playerId The player id
   * @param button The image of a button
   */
  private void displayStats(Graphics2D graphics, int playerId, BufferedImage button) {
    Objects.requireNonNull(graphics);
    if (playerId != 1 && playerId != 2)
      throw new IllegalArgumentException("playerId has to be 1 or 2.");
    Objects.requireNonNull(button);
    graphics.setColor(Color.WHITE);
    graphics.fill(new Rectangle2D.Float(400 + (playerId == 2 ? 620 : 0), 1020, 500, 50));
    graphics.setColor(Color.BLACK);
    graphics.draw(new Rectangle2D.Float(400 + (playerId == 2 ? 620 : 0), 1020, 500, 50));
    graphics.setFont(new Font("Arial", Font.BOLD, 32));
    // Buttons
    graphics.drawImage(button, 400 + (playerId == 2 ? 620 : 0), 1030, null);
    graphics.drawString("" + buttons, 430 + (playerId == 2 ? 620 : 0), 1058);
    // Number of empty spaces
    graphics.draw(new Rectangle2D.Float(490 + (playerId == 2 ? 620 : 0), 1035, 20, 20));
    graphics.drawString("" + quiltBoard.numberOfEmptySpace(), 520 + (playerId == 2 ? 620 : 0), 1058);
    // Score
    graphics.drawString("Score : " + score(), 580 + (playerId == 2 ? 620 : 0), 1058);
    // Has 7x7
    if (has7x7) {
      graphics.draw(new Rectangle2D.Float(800 + (playerId == 2 ? 620 : 0), 1035, 20, 20));
      graphics.drawString("+", 780 + (playerId == 2 ? 620 : 0), 1058);
      graphics.setFont(new Font("Arial", Font.BOLD, 16));
      graphics.drawString("7", 807 + (playerId == 2 ? 620 : 0), 1052);
    }
  }
  
  /**
   * Draw a circle
   * 
   * @param graphics Graphics2D object used to display
   * @param x Coordinate X
   * @param y Coordinate Y
   * @param tileSize The tile size
   */
  private void drawCircle(Graphics2D graphics, int x, int y, int tileSize) {
    Objects.requireNonNull(graphics);
    if (tileSize < 0)
      throw new IllegalArgumentException("tileSize invalid : < 0");
    graphics.setColor(Color.BLACK);
    graphics.fill(new Ellipse2D.Float(x + tileSize / 2 - 9,
        y + tileSize / 2 - 9, 21, 21));
    if (id == 1)
      graphics.setColor(Color.RED);
    else
      graphics.setColor(Color.GREEN);
    graphics.fill(new Ellipse2D.Float(x + tileSize / 2 - 8,
        y + tileSize / 2 - 8, 19, 19));
    graphics.setColor(Color.BLACK);
    graphics.setFont(new Font("Arial", Font.BOLD, 15));
    graphics.drawString("" + id, x + tileSize / 2 - 2, y + tileSize / 2 + 8);
  }
  
  /**
   * Display player position on the time board
   * 
   * @param graphics Graphics2D object used to display
   * @param boardSize Board size
   */
  private void displayPosition(Graphics2D graphics, int boardSize) {
    int col = 0, row = 0, layer = 0, tileSize = 32;
    var borderOffsetX = 400;
    var borderOffsetY = 130;
    var limit = (int)Math.round(Math.sqrt(boardSize));
    for (var i = 0; i < tokenPosition; i++) {
      if (col == layer && row == layer + 1) {
        layer++;
        limit--;
      }
      if (row == layer && col < limit) {
        col++;
      } else if (row < limit && col == limit) {
        row++;
      } else if (row == limit && col > layer) {
        col--;
      } else if (row > layer && col == layer) {
        row--;
      }
    }
    drawCircle(graphics, borderOffsetX + col * (tileSize + 20), borderOffsetY + row * (tileSize + 20), tileSize);
  }
  
  /**
   * Display the player
   * 
   * @param graphics Graphics2D object used to display
   * @param isPlaying true if its the turn of this player
   * @param playerId Player id
   * @param button Button image
   * @param boardSize Board size
   */
  public void display(Graphics2D graphics, boolean isPlaying, int playerId, BufferedImage button, int boardSize) {
    Objects.requireNonNull(graphics);
    if (playerId != 1 && playerId != 2)
      throw new IllegalArgumentException("playerId has to be 1 or 2.");
    Objects.requireNonNull(button);
    if (isPlaying) {
      quiltBoard.display(graphics);
      graphics.setFont(new Font("Arial", Font.BOLD, 64));
      graphics.drawString("Your turn", 500 + (playerId == 2 ? 632 : 0), 970);
    }
    graphics.setColor(Color.BLACK);
    graphics.setFont(new Font("Arial", Font.BOLD, 128));
    graphics.drawString("P" + playerId, 10 + (playerId == 2 ? 1750 : 0), 1070);
    quiltBoard.displayMini(graphics, (playerId == 1) ? 0 : 1376);
    displayStats(graphics, playerId, button);
    displayPosition(graphics, boardSize);
  }
}