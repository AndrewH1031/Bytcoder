//Class to store our tokens
//Couldn't make this in the main Lexer file for some reason, so I just gave it its own file
public class Token {
    public String tokenType;
    public String name;
    public int lineCount;
    public int lineNum;
    public int progNum;

    public Token(String tokenType, String name, int lineCount, int lineNum, int progNum) {
        this.tokenType = tokenType;
        this.name = name;
        this.lineCount = lineCount;
        this.lineNum = lineNum;
        this.progNum = progNum;
        
    }
}