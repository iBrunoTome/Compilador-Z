package compilador.sintatico;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.TagToken;
import compilador.lexico.Token;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Analisador sintático
 *
 * @author Bruno Tomé - 0011254
 * @author Cláudio Menezes - 0011255
 * @author Vinícius Duarte - 0011918
 * @since 13/06/2016
 */
public class AnalisadorSintatico {

	private Token token;
	private AnalisadorLexico lex;
	private BufferedReader reader;
	private String linha = "";
	private Producoes producaoAtual;
	private Boolean imprimeErro = false;
	private int idxLinha = 0;
	private int qtdErros = 0;

	public AnalisadorSintatico(BufferedReader reader) throws IOException {
		this.lex = new AnalisadorLexico();
		this.reader = reader;
	}

	public AnalisadorLexico getLex() {
		return this.lex;
	}

	/**
	 * Busca o próximo token na linha seguinte
	 */
	public void validaToken() {
		try {
			this.linha = this.reader.readLine();
		} catch (Exception e) {
			System.out.println("Não foi possível ler o arquivo");
		}
		this.linha = this.linha == null ? null : linha.toLowerCase();
		if (linha != null) {
			this.idxLinha++;
			this.lex.setLinha(this.linha);
			this.lex.setIdxColuna(0);
			this.lex.setIdxLinha(idxLinha);
			this.token = this.lex.nextToken();
			while ((this.token == null) && (this.linha != null)) {
				this.idxLinha++;
				try {
					this.linha = this.reader.readLine();
				} catch (Exception e) {
					System.out.println("Não foi possível ler o arquivo");
				}
				this.linha = this.linha == null ? null : linha.toLowerCase();
				if (this.linha != null) {
					this.lex.setIdxLinha(this.idxLinha);
					this.lex.setIdxColuna(0);
					this.lex.setLinha(this.linha);
					this.token = this.lex.nextToken();
				}
			}
		} else {
			// Fim de arquivo, hora de contar os erros
		}
	}

	/**
	 * Método chamado sempre que é necessário buscar um novo token de
	 * sincronização
	 */
	private void synchronization() {
		this.token = lex.nextToken();
		if (this.token == null) {
			validaToken();
		}
	}

