//placeholder stuff tee hee
//obviously non functional right now

import java.util.ArrayList;

public class CodeGen {
    
    ArrayList<String> genTable; //import this from Semantic
    ArrayList<Integer> astDepth = new ArrayList<>(); //this one too
    ArrayList<Symbol> symbolList; //project said to include this idk what to do with it yet
    ArrayList<String> opCodeList = new ArrayList<>(); //should be under 256, else throw error


    public void main(ArrayList<String> list) {
        System.out.println("IOU one Code Gen");

        genTable = list;

        genCode();
    }

    public void genCode() {
        System.out.println("genCode");

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
        for(int i = 0; i < genTable.size(); i++) {
            System.out.println("IOU one genCode");
            switch(genTable.get(i)) {
                case "Assign":
                    System.out.println("yay!!!");

            }
        }
    }

}