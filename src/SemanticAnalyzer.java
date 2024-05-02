import java.util.ArrayList;

public class SemanticAnalyzer {
    
    ArrayList<String> AST = new ArrayList<>(); //Assuming I'm going to need this like last time
    ArrayList<Integer> astDepth = new ArrayList<>(); //this too
    ArrayList<Symbol> symbolList = new ArrayList<>();

    ArrayList<Token> semanticList; //List for storing our parse variables, we want to use these to print our AST as well
    int parseCounter = 0; //for counting each token in the list, one by one
    int errors = 0; //Error count, tracks how many errors we happen to run into in our Parsing process. Program will refuse to print CST if there's any errors
    int warnings = 0; //might need this soon
    int depth = 0;
    int scope = 0;
    String currentToken; //The current token in the symbol list
    String nextToken; //The next token in line, used for finding proper tokens in sequence in stuff like IntOp
    String sentence = "";
    String prevToken; //The previous token that we parsed. Used for comparing to the result of our expression when type checking assignments
    boolean endTheDamnThing = false; //If this is true, end our program and print the CST if we have no errors. Only found if we parse through an EOP token
    boolean declaring = false; //Used when we want to declare a variable initially
    boolean assigning = false; //Used when we're assigning values to our tokens

    CodeGen codeGen = new CodeGen();

    //Symbol table is currently a bit strange when it comes to detecting if something's been used or not - currently working on a fix
    //still working on this....

    public void main(ArrayList<Token> list) {

        

        semanticList = list; //set parseList equal to the list in our Lexer for comparison purposes
        parseCounter = 0; //Reset parseCounter between lexer calls
        depth = 0; //Reset depth between lexer calls
        scope = -1; //this looks awful but it's the only way to make sure it's 0 on the first block
        errors = 0;

        System.out.println();
        System.out.println("Semantic Analysis for Program " + semanticList.get(parseCounter).progNum);
        System.out.println();
        System.out.println("Beginning Analyzer...");
        currentToken = semanticList.get(parseCounter).tokenType; //Set current token to the first element in our borrowed list

        analyze(); //Call AST print from this

        AST.clear();
        astDepth.clear();
        symbolList.clear();
        
        }

    //Main parse function
    public void analyze() {
        //System.out.println(semanticList);
        analyzeProgram();
        if(errors > 0) {
            System.out.println("Semantic Analysis failed with " + errors + " errors and " + warnings + " warnings");
            System.out.println();
            System.out.println("AST and Symbol Table skipped due to Semantic Analysis errors");
        }
        else {
            System.out.println("Semantic Analysis completed with " + errors + " errors and " + warnings + "warnings");
            System.out.println();
            System.out.println("Printing AST...");
            System.out.println();
            AST();
            printSymbolList();
            codeGen.main();
        }
    }

    public void analyzeProgram() {
        analyzeBlock();
    }

    public void analyzeBlock() {
        //DON'T check for match - parser already did that

        System.out.println("SEMANTIC: BLOCK");
        addAST("Open Block", depth);
        depth++;
        scope++; //increment scope to accomodate new block

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        //System.out.println(scope); //test

        analyzeStatementList();

        //Check for a close block at the end of our block statement - no need to match it up, parser has that covered already!
        addAST("Close Block", depth);

        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        //Decrement depth and scope when we're done
        depth--;
        scope--;

    }

    public void analyzeStatementList() {
        System.out.println("SEMANTIC: Statement List");

        currentToken = semanticList.get(parseCounter).tokenType;
        //System.out.println(currentToken); //test

        if(currentToken == "PRINTSTATEMENT" || currentToken == "ID" || currentToken == "WHILESTATEMENT" || currentToken == "IFSTATEMENT" || currentToken == "OPEN_BLOCK" || currentToken == "TYPEINT" || currentToken == "TYPESTRING" || currentToken == "TYPEBOOL") {

            analyzeStatement();
            analyzeStatementList();
        }
    }

