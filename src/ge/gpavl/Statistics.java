package ge.gpavl;

public class Statistics {
    private int numberOfFilesIndexed;
    private int numberOfUniqueWords;
    private int totalAmountOfWords;
    private int averageWordsPerFile;
    private String smallestFile;
    private String largestFile;

    public void setNumberOfFilesIndexed(int numberOfFilesIndexed) {
        this.numberOfFilesIndexed = numberOfFilesIndexed;
    }

    public void setNumberOfUniqueWords(int numberOfUniqueWords) {
        this.numberOfUniqueWords = numberOfUniqueWords;
    }

    public void setTotalAmountOfWords(int totalAmountOfWords) {
        this.totalAmountOfWords = totalAmountOfWords;
    }

    public void setAverageWordsPerFile() {
        averageWordsPerFile = totalAmountOfWords / numberOfFilesIndexed;
    }

    public void setSmallestFile(String smallestFile) {
        this.smallestFile = smallestFile;
    }

    public void setLargestFile(String largestFile) {
        this.largestFile = largestFile;
    }

    @Override
    public String toString() {
        return String.format("""
                Number of files indexed - %d
                Number of unique words - %d
                Total amount of words - %d
                Average words per file - %d
                Smallest file - %s
                Largest file - %s
                """,
                numberOfFilesIndexed,
                numberOfUniqueWords,
                totalAmountOfWords,
                averageWordsPerFile,
                smallestFile,
                largestFile);
    }
}
