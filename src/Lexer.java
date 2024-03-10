//A very generous thank you to the folks at:
//      https://stackoverflow.com/questions/13185727/reading-a-txt-file-using-scanner-class-in-java
//for teaching me how to read text from a file input, because I am not a perfect human being

import java.util.Scanner;
import java.io.File; //Import to take file input
import java.io.FileNotFoundException; //Import for catch string at the end of the class
import java.util.ArrayList; //Import to initialize our token array list, which we'll use to store the stuff we find
import java.util.regex.Pattern; //Import to match our letters with a matcher tool

public class Lexer {

    public static void main(String src) {

        Parser Parser = new Parser(); //Parser setup, this will come in handy later

        ArrayList<Token> list = new ArrayList<>(); //New ArrayList to handle our input
        File fileList = new File(src); //This is what allows us to pass our test files to our scanner, uses the src file which is where our Compiler.java file is located

        int counter = 1; //Keeps track of the current token's position in a line, increments as we iterate through
        int programCounter = 1; //Current program we're lexing through, increments every time we end a program with "$"
        int lineCounter = 0; //Current line we're on in the program, increments every time we reach the beginning of a string
        int errors = 0; //Amount of errors we get when lexing
        int warnings = 0; //Amount of warnings we get when lexing
        
        String stringolon = ""; //String to grab our current soon-to-be token from the file - WE MAY NOT NEED THIS IN THE FUTURE

        String compareLetters = "[a-zA-Z]"; //String to compare our valid keyword/statement/boolean/ID tokens

        boolean inAComment = false; //Determines if we're currently in a comment
        boolean inAString = false; //Determines if we're currently in a string
        boolean itsABracket = false; //Determines if we're in an open bracket statement
        boolean foundEndComment = false; //Determines if we've got the second half of the end comment token "*/"
        boolean donttouchtheEquals = false; //Used for Boolean != and == statements to conveniently omit the second = after we've already initialized it

        char symbolon = ' '; //Character to grab the current input in the string, one at a time
        char forward = ' '; //Character to check ahead one spot in our string for things such as boolean operands and comment endings
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

                //Iterates through the entirety of our current string
                for(int i = 0; i < string.length(); i++) {
                    symbolon = ' '; //Resets symbolon every character
                    stringolon = stringolon + string.charAt(i);
                    symbolon = string.charAt(i);

                    if(string.length() - 1 > i + 1) {
                        forward = string.charAt(i + 1);
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
                                counter++;
                                break;
                            }
                        }
                    }

