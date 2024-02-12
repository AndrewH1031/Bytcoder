//package src;

//A very generous thank you to the folks at:
//      https://stackoverflow.com/questions/13185727/reading-a-txt-file-using-scanner-class-in-java
//for teaching me how to read text from a file input, because I am not a perfect human being

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Pattern;

//NOTE: Code is currently able to properly lex project 1 sans letters and some comment rules, will be looking into this

public class Lexer {

    public static void main(String src) {

        ArrayList<Token> list = new ArrayList<>();
        File fileList = new File(src);

        int counter = 1; //Counts our token's position
        int programCounter = 1; //Counts the number of programs we've lexed through
        int lineCounter = 0;
        int errors = 0; //Number of errors we get when parsing through our strings
        
     
        String stringolon = ""; //char to grab our current soon-to-be token (one at a time) from the file
        String commentEnd = "\\*/";
        String compareLetters = "[a-zA-Z]";

        boolean inAComment = false;
        boolean inAString = false;
        boolean isLetter = false;
        boolean itsABracket = false;

        char symbolon = ' ';
        

        try {
        //Basic list stuff to handle our input and token dump
        Scanner tokenList = new Scanner(fileList);

            System.out.println();
            System.out.println("Lexing program " + programCounter + "...");
            while(tokenList.hasNextLine()) {
                //System.out.println("IOU one lexer");
                String string = tokenList.nextLine();
                //System.out.println(string.length());
                stringolon = ""; //Resets stringolon every line
                lineCounter++;

                for(int i = 0; i < string.length(); i++) {
                    symbolon = ' '; //Resets symbolon every character
                    stringolon = stringolon + string.charAt(i);
                    symbolon = string.charAt(i);
                    
                    //System.out.println(symbolon);
                    //System.out.println(stringolon);
                    //System.out.println(string);
                    
                    if(inAComment == true) {
                        if(Pattern.matches(commentEnd, stringolon)) {
                            //System.out.println("Comments mode deactivated!"); - use this to test
                            inAComment = false;
                        }
                        //Will update this as soon as I get comments working properly
                        /*if(string.length() == i + 1) {
                            System.out.println("ERROR LEXER - Error: " + lineCounter + " : " + counter + " Comment Never Closed");
                        }*/
                    }
                    else {
                    //Switch statement to determine what we should do with our symbol tokens
                    //Vaguely follows the grammar order from the project 1 grammar.pdf
                    switch(symbolon) {
                        case '$':
                        System.out.println("DEBUG LEXER - EOP [ $ ] found at position (" + lineCounter + " : " + counter + ") - Program " + programCounter);
                            counter = 1;
                            lineCounter = 1;
                            programCounter++;
                            stringolon = "";
                            symbolon = ' ';
                            if(itsABracket == true) {
                                System.out.println("ERROR Lexer - Bracket Statement Never Closed");
                                errors++;
                            }
                            if(errors == 0) {
                                System.out.println("Lexing completed with " + errors + " errors recorded");
                            }
                            if(errors > 0) {
                                System.out.println("Lexer failed with " + errors + " errors recorded");
                                errors = 0;
                            }
                            if(tokenList.hasNextLine()) {
                                System.out.println();
                                System.out.println("Lexing program " + programCounter + "...");
                            }
                        break;
                        case '{':
                            if(string.length() > 1 && string.length() == i + 1) {
                                System.out.println("ERROR Lexer - ERROR: Bracket Open Statement At Invalid Position");
                                errors++;
                            }
                            else {
                                handleToken(list, "OPEN_BLOCK", "{", lineCounter, counter, programCounter);
                                counter++;
                                itsABracket = true;
                                stringolon = "";
                            }
                            
                        break;
                        case '}':
                            handleToken(list, "CLOSE_BLOCK", "}", lineCounter, counter, programCounter);
                            counter++;
                            itsABracket = false;
                            stringolon = "";
                        break;
                        case '(':
                            handleToken(list, "OPEN_PAREN", "(", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                        break;
                        case ')':
                            handleToken(list, "CLOSE_BLOCK", ")", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                        break;
                        case '=':
                            handleToken(list, "ASSIGNOP", "=", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                        break;
                        case '\'':
                            handleToken(list, "CHAR", "\'", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                            //Activate inAString
                        break;
                        case '\"':
                            handleToken(list, "CHAR", "\"", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                            //Activate inAString
                        break;
                        case '+':
                            handleToken(list, "INTOP", "+", lineCounter, counter, programCounter);
                            counter++;
                            stringolon = "";
                        break;
                        case '0': //testing
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
                        case ' ':
                            //handleToken(list, "SPACE", " ", lineCounter, counter, programCounter);
                            if(string.length() == i + 1) {
                                System.out.println("WARNING Lexer - WARNING: Whitespace Detected At Invalid Position");
                            }
                            counter++;
                            stringolon = "";
                        break;
                        
                    }
                    //Switch statement to handle our string inputs
                    switch(stringolon) {
                        case "string":
                            handleToken(list, "TYPE", "string", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "int":
                            handleToken(list, "TYPE", "int", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "print":
                            handleToken(list, "PRINTSTATEMENT", "print", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "ID":
                            handleToken(list, "ID", "id", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "while":
                            handleToken(list, "WHILESTATEMENT", "while", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "if":
                            handleToken(list, "IFSTATEMENT", "if", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "boolean":
                            handleToken(list, "TYPE", "bool", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "true":
                            handleToken(list, "BOOLVAL", "true", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "false":
                            handleToken(list, "BOOLVAL", "false", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "==":
                            handleToken(list, "BOOLOP", "==", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case "!=":
                            handleToken(list, "BOOLOP", "!=", lineCounter, counter, programCounter);
                            stringolon = "";
                        break;
                        case " ":
                            //don't tokenize this
                            stringolon = "";
                        break;
                        case "/*":
                            if(inAComment == false) {
                                //System.out.println("COMMENTS MODE ACTIVATED!!!"); - use this to test
                                inAComment = true;
                            }
                        case "*/":
                            if(inAComment == false) {
                                System.out.println("ERROR Lexer - ERROR: Comment End Detected Before Start");
                                errors++;
                            } 
                        break;
                        /*default:
                        //Will re-add this after I finish moving things around
                        System.out.println("ERROR: " + lineCounter + " : " + counter + " - Unrecognized token");
                        errors++;
                        break;*/
                        }
                    }
                }
            }
        tokenList.close(); 
        }
    
        //Not entirely sure I even need this but I"m too scared to take it out
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
        System.out.println("DEBUG LEXER - " + tokenType + " [ " + tokenName + " ]" + "found at position (" + linePos + " : " + countPos + ") - Program " + progPos);
    }
}