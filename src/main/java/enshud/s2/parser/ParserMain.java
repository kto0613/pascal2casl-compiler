package enshud.s2.parser;

import java.io.*;
import java.util.*;

/**
 * ParserMain クラスのインスタンスを使用すると TS ファイルを構文解析できます。(Version 2)
 */
final class ParserMain {
	private ArrayList<ParserData> list = null;
	private boolean initialized;
	
	private int index;
	
	/**
	 * ParserMain クラスのコンストラクタです。
	 */
	ParserMain() {
		initialized = false;
	}
	
	/**
	 * TS ファイルからトークン列を読み込んで構文解析の初期化を行うメソッドです。
	 * @param inputFileName
	 * 入力の TS ファイルです。
	 * @return
	 * TS ファイルからトークン列の読み込みに成功し、初期化が正しく行われたら {@code true} をリターンします。
	 * TS ファイルのフォーマットに問題がある場合は {@code false} をリターンします。
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public boolean Initialize(String inputFileName) throws FileNotFoundException, IOException {
		BufferedReader in;
		String str;
		
		list = new ArrayList<ParserData>();
		in = new BufferedReader(new FileReader(inputFileName));
		
		while((str = in.readLine()) != null) {
			String[] spl = str.split("\t");
			if(spl.length == 4) {
				int tokenID = Integer.parseInt(spl[2]);
				int lineNum = Integer.parseInt(spl[3]);
				if(spl[1].equals(ParserData.getTokenNameof(tokenID))) {
					list.add(new ParserData(tokenID, lineNum));
					continue;
				}
			}
			
			in.close();
			list = null;
			initialized = false;
			return false;
		}
		
		in.close();
		initialized = true;
		return true;
	}
	
	private int getTokenIDofIndex() {
		if(index >= 0 && index < list.size())
			return ((ParserData)list.get(index)).getTokenID();
		else
			return -1;
	}
	
	private int getLineNumofIndex() {
		if(index >= 0 && index < list.size())
			return ((ParserData)list.get(index)).getLineNum();
		else
			return -1;
	}
	
	/**Exception*/
	private void throwException() throws ParserException {
		throw new ParserException(getLineNumofIndex());
	}
	
	/**
	 * 構文解析を行うメソッドです。
	 */
	public void doParser() {
		if(initialized) {
			index = 0;
			try {
				parseProgram();
			} catch (ParserException e) {
				System.err.print(e.getMessage());
				return;
			}
			
			if(index == list.size()) {
				System.out.print("OK");
			}
			else {
				System.err.print("Invalid extra data");
			}
		}
	}
	
	/**プログラム*/
	private void parseProgram() throws ParserException {
		if(getTokenIDofIndex() != ParserData.SPROGRAM) throwException();
		else index++;
		
		parseProgramName();
		
		if(getTokenIDofIndex() != ParserData.SLPAREN) throwException();
		else index++;

		parseNameArray();

		if(getTokenIDofIndex() != ParserData.SRPAREN) throwException();
		else index++;

		if(getTokenIDofIndex() != ParserData.SSEMICOLON) throwException();
		else index++;

		parseBlock();
		
		parseStatementBlock(false);
		
		if(getTokenIDofIndex() != ParserData.SDOT) throwException();
		else index++;
	}
	
	/**プログラム名*/
	private void parseProgramName() throws ParserException {
		if(getTokenIDofIndex() != ParserData.SIDENTIFIER) throwException();
		else index++;
	}
	
	/**名前の並び*/
	private void parseNameArray() throws ParserException {
		if(getTokenIDofIndex() != ParserData.SIDENTIFIER) throwException();
		else index++;
		
		while(getTokenIDofIndex() == ParserData.SCOMMA) {
			index++;
			
			if(getTokenIDofIndex() != ParserData.SIDENTIFIER) throwException();
			else index++;
		}
	}
	
	/**ブロック*/
	private void parseBlock() throws ParserException {
		parseVarDecl();
		
		parseSubProgramDeclArray();
	}
	
	/**変数宣言*/
	private void parseVarDecl() throws ParserException {
		if(getTokenIDofIndex() == ParserData.SVAR) {
			index++;
			
			parseVarDeclArray();
		}
	}
	
