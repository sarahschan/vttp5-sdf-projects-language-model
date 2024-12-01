import java.io.Console;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public class Main {

    public static void main(String[] args) throws IOException {
        
        String file = args[0];
        FileReader fileReader = new FileReader(file);
        LanguageModel languageModel = new LanguageModel(fileReader);
        languageModel.buildModel();

        // Print model to check:
        languageModel.printModel();


        Console cons = System.console();

        while (true) {

            String startWord = cons.readLine("> Start word: ");
            int numWords = Integer.parseInt(cons.readLine("> Number of words: "));

            // Greedy algo (exploitation) sentence generator
            // String generatedSentence = languageModel.generateGreedy(startWord, numWords);
            // System.out.println("\nGenerated sentence (exploitation): " + generatedSentence + "\n");


            // Exploration algo sentence generator
            // String generatedSentence = languageModel.generateExplore(startWord, numWords);
            // System.out.println("\nGenerated sentence (random): " + generatedSentence);


            // Sentence generator with hallucination factor
            float hallucinationLevel = Float.parseFloat(cons.readLine("> Hallucination level (0 - 1): "));
            String generatedSentence = languageModel.generateHallucination(startWord, numWords, hallucinationLevel);
            System.out.println("\nGenerated sentence (hallucination): " + generatedSentence);
        }
        


    }

}