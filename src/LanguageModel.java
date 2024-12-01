import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class LanguageModel {
    
    // Class members
    private FileReader fileReader;                          // Filereader object named "fileReader"
    private Map <String, Map<String, Integer> > firstWord;  // Map object "firstWord" to store first word and associated secondWord + count
    private Random random = new Random();                   // Random generator object


    // LanguageModel object constructor
    public LanguageModel(FileReader fileReader) {           // Constructor takes fileReader as an argument and initializes the object
        this.fileReader = fileReader;                       // Assigns the passed fileReader to instance variable
        this.firstWord = new HashMap<>();                   // firstWords is a new HashMap<>(), which will store the word transition model
    }


    // Building the language model - read the text input and counting word transitions
    public void buildModel() throws IOException {

        // Initialize readers and String to store the line
        BufferedReader br = new BufferedReader(fileReader); // Buffered reader wraps the FileReader object, already initialized in main and passed as an argument into the constructor
        String line = "";                                   // line variable to store each line of text read from the input

        // Read and clean the line
        while ((line = br.readLine()) != null) {
            
            // Clean the line
            line = line.trim().replaceAll("\\p{Punct}", ""); // Clean the line. Remove all leading or trailing whitespaces and punctuation marks
            
            
            if (line.length() <= 0) { // If the line is empty after cleaning, continue
                continue;

            } else {    // Else, split the cleaned line into individual words, using any sequence of whitespace as the delimiter
                String[] words = line.split("\\s+");

                for (int i = 0; i < words.length - 1; i++) {        // Loop through the words in the line
                    String currentWord = words[i];                  // Current word is i
                    String nextWord = words[i + 1];                 // Next word is i+1
                    addToWordDistribution(currentWord, nextWord);   // Calls the addToWordDistribution method to update nextWords map
                }
                
            }
        }


    }


    private void addToWordDistribution(String currentWord, String nextWord) {
        
        // firstWord = HashMap <String , <String, Integer> >
        // secondWord = HashMap <String, Integer>
        
        Map<String, Integer> secondWord;

        // Check if currentWord already exists in the firstWord map
        if (firstWord.containsKey(currentWord)) {      // If firstWords already contains currentWord
            secondWord = firstWord.get(currentWord);   // Retrieve the associated secondWord map
        } else {
            secondWord = new HashMap<>();              // If not, create a new associated secondWord map
        }

        // Initialize a counter to count occurances of word pattern
        int count = 0;

        // Check if nextWord already exists in the secondWords map
        if (secondWord.containsKey(nextWord)) {        // If secondWord already has nextWord
            count = secondWord.get(nextWord) + 1;      // Get the current count and increment by 1

        } else {                                       // If secondWord doesn't have nextWord
            count++;                                   // Increment the counter
        }

        // Update secondWord map with nextWord and count
        secondWord.put(nextWord, count);

        // Update firstWord map with currentWord and updated secondWord Map
        firstWord.put(currentWord, secondWord);
    
    }


    public void printModel() {

        System.out.println("\n");

        for (String word : firstWord.keySet()) {                    // For every word in firstWord HashMap
            System.out.println("First word: " + word);              // Print out the first word

            Map<String, Integer> secondWord = firstWord.get(word);  // Get secondWord map, associated with the firstWord
            for (String wordNext : secondWord.keySet()) {           // For every wordNext in the secondWorld map
                System.out.println(" -> " + wordNext + " : "        // Print wordNext and associated count value
                    + secondWord.get(wordNext));
            }

            System.out.println("\n");

        }
    }



    // METHODS TO GENERATE SENTENCES
    // Greedy algo (exploitation) - Generate a sentence with the most likely next word
    public String generateGreedy(String root, int numWords) {
        String theSentence = root;              // Starts the sentence with the root word
        String currentWord = root;              // Initialize the current word to the root
        String nextWord = "";                   // Initialize the next word as an empty string
        
        for (int i = 0; i < numWords; i++) {    // For however many words requested by the user    
            nextWord = nextWord(currentWord);   // Use the method nextWord, passing in the current word, to get the most likely next word
            if (nextWord.length() <= 0) {       // If there is no next word
                break;                          // break and skip to returning the sentence
            }
            theSentence += " " + nextWord;      // Append the next word to the sentence
            currentWord = nextWord;             // Update the currentWord to the nextWord, for the next iteration
        }

        return theSentence;                     // Return the completed sentence
    }

    // Greedy algo (exploitation) - Generate the most likely next word
    public String nextWord(String word) {
        int wordFrequency = -1;                 // Initialize wordCount to track the highest count, -1 because no word will have -1
        String theWord = "";                    // Initialize variable to store the most likely next word

        Map<String, Integer> secondWord = firstWord.get(word);  // Retrieve word distribution associated with the passed in word from firstWord Map
        if (secondWord == null) {               // If there are no second words
            return "";                          // Return an empty string
        }

        for (String w : secondWord.keySet()) {      // Loop through the next possible words
            if (secondWord.get(w) > wordFrequency){ // If the value of the current word is more than the word
                wordFrequency = secondWord.get(w);  // Update to the highest word frequency
                theWord = w;                        // Update that word to be the most likely next word
            }
        }

        return theWord;

    }


    // Exploration - Generate a sentence with a random next word
    public String generateExplore(String root, int numWords) {
        String theSentence = root;
        String currentWord = root;
        String nextWord = "";

        for (int i = 0; i < numWords; i++) {
            nextWord = randomNextWord(currentWord);
            if (nextWord.length() <= 0) {
                break;
            }
            theSentence += " " + nextWord;
            currentWord = nextWord;
        }

        return theSentence;
    }

    // Exploration - Generate a random next word
    public String randomNextWord(String word){

        Map<String, Integer> secondWord = firstWord.get(word);
        if (secondWord == null){
            return "";
        }

        int numWords = secondWord.size();            // Get the number of words in the map
        int theWord = random.nextInt(numWords);      // Generate a random int from 0 - numWords, assign that number to theWord

        int i = 0;                                   // counter = 0
        String nextWord = "";                        // String to store the word
        for (String w : secondWord.keySet()) {       // Loop through the secondWord map, only the keys
            nextWord = w;                            // (temp) assign w to nextWord
            if (i == theWord) {                       // if i == the random number generated and assigned to theWord
                return nextWord;                     // Return the word
            }
            i++;                                     // If not increment the counter and continue the loop
        }

        // // Alternatively, convert keys in secondWord to a list and get the word using the index
        // List<String> nextWordList = new ArrayList<>(secondWord.keySet());
        // int numWords = nextWordList.size();
        // Random random = new Random();
        // int theWordIndex = random.nextInt(numWords);
        // String nextWord = nextWordList.get(theWordIndex);

        return nextWord;

    }


    // GENERATE WITH HALLUCINATION FACTOR
    public String generateHallucination(String root, int numWords, float hallucinationLevel) {
        String theSentence = root;
        String currentWord = root;
        String nextWord = "";
        float awake = 1 - hallucinationLevel;
        // If hallucination = 0, awake = 1  ->  The next word will be the most likely word
        // If hallucination = 1, awake = 0  ->  The next word will be random

        for (int i = 0; i < numWords; i++){
            if (random.nextFloat() < awake) {
                nextWord = nextWord(currentWord);
            } else {
                nextWord = randomNextWord(currentWord);
            }

            if (nextWord.length() <= 0) {
                break;
            } else {
                theSentence += " " + nextWord;
                currentWord = nextWord;
            }
        }

        return theSentence;

    }
}
