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

    //My godforsaken 4 year old laptop cannot run this thing in the command prompt to save its life, so please bear with me as I lose my mind trying to update it fruitlessly

    //nvm figured it out, just make a new copy of it locally and run it there
    //don't actually change anything, just use the file to run stuff
    //delete when done, repeat ad nauseum - this method is so stupid and genuinely shouldn't work but it does and I don't care anymore

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
        //System.out.println(currentToken);
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
                parseCounter++;
            break;
            case("PRINTSTATEMENT"):
                parsePrint();
            break;
            case("IFSTATEMENT"):
                parseIf();
                parseCounter++;
            break;
            case("WHILESTATEMENT"):
                parseWhile();
                parseCounter++;
            break; 
            case("ID"):
                parseID();
                parseCounter++;
            break;
            case("TYPEINT"):
                parseVarDecl();
                parseCounter++;
            break;
            case("TYPESTRING"):
                parseVarDecl();
                parseCounter++;
            break;
            case("TYPEBOOL"):
                parseVarDecl();
                parseCounter++;
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
        parseCounter++;
        handleParseToken("OPEN_PAREN", "parseOpenExpression()");
        parseCounter++;
        parseExpression();
        parseCounter++;
        handleParseToken("CLOSE_PAREN", "parseCloseExpression()");
        parseCounter++;
        //May not need these two
        /*parseExpression();
        parseCharList();*/
    }

    public void parseAssign() {
        //something here to make sure it's followed up by an = symbol
        //then pass to varDecl?
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
        handleParseToken("OPEN_PAREN", "parseOpenExpression()")
    }

    public void parseVarDecl() {
        //Declare type variables here
        System.out.println("parseVarDeclaration()")
        parseTypeCheck();
        parseID();
    }

    public void parseIf() {
        handleParseToken("IFSTATEMENT", "parseIfStatement()");
        //add if
    }

    public void parseWhile() {
        handleParseToken("WHILESTATEMENT", "parseWhileStatement()");
        //add while
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

    public static void parseTypeCheck() {
        //String, Int, Boolean type checking goes here
        System.out.println("parseTypeChecking()")
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                case("TYPEINT"):
                    //Can probably optimize this
                    handleParseToken("TYPEINT", "parseInt()");
                break;
                case("TYPESTRING"):
                    handleParseToken("TYPEINT", "parseString()");
                break;
                case("TYPEBOOL"):
                    handleParseToken("TYPEINT", "parseBoolean()");
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
    }

    public static void parseBoolOp() {
        //Equals, not equals goes here
        handleParseToken("BOOLOP", "parseBooleanOperand()");
    }

    public static void parseEBoolVal() {
        //True, false go here
        handleParseToken("BOOLVAL", "parseBooleanVal()");
    }

    public static void parseAdd() {
        //+ goes here
        handleParseToken("INTOP", "parseIntop()");
    }
    
    //Print CST here????

    //Could possibly change this to accept currentToken instead of being non-static
    public void handleParseToken(String expected, String output) {
        if(currentToken == expected) {
            System.out.println(expected); //temporary
        }
        else {
            System.out.println("error! wrong token here");
        }
        //compare expected to given
        System.out.println("PARSER: " + output);
    }

}
