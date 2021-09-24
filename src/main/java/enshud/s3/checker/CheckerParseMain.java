package enshud.s3.checker;

import java.io.*;
import java.util.*;

/**
 * CheckerParseMain クラスのインスタンスを使用すると TS ファイルを構文・意味解析できます。
 */
final class CheckerParseMain {
	private ArrayList<CheckerParseData> list = null;
	private boolean initialized;
	
	private int index;
	
	CheckerTypeCheck typeCheck;
	
	/**
	 * CheckerParseMain クラスのコンストラクタです。
	 */
	CheckerParseMain() {
		initialized = false;
		typeCheck = new CheckerTypeCheck();
	}
	
	/**
	 * TS ファイルからトークン列を読み込んで構文・意味解析の初期化を行うメソッドです。
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
		
		list = new ArrayList<CheckerParseData>();
		in = new BufferedReader(new FileReader(inputFileName));
		
		while((str = in.readLine()) != null) {
			String[] spl = str.split("\t");
			if(spl.length == 4) {
				String string = spl[0];
				int tokenID = Integer.parseInt(spl[2]);
				int lineNum = Integer.parseInt(spl[3]);
				if(spl[1].equals(CheckerParseData.getTokenNameof(tokenID))) {
					list.add(new CheckerParseData(string, tokenID, lineNum));
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
	
	private String getStringofIndex() {
		if(index >= 0 && index < list.size())
			return ((CheckerParseData)list.get(index)).getString();
		else
			return null;
	}
	
	private int getTokenIDofIndex() {
		if(index >= 0 && index < list.size())
			return ((CheckerParseData)list.get(index)).getTokenID();
		else
			return -1;
	}
	
	private int getLineNumofIndex() {
		if(index >= 0 && index < list.size())
			return ((CheckerParseData)list.get(index)).getLineNum();
		else
			return -1;
	}
	
	/**Exception*/
	private void throwParseException() throws CheckerException {
		throw new CheckerException(getLineNumofIndex(), true);
	}
	private void throwCheckException() throws CheckerException {
		throw new CheckerException(getLineNumofIndex(), false);
	}
	