    public void analyzeStatement() {
        System.out.println("SEMANTIC: Statement");
        //Case statement depending on what kind of token we have
        switch(currentToken) {
            //Can add nested block statements through this
            case("OPEN_BLOCK"):
                analyzeBlock();
            break;
            case("PRINTSTATEMENT"):
                analyzePrint();
            break;
            case("IFSTATEMENT"):
                analyzeIf();
            break;
            case("WHILESTATEMENT"):
                analyzeWhile();
            break; 
            case("ID"):
            //Assigning an ID or do we just have a lone ID? Don't know, let's pass it to Assign()
                analyzeAssign();
            break;
            case("TYPEINT"):
            //Types are declared, need to pass it to VarDecl()
                analyzeVarDecl();
            break;
            case("TYPESTRING"):
                analyzeVarDecl();
            break;
            case("TYPEBOOL"):
                analyzeVarDecl();
            break;
            //If we've got nothing, then throw an error
            default:
                System.out.println("ERROR: expected STMT token, got token " + currentToken +  " in scope " + scope + " on line " + semanticList.get(parseCounter).lineCount);
                errors++;
            break;
        }
    }

    public void analyzePrint() {
        addAST("Print", depth);
        System.out.println("SEMANTIC: Print");
        
        //Skip the first two tokens we encounter (string statement and open quotes) and jump straight to the expression
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        depth++;
        analyzeExpression();
        depth--;
        //One last skip over the close quotes token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        

    }

    //Set our assigning flag to true and scan the ID to see if we're all set to initialize
    public void analyzeAssign() {
        addAST("Assign", depth);
        System.out.println("SEMANTIC: Assign");

        depth++;
        //Set assigning to true so we can properly deal with our assignment statements
        assigning = true;  
        analyzeID();
        
        //Skip over this token that we previously checked for
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        //Type checking time! We'll need to see if our assignments are making sense
        //Initialize our two comparison tokens - pastToken, which will grab the type value of the previous ID token in line, and secondType, which will grab the result of the expression that we want to parse.
        //our prevToken is ran through a scope checking function to make sure it's on a scope level we can access
        String pastToken = typeMatch(symbolList, prevToken, scope);
        String secondType = analyzeExpression();
        //System.out.println("past token is " + pastToken);

        //Compare our two tokens that we got
        if(pastToken == secondType) {
            //do nothing, this is a good thing
        }
        //If our tokens don't match (usually due to it returning an "error"), then throw an error message
        else {
            mismatchError(pastToken, secondType, semanticList.get(parseCounter).lineCount);
            errors++;
        }
        depth--;
    }

    //Ignore all tokens except for what's in the statement
    public void analyzeIf() {
        addAST("If", depth);
        System.out.println("SEMANTIC: If");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;

        depth++;
        analyzeBoolean();
        analyzeBlock();
        depth--;
    }

    //Ignore all tokens except for what's in the statement
    public void analyzeWhile() {
        addAST("While", depth);
        System.out.println("SEMANTIC: While");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        
        depth++;
        analyzeBoolean();
        analyzeBlock();
        depth--;
    }

    //Declares type check and sets declaring to true, since we're initializing a variable with this method
    public void analyzeVarDecl() {
        addAST("Variable Dec", depth);
        System.out.println("SEMANTIC: VarDecl");

        depth++;
        analyzeTypeCheck();
        declaring = true;
        analyzeID();
        depth--;
    }

    public void analyzeBoolean() {
        System.out.println("SEMANTIC: Boolean");
        if(currentToken == "OPEN_PAREN") {
            parseCounter = refresh(parseCounter);
            currentToken = semanticList.get(parseCounter).tokenType;
            analyzeExpression();
            analyzeBoolOp();

            //Set nextToken to currentToken + 1
            if(semanticList.size() - 1 > parseCounter + 1) {
                nextToken = semanticList.get(parseCounter + 1).tokenType;
            }

            if(nextToken == "BOOLVAL") {
                analyzeBoolVal();
            }
            else {
                analyzeExpression();
            }
            //increment parsecounter and skip this next close paren token - use refresh
            parseCounter = refresh(parseCounter);
            currentToken = semanticList.get(parseCounter).tokenType;
            
        }

        //If the token is not what we expected, throw an error
        else {
            System.out.println("ERROR: expected " + currentToken + ", got token " + scope + " on line " + semanticList.get(parseCounter).lineCount);
            errors++;
        }
    }

