
import java.util.ArrayList;

public class SemanticAnalyzer {

    ArrayList<String> CST = new ArrayList<>(); //This is a list for now, will likely upgrade into a full tree eventually
    ArrayList<Integer> cstDepth = new ArrayList<>(); //Depth of each of our CST tokens, dutifully calculated before handling each one
    ArrayList<Token> parseList; //List for storing our parse variables, we want to use these to print our CST later
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int depth = 0; //Random variable that we increase/decrease to print out in our CST. Usually is incremented before a program starts and decremented after it finishes
    String currentToken; //The current token we want to match up
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token

    public void main(ArrayList<Token> list) {
        parseList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer cals
        depth = 0; //Reset depth between lexer calls
        currentToken = parseList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list

        System.out.println("PARSER: SEMANTIC ANALYZER called from LEXER");
        System.out.println();
        System.out.println("Beginning Analyzer...");
    }
}