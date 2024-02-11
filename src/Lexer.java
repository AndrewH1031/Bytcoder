//package src;

//A very generous thank you to the folks at:
//      https://stackoverflow.com/questions/13185727/reading-a-txt-file-using-scanner-class-in-java
//for teaching me how to read text from a file input, because I am not a perfect human being

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

//NOTE: Code is currently still very incomplete, just messing around



public class Lexer {



    public static void main(String src) {

        ArrayList<Token> list = new ArrayList<>();
        File fileList = new File(src);

        int counter = 1; //Counts our token's position
        int programCounter = 1; //Counts the number of programs we've lexed through
        int lineCounter = 1;
        
     
        String token = ""; //char to grab our current soon-to-be token (one at a time) from the file
        String string = ""; //string to grab the current whole string (not just one token), which will help us with our pointer

        boolean inAComment = false;
        boolean inAString = false;

        //for(int i = 0; i < tokenList.length(); i++) { //Won"t work, currently figuring out another method besides scanner


        try {
        //Basic list stuff to handle our input and token dump
        Scanner tokenList = new Scanner(fileList);

            System.out.println();
            System.out.println("Lexing program " + programCounter + "...");
            while (tokenList.hasNextLine()) {
                //System.out.println("IOU one lexer");
                string = tokenList.nextLine();
                token = ""; //dump this once you have the full thing set up
                lineCounter++;

                
                
                for (int i = 0; i < string.length(); i++) {
                    token = token + string.charAt(i);
                    
                    //System.out.println(token);
                    //System.out.println(string);

                    //Switch statement to determine what we should do with our token
                    //Vaguely follows the grammar order from the project 1 grammar.pdf
                    switch(token) {
                        case "$":
                        System.out.println("DEBUG LEXER - [ $ ] found at position (" + lineCounter + " : " + counter + ") - Program " + programCounter);
                            token = "";
                            counter = 1;
                            programCounter++;
                            System.out.println("Lexing completed!");
                            if(tokenList.hasNextLine()) {
                                System.out.println();
                                System.out.println("Lexing program " + programCounter + "...");
                            }
                        break;
                        case "{":
                            list.add(new Token("OPEN_BRACKET", "{", lineCounter, counter, programCounter));
                            System.out.println("DEBUG LEXER - [ { ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
                            token = "";
                            counter++;
                        break;
                        case "}":
                            list.add(new Token("CLOSE_BRACKET", "}", lineCounter, counter, programCounter));
                            System.out.println("DEBUG LEXER - [ } ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
                            token = "";
                            counter++;
                        break;
                        case "(":
<<<<<<< HEAD
                            list.add(new Token("OPEN_PARENTHESES", "(", lineCounter, counter, programCounter));
                            System.out.println("DEBUG LEXER - [ ( ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
=======
                        list.add(new Token("OPEN_PARENTHESES", "{", lineCounter, counter, programCounter));
                        System.out.println("DEBUG LEXER - [ ( ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
>>>>>>> 08fec120bd54e08076a1b245e7e0de190df155d6
                            token = "";
                            counter++;
                        break;
                        case ")":
<<<<<<< HEAD
                            list.add(new Token("CLOSE_PARENTHESES", ")", lineCounter, counter, programCounter));
=======
                            list.add(new Token("CLOSE_PARENTHESES", "{", lineCounter, counter, programCounter));
>>>>>>> 08fec120bd54e08076a1b245e7e0de190df155d6
                            System.out.println("DEBUG LEXER - [ ) ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
                            token = "";
                            counter++;
                        break;
                        case "=":
<<<<<<< HEAD
                            list.add(new Token("ASSIGN_OP", "=", lineCounter, counter, programCounter));
                            System.out.println("DEBUG LEXER - [ = ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
=======
                            System.out.println("= sign");
>>>>>>> 08fec120bd54e08076a1b245e7e0de190df155d6
                            token = "";
                            counter++;
                        break;
                        case "\'":
<<<<<<< HEAD
                            list.add(new Token("CHAR", "=", lineCounter, counter, programCounter));
                            System.out.println("DEBUG LEXER - [ \' ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
=======
                            System.out.println("\' sign");
>>>>>>> 08fec120bd54e08076a1b245e7e0de190df155d6
                            token = "";
                            counter++;
                        break;
                        case "\"":
<<<<<<< HEAD
                            list.add(new Token("STRING", "\"", lineCounter, counter, programCounter));
                            System.out.println("DEBUG LEXER - [ \" ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
                            
                            counter++;
                        break;
                        case "+":
                            list.add(new Token("PLUS", "=", lineCounter, counter, programCounter));
                            System.out.println("DEBUG LEXER - [ + ] found at position (" + lineCounter + " : " + counter + ") - Program " +  programCounter );
=======
                            System.out.println("\" sign");
                            token = "";
                            counter++;
                        break;
                        case "+":
                            System.out.println("+ sign");
>>>>>>> 08fec120bd54e08076a1b245e7e0de190df155d6
                            token = "";
                            counter++;
                        break;
                        case "print":
                            System.out.println("print sign");
                            token = "";
                            counter++;
                        break;
                        case "while":
                            System.out.println("while sign");
                            token = "";
                            counter++;
                        break;
                        case "if":
                            System.out.println("if sign");
                            token = "";
                            counter++;
                        break;
                        case "int":
                            System.out.println("type int sign");
                            token = "";
                            counter++;
                        break;
                        case "string":
                            System.out.println("type string sign");
                            token = "";
                            counter++;
                        break;
                        case "ID":
                            System.out.println("type ID sign");
                            token = "";
                            counter++;
                        break;
                        case "boolean":
                            System.out.println("boolean sign");
                            token = "";
                            counter++;
                        break;
                        case "true":
                            System.out.println("boolean value: true sign");
                            token = "";
                            counter++;
                        break;
                        case "false":
                            System.out.println("boolean value: false sign");
                            token = "";
                            counter++;
                        break;
                        case "==":
                            System.out.println("boolean operator: equal to sign");
                            token = "";
                            counter++;
                        break;
                        case "!=":
                            System.out.println("boolean operator: not equal to sign");
                            token = "";
                            counter++;
                        break;

                        //Everything below this is purely for testing purposes right now
                        case "/*":
                            System.out.println("I'M GOIN' COMMENT MODE!!!!1!!");
                            inAComment = true;
                            token = "";
                            counter++;
                            //Pass to inAComment boolean at beginning of loop
                        break;
                        case " ":
                            System.out.println("space sign");
                            token = "";
                        break;
                        case "a":
                            System.out.println("a sign");
                            token = "";
                        break;
                        case "b":
                            System.out.println("b sign");
                            token = "";
                        break;
                        default:
                        //Nothing here on purpose, we want to skip over white space
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
}