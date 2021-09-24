package enshud.s1.lexer;

import java.io.*;

public class Lexer {
	/**
	 * サンプルmainメソッド．
	 * 単体テストの対象ではないので自由に改変しても良い．
	 */
	public static void main(final String[] args) {
		/*
		// normalの確認
		new Lexer().run("data/pas/normal01.pas", "tmp/out1.ts");
		new Lexer().run("data/pas/normal02.pas", "tmp/out2.ts");
		*/
		new Lexer().run("data/custom/custom.pas", "tmp/custom.ts");
	}

	/**
	 * TODO
	 * 
	 * 開発対象となるLexer実行メソッド．
	 * 以下の仕様を満たすこと．
	 * 
	 * 仕様:
	 * 第一引数で指定されたpasファイルを読み込み，トークン列に分割する．
	 * トークン列は第二引数で指定されたtsファイルに書き出すこと．
	 * 正常に処理が終了した場合は標準出力に"OK"を，
	 * 入力ファイルが見つからない場合は標準エラーに"File not found"と出力して終了すること．
	 * 
	 * @param inputFileName 入力pasファイル名
	 * @param outputFileName 出力tsファイル名
	 */
	public void run(final String inputFileName, final String outputFileName) {
		LexerHelper lh = new LexerHelper(inputFileName, outputFileName);
		int tokenID;
		
		/*pas 字句解析*/
		try { //FileNotFoundException, IOException
			lh.Start();
			while(lh.C() != -1) { //EOF　ではない限り...
				if(lh.CisSeparator()) { //記号分離子の場合
					do {
						lh.Skip();
					}
					while(lh.CisSeparator());
				}
				else if(lh.CisAlpha()) { //キーワード、識別子の場合
					lh.Append();
					while(lh.CisAlnum()) {
						lh.Append();
					}
					tokenID = lh.getTokenID();
					if(tokenID == -1) tokenID = 43;
					lh.Register(tokenID);
				}
				else if(lh.CisNum()) { //符号なし整数の場合
					lh.Append();
					while(lh.CisNum()) {
						lh.Append();
					}
					if(lh.CisAlpha()) {
						/*<!--整数末尾の英字はエラー*/
						while(lh.CisAlnum()) {
							lh.Append();
						}
						lh.printErrorMessage(LexerHelper.Error.INVALID_CONSTANT);
						return;
						/*エラー処理はここまで-->*/
					}
					else{
						tokenID = 44;
						lh.Register(tokenID);
					}
				}
				else if(lh.C() == '\'') { //文字列の場合
					lh.Append();
					if(lh.C() == '\'') {
						/*<!--空の文字列はエラー*/
						lh.printErrorMessage(LexerHelper.Error.NULL_STRING);
						return;
						/*エラー処理はここまで-->*/
					}
					while(lh.C() != '\'') {
						if(lh.C() == '\n' || lh.C() == -1) {
							/*<!--一行に含まれない文字列はエラー*/
							lh.printErrorMessage(LexerHelper.Error.INVALID_STRING);
							return;
							/*エラー処理はここまで-->*/
						}
						else lh.Append();
					}
					lh.Append();
					tokenID = 45;
					lh.Register(tokenID);
				}
				else if(lh.C() == '{') { //注釈の場合
					lh.Skip();
					while(lh.C() != '}') {
						if(lh.C() == -1) {
							/*<!--注釈が最後まで終わらないとエラー*/
							lh.printErrorMessage(LexerHelper.Error.UNTERMINATED_COMMENT);
							return;
							/*エラー処理はここまで-->*/
						}
						else lh.Skip();
					}
					lh.Skip();
				}
				else { //特殊記号の場合
					switch(lh.C()) {
					case '<':
						lh.Append();
						if(lh.C() == '>') lh.Append();
						else if(lh.C() == '=') lh.Append();
						break;
					case '>':
						lh.Append();
						if(lh.C() == '=') lh.Append();
						break;
					case ':':
						lh.Append();
						if(lh.C() == '=') lh.Append();
						break;
					case '.':
						lh.Append();
						if(lh.C() == '.') lh.Append();
						break;
					case '+':
					case '-':
					case '*':
					case '/':
					case '=':
					case '(':
					case ')':
					case '[':
					case ']':
					case ',':
					case ';':
						lh.Append();
						break;
					default:
						/*<!--他の文字はエラー*/
						lh.printErrorMessage(LexerHelper.Error.INVALID_CHARACTER);
						return;
						/*エラー処理はここまで-->*/
					}
					tokenID = lh.getTokenID();
					if(tokenID == -1) {
						/*MUST UNREACHABLE*/
						/*<!--トークンIDが見つからないとエラー*/
						lh.printErrorMessage(LexerHelper.Error.INVALID_SYMBOL);
						return;
						/*エラー処理はここまで-->*/
					}
					lh.Register(tokenID);
				}
			}
			lh.End();
		} catch (FileNotFoundException e) {
			System.err.print("File not found");
			return;
		} catch (IOException e) {
			/*<!--入出力例外処理*/
			System.err.print("Unexpected IO exception");
			return;
			/*例外処理はここまで-->*/
		}
		
		/*ts ファイル出力*/
		try { //IOException
			lh.writeTSFile();
		} catch (IOException e) {
			/*<!--入出力例外処理*/
			System.err.print("Unexpected IO exception");
			return;
			/*例外処理はここまで-->*/
		}
		
		/*正常終了*/
		System.out.print("OK");
	}
}
