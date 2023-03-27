package fr.uge.patchwork;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Scanner;

/**
 * The class that is used for the user interface (read and print)
 * 
 * @author BERNIER Valentin
 * @author VILAYVANH Mickael
 */
public final class UserInterfaceTerminal implements UserInterface {
  /**
   * A scanner that will be useful for reading in the terminal
   */
  private Scanner scanner;

  /**
   * UserInterfaceTerminal constructor : Initialize a scanner
   */
  public UserInterfaceTerminal() {
    scanner = new Scanner(System.in);
  }

  /**
   * Read an int with the scanner and return an OptionalInt with the value (or
   * empty if there is no int)
   * 
   * @return An OptionalInt with the value (or empty if there is no int)
   */
  private OptionalInt readInt() {
    OptionalInt result;
    try {
      result = OptionalInt.of(scanner.nextInt());
    } catch (InputMismatchException e) {
      result = OptionalInt.empty();
      scanner.nextLine();
      System.out.println("Invalid entry!");
    }
    return result;
  }

  /**
   * Take the patch in a position given by user in a scanner.
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

    System.out.print("Choose a patch to take.\n(Enter the id / -1 to move without taking patch)\n==> ");

    while (true) {
      position = readInt().orElse(-2);
      System.out.println();

      if (position == -1)
        return null;

      if (patchesList.isAValidPatch(position, player))
        return patchesList.takePatch(position, player);

      System.out.print("You can't take this patch! Try again!\n==> ");
    }
  }

  /**
   * Places a patch in the given coordinate by the user in a scanner. Two int
   * inputs are requiered which the first one refers to the line and the second
   * one to the column. The coordinate pointed is the place of the topleft corner
   * of a patch's shape
   * 
   * 
   * @param patch      The taken patch
   * @param quiltBoard the refering quiltBoard where the patch will be placed
   */
  @Override
  public void placePatchUI(Patch patch, QuiltBoard quiltBoard) {
    Objects.requireNonNull(patch);
    Objects.requireNonNull(quiltBoard);
    int line, column;
    System.out.print("Choose a coordinate to place your patch\n(line first, then column or -1 to rotate or -2 to throw away).\n" + patch + "\n==> ");
    while (true) {
      line = readInt().orElse(-3);
      if (line == -1) {
        patch = patch.rotate();
        System.out.print("New rotation :\n" + patch + "\n==> ");
        continue;
      }
      if (line == -2)
        return;
      column = readInt().orElse(-3);
      System.out.println();
      if (quiltBoard.placePatch(patch, new Coordinate(line, column)))
        break;
      System.out.print("You can't place here! Try again!\n==> ");
    }
    System.out.println(quiltBoard);
  }

  /**
   * Displays the localisation of each player in the game. The player that has to
   * play the turn will be printed first
   * 
   * @param players    a HashMap that contains all players.
   * @param playerTurn indicates which player's turn it is. Used here to display
   *                   in the right order specfic lines.
   */
  private void displayPlayers(HashMap<Integer, Player> players, int playerTurn) {
    Objects.requireNonNull(players);
    if (playerTurn != 1 && playerTurn != 2)
      throw new IllegalArgumentException("playerTurn has to be 1 or 2.");

    System.out.println(players.get(playerTurn));
    System.out.println(players.get(playerTurn % 2 + 1));
  }

