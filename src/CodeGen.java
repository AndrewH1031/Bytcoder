//placeholder stuff tee hee
//obviously non functional right now

import java.util.ArrayList;

public class CodeGen {
    
    ArrayList<String> genTable; //import this from Semantic
    ArrayList<Symbol> symbolOp; //project said to include this idk what to do with it yet
    ArrayList<String> opCodeList = new ArrayList<>(); //should be under 256, else throw error

    int newScope; //Copy of scope, might need this
    //Pass existing scope over? or use it from symbol class
    int opCounter;
    int declCounter; //Used for vardecl so we can store each variable in a unique address 
    int heapCount;


    public void main(ArrayList<String> list, ArrayList<Symbol> symbolList) {
        //System.out.println("IOU one Code Gen");

        genTable = list;
        symbolOp = symbolList;

        newScope = -1;
        heapCount = 255;

        System.out.println(genTable); //testing

        genCode();
        //check for error (>256 opcodes)
        printCode();
        
        //call stack??
    }

    public void genCode() {
        //System.out.println("genCode");

        //Loop through imported AST, check to see if our AST tokens match
        //Should match:
        //Assign
        //If
        //while
        //variable decl
        //strings
        //block statements
        //boolops
        //bool vals
        //something else

        //Rudimentary loop to parse through our AST we passed from semantic, will get around to including all tokens
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

                    newScope--;
                    //System.out.println("newScope is " + newScope); //testing

                break;
                case "Assign":

                    i++;

                    System.out.println("Assign statement");

                    
                    //check for int or string
                    Symbol tempSymbol;

                    

                    for(int j = 0; j < symbolOp.size(); j++) {
                        //System.out.println(symbolOp.get(j).name);
                        //System.out.println(symbolOp.get(j).symbolType);
                        //System.out.println(newScope);
                        //System.out.println("current token is " + genTable.get(i));

                        if (symbolOp.get(j).name == "int" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            //System.out.println("yayy!!!" + symbolOp.get(j).scope);
                            tempSymbol = symbolOp.get(j);
                            System.out.println("current token is" + genTable.get(i));
                            i++;
                            String tempy = "";
                            System.out.println("currenterrrr token is " + genTable.get(i));

                            addOpCodes("A9");

                            if(genTable.get(i).substring(1, genTable.get(i).length() - 1).length() < 2) {
                                tempy = "0" + Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, genTable.get(i).length() - 1)));
                                addOpCodes(tempy);
                            }
                            else {
                                addOpCodes(Integer.toHexString(Integer.valueOf(genTable.get(i).substring(1, genTable.get(i).length() - 1))));
                            }

                            addOpCodes("8D");
                            addOpCodes("T0");
                            addOpCodes("XX");

                            //expand this, it's definitely not long enough
                            //include assigning for IDs, strings and ints

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
                            heapCount--;
                            addOpCodes("00");
                            
                            for (int x = 0; x < currentString.length(); x++) {
                                addOpCodes("A9");
                                addOpCodes(Integer.toHexString((int)currentString.charAt(x)));
                                addOpCodes("8D");
                                addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                                heapCount--;
                                addOpCodes("00");
                                //System.out.println(currentString.charAt(x) + " is our currentString"); //test
                            }

                            addOpCodes("A9");
                            addOpCodes(Integer.toHexString(heapCount + 1));
                            addOpCodes("8D");
                            addOpCodes("00"); //placeholder
                            addOpCodes("00");

