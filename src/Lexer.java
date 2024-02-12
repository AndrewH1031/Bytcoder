//package src;

//A very generous thank you to the folks at:
//      https://stackoverflow.com/questions/13185727/reading-a-txt-file-using-scanner-class-in-java
//for teaching me how to read text from a file input, because I am not a perfect human being

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Pattern;

//NOTE: Code is currently able to properly lex project 1 sans letters, pretty good!

public class Lexer {

    public static void main(String src) {

        ArrayList<Token> list = new ArrayList<>();
        File fileList = new File(src); //This is what allows us to pass our test files to our scanner

        int counter = 1;
        int programCounter = 1;
        int lineCounter = 0;
        int errors = 0;
        int warnings = 0;
        
     
        String stringolon = ""; //String to grab our current soon-to-be tokens from the file
        String commentEnd = "\\*/";
        String compareLetters = "[a-zA-Z]";

        boolean inAComment = false;
        boolean inAString = false;
        boolean isLetter = false;
        boolean itsABracket = false;
        boolean foundEndComment = false;

        char symbolon = ' '; //Character to store the current input in the string, one at a time
        
        try {
        Scanner tokenList = new Scanner(fileList);

            System.out.println();
            System.out.println("Lexing program " + programCounter + "...");
            while(tokenList.hasNextLine()) {
                //System.out.println("IOU one lexer");
                String string = tokenList.nextLine();
                //System.out.println(string.length());
                stringolon = ""; //Resets stringolon every line
                lineCounter++;

                if(string.isEmpty()) {
                    System.out.println("WARNING LEXER - WARNING: Nothing Found on Line " + lineCounter);
                    warnings++;
                }

                for(int i = 0; i < string.length(); i++) {
                    symbolon = ' '; //Resets symbolon every character
                    stringolon = stringolon + string.charAt(i);
                    symbolon = string.charAt(i);
                    
                    //System.out.println(symbolon);
                    //System.out.println(stringolon);
                    //System.out.println(string);
                    
                    //If there's no end program symbol found at the end of our file input, throw a FATAL error
                    //Will print for the current line, working on optimizing this
                    if(!tokenList.hasNextLine() && string.length() == i + 1) {
                        if(string.charAt(i) != '$') { //Ensures it checks the absolute last character
                            System.out.println("ERROR LEXER - Fatal Error: " + lineCounter + " : " + counter + " Missing Block Statement for Current Program");
                            errors++;
                            break;
                        }
                    }

                    //If we're in a comment, check to see if there's a matching end comment token we can use
                    if(inAComment == true) {
                        if(string.charAt(i) == '*') {
                            foundEndComment = true;
                        }
                        else if(string.charAt(i) == '/') {
                            if(foundEndComment = true) {
                                //System.out.println("Comments mode deactivated!"); //- use this to test
                                inAComment = false;
                                foundEndComment = false;
                            }
                        }
                        if(string.length() == i + 1) {
                            System.out.println("ERROR LEXER - Error: " + lineCounter + " : " + counter + " Comment Never Closed");
                            //Force close the comment
                            inAComment = false;
                            errors++;
                        }
                    }
                    //If we're in a string, check to see if there's a matching set of quotes
                    else if(inAString == true) {
                        if((string.charAt(i) == '\"') || (string.charAt(i) == '\'')) {
                            //System.out.println("String mode deactivated!"); //- use this to test
                            inAString = false;
                        }
                        if(string.length() == i + 1) {
                            System.out.println("ERROR Lexer - ERROR: String Statement Never Closed");
                            errors++;
                            //Force close the string
                            inAString = false;
                        }
                    }
                    else {
                    //Switch statement to determine what we should do with our symbol tokens
                    //Vaguely follows the grammar order from the project 1 grammar.pdf
                    switch(symbolon) {
                        //Token to end the current process. This will IMMEDIATELY end the program regardless of what comes after it, clear any indexes and hand it off to the handleToken class
                        case '$':
                            System.out.println("DEBUG LEXER - EOP [ $ ] found at position (" + lineCounter + " : " + counter + ") - Program " + programCounter);
                            counter = 1;
                            lineCounter = 1;
                            programCounter++;
                            stringolon = "";
                            symbolon = ' ';
                            //If we've got an open bracket when the program is terminated, print an error
                            if(itsABracket == true) {
                                System.out.println("ERROR LEXER - Error: " + lineCounter + " : " + counter + " Bracket Statement Never Closed");
                                errors++;
                            }
                            //If we have no errors, great! Print a message saying we're done
                            if(errors == 0) {
                                System.out.println("Lexing completed with " + errors + " errors and " + warnings + " warnings recorded");
                                errors = 0;
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
                            handleToken(list, "OPEN_BLOCK", "{", lineCounter, counter, programCounter);
                            counter++;
                            itsABracket = true;
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
                        break;
                        //Assignment token
                        case '=':
                            handleToken(list, "ASSIGNOP", "=", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
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
                        //ID expression token
                        case "ID":
                            handleToken(list, "ID", "id", lineCounter, counter, programCounter);
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
                        //Boolean equals operand token
                        //Currently overshadowed by its little brother =, but I'm working on a fix
                        case "==":
                            handleToken(list, "BOOLOP", "==", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //Boolean does not equal operand token
                        //Also doesn't work. Again, still working on it.
                        case "!=":
                            handleToken(list, "BOOLOP", "!=", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        //Beginning string token
                        case "/*":
                            if(inAComment == false) {
                                //System.out.println("COMMENTS MODE ACTIVATED!!!"); - use this to test
                                inAComment = true;
                            }
                        //Ending string token
                        case "*/":
                            if(inAComment == false) {
                                System.out.println("WARNING LEXER - WARNING: " + lineCounter + " : " + counter + " Comment End Detected Before Start");
                                errors++;
                            } 
                        break;
                        default:
                        //Will re-add this after I finish moving things around
                        /*System.out.println("ERROR: " + lineCounter + " : " + counter + " - Unrecognized token");
                        errors++;
                        stringolon = "";
                        break;*/
                        }
                    }
                }
            }
        tokenList.close();
        }
    
        //Backup catch statement in case our file didn't show up, which should ideally be never
        catch (Exception noFile) {
            noFile.printStackTrace();
        }
    }

    public static void handleLetters(ArrayList<Token> list, String tokenType, String tokenName, int linePos, int countPos, int progPos) {
        //pass to handletoken class
    }

    public static void handleToken(ArrayList<Token> list, String tokenType, String tokenName, int linePos, int countPos, int progPos) {
        //System.out.println("Use this to DEBUG");
        list.add(new Token(tokenType, tokenName, linePos, countPos, progPos));
        System.out.println("DEBUG LEXER - " + tokenType + " [ " + tokenName + " ]" + " found at position (" + linePos + " : " + countPos + ") - Program " + progPos);
    }
}