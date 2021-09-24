package enshud.s4.compiler;

final class CompilerException extends Exception {
	/*nouse*/
	static final long serialVersionUID = 0x01L; 
	
	/**Constructor of CompilerException*/
	public CompilerException(int lineNum, boolean isSyntaxError) {
		super((isSyntaxError ? "Syntax error: line " : "Semantic error: line ") + lineNum);
	}
}
