package fr.uge.patchwork;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

/**
 * Representation of a Patchwork game
 * 
 * @author BERNIER Valentin
 * @author VILAYVANH Mickael
 */
public class Patchwork {
  /**
   * The players represented by a number (1 or 2) and an object from the class
   * Player
   */
  private final HashMap<Integer, Player> players;
  /**
   * Time board
   */
  private final TimeBoard timeBoard;
  /**
   * List of patches
   */
  private final PatchesList patchesList;
  /**
   * The player who has to play this turn (1 or 2)
   */
  private int playerTurn;
  /**
   * The Game mode : 1 - Basic Game Mode 2 - Complete Game Mode 3 - Graphic mode
   */
  private final int gameMode;
  /**
   * The string that represents the path to the file that contains patches data
   */
  private final String patchesData;
  /**
   * The id of the first player who has finished the game
   */
  private int firstPlayerAtEnd;
  /**
   * Used to know if the specialTile has been given or not.
   */
  private boolean specialTileGiven;

  /**
   * Patchwork constructor : Create a new game of Patchwork
   * 
   * @param timeBoardSize Number of squares in the time board
   * @param gameMode      The Game mode : 1 - Basic Game Mode 2 - Complete Game
   *                      Mode
   */
  public Patchwork(int timeBoardSize, int gameMode) {
    if (timeBoardSize < 0)
      throw new IllegalArgumentException("timeBoardSize invalid");
    if (!isALegalGameMode(gameMode))
      throw new IllegalArgumentException("gameMode invalid");
    players = new HashMap<>();
    players.put(1, new Player(1));
    players.put(2, new Player(2));
    timeBoard = new TimeBoard(timeBoardSize, gameMode);
    patchesList = new PatchesList();
    playerTurn = 1;
    firstPlayerAtEnd = 0;
    this.gameMode = gameMode;
    patchesData = switch (gameMode) {
    case 1 -> "data/patches/patchesBase.data";
    case 2 -> "data/patches/patches.data";
    case 3 -> "data/patches/patches.data";
    default -> throw new IllegalArgumentException("Unexpected value: " + gameMode);
    };
  }

  /**
   * Returns true if gameMode is 1 or 2.
   * 
   * @param gameMode Current gameMode chosen.
   * @return true if gameMode is 1 or 2.
   */
  private boolean isALegalGameMode(int gameMode) {
    return gameMode == 1 || gameMode == 2 || gameMode == 3;
  }

  /**
   * Switch the value of playerTurn from 1 to 2 or the other way around. So
   * playerTurn can either be 1 or 2, indicating which player has to play.
   */
  private void switchTurn() {
    playerTurn = switch (playerTurn) {
    case 1 -> 2;
    case 2 -> 1;
    default -> throw new IllegalStateException();
    };
  }

  /**
   * Updates the value of playerTurn if a player is crossing another player.
   */
  private void updatePlayerTurn() {
    if (players.get(playerTurn).isAfter(players.get(playerTurn % 2 + 1)))
      switchTurn();
  }

  /**
   * Check if the players are at end or if the patchesList is empty
   * 
   * @return True if the players are at end or if the patchesList is empty
   */
  private boolean gameIsEnd() {
    if (firstPlayerAtEnd == 0) {
      if (players.get(1).isAtEnd(timeBoard))
        firstPlayerAtEnd = 1;
      else if (players.get(2).isAtEnd(timeBoard))
        firstPlayerAtEnd = 2;
    }
    return patchesList.isEmpty() || (players.get(1).isAtEnd(timeBoard) && players.get(2).isAtEnd(timeBoard));
  }

  /**
   * Updates specialTileGiven value if it is not given yet.
   */
  private void updateSpecialTile() {
    if (!specialTileGiven) {
      if (players.get(playerTurn).updateHas7x7()) {
        specialTileGiven = true;
      }
    }
  }

  /**
   * Game loop
   * 
   * @param ui Object that is used for display and user interactions
   */
  private void gameLoop(UserInterface ui) {
    Objects.requireNonNull(ui);
    Patch patch;
    Player player;
    int nb1x1;
    while (!gameIsEnd()) {
      ui.displayGameTurn(players, timeBoard, patchesList, playerTurn);
      player = players.get(playerTurn);
      patch = ui.takePatchUI(patchesList, player);
      if (Objects.isNull(patch)) {
        nb1x1 = player.advanceAndReceiveButtons(players.get(playerTurn % 2 + 1), timeBoard);
      } else {
        player.payButtons(patch.cost());
        ui.placePatchUI(patch, player.getQuiltBoard());
        nb1x1 = player.moveToken(patch.time(), timeBoard);
      }
      for (int i = 0; i < nb1x1; i++)
        ui.placePatchUI(Patch.createPatch("33;0;0;0;1;1/\n10000\n00000\n00000\n00000\n00000\n", gameMode),
            player.getQuiltBoard());
      if (gameMode != 1) updateSpecialTile();
      updatePlayerTurn();
    }
    ui.displayGameTurn(players, timeBoard, patchesList, playerTurn);
  }

  /**
   * The main game method
   * 
   * @param ui Object that is used for display and user interactions
   */
  public void game(UserInterface ui) {
    Objects.requireNonNull(ui);

    try {
      patchesList.loadPatches(Path.of(patchesData), gameMode);
    } catch (IOException e1) {
      try {
        patchesList.loadPatches(Path.of("src/" + patchesData), gameMode);
      } catch (IOException e2) {
        throw new IllegalStateException("File patchesData not valid", e2);
      }
    }

    patchesList.shufflePatches();

    gameLoop(ui);

    ui.displayScore(players, firstPlayerAtEnd);
  }
}
