import java.util.ArrayList;

public class Parser {
    
    ArrayList<String> CST = new ArrayList<>(); //This is a list for now, will likely upgrade into a full tree eventually
    ArrayList<Token> parseList;
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0;
    String currentToken;
    boolean endTheDamnThing = false;

    //NOTE: code is currently a little rough right now, everything aside from expressions work fine

    public void main(ArrayList<Token> list) {
        parseList = list;
        parseCounter = 0;
        currentToken = parseList.get(parseCounter).tokenType;

        System.out.println("PARSER: Parser called from LEXER");
        System.out.println();
        System.out.println("Beginning Parser...");
        parse();

        //Token input from Lexer goes here
    }

    public void parse() {
        System.out.println("PARSER: parseBody()");
        parseProgram();
    }

    public void parseProgram() {
        System.out.println("PARSER: parseProgram()");
        parseBlock();
        //Check for an End-Of-Program symbol
        handleParseToken("EOP_BLOCK", "parseEndOfProgram()");
        if(endTheDamnThing == true) {
            if(errors > 0) {
                System.out.println();
                System.out.println("PARSER: Parser failed with " + errors + " errors");
                System.out.println("PARSER: CST skipped due to PARSER error(s)");
            }
            else {
                System.out.println();
                System.out.println("PARSER: Parsing completed successfully");
                System.out.println("PARSER: Printing CST...");
                CST();

                //Semantic analyzer call here for the future
            }
        }
    }

    public void parseBlock() {
        System.out.println("PARSER: parseBlock()");
        handleParseToken("OPEN_BLOCK", "parseBlock()");
        parseStatementList();
        //Use this to check for the complementary close block token
        handleParseToken("CLOSE_BLOCK", "parseCloseBlock()");
    }

    public void parseStatementList() {
        System.out.println("PARSER: parseStatementList()");
        currentToken = parseList.get(parseCounter).tokenType;
        //System.out.println(currentToken);
        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {

            parseStatement();
            parseStatementList();
        }
    }

    public void parseStatement() {
        System.out.println("PARSER: parseStatement()");
        //add if/case statements depending on what kind of token
        switch(currentToken) {
            case("OPEN_BLOCK"):
                parseBlock();
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
            //Assigning an ID or do we just have a lone ID? Don't know, let's pass it to Assign()
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
            default:
                error("STATEMENT", currentToken);
            break;
        }
    }

    public void parsePrint() {
        //Print statement - check for a Print (obviously), then look for both sets of parentheses and an expression
        handleParseToken("PRINTSTATEMENT", "parsePrintStatement()");
        handleParseToken("OPEN_PAREN", "parseOpenExpression()");
        parseExpression();
        handleParseToken("CLOSE_PAREN", "parseCloseExpression()");
    }

    public void parseAssign() {
        //Assignment statement - check for an existing ID to declare, then search for a BoolOp and then finally pass it to Expression() to find what we want to declare it to
        System.out.println("PARSER: parseAssignStatement()");
        parseID();
        handleParseToken("ASSIGNOP", "parseAssignment()");
        parseExpression();
    }

    public void parseInt() {
        System.out.println("PARSER: parseIntExpression()");
        //Expression
        if(currentToken == "NUM") {
            parseDigit();
            if(currentToken == "INTOP") {
                parseAdd();
                parseExpression();
            }
        }
        else {
            error("NUM", currentToken);
        }
    }

    public void parseString() {
        System.out.println("PARSER: parseStringExpression()");
        //Expression
        if(currentToken == "OPENSTRING") {
            handleParseToken("OPENSTRING", currentToken);
            parseCharList();
            handleParseToken("CLOSESTRING", currentToken);
        }
        else {
            error("OPENSTRING", currentToken);
        }
    }

    public void parseBoolean() {
        //Boolean initialization - we need this for our while and if statements to work
        System.out.println("PARSER: parseBoolean()");
        //If we have an open paren, then that means our statement is valid - call to handle open paren, then parse through both expressions and the boolop in between
        if(currentToken == "OPEN_PAREN") {
            //while(expr == expr/boolval) {
            handleParseToken("OPEN_PAREN", "parseOpenParen()");
            //Include expression functionality before tweaking this order
            parseExpression();
            parseBoolOp();
            //If we've got a BoolVal (through parsing through an existing if/while statement), then initialize it
            if(currentToken == "BOOLVAL") {
                parseBoolVal();
            }
            //Else, it's just a regular ol' expression - shove it in there
            else {
                parseExpression();
            }
            handleParseToken("CLOSE_PAREN", "parseCloseParen()");
        }
        //If we don't find anything relevant (i.e. open paren token), print an error
        else {
            error("OPEN_PAREN", currentToken);
        }
    }

    public void parseVarDecl() {
        System.out.println("PARSER: parseVarDeclaration()");
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

    public void parseSpace() {
        handleParseToken("CHARSPACE", "parseSpace()");
    }

    public void parseCharList() {
        //Check to see if we have a valid char for our current string (i.e. if it's considered a char or charspace, NOT an id in the token list)
        //Once we have our character, loop back to charlist to parse for more chars
        System.out.println("parseCharList()");
        if(currentToken == "CHAR") {
            parseChar();
            parseCharList();
        }
        //Finally doing stuff with our space tokens
        else if(currentToken == "CHARSPACE") {
            parseSpace();
            parseCharList();
        }
        else {
            //Nothing here...
        }
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
        System.out.println("PARSER: parseTypeChecking()");
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
        //nvm it's nice to have backup options
        else {
            error("TYPE", currentToken);
        }
    }

    public void parseExpression() {
        //Expressions go here (addition, strings, etc.)
        System.out.println("PARSER: parseExpression()");
        
        switch(currentToken) {
            case("NUM"):
                parseInt();
            break;
            case("OPENSTRING"):
                parseString();
            break;
            case("ID"):
                parseID();
            break;
            case("OPEN_PAREN"):
                parseBoolean();
            break;
            case("BOOLVAL"):
                parseBoolVal();
            break;
            default:
                error("EXPRESSION", currentToken);
            break;
        }
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
    
    public void CST() {
        System.out.println("IOU one CST, mmmkay");
    }

    public void printCST() {
        //for length of CST, print i or smth
    }
    //Print CST here????

    //Could possibly change this to accept currentToken instead of being non-static
    public void handleParseToken(String expected, String output) {
        if(currentToken == expected) {
            if(currentToken == "EOP_BLOCK") {
                endTheDamnThing = true;
                //DON'T increment parseCounter here - it's the end of the program, there's nothing else left to read!
            }
            else {
                System.out.println("Correct Token! It's " + expected); //temporary
                parseCounter++;
            }
        }
        else {
            System.out.println("oh no!");
            error(expected, currentToken);
            parseCounter++;
        }
        //compare expected to given
        //System.out.println("PARSER: " + output);

        currentToken = parseList.get(parseCounter).tokenType;
    }

    public void error(String expectedToken, String errorToken) {
        System.out.println("ERROR: Expected [" + expectedToken + "], got [" + errorToken + "] on line " + parseList.get(parseCounter).lineCount);
        errors++;
    }

}
