package fr.uge.patchwork;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Objects;

import fr.umlv.zen5.Application;
import fr.umlv.zen5.Event;
import fr.umlv.zen5.Event.Action;

/**
 * Class to manage main menu
 * 
 * @author BERNIER Valentin
 * @author VILAYVANH Mickael
 *
 */
public class MenuGraphic {
  /**
   * MenuGraphic class contructor.
   */
  public MenuGraphic() {
  }
  
  /**
   * Method to manage main menu
   */
  public static void menu() {
    Application.run(Color.BLACK, context -> {
      var ui = new UserInterfaceGraphic(context);
      Event event;
      Point2D.Float location;
      while (true) {
        ui.displayMenu();
        event = context.pollOrWaitEvent(10);
        if (Objects.isNull(event) || event.getAction() != Action.POINTER_UP) continue;
        location = event.getLocation();
        ui.isQuitting(location);
        if (location.x >= 810 && location.x <= 1110 && location.y >= 600 && location.y <= 680) {
          var patchwork = new Patchwork(54, 3);
          patchwork.game(ui);
        }
      }
    });
  }
}
