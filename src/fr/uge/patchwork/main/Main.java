package fr.uge.patchwork.main;

import fr.uge.patchwork.MenuGraphic;
import fr.uge.patchwork.Patchwork;
import fr.uge.patchwork.UserInterfaceTerminal;

/**
 * A class that represents the main of Patchwork game.
 * 
 * @author VILAYVANH Mickael
 * @author BERNIER Valentin
 */
public class Main {

  /**
   * Main class contructor.
   */
  public Main() {
  }

  /**
   * It is the main of the Patchwork game.
   * 
   * @param args arguments given in command line.
   */
  public static void main(String[] args) {
    var uiTerminal = new UserInterfaceTerminal();
    uiTerminal.diplayTitle();
    int gameMode = uiTerminal.chooseGameMode();
    
    // Selecting correct UI. There will be an interface containing both UI.
    if (gameMode == 0)
      System.exit(0);
    
    else if (gameMode == 1 || gameMode == 2) {
      var patchwork = new Patchwork(54, gameMode);
      // Launch the game with specified parameters.
      patchwork.game(uiTerminal);
    }
    
    else if (gameMode == 3) {
      MenuGraphic.menu();
    }
  }
}
