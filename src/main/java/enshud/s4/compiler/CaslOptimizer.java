package enshud.s4.compiler;

import java.io.*;
import java.util.*;

final class CaslOptimizer {
	
	private List<String> data;
	
	private String casFileIn;
	private String casFileOut;
	
	/*static optimizer methods*/
	public static void optimize(String fileIO) {
		optimize(fileIO, fileIO);
	}
	
	public static void optimize(String fileI, String fileO) {
		CaslOptimizer co = new CaslOptimizer(fileI, fileO);
		co.read();
		int count;
		do {
			count = 0;
			count += co.optimize0();
			count += co.optimize1();
			count += co.optimize2();
			//TODO: DEBUG
			//System.out.println(count);
		}
		while(count != 0);
		co.write();
		return;
	}
	
	/*static helper methods*/
	private static String removeComment(String line) {
		if(line == null) return null;
		String[] split = line.split("[ \t]*;", 2);
		if(split.length >= 1) return split[0];
		else return line;
	}
	
	private static String getLabel(String line) {
		if(line == null) return null;
		String code = removeComment(line);
		String[] split = code.split("[ \t]+", 3);
		if(split.length >= 1 && !split[0].equals("")) return split[0];
		else return null;
	}
	
	private static boolean hasLabel(String line) {
		if(line == null) return false;
		return (getLabel(line) != null);
	}
	
	private static String getInst(String line) {
		if(line == null) return null;
		String code = removeComment(line);
		String[] split = code.split("[ \t]+", 3);
		if(split.length >= 2 && !split[1].equals("")) return split[1];
		else return null;
	}
	
	private static String getOp(String line) {
		if(line == null) return null;
		String code = removeComment(line);
		String[] split = code.split("[ \t]+", 3);
		if(split.length == 3 && !split[2].equals("")) return split[2];
		else return null;
	}
	
	private static String splitOp(String op, int n) {
		if(n >= 1 && n <= 3 && op != null) {
			String[] split = op.replaceAll("'([^']|'')*'", "STRING").split("[ \t]*,[ \t]*", 3);
			if(split.length >= n) return split[n-1];
		}
		return null;
	}
	
	private static boolean isRegister(String splOp) {
		if(splOp == null) return false;
		return splOp.matches("(G|g)(R|r)[0-8]");
	}
	
	private static boolean isRegisterUse(String op, String register) {
		if(op == null || register == null) return false;
		for(int i = 0; i < 3; i++) {
			String splOp = splitOp(op, i+1);
			if(splOp != null && splOp.equalsIgnoreCase(register))
				return true;
		}
		return false;
	}
	
	private static boolean isBlockDivider(String line) {
		if(line == null) return false;
		String inst = getInst(line);
		return (hasLabel(line) || (inst != null
				&& (inst.matches("(J|j).*")
						|| inst.matches("(R|r)(P|p).*")
						|| inst.equalsIgnoreCase("CALL")
						|| inst.equalsIgnoreCase("RET")
						|| inst.equalsIgnoreCase("SVC")
						)));
	}
	
	/*constructor*/
	private CaslOptimizer(String fileI, String fileO) {
		data = new LinkedList<String>();
		casFileIn = fileI;
		casFileOut = fileO;
	}
	
