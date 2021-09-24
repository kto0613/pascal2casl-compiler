package enshud.casl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import pycasl2.PyCasl2;
import pycomet2.PyComet2;

/**
 * !!! このコードは編集禁止 !!!
 * （Compilerテストが本クラスを利用するため）
 * 
 * 本クラスは自由に利用しても良いが，開発対象ではないことに注意．
 * 
 */
public class CaslSimulator {
	public static void main(final String[] args) throws IOException {
		//CaslSimulator.run("data/cas/normal01.cas", "tmp/out.ans");
		CaslSimulator.run("data/cas/normal04.cas", "tmp/out.ans", "8", "12");
	}

	private static final String LIBCAS = "data/cas/lib.cas";

	/**
	 * 第一引数で指定されたCASL IIプログラムファイルを読み込み，PyCaslアセンブラとPyCometシミュレータで実行する． 
	 * 最終的なPyCometの実行結果は第二引数で指定されたファイルに書き出される．
	 * 
	 * 使い方：
	 * CaslSimulator.execute("data/cas/normal01.cas", "tmp/out.ans");
	 * 
	 * 
	 * PyCometの標準入力へのは書き込みは第三引数に値を与えれば良い．
	 * 
	 * 使い方： GCD(8,12)を実行する場合，
	 * CaslSimulator.execute("data/cas/normal04.cas", "tmp/out.ans", "8", "12"); 
	 * 
	 * @param inputFileName 入力casファイル名
	 */
	public static void run(final String inputFileName, final String outputFileName,
			final String... params) {
		final String comFileName = removeFileNameExtension(outputFileName) + ".com";
		new File(comFileName).delete();
		PyCasl2.execute(new File(inputFileName), new File(comFileName));
		PyComet2.execute(new File(comFileName), new File(outputFileName), params);
	}

	/**
	 * 第一引数で指定されたcasファイルの末尾にdata/cas/lib.casを追加する．
	 * 
	 * 使い方：
	 * CaslSimulator.appendLibcas("tmp/out.cas"); 
	 * 
	 * @param fileName 対象casファイル名
	 */
	public static void appendLibcas(final String fileName) {
		try {
			final List<String> libcas = Files.readAllLines(Paths.get(LIBCAS));
			Files.write(Paths.get(fileName), libcas, StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String removeFileNameExtension(final String fileName) {
		final int n = fileName.lastIndexOf(".");
		if (n > 0) {
			return fileName.substring(0, n);
		}
		return fileName;
	}
}
