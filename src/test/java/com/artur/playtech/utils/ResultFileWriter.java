package com.artur.playtech.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

public class ResultFileWriter {
    public static void writeToFile(String filePath, String content) throws IOException {
        Path path = Path.of(filePath);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        Files.writeString(path, content);
    }
}