	/*IO methods*/
	private void read() {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(casFileIn));
			String line;
			while((line = in.readLine()) != null) {
				data.add(line);
				//TODO: DEBUG
				/*
				System.out.println("###############");
				System.out.println(line);
				System.out.println("LABEL #" + getLabel(line));
				System.out.println("INST  #" + getInst(line));
				System.out.println("OP    #" + getOp(line));
				System.out.println("OP1   #" + splitOp(getOp(line), 1));
				System.out.println("OP2   #" + splitOp(getOp(line), 2));
				System.out.println("OP3   #" + splitOp(getOp(line), 3));
				*/
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void write() {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(casFileOut));
			for(String line : data) {
				out.write(line);
				out.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*optimization implemented methods*/
	
	private int optimize0() {
		int count = 0;
		for(int index = 0; index < data.size();) {
			String line = data.get(index);
			if(getInst(line) != null && getInst(line).equalsIgnoreCase("NOP")) {
				if(hasLabel(line)) {
					String next = data.get(index + 1);
					if(getInst(next) != null && !hasLabel(next)) {
						data.remove(index);
						data.remove(index);
						data.add(index, getLabel(line) + next);
						count++;
					}
					else index++;
				}
				else {
					data.remove(index);
					count++;
				}
			}
			else index++;
		}
		return count;
	}
	
	private int optimize1() {
		int count = 0;
		for(int index = 0; index < data.size();) {
			String line = data.get(index);
			if(getInst(line) != null && getInst(line).equalsIgnoreCase("PUSH")) {
				String next = null;
				int index2 = index + 1;
				int height = 1;
				while(index2 < data.size() && !isBlockDivider(data.get(index2))) {
					String inst = getInst(data.get(index2));
					if(inst != null) {
						if(inst.equalsIgnoreCase("PUSH")) height++;
						else if(inst.equalsIgnoreCase("POP")) height--;
					}
					if(height == 0) {
						next = data.get(index2);
						break;
					}
					index2++;
				}
				if(next == null) {
					index++;
					continue;
				}
				String pushOp = getOp(line);
				String popOp = getOp(next);
				if(splitOp(pushOp, 2) == null) {
					String insert = "\tLAD\t" + popOp + ", " + pushOp;
					data.remove(index2);
					if(hasLabel(next)) data.add(index2, getLabel(next) + insert);
					else data.add(index2, insert);
					data.remove(index);
					if(hasLabel(line)) {
						data.add(index, getLabel(line) + "\tNOP");
						index++;
					}
					count++;
					continue;
				}
				String pushGR = splitOp(pushOp, 2);
				String popGR = splitOp(popOp, 1);
				if(isRegister(pushGR) && isRegister(popGR)) {
					boolean pushGRuse = false;
					boolean popGRuse = false;
					for(int i = index + 1; i < index2; i++) {
						String codeOp = getOp(data.get(i));
						if(isRegisterUse(codeOp, pushGR)) pushGRuse = true;
						if(isRegisterUse(codeOp, popGR)) popGRuse = true;
					}
					if(!popGRuse) {
						String insert = "\tLAD\t" + popOp + ", " + pushOp;
						data.remove(index2);
						if(hasLabel(next)) data.add(index2, getLabel(next) + "\tNOP");
						data.remove(index);
						if(hasLabel(line)) data.add(index, getLabel(line) + insert);
						else data.add(index, insert);
						index++;
						count++;
						continue;
					}
					else if(!pushGRuse) {
						String insert = "\tLAD\t" + popOp + ", " + pushOp;
						data.remove(index2);
						if(hasLabel(next)) data.add(index2, getLabel(next) + insert);
						else data.add(index2, insert);
						data.remove(index);
						if(hasLabel(line)) {
							data.add(index, getLabel(line) + "\tNOP");
							index++;
						}
						count++;
						continue;
					}
				}
				index++;
			}
			else index++;
		}
		return count;
	}
	
	private int optimize2() {
		int count = 0;
		for(int index = 0; index < data.size();) {
			String line = data.get(index);
			if(getInst(line) != null && getInst(line).equalsIgnoreCase("LAD")) {
				String op = getOp(line);
				String opGR1 = splitOp(op, 1);
				String opGR2 = splitOp(op, 3);
				String opData = splitOp(op, 2);
				if(isRegister(opGR1) && isRegister(opGR2) && opGR1.equalsIgnoreCase(opGR2)
						&& (opData.equals("0") || opData.equals("#0000"))) {
					data.remove(index);
					if(hasLabel(line)) {
						data.add(index, getLabel(line) + "\tNOP");
						index++;
					}
					count++;
				}
				else index++;
			}
			else index++;
		}
		return count;
	}
}
