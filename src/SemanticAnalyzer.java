import java.util.ArrayList;

public class SemanticAnalyzer {
    
    ArrayList<String> AST = new ArrayList<>(); //Assuming I'm going to need this like last time
    ArrayList<Integer> astDepth = new ArrayList<>(); //this too

    //edit these for our incoming AST
    ArrayList<Token> semanticList; //List for storing our parse variables, we want to use these to print our AST as well
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int depth = 0; //Need to reformat this
    String currentToken; //idk what to do with this anymore
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token

    int progCounter = 0;

    //Currently just messing around with the code to accept proper AST syntax - I'll do the stupid symbol table later. Code is also COMPLETELY NON-FUNCTIONAL, don't bother trying to run it yet.

    //Also basically copy-pasted my Parser over because I ASSUME the AST is going to follow at least the same parsing logic as our parser

    public void main(ArrayList<Token> list) {
        semanticList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer cals
        depth = 0; //Reset depth between lexer calls
        currentToken = semanticList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list

        System.out.println("STARTING SEMANTIC ANALYSIS ON PROGRAM " + semanticList.get(parseCounter).progNum + "."); //change
        System.out.println();
        System.out.println("Beginning Analyzer...");
        currentToken = semanticList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list
        analyze(); //Call AST print and Symbol Table methods from this

        AST.clear();
        astDepth.clear();
        
        }

    //Main parse function - I'm only keeping this here for [insert witty joke]
    public void analyze() { //use this naming scheme
        //System.out.println(semanticList);
        parseProgram();
    }

    //I think this just analyzes the program thingy now and nothing else
    public void parseProgram() {
        parseBlock();
    }

    public void parseBlock() {
        //Increment depth, scope comes later for symbol table
        System.out.println("parseBlock()");
        //addAST("Block", depth);
        parseStatementList();
    }

    public void parseStatementList() {
        System.out.println("Statement List");
        parseStatement();
    }

    public void parseStatement() {
        System.out.println("Statement");
        //parseBlock();
        parseIf();
        parseWhile();
        parsePrint();
        parseAssign();
        parseVarDecl();
        parseInt();
        parseDigit();
        parseSpace();
        parseAST();
        addAST();
        handleAnalyzedToken();
        padDepth();
        parseExpression();
        error();
    }

    public void parsePrint() {
        //addAST("Print", depth);
        System.out.println("Print");
    }

    public void parseAssign() {
        //addAST("Assign", depth)
        System.out.println("Assign");
    }

    public void parseIf() {
        //addAST("If", depth);
        System.out.println("If");
    }

    public void parseWhile() {
        //addAST("While", depth)
        System.out.println("While");
    }

    public void parseVarDecl() {
        //addAST("Variable Dec", depth);
        System.out.println("VarDecl");
    }

    public void parseBoolean() {
        System.out.println("Boolean");
    }

    public void parseInt() {
        System.out.println("Int");
    }

    public void parseString() {
        System.out.println("String");
    }

    public void parseChar() {
        System.out.println("Char");
    }

    public void parseCharList() {
        //addAST("")
        System.out.println("CharList");
    }

    public void parseSpace() {
        System.out.println("Space");
    }

    public void parseExpression() {
        System.out.println("Expression");
    }

    public void parseID() {
        System.out.println("ID");
    }

    public void parseDigit() {
        System.out.println("Digit");
    }

    public void parseTypeCheck() {
        System.out.println("TypeCheck");
    }

    public void parseBoolOp() {
        //addAST("BoolOp", depth)
        System.out.println("BoolOp");
    }

    public void parseBoolVal() {
        System.out.println("BoolVal");
    }

    public void parseAdd() {
        System.out.println("Intop");
    }

    public void parseAST() {
        System.out.println("AST goes here");
    }

    public void addAST() {
        System.out.println("AST addition goes here");
    }

    public void error() {
        System.out.println("errors go here");
    }

    public void padDepth() {
        System.out.println("padding depth...turn this into a string function");
    }

    public void handleAnalyzedToken() {
        //addAST("[" + semanticList.get(parseCounter).name + "]", depth);
        System.out.println("Analyze da tokenz");
    }
}
