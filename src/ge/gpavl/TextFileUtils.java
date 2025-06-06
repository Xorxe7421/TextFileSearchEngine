package ge.gpavl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextFileUtils {

    public static Map<String, List<List<String>>> getTextFilenameWordMapping() throws IOException {
        List<Path> textFiles = getTextFilesFromCurrentDirectory();
        return getFilenameWordMap(textFiles);
    }

    private static List<Path> getTextFilesFromCurrentDirectory() throws IOException {
        Path currentDirectory = Path.of("");
        try (Stream<Path> currentDirectoryFiles = Files.list(currentDirectory)) {
            return currentDirectoryFiles.filter(path -> {
                String filename = getFileNameAsString(path);
                return isTextFile(filename);
            }).toList();
        }
    }

    private static Map<String, List<List<String>>> getFilenameWordMap(List<Path> textFiles) {
        return textFiles
                .stream()
                .collect(
                        Collectors.toMap(
                                TextFileUtils::getFileNameAsString,
                                TextFileUtils::getCleanedLinesFromFile
                        )
                );
    }

    private static List<List<String>> getCleanedLinesFromFile(Path file) {
        try  {
            List<String> textLines = Files.readAllLines(file);
            return textLines.stream().map(TextFileUtils::getCleanedWords).collect(Collectors.toList());
        }catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static List<String> getCleanedWords(String line){
        String delimiters = ",.!? ";
        StringTokenizer stringTokenizer = new StringTokenizer(line, delimiters);

        List<String> cleanedWords = new ArrayList<>();

        while(stringTokenizer.hasMoreTokens()) {
            cleanedWords.add(stringTokenizer.nextToken());
        }

        return toLowerCase(cleanedWords);
    }

    public static List<String> toLowerCase(List<String> words) {
        return words.stream().map(String::toLowerCase).toList();
    }

    private static String getFileNameAsString(Path file) {
        Path lastPart = file.getFileName();
        return lastPart.toString();
    }

    private static boolean isTextFile(String filename) {
        return filename.endsWith(".txt");
    }
}
