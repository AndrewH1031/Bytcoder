//placeholder stuff tee hee
//obviously non functional right now

import java.util.ArrayList;
import java.util.regex.Pattern;

public class CodeGen {
    
    ArrayList<String> genTable; //import this from Semantic
    ArrayList<Symbol> symbolOp; //project said to include this idk what to do with it yet
    ArrayList<String> opCodeList = new ArrayList<>(); //should be under 256, else throw error
    ArrayList<String> stackList = new ArrayList<>();

    int newScope; //Copy of scope, might need this
    //Pass existing scope over? or use it from symbol class
    int opCounter;
    int declCounter; //Used for vardecl so we can store each variable in a unique address 
    int heapCount; //Used to track our heap values to assign for some of our variables
    int errors;

    int branchNum = 0;
    int jumpVal;
    int newerScope;

    boolean stopAddingFirst;
    boolean stopAddingSecond;
    boolean notEquals;
    boolean areWeJumping = false;


    public void main(ArrayList<String> list, ArrayList<Symbol> symbolList) {
        //System.out.println("IOU one Code Gen");

        genTable = list;
        symbolOp = symbolList;

        newScope = -1; //same as last time, dumb but it works
        heapCount = 255; //Heap pointer - starts at address FF and works its way down

        System.out.println(genTable); //testing
        for(int z = 0; z < symbolList.size(); z++) {
            System.out.println(symbolList.get(z).symbolType + " ");
        }

        genCode();
        addOpCodes("00");
        if(opCodeList.size() > 256) {
            System.out.println("ERROR: Cannot exceed more than 256 OpCodes in one program.");
            errors++;
        }
        if(errors > 0) {
            System.out.println("CODE GEN: OPCODE GENERATION SKIPPED DUE TO CODE GEN ERORRS");
        }
        else {
            System.out.println("Printing OpCodes...");
            System.out.println();
            handleStack(); //need to process temp codes before we can print
            printCode();
        }

    }

