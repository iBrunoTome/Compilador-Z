package compilador.lexico;

import java.util.Hashtable;

/**
 * Classe que representa uma tabela de símbolos, obtida na página 57 do Dragon Book
 *
 * @author Bruno Tomé - 0011254
 * @author Cláudio Menezes - 0011255
 * @author Vinícius Duarte - 0011918
 * @since 29/05/2016
 */
public class TabelaDeSimbolos {

	private Hashtable table;

	public TabelaDeSimbolos() {
		this.table = new Hashtable();
	}

	/**
	 * Checa se o token já existe na tabela
	 *
	 * @param lexema
	 * @return Token || null
	 */
	public Token tokenExist(String lexema) {
		return (Token) this.table.get(lexema);
	}

	/**
	 * Adiciona um Token na tabela de símbolos
	 *
	 * @param token
	 */
	public void put(Token token) {
		Token aux = this.tokenExist(token.getLexema());
		if (aux == null) {
			this.table.put(token.getLexema(), token);
		} else {
			aux.addLinha(token.getLinhas().get(0));
			aux.addColuna(token.getColunas().get(0));
			this.table.put(aux.getLexema(), aux);
		}
	}

	@Override
	public String toString() {
		return this.table.toString();
	}

}
