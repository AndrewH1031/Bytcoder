
//Figured I'd make this since I can't store the info I want for symbols in the token class anyways
public class Symbol {
    public String symbolType;
    public String name;
    public int scope;

    //Booleans to check if the value is declared or intialized at all; We'll be able to flip these to true or false at will within our main class
    public Boolean isItDeclared;
    public boolean isItUsed;

    public Symbol(String symbolType, String name, int scope) {
        this.symbolType = symbolType;
        this.name = name;
        this.scope = scope;

        isItDeclared = false;
        isItUsed = false;
        
        
    }
}