    public void genCode() {
        stackList.add("T0XX");
        stackList.add("T1XX");
        System.out.println(stackList);
        //System.out.println("genCode");

        //Rudimentary loop to parse through our AST we passed from semantic
        //System.out.println("IOU one genCode");
        for(int i = 0; i < genTable.size(); i++) {
            System.out.println("codepointer is " + opCounter);
            
            switch(genTable.get(i)) {
                case "Open Block":
                    System.out.println("oblock");

                    newScope++; //increment scope when we have a new block like usual
                    //System.out.println("newScope is " + newScope); //testing

                break;
                case "Close Block":
                    System.out.println("closeblock");

                    System.out.println(areWeJumping);
                    System.out.println("current newerscope scope is " + newerScope);
                    System.out.println("newS cope is " + newScope);

                    newScope--;
                    System.out.println("newS cope is " + newScope); //testing
                    if(areWeJumping == true) {
                        System.out.println("we did it!");
                        addOpCodes("A9");
                            addOpCodes("01");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("A2");
                            addOpCodes("02");
                            addOpCodes("EC");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("D0");
                            //Branch this # to get back to the beginning of the loop
                            int goBack = (255 - opCounter) + branchNum;
                            addOpCodes(Integer.toHexString(goBack));
                            areWeJumping = false;
                    }

                break;
                case "Assign":

                    i++;

                    System.out.println("Assign statement");

                    
                    //check for int or string
                    Symbol tempSymbol;

                    

                        //put id checking here
                    //if(!genTable.get(i + 1).length > 3) || Character.isDigit()

                    

                    for(int j = 0; j < symbolOp.size(); j++) {
                        //System.out.println(symbolOp.get(j).name);
                        //System.out.println(symbolOp.get(j).symbolType);
                        //System.out.println(newScope);
                        //System.out.println("current token is " + genTable.get(i));

                        if (symbolOp.get(j).name == "int" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("yayy!!!" + symbolOp.get(j).scope);
                            tempSymbol = symbolOp.get(j);
                            i++;
                            String tempy = "";

                            addOpCodes("A9");

                            if(genTable.get(i).substring(1, genTable.get(i).length() - 1).length() < 3) {
                                tempy = "0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, genTable.get(i).length() - 1)));
                                tempy = tempy.toUpperCase();
                                addOpCodes(tempy);
                                System.out.println("tempy is " + tempy);
                            }
                            else {
                                addOpCodes(Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, genTable.get(i).length() - 1))));
                            }

                            addOpCodes("8D");
                            //something wrong here
                            addOpCodes("T0");
                            addOpCodes("00");

                            /*if(genTable.get(i + 1).equals("IntOp") || (genTable.get(i).equals("[+]"))) {
                                System.out.println("yayyy!!!!");
                            }*/

                            //If we've got an ID coming up, then match it
                            if(Pattern.matches("[a-z]", genTable.get(i).substring(1, 2))) {
                                System.out.println("right where we need to be");

                            }
                            //else it's a number, act accordingly
                            else {
                                System.out.println("right where we DON'T need to be");
                                

                            }
                            break;
                        }


                        else if (symbolOp.get(j).name == "string" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("nooooo!!!!" + symbolOp.get(j).scope);
                            //System.out.println(symbolOp.get(j).scope);
                            tempSymbol = symbolOp.get(j);
                            System.out.println("current symbol is " + tempSymbol.name);
                            i++;
                            System.out.println("currenterrrr token is " + genTable.get(i));

                            String currentString = genTable.get(i).substring(1, genTable.get(i).length() - 1);

                            addOpCodes("A9");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                            addOpCodes("00");
                            heapCount--;
                            
                            for (int x = 0; x < currentString.length(); x++) {
                                addOpCodes("A9");
                                addOpCodes(Integer.toHexString((int)currentString.charAt(x)).toUpperCase());
                                addOpCodes("8D");
                                addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                                addOpCodes("00");
                                heapCount--;
                                //System.out.println(currentString.charAt(x) + " is our currentString"); //test
                            }

                            addOpCodes("A9");
                            addOpCodes(Integer.toHexString(heapCount + 1).toUpperCase());
                            addOpCodes("8D");
                            addOpCodes("00"); //placeholder
                            addOpCodes("00");

                            break;
                        }

                        else if (symbolOp.get(j).name == "bool" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("boolean is supposed to be here");
                            i++;

                            addOpCodes("A9");
                            
                            if(genTable.get(i).equals("[true]")) {
                                //System.out.println("true is supposed to go here");
                                addOpCodes("01");
                            }
                            else {
                                //System.out.println("false is supposed to go here");
                                addOpCodes("00");
                            }

                            addOpCodes("8D");
                            addOpCodes("00"); //placeholder
                            addOpCodes("00");
                        }
                    }

                break;
                case "Variable Dec":
                    i = i + 2; //Can skip the next two tokens since we can just add the value directly to memory now
                    System.out.println("Vardecl");

                    addOpCodes("A9");
                    System.out.println("T" + Integer.toString(declCounter));
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T" + Integer.toString(declCounter)); //temp value to store in memory
                    //System.out.println("whats the declcounter??? " + declCounter);
                    addOpCodes("00"); //have to change later
                    stackList.add("T" + Integer.toString(declCounter) + "00");
                    declCounter++;
                    
                break;
                case "Print":
                    System.out.println("Print");
                    i++;

                    System.out.println(genTable.get(i).substring(0, 1));

