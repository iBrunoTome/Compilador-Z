package compilador.lexico;

import static compilador.lexico.Automato.*;

/**
 * Analisador léxico
 *
 * @author Bruno Tomé - 0011254
 * @author Cláudio Menezes - 0011255
 * @author Vinícius Duarte - 0011918
 * @since 28/05/2016
 */
public class AnalisadorLexico {

	private TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();
	private boolean comentarioBloco = false;
	private boolean cadeiaNaoFechada = false;
	private String linha;
	private int idxColuna = 0;
	private int idxLinha = 0;
	private int idxLinhaComentario = 0;
	private int idxLinhaCadeia = 0;
	private int qtdErros = 0;
	private char ch;

	public AnalisadorLexico() {

	}

	public TabelaDeSimbolos getTabelaDeSimbolos() {
		return this.tabelaDeSimbolos;
	}

	public int getIdxLinhaCadeia() {
		return this.idxLinhaCadeia;
	}

	public int getIdxLinhaComentario() {
		return this.idxLinhaComentario;
	}

	public int getQtdErros() {
		return this.qtdErros;
	}

	private void readCh() {
		this.ch = this.linha.length() > this.idxColuna ? this.linha.charAt(this.idxColuna++) : 0;
	}

	public void setLinha(String linha) {
		this.linha = linha;
	}

	public void setIdxColuna(int idxColuna) {
		this.idxColuna = idxColuna;
	}

	public void setIdxLinha(int idxLinha) {
		this.idxLinha = idxLinha;
	}


	/**
	 * Checa o próximo char, se não for o esperado deve-se devolver a posição da coluna com idxColuna--
	 *
	 * @param ch char que se espera encontrar
	 * @return true || false
	 */
	private boolean lookahead(char ch) {
		readCh();
		return (this.ch == ch);
	}

