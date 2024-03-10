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
        System.out.println("parse body");
        parseProgram();
    }

    public void parseProgram() {
        System.out.println("parse program");
        parseBlock();
    }

    public void parseBlock() {
        System.out.println("parse block");
        parseStatementList();
    }

    public void parseStatementList() {
        System.out.println("parse statement list");
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
        System.out.println("parse statement stuff");
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
                parseAssign();
            break;
            case("TYPEINT"):
                parseVarDecl();
            break;
            case("TYPESTRING"):
                parseVarDecl();
            break;
            case("TYPEBOOL"):
                parseVarDecl();
            break;
        }
        //ignore this stuff for nows
        /*parseBlock();
        parsePrint();
        parseAssign();
        parseVarDecl();
        parseIf();
        parseWhile();*/
    }

    public void parsePrint() {
        handleParseToken("PRINTSTATEMENT", "parsePrintStatement()");
        //add parseCount to handleParseToken please and thank you
        handleParseToken("OPEN_PAREN", "parseOpenExpression()");
        parseExpression();
        handleParseToken("CLOSE_PAREN", "parseCloseExpression()");
        //May not need these two
        /*parseExpression();
        parseCharList();*/
    }

    public void parseAssign() {
        //something here to make sure it's followed up by an = symbol
        //then pass to varDecl?
        System.out.println("parseAssignStatement()");
        if(currentToken == "ID") {
            parseID();
            handleParseToken("ASSIGNOP", "parseAssignment()");
            parseExpression();
        }
        else {
            System.out.println("Error! Invalid token here");
        }

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
        //Expression
        System.out.println("parseBoolean()");
        handleParseToken("OPEN_PAREN", "parseOpenExpression()");
        if(currentToken == "OPEN_PAREN") {
            handleParseToken("OPEN_PAREN", "parseOpenQuote()");
            parseExpression();
            parseBoolOp();
            parseExpression();
        }
        else if(currentToken == "BOOLVAL") {
            parseBoolVal();
        }
        else {
            System.out.println("Error! Invalid token detected here");
        }
    }

    public void parseVarDecl() {
        //Declare type variables here
        System.out.println("parseVarDeclaration()");
        parseTypeCheck();
        parseID();
    }

    public void parseIf() {
        handleParseToken("IFSTATEMENT", "parseIfStatement()");
        //add if
        parseBoolean();
        parseBlock();
    }

    public void parseWhile() {
        handleParseToken("WHILESTATEMENT", "parseWhileStatement()");
        //add while
        parseBoolean();
        parseBlock();
    }

    public void parseChar() {
        //add char
        handleParseToken("CHAR", "parseChar()");
    }

    public void parseCharList() {
        //Check to see if we have a valid char for our current string (i.e. if it's considered a char, NOT an id in the token list)
        //Need to be able to detect string starts(open quotes) in lexer first
    }

    public void parseDigit() {
        //add digits
        handleParseToken("NUM", "parseDigit()");
    }

    public void parseID() {
        handleParseToken("ID", "parseID()");
        //add IDs
    }

    public void parseTypeCheck() {
        //String, Int, Boolean type checking goes here
        System.out.println("parseTypeChecking()");
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                case("TYPEINT"):
                    //Can probably optimize this
                    handleParseToken("TYPEINT", "parseInt()");
                break;
                case("TYPESTRING"):
                    handleParseToken("TYPESTRING", "parseString()");
                break;
                case("TYPEBOOL"):
                    handleParseToken("TYPEBOOL", "parseBoolean()");
                break;
            }
        }
        else {
            //Expand this
            System.out.println("Error! Unexpected token here");
        }
    }

    public void parseExpression() {
        //Expressions go here (addition, strings, etc.)
        System.out.println("parseExpression()");
        //temporary
        parseCounter++;
        currentToken = parseList.get(parseCounter).tokenType;
    }

    public void parseBoolOp() {
        //Equals, not equals goes here
        handleParseToken("BOOLOP", "parseBooleanOperand()");
    }

    public void parseBoolVal() {
        //True, false go here
        handleParseToken("BOOLVAL", "parseBooleanVal()");
    }

    public void parseAdd() {
        //+ goes here
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
