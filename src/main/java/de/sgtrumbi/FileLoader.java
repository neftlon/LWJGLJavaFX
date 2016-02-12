package de.sgtrumbi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * @author Johannes (on 12.02.2016)
 * @see de.sgtrumbi
 */
public class FileLoader {

    private FileLoader() {
    }

    public static String asString(String path) {
        String source = "";

        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNext()) {
                source += scanner.nextLine() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return source;
    }
}
