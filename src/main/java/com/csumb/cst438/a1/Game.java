package com.csumb.cst438.a1;

import static com.csumb.cst438.a1.MyHttpServer.RESOURCE_DIR;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Game contains logic to play hangman game.
 * Includes code to generate new words,
 * process user guess, determine win or lose.
 * @author David
 * @version 1.0
 */
public class Game {
 
    private Random generator;  
    private int state;  
	           //  1 means no incorrect guesses, or start of game
                   //  2 means 1 incorrect guess.
                   //  3 means 2 incorrect guesses.
                   //  4 means 3 incorrect guesses.
                   //  5 means 4 incorrect guesses.
                   //  6 means 5 incorrect guesses.
	           //  7 means 6 incorrect guesses, user has lost game
    
    private String word;   // the word to be guessed 
    private StringBuffer displayWord; // part of the word (if any) to show user
    private ArrayList<String> wordlist;  // list of words
    
    public Game(){
        /*URL url = getClass().getResource("wordlist.txt");
        File wordFile = new File(url.getPath());
        
        if(wordFile.exists()) {
            Scanner s = new Scanner(wordFile);
            ArrayList<String> wordList = new ArrayList<String>();
            
            while(s.hasNext()) {
            wordList.add(s.next());
            }
        }
        
        
        s.close();
        
        Random randomGen = new Random();
        int randomIndex = randomGen.nextInt(wordList.size());*/
        
        
        //simply add randomWord()
        state=1;
        wordlist=null;
        generator = new Random();
        word=randomWord();
        createDisplayWord();

    }
    
    public int getState(){
        return state;
    }
    
    public String getWord(){
        return word;
    }
    
    public String getDisplayWord(){
        return displayWord.toString();
    }
    
    public void startNewGame() {
        state = 1;
        //add randomWord()
        word = randomWord();
        createDisplayWord();
        
    }
    
    /*
     * process a user guess return page saying game over user win or lost, or
     * return page with updated game display
     * return 0 = continue game, good guess
     *        1 = good guess.  Win game. 
     *        2 = bad guess.  continue game
     *        3 = bad guess.  Lost game.
     */
    public int playGame(char guess) {
            boolean correctGuess = updateDisplayWord(guess);
            if (correctGuess==false) { 
                state++;
                if (state==7) {
                    // user has lost game
                    return 3;
                } else {
                    return 2; // bad guess, continue
                }
            } else if ( displayWord.indexOf("_") >= 0) {
               return 0; // continue game, with good guess
            } else {
               return 1; // all characters has been guessed, user has won game.
            }
    }
    /**
     * update display word to show any occurrences of guess
     * There is a space character between each letter, and 
     * any letters remaining to be guessed are displayed as underscore
     * @param guess
     * @return true if at least one match, false if guess is incorrect
     */
    private boolean updateDisplayWord(char guess){
        boolean correctGuess = false;
        for (int i=0; i<word.length(); i++) {
            if (word.charAt(i)==guess){
                    displayWord.replace(2*i, 2*i+1, Character.toString(guess)); 
                    correctGuess=true;
                }
        }
        return correctGuess;
        
    }
    /**
     * Create the display version of word with letters replaced by
     * underscore for letter remaining to be guessed
     * @param word
     * @return 
     */
    private void createDisplayWord(){
        displayWord = new StringBuffer("_");
        for (int i=1; i<word.length(); i++) {
            displayWord.append(" _");
        }
   
    }
    
       /**
     * generate random word.
     * 
     * @return a word from the list
     */
    private String randomWord() {
	try {
            if (wordlist == null) {
		wordlist = new ArrayList<String>();
		// read in word list
		Scanner infile = new Scanner(new File(RESOURCE_DIR + "wordlist.txt"));
		while (infile.hasNextLine()) {
                    wordlist.add(infile.nextLine());
		}
		infile.close();
            }
            int t = generator.nextInt(wordlist.size());
            return wordlist.get(t);

        } catch (Exception e) {
            System.out.println("Error randomWord: reading wordlist. " + e.getMessage());
            System.exit(0);
            return null; // to keep compiler happy
	}
    }
}
