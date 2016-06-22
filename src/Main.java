import compilador.lexico.TabelaDeSimbolos;
import compilador.sintatico.AnalisadorSintatico;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe Main
 *
 * @author Bruno Tomé - 0011254
 * @author Cláudio Menezes - 0011255
 * @author Vinícius Duarte - 0011918
 * @since 27/05/2016
 */
public class Main {

	private static BufferedReader reader;

	public static void main(String[] args) throws IOException {
		String arquivoIn = "exemplos/exemplo32.txt";
		String arquivoOut;
		Boolean gravaTabelaDeSimbolos = false;

		// Checa quais argumentos foram passados na linha de comando
		if (args.length == 0) {
			System.err.println("Passe o arquivo texto como argumento! Foi usado o \"" + arquivoIn + "\" por " +
					"default\n");
		} else if (args.length == 1) {
			arquivoIn = args[0];
		} else if ((args.length == 3) && (args[1].equals("-t"))) {
			arquivoIn = args[0];
			gravaTabelaDeSimbolos = true;
		} else {
			System.err.println("Argumentos inválidos, consulte o enunciado do trabalho.");
			System.exit(1);
		}

		// Inicia compilador
		FileReader arquivoTxt = new FileReader(arquivoIn);
		reader = new BufferedReader(arquivoTxt);
		AnalisadorSintatico parser = new AnalisadorSintatico(reader);

		try {
			parser.validaToken();
			parser.PROG();
			arquivoTxt.close();
			parser.totalErros();
		} catch (Exception e) {
			parser.totalErros();
		}

		// Finaliza compilador

		// Checa se irá gravar a tabela de símbolos em um arquivo txt
		if (gravaTabelaDeSimbolos) {
			arquivoOut = args[2];
			PrintWriter writer = new PrintWriter(arquivoOut);
			TabelaDeSimbolos tabelaDeSimbolos = parser.getLex().getTabelaDeSimbolos();
			writer.println("\nTABELA DE SÍMBOLOS\n");
			writer.println(tabelaDeSimbolos.toString());
			writer.close();
			System.out.println("\nTABELA DE SÍMBOLOS GRAVADA NO ARQUIVO \n" + arquivoOut);
		}
	}
}