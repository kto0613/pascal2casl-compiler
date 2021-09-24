package enshud.s2.parser;

final class ParserException extends Exception {
	/**nouse*/
	static final long serialVersionUID = 0x01L; 
	
	/**Constructor of ParserException*/
	public ParserException(int lineNum) {
		super("Syntax error: line " + lineNum);
		//super((isSyntaxError ? "Syntax error: line " : "Semantic error: line ") + lineNum);
	}
}