	/**
	 * 構文解析を行うメソッドです。
	 */
	public void doParser() {
		if(initialized) {
			index = 0;
			try {
				parseProgram();
			} catch (CheckerException e) {
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
	private void parseProgram() throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SPROGRAM) throwParseException();
		else index++;
		
		parseProgramName();
		
		if(getTokenIDofIndex() != CheckerParseData.SLPAREN) throwParseException();
		else index++;

		parseNameArray();

		if(getTokenIDofIndex() != CheckerParseData.SRPAREN) throwParseException();
		else index++;

		if(getTokenIDofIndex() != CheckerParseData.SSEMICOLON) throwParseException();
		else index++;

		parseBlock();
		
		parseStatementBlock(false);
		
		if(getTokenIDofIndex() != CheckerParseData.SDOT) throwParseException();
		else index++;
	}
	
	/**プログラム名*/
	private void parseProgramName() throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SIDENTIFIER) throwParseException();
		else index++;
	}
	
	/**名前の並び*/
	private void parseNameArray() throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SIDENTIFIER) throwParseException();
		else index++;
		
		while(getTokenIDofIndex() == CheckerParseData.SCOMMA) {
			index++;
			
			if(getTokenIDofIndex() != CheckerParseData.SIDENTIFIER) throwParseException();
			else index++;
		}
	}
	
	/**ブロック*/
	private void parseBlock() throws CheckerException {
		parseVarDecl(true);
		
		parseSubProgramDeclArray();
	}
	
	/**変数宣言*/
	private void parseVarDecl(boolean isGlobal) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SVAR) {
			index++;
			
			parseVarDeclArray(isGlobal);
		}
	}
	
	/**変数宣言の並び*/
	private void parseVarDeclArray(boolean isGlobal) throws CheckerException {
		parseVarNameArray(false);
		
		if(getTokenIDofIndex() != CheckerParseData.SCOLON) throwParseException();
		else index++;
		
		parseType();
		
		if(isGlobal) {
			if(!typeCheck.addGlobalIdentifiers()) throwCheckException();
		}
		else {
			if(!typeCheck.addLocalIdentifiers()) throwCheckException();
		}
		typeCheck.clearStringList();
		
		if(getTokenIDofIndex() != CheckerParseData.SSEMICOLON) throwParseException();
		else index++;
		
		while(parseVarNameArray(true)) {
			if(getTokenIDofIndex() != CheckerParseData.SCOLON) throwParseException();
			else index++;
			
			parseType();
			
			if(isGlobal) {
				if(!typeCheck.addGlobalIdentifiers()) throwCheckException();
			}
			else {
				if(!typeCheck.addLocalIdentifiers()) throwCheckException();
			}
			typeCheck.clearStringList();
			
			if(getTokenIDofIndex() != CheckerParseData.SSEMICOLON) throwParseException();
			else index++;
		}
	}
	
	/**変数名の並び*/
	private boolean parseVarNameArray(boolean isOption) throws CheckerException {
		if(!parseVarName(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		while(getTokenIDofIndex() == CheckerParseData.SCOMMA) {
			index++;
			
			parseVarName(false);
		}
		
		return true;
	}
	
	/**変数名*/
	private boolean parseVarName(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SIDENTIFIER) {
			if(isOption) return false;
			else throwParseException();
		}
		else {
			typeCheck.addString(getStringofIndex());
			index++;
		}
		
		return true;
	}
	
	/**型*/
	private void parseType() throws CheckerException {
		if(parseNormalType(true)) return;
		else if(parseArrayType(true)) return;
		else throwParseException();
	}
	
	/**標準型*/
	private boolean parseNormalType(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SINTEGER) {
			typeCheck.pushType(CheckerTypeData.TYPE_INTEGER);
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SCHAR) {
			typeCheck.pushType(CheckerTypeData.TYPE_CHAR);
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SBOOLEAN) {
			typeCheck.pushType(CheckerTypeData.TYPE_BOOLEAN);
			index++;
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**配列型*/
	private boolean parseArrayType(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SARRAY){
			if(isOption) return false;
			else throwParseException();
		}
		else index++;
		
		if(getTokenIDofIndex() != CheckerParseData.SLBRACKET) throwParseException();
		else index++;
		
		parseIndexMin();
		
		if(getTokenIDofIndex() != CheckerParseData.SRANGE) throwParseException();
		else index++;
		
		parseIndexMax();
		
		if(!typeCheck.checkIndex()) throwCheckException();
		
		if(getTokenIDofIndex() != CheckerParseData.SRBRACKET) throwParseException();
		else index++;
		
		if(getTokenIDofIndex() != CheckerParseData.SOF) throwParseException();
		else index++;
		
		parseNormalType(false);
		typeCheck.fromNormalTypeToArrayType();
		
		return true;
	}
	
	/**添字の最小値*/
	private void parseIndexMin() throws CheckerException {
		parseInteger();
		if(!typeCheck.setIndexMin()) throwCheckException();
	}
	
	/**添字の最大値*/
	private void parseIndexMax() throws CheckerException {
		parseInteger();
		if(!typeCheck.setIndexMax()) throwCheckException();
	}
	
	/**整数*/
	private void parseInteger() throws CheckerException {
		parseSign(true);
		
		if(getTokenIDofIndex() != CheckerParseData.SCONSTANT) throwParseException();
		else {
			if(!typeCheck.setNumberConstant(getStringofIndex())) throwCheckException();
			index++;
		}
	}
	
	/**符号*/
	private boolean parseSign(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SPLUS) {
			typeCheck.setSign('+');
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SMINUS) {
			typeCheck.setSign('-');
			index++;
		}
		else {
			if(isOption) {
				typeCheck.setSign('\0');
				return false;
			}
			else throwParseException();
		}
		
		return true;
	}
	
	/**副プログラム宣言群*/
	private void parseSubProgramDeclArray() throws CheckerException {
		while(parseSubProgramDecl(true)) {
			if(getTokenIDofIndex() != CheckerParseData.SSEMICOLON) throwParseException();
			else index++;
		}
	}
	
	/**副プログラム宣言*/
	private boolean parseSubProgramDecl(boolean isOption) throws CheckerException {
		if(!parseSubProgramHeader(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		parseVarDecl(false);
		
		parseStatementBlock(false);
		
		typeCheck.clearLocalIdentifierList();
		
		return true;
	}
	
	/**副プログラム頭部*/
	private boolean parseSubProgramHeader(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SPROCEDURE) {
			if(isOption) return false;
			else throwParseException();
		}
		else index++;
		
		parseRoutineName(false);
		
		parseParam();
		
		if(!typeCheck.addGlobalSubroutineIdentifier()) throwCheckException();
		else typeCheck.clearArgumentTypeList();
		
		if(getTokenIDofIndex() != CheckerParseData.SSEMICOLON) throwParseException();
		else index++;
		
		return true;
	}
	
	/**手続き名*/
	private boolean parseRoutineName(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SIDENTIFIER) {
			if(isOption) return false;
			else throwParseException();
		}
		else {
			typeCheck.setSubroutineString(getStringofIndex());
			index++;
		}
		
		return true;
	}
	
	/**仮パラメータ*/
	private void parseParam() throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SLPAREN) {
			index++;
			
			parseParamArray();
			
			if(getTokenIDofIndex() != CheckerParseData.SRPAREN) throwParseException();
			else index++;
		}
	}
	
	/**仮パラメータの並び*/
	private void parseParamArray() throws CheckerException {
		parseParamNameArray();
		
		if(getTokenIDofIndex() != CheckerParseData.SCOLON) throwParseException();
		else index++;
		
		parseNormalType(false);
		
		typeCheck.addArgumentTypes();
		if(!typeCheck.addLocalIdentifiers()) throwCheckException();
		else typeCheck.clearStringList();
		
		while(getTokenIDofIndex() == CheckerParseData.SSEMICOLON) {
			index++;
			
			parseParamNameArray();
			
			if(getTokenIDofIndex() != CheckerParseData.SCOLON) throwParseException();
			else index++;
			
			parseNormalType(false);
			
			typeCheck.addArgumentTypes();
			if(!typeCheck.addLocalIdentifiers()) throwCheckException();
			else typeCheck.clearStringList();
		}
	}
	
	/**仮パラメータ名の並び*/
	private void parseParamNameArray() throws CheckerException {
		parseParamName();
		
		while(getTokenIDofIndex() == CheckerParseData.SCOMMA) {
			index++;
			
			parseParamName();
		}
	}
	
	/**仮パラメータ名*/
	private void parseParamName() throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SIDENTIFIER) throwParseException();
		else {
			typeCheck.addString(getStringofIndex());
			index++;
		}
	}
	
	/**複合文*/
	private boolean parseStatementBlock(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() != CheckerParseData.SBEGIN) {
			if(isOption) return false;
			else throwParseException();
		}
		else index++;
		
		parseStatementArray();
		
		if(getTokenIDofIndex() != CheckerParseData.SEND) throwParseException();
		else index++;
		
		return true;
	}
	
	/**文の並び*/
	private void parseStatementArray() throws CheckerException {
		parseStatement();
		
		while(getTokenIDofIndex() == CheckerParseData.SSEMICOLON) {
			index++;
			
			parseStatement();
		}
	}
	
	/**文*/
	private void parseStatement() throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SIF) {
			index++;
			
			typeCheck.pushType(CheckerTypeData.TYPE_BOOLEAN);
			parseExpression();
			if(!typeCheck.checkType(true)) throwCheckException();
			
			if(getTokenIDofIndex() != CheckerParseData.STHEN) throwParseException();
			else index++;
			
			parseStatementBlock(false);
			
			if(getTokenIDofIndex() == CheckerParseData.SELSE) {
				index++;
				
				parseStatementBlock(false);
			}
		}
		else if(getTokenIDofIndex() == CheckerParseData.SWHILE) {
			index++;
			
			typeCheck.pushType(CheckerTypeData.TYPE_BOOLEAN);
			parseExpression();
			if(!typeCheck.checkType(true)) throwCheckException();
			
			if(getTokenIDofIndex() != CheckerParseData.SDO) throwParseException();
			else index++;
			
			parseStatement();
		}
		else if(parseNormalStatement(true)) return;
		else throwParseException();
	}
	
	/**基本文*/
	private boolean parseNormalStatement(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SIDENTIFIER) {
			if(parseAssignStatement(true)) ;
			else if(parseRoutineCallStatement(true)) ;
			else throwCheckException();
		}
		else if(parseIOStatement(true)) ;
		else if(parseStatementBlock(true)) ;
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**代入文*/
	private boolean parseAssignStatement(boolean isOption) throws CheckerException {
		if(!parseLeftValue(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		if(getTokenIDofIndex() != CheckerParseData.SASSIGN) throwParseException();
		else index++;
		
		if(!CheckerTypeData.isNormalType(typeCheck.peekType())) throwCheckException();
		
		parseExpression();
		if(!typeCheck.checkType(true)) throwCheckException();
		
		return true;
	}
	
	/**左辺*/
	private boolean parseLeftValue(boolean isOption) throws CheckerException {
		if(!parseVariable(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**変数*/
	private boolean parseVariable(boolean isOption) throws CheckerException {
		if(parseIndexedVariable(true)) ;
		else if(parseNormalVariable(true)) ;
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**純変数*/
	private boolean parseNormalVariable(boolean isOption) throws CheckerException {
		int oldIndex = index;
		
		if(!parseVarName(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		if(!typeCheck.pushVariableType()) {
			index = oldIndex;
			if(isOption) {
				typeCheck.clearStringList();
				return false;
			}
			else throwCheckException();
		}
		
		return true;
	}
	
	/**添字付き変数*/
	private boolean parseIndexedVariable(boolean isOption) throws CheckerException {
		int oldIndex = index;
		
		if(!parseVarName(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		if(!typeCheck.pushVariableType()) {
			index = oldIndex;
			if(isOption) {
				typeCheck.clearStringList();
				return false;
			}
			else throwCheckException();
		}
		else if(!CheckerTypeData.isArrayType(typeCheck.peekType())) {
			index = oldIndex;
			if(isOption) {
				typeCheck.popType();
				return false;
			}
			else throwCheckException();
		}
		
		if(getTokenIDofIndex() != CheckerParseData.SLBRACKET) {
			if(isOption) {
				index = oldIndex;
				typeCheck.popType();
				return false;
			}
			else throwParseException();
		}
		else index++;
		
		parseIndex();
		
		if(getTokenIDofIndex() != CheckerParseData.SRBRACKET) throwParseException();
		else index++;
		
		if(!typeCheck.fromArrayTypeToNormalType()) throwCheckException();
		
		return true;
	}
	
	/**添字*/
	private void parseIndex() throws CheckerException {
		typeCheck.pushType(CheckerTypeData.TYPE_INTEGER);
		parseExpression();
		if(!typeCheck.checkType(true)) throwCheckException();
	}
	
	/**手続き呼出し文*/
	private boolean parseRoutineCallStatement(boolean isOption) throws CheckerException {
		int oldIndex = index;
		
		if(!parseRoutineName(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		if(!typeCheck.pushSubroutineArgumentsType()) {
			index = oldIndex;
			throwCheckException();
		}
		
		if(getTokenIDofIndex() == CheckerParseData.SLPAREN) {
			index++;
			
			parseExpressionArray(false);
			
			if(getTokenIDofIndex() != CheckerParseData.SRPAREN) throwParseException();
			else index++;
		}
		
		if(typeCheck.popType() != CheckerTypeData.TYPE_SUBROUTINE) throwCheckException();
		
		return true;
	}
	
	/**式の並び*/
	private void parseExpressionArray(boolean isIORoutine) throws CheckerException {
		parseExpression();
		if(!isIORoutine) {
			if(!typeCheck.checkType(true)) throwCheckException();
		}
		else typeCheck.popType();
		
		while(getTokenIDofIndex() == CheckerParseData.SCOMMA) {
			index++;
			
			parseExpression();
			if(!isIORoutine) {
				if(!typeCheck.checkType(true)) throwCheckException();
			}
			else typeCheck.popType();
		}
	}
	
	/**式*/
	private void parseExpression() throws CheckerException {
		parseSimpleExpression();
		
		if(parseRelativeOp(true)) {
			parseSimpleExpression();
			if(!typeCheck.checkType(true)) throwCheckException();
			else typeCheck.pushType(CheckerTypeData.TYPE_BOOLEAN);
		}
	}
	
	/**単純式*/
	private void parseSimpleExpression() throws CheckerException {
		parseSign(true);
		
		parseTerm();
		if(typeCheck.hasSign()) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_INTEGER) throwCheckException();
		}
		
		while(parseAdditiveOp(true)) {
			parseTerm();
			
			if(!typeCheck.checkType(false)) throwCheckException();
		}
	}
	
	/**項*/
	private void parseTerm() throws CheckerException {
		parseFactor();
		
		while(parseMultiplicativeOp(true)) {
			parseFactor();
			
			if(!typeCheck.checkType(false)) throwCheckException();
		}
	}
	
	/**因子*/
	private void parseFactor() throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SIDENTIFIER) {
			parseVariable(false);
		}
		else if(parseConstant(true)) return;
		else if(getTokenIDofIndex() == CheckerParseData.SLPAREN) {
			index++;
			
			parseExpression();
			
			if(getTokenIDofIndex() != CheckerParseData.SRPAREN) throwParseException();
			else index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SNOT) {
			index++;
			
			parseFactor();
			if(typeCheck.peekType() != CheckerTypeData.TYPE_BOOLEAN) throwCheckException();
		}
		else throwParseException();
	}
	
	/**関係演算子*/
	private boolean parseRelativeOp(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SEQUAL) index++;
		else if(getTokenIDofIndex() == CheckerParseData.SNOTEQUAL) index++;
		else if(getTokenIDofIndex() == CheckerParseData.SLESS) index++;
		else if(getTokenIDofIndex() == CheckerParseData.SLESSEQUAL) index++;
		else if(getTokenIDofIndex() == CheckerParseData.SGREAT) index++;
		else if(getTokenIDofIndex() == CheckerParseData.SGREATEQUAL) index++;
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**加法演算子*/
	private boolean parseAdditiveOp(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SPLUS) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_INTEGER) throwCheckException();
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SMINUS) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_INTEGER) throwCheckException();
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SOR) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_BOOLEAN) throwCheckException();
			index++;
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**乗法演算子*/
	private boolean parseMultiplicativeOp(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SSTAR) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_INTEGER) throwCheckException();
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SDIVD) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_INTEGER) throwCheckException();
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SMOD) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_INTEGER) throwCheckException();
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SAND) {
			if(typeCheck.peekType() != CheckerTypeData.TYPE_BOOLEAN) throwCheckException();
			index++;
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**入出力文*/
	private boolean parseIOStatement(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SREADLN) {
			index++;
			
			if(getTokenIDofIndex() == CheckerParseData.SLPAREN) {
				index++;
				
				parseVarArray();
				
				if(getTokenIDofIndex() != CheckerParseData.SRPAREN) throwParseException();
				else index++;
			}
		}
		else if(getTokenIDofIndex() == CheckerParseData.SWRITELN) {
			index++;
			
			if(getTokenIDofIndex() == CheckerParseData.SLPAREN) {
				index++;
				
				parseExpressionArray(true);
				
				if(getTokenIDofIndex() != CheckerParseData.SRPAREN) throwParseException();
				else index++;
			}
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**変数の並び*/
	private void parseVarArray() throws CheckerException {
		parseVariable(false);
		typeCheck.popType();
		
		while(getTokenIDofIndex() == CheckerParseData.SCOMMA) {
			index++;
			
			parseVariable(false);
			typeCheck.popType();
		}
	}
	
	/**定数*/
	private boolean parseConstant(boolean isOption) throws CheckerException {
		if(getTokenIDofIndex() == CheckerParseData.SCONSTANT) {
			typeCheck.pushType(CheckerTypeData.TYPE_INTEGER);
			if(!typeCheck.setNumberConstant(getStringofIndex())) throwCheckException();
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SSTRING) {
			if(getStringofIndex().length() == 3)
				typeCheck.pushType(CheckerTypeData.TYPE_CHAR);
			else
				typeCheck.pushType(CheckerTypeData.TYPE_ARRAY_CHAR);
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.SFALSE) {
			typeCheck.pushType(CheckerTypeData.TYPE_BOOLEAN);
			index++;
		}
		else if(getTokenIDofIndex() == CheckerParseData.STRUE) {
			typeCheck.pushType(CheckerTypeData.TYPE_BOOLEAN);
			index++;
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
}