                    //Substring is a very helpful tool to grab our next token wiothout brackets, free of charge
                    if(genTable.get(i).substring(0, 1).equals("(")) {
                        //System.out.println("this is where strings should go");

                        String currentString = genTable.get(i).substring(1, genTable.get(i).length() - 1);
                        stackList.add("T" + Integer.toString(declCounter) + "00");

                        addOpCodes("A9");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                        addOpCodes("00");
                        heapCount--; //Shift to a new heap variable at the end

                        //Straight carbon copy of the loop from Assign, it's stupid but it works
                        for (int y = 0; y < currentString.length(); y++) {
                            addOpCodes("A9");
                            addOpCodes(Integer.toHexString((int)currentString.charAt(y)).toUpperCase());
                            addOpCodes("8D");
                            addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                            addOpCodes("00");
                            heapCount--; 
                        }

                        addOpCodes("A9");
                        addOpCodes(Integer.toHexString(heapCount + 1).toUpperCase());
                        addOpCodes("8D");
                        addOpCodes("T" + Integer.toString(declCounter).toUpperCase());
                        addOpCodes("00");
                        addOpCodes("A2");
                        addOpCodes("02");
                        addOpCodes("AC");
                        addOpCodes("T" + Integer.toString(declCounter).toUpperCase());
                        addOpCodes("00");
                        addOpCodes("FF");
                        declCounter++;


                    }
                    //If our print statement is only one char long, then we've got an ID on our hands
                    //No need to double check our type/syntax since our previous parts of the compiler have that taken care of
                    else if (genTable.get(i).length() < 2) {
                        //System.out.println("This is where variables go");

                        addOpCodes("AC");
                        addOpCodes("00"); //placeholder - upgrade this later
                        addOpCodes("00");
                        addOpCodes("A2");

                        for(int j = 0; j < symbolOp.size(); j++) { //expand this
                            //Checking for an int with exact name and type as our current symbol
                            if (symbolOp.get(j).name.equals("int") && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                //System.out.println("this int goes hereeeeee");
                                addOpCodes("01");

                            }

                            //Checking for a boolean type
                            else if (symbolOp.get(j).name.equals("bool") && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                //System.out.println("this boolean goes hereeeeee");
                                addOpCodes("01");
                            }

                            //Lastly, check for a string type
                            else if (symbolOp.get(j).name.equals("string") && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                //System.out.println("this string goes hereeeeee");
                                addOpCodes("02");
                            }
                        }
                        addOpCodes("FF");
                    }

                    //Checking if we want to print a boolean value instead
                    //I should really remove the brackets on these but I guess it's fine for now....
                    else if(genTable.get(i).equals("[true]") || genTable.get(i).equals("[false]")) {
                        //If the value is true
                        if(genTable.get(i).equals("[true]")) {
                            //System.out.println("print true");
                            addOpCodes("A2");
                            addOpCodes("01");
                            addOpCodes("A0");
                            //Set the value to 1 for true
                            addOpCodes("01");
                            addOpCodes("FF");
                        }
                        //else it's false, change the loaded variable
                        else {
                            //System.out.println("print false");
                            addOpCodes("A2");
                            addOpCodes("01");
                            addOpCodes("A0");
                            //Set the value to 0 for false
                            addOpCodes("00");
                            addOpCodes("FF");
                        }
                    }

                    else {
                        
                    }

                break;

                //If statements - we're gonna take the long way around with this....
                case "If":
                    System.out.println("Ifstatement");
                    i++;
                    //System.out.println("next if is " + genTable.get(i).substring(0, 1));

                    //Sets our boolean values to false, so we don't accidentally trigger a loop
                    stopAddingFirst = false;
                    stopAddingSecond = false;
                    notEquals = false;

                //While our first loop variable is active, search for and add tokens we find into memory
                while(stopAddingFirst == false) {
                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T0");
                    addOpCodes("00");

                    //Matches variables
                    if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                        addOpCodes("AD");
                        addOpCodes("00");
                        addOpCodes("00");
                        addOpCodes("6D");
                        addOpCodes("T0");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes("T0");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes("T0");
                        addOpCodes("00");
                    }

