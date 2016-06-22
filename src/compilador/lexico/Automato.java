package compilador.lexico;

/**
 * Classe que enumera os estados do Autômato utilizado no analisador léxico
 *
 * @author Bruno Tomé - 0011254
 * @author Cláudio Menezes - 0011255
 * @author Vinícius Duarte - 0011918
 * @since 28/05/2016
 */
public enum Automato {
	NAO_ALFA_NUMERICO,
	CONSTANTE_INTEIRA,
	CONSTANTE_REAL,
	ANALISA_PARTE_DECIMAL,
	ID_PALAVRA_RESERVADA,
	CADEIA,
	BLOCO_COMENTARIO
}