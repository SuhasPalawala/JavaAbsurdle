// Name: Suhas Palawala
// Date: 11/7/2023

/*
This class is intended to replicate the game Absurdle, a game that works somewhat like Wordle,
but has some key differences. Like Wordle, the user can guess words and receive hints as to what
the final word could be through a pattern of colored emojis. But, Absurdle works differently in that 
it does not select a single word initially. It instead filters through a dictionary to prolong the game
as much as possible. Every turn, Absurdle compares potential options for what the final word could be
by determining which pattern of emojis displayed have the most potential words associated with them.
*/
import java.util.*;
import java.io.*;

public class Absurdle  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    // [[ ALL OF MAIN PROVIDED ]]
    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        List<String> contents = loadFile(new Scanner(new File("dictionary1.txt")));
        Set<String> words = pruneDictionary(contents, 5);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, 5);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // [[ PROVIDED ]]
    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // [[ PROVIDED ]]
    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // [[ PROVIDED ]]
    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    // TODO: Write your code here! 
    /*
    Behavior: This method goes through a collection of words and filters out the 
                words that match the length of the words the user specifies. 
    Exceptions: IllegalArgumentException: This exception is thrown when the size 
                of the word the user specifies is less than 1.
    Returns: Set<String>: a collection of strings filled with words that match the 
                        length the user specified
    Parameters: contents: a list of words extracted from a user-inputted file
                wordLength: how many characters the user wants to have in the target word
                            (length of the word)
    */
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if(wordLength < 1) {
            throw new IllegalArgumentException();
        }

        Set<String> usableWords = new HashSet<>();

        for(int i = 0; i < contents.size(); i++) {
            if(contents.get(i).length() == wordLength) {
                usableWords.add(contents.get(i));
            }
        }
        
        return (usableWords);
    }

    /*
    Behavior: This method takes a user-inputted guess and determines what pattern of 
              emojis is displayed to the user. Additionally, it determines what set 
              of words to keep for the next round of guessing.
    Exceptions: IllegalArgumentException: This exception is thrown when the length of 
                the word the user guesses does not equal the length of the word the 
                user initally specified. It is also thrown when the collection of words
                passed to the method is empty.
    Returns: String: the pattern of emojis that is outputted to the user
    Parameters: guess: the word the user inputted as a guess
                words: a collection of words the program considers as potential final words when producing
                        the pattern of emojis
                wordLength: how many characters the user wants to have in the final word
                            (length of the word)
                
    */
    public static String record(String guess, Set<String> words, int wordLength) {
        if((guess.length() != wordLength) || (words.isEmpty())) {
            throw new IllegalArgumentException();
        }
        Map<String, Set<String>> possibleChoices = new TreeMap<>();
        String theFeederVar = "";
        for(String currentWord: words) {
            theFeederVar = patternFor(currentWord, guess);
            if(possibleChoices.containsKey(theFeederVar)) {
                possibleChoices.get(theFeederVar).add(currentWord);
            } else {
                possibleChoices.put(theFeederVar, new HashSet<String>());
                possibleChoices.get(theFeederVar).add(currentWord);
            }
        }
        
        int largestNum = 0;
        String theBiggestKey = "";
        for(String currentKey: possibleChoices.keySet()) {
            if(possibleChoices.get(currentKey).size() > largestNum) {
                largestNum = possibleChoices.get(currentKey).size();
                words.clear();
                words.addAll(possibleChoices.get(currentKey));
                theBiggestKey = currentKey;
            }
        }

        return theBiggestKey;
    }

    /*
    Behavior: This method produces a pattern of emojis that is supposed to represent
                how close the guess the user inputs is to a potential target word
                that is passed to this method.
    Returns: String: the pattern of emojis that has been generated based on how similar 
                the two words that are passed to this method are
    Parameters: word: a potential target word from the user-inputted file that is passed
                 to the method guess: the word the user inputted as a guess
    */
    public static String patternFor(String word, String guess) {
        List<String> checkCharacters = new ArrayList<String>();
        for(int i = 0; i < guess.length(); i++) {
            checkCharacters.add(guess.substring(0 + i, 1 + i));
        }

        Map<Character, Integer> letterCounting = new TreeMap<>();
        char theKey = ' ';
        int currentValue = 0;
        for(int i = 0; i < checkCharacters.size(); i++) {
            theKey = word.charAt(i);
            if(!letterCounting.containsKey(theKey)) {
                letterCounting.put(theKey, 1);
            } else {
                currentValue = letterCounting.get(theKey);
                letterCounting.put(theKey, currentValue + 1);
            }
        }

        changeLetterToGreen(word, guess, checkCharacters, letterCounting);
        changeLetterToYellow(guess, checkCharacters, letterCounting);
        changeLetterToGray(checkCharacters);

        String returnValue = "";
        for(int i = 0; i < checkCharacters.size(); i++) {
            returnValue += checkCharacters.get(i);
        }

        return(returnValue);
    }

    /*
    Behavior: This method goes through the inputted list and changes the character that is both present
                in and located at the right position as the potential target word to a green square emoji.
    Parameters: word: a potential target word from the user-inputted file that is passed to the method
                guess: the word the user inputted as a guess
                checkCharacters: a collection that represents each letter of the guessed 
                word as an individual character
                letterCounting: a collection that tracks what characters and how many of 
                                each are present in the word from the user-inputted file
    */
    public static void changeLetterToGreen(String word, String guess, List<String> checkCharacters, Map<Character, Integer> letterCounting) {
        char theKey = ' ';
        int currentValue = 0;
        for(int i = 0; i < guess.length(); i++) {
            theKey = guess.charAt(i);

            if(checkCharacters.get(i).charAt(0) == word.charAt(i)) {
                currentValue = letterCounting.get(theKey);
                letterCounting.put(theKey, currentValue - 1);
                checkCharacters.set(i, GREEN);
            }
        }
    }

    /*
    Behavior: This method goes through the inputted list and changes the character that
             is present in but not located at the right position as the potential target 
             word to a yellow square emoji.
    Parameters: guess: the word the user inputted as a guess
                checkCharacters: a collection that represents each letter of the guessed
                word as an individual character
                letterCounting: a collection that tracks what characters and how many of 
                                each are present in the word from the user-inputted file
    */
    public static void changeLetterToYellow(String guess, List<String> checkCharacters, Map<Character, Integer> letterCounting) {
        char theKey = ' ';
        int currentValue = 0;
        for(int i = 0; i < guess.length(); i++) {
            theKey = guess.charAt(i);
            if(letterCounting.containsKey(theKey) && letterCounting.get(theKey) > 0 && !checkCharacters.get(i).equals(GREEN)) {
                checkCharacters.set(i, YELLOW);
                currentValue = letterCounting.get(theKey);
                letterCounting.put(theKey, currentValue - 1);
            }
        }
    }

    /*
    Behavior: This method goes through the inputted list and changes the character 
                that is not present in the potential target word to a gray square emoji.
    Parameters: checkCharacters: a collection that represents each letter of the guessed
                 word as an individual character
    */
    public static void changeLetterToGray(List<String> checkCharacters) {
        for(int i = 0; i < checkCharacters.size(); i++) {
            if(checkCharacters.get(i) != GREEN && checkCharacters.get(i) != YELLOW) {
                checkCharacters.set(i, GRAY);
            }
        }
    }
}