    public void analyzeInt() {
        System.out.println("SEMANTIC: Int");
        //analyzeDigit - no need to check for num, it's already foretold
        
        if(semanticList.size() - 1 > parseCounter + 1) {
            nextToken = semanticList.get(parseCounter + 1).tokenType;
        }

        //Still parse digit
        analyzeDigit();

        if(nextToken == "INTOP") {
            analyzeAdd();
            analyzeExpression();
        }
    }

    public void analyzeString() {
        System.out.println("SEMANTIC: String");
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        analyzeCharList();
        //skip over open/close quotes, we don't need them
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeChar() {
        //System.out.println("SEMANTIC: Char");

        //Call our processString method to add our current token to the string
        sentence = processString(sentence);

        //Refresh our token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeSpace() {
        //System.out.println("SEMANTIC: Space");

        //Call our processString method to add our current token to the string
        sentence = processString(sentence);

        //Refresh our token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeCharList() {
        
        //addAST("")
        //System.out.println("SEMANTIC: CharList");
        if(currentToken == "CHAR") {
            analyzeChar();
            analyzeCharList();
        }
        //Finally doing stuff with our space tokens
        else if(currentToken == "CHARSPACE") {
            analyzeSpace();
            analyzeCharList();
        }
        else {
            //If we've reached this point that means our sentence is ready - add to AST and continue
            addAST("[" + sentence + "]", depth);
            sentence = "";
        }
    }

    //Simple little function to concatenate our strings together using the power of our Token class. Thanks, Token class!
    //Basically just pulls the current token's name (not type, which is what we usually have on hand) and adds it to sentence. Since we're always going to be in a string when we reach this function,
    //we can treat every token as a char
    public String processString(String sentence) {
        sentence = sentence + semanticList.get(parseCounter).name;

        return sentence;
    }

    //Revamped to access type checking from the analyzeAssign method - can return a string value based on the type of token being plugged into it
    //A big thank you once again to Stack Overflow for teaching me that I could do this:
    //      https://stackoverflow.com/questions/18855837/initialize-variable-in-recursive-function
    //      https://stackoverflow.com/questions/47250825/recursive-function-variable-initialization 
    public String analyzeExpression() {
        if(currentToken.equals("NUM")) {
            analyzeInt();
            return "int";
        }
        else if(currentToken.equals("OPENSTRING")) {
            analyzeString();
            return "string";
        }
        else if(currentToken.equals("ID")) {
            analyzeID();
            return "id";
        }
        else if(currentToken.equals("OPEN_PAREN")) {
            analyzeBoolean();
            return "bool";
        }
        else if(currentToken.equals("BOOLVAL")) {
            analyzeBoolVal();
            return "bool";
        }
        else {
            errors++;
            return "error";
        }
    }

    public void analyzeID() {
        //System.out.println("SEMANTIC: ID");
        addAST(semanticList.get(parseCounter).name, depth);
        //System.out.println(declaring);
        scopeCheck();
        
        //refresh our token
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    public void analyzeDigit() {
        //System.out.println("SEMANTIC: Digit");
        //just handle this like normal
        handleSemanticToken("NUM", currentToken);
    }

    public void analyzeTypeCheck() {
        System.out.println("SEMANTIC: TypeCheck");
        if((currentToken == "TYPEINT") || (currentToken == "TYPESTRING") || (currentToken == "TYPEBOOL")) {
            switch(currentToken) {
                case("TYPEINT"):
                    handleSemanticToken("TYPEINT", "analyzeInt()");
                break;
                case("TYPESTRING"):
                    handleSemanticToken("TYPESTRING", "analyzeString()");
                break;
                case("TYPEBOOL"):
                    handleSemanticToken("TYPEBOOL", "analyzeBoolean()");
                break;
            }
        }
    }

    public void analyzeBoolOp() {
        //System.out.println("SEMANTIC: BoolOp");
        addAST("Boolean Op", depth);
        
        depth++;
        parseCounter = refresh(parseCounter);
        currentToken = semanticList.get(parseCounter).tokenType;
        depth--;
    }

    public void analyzeBoolVal() {
        //System.out.println("SEMANTIC: BoolVal");
        handleSemanticToken("BOOLVAL", currentToken);
    }

    //Add digit to AST and double check it
    public void analyzeAdd() {
        //System.out.println("Intop");
        addAST("SEMANTIC: Integer Operation", depth);
        depth++;
        handleSemanticToken("INTOP", currentToken);
        depth--;
    }

    //Print AST method
    public void AST() {
        System.out.println();

        for(int i = 0; i < AST.size(); i++) {
            String printToken = AST.get(i);
            String depthPadded = padDepth(astDepth.get(i));
            System.out.println(depthPadded + "<" + printToken + ">");
        }

        //Clear our lists for future use
        AST.clear();
        astDepth.clear();
    }

    //Add AST method, much like how we added to the CST in our parser.
    public void addAST(String tokenInAST, int tokenDepth) {
        //System.out.println("AST addition goes here - current token is " + tokenInAST);
        AST.add(tokenInAST);
        astDepth.add(tokenDepth);
    }

    public String padDepth(int depthToBePadded) {
        String filler = "";
        //If depth = 7, will add 7 "-" symbols to the filler list
        for (int i = 0; i < depthToBePadded; i++) {
            filler = filler + "-";
        }
        return filler;
    }

    //I'm not sure if I still need this but I'll keep it around
    public void handleSemanticToken(String expected, String output) {
        if(currentToken == expected) {
            //if we've parsed over an EOP token, then set our boolean to true and stop the program
            if(currentToken == "EOP_BLOCK") {
                //System.out.println("yay!");
                endTheDamnThing = true;
                //DON'T increment parseCounter here - it's the end of the program, there's nothing else left to read!
            }
            //Else it's just a regular token, handle it and add to the CST
                addAST("[" + semanticList.get(parseCounter).name + "]", depth);
                //System.out.println("Correct Token! It's " + expected); //Use this to test
                parseCounter++;
        }
        //If it doesn't match, then we throw an error and increment parseCounter
        else {
            System.out.println("ERROR: expected " + expected + ", got token " + currentToken +  " in scope " + scope + " on line " + semanticList.get(parseCounter).lineCount);
        }
        //Set our parser token to the next token once we're done
        currentToken = semanticList.get(parseCounter).tokenType;
    }

    //replaces the current token with the next token in line, effectively "refreshing" it. Basically the same as what was going on in the old handleParseToken
    //function in the Parser, only that this has a lot less steps
    public int refresh(int parseCounter) {
        parseCounter++;
        currentToken = semanticList.get(parseCounter).tokenType;

        return parseCounter;
    }

    //Check the scope of our ID tokens
    public void scopeCheck() {

        //If we're initializing or declaring a variable, this is where we go
        if(declaring == true) {

        //Backup variable to store the results of our symbolMatch class
        //symbolMatch will scope check the AST and return the scope of any other symbols that have the same declaration
            int backup = symbolMatch(symbolList, scope);
            
            if(backup != 300) {

                //If we've got a similar declaration in a scope higher or lower than our current symbol, then that means we can definitely initialize it here, since it won't overlap with any pre-existing
                //declarations in this scope
                if(backup > scope || backup < scope) {
                    //System.out.println("backup completed!");
                    symbolList.add(new Symbol(semanticList.get(parseCounter).name, semanticList.get(parseCounter-1).name, scope));

                    //Set our declared flag to true since we just added it to our symbol table
                    symbolList.get(symbolList.size()-1).isItDeclared = true;
                }

                //If we've got the same scope for both matching declarations, throw an error
                else if(backup == scope) {
                    declError();
                    errors++;
                }
            }

            //If it's not recognized in the symbol table search, then it's a completely new token for this program - add it to the table
            else {
                symbolList.add(new Symbol(semanticList.get(parseCounter).name, semanticList.get(parseCounter-1).name, scope)); //move this
                symbolList.get(symbolList.size()-1).isItDeclared = true;
            }
        declaring = false;
        }

        //If we're assigning a value to a variable
        if(assigning == true) {

            int backup = symbolMatch(symbolList, scope);
            if(backup != 300) {

            //If our target scope and current scope are equal (or if current scope is greater than the backup)
            if(backup < scope || backup == scope) {

                
                //This is currently wonky, working on fixing it
                for (Symbol symbol : symbolList) {
                    if (symbol.scope == scope) {
                        symbol.isItUsed = true;
                    }
                }
            }

            //We can't assign stuff if it's in a higher scope than us - throw an error
            else if(backup > scope) {
                //System.out.println("secondary backup completed?");
                assignError();
                errors++;
            }
            else {
                for (Symbol symbol : symbolList) {
                    if (symbol.scope == scope) {
                        symbol.isItUsed = true;
                    }
                }
            }
            assigning = false;
            }

            else {
                assignError();
                errors++;
            }
            //Set prevtoken to the last ID we parsed
            prevToken = semanticList.get(parseCounter).name;
        }
    }

    //Print our symbol table
    public void printSymbolList() {
        //print out table - put this in its own method
        System.out.println();
        System.out.println("NAME | TYPE | SCOPE | IS DECLARED? | IS USED?");
        System.out.println("-----------------------------------------------");
        int specialWarning = 0; //I am very creative
        for(int i = 0; i < symbolList.size(); i++) {
            System.out.println(symbolList.get(i).symbolType + "     " +  symbolList.get(i).name + "     " + symbolList.get(i).scope + "     " + symbolList.get(i).isItDeclared + "      " + symbolList.get(i).isItUsed); //thing that we want to compare
            if(symbolList.get(i).isItDeclared == true && symbolList.get(i).isItUsed == false) {
                specialWarning++;
            }
        }
        if(specialWarning > 0) {
            System.out.println("WARNING: variables declared but never used");
        }
    }

    //Used ChatGPT a little bit for this - asked to optimize the name matching between the two compared lists
    //Parses through the entire symbol list to see if we've got any that are similar in type
    public int symbolMatch(ArrayList<Symbol> symbolList, int scope) {
        String currentToken = semanticList.get(parseCounter).name; //Declaring currentToken again in case

        for (Symbol symbol : symbolList) {
            if (symbol.symbolType.equals(currentToken)) {
                //If our names and scopes match, great! hand it back to our main loop and set it to declared
                if (symbol.scope == scope) {
                    return symbol.scope;
                }
                //If our names match but our scopes don't, return scope anyways
                else {
                    return symbol.scope;
                }
            }
        }
        return 300; //Returns a dummy number if nothing matches
    }

    //Carbon copy of our scope comparison method, this time tweaked a bit to compare token types instead
    public String typeMatch(ArrayList<Symbol> symbolList, String prevToken, int scope) {

        for (Symbol symbol : symbolList) {
            //System.out.println("current symbol is " + symbol.symbolType);

            //If our current symbol's type matches with the previous ID's type, then we can assign
            if (symbol.symbolType.equals(prevToken)) {
                //If our names and scopes match, great! hand it back to our main loop and set it to declared
                if (symbol.scope == scope) {
                    //System.out.println("yay!!!");
                    return symbol.name;
                    
                }
                //If our names match but our scopes don't, return name anyways
                else {
                    return symbol.name;
                }
            }
        }
        return "nothing"; //Returns a dummy output if nothing matches
    }

    //Assignment error printoud method - used for scope errors
    public void assignError() {
        System.out.println("ERROR: the token " + currentToken + " was used before being declared on line " + semanticList.get(parseCounter).lineCount);
    }

    //Another scope error print method
    public void declError() {
        System.out.println("ERROR: the token " + currentToken + " has already been declared in scope " + scope + " on line " + semanticList.get(parseCounter).lineCount);
    }

    //Assignment mismatch error method
    public void mismatchError(String pasterToken, String firstType, int lineThingy) {
        System.out.println("ERROR: Expected " + pasterToken + " token for assignment, got " + firstType + " on line " + lineThingy);
    }
}
