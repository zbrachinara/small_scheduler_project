package tests;

import main.Main;
import org.junit.jupiter.api.Test;
import templator.Templator;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class TemplatorTest {

    static String out = Main.outDir;

    @Test
    void testTemplate() throws IOException, ParseException {
        Templator testTemplate = new Templator(new File(out + "tests/testModt.modt"));
        HashMap<String, String> testModVariables = new HashMap<>();

        testModVariables.put("variable1", "first test");
        testModVariables.put("variable2", "second test");

        testTemplate.template(testModVariables,  new File(out + "tests/testOut.modc"));
    }
}
