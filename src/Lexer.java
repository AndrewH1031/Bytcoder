package src;

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

    //for(int i = 0; i < tokenList.length(); i++) { //Won't work, currently figuring out another method besides scanner


    try {
    //Basic list stuff to handle our input and token dump
    Scanner tokenList = new Scanner(fileList);

        while (tokenList.hasNextLine()) {
            System.out.println("IOU one lexer"); //temp
        } 
    tokenList.close(); 
    }
    
    //Not entirely sure I even need this but I'm too scared to take it out
    catch (Exception noFile) {
        noFile.printStackTrace();
    }



  }
}