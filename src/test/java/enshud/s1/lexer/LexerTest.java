package enshud.s1.lexer;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.Timeout;
import org.junit.runners.MethodSorters;

import enshud.s1.lexer.Lexer;

/**
 * !!! このコードは編集禁止 !!!
 * 
 * Lexerの単体テストクラス．
 * ここに記述された全ての単体テストが正しく動作するようにLexer.run()メソッドを開発すること．
 * 
 * テストクラスの読み方はTrialTest.javaのコメントを確認すること．
 * 
 * 基本的にはlexer.run("data/pas/XXXX.pas", "tmp/out.ts")を実行し，
 * 出力結果tmp/out.tsの中身がdata/ts/XXXX.tsと完全に一致するか，および
 * 標準出力に"OK"が出ているかを確認している．
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LexerTest {
	@Rule
	public final Timeout globalTimeout = Timeout.seconds(10);
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;
	private static final String TMP_OUT_TS = "tmp/out.ts";

	@Before
	public void before() {
		new File(TMP_OUT_TS).getParentFile().mkdir();
		new File(TMP_OUT_TS).delete();

		out = new ByteArrayOutputStream();
		err = new ByteArrayOutputStream();
		System.setOut(new PrintStream(out));
		System.setErr(new PrintStream(err));
	}

	@After
	public void after() throws IOException {
		out.close();
		err.close();
	}

	@Test
	public void testNormal01() {
		new Lexer().run("data/pas/normal01.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal01.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal02() {
		new Lexer().run("data/pas/normal02.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal02.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal03() {
		new Lexer().run("data/pas/normal03.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal03.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal04() {
		new Lexer().run("data/pas/normal04.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal04.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal05() {
		new Lexer().run("data/pas/normal05.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal05.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal06() {
		new Lexer().run("data/pas/normal06.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal06.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal07() {
		new Lexer().run("data/pas/normal07.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal07.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal08() {
		new Lexer().run("data/pas/normal08.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal08.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal09() {
		new Lexer().run("data/pas/normal09.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal09.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testNormal10() {
		new Lexer().run("data/pas/normal10.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal10.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSynerr01() {
		new Lexer().run("data/pas/synerr01.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr01.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSynerr02() {
		new Lexer().run("data/pas/synerr02.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr02.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSynerr03() {
		new Lexer().run("data/pas/synerr03.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr03.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSynerr04() {
		new Lexer().run("data/pas/synerr04.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr04.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSynerr05() {
		new Lexer().run("data/pas/synerr05.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr05.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSynerr06() {
		new Lexer().run("data/pas/synerr06.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr06.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSynerr07() {
		new Lexer().run("data/pas/synerr07.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr07.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSynerr08() {
		new Lexer().run("data/pas/synerr08.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/synerr08.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSemerr01() {
		new Lexer().run("data/pas/semerr01.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr01.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSemerr02() {
		new Lexer().run("data/pas/semerr02.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr02.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSemerr03() {
		new Lexer().run("data/pas/semerr03.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr03.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSemerr04() {
		new Lexer().run("data/pas/semerr04.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr04.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSemerr05() {
		new Lexer().run("data/pas/semerr05.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr05.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSemerr06() {
		new Lexer().run("data/pas/semerr06.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr06.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSemerr07() {
		new Lexer().run("data/pas/semerr07.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr07.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	@Test
	public void testSemerr08() {
		new Lexer().run("data/pas/semerr08.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/semerr08.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}

	////////////////////////////////////////////////////////////////////////////////

	/**
	 *  入力ファイルが存在しない場合に正しく動作するか
	 */
	@Test
	public void test_InputFileNotFound() {
		new Lexer().run("data/pas/xxxxxxxx.pas", TMP_OUT_TS);
		assertThat(err.toString().trim()).isEqualTo("File not found");
	}

	/**
	 *  出力ファイルが既に存在する場合に正しく動作するか
	 */
	@Test
	public void test_OutputFileAlreadyExists() throws IOException {
		final FileWriter writer = new FileWriter(TMP_OUT_TS);
		writer.write("xxxxxxxxxxxx");
		writer.write("xxxxxxxxxxxx");
		writer.write("xxxxxxxxxxxx");
		writer.close();

		new Lexer().run("data/pas/normal01.pas", TMP_OUT_TS);
		final File actual = new File(TMP_OUT_TS);
		final File expected = new File("data/ts/normal01.ts");
		assertThat(actual).hasSameContentAs(expected);
		assertThat(out.toString().trim()).isEqualTo("OK");
	}
}
