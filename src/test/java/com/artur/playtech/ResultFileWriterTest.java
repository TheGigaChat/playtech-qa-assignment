package com.artur.playtech;

import com.artur.playtech.utils.ResultFileWriter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * This class tests the file writter.
 */
public class ResultFileWriterTest {

    /**
     * Checks that the file writer doesn't throw any errors.
     */
    @Test
    void shouldWriteTextFile() {
        String filePath = "output/results.txt";
        String content = "Playtech QA assignment results";

        assertDoesNotThrow(() -> ResultFileWriter.writeToFile(filePath, content));
    }
}
