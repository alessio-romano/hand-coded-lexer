import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.io.FileReader;

public class Lexer {

    private static final boolean DEBUG = true;

    private static final String RELOP = "RELOP";
    String buffer="";
    private int beginLexem;
    private int forward;
    private File input;
    private static HashMap<String, Token> stringTable;
    private int state;
    FileReader fileReader;

    public Lexer(){
        stringTable = new HashMap<>();//La symbol table in questo caso equivale alla stringTable
        beginLexem = 0;
        state = 0;
        // Inserimento delle parole chiave nella stringTable. Questa scelta è stata fatta
        // al fine di non costruire un diagramma di transizione per ogni parola chiave.
        // Le parole chiave verranno "catturate" dal diagramma di transizione e gestite e di conseguenza.
        // IF poteva anche essere associato ad una costante numerica
        stringTable.put("if", new Token("IF"));
        stringTable.put("then", new Token("THEN"));
        stringTable.put("else", new Token("ELSE"));
        stringTable.put("while", new Token("WHILE"));
        stringTable.put("int", new Token("INT"));
        stringTable.put("float", new Token("FLOAT"));
    }

    public Boolean initialize(String filePath) throws IOException {
        //Prepara file input per lettura e controlla errori
        input = new File(filePath);
        fileReader = new FileReader(input);
        int i;
        while((i = fileReader.read()) != -1){
            buffer += (char) i;
        }
        buffer += "\0";
        if(DEBUG) System.out.println(buffer);
        return true;
    }