	/**
	 * Lê caracteres, identifica e retorna o próximo token, ou nulo se não existir na linha
	 *
	 * @return Token
	 */
	public Token nextToken() {
		Token token = null;
		Automato estado = NAO_ALFA_NUMERICO;
		String lexema = "";

		while (token == null) {
			if (comentarioBloco == true) {
				estado = BLOCO_COMENTARIO;
				if (idxLinhaComentario == 0) {
					idxLinhaComentario = this.idxLinha - 1;
				}
			}

			if (cadeiaNaoFechada == true) {
				estado = CADEIA;
				if (idxLinhaCadeia == 0) {
					idxLinhaCadeia = this.idxLinha - 1;
				}
			}

			switch (estado) {
				/* Identifica operadores e outros símbolos, default == não é um operador símbolo */
				case NAO_ALFA_NUMERICO:
					readCh();
					switch (this.ch) {
						case '+':
							token = new Token(TagToken.OPAD, "+");
							break;
						case '-':
							token = new Token(TagToken.OPAD, "-");
							break;
						case '*':
							token = new Token(TagToken.OPMUL, "*");
							break;
						case ',':
							token = new Token(TagToken.VIRG, ",");
							break;
						case ';':
							token = new Token(TagToken.PVIRG, ";");
							break;
						case '!':
							token = new Token(TagToken.OPNEG, "!");
							break;
						case '{':
							token = new Token(TagToken.ABRECH, "{");
							break;
						case '}':
							token = new Token(TagToken.FECHACH, "}");
							break;
						case '(':
							token = new Token(TagToken.ABREPAR, "(");
							break;
						case ')':
							token = new Token(TagToken.FECHAPAR, ")");
							break;
						case '=':
							token = new Token(TagToken.OPREL, "=");
							break;
						case '.':
							estado = ANALISA_PARTE_DECIMAL;
							continue;
						case '"':
							estado = CADEIA;
							continue;
						case '/':
							if (lookahead('/')) {
								return null;
							} else {
								this.idxColuna--;
								if (lookahead('*')) {
									estado = BLOCO_COMENTARIO;
									break;
								} else {
									this.idxColuna--;
									token = new Token(TagToken.OPMUL, "/");
									break;
								}
							}
						case ':':
							if (lookahead('=')) {
								token = new Token(TagToken.ATRIB, ":=");
							} else {
								this.idxColuna--;
								token = new Token(TagToken.DPONTOS, ":");
							}
							break;
						case '>':
							if (lookahead('=')) {
								token = new Token(TagToken.OPREL, ">=");
							} else {
								this.idxColuna--;
								token = new Token(TagToken.OPREL, ">");
							}
							break;
						case '<':
							if (lookahead('=')) {
								token = new Token(TagToken.OPREL, "<=");
							} else {
								this.idxColuna--;
								if (lookahead('>')) {
									token = new Token(TagToken.OPREL, "<>");
								} else {
									this.idxColuna--;
									token = new Token(TagToken.OPREL, "<");
								}
							}
							break;
						default:
							if (this.ch >= '0' && this.ch <= '9') {
								estado = CONSTANTE_INTEIRA;
							} else if (this.ch >= 'a' && this.ch <= 'z') {
								estado = ID_PALAVRA_RESERVADA;
							} else if (this.ch == 0) {
								return null;
							} else if (Character.isWhitespace(ch)) {
								continue;
							} else {
								this.qtdErros++;
								System.out.println("ERRO: Caractere \'" + this.ch + "\' inválido " +
										"na" + " linha " + this.idxLinha + " coluna " + this.idxColuna);
							}
							break;
					}
					break;
				case CONSTANTE_INTEIRA: // Estado CONSTANTE_INTEIRA: Analisa constantes numéricas inteiras
					lexema += this.ch;
					readCh();
					if (this.ch >= '0' && this.ch <= '9') {
						continue;
					} else if (this.ch == '.') {
						estado = CONSTANTE_REAL;
					} else {
						if (this.ch != 0) {
							this.idxColuna--;
						}
						token = new Token(TagToken.CTE, lexema);
					}
					break;
				case ANALISA_PARTE_DECIMAL: // Estado ANALISA_PARTE_DECIMAL: Analisa se é mais um número
					lexema += this.ch;
					readCh();
					if (this.ch >= '0' && this.ch <= '9') {
						estado = CONSTANTE_REAL;
					} else {
						System.out.println("ERRO: Caractere \'" + this.ch + "\' inválido " +
								"na" + " linha " + this.idxLinha + " coluna " + this.idxColuna);
					}
					break;
				case CONSTANTE_REAL: // Estado CONSTANTE_REAL: Analisa parte decimal de um número real
					lexema += this.ch;
					readCh();
					if (this.ch >= '0' && this.ch <= '9') {
						continue;
					} else {
						if (this.ch != 0) {
							this.idxColuna--;
						}
						token = new Token(TagToken.CTE, lexema);
					}
					break;
				case ID_PALAVRA_RESERVADA: // Estado ID_PALAVRA_RESERVADA: Uma palavra reservada ou identificador
					lexema += this.ch;
					readCh();
					if ((this.ch >= 'a' && this.ch <= 'z') || (this.ch >= '0' && this.ch <= '9')) {
						continue;
					} else {
						if (this.ch != 0) {
							this.idxColuna--;
						}
						switch (lexema) {
							case "programa":
								token = new Token(TagToken.PROGRAMA, lexema);
								break;
							case "variaveis":
								token = new Token(TagToken.VARIAVEIS, lexema);
								break;
							case "inteiro":
								token = new Token(TagToken.INTEIRO, lexema);
								break;
							case "real":
								token = new Token(TagToken.REAL, lexema);
								break;
							case "logico":
								token = new Token(TagToken.LOGICO, lexema);
								break;
							case "caracter":
								token = new Token(TagToken.CARACTER, lexema);
								break;
							case "se":
								token = new Token(TagToken.SE, lexema);
								break;
							case "senao":
								token = new Token(TagToken.SENAO, lexema);
								break;
							case "enquanto":
								token = new Token(TagToken.ENQUANTO, lexema);
								break;
							case "leia":
								token = new Token(TagToken.LEIA, lexema);
								break;
							case "escreva":
								token = new Token(TagToken.ESCREVA, lexema);
								break;
							case "falso":
								token = new Token(TagToken.FALSO, lexema);
								break;
							case "verdadeiro":
								token = new Token(TagToken.VERDADEIRO, lexema);
								break;
							default:
								/**
								 * Resolve requisito de ID ter no máximo 32 caracteres.
								 * Segundo o Wallace, a maioria das linguagens apenas
								 * corta a variável, ignorando os caracteres restantes
								 */
								lexema = lexema.length() > 32 ? lexema.substring(0, 31) : lexema;
								token = new Token(TagToken.ID, lexema);
								break;
						}
						// Adiciona palavra reservada ou ID a tabela de símbolos
						token.addLinha(idxLinha);
						token.addColuna((idxColuna - (token.getLexema().length())) + 1);
						this.tabelaDeSimbolos.put(token);
					}
					break;
				case CADEIA: // Estado CADEIA: Analisa uma cadeia de caracteres
					lexema += this.ch;
					readCh();
					if (this.ch == 0) {
						lexema = "";
						token = new Token(TagToken.CADEIA, lexema);
						this.cadeiaNaoFechada = true;
						this.idxLinhaCadeia = this.idxLinha;
						return token;
					} else if (this.ch != '"') {
						this.cadeiaNaoFechada = false;
						continue;
					} else {
						// Cadeia encontrada
						lexema += this.ch;
						token = new Token(TagToken.CADEIA, lexema);
					}
					break;
				case BLOCO_COMENTARIO:
					readCh();
					if (this.ch == 0) {
						this.comentarioBloco = true;
						return null;
					} else if (this.ch == '*') {
						if (lookahead('/')) {
							this.comentarioBloco = false;
							estado = NAO_ALFA_NUMERICO;
							break;
						}
					}
					continue;
				default:
					return null;
			}
		}

		if (token.getLinhas().size() == 0) {
			token.addLinha(idxLinha);
			token.addColuna((idxColuna - (token.getLexema().length())) + 1);
		}

		return token;
	}
}