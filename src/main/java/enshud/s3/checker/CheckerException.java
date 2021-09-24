package enshud.s3.checker;

final class CheckerException extends Exception {
	/**nouse*/
	static final long serialVersionUID = 0x01L; 
	
	/**Constructor of CheckerException*/
	public CheckerException(int lineNum, boolean isSyntaxError) {
		super((isSyntaxError ? "Syntax error: line " : "Semantic error: line ") + lineNum);
	}
}