  /**
   * Displays on terminal informations about the current turn of the game.
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

    displayPlayerTurn(playerTurn);
    System.out.println(patchesList + "\n");
    displayPlayers(players, playerTurn);
    System.out.println(timeBoard + "\n");
    System.out.println(players.get(playerTurn).getQuiltBoard() + "\n");
  }

  /**
   * Display player turn
   * 
   * @param playerTurn The player turn
   */
  private void displayPlayerTurn(int playerTurn) {
    if (playerTurn != 1 && playerTurn != 2)
      throw new IllegalArgumentException("playerTurn has to be 1 or 2.");

    if (playerTurn == 1)
      System.out.println("  _____  _                         __ \n" + " |  __ \\| |                       /_ |\n"
          + " | |__) | | __ _ _   _  ___ _ __   | |\n" + " |  ___/| |/ _` | | | |/ _ \\ '__|  | |\n"
          + " | |    | | (_| | |_| |  __/ |     | |\n" + " |_|    |_|\\__,_|\\__, |\\___|_|     |_|\n"
          + "                  __/ |               \n" + "                 |___/                \n");
    if (playerTurn == 2)
      System.out.println("  _____  _                         ___  \n" + " |  __ \\| |                       |__ \\ \n"
          + " | |__) | | __ _ _   _  ___ _ __     ) |\n" + " |  ___/| |/ _` | | | |/ _ \\ '__|   / / \n"
          + " | |    | | (_| | |_| |  __/ |     / /_ \n" + " |_|    |_|\\__,_|\\__, |\\___|_|    |____|\n"
          + "                  __/ |                 \n" + "                 |___/                  \n");
  }

  /**
   * Display "PatchWork" title
   */
  public void diplayTitle() {
    System.out.println("  _____      _       _ __          __        _    \n"
        + " |  __ \\    | |     | |\\ \\        / /       | |   \n"
        + " | |__) |_ _| |_ ___| |_\\ \\  /\\  / /__  _ __| | __\n"
        + " |  ___/ _` | __/ __| '_ \\ \\/  \\/ / _ \\| '__| |/ /\n"
        + " | |  | (_| | || (__| | | \\  /\\  / (_) | |  |   < \n"
        + " |_|   \\__,_|\\__\\___|_| |_|\\/  \\/ \\___/|_|  |_|\\_\\\n"
        + "                                                  \n");
  }

  /**
   * Display scores
   * 
   * @param players          a HashMap that contains all players.
   * @param firstPlayerAtEnd an int that specifies which player has won.
   */
  public void displayScore(HashMap<Integer, Player> players, int firstPlayerAtEnd) {
    if (firstPlayerAtEnd < 0 && firstPlayerAtEnd > 2) {
      throw new IllegalArgumentException("firstPlayerAtEnd is wrong.");
    }
    Objects.requireNonNull(players);
    System.out.println("\n   _____                         \n" + "  / ____|                        \n"
        + " | (___   ___ ___  _ __ ___  ___ \n" + "  \\___ \\ / __/ _ \\| '__/ _ \\/ __|\n"
        + "  ____) | (_| (_) | | |  __/\\__ \\\n" + " |_____/ \\___\\___/|_|  \\___||___/\n"
        + "                                 \n");
    System.out.print("Player 1 :\n" + players.get(1).score());
    if (players.get(1).getHas7x7())
      System.out.print(" (+ 7x7 Bonus Tile)");
    System.out.println("\n" + players.get(1).getQuiltBoard() + "\n");
    System.out.print("Player 2 :\n" + players.get(2).score());
    if (players.get(2).getHas7x7())
      System.out.print(" (+ 7x7 Bonus Tile)");
    System.out.println("\n" + players.get(2).getQuiltBoard() + "\n");
    if (players.get(1).score() > players.get(2).score() || (players.get(1).score() == players.get(2).score() && firstPlayerAtEnd == 1))
      displayPlayerTurn(1);
    else
      displayPlayerTurn(2);
  }

  /**
   * Choose the game mode with a scanner
   * 
   * @return 1 for basic game mode, 2 for complete game mode, 3 for graphic game mode, 0 to quit game
   */
  public int chooseGameMode() {
    int gameMode;

    System.out.print(
        "Choose a game mode.\n   1   - Basic Game Mode\n   2   - Complete Game Mode\n   3   - Graphic Game Mode\n\n   0   - Quit Game\n\n==> ");

    while (true) {
      gameMode = readInt().orElse(-1);
      System.out.println();

      if (gameMode >= 0 && gameMode <= 3)
        return gameMode;

      System.out.print("Invalid Game Mode! Try again!\n==> ");
    }
  }
}
