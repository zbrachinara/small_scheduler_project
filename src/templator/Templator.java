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
    int chars;
    HashMap<String, String> modVariables;

    // debugging methods

    public String[] getVarNames() {
        return varNames;
    }

    public int[][] getVarPositions() {
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

        chars = 0;
        char currentChar;
        StringBuilder variableName = new StringBuilder();
        boolean nameFoundFlag = false;
        int startOfVariable = 0; // saves the first position of variable indicator (\$)
        while ((currentChar = (char) r.read()) != '\uFFFF') {

            // to tell the difference between names and regular characters
            if (nameFoundFlag) {

                if (currentChar == '\\') {

                    positions.add(new int[]{startOfVariable, chars}); // put in the range where the variable exists [a, b)
                    names.add(variableName.toString());
                    nameFoundFlag = false;
                    variableName = new StringBuilder();

                } else {
                    variableName.append(currentChar);
                }

            } else {

                // detect the start of a variable name
                if (currentChar == '\\') {
                    r.mark(2); // mark so that if it is not a variable, we can return
                    if (r.read() == '$') {
                        nameFoundFlag = true;
                        startOfVariable = chars;
                        chars++;
                    } else {
                        r.reset();
                    }
                }

            }

            chars++;

        }

        // throw an exception if a variable name was not closed
        if (nameFoundFlag) {
            throw new ParseException("A template variable was not closed", chars + 1);
        }

        // put all the gathered data into the class variables
        tvarPositions = positions.toArray(new int[2][positions.size()]);
        varNames = names.toArray(new String[0]);

    }

    public void template(HashMap<String, String> modVariables, File target) throws IOException {

        if (modVariables == null) {
            throw new NullPointerException("Template variable values were not added");
        }

        // setting up readers and writers
        BufferedReader templateReader = new BufferedReader(new FileReader(modtFile));
        BufferedWriter targetWriter = new BufferedWriter(new FileWriter(target));

        // changing arrays to iterators, since they are already ordered by position
        Iterator<int[]> varPositions = Arrays.asList(tvarPositions).iterator();
        Iterator<String> varNames = Arrays.asList(this.varNames).iterator();

        boolean allVariablesProcessed = false;
        int[] currentVarPositions = varPositions.next();
        // TODO: Edge case: 0 variables
        for (int charPos = 0; charPos < chars; charPos++) {

            if (allVariablesProcessed || currentVarPositions[0] != charPos) {
                targetWriter.write(templateReader.read());
            } else { // currently on a variable

                targetWriter.write(modVariables.get(varNames.next())); // Potential threat -- there is no next item in the iterator (explained in to do)
                // set the cursor to the end of the variable
                charPos = currentVarPositions[1];
                templateReader.skip(currentVarPositions[1]-currentVarPositions[0] + 1);

                if (varPositions.hasNext()) { // if there are more variables
                    currentVarPositions = varPositions.next(); // go to the next variable
                } else { // all variables have been processed
                    allVariablesProcessed = true;
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
