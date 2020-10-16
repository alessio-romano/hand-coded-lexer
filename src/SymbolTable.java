import java.util.ArrayList;

public class SymbolTable {
    private ArrayList<SymbolTableRow> symTab;

    public SymbolTable() {
        this.symTab = new ArrayList<>();
    }

    public boolean add(int id, String lessema, Token token){
        return symTab.add(new SymbolTableRow(id, lessema, token));
    }

    public Token contain(String lessema){
        for(SymbolTableRow s: symTab){
            if(s.lessema.equals(lessema)){
                return s.token;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        String out= "";
        for(SymbolTableRow s: symTab){
            out += s.toString() + "\n";
        }

        return out;
    }

    class SymbolTableRow {
        private int id;
        private String lessema;
        private Token token;

        public SymbolTableRow(int id, String lessema, Token token) {
            this.id = id;
            this.lessema = lessema;
            this.token = token;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLessema() {
            return lessema;
        }

        public void setLessema(String lessema) {
            this.lessema = lessema;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return "ID: " + id + "\t | Lessema: " + lessema + "\t | Token: " + token.getName();
        }
    }
}
