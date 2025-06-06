package ge.gpavl;

import java.util.Map;

public class FileLocation {

    private final String fileName;
    private Map<Integer, Integer> lineFrequencies;

    public FileLocation(String fileName, Map<Integer, Integer> lineFrequencyMap) {
        this.fileName = fileName;
        this.lineFrequencies = lineFrequencyMap;
    }

    public FileLocation(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<Integer, Integer> getLineFrequencies() {
        return lineFrequencies;
    }

    @Override
    public String toString() {
        return "fileName: " + fileName + " " + "lineNumbers: " + lineFrequencies.toString();
    }
}
