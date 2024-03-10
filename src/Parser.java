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
    ArrayList<Token> parseList;
    int parseCounter = 0; //for counting each token in the list, one by one
    String currentToken;

    //NOTE: code is currently VERY rough right now, will make sure it's working in future commits

    public void main(ArrayList<Token> list) {
        parseList = list;
        currentToken = parseList.get(parseCounter).tokenType;
        parseCounter = 0;

        System.out.println("by the power of God and Whiteclaw, I conjure forth this Parser!");
        System.out.println();
        System.out.println("Beginning Parser...");
        parse();

        //Token input from Lexer goes here
    }

    public void parse() {
        System.out.println("parseBody()");
        parseProgram();
    }

    public void parseProgram() {
        System.out.println("parseProgram()");
        parseBlock();
        handleParseToken("EOP_BLOCK", "parseEndOfProgram()");
    }

    public void parseBlock() {
        System.out.println("parseBlock()");
        parseStatementList();
        handleParseToken("CLOSE_BLOCK", "parseCloseBlock()");
    }

    public void parseStatementList() {
        System.out.println("parseStatementList()");
        currentToken = parseList.get(parseCounter).tokenType;
        System.out.println(currentToken);
        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {
            //System.out.println("token here?");

            parseStatement();
            parseStatementList();
        }
        else {
            //do nothing
        }
    }

    public void parseStatement() {
        System.out.println("parseStatement()");
        //add if/case statements depending on what kind of token
        switch(currentToken) {
            case("OPEN_BLOCK"):
                handleParseToken("OPEN_BLOCK", "parseBlock()");
            break;
            case("PRINTSTATEMENT"):
                parsePrint();
            break;
            case("IFSTATEMENT"):
                parseIf();
            break;
            case("WHILESTATEMENT"):
                parseWhile();
            break; 
            case("ID"):
            //Assigning an ID or just a lone ID? Don't know, let's pass it to Assign()
                parseAssign();
            break;
            case("TYPEINT"):
            //Types are declared, need to pass it to VarDecl()
                parseVarDecl();
            break;
            case("TYPESTRING"):
                parseVarDecl();
            break;
            case("TYPEBOOL"):
                parseVarDecl();
            break;
        }
    }

    public void parsePrint() {
        handleParseToken("PRINTSTATEMENT", "parsePrintStatement()");
        handleParseToken("OPEN_PAREN", "parseOpenExpression()");
        parseExpression();
        handleParseToken("CLOSE_PAREN", "parseCloseExpression()");
        //May not need these two
        /*parseExpression();
        parseCharList();*/
    }

    public void parseAssign() {
        //Assignment statement - check for an existing ID to declare, then search for a BoolOp and then finally pass it to Expression() to find what we want to declare it to
        System.out.println("parseAssignStatement()");
        parseID();
        handleParseToken("ASSIGNOP", "parseAssignment()");
        parseExpression();
    }

    public void parseInt() {
        handleParseToken("TYPEINT", "parseInt()");
        //Expression
    }

    public void parseString() {
        handleParseToken("TYPESTRING", "parseString()");
        //Expression
    }

    public void parseBoolean() {
        //Boolean initialization - we need this for our while and if statements to work
        System.out.println("parseBoolean()");
        handleParseToken("OPEN_PAREN", "parseOpenExpression()");
        //If we have an open paren, then that means our statement is valid - call to handle open paren, then parse through both expressions and the boolop in between
        if(currentToken == "OPEN_PAREN") {
            //while(expr == expr/boolop) {
            handleParseToken("OPEN_PAREN", "parseOpenQuote()");
            //Include expression functionality before tweaking this order
            parseExpression();
            parseBoolOp();
            parseExpression(); //if boolval then handle boolval; else expression??
        }
        //Else if we've got a BoolVal (through parsing through an existing if/while statement), then initialize it
        //Can likely just merge this with the main open paren statement
        else if(currentToken == "BOOLVAL") {
            parseBoolVal();
        }
        //If we don't find anything relevant (i.e. open paren token), print an error
        else {
            System.out.println("Error! Invalid token detected here - Expected an open quote");
        }
    }

    public void parseVarDecl() {
        System.out.println("parseVarDeclaration()");
        //Double check ourselves to make sure we've got a Type on our hands
        parseTypeCheck();
        //...then add the ID we want to declare
        parseID();
    }

    public void parseIf() {
        //If statement: initialize if, then pass to Boolean() to determine what to do with the following parentheses, and then Block() to make sure we have an open bracket following too
        handleParseToken("IFSTATEMENT", "parseIfStatement()");
        parseBoolean();
        parseBlock();
    }

    public void parseWhile() {
        //While statement: pretty much like if
        handleParseToken("WHILESTATEMENT", "parseWhileStatement()");
        parseBoolean();
        parseBlock();
    }

    public void parseChar() {
        //add a Character (ID part of string)
        //Need to add string functionality to lexer first
        handleParseToken("CHAR", "parseChar()");
    }

    public void parseCharList() {
        //Check to see if we have a valid char for our current string (i.e. if it's considered a char, NOT an id in the token list)
        //Need to be able to detect string starts(open quotes) in lexer first
    }

    public void parseDigit() {
        //add a Digit
        handleParseToken("NUM", "parseDigit()");
    }

    public void parseID() {
        //add an ID
        handleParseToken("ID", "parseID()");
    }

    public void parseTypeCheck() {
        //String, Int, Boolean type checking to make extra sure we've got something to declare
        System.out.println("parseTypeChecking()");
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                //Declare an Int
                case("TYPEINT"):
                    //Can probably optimize this
                    handleParseToken("TYPEINT", "parseInt()");
                break;
                //Declare a String
                case("TYPESTRING"):
                    handleParseToken("TYPESTRING", "parseString()");
                break;
                //Declare a Boolean
                case("TYPEBOOL"):
                    handleParseToken("TYPEBOOL", "parseBoolean()");
                break;
            }
        }
        //Don't need an else statement here since handleParseToken takes care of errors for us
    }

    public void parseExpression() {
        //Expressions go here (addition, strings, etc.)
        System.out.println("parseExpression()");
        //temporary
        parseCounter++;
        currentToken = parseList.get(parseCounter).tokenType;

        //ROUGH IDEA FOR THIS:
        //We need to use this for processing stuff in parentheses
        //Check for parentheses first
        //Detect open strings, numbers and IDs
        //Print error as usual if stuff doesn't match
    }

    public void parseBoolOp() {
        //add a BoolOp (==, !=) here - these will be our main comparison tools inside expressions
        handleParseToken("BOOLOP", "parseBooleanOperand()");
    }

    public void parseBoolVal() {
        //add a Boolean value (true, false)
        handleParseToken("BOOLVAL", "parseBooleanVal()");
    }

    public void parseAdd() {
        //add an IntOp (addition)
        handleParseToken("INTOP", "parseIntop()");
    }
    
    //Print CST here????

    //Could possibly change this to accept currentToken instead of being non-static
    public void handleParseToken(String expected, String output) {
        if(currentToken == expected) {
            System.out.println("Correct Token! It's " + expected); //temporary
        }
        else {
            System.out.println("error! wrong token here, should be " + expected + ", got " + currentToken);
        }
        //compare expected to given
        //System.out.println("PARSER: " + output);
        parseCounter++;
        currentToken = parseList.get(parseCounter).tokenType;
    }

}
