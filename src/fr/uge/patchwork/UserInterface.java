package fr.uge.patchwork;

import java.util.HashMap;

/**
 * Interface used to make a link between Terminal and graphic displaying
 * 
 * @author VILAYVANH Mickael
 * @author BERNIER Valentin
 *
 */
public sealed interface UserInterface permits UserInterfaceTerminal, UserInterfaceGraphic {
  /**
   * Take the patch in a position
   * 
   * @param patchesList The list of patches
   * @param player      The player that take the patch
   * 
   * @return The taken patch
   */
  Patch takePatchUI(PatchesList patchesList, Player player);
  
  /**
   * Places a patch in the given coordinate. 
   * The coordinate pointed is the place of the topleft corner
   * of a patch's shape
   * 
   * @param patch      The taken patch
   * @param quiltBoard the refering quiltBoard where the patch will be placed
   */
  void placePatchUI(Patch patch, QuiltBoard quiltBoard);
  
  /**
   * Displays informations about the current turn of the game.
   * 
   * @param players     a HashMap that contains all players.
   * @param timeBoard   a TimeBoard that represents the timeboard.
   * @param patchesList a PatchesList that represents the patcheslist.
   * @param playerTurn  indicates which player's turn it is. Used here to display
   *                    in the right order specfic lines.
   */
  void displayGameTurn(HashMap<Integer, Player> players, TimeBoard timeBoard, PatchesList patchesList, int playerTurn);
  
  /**
   * Display scores
   * 
   * @param players          a HashMap that contains all players.
   * @param firstPlayerAtEnd an int that specifies which player has won.
   */
  void displayScore(HashMap<Integer, Player> players, int firstPlayerAtEnd);
}
