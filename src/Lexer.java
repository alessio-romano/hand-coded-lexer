import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Lexer {

    private static final boolean DEBUG = true;

    private static final String RELOP = "RELOP";
    private Scanner sc;
    private char[] buffer;
    private int beginLexem;
    private int forward;
    private File input;
    private static HashMap<String, Token> stringTable;  // la struttura dati potrebbe essere una hash map
    private int state;

    public Lexer(){
        // la symbol table in questo caso la chiamiamo stringTable
        stringTable = new HashMap<>();
        state = 0;
        stringTable.put("if", new Token("IF"));   // inserimento delle parole chiavi nella stringTable per evitare di scrivere un diagramma di transizione per ciascuna di esse (le parole chiavi verranno "catturate" dal diagramma di transizione e gestite e di conseguenza). IF poteva anche essere associato ad una costante numerica
        stringTable.put("then", new Token("THEN"));
        stringTable.put("else", new Token("ELSE"));
        stringTable.put("while", new Token("WHILE"));
        stringTable.put("int", new Token("INT"));
        stringTable.put("float", new Token("FLOAT"));

    }

    public Boolean initialize(String filePath) throws FileNotFoundException {
        // prepara file input per lettura e controlla errori
        input = new File(filePath);
        sc = new Scanner(input);
        riempiBuffer();
        return true;
    }

    public Token nextToken()throws Exception{

        //Ad ogni chiamata del lexer (nextToken())
        //si resettano tutte le variabili utilizzate

       forward = beginLexem;

        if(DEBUG){
            System.out.println("Dim. buffer: " + buffer.length);
            System.out.println("Valore forward: " + forward);
        }

        //boolean endOfFile = (buffer[forward] == '\r' && buffer[forward+1] == '\n');

        state = 0;
        String lessema = ""; //è il lessema riconosciuto
        char c;

        while(true){

            // legge un carattere da input e lancia eccezione quando incontra EOF per restituire null
            //  per indicare che non ci sono più token
            if(forward >= buffer.length){ //significa che la linea è terminata
                riempiBuffer();
            } /*else {
                return null;
            }*/
            c = buffer[forward];
            forward++;

            if (DEBUG) {
                System.out.println("-----------------------");
                System.out.println("Carattere Letto: " + c);
                System.out.println("State: " + state);
                System.out.println("Forward: " + forward);
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
                        /*if (endOfFile) {
                            return installID(lessema);
                        }*/
                        //WHITESPACE
                    } else if (Character.isWhitespace(c)) { //ex stato 22
                        state = 23;
                        //NUMBERS
                    } else if (Character.isDigit(c)) { //ex stato 12
                        state = 13;
                        lessema += c;
                        /*if (endOfFile) {
                            return new Token("NUMBER", lessema);
                        }*/
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
                        /*if (endOfFile) {
                            return installID(lessema);
                        }*/
                    } else {
                        state = 11;
                        retrack();
                        return installID(lessema);
                    }
                    break;
                    //unsigned numbers
                case 13:
                    if (c == '.') {
                        state = 14;
                        lessema += c;
                    } else if (!Character.isDigit(c)) {
                        state = 20;
                        retrack();
                        return new Token("NUMBER", lessema);
                    } else { //sto leggendo ancora un numero
                        lessema += c;
                        //lo stato è sempre 13 quindi non si modifica
                    }
                    break;
                case 14:
                    if (Character.isDigit(c)) {
                        state = 15;
                        lessema += c;
                    } else {
                        //da gestire in maniera diversa in modo da non eliminare tutto ciò letto finora
                        return new Token("ERROR", lessema);
                    }
                    break;
                case 15:
                    if (Character.isDigit(c)) {
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
                       /* if (endOfFile) {
                            return new Token("NUMBER", lessema);
                        }*/
                    } else if (c == '+' || c == '-') {
                        state = 17;
                        lessema += c;
                    }
                    break;
                case 17:
                    if (Character.isDigit(c)) {
                        state = 18;
                        lessema += c;
                        /*if (endOfFile) {
                            return new Token("NUMBER", lessema);
                        }*/
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
                case 23:
                    if (!Character.isWhitespace(c)) {
                        state = 24;
                    }
                    //else {sto leggendo ancora un ws e quindi resto nello stato 23}
                    break;
                case 24:
                    state = 0;
                    retrack();
                default:
                    break; //eventualmente anche errore qui
            } //end switch
          /*  if(forward >= buffer.length){
                riempiBuffer();
            }*/
        }//end while
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
        // fa il retract nel file di un carattere
        forward--;
        beginLexem = forward;
    }

    private void riempiBuffer(){

        String line;
        if(sc.hasNext("(.|[[:cntrl:]])*")){
            line = sc.next("(.|[[:cntrl:]])*"); //cambia

            buffer = new char[line.length()];
            buffer = line.toCharArray();
            forward = 0;
            beginLexem = forward;
            if(DEBUG) {
                System.out.println("Linea: " + line);
                System.out.println("Dim. linea: " + line.length());
            }
        }
    }
}// end class