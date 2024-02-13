//package src;

//A very generous thank you to the folks at:
//      https://stackoverflow.com/questions/13185727/reading-a-txt-file-using-scanner-class-in-java
//for teaching me how to read text from a file input, because I am not a perfect human being

import java.util.Scanner;
import java.io.File; //Import to take file input
import java.io.FileNotFoundException; //Import for catch string at the end of the class
import java.util.ArrayList;
import java.util.regex.Pattern; //Import to match our letters/digits with a matcher tool

//NOTE: Code is currently able to properly lex project 1 sans some letters, pretty good!

public class Lexer {

    public static void main(String src) {

        ArrayList<Token> list = new ArrayList<>(); //New ArrayList to handle our input
        File fileList = new File(src); //This is what allows us to pass our test files to our scanner, uses the src file which is where our Compiler.java file is located

        int counter = 1; //Keeps track of the current token's position in a line, increments as we iterate through
        int programCounter = 1; //Current program we're lexing through, increments every time we end a program with "$"
        int lineCounter = 0; //Current line we're on in the program, increments every time we reach the beginning of a string
        int errors = 0; //Amount of errors we get when lexing
        int warnings = 0; //Amount of warnings we get when lexing
        
        String stringolon = ""; //String to grab our current soon-to-be token from the file

        //Not too sure what these do, I think I had them set up for something with letter matching...
        //In any case, I'll let them stay here for now, I might need them later on
        String invalidToken = "[\s]";
        String compareLetters = "[a-zA-Z]+";

        boolean inAComment = false; //Determines if we're currently in a comment
        boolean inAString = false; //Determines if we're currently in a string
        boolean itsABracket = false; //Determines if we're in an open bracket statement
        boolean foundEndComment = false; //Determines if we've got the second half of the end comment token "*/"
        boolean donttouchtheEquals = false; //Used for Boolean != and == statements to conveniently omit the second = after we've already initialized it

        char symbolon = ' '; //Character to store the current input in the string, one at a time
        char forward = ' ';
        char forwarder = ' ';
        
        //Try statement, we NEED this for our file input to work
        try {
        Scanner tokenList = new Scanner(fileList);

            System.out.println();
            System.out.println("Lexing program " + programCounter + "...");

            while(tokenList.hasNextLine()) {
                //System.out.println(string.length());

                String string = tokenList.nextLine();

                stringolon = ""; //Resets stringolon every line
                lineCounter++; //Increments line counter when we get to a new line

                if(string.isEmpty()) {
                    System.out.println("WARNING LEXER - WARNING: Nothing Found on Line " + lineCounter);
                    warnings++;
                }

                for(int i = 0; i < string.length(); i++) {
                    symbolon = ' '; //Resets symbolon every character
                    stringolon = stringolon + string.charAt(i);
                    symbolon = string.charAt(i);

                    if(string.length()-1 > i + 1) {
                        forward = string.charAt(i+1);
                    }
                    
                    //System.out.println(symbolon);
                    //System.out.println(stringolon);
                    //System.out.println(string);
                    
                    //If there's no end program symbol found at the end of our file input, throw a FATAL error
                    //Will print for the current line, working on optimizing this
                    if(string.length() == i + 1) {
                        if(!tokenList.hasNextLine()) {
                            if(string.charAt(i) != '$') { //Ensures it checks the absolute last character
                                System.out.println("ERROR LEXER - Fatal Error: " + lineCounter + " : " + counter + " Missing Block Statement for Current Program");
                                errors++;
                                break;
                            }
                        }
                    }

                    //If we're in a comment, check to see if there's a matching end comment token we can use
                    if(inAComment == true) {
                        counter++;
                        switch(symbolon) {
                            //We've got one half of the token we want
                            case '*':
                                foundEndComment = true;
                            break;
                            //Here's the other half we can find in another loop, but we still need another step to verify if we obtained the first half before
                            case '/':
                                //If we do have the other half, we can safely end the comment and reset foundEndComment
                                if(foundEndComment = true) {
                                    //System.out.println("Comments mode deactivated!"); //- use this to test
                                    inAComment = false;
                                    foundEndComment = false;
                                }
                            break;
                        }
                        //Don't throw a comment end error since comments should wrap around
                    }
                    //If we're in a string, check to see if there's a matching set of quotes
                    else if(inAString == true) {
                        if((string.charAt(i) == '\"') || (string.charAt(i) == '\'')) {
                            //System.out.println("String mode deactivated!"); //- use this to test
                            inAString = false;
                        }

                        if((stringolon.matches(("[a-z\\s'\"]+")))) {
                            if(symbolon == ' ') {
                                //Do nothing, we want to process whitespace for now
                            }
                            else {
                                handleToken(list, "ID", (string.charAt(i) + ""), lineCounter, counter, programCounter);
                            }
                        }
                        else {
                            System.out.println("ERROR LEXER - Error: " + lineCounter + " : " + counter + " Invalid Character Type Included In String");
                            errors++;
                        }
                        //Much like inAComment, if we're at the end of our current line and we don't have our quote pair token, print an error
                        if(string.length() == i + 1) {
                            System.out.println("WARNING LEXER - Warning: " + lineCounter + " : " + counter + " String Never Closed; Process Auto-Closed String Statement");
                            warnings++;
                            //Force close the string
                            inAString = false;
                        }
                        stringolon = "";
                        counter++;
                    }
                    else {
                    //Switch statement to determine what we should do with our symbol tokens
                    //Vaguely follows the grammar order from the project 1 grammar.pdf
                    switch(symbolon) {
                        //Token to end the current process. This will IMMEDIATELY end the program regardless of what comes after it, clear any indexes and hand it off to the handleToken class
                        case '$':
                            handleToken(list, "EOP_BLOCK", "$", lineCounter, counter, programCounter);
                            counter = 1;
                            lineCounter = 1;
                            stringolon = "";
                            symbolon = ' ';
                            programCounter++;
                            //If we've got an open bracket when the program is terminated, print an error
                            if(itsABracket == true) {
                                System.out.println("ERROR LEXER - Error: " + lineCounter + " : " + counter + " Bracket Statement Never Closed");
                                errors++;
                                warnings = 0;
                            }
                            //If we have no errors, great! Print a message saying we're done
                            if(errors == 0) {
                                System.out.println("Lexing completed with " + errors + " errors and " + warnings + " warnings recorded");
                                errors = 0;
                                warnings = 0;
                            }
                            //If we have errors, we want to show how many we have, as well as point out that our lexer has failed
                            else if(errors > 0) {
                                System.out.println("Lexer failed with " + errors + " errors and " + warnings + " warnings recorded");
                                errors = 0;
                            }
                            //If there's still lines in the input file (i.e. there's still programs to lex through), print a message saying so
                            if(tokenList.hasNextLine()) {
                                System.out.println();
                                System.out.println("Lexing program " + programCounter + "...");
                            }
                        break;
                        //Open bracket token
                        case '{':
                        //Basic steps to the lexer processing:
                            //Passes token to the handleToken class for processing
                            handleToken(list, "OPEN_BLOCK", "{", lineCounter, counter, programCounter);
                            //Increments the counter
                            counter++;
                            //Sets itsABracket to true, indicating we're currently in a bracket statement
                            itsABracket = true;
                            //Resets stringolon
                            stringolon = "";
                        break;
                        //Close bracket token
                        case '}':
                            handleToken(list, "CLOSE_BLOCK", "}", lineCounter, counter, programCounter);
                            counter++;
                            itsABracket = false;
                            stringolon = "";
                            break;
                        //Open parentheses token
                        case '(':
                            handleToken(list, "OPEN_PAREN", "(", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                        break;
                        //Close parentheses token
                        case ')':
                            handleToken(list, "CLOSE_BLOCK", ")", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                            //Error here for close paren before open
                        break;
                        //Assignment token
                        case '=':
                            if(donttouchtheEquals == true) {
                                System.out.println("nope!");
                                donttouchtheEquals = false;
                                stringolon = "";
                            }
                            else if (forward == '=') {
                                handleToken(list, "BOOLOP", "==", lineCounter, counter, programCounter);
                                donttouchtheEquals = true;
                            }
                            else {
                                handleToken(list, "ASSIGNOP", "=", lineCounter, counter, programCounter);
                                counter++;
                                stringolon = "";
                                donttouchtheEquals = false;
                            }
                            counter++;
                        break;
                        //Open quotes indicator
                        case '\'':
                            //handleToken(list, "CHAR", "\'", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                            inAString = true;
                        break;
                        //Open quotes indicator
                        case '\"':
                            //handleToken(list, "CHAR", "\"", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                            inAString = true;
                        break;
                        //IntoOp token
                        case '+':
                            handleToken(list, "INTOP", "+", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                        break;
                        case '0': //Numbers tokens, I'll probably think of a better way to detect these than stuffing them here
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "0", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '1':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "1", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '2':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "2", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '3':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "3", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '4':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "4", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '5':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "5", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '6':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "6", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '7':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "7", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '8':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "8", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '9':
                            if(Character.isDigit(string.charAt(i))) {
                                handleToken(list, "NUM", "9", lineCounter, counter, programCounter);
                            }
                            counter++;
                            stringolon = "";
                        break;
                        case '!':
                            //If we've got a BoolOp statement lined up, we can see the upcoming = symbol with our forward pointer
                            if(forward == '=') {
                                handleToken(list, "BOOLOP", "!=", lineCounter, counter, programCounter);
                                stringolon = "";
                                donttouchtheEquals = true;
                            }
                            //Otherwise, we're not meant to encounter this by itself, so we print an error
                            else {
                                System.out.println("ERROR LEXER - ERROR:" + lineCounter + " : " + counter + " Invalid Character \"!\" Detected Outside of BoolOp Statement");
                                errors++;
                                stringolon = "";
                            }
                        //Space token
                        //We DO NOT want to tokenize this yet, it will cause too much noise in the lexer
                        case ' ':
                            //handleToken(list, "SPACE", " ", lineCounter, counter, programCounter);
                            if(string.length() == i + 1) {
                                System.out.println("WARNING LEXER - WARNING: " + lineCounter + " : " + counter + " Whitespace Detected at Invalid Position");
                                errors++;
                            }
                            counter++;
                            stringolon = "";
                        break;
                        
                    }
                    //Switch statement to handle our string inputs
                    switch(stringolon) {
                        //String expression token
                        case "string":
                            handleToken(list, "TYPE", "string", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //Int expression token
                        case "int":
                            handleToken(list, "TYPE", "int", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //Print expression token
                        case "print":
                            handleToken(list, "PRINTSTATEMENT", "print", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //While expression token
                        case "while":
                            handleToken(list, "WHILESTATEMENT", "while", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //If expression token
                        case "if":
                            handleToken(list, "IFSTATEMENT", "if", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //Boolean expression token
                        case "boolean":
                            handleToken(list, "TYPE", "bool", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //Boolean value token
                        case "true":
                            handleToken(list, "BOOLVAL", "true", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //Boolean value token
                        case "false":
                            handleToken(list, "BOOLVAL", "false", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "/*":
                            if(inAComment == false) {
                                //System.out.println("COMMENTS MODE ACTIVATED!!!"); - use this to test
                                inAComment = true;
                            }
                        //Token to NOT end our comment string - this part is handled by our inAComment function!
                        //Instead prints an error, since there's no way to get here if you formatted everything correctly
                        case "*/":
                            if(inAComment == false) {
                                System.out.println("WARNING LEXER - WARNING: " + lineCounter + " : " + counter + " Comment End Detected Before Start");
                                warnings++;
                            } 
                        break;
                        }
                    }
                }
            }
        //Close our file input
        tokenList.close();
        }
    
        //Backup catch statement in case our file didn't show up, which should ideally be never
        catch (Exception noFile) {
            noFile.printStackTrace();
        }
    }

    //handleToken array, which will neatly tokenize and print whatever token is passed to it
    public static void handleToken(ArrayList<Token> list, String tokenType, String tokenName, int linePos, int countPos, int progPos) {
        //System.out.println("Use this to DEBUG");
        list.add(new Token(tokenType, tokenName, linePos, countPos, progPos));
        System.out.println("DEBUG LEXER - " + tokenType + " [ " + tokenName + " ]" + " found at position (" + linePos + " : " + countPos + ") - Program " + progPos);
    }
}