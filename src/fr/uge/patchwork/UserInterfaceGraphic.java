package fr.uge.patchwork;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import fr.umlv.zen5.ApplicationContext;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;

/**
 * The class that is used for the user interface with the graphic lib zen5
 * 
 * @author BERNIER Valentin
 * @author VILAYVANH Mickael
 *
 */
public final class UserInterfaceGraphic implements UserInterface {
  /**
   * Application Context
   */
  private ApplicationContext context;
  /**
   * Background image
   */
  private final BufferedImage background;
  /**
   * Button image
   */
  private final BufferedImage button;
  
  /**
   * UserInterfaceGraphic constructor : Load images
   * 
   * @param context Application Context
   */
  public UserInterfaceGraphic(ApplicationContext context) {
    Objects.requireNonNull(context);
    this.context = context;
    this.background = loadImage("hud/background");
    this.button = loadImage("hud/button");
  }
  
  /**
   * Display the user interface for patch taking
   * 
   * @param patchesList Patches list
   * @param player Player
   */
  private void displayTakePatchUI(PatchesList patchesList, Player player) {
    context.renderFrame(graphics -> {
      graphics.setFont(new Font("Arial", Font.BOLD, 24));
      graphics.drawString("Take one of the first three patches or", 60, 650);
      graphics.setFont(new Font("Arial", Font.BOLD, 18));
      graphics.drawString("Move without", 505, 632);
      graphics.drawString("taking patch", 505, 650);
      graphics.draw(new Rectangle2D.Float(500, 610, 125, 50));
    });
  }
  
  /**
   * Take the patch in a position given by user by clicking.
   * 
   * @param patchesList The list of patches
   * @param player      The player that take the patch
   * 
   * @return The taken patch
   */
  @Override
  public Patch takePatchUI(PatchesList patchesList, Player player) {
    Objects.requireNonNull(patchesList);
    Objects.requireNonNull(player);

    int position;
    Event event;
    Point2D.Float location;
    while (true) {
      displayTakePatchUI(patchesList, player);
      event = context.pollOrWaitEvent(10);
      if (Objects.isNull(event) || event.getAction() != Action.POINTER_UP) continue;
      location = event.getLocation();
      isQuitting(location);
      position = patchesList.coordToPatchId(location.x, location.y);
      if (position == -1)
        return null;
      if (patchesList.isAValidPatch(position, player))
        return patchesList.takePatch(position, player);
    }
  }
  
  /**
   * Display the user interface for patch placing
   * 
   * @param patch Patch
   * @param quiltBoard Quilt board
   */
  private void displayPlacePatchUI(Patch patch, QuiltBoard quiltBoard) {
    context.renderFrame(graphics -> {
      graphics.drawImage(background.getSubimage(0, 600, 630, 62), 0, 600, null);
      graphics.setFont(new Font("Arial", Font.BOLD, 30));
      graphics.drawString("Place this Patch", 1440, 200);
      graphics.drawImage(background.getSubimage(1440, 234, 160, 160), 1440, 234, null);
      graphics.drawString("Rotate", 1710, 297);
      graphics.draw(new Rectangle2D.Float(1664, 264, 190, 40));
      graphics.drawString("Throw Away", 1670, 347);
      graphics.draw(new Rectangle2D.Float(1664, 314, 190, 40));
      patch.display(graphics, 1440, 234);
      quiltBoard.display(graphics);
    });
  }
  
  /**
   * Places a patch in the given coordinate by the user by clicking. 
   * The coordinate pointed is the place of the topleft corner
   * of a patch's shape
   * 
   * @param patch      The taken patch
   * @param quiltBoard the refering quiltBoard where the patch will be placed
   */
  @Override
  public void placePatchUI(Patch patch, QuiltBoard quiltBoard) {
    Objects.requireNonNull(patch);
    Objects.requireNonNull(quiltBoard);
    
    Coordinate coordinate;
    Event event;
    Point2D.Float location;
    while (true) {
      displayPlacePatchUI(patch, quiltBoard);
      event = context.pollOrWaitEvent(10);
      if (Objects.isNull(event) || event.getAction() != Action.POINTER_UP) continue;
      location = event.getLocation();
      isQuitting(location);
      coordinate = quiltBoard.coordToCoordinate(location.x, location.y);
      if (coordinate.line() == -1) patch = patch.rotate();
      else if (coordinate.line() == -2) return;
      else if (quiltBoard.placePatch(patch, coordinate)) return;
    }
  }
  
  /**
   * Display players informations : stats, quiltboard, turn
   * 
   * @param players HashMap that contains all players.
   * @param playerTurn Player turn
   * @param graphics Graphics2D object used to display
   * @param boardSize Board size
   */
  private void displayPlayers(HashMap<Integer, Player> players, int playerTurn, Graphics2D graphics, int boardSize) {
    Objects.requireNonNull(players);
    if (playerTurn != 1 && playerTurn != 2)
      throw new IllegalArgumentException("playerTurn has to be 1 or 2.");
    Objects.requireNonNull(graphics);

    players.get(playerTurn % 2 + 1).display(graphics, false, playerTurn % 2 + 1, button, boardSize);
    players.get(playerTurn).display(graphics, true, playerTurn, button, boardSize);
    
    graphics.setFont(new Font("Arial", Font.BOLD, 64));
    graphics.drawString("Turn : Player " + playerTurn, 10, 64);
  }
  
