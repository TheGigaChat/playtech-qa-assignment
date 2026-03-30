package com.artur.playtech.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

/**
 * This class is used for writing text into files.
 */
public class ResultFileWriter {
    /**
     * This method writes the content to the file by using the filePath.
     *
     * @param filePath is a path of the output file.
     * @param content is what we write to the file.
     * @throws IOException creating a directory with a wrong path or writing to unexisting file.
     */
    public static void writeToFile(String filePath, String content) throws IOException {
        Path path = Path.of(filePath);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        Files.writeString(path, content);
    }
}
