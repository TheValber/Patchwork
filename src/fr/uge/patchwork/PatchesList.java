package fr.uge.patchwork;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * Representation of a list of patches
 * 
 * @author BERNIER Valentin
 * @author VILAYVANH Mickael
 */
public class PatchesList {
  /**
   * An ArrayList that represents all the Patches of the game.
   */
  private final ArrayList<Patch> patchesList;
  /**
   * An int that represents the position of the neutral token.
   */
  private int neutralTokenPosition;

  /**
   * PatchesList constructor : Create an empty patchesList with the
   * neutralTokenPosition at the position 0.
   */
  public PatchesList() {
    patchesList = new ArrayList<>();
    neutralTokenPosition = 0;
  }

  /**
   * Load the patchesList from the UTF8 file located at the Path path.
   * 
   * @param path Path of the file to read
   * @param gameMode game mode
   * @throws IOException In case of file reading error
   */
  public void loadPatches(Path path, int gameMode) throws IOException {
    Objects.requireNonNull(path);
    try (var reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      // Initializing with the first line of the file
      var builder = new StringBuilder();
      int nbLine = 0;
      String line = reader.readLine();
      builder.append(line).append("\n");
      while (line != null) {
        // A patch description is complete, creation of the patch in the List
        if (nbLine == 5) {
          nbLine = -1;
          patchesList.add(Patch.createPatch(builder.toString(), gameMode));
          builder = new StringBuilder();
        }
        // Next Line
        nbLine++;
        line = reader.readLine();
        builder.append(line).append("\n");
      }
    }
  }

  /**
   * Check if the Patch located at patchPosition is in one of the three next Patch
   * after the neutral token.
   * 
   * @param patchPosition The Patch's position to check
   * @return true if it is in the three next, false otherwise
   */
  public boolean isInTheThreeNext(int patchPosition) {
    if (patchPosition < 0 || patchPosition >= patchesList.size())
      throw new IllegalArgumentException("patchPosition not valid");
    boolean onePatchChecked = false;
    boolean twoPatchesChecked = false;
    int size = patchesList.size();
    // Else, it has to check the three next patches without counting those which
    // have already been taken
    for (int i = 0; i < size; i++) {
      if (Objects.nonNull(patchesList.get((i + neutralTokenPosition) % size))) { // There is a Patch at the i position
        if (patchPosition == (i + neutralTokenPosition) % size) // Same position -> right place
          return true;
        else if (twoPatchesChecked) // 2 + 1 wrong patches -> continue searching
          return false;
        else if (onePatchChecked) // 1 + 1 wrong patches -> continue searching
          twoPatchesChecked = true;
        else // 0 + 1 wrong patches -> continue searching
          onePatchChecked = true;
      }
    }
    return false;
  }

  /**
   * Transform into String the neutral token
   * 
   * @param pos     Index progression in the patchesList
   * @param hasNext If the neutral token has a patch after (!= null)
   * @return The string with the neutral token (or spaces)
   */
  private String toStringNeutralToken(int pos) {
    if (pos == neutralTokenPosition)
      return " > ";
    if (Objects.nonNull(patchesList.get(pos)))
      return "   ";
    return "";
  }

  /**
   * Transform into String the index of all patches
   * 
   * @return The string with patches index
   */
  private String toStringIndex() {
    var builder = new StringBuilder();

    // For each Patch, add to the builder its data (cost, time, income)
    for (int j = 0; j < patchesList.size(); j++) {
      builder.append(toStringNeutralToken(j));
      if (Objects.nonNull(patchesList.get(j))) {
        builder.append(String.format("%-2d", j));
        builder.append("   ");
        if (patchesList.get(j).costMoreThan10())
          builder.append(" ");
      }
    }
    builder.append("\n");

    return builder.toString();
  }

  /**
   * Transform into String the data of all patches
   * 
   * @return The string with patches data
   */
  private String toStringData() {
    var builder = new StringBuilder();

    // For each Patch, add to the builder its data (cost, time, income)
    for (int j = 0; j < patchesList.size(); j++) {
      builder.append(toStringNeutralToken(j));
      if (Objects.nonNull(patchesList.get(j))) {
        builder.append(patchesList.get(j).toStringData());
      }
    }

    return builder.toString();
  }