	/**変数宣言の並び*/
	private void parseVarDeclArray() throws ParserException {
		parseVarNameArray(false);
		
		if(getTokenIDofIndex() != ParserData.SCOLON) throwException();
		else index++;
		
		parseType();
		
		if(getTokenIDofIndex() != ParserData.SSEMICOLON) throwException();
		else index++;
		
		while(parseVarNameArray(true)) {
			if(getTokenIDofIndex() != ParserData.SCOLON) throwException();
			else index++;
			
			parseType();
			
			if(getTokenIDofIndex() != ParserData.SSEMICOLON) throwException();
			else index++;
		}
	}
	
	/**変数名の並び*/
	private boolean parseVarNameArray(boolean isOption) throws ParserException {
		if(!parseVarName(true)) {
			if(isOption) return false;
			else throwException();
		}
		
		while(getTokenIDofIndex() == ParserData.SCOMMA) {
			index++;
			
			parseVarName(false);
		}
		
		return true;
	}
	
	/**変数名*/
	private boolean parseVarName(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() != ParserData.SIDENTIFIER) {
			if(isOption) return false;
			else throwException();
		}
		else index++;
		
		return true;
	}
	
	/**型*/
	private void parseType() throws ParserException {
		if(parseNormalType(true)) return;
		else if(parseArrayType(true)) return;
		else throwException();
	}
	
	/**標準型*/
	private boolean parseNormalType(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() == ParserData.SINTEGER) index++;
		else if(getTokenIDofIndex() == ParserData.SCHAR) index++;
		else if(getTokenIDofIndex() == ParserData.SBOOLEAN) index++;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**配列型*/
	private boolean parseArrayType(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() != ParserData.SARRAY){
			if(isOption) return false;
			else throwException();
		}
		else index++;
		
		if(getTokenIDofIndex() != ParserData.SLBRACKET) throwException();
		else index++;
		
		parseIndexMin();
		
		if(getTokenIDofIndex() != ParserData.SRANGE) throwException();
		else index++;
		
		parseIndexMax();
		
		if(getTokenIDofIndex() != ParserData.SRBRACKET) throwException();
		else index++;
		
		if(getTokenIDofIndex() != ParserData.SOF) throwException();
		else index++;
		
		parseNormalType(false);
		
		return true;
	}
	
	/**添字の最小値*/
	private void parseIndexMin() throws ParserException {
		parseInteger();
	}
	
	/**添字の最大値*/
	private void parseIndexMax() throws ParserException {
		parseInteger();
	}
	
	/**整数*/
	private void parseInteger() throws ParserException {
		parseSign(true);
		
		if(getTokenIDofIndex() != ParserData.SCONSTANT) throwException();
		else index++;
	}
	
	/**符号*/
	private boolean parseSign(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() == ParserData.SPLUS) index++;
		else if(getTokenIDofIndex() == ParserData.SMINUS) index++;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**副プログラム宣言群*/
	private void parseSubProgramDeclArray() throws ParserException {
		while(parseSubProgramDecl(true)) {
			if(getTokenIDofIndex() != ParserData.SSEMICOLON) throwException();
			else index++;
		}
	}
	
	/**副プログラム宣言*/
	private boolean parseSubProgramDecl(boolean isOption) throws ParserException {
		if(!parseSubProgramHeader(true)) {
			if(isOption) return false;
			else throwException();
		}
		
		parseVarDecl();
		
		parseStatementBlock(false);
		
		return true;
	}
	
	/**副プログラム頭部*/
	private boolean parseSubProgramHeader(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() != ParserData.SPROCEDURE) {
			if(isOption) return false;
			else throwException();
		}
		else index++;
		
		parseRoutineName(false);
		
		parseParam();
		
		if(getTokenIDofIndex() != ParserData.SSEMICOLON) throwException();
		else index++;
		
		return true;
	}
	
	/**手続き名*/
	private boolean parseRoutineName(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() != ParserData.SIDENTIFIER) {
			if(isOption) return false;
			else throwException();
		}
		else index++;
		
		return true;
	}
	
	/**仮パラメータ*/
	private void parseParam() throws ParserException {
		if(getTokenIDofIndex() == ParserData.SLPAREN) {
			index++;
			
			parseParamArray();
			
			if(getTokenIDofIndex() != ParserData.SRPAREN) throwException();
			else index++;
		}
	}
	
	/**仮パラメータの並び*/
	private void parseParamArray() throws ParserException {
		parseParamNameArray();
		
		if(getTokenIDofIndex() != ParserData.SCOLON) throwException();
		else index++;
		
		parseNormalType(false);
		
		while(getTokenIDofIndex() == ParserData.SSEMICOLON) {
			index++;
			
			parseParamNameArray();
			
			if(getTokenIDofIndex() != ParserData.SCOLON) throwException();
			else index++;
			
			parseNormalType(false);
		}
	}
	
	/**仮パラメータ名の並び*/
	private void parseParamNameArray() throws ParserException {
		parseParamName();
		
		while(getTokenIDofIndex() == ParserData.SCOMMA) {
			index++;
			
			parseParamName();
		}
	}
	
	/**仮パラメータ名*/
	private void parseParamName() throws ParserException {
		if(getTokenIDofIndex() != ParserData.SIDENTIFIER) throwException();
		else index++;
	}
	
	/**複合文*/
	private boolean parseStatementBlock(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() != ParserData.SBEGIN) {
			if(isOption) return false;
			else throwException();
		}
		else index++;
		
		parseStatementArray();
		
		if(getTokenIDofIndex() != ParserData.SEND) throwException();
		else index++;
		
		return true;
	}
	
	/**文の並び*/
	private void parseStatementArray() throws ParserException {
		parseStatement();
		
		while(getTokenIDofIndex() == ParserData.SSEMICOLON) {
			index++;
			
			parseStatement();
		}
	}
	
	/**文*/
	private void parseStatement() throws ParserException {
		if(getTokenIDofIndex() == ParserData.SIF) {
			index++;
			
			parseExpression();
			
			if(getTokenIDofIndex() != ParserData.STHEN) throwException();
			else index++;
			
			parseStatementBlock(false);
			
			if(getTokenIDofIndex() == ParserData.SELSE) {
				index++;
				
				parseStatementBlock(false);
			}
		}
		else if(getTokenIDofIndex() == ParserData.SWHILE) {
			index++;
			
			parseExpression();
			
			if(getTokenIDofIndex() != ParserData.SDO) throwException();
			else index++;
			
			parseStatement();
		}
		else if(parseNormalStatement(true)) return;
		else throwException();
	}
	
	/**基本文*/
	private boolean parseNormalStatement(boolean isOption) throws ParserException {
		if(parseAssignStatement(true)) ;
		else if(parseRoutineCallStatement(true)) ;
		else if(parseIOStatement(true)) ;
		else if(parseStatementBlock(true)) ;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**代入文*/
	private boolean parseAssignStatement(boolean isOption) throws ParserException {
		int oldIndex = index;
		
		if(!parseLeftValue(true)) {
			if(isOption) return false;
			else throwException();
		}
		
		if(getTokenIDofIndex() != ParserData.SASSIGN) {
			if(isOption) {
				index = oldIndex;
				return false;
			}
			else throwException();
		}
		else index++;
		
		parseExpression();
		
		return true;
	}
	
	/**左辺*/
	private boolean parseLeftValue(boolean isOption) throws ParserException {
		if(!parseVariable(true)) {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**変数*/
	private boolean parseVariable(boolean isOption) throws ParserException {
		if(parseIndexedVariable(true)) ;
		else if(parseNormalVariable(true)) ;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**純変数*/
	private boolean parseNormalVariable(boolean isOption) throws ParserException {
		if(!parseVarName(true)) {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**添字付き変数*/
	private boolean parseIndexedVariable(boolean isOption) throws ParserException {
		int oldIndex = index;
		
		if(!parseVarName(true)) {
			if(isOption) return false;
			else throwException();
		}
		
		if(getTokenIDofIndex() != ParserData.SLBRACKET) {
			if(isOption) {
				index = oldIndex;
				return false;
			}
			else throwException();
		}
		else index++;
		
		parseIndex();
		
		if(getTokenIDofIndex() != ParserData.SRBRACKET) throwException();
		else index++;
		
		return true;
	}
	
	/**添字*/
	private void parseIndex() throws ParserException {
		parseExpression();
	}
	
	/**手続き呼出し文*/
	private boolean parseRoutineCallStatement(boolean isOption) throws ParserException {
		if(!parseRoutineName(true)) {
			if(isOption) return false;
			else throwException();
		}
		
		if(getTokenIDofIndex() == ParserData.SLPAREN) {
			index++;
			
			parseExpressionArray();
			
			if(getTokenIDofIndex() != ParserData.SRPAREN) throwException();
			else index++;
		}
		
		return true;
	}
	
	/**式の並び*/
	private void parseExpressionArray() throws ParserException {
		parseExpression();
		
		while(getTokenIDofIndex() == ParserData.SCOMMA) {
			index++;
			
			parseExpression();
		}
	}
	
	/**式*/
	private void parseExpression() throws ParserException {
		parseSimpleExpression();
		
		if(parseRelativeOp(true)) {
			parseSimpleExpression();
		}
	}
	
	/**単純式*/
	private void parseSimpleExpression() throws ParserException {
		parseSign(true);
		
		parseTerm();
		
		while(parseAdditiveOp(true)) {
			parseTerm();
		}
	}
	
	/**項*/
	private void parseTerm() throws ParserException {
		parseFactor();
		
		while(parseMultiplicativeOp(true)) {
			parseFactor();
		}
	}
	
	/**因子*/
	private void parseFactor() throws ParserException {
		if(parseVariable(true)) return;
		else if(parseConstant(true)) return;
		else if(getTokenIDofIndex() == ParserData.SLPAREN) {
			index++;
			
			parseExpression();
			
			if(getTokenIDofIndex() != ParserData.SRPAREN) throwException();
			else index++;
		}
		else if(getTokenIDofIndex() == ParserData.SNOT) {
			index++;
			
			parseFactor();
		}
		else throwException();
	}
	
	/**関係演算子*/
	private boolean parseRelativeOp(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() == ParserData.SEQUAL) index++;
		else if(getTokenIDofIndex() == ParserData.SNOTEQUAL) index++;
		else if(getTokenIDofIndex() == ParserData.SLESS) index++;
		else if(getTokenIDofIndex() == ParserData.SLESSEQUAL) index++;
		else if(getTokenIDofIndex() == ParserData.SGREAT) index++;
		else if(getTokenIDofIndex() == ParserData.SGREATEQUAL) index++;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**加法演算子*/
	private boolean parseAdditiveOp(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() == ParserData.SPLUS) index++;
		else if(getTokenIDofIndex() == ParserData.SMINUS) index++;
		else if(getTokenIDofIndex() == ParserData.SOR) index++;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**乗法演算子*/
	private boolean parseMultiplicativeOp(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() == ParserData.SSTAR) index++;
		else if(getTokenIDofIndex() == ParserData.SDIVD) index++;
		else if(getTokenIDofIndex() == ParserData.SMOD) index++;
		else if(getTokenIDofIndex() == ParserData.SAND) index++;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**入出力文*/
	private boolean parseIOStatement(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() == ParserData.SREADLN) {
			index++;
			
			if(getTokenIDofIndex() == ParserData.SLPAREN) {
				index++;
				
				parseVarArray();
				
				if(getTokenIDofIndex() != ParserData.SRPAREN) throwException();
				else index++;
			}
		}
		else if(getTokenIDofIndex() == ParserData.SWRITELN) {
			index++;
			
			if(getTokenIDofIndex() == ParserData.SLPAREN) {
				index++;
				
				parseExpressionArray();
				
				if(getTokenIDofIndex() != ParserData.SRPAREN) throwException();
				else index++;
			}
		}
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
	
	/**変数の並び*/
	private void parseVarArray() throws ParserException {
		parseVariable(false);
		
		while(getTokenIDofIndex() == ParserData.SCOMMA) {
			index++;
			
			parseVariable(false);
		}
	}
	
	/**定数*/
	private boolean parseConstant(boolean isOption) throws ParserException {
		if(getTokenIDofIndex() == ParserData.SCONSTANT) index++;
		else if(getTokenIDofIndex() == ParserData.SSTRING) index++;
		else if(getTokenIDofIndex() == ParserData.SFALSE) index++;
		else if(getTokenIDofIndex() == ParserData.STRUE) index++;
		else {
			if(isOption) return false;
			else throwException();
		}
		
		return true;
	}
}
