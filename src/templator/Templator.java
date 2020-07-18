package templator;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class Templator {

    File modtFile;
    String[] varNames;
    int[][] tvarPositions;
    int lines;
    HashMap<String, String> modVariables;

    // debugging methods

    public String[] getVarNames() {
        return varNames;
    }

    public int[][] getTvarPositions() {
        return tvarPositions;
    }

    // production methods

    public void setModVariables(HashMap<String, String> modVariables) {

        if (modVariables.size() != tvarPositions.length) {
            throw new IllegalArgumentException("You passed in too few or too many variables");
        }
        this.modVariables = modVariables;

    }

    public Templator(File modtFile) throws IOException, ParseException {

        this.modtFile = modtFile;
        findTemplates();

    }

    private void findTemplates() throws IOException, ParseException {

        ArrayList<int[]> positions = new ArrayList<>(); // formatted as {currentLineNumber, startOfVariable, end + 1}
        ArrayList<String> names = new ArrayList<>(); // only the names of the variables, not the beginning or end

        BufferedReader r = new BufferedReader(new FileReader(modtFile));

        String currentLine;
        boolean nameFoundFlag = false;
        int startOfVariable = 0; // saves the first position of variable indicator (\$)
        int currentLineNumber = 0;
        while ((currentLine = r.readLine()) != null) {

            for (int i = 1; i < currentLine.length(); i++) {

                // to tell the difference between names and regular characters
                if (nameFoundFlag) {

                    if (currentLine.charAt(i) == '\\') {

                        positions.add(new int[]{currentLineNumber, startOfVariable, i + 1}); // put in the range where the variable exists [a, b)
                        names.add(currentLine.substring(startOfVariable + 2, i));
                        nameFoundFlag = false;

                    }

                } else {

                    // detect the start of a variable name
                    if (currentLine.charAt(i) == '$' && currentLine.charAt(i - 1) == '\\') {
                        nameFoundFlag = true;
                        startOfVariable = i-1;
                    }

                }

            }

            // throw an exception if a variable name was not closed
            if (nameFoundFlag) {
                throw new ParseException("A template variable was not closed", currentLineNumber + 1);
            }

            currentLineNumber++;

        }

        // put all the gathered data into the class variables
        lines = currentLineNumber;
        tvarPositions = positions.toArray(new int[3][positions.size()]);
        varNames = names.toArray(new String[0]);

    }

    public void template(HashMap<String, String> modVariables, File target) throws IOException {

        if (modVariables == null) {
            throw new NullPointerException("Template variable values were not added");
        }

        // setting up readers and writers
        BufferedReader templateReader = new BufferedReader(new FileReader(modtFile));
        BufferedWriter targetWriter = new BufferedWriter(new FileWriter(target));

        // changing the list of variable positions to an iterator, since it is already ordered by position
        Iterator<int[]> variablePositions = Arrays.asList(tvarPositions).iterator();
        // the array of variable names for the same reason
        Iterator<String> variables = Arrays.asList(varNames).iterator();

        boolean allVariablesProcessed = false;
        int[] currentVariablePositions = variablePositions.next();
        // TODO: Edge case: 0 variables
        for (int i = 0; i < lines; i++) {

            if (allVariablesProcessed) {
                targetWriter.write(templateReader.readLine() + "\n");
            } else {

                String currentLine = templateReader.readLine();

                if (currentVariablePositions[0] == i) { // if line contains template variable

                    int j = 0;
                    while (j < currentLine.length()) { // go through the current line and replace all variables

                        targetWriter.write(currentLine.substring(j, currentVariablePositions[1])); // put in the part before the variable
                        targetWriter.write(modVariables.get(variables.next())); // Potential threat -- there is no next item in the iterator (explained in to do)
                        j = currentVariablePositions[2]; // set the cursor to the end of the variable
                        if (variablePositions.hasNext()) { // if there are more variables
                            currentVariablePositions = variablePositions.next(); // go to the next variable
                            if (currentVariablePositions[0] != i + 1) { // exit if no more variables on this line
                                targetWriter.write(currentLine.substring(j));
                                break;
                            }
                        } else { // all variables have been processed
                            allVariablesProcessed = true;
                            targetWriter.write(currentLine.substring(j));
                            break;
                        }

                    }

                    targetWriter.write('\n');

                } else {
                    targetWriter.write(currentLine + "\n");
                }

            }

        }

        targetWriter.close();

    }

    // in case it is easier to have one variable set
    public void template(File target) throws IOException {
        template(modVariables, target);
    }

}
