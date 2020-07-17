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

        ArrayList<int[]> positions = new ArrayList<>(); // formatted as {line, start, end + 1}
        ArrayList<String> names = new ArrayList<>(); // only the names of the variables, not the beginning or end

        BufferedReader r = new BufferedReader(new FileReader(modtFile));

        String current; // saves the string which is being parsed
        boolean nameFlag = false;
        int start = 0; // saves the first position of variable indicator (\$)
        int line = 0;
        while ((current = r.readLine()) != null) {

            for (int i = 1; i < current.length(); i++) {

                // to tell the difference between names and regular characters
                if (nameFlag) {

                    if (current.charAt(i) == '\\') {

                        positions.add(new int[]{line, start, i + 1}); // put in the range where the variable exists [a, b)
                        names.add(current.substring(start + 2, i));
                        nameFlag = false; // reset the flag to look for more variables

                    }

                } else {

                    // detect the start of a variable name
                    if (current.charAt(i) == '$' && current.charAt(i - 1) == '\\') {
                        nameFlag = true;
                        start = i-1;
                    }

                }

            }

            // throw an exception if a variable name was not closed
            if (nameFlag) {
                throw new ParseException("A template variable was not closed", line + 1);
            }

            line++;

        }

        // put all the gathered data into the class variables
        lines = line;
        tvarPositions = positions.toArray(new int[3][positions.size()]);
        varNames = names.toArray(new String[0]);

    }

    public void template(HashMap<String, String> modVariables, File target) throws IOException {

        if (modVariables == null) {
            throw new NullPointerException("Remember to set the variables before applying the template");
        }

        // setting up readers and writers
        BufferedReader r = new BufferedReader(new FileReader(modtFile));
        BufferedWriter w = new BufferedWriter(new FileWriter(target));

        // changing the list of variable positions to an iterator, since it is already ordered by position, lowest to highest
        Iterator<int[]> items = Arrays.asList(tvarPositions).iterator();
        // the array of variable names for the same reason
        Iterator<String> variables = Arrays.asList(varNames).iterator();

        boolean processed = false; // flags if all variables have been processed
        int[] current = items.next(); // holds the position of the next variable
        // TODO: Edge case: 0 variables
        for (int i = 0; i < lines; i++) {

            if (processed) {
                w.write(r.readLine() + "\n"); // copy the rest of it over if there aren't variables left
            } else {

                String currentLine = r.readLine(); // save the line to a variable

                if (current[0] == i) { // if the line is known to have a variable in it * i is incremented because

                    int j = 0;
                    while (j < currentLine.length()) { // go through the current line and replace all variables

                        w.write(currentLine.substring(j, current[1])); // put in the part before the variable
                        w.write(modVariables.get(variables.next())); // put in the variable itself | Potential threat -- there is no next item in the iterator
                        j = current[2]; // set the cursor to the end of the variable
                        if (items.hasNext()) { // if there are more variables
                            current = items.next(); // go to the next variable
                            if (current[0] != i + 1) {
                                w.write(currentLine.substring(j)); // write the rest of the line
                                break; // there is nothing else in the line to write
                            }
                        } else { // if there are not more variables
                            processed = true; // stop looking for more variables
                            w.write(currentLine.substring(j)); // write the rest of the line
                            break; // there is nothing else in the line to write
                        }

                    }

                    w.write('\n'); // end the line

                } else {
                    w.write(currentLine + "\n");
                }

            }

        }

        w.close();

    }

    // in case it is easier to have one templator object
    public void template(File target) throws IOException {
        template(modVariables, target);
    }

}