                            break;
                        }

                        else if (symbolOp.get(j).name == "bool" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                            System.out.println("boolean is supposed to be here");
                            i++;

                            addOpCodes("A9");
                            
                            if(genTable.get(i).equals("[true]")) {
                                System.out.println("true is supposed to go here");
                                addOpCodes("01");
                            }
                            else {
                                System.out.println("false is supposed to go here");
                                addOpCodes("00");
                            }

                            addOpCodes("8D");
                            addOpCodes("00"); //placeholder
                            addOpCodes("XX");
                        }
                    }

                    /*if(genTable.get(i).length() > 2) {
                        System.out.println("example test " + symbolOp.get(2).symbolType);
                        System.out.println("example test " + symbolOp.get(2).name);
                        System.out.println("example test " + symbolOp.get(2).scope);
                        System.out.println("example test " + genTable.get(i));
                    }*/
                    
                    


                break;
                case "Variable Dec":
                    i = i + 2; //need to skip the next two since we can just add the value directly to memory now
                    System.out.println("Vardecl");

                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T" + Integer.toString(declCounter)); //temp value to store in memory
                    addOpCodes("XX"); //have to change later
                    declCounter++;
                    
                break;
                case "Print":
                    System.out.println("Print");
                    i++;

                    System.out.println(genTable.get(i).substring(0, 1));

                    //Substring is a very helpful tool to grab our next token, free of charge
                    if(genTable.get(i).substring(0, 1).equals("(")) {
                        System.out.println("this is where strings should go");

                        String currentString = genTable.get(i).substring(1, genTable.get(i).length() - 1);

                        addOpCodes("A9");
                        addOpCodes("00");
                        addOpCodes("8D");
                        addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                        heapCount--;
                        addOpCodes("00");

                        //Straight carbon copy of the loop from Assign, it's stupid but it works
                        for (int y = 0; y < currentString.length(); y++) {
                            addOpCodes("A9");
                            addOpCodes(Integer.toHexString((int)currentString.charAt(y))); //make this uppercase eventually
                            addOpCodes("8D");
                            addOpCodes(Integer.toHexString(heapCount).toUpperCase());
                            heapCount--;
                            addOpCodes("00");
                        }

                        addOpCodes("A9");
                        addOpCodes(Integer.toHexString(heapCount + 1).toUpperCase());
                        addOpCodes("8D");
                        addOpCodes("T" + Integer.toString(declCounter).toUpperCase());
                        addOpCodes("XX");
                        addOpCodes("A2");
                        addOpCodes("02");
                        addOpCodes("AC");
                        addOpCodes("T" + Integer.toString(declCounter).toUpperCase());
                        declCounter++;
                        addOpCodes("XX");
                        addOpCodes("FF");


                    }
                    //If our print statement is only one char long, then we've got an ID on our hands
                    //No need to double check our type/syntax since our previous parts of the compiler have that taken care of
                    else if (genTable.get(i).length() < 2) {
                        System.out.println("This is where variables go");

                        addOpCodes("A2");

                        for(int j = 0; j < symbolOp.size(); j++) { //expand this

                            //Checking for an int with exact name and type as our current symbol
                            if (symbolOp.get(j).name == "int" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                System.out.println("this int goes hereeeeee");
                                addOpCodes("01");

                            }

                            //Checking for a boolean type

                            //Merge this with int?
                            else if (symbolOp.get(j).name == "boolean" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                System.out.println("this boolean goes hereeeeee");
                                addOpCodes("01");
                            }

                            //Lastly, check for a string type
                            else if (symbolOp.get(j).name == "string" && symbolOp.get(j).symbolType.equals(genTable.get(i))) {
                                System.out.println("this string goes hereeeeee");
                                addOpCodes("02");
                            }
                            addOpCodes("AC");
                            addOpCodes("00"); //placeholder - upgrade this later
                            addOpCodes("XX");
                            addOpCodes("FF");
                        }
                    }

                    //Checking if we want to print a boolean value instead
                    //I should really remove the brackets on these but I guess it's fine for now....
                    else if(genTable.get(i).equals("[true]") || genTable.get(i).equals("[false]")) {
                        //If the value is true
                        if(genTable.get(i).equals("[true]")) {
                            System.out.println("print true");
                            addOpCodes("A2");
                            addOpCodes("01");
                            addOpCodes("A0");
                            //Set the value to 1 for true
                            addOpCodes("01");
                        }
                        //else it's false, change the loaded variable
                        else {
                            System.out.println("print false");
                            addOpCodes("A2");
                            addOpCodes("01");
                            addOpCodes("A0");
                            //Set the value to 0 for false
                            addOpCodes("00");
                        }
                        

                    }

                    //If it's NONE of those things, do something (don't know what yet);
                    else {
                        System.out.println("idk what to put here");
                    }

                break;
                case "If":
                    System.out.println("Ifstatement");
                    i++;
                    boolean notDoneYet = true;
                    boolean negation = false;
                    addOpCodes("A9");
                    addOpCodes("00");
                    addOpCodes("8D");
                    addOpCodes("T0");
                    addOpCodes("XX");
                    

                        //add branching, int expr and bool checking to this


                break;
                case "While":
                    System.out.println("Whilestatement");



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
        int tempcount = 1; //temporary counter to see our order of op codes

        //Print our opcodes as we've initialized them
        for (int i = 0; i < opCodeList.size(); i++) {
            System.out.println(tempcount + ". " + opCodeList.get(i));
            tempcount++;
        }
    }

    //stack stuff goes here


}