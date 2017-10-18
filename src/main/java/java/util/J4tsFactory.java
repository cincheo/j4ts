package java.util;

import static def.js.Globals.eval;

public class J4tsFactory {

	
	public static void main(String[] args) {
		 eval("var g; if (typeof window === 'undefined') { g = global;}else{ g = window;}   g['java']=java ;");
	};

	
}
