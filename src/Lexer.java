//package src;

//A very generous thank you to the folks at:
//      https://stackoverflow.com/questions/13185727/reading-a-txt-file-using-scanner-class-in-java
//for teaching me how to read text from a file input, because I am not a perfect human being

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

//NOTE: Code is currently still very incomplete, just messing around

public class Lexer {

  public static void main(String src) {

    File fileList = new File(src);

    int counter = 0; //Probably gonna need this for when we start counting our lex outputs
     //string to grab the current whole string (not just one token), which will help us with our pointer
    String token; //char to grab our current soon-to-be token (one at a time) from the file
    String string;

    //for(int i = 0; i < tokenList.length(); i++) { //Won"t work, currently figuring out another method besides scanner


    try {
    //Basic list stuff to handle our input and token dump
    Scanner tokenList = new Scanner(fileList);

        while (tokenList.hasNextLine()) {
            //System.out.println("IOU one lexer");
            string = tokenList.nextLine();
            token = "";

            for (int i = 0; i < string.length(); i++) {

                //Check to see if comment
                //Check to see if string

                token = token + string.charAt(i);
                //System.out.println(token);
                //System.out.println(string);

                /*if(token == "$") {
                    System.out.println("$ sign");
                }
                else if(token == "print") {
                    System.out.println("print sign");
                }
                else {

                }*/

                //Switch statement to determine what we should do with our token
                //Vaguely follows the grammar order from the project 1 grammar.pdf
                switch(token) {
                    case "$":
                        System.out.println("$ sign");
                    break;
                    case "{":
                        System.out.println("{ sign");
                    break;
                    case "}":
                        System.out.println("} sign");
                    break;
                    case "(":
                        System.out.println("( sign");
                    break;
                    case ")":
                        System.out.println(") sign");
                    break;
                    case "=":
                        System.out.println("= sign");
                    break;
                    case "\'":
                        System.out.println("\" sign");
                        //comment
                    break;
                    case "\"":
                        System.out.println("\" sign");
                        //comment
                    break;
                    case "print":
                        System.out.println("print sign");
                        //comment
                    break;
                    default:
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