                    //Matches ints
                    else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                        addOpCodes("A9");
                        addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                        addOpCodes("6D");
                        addOpCodes("T0");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes("T0");
                        addOpCodes("00");
                    }

                    else {
                        //Do nothing, only way we should be getting here is if we have an IntOp or [+] token

                    }

                    //If we've reached the end of our loop (when the token size > 3, usually meaning there's a close block up ahead), then break out of the loop
                    if(!genTable.get(i + 1).equals("==") && !genTable.get(i + 1).equals("!=") && !genTable.get(i + 1).equals("IntOp") && genTable.get(i + 1).length() > 3) {
                        stopAddingFirst = true;
                    }

                    //If we've reached our boolean statement, move on to the second loop
                    else if(genTable.get(i + 1).equals("==") || genTable.get(i + 1).equals("!=")) {
                        if(genTable.get(i + 1).equals("!=")) {
                            System.out.println("yipppeeeeee!!!!");
                            notEquals = true;
                        }
                        i = i + 2; //Skip over the boolean token
                        addOpCodes("A9");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes("T1"); //Increment our temp values a bit to accommodate for our new half of the operand
                        addOpCodes("00");

                        //This loop handles the second half of the expression
                        //I probably could have combined both loops into one thing, but whatever....
                        while(stopAddingSecond == false) {

                            //System.out.println("current token is" + genTable.get(i));

                            if(genTable.get(i).equals("[true]")) {
                                //Stores our true boolean value
                                addOpCodes("A9");
                                addOpCodes("01");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if(genTable.get(i).equals("[false]")) {
                                //Stores our false boolean value
                                //System.out.println("this is false, you're doing it right");
                                addOpCodes("A9");
                                addOpCodes("00");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                                //System.out.println("this is for letters");
                                addOpCodes("AD");
                                addOpCodes("00");
                                addOpCodes("00");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                                //Processes numbers
                                //System.out.println("this is for numbers");
                                System.out.println("next if is NOT " + genTable.get(i).substring(1, 2));
                                addOpCodes("A9");
                                addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else {
                                //Do nothing, only way we should be getting here is if we run into a close block token

                            }

                            //If we run into an ending token (NOT intop though), then we break out of the second loop
                            if((genTable.get(i + 1).length() > 3) && (!genTable.get(i + 1).equals("IntOp")))  {
                                stopAddingSecond = true;
                            }
                            
                            //Else we're not done, keep looping
                            else {
                                i++;
                            }
                        }
                        stopAddingFirst = true;

                    }

                    else {
                        i++;
                        //System.out.println("loop again");
                    }
                }
                addOpCodes("AE");
                addOpCodes("T0");
                addOpCodes("00");
                addOpCodes("EC");
                addOpCodes("T1");
                addOpCodes("00");

                //Thanks ChatGPT for giving me a little bit of an idea how to put this in
                if(notEquals == true) {
                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("D0");
                    addOpCodes("02");
                    addOpCodes("A9");
                    addOpCodes("01");
                    addOpCodes("A2");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T0");
                    addOpCodes("00");
                    addOpCodes("EC");
                    addOpCodes("T0");
                    addOpCodes("00");
                }
                addOpCodes("DO"); //Branch instruction - here we'll initialize the jump value for this loop
                addOpCodes("J" + Integer.toString(branchNum));
                branchNum++;

                break;
                case "While":
                    System.out.println("Whilestatement");
                    i++;
                    //System.out.println("next if is " + genTable.get(i).substring(0, 1));

                    jumpVal = opCounter;

                    //Sets our boolean values to false, so we don't accidentally trigger a loop
                    stopAddingFirst = false;
                    stopAddingSecond = false;
                    notEquals = false;

                    while(stopAddingFirst == false) {
                        addOpCodes("A9");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes("T0");
                        addOpCodes("00");

                            //Matches variables
                        if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                            addOpCodes("AD");
                            addOpCodes("00");
                            addOpCodes("00");
                            addOpCodes("6D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                        }

                        //Matches ints
                        else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                            addOpCodes("A9");
                            addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                            addOpCodes("6D");
                            addOpCodes("T0");
                            addOpCodes("00");
                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("00");
                        }
                        
                    else {
                        //Do nothing, only way we should be getting here is if we have an IntOp or [+] token

                    }

                    //If we've reached the end of our loop (when the token size > 3, usually meaning there's a close block up ahead), then break out of the loop
                    if(!genTable.get(i + 1).equals("==") && !genTable.get(i + 1).equals("!=") && !genTable.get(i + 1).equals("IntOp") && genTable.get(i + 1).length() > 3) {
                        stopAddingFirst = true;
                    }

                    //If we've reached our boolean statement, move on to the second loop
                    else if(genTable.get(i + 1).equals("==") || genTable.get(i + 1).equals("!=")) {
                        if(genTable.get(i + 1).equals("!=")) {
                            notEquals = true;
                        }

                        while(stopAddingSecond == false) {
                            if(genTable.get(i).equals("[true]")) {
                                //Stores our true boolean value
                                addOpCodes("A9");
                                addOpCodes("01");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if(genTable.get(i).equals("[false]")) {
                                //Stores our false boolean value
                                //System.out.println("this is false, you're doing it right");
                                addOpCodes("A9");
                                addOpCodes("00");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if(Pattern.matches("[a-z]", genTable.get(i).substring(0, 1))) {
                                //System.out.println("this is for letters");
                                addOpCodes("AD");
                                addOpCodes("00");
                                addOpCodes("00");
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }
                            else if (Pattern.matches("[0-9]", genTable.get(i).substring(1, 2))) {
                                //Processes numbers
                                //System.out.println("this is for numbers");
                                System.out.println("next if is NOT " + genTable.get(i).substring(1, 2));
                                addOpCodes("A9");
                                addOpCodes("0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, 2))));
                                addOpCodes("6D");
                                addOpCodes("T1");
                                addOpCodes("00");
                                addOpCodes("8D");
                                addOpCodes("T1");
                                addOpCodes("00");
                            }

                            else {
                                //Do nothing, only way we should be getting here is if we run into a close block token

                            }

                            //If we run into an ending token (NOT intop though), then we break out of the second loop
                            if((genTable.get(i + 1).length() > 3) && (!genTable.get(i + 1).equals("IntOp")))  {
                                stopAddingSecond = true;
                            }
                            
                            //Else we're not done, keep looping
                            else {
                                i++;
                            }
                        }
                        stopAddingFirst = true;

                    }
                    else {
                        i++;
                        //System.out.println("loop again");
                    }

                }
                addOpCodes("AE");
                addOpCodes("T0");
                addOpCodes("00");
                addOpCodes("EC");
                addOpCodes("T1");
                addOpCodes("00");

                if(notEquals == true) {
                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("D0");
                    addOpCodes("02");
                    addOpCodes("A9");
                    addOpCodes("01");
                    addOpCodes("A2");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T0");
                    addOpCodes("00");
                    addOpCodes("EC");
                    addOpCodes("T0");
                    addOpCodes("00");
                }

                addOpCodes("DO"); //Branch instruction - here we'll initialize the jump value for this loop
                addOpCodes("J" + Integer.toString(branchNum));
                branchNum++;
                areWeJumping = true; //Set our jumping boolean to true, since we need to jump if the while loop isn't completed
                newerScope = newScope;
                

                break;
                default:
                    //testing to make sure I didn't break anything :P
                    System.out.println("default response is " + genTable.get(i));
                break;

            }
        }
    }

    //Adds our opcodes to the list
    public void addOpCodes(String newCode) {
        opCodeList.add(opCounter, newCode);
        opCounter++; //increment counter to let us know where we are after the addition
        
        //System.out.println("CURRENT OPCODE LIST IS " + opCodeList); //testing
    }

    public void printCode() {
        System.out.println();
        System.out.println("CODEGEN: Printing Op Codes:");

        //Print our opcodes as we've initialized them
        for (int i = 0; i < opCodeList.size(); i++) {
            System.out.print(" " + opCodeList.get(i));
        }
    }

    //ChatGPT'd this - was easy printing stack but I asked how to modify the list for each pointer value
    //Modifies our temp variables so we store them in different values in our list depending on their assignment
    public void handleStack() {
        System.out.println(stackList);
        for(int y = 0; y < stackList.size(); y++) {
            for(int z = 0; z < opCodeList.size(); z++) {//If it appears at all
                if(opCodeList.get(z).equals("T" + Integer.toString(y))) {
                    opCodeList.set(z, Integer.toHexString(opCounter));
                }
            }
            opCounter++;
        }
    }


}