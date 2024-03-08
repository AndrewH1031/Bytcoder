import java.util.ArrayList;

public class Parser {

    /*
    We need:
    - parse
    - parse program
    - parse block
    - parse statement list
    - parse statement
    - parse print
    - parse assign
    - parse if, while
    - parse int, string, bool (types)
    - parse digits, characters, id
    - parse string (add stuff in lexer for this)
    - parse expressions (including bool)
    - parse boolops, boolvals
    - parse addition
    - parse var declaration
     */
    
    ArrayList<String> CST = new ArrayList<>(); //This is a list for now, will likely upgrade into a full tree eventually
    int progCounter = 0; //For each parse line

    public void main(ArrayList<Token> list) {
        System.out.println("by the power of God and Whiteclaw, I conjure forth this Parser!");
        //parse();

        //Token input from Lexer goes here
    }

    public static void parse() {
        System.out.println("parse body");
        parseProgram();
    }

    public static void parseProgram() {
        System.out.println("parse program");
        parseBlock();
    }

    public static void parseBlock() {
        System.out.println("parse block");
        parseStatementList();
    }

    public static void parseStatementList() {
        System.out.println("parse statement list");
        parseStatement();
    }

    public static void parseStatement() {
        System.out.println("parse statement stuff");
        //I think this is how you lay it out
        //add if statements depending on what kind of token
        parseBlock();
        parsePrint();
        parseAssign();
        parseVarDecl();
        parseIf();
        parseWhile();
    }

    public static void parsePrint() {
        System.out.println("parse print");
        //May not need these two
        parseExpression();
        parseCharList();
    }

    public static void parseAssign() {
        System.out.println("parse variable assignments");
        parseID();
        //something here to make sure it's followed up by an = symbol
        //then pass to varDecl?
    }

    public static void parseInt() {
        System.out.println("parse Int");
        //Expression
    }

    public static void parseString() {
        System.out.println("parse String");
        //Expression
    }

    public static void parseBoolean() {
        System.out.println("parse Boolean");
        //Expression
    }

    public static void parseVarDecl() {
        System.out.println("parse variable declarations");
        //Declare variables here
    }

    public static void parseIf() {
        System.out.println("parse If statements");
        //add if
    }

    public static void parseWhile() {
        System.out.println("parse While statements");
        //add while
    }

    public static void parseChar() {
        System.out.println("parse Char");
        //add char
    }

    public static void parseCharList() {
        System.out.println("parse Char list for valid characters");
        //Check to see if we have a valid char for our current token (i.e. if it's considered a char, NOT an id in the token list)
        parseChar();
    }

    public static void parseDigit() {
        System.out.println("parse digits");
        //add digits
    }

    public static void parseID() {
        System.out.println("parse IDs");
        //add IDs
    }

    public static void parseTypeCheck() {
        System.out.println("parse for Int, String, Boolean");
        //String, Int, Boolean type checking goes here
    }

    public static void parseExpression() {
        System.out.println("parse variable declarations");
        //Expressions go here (addition, strings, etc.)
        parseInt();
        parseString();
        parseBoolean();
    }

    public static void parseBoolOp() {
        System.out.println("parse Boolean operands");
        //Equals, not equals goes here
    }

    public static void parseEBoolVal() {
        System.out.println("parse Boolean values");
        //True, false go here
    }

    public static void parseAdd() {
        System.out.println("parse Intop + symbol");
        //+ goes here
    }
    
    //Print CST here????

}