  /**
   * Displays on the screen informations about the current turn of the game.
   * 
   * @param players     a HashMap that contains all players.
   * @param timeBoard   a TimeBoard that represents the timeboard.
   * @param patchesList a PatchesList that represents the patcheslist.
   * @param playerTurn  indicates which player's turn it is. Used here to display
   *                    in the right order specfic lines.
   */
  @Override
  public void displayGameTurn(HashMap<Integer, Player> players, TimeBoard timeBoard, PatchesList patchesList, int playerTurn) {
    Objects.requireNonNull(players);
    Objects.requireNonNull(timeBoard);
    Objects.requireNonNull(patchesList);
    if (playerTurn != 1 && playerTurn != 2)
      throw new IllegalArgumentException("playerTurn has to be 1 or 2.");
    
    context.renderFrame(graphics -> {
      graphics.drawImage(background, 0, 0, null);
      patchesList.display(graphics);
      timeBoard.display(graphics);
      displayPlayers(players, playerTurn, graphics, timeBoard.getSize());
    });
  }
  
  /**
   * Display the user interface for showing the scores
   * 
   * @param players HashMap that contains all players.
   * @param firstPlayerAtEnd An int that specifies which player has won.
   */
  private void displayDisplayScore(HashMap<Integer, Player> players, int firstPlayerAtEnd) {
    context.renderFrame(graphics -> {
      graphics.drawImage(background.getSubimage(0, 0, 1920, 900), 0, 0, null);
      graphics.drawImage(background.getSubimage(400, 900, 1120, 100), 400, 900, null);
      graphics.setFont(new Font("Arial", Font.BOLD, 150));
      graphics.drawString("Scores" , 700, 380);
      graphics.setFont(new Font("Arial", Font.BOLD, 100));
      if (players.get(1).score() > players.get(2).score()
          || (players.get(1).score() == players.get(2).score() && firstPlayerAtEnd == 1))
        graphics.drawString("Winner : Player 1" , 540, 580);
      else
        graphics.drawString("Winner : Player 2" , 540, 580);
      graphics.setFont(new Font("Arial", Font.BOLD, 32));
      graphics.drawString("Click to return on the menu" , 730, 780);
    });
  }
  
  /**
   * Display scores
   * 
   * @param players          a HashMap that contains all players.
   * @param firstPlayerAtEnd an int that specifies which player has won.
   */
  @Override
  public void displayScore(HashMap<Integer, Player> players, int firstPlayerAtEnd) {
    Objects.requireNonNull(players);
    if (firstPlayerAtEnd != 1 && firstPlayerAtEnd != 2)
      throw new IllegalArgumentException("firstPlayerAtEnd has to be 1 or 2.");
    Event event;
    Point2D.Float location;
    while (true) {
      displayDisplayScore(players, firstPlayerAtEnd);
      event = context.pollOrWaitEvent(10);
      if (Objects.isNull(event) || event.getAction() != Action.POINTER_UP) continue;
      location = event.getLocation();
      isQuitting(location);
      return;
    }
  }
  
  /**
   * Load and return the image with the given path
   * 
   * @param path Image path in the [project]/images/ folder
   * @return The image
   */
  public static BufferedImage loadImage(String path) {
    Objects.requireNonNull(path);
    BufferedImage image;
    try {
      image = ImageIO.read(Patch.class.getResource("/data/images/" + path + ".png"));
    } catch (IOException e) {
      throw new IllegalStateException("Image loading failed : " + path, e);
    }
    return image;
  }
  
  /**
   * Check if the mouse located at location is on the cross.
   * If it is the case, quit the game.
   * 
   * @param location Location of the mouse
   */
  public void isQuitting(Point2D.Float location) {
    Objects.requireNonNull(location);
    if (location.x >= 1856 && location.y <= 63) {
      context.exit(0);
    }
  }
  
  /**
   * Display the menu
   */
  public void displayMenu() {
    context.renderFrame(graphics -> {
      graphics.drawImage(background, 0, 0, null);
      graphics.setFont(new Font("Arial", Font.BOLD, 150));
      graphics.drawString("Patchwork", 580, 380);
      graphics.setColor(Color.WHITE);
      graphics.fill(new Rectangle2D.Float(810, 600, 300, 80));
      graphics.setColor(Color.BLACK);
      graphics.draw(new Rectangle2D.Float(810, 600, 300, 80));
      graphics.setFont(new Font("Arial", Font.BOLD, 64));
      graphics.drawString("Play", 890, 660);
    });
  }
}
