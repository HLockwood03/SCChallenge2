import java.io.FileReader;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.util.Stack;

public class Main {
  String[] lines;
  String[] variables;
  String[] keyWords = new String[8];
  int numWhiles = 0;
  int numEnds = 0;
  Map<String, Variable> variableMap = new HashMap<String, Variable>();

  /**
   * This method just reads in the text from the BareBones.txt file
   * It will also call the checkSyntax method to ensure the syntax is valid for each line
   * Another check is if the number of "whiles" equals the number of "ends": if not, the txt is invalid
   */
  public void readText() {
    String line;
    int numLines = 0;

    try {
      BufferedReader br = new BufferedReader(new FileReader("BareBones.txt"));
      line = br.readLine();

      while (line != null) {
        numLines++;
        line = br.readLine();
      }
      lines = new String[numLines];
      variables = new String[numLines];
      br.close();
      br = new BufferedReader(new FileReader("BareBones.txt"));

      for (int i = 0; i < numLines; i++) {
        lines[i] = br.readLine();
        lines[i] = lines[i].trim();
        variables[i] = extractVariable(lines[i]);
        if (!checkSyntax(lines[i], variables[i])) {
          System.err.println("Invalid syntax in file at line " + i);
          System.exit(1);
        }
      }
      if (numWhiles != numEnds) {
        System.err.println("Invalid syntax with while loops");
        System.exit(1);
      }

    } catch (Exception e) {
      System.out.println("File could not be read");
      System.exit(1);
    }
  }

  /**
   * This method removes all constants from the BareBones language to get the variable name
   * Could be a problem if the variable name is something like "while" or "decr" but this is partially
   * the fault of the BareBones language being underspecified
   * @param line takes the current line to get the variable from
   * @return the variable name found
   */
  public String extractVariable(String line) {
    String variableName = line;
    for (int j = 0; j < 8; j++) {
      variableName = variableName.replaceAll(keyWords[j], "");
    }

    return variableName;
  }

    /**
     * This method uses regex to ensure that each line has valid syntax
     * i.e ends with a ;, matches any of the patterns of valid inputs
     * @param line the current line being checked
     * @param variableName the variable name within the line
     * @return if syntax is valid or not (true/false)
     */
  public Boolean checkSyntax(String line, String variableName) {
    Boolean isValid = true;
    String[] regex = new String[5];
    regex[0] = "clear " + variableName + ";";
    regex[1] = "incr " + variableName + ";";
    regex[2] = "decr " + variableName + ";";
    regex[3] = "while " + variableName + " not 0 do;";
    regex[4] = "end;";

    //removing whitespace, mostly for the indented code
    line = line.trim();

    if (!line.substring(line.length() - 1, line.length()).matches(";")) {
      isValid = false;
    }

    for (int i = 0; i < 5; i++) {
      if (line.matches(regex[i])) {
        if (i == 3) {
          numWhiles++;
        } else if (i == 4) {
          numEnds++;
        }
        break;
      }

      if (i == 4) {
        isValid = false;
      }
    }
    return isValid;
  }

  /**
   * Method that actually "executes" the BareBones txt
   * Using a stack we can "track" the last position of a while and end
   * This is useful because, for a while loop inside of another, we cannot assume the first end
   * we see is for the first while. Hence a stack forms a FILO structure that we can use to remove
   * positions when needed.
   */
  public void interpreter() {
    Stack<Integer> whilePositions = new Stack<>();
    Stack<Integer> endPositions = new Stack<>();

    for (int i = 0; i < lines.length; i++) {

      if (variableMap.get(variables[i]) == null && !variables[i].matches("")) {
        variableMap.put(variables[i], new Variable());
        variableMap.get(variables[i]).setName(variables[i]);
      }

      lines[i] = lines[i].replaceAll(";", " ");
      switch (lines[i].substring(0, lines[i].indexOf(" "))) {
        case "incr":
          variableMap.get(variables[i]).increment();
          break;
        case "decr":
          variableMap.get(variables[i]).decrement();
          break;
        case "clear":
          variableMap.get(variables[i]).clear();
          variableMap.get(variables[i]).setCleared(true);
          break;
        case "while":
          if (variableMap.get(variables[i]).getCleared()) {
            if (whilePositions.size() == 0) {
              whilePositions.push(i);
            } else if (whilePositions.peek() != i) {
              whilePositions.push(i);
            }
            if (variableMap.get(variables[i]).getValue() == 0) {
              i = endPositions.peek();
              whilePositions.pop();
              endPositions.pop();
            }
          } else {
            System.err.println(
                "Variable " + variableMap.get(variables[i]).getName() + " has not been cleared. ");
            System.exit(1);
          }
          break;
        case "end":
          if (endPositions.size() == 0) {
            endPositions.push(i);
          } else if (endPositions.peek() != i) {
            endPositions.push(i);
          }

          i = whilePositions.peek() - 1;

          break;
      }
    }
  }

  /**
   * This method gets all of the values for all variables at the end so the results are displayed
   */
  public void displayValues() {
    variableMap.forEach(
        (k, v) -> System.out.println(
            "Value of " + variableMap.get(k).getName() + ": " + variableMap.get(k).getValue()));
  }

  public static void main(String[] args) {
    Main main = new Main();

    //looks quite ugly but needs to be done
    main.keyWords[0] = "while";
    main.keyWords[1] = "not 0 do";
    main.keyWords[2] = "incr";
    main.keyWords[3] = "decr";
    main.keyWords[4] = "clear";
    main.keyWords[5] = " ";
    main.keyWords[6] = ";";
    main.keyWords[7] = "end";

    main.readText();
    main.interpreter();
    main.displayValues();
  }
}
