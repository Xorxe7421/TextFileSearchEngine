package ge.gpavl;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ge.gpavl.TextFileUtils.toLowerCase;

public class SearchEngine {

    private Map<String, List<FileLocation>> index;

    public SearchEngine() throws IOException {
        initializeIndex();
    }

    private void initializeIndex() throws IOException {
        Map<String, List<List<String>>> filenameWordMapping = TextFileUtils.getTextFilenameWordMapping();

        index = new HashMap<>();

        Set<String> filenameKeySet = filenameWordMapping.keySet();
        for (String filenameKey : filenameKeySet) {
            List<List<String>> lines = filenameWordMapping.get(filenameKey);
            for (int i = 0; i < lines.size(); i++) {
                List<String> line = lines.get(i);
                for (String word : line) {
                    processWord(filenameKey, word, i);
                }
            }
        }
    }

    private void processWord(String filenameKey, String word, int lineIndex) {
        int lineNumber = lineIndex + 1;
        if (!index.containsKey(word)) {
            FileLocation fileLocation = createInitialFileLocation(filenameKey, lineNumber);
            index.put(word, createInitialFileLocationList(fileLocation));
            return;
        }

        List<FileLocation> existingFileLocations = index.get(word);
        Optional<FileLocation> fileLocationByFilenameOptional = getFileLocationByFilename(existingFileLocations, filenameKey);
        if (fileLocationByFilenameOptional.isPresent()) {
            FileLocation fileLocationByFilename = fileLocationByFilenameOptional.get();
            Map<Integer, Integer> lineFrequencyMap = fileLocationByFilename.getLineFrequencies();
            lineFrequencyMap.merge(lineNumber, 1, Integer::sum);
            return;
        }

        FileLocation fileLocation = createInitialFileLocation(filenameKey, lineNumber);
        existingFileLocations.add(fileLocation);
    }

    private FileLocation createInitialFileLocation(String fileName, int lineNumber) {
        Map<Integer, Integer> lineFrequencyMap = new HashMap<>(Map.of(lineNumber, 1));
        return new FileLocation(fileName, lineFrequencyMap);
    }

    private List<FileLocation> createInitialFileLocationList(FileLocation fileLocation) {
        return new ArrayList<>(List.of(fileLocation));
    }

    private Optional<FileLocation> getFileLocationByFilename(List<FileLocation> fileLocations, String filename) {
        return fileLocations
                .stream()
                .filter(fileLocation ->
                        fileLocation
                        .getFileName()
                        .equals(filename)
                ).findAny();
    }

    public List<FileLocation> singleWordSearch(String word) {
        String lowerCaseWord = word.toLowerCase();
        return index.get(lowerCaseWord);
    }

    public List<FileLocation> multiWordSearch(List<String> words) {
        List<String> lowerCaseWords = toLowerCase(words);
        List<String> foundFilenames = findContainedFiles(lowerCaseWords);
        return createFileLocationsWithWordFrequency(lowerCaseWords, foundFilenames);
    }

    private List<String> findContainedFiles(List<String> words) {
        List<List<FileLocation>> fileLocationLists = words
                        .stream()
                        .map(word -> index.get(word))
                        .toList();

        List<FileLocation> flattenedList = fileLocationLists.stream().flatMap(Collection::stream).toList();
        List<String> mappedFilenames = flattenedList.stream().map(FileLocation::getFileName).toList();
        return mappedFilenames
                .stream()
                .filter(
                        filename -> Collections.frequency(mappedFilenames, filename) == words.size()
                )
                .distinct()
                .toList();
    }

    private List<FileLocation> createFileLocationsWithWordFrequency(List<String> words, List<String> foundFilenames) {
        Map<String, List<Map<Integer, Integer>>> fileLineFrequencies = getFileLineFrequencies(words, foundFilenames);
        return fileLineFrequencies
                .keySet()
                .stream()
                .map(filename -> new FileLocation(filename, combineFrequencies(fileLineFrequencies.get(filename))))
                .toList();
    }

    private Map<String, List<Map<Integer, Integer>>> getFileLineFrequencies(List<String> words, List<String> foundFilenames) {
        return foundFilenames
                .stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                filename -> words
                                        .stream()
                                        .map(word -> getFileLocationByFilename(index.get(word), filename).get().getLineFrequencies())
                                        .toList()
                        )
                );
    }

    private Map<Integer, Integer> combineFrequencies(List<Map<Integer, Integer>> maps) {
        Set<Integer> combinedKeySet = maps
                .stream()
                .map(Map::keySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return combinedKeySet
                .stream()
                .collect(
                        Collectors.toMap(
                                Function.identity(),
                                key -> maps
                                        .stream()
                                        .map(map -> map.get(key))
                                        .filter(Objects::nonNull)
                                        .reduce(0, Integer::sum)
                        )
                );
    }

    public List<FileLocation> wildcardSearch(String patternString) {
        List<FileLocation> matchedFileLocations = index
                .keySet()
                .stream()
                .filter(word -> {
                            Pattern pattern = Pattern.compile(patternString);
                            Matcher matcher = pattern.matcher(word);
                            return matcher.matches();
                })
                .flatMap(word -> index.get(word).stream())
                .toList();

        Map<String, List<FileLocation>> groupedByFilename =
                matchedFileLocations.stream().collect(Collectors.groupingBy(FileLocation::getFileName));

        return groupedByFilename.keySet().stream().map(filename -> {
            List<Map<Integer, Integer>> lineFrequencies = groupedByFilename.get(filename).stream().map(FileLocation::getLineFrequencies).toList();
            return new FileLocation(filename, combineFrequencies(lineFrequencies));
        }).toList();
    }
}
