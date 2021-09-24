package enshud.s4.compiler;

import java.io.*;
import java.util.*;

/**
 * <p><b>名前</b>: <b>{@code CompilerParseMain} クラス</b>
 * <p><b>説明</b>: 字句解析結果(TSファイル)をCASL IIアセンブリに変換するクラスです。
 * <p><b>生成子</b>:
 * <ul>
 * <li>{@link #CompilerParseMain()}
 * </ul>
 * <p><b>全域メソッド</b>:
 * <ul>
 * <li>{@link #Initialize(String)}
 * <li>{@link #doParser()}
 * <li>{@link #createCAS(String)}
 * </ul>
 * <p><b>詳細</b>: <b>{@code CompilerParseMain}</b> クラスは三つの段階で有効なTSファイルをCASファイルに変換します。
 * TSファイルはPascal風言語の字句解析結果ファイルです。CASファイルはCASL IIのアセンブリファイルです。<br>
 * 変換の第一段階はTSファイルの読み込みです。TSファイルの読み込みは {@link #Initialize(String)} メソッドで行います。<br>
 * 第二段階は構文の解析およびCASL IIアセンブリコードの生成です。 {@link #doParser()} メソッドで行います。<br>
 * 第三段階はCASファイルへの書き込みです。 {@link #createCAS(String)} メソッドで行います。<br>
 * 入力のTSファイルが有効なものであり、CASファイルの生成段階が正しく行われたら、 <b>{@code CompilerParseMain}</b> クラスのインスタンスは有効なCASファイルを生成できます。
 */
final class CompilerParseMain {
	private ArrayList<CompilerParseData> list = null;
	private boolean initialized;
	
	private int index;
	private int op;
	
	private CompilerTypeCheck typeCheck;
	
	private CompilerCodeGen codeGen;
	private CompilerLabelGen labelGen;
	
	/**
	 * <p><b>名前</b>: <b>{@code CompilerParseMain()} 生成子</b>
	 * <p><b>階層</b>: {@link CompilerParseMain}.<b>{@code CompilerParseMain()}</b>
	 * <p><b>説明</b>: {@link CompilerParseMain} クラスのインスタンスを初期化する生成子です。
	 * <p><b>引数</b>: 無し
	 * <p><b>参照</b>:
	 * <ul>
	 * <li>{@link CompilerLabelGen} クラス
	 * <li>{@link CompilerCodeGen} クラス
	 * <li>{@link CompilerTypeCheck} クラス
	 * </ul>
	 */
	public CompilerParseMain() {
		initialized = false;
		labelGen = new CompilerLabelGen();
		codeGen = new CompilerCodeGen();
		typeCheck = new CompilerTypeCheck(labelGen);
	}
	