  /**
   * Transform into String the shapes of all patches.
   * 
   * @return The string with patches shapes.
   */
  private String toStringShape() {
    var builder = new StringBuilder();

    // For each line (a patch is composed of 5 lines), add to the builder the String
    // that represents this line for each patch
    for (int i = 0; i < 5; i++) {
      builder.append("\n");
      for (int j = 0; j < patchesList.size(); j++) {
        builder.append(toStringNeutralToken(j));
        if (Objects.nonNull(patchesList.get(j))) {
          builder.append(patchesList.get(j).toStringLine(i, ' '));
        }
      }
    }

    return builder.toString();
  }

  @Override
  public String toString() {
    System.out.println("Patches List (id // buttons cost, time cost, buttons income // shape) :\n");

    var builder = new StringBuilder();

    builder.append(toStringIndex());
    builder.append(toStringData());
    builder.append(toStringShape());

    return builder.toString();
  }

  /**
   * Shuffle patchesList.
   */
  public void shufflePatches() {
    Collections.shuffle(patchesList);
  }

  /**
   * Take the patch at the position position.
   * 
   * @param position Position of the patch to take.
   * @param player   The player taking the patch.
   * 
   * @return The taken patch
   */
  public Patch takePatch(int position, Player player) {
    Objects.requireNonNull(player);
    if (!isAValidPatch(position, player))
      throw new IllegalArgumentException("position not valid");

    Patch takenPatch = patchesList.get(position);
    patchesList.set(position, null);
    neutralTokenPosition = (position + 1) % patchesList.size();

    return takenPatch;
  }

  /**
   * Check if the patch at position is possible to take.
   * 
   * @param position The patch position to check.
   * @param player   The player concerned by the verification.
   * 
   * @return true if it is possible, false otherwise
   */
  public boolean isAValidPatch(int position, Player player) {
    Objects.requireNonNull(player);
    return position >= 0 && position < patchesList.size() && Objects.nonNull(patchesList.get(position))
        && isInTheThreeNext(position) && player.hasEnoughButtons(patchesList.get(position).cost());
  }

  /**
   * Check if the patchesList is empty.
   * 
   * @return true if it is empty, false otherwise.
   */
  public boolean isEmpty() {
    for (var patch : patchesList) {
      if (Objects.nonNull(patch))
        return false;
    }
    return true;
  }
  
  /**
   * Display the neutral token
   * 
   * @param graphics Graphics2D object used to display
   */
  private void displayNeutralToken(Graphics2D graphics) {
    Objects.requireNonNull(graphics);
    graphics.setColor(Color.RED);
    graphics.fill(new Rectangle2D.Float(60, 668, 8, 160));
    graphics.setColor(Color.BLACK);
    graphics.draw(new Rectangle2D.Float(60, 668, 8, 160));
  }
  
  /**
   * Display the patches list
   * 
   * @param graphics Graphics2D object used to display
   */
  public void display(Graphics2D graphics) {
    Objects.requireNonNull(graphics);
    displayNeutralToken(graphics);
    int x = 128;
    int size = patchesList.size();
    int n = 0;
    for (int i = 0; i < size; i++) {
      if (Objects.nonNull(patchesList.get((i + neutralTokenPosition) % size))) {
        if (n < 10)
          x += patchesList.get((i + neutralTokenPosition) % size).display(graphics, x, 668) + 32;
        n++;
      }
    }
    if (n > 10) {
      graphics.setFont(new Font("Arial", Font.BOLD, 64));
      graphics.drawString("+ " + (n - 10), x, 768);
    }
  }
  
  /**
   * Return the patch id that correspond to the mouse coordinates
   * 
   * @param x X position
   * @param y Y position
   * @return Patch id; -1 if skip turn button; -2 if no patch is here
   */
  public int coordToPatchId(double x, double y) {
    if (x >= 500 && x <= 625 && y >= 610 && y <= 660)
      return -1;
    if (y < 668 || y > 828)
      return -2;
    int cursor = 128,  size = patchesList.size(), n = 0, width;
    for (int i = 0; i < size; i++) {
      if (Objects.nonNull(patchesList.get((i + neutralTokenPosition) % size))) {
        if (n < 10) {
          width = patchesList.get((i + neutralTokenPosition) % size).width() * 32;
          if (x >= cursor && x <= cursor + width) {
            return (i + neutralTokenPosition) % size;
          }
          cursor += width + 32;
        }
        n++;
      }
    }
    return -2;
  }
}