	/**
	 * Sincronização dos erros baseado nos first follow
	 */
	private void onTheFly() {
		switch (this.producaoAtual) {
			case PROG:
				while ((this.token != null) && (this.token.getTag() != TagToken.VARIAVEIS)
						&& (this.token.getTag() != TagToken.ABRECH)) {
					synchronization();
				}
				break;
			case LIST_DECLS:
			case DECLS:
			case READ:
				while ((this.token != null) && (this.token.getTag() != TagToken.ID)) {
					synchronization();
				}
				break;
			case D:
				while ((this.token != null) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.ABRECH)) {
					synchronization();
				}
				break;
			case DECL_TIPO:
				while ((this.token != null) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.INTEIRO) && (this.token.getTag() != TagToken.REAL)
						&& (this.token.getTag() != TagToken.LOGICO) && (this.token.getTag() != TagToken.CARACTER)) {
					synchronization();
				}
				break;
			case LIST_ID:
			case E:
				while ((this.token != null) && (this.token.getTag() != TagToken.VIRG)
						&& (this.token.getTag() != TagToken.FECHAPAR) && (this.token.getTag() != TagToken.DPONTOS)) {
					synchronization();
				}
				break;
			case C_COMP:
			case G:
			case COMANDOS:
				while ((this.token != null) && (this.token != null) && (this.token.getTag() != TagToken.SE)
						&& (this.token.getTag() != TagToken.LEIA) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.ENQUANTO) && (this.token.getTag() != TagToken.ESCREVA)) {
					synchronization();
				}
				break;
			case LISTA_COMANDOS:
				while ((this.token != null) && (this.token.getTag() != TagToken.SE)
						&& (this.token.getTag() != TagToken.LEIA) && (this.token.getTag() != TagToken.FECHACH)
						&& (this.token.getTag() != TagToken.ID) && (this.token.getTag() != TagToken.ENQUANTO)
						&& (this.token.getTag() != TagToken.ESCREVA)) {
					synchronization();
				}
				break;
			case IF:
				while ((this.token != null) && (this.token.getTag() != TagToken.CTE)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.VERDADEIRO) && (this.token.getTag() != TagToken.OPNEG)
						&& (this.token.getTag() != TagToken.FALSO) && (this.token.getTag() != TagToken.ABRECH)
						&& (this.token.getTag() != TagToken.SENAO) && (this.token.getTag() != TagToken.SE)
						&& (this.token.getTag() != TagToken.LEIA) && (this.token.getTag() != TagToken.ENQUANTO)
						&& (this.token.getTag() != TagToken.ESCREVA) && (this.token.getTag() != TagToken.FECHACH)) {
					synchronization();
				}
				break;
			case H:
				while ((this.token != null) && (this.token.getTag() != TagToken.ABRECH)) {
					synchronization();
				}
				break;
			case WHILE:
				while ((this.token != null) && (this.token.getTag() != TagToken.CTE)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.VERDADEIRO) && (this.token.getTag() != TagToken.OPNEG)
						&& (this.token.getTag() != TagToken.FALSO) && (this.token.getTag() != TagToken.ABRECH)) {
					synchronization();
				}
				break;
			case ATRIB:
			case ELEM_W:
			case P:
			case R:
			case S:
			case FAT:
				while ((this.token != null) && (this.token.getTag() != TagToken.CTE)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.VERDADEIRO) && (this.token.getTag() != TagToken.OPNEG)
						&& (this.token.getTag() != TagToken.FALSO)) {
					synchronization();
				}
				break;
			case WRITE:
			case L:
				while ((this.token != null) && (this.token.getTag() != TagToken.CADEIA)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.VERDADEIRO) && (this.token.getTag() != TagToken.OPNEG)
						&& (this.token.getTag() != TagToken.FALSO) && (this.token.getTag() != TagToken.CTE)) {
					synchronization();
				}
				break;
			case LIST_W:
				while ((this.token != null) && (this.token.getTag() != TagToken.CADEIA)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.VERDADEIRO) && (this.token.getTag() != TagToken.OPNEG)
						&& (this.token.getTag() != TagToken.FALSO) && (this.token.getTag() != TagToken.CTE)
						&& (this.token.getTag() != TagToken.VIRG) && (this.token.getTag() != TagToken.FECHAPAR)) {
					synchronization();
				}
				break;
			case EXPR:
				while ((this.token != null) && (this.token.getTag() != TagToken.OPREL)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.VERDADEIRO) && (this.token.getTag() != TagToken.OPNEG)
						&& (this.token.getTag() != TagToken.FALSO) && (this.token.getTag() != TagToken.CTE)
						&& (this.token.getTag() != TagToken.FECHAPAR) && (this.token.getTag() != TagToken.PVIRG)
						&& (this.token.getTag() != TagToken.VIRG)) {
					synchronization();
				}
				break;
			case SIMPLES:
				while ((this.token != null) && (this.token.getTag() != TagToken.OPAD)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.ID)
						&& (this.token.getTag() != TagToken.VERDADEIRO) && (this.token.getTag() != TagToken.OPNEG)
						&& (this.token.getTag() != TagToken.FALSO) && (this.token.getTag() != TagToken.CTE)
						&& (this.token.getTag() != TagToken.FECHAPAR) && (this.token.getTag() != TagToken.PVIRG)
						&& (this.token.getTag() != TagToken.VIRG) && (this.token.getTag() != TagToken.OPREL)) {
					synchronization();
				}
				break;
			case TERMO:
				while ((this.token != null) && (this.token.getTag() != TagToken.ID) && (this.token.getTag() !=
						TagToken.CTE)
						&& (this.token.getTag() != TagToken.ABREPAR) && (this.token.getTag() != TagToken.VERDADEIRO)
						&& (this.token.getTag() != TagToken.OPNEG) && (this.token.getTag() != TagToken.FALSO)
						&& (this.token.getTag() != TagToken.OPMUL) && (this.token.getTag() != TagToken.OPAD)
						&& (this.token.getTag() != TagToken.OPREL) && (this.token.getTag() != TagToken.FECHAPAR)
						&& (this.token.getTag() != TagToken.PVIRG) && (this.token.getTag() != TagToken.VIRG)) {
					synchronization();
				}
				break;
		}
	}

	/**
	 * Método match
	 *
	 * @param tag é a Tag que será comparada com a tag do token atual
	 */
	private void match(TagToken tag) {
		if ((this.token != null) && tag == this.token.getTag()) {
			synchronization();
		} else {
			if ((this.token == null) && (this.lex.getIdxLinhaComentario() != 0)) {
				System.out.println("ERRO: Comentário de bloco aberto e não fechado na " + "linha "
						+ this.lex.getIdxLinhaComentario());
				this.qtdErros++;
			} else if ((this.token != null) && (this.lex.getIdxLinhaCadeia() != 0)) {
				System.out.println("ERRO: Cadeia aberta e não fechada na linha " + this.lex.getIdxLinhaCadeia());
				this.qtdErros++;
			} else {
				if (!this.imprimeErro) {
					System.out.println(selecionaERRO(tag));
					this.imprimeErro = true;
					onTheFly();
				}
			}
		}
	}

	/**
	 * Imprime o total de erros encontrados
	 */
	public void totalErros() {
		this.qtdErros += this.lex.getQtdErros();
		if (this.qtdErros == 0) {
			System.out.println("\nProcesso finalizado sem erros");
		} else if (this.qtdErros == 1) {
			System.err.println("\n" + this.qtdErros + " erro gerado");
		} else {
			System.err.println("\n" + this.qtdErros + " erros gerados");
		}
	}

	/**
	 * Seleciona o erro que será impresso de acordo com a tag
	 *
	 * @param tag representando o que era esperado
	 * @return String erro selecionado
	 */
	private String selecionaERRO(TagToken tag) {
		this.qtdErros++;
		if (this.token != null) {
			switch (tag) {
				case PROGRAMA:
					return "ERRO: Esperado a palavra reservada \'PROGRAMA\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case VARIAVEIS:
					return "ERRO: Esperado a palavra reservada \'VARIAVEIS\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case INTEIRO:
					return "ERRO: Esperado a palavra reservada \'INTEIRO\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case REAL:
					return "ERRO: Esperado a palavra reservada \'REAL\' na linha: " + token.getLinhas().get(0) + " " +
							"coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case LOGICO:
					return "ERRO: Esperado a palavra reservada \'LOGICO\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case CARACTER:
					return "ERRO: Esperado a palavra reservada \'CARACTER\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case SE:
					return "ERRO: Esperado a palavra reservada \'SE\' na linha: " + token.getLinhas().get(0) + " " +
							"coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case SENAO:
					return "ERRO: Esperado a palavra reservada \'SENAO\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case ENQUANTO:
					return "ERRO: Esperado a palavra reservada \'ENQUANTO\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case LEIA:
					return "ERRO: Esperado a palavra reservada \'LEIA\' na linha: " + token.getLinhas().get(0) + " " +
							"coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case ESCREVA:
					return "ERRO: Esperado a palavra reservada \'ESCREVA\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case FALSO:
					return "ERRO: Esperado a palavra reservada \'FALSO\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case VERDADEIRO:
					return "ERRO: Esperado a palavra reservada \'VERDADEIRO\' na linha: " + token.getLinhas().get(0)
							+ " coluna: " + token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() +
							"\'";
				case ID:
					return "ERRO: Esperado um identificador na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case CTE:
					return "ERRO: Esperado uma constante na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case CADEIA:
					return "ERRO: Cadeia nao fechada na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case ATRIB:
					return "ERRO: Esperado \':=\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case OPREL:
					return "ERRO: Esperado um operador relacional \'[=\t<\t>\t<=\t>=\t<>]\' na " + "linha: "
							+ token.getLinhas().get(0) + " coluna: " + token.getColunas().get(0);
				case OPAD:
					return "ERRO: Esperado \'+\' ou \'-\'  na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case OPMUL:
					return "ERRO: Esperado \'*\' ou \'/\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
			/* Nunca cai no caso de erro de esperado um operador de negação */
				case OPNEG:
					return "ERRO: Esperado \'!\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case PVIRG:
					return "ERRO: Esperado \';\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case DPONTOS:
					return "ERRO: Esperado \':\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case VIRG:
					return "ERRO: Esperado \',\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case ABREPAR:
					return "ERRO: Esperado \'(\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case FECHAPAR:
					return "ERRO: Esperado \')\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case ABRECH:
					return "ERRO: Esperado \'{\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				case FECHACH:
					return "ERRO: Esperado \'}\' na linha: " + token.getLinhas().get(0) + " coluna: "
							+ token.getColunas().get(0) + " -> encontrado \'" + this.token.getLexema() + "\'";
				default:
					return "Não Consegue Moisés";
			}
		}
		return "";
	}

	/**
	 * Representação das produções da gramática
	 */

	public void PROG() {
		this.producaoAtual = Producoes.PROG;
		this.imprimeErro = false;
		match(TagToken.PROGRAMA);
		match(TagToken.ID);
		match(TagToken.PVIRG);
		DECLS();
		C_COMP();
		/*
		 * De última hora :) Reconhece como erro qualquer coisa escrita após o final do programa, que não seja
		 * comentário
		 */
		if (this.token.getLexema() != null) {
			this.qtdErros++;
			System.out.println("ERRO: Só são permitidos comentários após o fim do programa");
		}
	}

	private void DECLS() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.DECLS;
		if ((this.token != null) && TagToken.VARIAVEIS == this.token.getTag()) {
			match(TagToken.VARIAVEIS);
			LIST_DECLS();
		}
	}

	private void LIST_DECLS() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.LIST_DECLS;
		DECL_TIPO();
		D();
	}

	private void D() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.D;
		if ((this.token != null) && TagToken.ID == this.token.getTag()) {
			LIST_DECLS();
		} else {
			// Representa o vazio da produção
		}
	}

	private void DECL_TIPO() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.DECL_TIPO;
		LIST_ID();
		match(TagToken.DPONTOS);
		TIPO();
		match(TagToken.PVIRG);
	}

	private void LIST_ID() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.LIST_ID;
		match(TagToken.ID);
		E();
	}

	private void E() {
		this.imprimeErro = false;
		if ((this.token != null) && (TagToken.VIRG == this.token.getTag())) {
			match(TagToken.VIRG);
			LIST_ID();
		}
	}

	private void TIPO() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.TIPO;
		TagToken tag = this.token.getTag();
		switch (tag) {
			case INTEIRO:
				match(TagToken.INTEIRO);
				break;
			case REAL:
				match(TagToken.REAL);
				break;
			case LOGICO:
				match(TagToken.LOGICO);
				break;
			case CARACTER:
				match(TagToken.CARACTER);
				break;
			default:
				match(TagToken.ABRECH);
				break;
		}
	}

	private void C_COMP() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.C_COMP;
		match(TagToken.ABRECH);
		LISTA_COMANDOS();
		match(TagToken.FECHACH);
	}

	private void LISTA_COMANDOS() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.LISTA_COMANDOS;
		COMANDOS();
		G();
	}

	private void G() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.G;
		if ((this.token != null) && ((TagToken.SE == this.token.getTag()) || (TagToken.ENQUANTO == this.token.getTag())
				|| (TagToken.LEIA == this.token.getTag()) || (TagToken.ESCREVA == this.token.getTag())
				|| (TagToken.ID == this.token.getTag()))) {
			LISTA_COMANDOS();
		} else {
			// Representa o vazio da produção
		}
	}

	private void COMANDOS() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.COMANDOS;
		TagToken tag = this.token.getTag();
		switch (tag) {
			case SE:
				IF();
				break;
			case ENQUANTO:
				WHILE();
				break;
			case LEIA:
				READ();
				break;
			case ESCREVA:
				WRITE();
				break;
			case ID:
				ATRIB();
				break;
			default:
				match(TagToken.ABREPAR);
				break;
		}
	}

	private void IF() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.IF;
		match(TagToken.SE);
		match(TagToken.ABREPAR);
		EXPR();
		match(TagToken.FECHAPAR);
		C_COMP();
		H();
	}

	private void H() {
		this.producaoAtual = Producoes.H;
		if ((this.token != null) && TagToken.SENAO == this.token.getTag()) {
			match(TagToken.SENAO);
			C_COMP();
		}
	}

	private void WHILE() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.WHILE;
		match(TagToken.ENQUANTO);
		match(TagToken.ABREPAR);
		EXPR();
		match(TagToken.FECHAPAR);
		C_COMP();
	}

	private void READ() {
		this.producaoAtual = Producoes.READ;
		match(TagToken.LEIA);
		match(TagToken.ABREPAR);
		LIST_ID();
		match(TagToken.FECHAPAR);
		match(TagToken.PVIRG);
	}

	private void ATRIB() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.ATRIB;
		match(TagToken.ID);
		match(TagToken.ATRIB);
		EXPR();
		match(TagToken.PVIRG);
	}

	private void WRITE() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.WRITE;
		match(TagToken.ESCREVA);
		match(TagToken.ABREPAR);
		LIST_W();
		match(TagToken.FECHAPAR);
		match(TagToken.PVIRG);
	}

	private void LIST_W() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.LIST_W;
		ELEM_W();
		L();
	}

	private void L() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.L;
		if (TagToken.VIRG == this.token.getTag()) {
			match(TagToken.VIRG);
			LIST_W();
		}
	}

	private void ELEM_W() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.ELEM_W;
		if (this.token.getTag() == TagToken.CADEIA) {
			match(TagToken.CADEIA);
		} else {
			EXPR();
		}
	}

	private void EXPR() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.EXPR;
		SIMPLES();
		P();
	}

	private void P() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.P;
		if (TagToken.OPREL == this.token.getTag()) {
			match(TagToken.OPREL);
			SIMPLES();
		}
	}

	private void SIMPLES() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.SIMPLES;
		TERMO();
		R();
	}

	private void R() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.R;
		if (TagToken.OPAD == this.token.getTag()) {
			match(TagToken.OPAD);
			SIMPLES();
		}
	}

	private void TERMO() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.TERMO;
		FAT();
		S();
	}

	private void S() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.S;
		if (TagToken.OPMUL == this.token.getTag()) {
			match(TagToken.OPMUL);
			TERMO();
		}
	}

	private void FAT() {
		this.imprimeErro = false;
		this.producaoAtual = Producoes.FAT;
		TagToken tag = this.token.getTag();
		switch (tag) {
			case ID:
				match(TagToken.ID);
				break;
			case CTE:
				match(TagToken.CTE);
				break;
			case ABREPAR:
				match(TagToken.ABREPAR);
				EXPR();
				match(TagToken.FECHAPAR);
				break;
			case VERDADEIRO:
				match(TagToken.VERDADEIRO);
				break;
			case FALSO:
				match(TagToken.FALSO);
				break;
			case OPNEG:
				match(TagToken.OPNEG);
				FAT();
				break;
			default:
				match(TagToken.ABREPAR);
				break;
		}
	}
}