                    //If we're in a comment, check to see if there's a matching end comment token we can use
                    //Works for now, will implement functionality with forward eventually
                    if(inAComment == true) {
                        counter++;
                        switch(symbolon) {
                            //We've got one half of the token we want
                            case '*':
                                foundEndComment = true;
                                counter++;
                            break;
                            //Here's the other half we can find in another loop, but we still need another step to verify if we obtained the first half before
                            case '/':
                                //If we do have the other half, we can safely end the comment and reset foundEndComment
                                if(foundEndComment = true) {
                                    //System.out.println("Comments mode deactivated!"); //- use this to test
                                    inAComment = false;
                                    foundEndComment = false;
                                    counter++;
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
                            //Add our closing symbol as a token
                            handleToken(list, "CLOSESTRING", "\"", lineCounter, counter, programCounter);
                            counter++;
                            
                        }

                        //If our current character is a valid character (i.e. a letter or single/double quotes) then print it out
                        else if((stringolon.matches(("[a-z]+")))) {
                            if(symbolon == ' ') {
                                //Do nothing, we want to process whitespace for now
                                //HAHAHAHA I LIED!!!! We want to process it ONLY if it's in a string!
                                handleToken(list, "CHARSPACE", " ", lineCounter, counter, programCounter);
                                counter++;
                                
                            }
                            else {
                                //Consider every valid token in a string as a lowercase letter and handle it
                                handleToken(list, "CHAR", (string.charAt(i) + ""), lineCounter, counter, programCounter);
                                counter++;
                            }
                        }
                        //If it's not a valid character in the string, print an error
                        else {
                            System.out.println("ERROR LEXER - Error: " + lineCounter + " : " + counter + " Invalid Character Type Included In String");
                            errors++;
                            counter++;
                        }
                        //If we're at the end of our current line and we don't have our quote pair token (AND our current token isn't the pair token), print an error
                        if((string.length() == i + 1) && (string.charAt(i) != '\"')) {
                            System.out.println("WARNING LEXER - Warning: " + lineCounter + " : " + counter + " String Never Closed; Process Auto-Closed String Statement");
                            warnings++;
                            //Force close the string
                            inAString = false;
                            counter++;
                        }
                        stringolon = "";
                        counter++;
                    }

                    else {
                        //Switch statement to determine what we should do with our symbol tokens
                        //Follows the grammar order from the project 1 grammar.pdf
                        switch(symbolon) {
                            //Token to end the current process. This will IMMEDIATELY end the program regardless of what comes after it, clear any indexes and hand it off to the handleToken class
                            case '$':
                                handleToken(list, "EOP_BLOCK", "$", lineCounter, counter, programCounter);
                                counter = 1;
                                stringolon = "";
                                symbolon = ' ';
                                programCounter++;
                                
                                //If we've got an open bracket when the program is terminated, print an error
                                if(itsABracket == true) {
                                    System.out.println("ERROR LEXER - Error: " + lineCounter + " : " + counter + " Bracket Statement Never Closed");
                                    errors++;
                                    warnings = 0;
                                }
                                //If we have no errors, great! Print a message saying we're done and hand it off to the Parser
                                if(errors == 0) {
                                    System.out.println("Lexing completed with " + errors + " errors and " + warnings + " warnings recorded");
                                    errors = 0;
                                    warnings = 0;
                                    Parser.main(list);
                                    list.clear();
                                }
                                //If we have errors, we want to show how many we have, as well as point out that our lexer has failed
                                else if(errors > 0) {
                                    System.out.println("Lexer failed with " + errors + " errors and " + warnings + " warnings recorded");
                                    errors = 0;
                                    System.out.println("PARSER: Skipped Parsing due to LEXER errors");
                                    System.out.println("CST for program " + programCounter + ": skipped CST due to LEXER errors");
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
                                handleToken(list, "CLOSE_PAREN", ")", lineCounter, counter, programCounter);
                                counter++;
                                stringolon = "";
                            break;
                            //Assignment token
                            case '=':
                                if(donttouchtheEquals == true) {
                                    donttouchtheEquals = false;
                                    stringolon = "";
                                    counter++;
                                    if(forward == '=') {
                                        //If we have more than one boolean operand statement in a row (i.e. something like !===), then we want to print a warning saying so
                                        System.out.println("WARNING LEXER - Warning: " + lineCounter + " : " + counter + " BoolOp Statement Out of Bounds");
                                        warnings++;
                                        donttouchtheEquals = true;
                                        counter++;
                                    }
                                }
                                //If we've got a == operand, add it as a token
                                else if(forward == '=') {
                                    handleToken(list, "BOOLOP", "==", lineCounter, counter, programCounter);
                                    counter++;
                                    stringolon = "";
                                    donttouchtheEquals = true;
                                }
                                //If it's just a = by itself, say so
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
                                handleToken(list, "OPENSTRING", "\'", lineCounter, counter, programCounter);
                                counter++;
                                stringolon = "";
                                inAString = true;
                            break;
                            //Open quotes indicator
                            case '\"':
                                handleToken(list, "OPENSTRING", "\"", lineCounter, counter, programCounter);
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
                                    //Sets a boolean value for the program to decide what happens to the upcoming = symbol
                                    donttouchtheEquals = true;
                                    counter++;
                                }
                                //Otherwise, we're not meant to encounter this by itself, so we print an error
                                else {
                                    System.out.println("ERROR LEXER - ERROR: " + lineCounter + " : " + counter + " Invalid Character \"!\" Detected Outside of BoolOp Statement");
                                    errors++;
                                    stringolon = "";
                                    counter++;
                                }
                                break;
                            //We should never be encountering this outside of comments, so print an error if we encounter it
                            case '*':
                                System.out.println("ERROR LEXER - ERROR: " + lineCounter + " : " + counter + " Invalid Character \"*\" Detected Outside of Comment Statement");
                                errors++;
                                stringolon = "";
                                counter++;
                            break;
                            case '/':
                                if(forward == '*') {
                                    //System.out.println("comments mode!!!"); // - use this to test
                                    inAComment = true;
                                }
                                else {
                                    System.out.println("ERROR LEXER - ERROR: " + lineCounter + " : " + counter + " Invalid Character \"/\" Detected Outside of Comment Statement");
                                    errors++;
                                    stringolon = "";
                                    counter++;
                                }
                            break;
                            //Space token
                            //We DO NOT want to tokenize this yet, it will cause too much noise in the lexer
                            case ' ':
                                //handleToken(list, "SPACE", " ", lineCounter, counter, programCounter);
                                if(string.length() == i + 1) {
                                    //If there's some whitespace left over at the end of the current string (or the current string is nothing but whitespace), then throw a warning
                                    System.out.println("WARNING LEXER - WARNING: " + lineCounter + " : " + counter + " Whitespace Detected at Invalid Position");
                                    errors++;
                                }
                                counter++;
                                stringolon = "";
                            break;
                            default:
                            //Errors to encompass invalid tokens, which we will not be including in our grammar
                            if((symbolon == '@') || (symbolon == '>') || (symbolon == '<') || (symbolon == '|') || (symbolon == '[') || (symbolon == ']') || (symbolon == '%') || (symbolon == '.') || (symbolon == '&') || (symbolon == '#') || (symbolon == '?')) {
                                System.out.println("ERROR LEXER - ERROR:" + lineCounter + " : " + counter + " Unrecognized Token");
                                errors++;
                                stringolon = "";
                                counter++;
                            }
                        }
                    }
                        

                        //Function to compare our individual letters. This sets an "endPointer" value and loops through our string, slowly narrowing down the options until we either find something or it stops
                        if(Pattern.matches(compareLetters, stringolon)) {
                            int startPointer = i; //might not need this
                            int endPointer = i + 1; //endPointer value to iterate throught the string one at a time
                            String longestMatch = null; //Stores our longest match as a string to be compared later

                            //Loops through our string to see if we've got a match for any outlying characters
                            //If substring matches from i to current endpointer, add keyword token and increment i
                            while(endPointer <= string.length()) {
                                String substring = string.substring(startPointer, endPointer);
                                //System.out.println(substring);
                                //System.out.println(longestMatch);

                                //Encompasses type declarations and statements
                                if(Pattern.matches("while", substring) || Pattern.matches("int", substring) || Pattern.matches("print", substring) || Pattern.matches("string", substring) || Pattern.matches("if", substring)) {
                                    if(Pattern.matches("int", substring)) {
                                        handleToken(list, "TYPEINT", "int", lineCounter, counter, programCounter);
                                        i = i + 2;
                                        longestMatch = substring;
                                    }
                                    else if(Pattern.matches("while", substring)) {
                                        handleToken(list, "WHILESTATEMENT", "while", lineCounter, counter, programCounter);
                                        i = i + 4;
                                        longestMatch = substring;
                                    }
                                    if(Pattern.matches("print", substring)) {
                                        handleToken(list, "PRINTSTATEMENT", "print", lineCounter, counter, programCounter);
                                        i = i + 4;
                                        longestMatch = substring;
                                    }
                                    if(Pattern.matches("string", substring)) {
                                        handleToken(list, "TYPESTRING", "string", lineCounter, counter, programCounter);
                                        i = i + 5;
                                        longestMatch = substring;
                                    }
                                    if(Pattern.matches("if", substring)) {
                                        handleToken(list, "IFSTATEMENT", "if", lineCounter, counter, programCounter);
                                        i = i + 1;
                                        longestMatch = substring;
                                    }
                                    //System.out.println(longestMatch);
                                }

                                //Encompasses boolean values and operands
                                if(Pattern.matches("boolean", substring) || Pattern.matches("true", substring) || Pattern.matches("false", substring)) {
                                    if(Pattern.matches("boolean", substring)) {
                                        handleToken(list, "TYPEBOOL", "bool", lineCounter, counter, programCounter);
                                        i = i + 6;
                                        longestMatch = substring;
                                    }
                                    if(Pattern.matches("true", substring)) {
                                        handleToken(list, "BOOLVAL", "true", lineCounter, counter, programCounter);
                                        i = i + 3;
                                        longestMatch = substring;
                                    }
                                    if(Pattern.matches("false", substring)) {
                                        handleToken(list, "BOOLVAL", "false", lineCounter, counter, programCounter);
                                        i = i + 4;
                                        longestMatch = substring;
                                    }
                                }
                                endPointer++;
                            }
                            //If there's a match in our original regex statement but it doesn't match anything else, it's a character
                            if(longestMatch == null) {
                                //Making sure we don't invalidate our grammar rules with pesky Uppercases
                                if(Character.isUpperCase(symbolon)) {
                                    System.out.println("WARNING LEXER - WARNING: " + lineCounter + " : " + counter + " Uppercase Character " + symbolon + " Not Supported");
                                    warnings++;
                                }
                                else {
                                handleToken(list, "ID", string.charAt(i) + "", lineCounter, counter, programCounter);
                                }
                            }
                            //System.out.println(startPointer);
                            //System.out.println(endPointer);
                            stringolon = "";
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