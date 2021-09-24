package enshud.s0.trial;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import enshud.s0.trial.Trial;

/**
 * !!! このコードは編集禁止 !!!
 * 
 * Trialの単体テストクラス．
 * ここに記述された全ての単体テストが正しく動作するようにLexer.run()メソッドを開発すること．
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrialTest {
	/**
	 * 全テストのタイムアウト時間の定義．
	 * 各テストは10秒以内に実行が完了すること．
	 * （無限ループ等の回避のため）
	 */
	@Rule
	public final Timeout globalTimeout = Timeout.seconds(10);

	/**
	 * System.exit()が呼び出されてもテストを継続する．
	 */
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	/**
	 * 標準入力の出力先．
	 */
	private ByteArrayOutputStream out;

	/**
	 * 標準エラーの出力先．
	 */
	private ByteArrayOutputStream err;

	/**
	 * 各テストの実行前に実行されるメソッド．
	 * 標準出力と標準エラーの出力先を捕まえておく．
	 */
	@Before
	public void before() {
		out = new ByteArrayOutputStream();
		err = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		System.setErr(new PrintStream(err));
	}

	/**
	 * 各テストの実行後に実行されるメソッド．
	 * 捕まえておいた標準出力と標準エラーを閉じる．
	 * @throws IOException
	 */
	@After
	public void after() throws IOException {
		out.close();
		err.close();
	}

	/**
	 * 単体テスト1 （正常系）．
	 * 入力ファイルがnormal01.pasの時に正しく35を出力するかを確認．
	 */
	@Test
	public void testNormal01() {
		new Trial().run("data/pas/normal01.pas");
		assertThat(out.toString().trim()).isEqualTo("35");
	}

	/**
	 * 単体テスト2 （正常系）．
	 * 入力ファイルがnormal02.pasの時に正しく94を出力するを確認．
	 */
	@Test
	public void testNormal02() {
		new Trial().run("data/pas/normal02.pas");
		assertThat(out.toString().trim()).isEqualTo("94");
	}

	/**
	 * 単体テスト3 （正常系）．
	 * 入力ファイルがnormal03.pasの時に正しく179を出力するかを確認．
	 */
	@Test
	public void testNormal03() {
		new Trial().run("data/pas/normal03.pas");
		assertThat(out.toString().trim()).isEqualTo("179");
	}

	/**
	 * 単体テスト4（正常系）．
	 * 入力ファイルが空の時に正しく0を出力するかを確認．
	 */
	@Test
	public void test_InputFileEmpty() throws IOException {
		Files.write(Paths.get("tmp/xxx.pas"), "".getBytes());
		new Trial().run("tmp/xxx.pas");
		assertThat(out.toString().trim()).isEqualTo("0");
	}

	/**
	 * 単体テスト5 （異常系）．
	 * 入力ファイルが存在しない場合に標準エラーへ"File not found"を出力するかを確認．
	 */
	@Test
	public void test_InputFileNotFound() {
		new Trial().run("data/pas/xxxxxxxx.pas");
		assertThat(err.toString().trim()).isEqualTo("File not found");
	}
}
