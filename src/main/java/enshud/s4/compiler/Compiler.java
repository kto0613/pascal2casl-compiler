package enshud.s4.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import enshud.casl.CaslSimulator;
import enshud.s1.lexer.Lexer;

public class Compiler {
	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		//Pre-processing
		System.out.print("Executing Lexer...");
		new Lexer().run("data/pas/normal10.pas", "tmp/custom.ts");
		System.out.println();
		
		// Compilerを実行してcasを生成する
		System.out.print("Executing Compiler...");
		new Compiler().run("tmp/custom.ts", "tmp/custom.cas");
		new Compiler().run("tmp/custom.ts", "tmp/custom.cas.out", false);
		System.out.println();
		
		/*
		// CaslSimulatorクラスを使ってコンパイルしたcasを，CASLアセンブラ & COMETシミュレータで実行する
		if((new File("tmp/custom.cas")).isFile()) {
			CaslSimulator.run("tmp/custom.cas", "tmp/custom.ans", "4gs3f","aaabb");
			//CaslSimulator.run("data/cas/normal04.cas", "tmp/out.ans", "36", "48");
		}
		else {
			System.err.println("cas is not generated!");
		}
		*/
		
		/*
		CaslOptimizer.optimize("data/cas/normal04.cas", "tmp/out.cas");
		CaslSimulator.run("tmp/out.cas", "tmp/out.ans", "64", "96");
		*/
		
		System.out.println("Done!");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるCompiler実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたtsファイルを読み込み，CASL IIプログラムにコンパイルする．
	 * コンパイル結果のCASL IIプログラムは第二引数で指定されたcasファイルに書き出すこと．
	 * 構文的もしくは意味的なエラーを発見した場合は標準エラーにエラーメッセージを出力すること．
	 * （エラーメッセージの内容はChecker.run()の出力に準じるものとする．）
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 * 
	 * @param inputFileName 入力tsファイル名
	 * @param outputFileName 出力casファイル名
	 */
	public void run(final String inputFileName, final String outputFileName) {
		run(inputFileName, outputFileName, true);
	}
	public void run(final String inputFileName, final String outputFileName, boolean doOptimize) {
		CompilerParseMain cpm = new CompilerParseMain();
		boolean initialized = false;
		
		try {
			initialized = cpm.Initialize(inputFileName);
		} catch (FileNotFoundException e) {
			System.err.print("File not found");
		} catch (IOException e) {
			System.err.print("Unexpected IO exception");
		}
		
		if(initialized) {
			if(!cpm.doParser()) return;
			else {
				try {
					cpm.createCAS(outputFileName);
				} catch (IOException e) {
					System.err.print("Unexpected IO exception");
				}
				if(doOptimize) {
					CaslOptimizer.optimize(outputFileName);
				}
				CaslSimulator.appendLibcas(outputFileName);
			}
		}
	}
}
