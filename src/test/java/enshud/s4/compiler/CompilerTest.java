package enshud.s4.compiler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import enshud.casl.CaslSimulator;
import enshud.s4.compiler.Compiler;

/**
 * !!! このコードは編集禁止 !!!
 * 
 * Compilerの単体テストクラス．
 * ここに記述された全ての単体テストが正しく動作するようにCompiler.run()メソッドを開発すること．
 * 
 * テストクラスの読み方はTrialTest.javaのコメントを確認すること．
 * 
 * 基本的には以下2つを実行し，
 * - Compiler.run("data/ts/XXXX.ts", "tmp/out.cas");
 * - CaslSimulator.run("tmp/out.cas", "tmp/out.com");
 * 
 * 以下を確認する．
 * - XXXXがnormalXXの場合： 各ansファイルの結果と一致するか．
 * - XXXXがsynerrXXの場合： "Syntax error: line X"と正しく出力されるか，
 * - XXXXがsemerrXXの場合： "Sematic error: line X"と正しく出力されるか，
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompilerTest {
	@Rule
	public final Timeout globalTimeout = Timeout.seconds(10);
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	private ByteArrayOutputStream out;
	private ByteArrayOutputStream err;
	private static final String TMP_OUT_CAS = "tmp/out.cas";
	private static final String TMP_OUT_ANS = "tmp/out.ans";

	@Before
	public void before() {
		new File(TMP_OUT_CAS).getParentFile().mkdirs();
		new File(TMP_OUT_CAS).delete();
		new File(TMP_OUT_ANS).getParentFile().mkdirs();
		new File(TMP_OUT_ANS).delete();

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
		new Compiler().run("data/ts/normal01.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal01.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal02() {
		new Compiler().run("data/ts/normal02.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal02.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal03() {
		new Compiler().run("data/ts/normal03.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal03.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal04() {
		new Compiler().run("data/ts/normal04.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS, "36", "48");
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal04.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal05() {
		new Compiler().run("data/ts/normal05.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal05.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal06() {
		new Compiler().run("data/ts/normal06.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal06.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal07() {
		new Compiler().run("data/ts/normal07.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal07.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal08() {
		new Compiler().run("data/ts/normal08.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal08.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal09() {
		new Compiler().run("data/ts/normal09.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS);
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal09.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	@Test
	public void testNormal10() {
		new Compiler().run("data/ts/normal10.ts", TMP_OUT_CAS);
		CaslSimulator.run(TMP_OUT_CAS, TMP_OUT_ANS, "36", "48");
		final File actual = new File(TMP_OUT_ANS);
		final File expected = new File("data/ans/normal10.ans");
		assertThat(actual).hasSameContentAs(expected);
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSynerr01() {
		new Compiler().run("data/ts/synerr01.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 1");
	}

	@Test
	public void testSynerr02() {
		new Compiler().run("data/ts/synerr02.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 3");
	}

	@Test
	public void testSynerr03() {
		new Compiler().run("data/ts/synerr03.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 8");
	}

	@Test
	public void testSynerr04() {
		new Compiler().run("data/ts/synerr04.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 10");
	}

	@Test
	public void testSynerr05() {
		new Compiler().run("data/ts/synerr05.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 11");
	}

	@Test
	public void testSynerr06() {
		new Compiler().run("data/ts/synerr06.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 13");
	}

	@Test
	public void testSynerr07() {
		new Compiler().run("data/ts/synerr07.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 29");
	}

	@Test
	public void testSynerr08() {
		new Compiler().run("data/ts/synerr08.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Syntax error: line 31");
	}

	////////////////////////////////////////////////////////////////////////////////

	@Test
	public void testSemerr01() {
		new Compiler().run("data/ts/semerr01.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 6");
	}

	@Test
	public void testSemerr02() {
		new Compiler().run("data/ts/semerr02.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 23");
	}

	@Test
	public void testSemerr03() {
		new Compiler().run("data/ts/semerr03.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 29");
	}

	@Test
	public void testSemerr04() {
		new Compiler().run("data/ts/semerr04.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 29");
	}

	@Test
	public void testSemerr05() {
		new Compiler().run("data/ts/semerr05.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 30");
	}

	@Test
	public void testSemerr06() {
		new Compiler().run("data/ts/semerr06.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 30");
	}

	@Test
	public void testSemerr07() {
		new Compiler().run("data/ts/semerr07.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 31");
	}

	@Test
	public void testSemerr08() {
		new Compiler().run("data/ts/semerr08.ts", TMP_OUT_CAS);
		assertThat(err.toString().trim()).isEqualTo("Semantic error: line 34");
	}

	////////////////////////////////////////////////////////////////////////////////

	/**
	 *  入力ファイルが存在しない場合に正しく動作するか
	 */
	@Test
	public void test_InputFileNotFound() {
		new Compiler().run("data/ts/xxxxxxxx.ts", TMP_OUT_CAS);
		assertThat("File not found").isEqualTo(err.toString().trim());
	}
}