	/**
	 * <p><b>名前</b>: <b>{@code Initialize(String)} メソッド</b>
	 * <p><b>階層</b>: {@link CompilerParseMain}.<b>{@code Initialize(String)}</b>
	 * <p><b>説明</b>: TSファイルを読み込みます。
	 * <p><b>引数</b>:
	 * <ul>
	 * <li><b>{@code inputFileName}</b>: TSファイルの経路名</li>
	 * </ul>
	 * <p><b>戻り値</b>: 読み込みが成功したら {@code true} を返し、失敗したら {@code false} を返します。
	 */
	public boolean Initialize(String inputFileName) throws FileNotFoundException, IOException {
		BufferedReader in;
		String str;
		
		list = new ArrayList<CompilerParseData>();
		in = new BufferedReader(new FileReader(inputFileName));
		
		while((str = in.readLine()) != null) {
			String[] spl = str.split("\t");
			if(spl.length == 4) {
				String string = spl[0];
				int tokenID = Integer.parseInt(spl[2]);
				int lineNum = Integer.parseInt(spl[3]);
				if(spl[1].equals(CompilerParseData.getTokenNameof(tokenID))) {
					list.add(new CompilerParseData(string, tokenID, lineNum));
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
	
	/**
	 * <p><b>名前</b>: <b>{@code createCAS(String)} メソッド</b>
	 * <p><b>階層</b>: {@link CompilerParseMain}.<b>{@code createCAS(String)}</b>
	 * <p><b>説明</b>: CASファイルを書き込みます。
	 * <p><b>引数</b>:
	 * <ul>
	 * <li><b>{@code outputFileName}</b>: CASファイルの経路名</li>
	 * </ul>
	 * <p><b>戻り値</b>: 無し
	 */
	public void createCAS(String outputFileName) throws IOException {
		if(initialized) {
			BufferedWriter out;
			List<String> codeList;
			
			out = new BufferedWriter(new FileWriter(outputFileName));
			codeList = codeGen.getCodeList();
			
			for(int i = 0; i < codeList.size(); i++) {
				out.write(codeList.get(i));
				out.newLine();
			}
			
			out.close();
		}
	}
	
	/*Getters*/
	private String getStringofIndex() {
		if(index >= 0 && index < list.size())
			return ((CompilerParseData)list.get(index)).getString();
		else
			return null;
	}
	
	private int getTokenIDofIndex() {
		if(index >= 0 && index < list.size())
			return ((CompilerParseData)list.get(index)).getTokenID();
		else
			return -1;
	}
	
	private int getLineNumofIndex() {
		if(index >= 0 && index < list.size())
			return ((CompilerParseData)list.get(index)).getLineNum();
		else
			return -1;
	}
	
	/*Exceptions*/
	private void throwParseException() throws CompilerException {
		throw new CompilerException(getLineNumofIndex(), true);
	}
	
	private void throwCheckException() throws CompilerException {
		throw new CompilerException(getLineNumofIndex(), false);
	}
	
	/**
	 * <p><b>名前</b>: <b>{@code doParser()} メソッド</b>
	 * <p><b>階層</b>: {@link CompilerParseMain}.<b>{@code doParser()}</b>
	 * <p><b>説明</b>: 構文解析を行い、CASL IIアセンブリコードを生成します。
	 * <p><b>引数</b>: 無し
	 * <p><b>戻り値</b>: 構文解析およびCASL IIアセンブリコードの生成が成功したら {@code true} を返し、失敗したら {@code false} を返します。
	 */
	public boolean doParser() {
		if(initialized) {
			index = 0;
			try {
				parseProgram();
			} catch (CompilerException e) {
				System.err.print(e.getMessage());
				return false;
			}
			
			if(index == list.size()) {
				System.out.print("OK");
				return true;
			}
			else {
				System.err.print("Invalid extra data");
				return false;
			}
		}
		else return false;
	}
	
	/**メモリラベル*/
	private void generateMemoryLabel() {
		ArrayList<String> varLabelList = labelGen.getVarLabelList();
		for(int i = 0; i < varLabelList.size(); i++) {
			String variable = varLabelList.get(i);
			int size = typeCheck.getVarSize(variable);
			if(size > 0) {
				codeGen.add("DS\t" + size, "VAR" + (i + 1));
			}
		}
		
		ArrayList<String> strDataLabelList = labelGen.getStrDataLabelList();
		for(int i = 0; i < strDataLabelList.size(); i++) {
			String strData = strDataLabelList.get(i);
			if(strData != null) {
				codeGen.add("DC\t" + strData, "STR" + (i + 1));
			}
		}
		
		codeGen.flush(true);
	}
	
	/**プログラム*/
	private void parseProgram() throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SPROGRAM) throwParseException();
		else index++;
		
		parseProgramName();
		
		if(getTokenIDofIndex() != CompilerParseData.SLPAREN) throwParseException();
		else index++;

		parseNameArray();

		if(getTokenIDofIndex() != CompilerParseData.SRPAREN) throwParseException();
		else index++;

		if(getTokenIDofIndex() != CompilerParseData.SSEMICOLON) throwParseException();
		else index++;
		
		codeGen.add("START\tBEGIN", "MAIN");
		codeGen.add("LAD\tGR6, 0", "BEGIN");
		codeGen.add("LAD\tGR7, LIBBUF");
		codeGen.flush(true);
		
		codeGen.setFlush(false);
		parseBlock();
		
		codeGen.setFlush(true);
		parseStatementBlock(false);
		
		codeGen.add("RET");
		codeGen.flush(true);
		
		codeGen.appendSub();
		
		codeGen.add("DS\t256", "LIBBUF");
		codeGen.flush(true);
		generateMemoryLabel();
		
		if(getTokenIDofIndex() != CompilerParseData.SDOT) throwParseException();
		else index++;
		
		codeGen.add("END");
		codeGen.flush(true);
	}
	
	/**プログラム名*/
	private void parseProgramName() throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SIDENTIFIER) throwParseException();
		else index++;
	}
	
