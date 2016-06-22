package compilador.lexico;

import java.util.ArrayList;

/**
 * Classe que representa um token
 *
 * @author Bruno Tomé - 0011254
 * @author Cláudio Menezes - 0011255
 * @author Vinícius Duarte - 0011918
 * @since 28/05/2016
 */
public class Token {

    private final TagToken tag;
    private String lexema;
    private ArrayList<Integer> linhas = new ArrayList<>();
    private ArrayList<Integer> colunas = new ArrayList<>();

    public ArrayList<Integer> getLinhas() {
        return linhas;
    }

    public void addLinha(int linha) {
        this.linhas.add(linha);
    }

    public ArrayList<Integer> getColunas() {
        return colunas;
    }

    public void addColuna(int coluna) {
        this.colunas.add(coluna);
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public TagToken getTag() {
        return this.tag;
    }

    public Token(TagToken tipo, String lexema) {
        this.tag = tipo;
        this.lexema = lexema;
    }

    public String listaToString(ArrayList<Integer> lista) {
        String linha = "";
        for (Integer lin : lista) {
            linha += " " + lin;
        }
        return linha;
    }

    @Override
    public String toString() {
        return lexema != null ? String.format("\nTAG: %s\nLexema: \"%s\"\nLinhas:  %s\nColunas: %s\n\n", tag, lexema, this.listaToString(linhas), this.listaToString(colunas)) : String.format("<%s>", tag);
    }
}