    public Token nextToken(){
        //Ad ogni chiamata del lexer (nextToken()) si resettano tutte le variabili utilizzate
        int zeroCounter = 0;
        forward = beginLexem;

        if(DEBUG){
            System.out.println("Dim. buffer: " + buffer.length());
            System.out.println("Valore forward: " + forward);
        }

        state = 0;
        String lessema = ""; //Corrisponde al lessema riconosciuto
        char c;
        boolean flag = true;

        //Utilizziamo una flag il cui scopo è quello di indicare l'eventuale raggiungimento
        //della fine del file, corrispondente al carattere '\0' all'interno della stringa
        while(flag){
            c = buffer.charAt(forward);
            if(c == '\0') flag = false;
            forward++;

            if (DEBUG) {
                System.out.println("-----------------------");
                System.out.println("Carattere Letto: " + c);
                System.out.println("State: " + state);
                System.out.println("Forward: " + forward);
                System.out.println("Zerocounter:" + zeroCounter);
                System.out.println("-----------------------");
            }

            switch (state) {
                case 0:
                    //RELOP
                    if (c == '<') {
                        state = 1;
                    } else if (c == '=') {
                        state = 5;
                        beginLexem = forward;
                        return new Token(RELOP, "EQ");
                    } else if (c == '>') {
                        state = 6;
                        //ID
                    } else if (Character.isLetter(c)) { //ex stato 9
                        lessema += c;
                        state = 10;
                        //WHITESPACE
                    } else if (Character.isWhitespace(c)) { //ex stato 22
                        state = 23;
                        //NUMBERS
                    } else if (Character.isDigit(c)) { //ex stato 12
                        if(c == '0'){
                            state = 12;
                        } else {
                            state = 13;
                        }
                        lessema += c;
                    }
                    //SEPARATORS
                    else if (c == ';') {
                        beginLexem = forward;
                        return new Token("SEMICOLON");
                    } else if (c == ',') {
                        beginLexem = forward;
                        return new Token("COMMA");
                    } else if (c == '(') {
                        beginLexem = forward;
                        return new Token("LPAR");
                    } else if (c == ')') {
                        beginLexem = forward;
                        return new Token("RPAR");
                    } else if (c == '{') {
                        beginLexem = forward;
                        return new Token("LBRAC");
                    } else if (c == '}') {
                        beginLexem = forward;
                        return new Token("RBRAC");
                    }
                    else if(c != '\0'){
                        beginLexem = forward;
                        lessema+=c;
                        return new Token("ERROR",lessema);
                    }
                    break; //end case 0
                case 1:
                    if (c == '=') {
                        state = 2;
                        beginLexem = forward;
                        return new Token(RELOP, "LE");
                    } else if (c == '>') {
                        state = 3;
                        beginLexem = forward;
                        return new Token(RELOP, "NE");
                    } else if (c == '-') {
                        state = 25;
                    } else {
                        state = 4;
                        retrack();
                        return new Token(RELOP, "LT");
                    } //end case 1
                    break;
                case 25:
                    if (c == '-') {
                        state = 26;
                        beginLexem = forward;
                        return new Token("ASSIGN");
                    }
                case 6:
                    if (c == '=') {
                        state = 7;
                        beginLexem = forward;
                        return new Token(RELOP, "GE");
                    } else {
                        state = 8;
                        retrack();
                        return new Token(RELOP, "GT");
                    }
                    //ID
                case 10:
                    if (Character.isLetterOrDigit(c)) {
                        lessema += c;
                    } else {
                        state = 11;
                        retrack();
                        return installID(lessema);
                    }
                    break;
                    //UNSIGNED NUMBERS
                case 12:
                    if(c == '.'){
                        state = 14;
                        lessema += c;
                    } else {
                        state = 20;
                        retrack();
                        return new Token("NUMBER", lessema);
                    }
                    break;
                case 13:
                    if (c == '.') {
                        state = 14;
                        lessema += c;
                    } else if (!Character.isDigit(c)) {
                        state = 20;
                        retrack();
                        return new Token("NUMBER", lessema);
                    } else { //stiamo ancora leggendo un numero
                        lessema += c;
                        //lo stato è sempre 13 quindi non va modificato
                    }
                    break;
                case 14:
                    if (Character.isDigit(c)) {
                        if(c != '0') {
                            state = 15;
                        }
                        else{
                            zeroCounter++;
                            state = 30;
                        }
                        lessema += c;
                    } else {
                        retrack();
                        retrack();
                        //Avendo trovato un token errato restituiamo il primo token corretto
                        //più lungo possibile
                        String x = lessema.substring(0,lessema.length()-1);
                        return new Token("NUMBER", x);
                    }
                    break;
                case 15:
                    if (Character.isDigit(c)) {
                        if(c == '0') {
                            state = 30;
                            zeroCounter++;
                        }
                        lessema += c;
                    } else if (c == 'E' || c == 'e') {
                        state = 16;
                        lessema += c;
                    } else {
                        state = 21;
                        retrack();
                        return new Token("NUMBER", lessema);
                    }
                    break;
                case 16:
                    if (Character.isDigit(c)) {
                        state = 18;
                        lessema += c;
                    } else if (c == '+' || c == '-') {
                        state = 17;
                        lessema += c;
                    }
                    break;
                case 17:
                    if (Character.isDigit(c)) {
                        state = 18;
                        lessema += c;
                    }
                    break;
                case 18:
                    if (!Character.isDigit(c)) {
                        state = 19;
                        retrack();
                        return new Token("NUMBER", lessema);
                    } else { //sto leggendo ancora un numero
                        lessema += c;
                    }
                    break;
                case 30:
                    if(Character.isDigit(c)){
                        if(c != '0') {
                            state = 15;
                            zeroCounter = 0; //azzero perchè altrimenti considero anche zeri precedenti
                        }
                        else{
                            zeroCounter++;
                        }
                        lessema+=c;
                    }
                    else{
                        forward -= zeroCounter;
                        retrack();
                        String x = lessema.substring(0,lessema.length()-zeroCounter);
                        return new Token("NUMBER", x);
                    }
                    break;
                case 23:
                    if (!Character.isWhitespace(c)) {
                        state = 0;
                        retrack();
                    }
                    //else {sto leggendo ancora un ws e quindi resto nello stato 23}
                    break;
                default:
                    break;
            } //end switch
        }//end while
        return null;
    }//end method

    private Token installID(String lessema){
        Token token;
        //utilizzo come chiave della hashmap il lessema
        if(stringTable.containsKey(lessema)) {
            return stringTable.get(lessema);
        } else {
            token =  new Token("ID", lessema);
            stringTable.put(lessema, token);
            return token;
        }
    }

    private void retrack(){
        //fa il retract nel file di un carattere
        forward--;
        beginLexem = forward;
    }

    public void printStringTable(){
        Token tokens[] = stringTable.values().toArray(new Token[0]);
        System.out.println("STRING TABLE");
        System.out.println("------------");
        stringTable.entrySet().forEach(stringTokenEntry -> {
            System.out.println(stringTokenEntry.getKey() + "\t | " + stringTokenEntry.getValue());
        });
    }
}// end class