	/**名前の並び*/
	private void parseNameArray() throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SIDENTIFIER) throwParseException();
		else index++;
		
		while(getTokenIDofIndex() == CompilerParseData.SCOMMA) {
			index++;
			
			if(getTokenIDofIndex() != CompilerParseData.SIDENTIFIER) throwParseException();
			else index++;
		}
	}
	
	/**ブロック*/
	private void parseBlock() throws CompilerException {
		parseVarDecl(true);
		
		parseSubProgramDeclArray();
	}
	
	/**変数宣言*/
	private void parseVarDecl(boolean isGlobal) throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SVAR) {
			index++;
			
			parseVarDeclArray(isGlobal);
		}
	}
	
	/**変数宣言の並び*/
	private void parseVarDeclArray(boolean isGlobal) throws CompilerException {
		parseVarNameArray(false);
		
		if(getTokenIDofIndex() != CompilerParseData.SCOLON) throwParseException();
		else index++;
		
		parseType();
		
		if(isGlobal) {
			if(!typeCheck.addGlobalIdentifiers()) throwCheckException();
		}
		else {
			if(!typeCheck.addLocalIdentifiers()) throwCheckException();
		}
		typeCheck.clearStringList();
		
		if(getTokenIDofIndex() != CompilerParseData.SSEMICOLON) throwParseException();
		else index++;
		
		while(parseVarNameArray(true)) {
			if(getTokenIDofIndex() != CompilerParseData.SCOLON) throwParseException();
			else index++;
			
			parseType();
			
			if(isGlobal) {
				if(!typeCheck.addGlobalIdentifiers()) throwCheckException();
			}
			else {
				if(!typeCheck.addLocalIdentifiers()) throwCheckException();
			}
			typeCheck.clearStringList();
			
			if(getTokenIDofIndex() != CompilerParseData.SSEMICOLON) throwParseException();
			else index++;
		}
	}
	
	/**変数名の並び*/
	private boolean parseVarNameArray(boolean isOption) throws CompilerException {
		if(!parseVarName(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		while(getTokenIDofIndex() == CompilerParseData.SCOMMA) {
			index++;
			
			parseVarName(false);
		}
		
		return true;
	}
	
	/**変数名*/
	private boolean parseVarName(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SIDENTIFIER) {
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
	private void parseType() throws CompilerException {
		if(parseNormalType(true)) return;
		else if(parseArrayType(true)) return;
		else throwParseException();
	}
	
	/**標準型*/
	private boolean parseNormalType(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SINTEGER) {
			typeCheck.pushType(CompilerTypeData.TYPE_INTEGER);
			index++;
		}
		else if(getTokenIDofIndex() == CompilerParseData.SCHAR) {
			typeCheck.pushType(CompilerTypeData.TYPE_CHAR);
			index++;
		}
		else if(getTokenIDofIndex() == CompilerParseData.SBOOLEAN) {
			typeCheck.pushType(CompilerTypeData.TYPE_BOOLEAN);
			index++;
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**配列型*/
	private boolean parseArrayType(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SARRAY){
			if(isOption) return false;
			else throwParseException();
		}
		else index++;
		
		if(getTokenIDofIndex() != CompilerParseData.SLBRACKET) throwParseException();
		else index++;
		
		parseIndexMin();
		
		if(getTokenIDofIndex() != CompilerParseData.SRANGE) throwParseException();
		else index++;
		
		parseIndexMax();
		
		if(!typeCheck.checkIndex()) throwCheckException();
		
		if(getTokenIDofIndex() != CompilerParseData.SRBRACKET) throwParseException();
		else index++;
		
		if(getTokenIDofIndex() != CompilerParseData.SOF) throwParseException();
		else index++;
		
		parseNormalType(false);
		typeCheck.fromNormalTypeToArrayType();
		
		return true;
	}
	
	/**添字の最小値*/
	private void parseIndexMin() throws CompilerException {
		parseInteger();
		if(!typeCheck.setIndexMin()) throwCheckException();
	}
	
	/**添字の最大値*/
	private void parseIndexMax() throws CompilerException {
		parseInteger();
		if(!typeCheck.setIndexMax()) throwCheckException();
	}
	
	/**整数*/
	private void parseInteger() throws CompilerException {
		parseSign(true);
		
		if(getTokenIDofIndex() != CompilerParseData.SCONSTANT) throwParseException();
		else {
			if(!typeCheck.setNumberConstant(getStringofIndex())) throwCheckException();
			index++;
		}
	}
	
	/**符号*/
	private boolean parseSign(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SPLUS) {
			typeCheck.setSign('+');
			index++;
		}
		else if(getTokenIDofIndex() == CompilerParseData.SMINUS) {
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
	private void parseSubProgramDeclArray() throws CompilerException {
		while(parseSubProgramDecl(true)) {
			if(getTokenIDofIndex() != CompilerParseData.SSEMICOLON) throwParseException();
			else index++;
		}
	}
	
	/**副プログラム宣言*/
	private boolean parseSubProgramDecl(boolean isOption) throws CompilerException {
		if(!parseSubProgramHeader(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		parseVarDecl(false);
		
		codeGen.add("NOP", typeCheck.getLastSubLabel());
		if(labelGen.getParamVarSize() > 0) {
			codeGen.add("LAD\tGR4, 1, GR8");
		}
		if(labelGen.getLocalVarSize() > 0) {
			codeGen.add("SUBL\tGR8, =" + labelGen.getLocalVarSize());
			codeGen.add("LAD\tGR5, 0, GR8");
		}
		codeGen.flush();
		
		parseStatementBlock(false);
		
		if(labelGen.getLocalVarSize() > 0) {
			codeGen.add("ADDL\tGR8, =" + labelGen.getLocalVarSize());
		}
		codeGen.add("RET");
		codeGen.flush();
		
		typeCheck.clearLocalIdentifierList();
		
		return true;
	}
	
	/**副プログラム頭部*/
	private boolean parseSubProgramHeader(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SPROCEDURE) {
			if(isOption) return false;
			else throwParseException();
		}
		else index++;
		
		parseRoutineName(false);
		
		parseParam();
		
		if(!typeCheck.addGlobalSubroutineIdentifier()) throwCheckException();
		else typeCheck.clearArgumentTypeList();
		
		if(getTokenIDofIndex() != CompilerParseData.SSEMICOLON) throwParseException();
		else index++;
		
		return true;
	}
	
	/**手続き名*/
	private boolean parseRoutineName(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SIDENTIFIER) {
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
	private void parseParam() throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SLPAREN) {
			index++;
			
			parseParamArray();
			
			if(getTokenIDofIndex() != CompilerParseData.SRPAREN) throwParseException();
			else index++;
		}
	}
	
	/**仮パラメータの並び*/
	private void parseParamArray() throws CompilerException {
		parseParamNameArray();
		
		if(getTokenIDofIndex() != CompilerParseData.SCOLON) throwParseException();
		else index++;
		
		parseNormalType(false);
		
		typeCheck.addArgumentTypes();
		if(!typeCheck.addLocalIdentifiers(true)) throwCheckException();
		else typeCheck.clearStringList();
		
		while(getTokenIDofIndex() == CompilerParseData.SSEMICOLON) {
			index++;
			
			parseParamNameArray();
			
			if(getTokenIDofIndex() != CompilerParseData.SCOLON) throwParseException();
			else index++;
			
			parseNormalType(false);
			
			typeCheck.addArgumentTypes();
			if(!typeCheck.addLocalIdentifiers(true)) throwCheckException();
			else typeCheck.clearStringList();
		}
	}
	
	/**仮パラメータ名の並び*/
	private void parseParamNameArray() throws CompilerException {
		parseParamName();
		
		while(getTokenIDofIndex() == CompilerParseData.SCOMMA) {
			index++;
			
			parseParamName();
		}
	}
	
	/**仮パラメータ名*/
	private void parseParamName() throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SIDENTIFIER) throwParseException();
		else {
			typeCheck.addString(getStringofIndex());
			index++;
		}
	}
	
	/**複合文*/
	private boolean parseStatementBlock(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() != CompilerParseData.SBEGIN) {
			if(isOption) return false;
			else throwParseException();
		}
		else index++;
		
		parseStatementArray();
		
		if(getTokenIDofIndex() != CompilerParseData.SEND) throwParseException();
		else index++;
		
		return true;
	}
	
	/**文の並び*/
	private void parseStatementArray() throws CompilerException {
		parseStatement();
		
		while(getTokenIDofIndex() == CompilerParseData.SSEMICOLON) {
			index++;
			
			parseStatement();
		}
	}
	
	/**文*/
	private void parseStatement() throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SIF) {
			index++;
			
			typeCheck.pushType(CompilerTypeData.TYPE_BOOLEAN);
			parseExpression();
			if(!typeCheck.checkType(true)) throwCheckException();
			
			codeGen.add("POP\tGR3");
			codeGen.add("CPL\tGR3, =#0001");
			codeGen.flush();
			
			if(getTokenIDofIndex() != CompilerParseData.STHEN) throwParseException();
			else index++;
			
			int label1 = labelGen.genNormalLabel();
			codeGen.add("JNZ\tL" + label1);
			codeGen.flush();
			
			parseStatementBlock(false);
			
			if(getTokenIDofIndex() == CompilerParseData.SELSE) {
				index++;
				
				int label2 = labelGen.genNormalLabel();
				codeGen.add("JUMP\tL" + label2);
				codeGen.add("NOP", "L" + label1);
				codeGen.flush();
				
				parseStatementBlock(false);
				
				codeGen.add("NOP", "L" + label2);
				codeGen.flush();
			}
			else {
				codeGen.add("NOP", "L" + label1);
				codeGen.flush();
			}
		}
		else if(getTokenIDofIndex() == CompilerParseData.SWHILE) {
			index++;
			
			int label1 = labelGen.genNormalLabel();
			codeGen.add("NOP", "L" + label1);
			codeGen.flush();
			
			typeCheck.pushType(CompilerTypeData.TYPE_BOOLEAN);
			parseExpression();
			if(!typeCheck.checkType(true)) throwCheckException();
			
			codeGen.add("POP\tGR3");
			codeGen.add("CPL\tGR3, =#0001");
			codeGen.flush();
			
			if(getTokenIDofIndex() != CompilerParseData.SDO) throwParseException();
			else index++;
			
			int label2 = labelGen.genNormalLabel();
			codeGen.add("JNZ\tL" + label2);
			codeGen.flush();
			
			parseStatement();
			
			codeGen.add("JUMP\tL" + label1);
			codeGen.add("NOP", "L" + label2);
			codeGen.flush();
		}
		else if(parseNormalStatement(true)) return;
		else throwParseException();
	}
	
	/**基本文*/
	private boolean parseNormalStatement(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SIDENTIFIER) {
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
	private boolean parseAssignStatement(boolean isOption) throws CompilerException {
		if(!parseLeftValue(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		if(getTokenIDofIndex() != CompilerParseData.SASSIGN) throwParseException();
		else index++;
		
		if(!CompilerTypeData.isNormalType(typeCheck.peekType())) throwCheckException();
		
		parseExpression();
		if(!typeCheck.checkType(true)) throwCheckException();
		
		codeGen.add("POP\tGR2");
		codeGen.add("POP\tGR1");
		codeGen.add("ST\tGR2, 0, GR1");
		codeGen.flush();
		
		return true;
	}
	
	/**左辺*/
	private boolean parseLeftValue(boolean isOption) throws CompilerException {
		if(!parseVariable(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**変数*/
	private boolean parseVariable(boolean isOption) throws CompilerException {
		if(parseIndexedVariable(true)) ;
		else if(parseNormalVariable(true)) ;
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**純変数*/
	private boolean parseNormalVariable(boolean isOption) throws CompilerException {
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
		
		int label;
		if(!((label = typeCheck.getLastParamLabel()) < 0)){
			codeGen.add("LAD\tGR1, " + label + ", GR4");
		}
		else if(!((label = typeCheck.getLastLocalLabel()) < 0)){
			codeGen.add("LAD\tGR1, " + label + ", GR5");
		}
		else if(!((label = typeCheck.getLastGlobalLabel()) < 0)){
			codeGen.add("LAD\tGR1, VAR" + label);
		}
		else { //TODO: error-check
			codeGen.addComment("ERROR: typeCheck error!");
		}
		if(CompilerTypeData.isArrayType(typeCheck.peekType())) {
			codeGen.add("LD\tGR0, =" + typeCheck.getLastArraySize());
		}
		codeGen.add("PUSH\t0, GR1");
		codeGen.flush();
		
		return true;
	}
	
	/**添字付き変数*/
	private boolean parseIndexedVariable(boolean isOption) throws CompilerException {
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
		else if(!CompilerTypeData.isArrayType(typeCheck.peekType())) {
			index = oldIndex;
			if(isOption) {
				typeCheck.popType();
				return false;
			}
			else throwCheckException();
		}
		
		if(getTokenIDofIndex() != CompilerParseData.SLBRACKET) {
			if(isOption) {
				index = oldIndex;
				typeCheck.popType();
				return false;
			}
			else throwParseException();
		}
		else index++;
		
		int label;
		if(!((label = typeCheck.getLastLocalLabel()) < 0)){
			codeGen.add("LAD\tGR1, " + label + ", GR5");
		}
		else if(!((label = typeCheck.getLastGlobalLabel()) < 0)){
			codeGen.add("LAD\tGR1, VAR" + label);
		}
		else { //TODO: error-check
			codeGen.addComment("ERROR: typeCheck error!");
		}
		codeGen.add("SUBL\tGR1, =" + typeCheck.getLastIndexMin());
		codeGen.add("PUSH\t0, GR1");
		codeGen.flush();
		
		parseIndex();
		
		if(getTokenIDofIndex() != CompilerParseData.SRBRACKET) throwParseException();
		else index++;
		
		if(!typeCheck.fromArrayTypeToNormalType()) throwCheckException();
		
		codeGen.add("POP\tGR2");
		codeGen.add("POP\tGR1");
		codeGen.add("ADDL\tGR1, GR2");
		codeGen.add("PUSH\t0, GR1");
		codeGen.flush();
		
		return true;
	}
	
	/**添字*/
	private void parseIndex() throws CompilerException {
		typeCheck.pushType(CompilerTypeData.TYPE_INTEGER);
		parseExpression();
		if(!typeCheck.checkType(true)) throwCheckException();
	}
	
	/**手続き呼出し文*/
	private boolean parseRoutineCallStatement(boolean isOption) throws CompilerException {
		int oldIndex = index;
		
		if(!parseRoutineName(true)) {
			if(isOption) return false;
			else throwParseException();
		}
		
		if(!typeCheck.pushSubroutineArgumentsType()) {
			index = oldIndex;
			throwCheckException();
		}
		
		String subLabel = typeCheck.getLastSubLabel();
		int subParamSize = typeCheck.getLastSubParameterSize();
		
		codeGen.add("PUSH\t0, GR4");
		codeGen.add("PUSH\t0, GR5");
		codeGen.flush();
		
		if(getTokenIDofIndex() == CompilerParseData.SLPAREN) {
			index++;
			
			parseExpressionArray(false);
			
			if(getTokenIDofIndex() != CompilerParseData.SRPAREN) throwParseException();
			else index++;
		}
		
		codeGen.add("CALL\t" + subLabel);
		if(subParamSize > 0) {
			codeGen.add("ADDL\tGR8, =" + subParamSize);
		}
		codeGen.add("POP\tGR5");
		codeGen.add("POP\tGR4");
		codeGen.flush();
		
		if(typeCheck.popType() != CompilerTypeData.TYPE_SUBROUTINE) throwCheckException();
		
		return true;
	}
	
	/**式の並び*/
	private void parseExpressionArray(boolean isIORoutine) throws CompilerException {
		while(true) {
			parseExpression();
			if(!isIORoutine) {
				if(!typeCheck.checkType(true)) throwCheckException();
			}
			else{
				codeGen.add("POP\tGR2");
				switch(typeCheck.peekType()) {
				case CompilerTypeData.TYPE_INTEGER:
					codeGen.add("CALL\tWRTINT");
					break;
				case CompilerTypeData.TYPE_CHAR:
					codeGen.add("CALL\tWRTCH");
					break;
				case CompilerTypeData.TYPE_ARRAY_CHAR:
					codeGen.add("LD\tGR1, GR0");
					codeGen.add("CALL\tWRTSTR");
					break;
				default:
					throwCheckException();
				}
				codeGen.flush();

				typeCheck.popType();
			}

			if(getTokenIDofIndex() != CompilerParseData.SCOMMA) break;
			index++;
		}
	}
	
	/**式*/
	private void parseExpression() throws CompilerException {
		parseSimpleExpression();
		
		int op;
		if(!((op = parseRelativeOp(true)) < 0)) {
			parseSimpleExpression();
			if(!CompilerTypeData.isNormalType(typeCheck.peekType())) throwCheckException();
			else if(!typeCheck.checkType(true)) throwCheckException();
			else typeCheck.pushType(CompilerTypeData.TYPE_BOOLEAN);

			codeGen.add("POP\tGR2");
			codeGen.add("POP\tGR1");
			codeGen.add("CPA\tGR1, GR2");
			int label1 = labelGen.genNormalLabel();
			int label2 = labelGen.genNormalLabel();
			boolean reverse = false;
			switch(op) {
			case CompilerParseData.SEQUAL:
				codeGen.add("JZE\tL" + label1);
				break;
			case CompilerParseData.SNOTEQUAL:
				codeGen.add("JNZ\tL" + label1);
				break;
			case CompilerParseData.SLESS:
				codeGen.add("JMI\tL" + label1);
				break;
			case CompilerParseData.SLESSEQUAL:
				codeGen.add("JPL\tL" + label1);
				reverse = true;
				break;
			case CompilerParseData.SGREAT:
				codeGen.add("JPL\tL" + label1);
				break;
			case CompilerParseData.SGREATEQUAL:
				codeGen.add("JMI\tL" + label1);
				reverse = true;
				break;
			default: //TODO: error-check
				codeGen.addComment("UNREACHABLE: check your code!");
			}
			codeGen.add("LD\tGR3, =" + (reverse ? "#0001" : "#0000"));
			codeGen.add("JUMP\tL" + label2);
			codeGen.add("LD\tGR3, =" + (reverse ? "#0000" : "#0001"), "L" + label1);
			codeGen.add("PUSH\t0, GR3", "L" + label2);
			codeGen.flush();
		}
	}
	
	/**単純式*/
	private void parseSimpleExpression() throws CompilerException {
		parseSign(true);
		
		parseTerm();
		char sign = typeCheck.getSign();
		if(sign != '\0') {
			if(typeCheck.peekType() != CompilerTypeData.TYPE_INTEGER) throwCheckException();
			
			if(sign == '-') {
				codeGen.add("POP\tGR2");
				codeGen.add("LD\tGR1, =0");
				codeGen.add("SUBA\tGR1, GR2");
				codeGen.add("PUSH\t0, GR1");
				codeGen.flush();
			}
		}
		
		int op;
		while(!((op = parseAdditiveOp(true)) < 0)) {
			parseTerm();
			
			if(!typeCheck.checkType(false)) throwCheckException();
			
			codeGen.add("POP\tGR2");
			codeGen.add("POP\tGR1");
			switch(op) {
			case CompilerParseData.SPLUS:
				codeGen.add("ADDA\tGR1, GR2");
				break;
			case CompilerParseData.SMINUS:
				codeGen.add("SUBA\tGR1, GR2");
				break;
			case CompilerParseData.SOR:
				codeGen.add("OR\tGR1, GR2");
				break;
			default: //TODO: error-check
				codeGen.addComment("UNREACHABLE: check your code!");
			}
			codeGen.add("PUSH\t0, GR1");
			codeGen.flush();
		}
	}
	
	/**項*/
	private void parseTerm() throws CompilerException {
		parseFactor();
		
		int op;
		while(!((op = parseMultiplicativeOp(true)) < 0)) {
			parseFactor();
			
			if(!typeCheck.checkType(false)) throwCheckException();
			
			codeGen.add("POP\tGR2");
			codeGen.add("POP\tGR1");
			boolean notGR1 = false;
			switch(op) {
			case CompilerParseData.SSTAR:
				codeGen.add("CALL\tMULT");
				notGR1 = true;
				break;
			case CompilerParseData.SDIVD:
				codeGen.add("CALL\tDIV");
				notGR1 = true;
				break;
			case CompilerParseData.SMOD:
				codeGen.add("CALL\tDIV");
				break;
			case CompilerParseData.SAND:
				codeGen.add("AND\tGR1, GR2");
				break;
			default: //TODO: error-check
				codeGen.addComment("UNREACHABLE: check your code!");
			}
			if(notGR1)
				codeGen.add("PUSH\t0, GR2");
			else
				codeGen.add("PUSH\t0, GR1");
			codeGen.flush();
		}
	}
	
	/**因子*/
	private void parseFactor() throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SIDENTIFIER) {
			parseVariable(false);
			
			if(CompilerTypeData.isNormalType(typeCheck.peekType())) {
				codeGen.add("POP\tGR1");
				codeGen.add("LD\tGR1, 0, GR1");
				codeGen.add("PUSH\t0, GR1");
				codeGen.flush();
			}
		}
		else if(parseConstant(true)) return;
		else if(getTokenIDofIndex() == CompilerParseData.SLPAREN) {
			index++;
			
			parseExpression();
			
			if(getTokenIDofIndex() != CompilerParseData.SRPAREN) throwParseException();
			else index++;
		}
		else if(getTokenIDofIndex() == CompilerParseData.SNOT) {
			index++;
			
			parseFactor();
			if(typeCheck.peekType() != CompilerTypeData.TYPE_BOOLEAN) throwCheckException();
			
			codeGen.add("POP\tGR1");
			codeGen.add("XOR\tGR1, =#0001");
			codeGen.add("PUSH\t0, GR1");
			codeGen.flush();
		}
		else throwParseException();
	}
	
	/**関係演算子*/
	private int parseRelativeOp(boolean isOption) throws CompilerException {
		op = getTokenIDofIndex();
		
		switch(op) {
		case CompilerParseData.SEQUAL:
		case CompilerParseData.SNOTEQUAL:
		case CompilerParseData.SLESS:
		case CompilerParseData.SLESSEQUAL:
		case CompilerParseData.SGREAT:
		case CompilerParseData.SGREATEQUAL:
			index++;
			break;
		default:
			if(isOption) return -1;
			else throwParseException();
		}
		
		return op;
	}
	
	/**加法演算子*/
	private int parseAdditiveOp(boolean isOption) throws CompilerException {
		op = getTokenIDofIndex();
		
		switch(op) {
		case CompilerParseData.SPLUS:
		case CompilerParseData.SMINUS:
			if(typeCheck.peekType() != CompilerTypeData.TYPE_INTEGER) throwCheckException();
			break;
		case CompilerParseData.SOR:
			if(typeCheck.peekType() != CompilerTypeData.TYPE_BOOLEAN) throwCheckException();
			break;
		default:
			if(isOption) return -1;
			else throwParseException();
		}
		index++;
		
		return op;
	}
	
	/**乗法演算子*/
	private int parseMultiplicativeOp(boolean isOption) throws CompilerException {
		op = getTokenIDofIndex();
		
		switch(op) {
		case CompilerParseData.SSTAR:
		case CompilerParseData.SDIVD:
		case CompilerParseData.SMOD:
			if(typeCheck.peekType() != CompilerTypeData.TYPE_INTEGER) throwCheckException();
			break;
		case CompilerParseData.SAND:
			if(typeCheck.peekType() != CompilerTypeData.TYPE_BOOLEAN) throwCheckException();
			break;
		default:
			if(isOption) return -1;
			else throwParseException();
		}
		index++;
		
		return op;
	}
	
	/**入出力文*/
	private boolean parseIOStatement(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SREADLN) {
			index++;
			
			if(getTokenIDofIndex() == CompilerParseData.SLPAREN) {
				index++;
				
				parseVarArray();
				
				if(getTokenIDofIndex() != CompilerParseData.SRPAREN) throwParseException();
				else index++;
			}
			else {
				codeGen.add("CALL\tRDLN");
				codeGen.flush();
			}
		}
		else if(getTokenIDofIndex() == CompilerParseData.SWRITELN) {
			index++;
			
			if(getTokenIDofIndex() == CompilerParseData.SLPAREN) {
				index++;
				
				parseExpressionArray(true);
				
				if(getTokenIDofIndex() != CompilerParseData.SRPAREN) throwParseException();
				else index++;
			}
			
			codeGen.add("CALL\tWRTLN");
			codeGen.flush();
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
	
	/**変数の並び*/
	private void parseVarArray() throws CompilerException {
		while(true) {
			parseVariable(false);
			
			codeGen.add("POP\tGR2");
			switch(typeCheck.peekType()) {
			case CompilerTypeData.TYPE_INTEGER:
				codeGen.add("CALL\tRDINT");
				break;
			case CompilerTypeData.TYPE_CHAR:
				codeGen.add("CALL\tRDCH");
				break;
			case CompilerTypeData.TYPE_ARRAY_CHAR:
				codeGen.add("LD\tGR1, GR0");
				codeGen.add("CALL\tRDSTR");
				break;
			default:
				throwCheckException();
			}
			codeGen.flush();

			typeCheck.popType();
			
			if(getTokenIDofIndex() != CompilerParseData.SCOMMA) break;
			index++;
		}
	}
	
	/**定数*/
	private boolean parseConstant(boolean isOption) throws CompilerException {
		if(getTokenIDofIndex() == CompilerParseData.SCONSTANT) {
			typeCheck.pushType(CompilerTypeData.TYPE_INTEGER);
			if(!typeCheck.setNumberConstant(getStringofIndex())) throwCheckException();
			
			codeGen.add("PUSH\t" + getStringofIndex());
			codeGen.flush();
			
			index++;
		}
		else if(getTokenIDofIndex() == CompilerParseData.SSTRING) {
			if(getStringofIndex().length() == 3) {
				typeCheck.pushType(CompilerTypeData.TYPE_CHAR);
				
				codeGen.add("PUSH\t" + (int)getStringofIndex().charAt(1));
				codeGen.flush();
			}
			else {
				typeCheck.pushType(CompilerTypeData.TYPE_ARRAY_CHAR);
				
				int label = labelGen.getStrDataLabel(getStringofIndex());
				codeGen.add("PUSH\tSTR" + label);
				codeGen.add("LD\tGR0, =" + (getStringofIndex().length() - 2));
				codeGen.flush();
			}
			index++;
		}
		else if(getTokenIDofIndex() == CompilerParseData.SFALSE) {
			typeCheck.pushType(CompilerTypeData.TYPE_BOOLEAN);
			
			codeGen.add("PUSH\t#0000");
			codeGen.flush();
			
			index++;
		}
		else if(getTokenIDofIndex() == CompilerParseData.STRUE) {
			typeCheck.pushType(CompilerTypeData.TYPE_BOOLEAN);
			
			codeGen.add("PUSH\t#0001");
			codeGen.flush();
			
			index++;
		}
		else {
			if(isOption) return false;
			else throwParseException();
		}
		
		return true;
	}
}
