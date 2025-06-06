package ge.gpavl;

import java.io.IOException;
import java.util.*;

import static ge.gpavl.IOUtils.*;

public class SearchEngineApplication {

    private boolean isFinished = false;
    private final SearchEngine searchEngine;

    public SearchEngineApplication() throws IOException {
        this.searchEngine = new SearchEngine();
    }

    public void run() {
        searchEngine.displayStatistics();
        while (!isFinished) {
            MenuOption menuOption = getChosenMenuOption();
            if (menuOption == MenuOption.Exit) {
                isFinished = true;
            }

            handleSearch();
        }
    }

    public static MenuOption getChosenMenuOption() {
        while (true) {
            try {
                String option = getUserInput("Choose following options - " + Arrays.toString(MenuOption.values()) + ": ");
                return MenuOption.valueOf(option);
            } catch (IllegalArgumentException e) {
                print("Illegal input, try again");
            }
        }
    }

    private void handleSearch() {
        if (isFinished) {
            return;
        }

        SearchOption searchOperation = getChoosenSearchOption();
        List<FileLocation> searchResult = switch (searchOperation) {
            case SingleWord -> {
                String word = getUserInput("Enter the word to search: ");
                yield searchEngine.singleWordSearch(word);
            }
            case MultiWord -> {
                String line = getUserInput("Enter the words separated by commas: ");
                List<String> words = getListFromLine(line);
                yield searchEngine.multiWordSearch(words);
            }
            case Wildcard -> {
                String wildcard = getUserInput("Enter the wildcard: ");
                yield searchEngine.wildcardSearch(wildcard);
            }
        };

        displaySearchResult(searchResult);
    }

    private SearchOption getChoosenSearchOption() {
        while (true) {
            try {
                String option = getUserInput("Choose following search options - " + Arrays.toString(SearchOption.values()) + ": ");
                return SearchOption.valueOf(option);
            } catch (IllegalArgumentException e) {
                print("Illegal input, try again");
            }
        }
    }

    private List<String> getListFromLine(String line) {
        return Arrays.asList(line.split(", "));
    }

    private void displaySearchResult(List<FileLocation> searchResult) {
        if (searchResult == null) {
            print("Provided word wasn't found");
            return;
        }

        orderSearchResult(searchResult);
        searchResult.forEach(this::displayFileLocation);
    }

    private void orderSearchResult(List<FileLocation> searchResult) {
        Comparator<FileLocation> comparator =
                Comparator.comparingInt(fileLocation -> calculateFileLocationScore(fileLocation.getLineFrequencies()));

        searchResult.sort(comparator.reversed());
    }

    private void displayFileLocation(FileLocation fileLocation) {
        Map<Integer, Integer> lineFrequencies = fileLocation.getLineFrequencies();
        int score = calculateFileLocationScore(lineFrequencies);

        String heading = fileLocation.getFileName() + " - score: " + score;
        print(heading);

        Set<Integer> lineNumbers = new TreeSet<>(lineFrequencies.keySet());
        lineNumbers.forEach(lineNumber -> {
            String lineDescriptor = "Line " + lineNumber + ", " + "occurrences" + " - " + lineFrequencies.get(lineNumber);
            print(lineDescriptor);
        });

        print("\n");
    }

    private int calculateFileLocationScore(Map<Integer, Integer> lineFrequencies) {
        return lineFrequencies.keySet().stream().map(lineFrequencies::get).reduce(0, Integer::sum);
    }
}
