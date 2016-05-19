/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package javaemul.internal;

/**
 * Wraps a primitive <code>int</code> as an object.
 */
public final class IntegerHelper extends NumberHelper implements Comparable<IntegerHelper> {

	private static final long serialVersionUID = 1L;
	public static final int MAX_VALUE = 0x7fffffff;
	public static final int MIN_VALUE = 0x80000000;
	public static final int SIZE = 32;

	/**
	 * Use nested class to avoid clinit on outer.
	 */
	private static class BoxedValues {
		// Box values according to JLS - between -128 and 127
		private static IntegerHelper[] boxedValues = new IntegerHelper[256];
	}

	/**
	 * Use nested class to avoid clinit on outer.
	 */
	private static class ReverseNibbles {
		/**
		 * A fast-lookup of the reversed bits of all the nibbles 0-15. Used to
		 * implement {@link #reverse(int)}.
		 */
		private static int[] reverseNibbles = { 0x0, 0x8, 0x4, 0xc, 0x2, 0xa, 0x6, 0xe, 0x1, 0x9, 0x5, 0xd, 0x3, 0xb,
				0x7, 0xf };
	}

	public static int bitCount(int x) {
		// Courtesy the University of Kentucky
		// http://aggregate.org/MAGIC/#Population%20Count%20(Ones%20Count)
		x -= ((x >> 1) & 0x55555555);
		x = (((x >> 2) & 0x33333333) + (x & 0x33333333));
		x = (((x >> 4) + x) & 0x0f0f0f0f);
		x += (x >> 8);
		x += (x >> 16);
		return x & 0x0000003f;
	}

	public static int compare(int x, int y) {
		if (x < y) {
			return -1;
		} else if (x > y) {
			return 1;
		} else {
			return 0;
		}
	}

	public static IntegerHelper decode(String s) throws NumberFormatException {
		return IntegerHelper.valueOf(__decodeAndValidateInt(s, MIN_VALUE, MAX_VALUE));
	}

	/**
	 * @skip
	 * 
	 * 		Here for shared implementation with Arrays.hashCode
	 */
	public static int hashCode(int i) {
		return i;
	}

	public static int highestOneBit(int i) {
		if (i < 0) {
			return MIN_VALUE;
		} else if (i == 0) {
			return 0;
		} else {
			int rtn;
			for (rtn = 0x40000000; (rtn & i) == 0; rtn >>= 1) {
				// loop down until matched
			}
			return rtn;
		}
	}

	public static int lowestOneBit(int i) {
		return i & -i;
	}

	public static int numberOfLeadingZeros(int i) {
		// Based on Henry S. Warren, Jr: "Hacker's Delight", p. 80.
		if (i < 0) {
			return 0;
		} else if (i == 0) {
			return SIZE;
		} else {
			int y, m, n;

			y = -(i >> 16);
			m = (y >> 16) & 16;
			n = 16 - m;
			i = i >> m;

			y = i - 0x100;
			m = (y >> 16) & 8;
			n += m;
			i <<= m;

			y = i - 0x1000;
			m = (y >> 16) & 4;
			n += m;
			i <<= m;

			y = i - 0x4000;
			m = (y >> 16) & 2;
			n += m;
			i <<= m;

			y = i >> 14;
			m = y & ~(y >> 1);
			return n + 2 - m;
		}
	}

	public static int numberOfTrailingZeros(int i) {
		if (i == 0) {
			return SIZE;
		} else {
			int rtn = 0;
			for (int r = 1; (r & i) == 0; r <<= 1) {
				rtn++;
			}
			return rtn;
		}
	}

	public static int parseInt(String s) throws NumberFormatException {
		return parseInt(s, 10);
	}

	public static int parseInt(String s, int radix) throws NumberFormatException {
		return __parseAndValidateInt(s, radix, MIN_VALUE, MAX_VALUE);
	}

	public static int reverse(int i) {
		int[] nibbles = ReverseNibbles.reverseNibbles;
		return (nibbles[i >>> 28]) | (nibbles[(i >> 24) & 0xf] << 4) | (nibbles[(i >> 20) & 0xf] << 8)
				| (nibbles[(i >> 16) & 0xf] << 12) | (nibbles[(i >> 12) & 0xf] << 16) | (nibbles[(i >> 8) & 0xf] << 20)
				| (nibbles[(i >> 4) & 0xf] << 24) | (nibbles[i & 0xf] << 28);
	}

	public static int reverseBytes(int i) {
		return ((i & 0xff) << 24) | ((i & 0xff00) << 8) | ((i & 0xff0000) >> 8) | ((i & 0xff000000) >>> 24);
	}

	public static int rotateLeft(int i, int distance) {
		while (distance-- > 0) {
			i = i << 1 | ((i < 0) ? 1 : 0);
		}
		return i;
	}

	public static int rotateRight(int i, int distance) {
		int ui = i & MAX_VALUE; // avoid sign extension
		int carry = (i < 0) ? 0x40000000 : 0; // MIN_VALUE rightshifted 1
		while (distance-- > 0) {
			int nextcarry = ui & 1;
			ui = carry | (ui >> 1);
			carry = (nextcarry == 0) ? 0 : 0x40000000;
		}
		if (carry != 0) {
			ui = ui | MIN_VALUE;
		}
		return ui;
	}

	public static int signum(int i) {
		if (i == 0) {
			return 0;
		} else if (i < 0) {
			return -1;
		} else {
			return 1;
		}
	}

	public static String toBinaryString(int value) {
		return toUnsignedRadixString(value, 2);
	}

	public static String toHexString(int value) {
		return toUnsignedRadixString(value, 16);
	}

	public static String toOctalString(int value) {
		return toUnsignedRadixString(value, 8);
	}

	public static String toString(int value) {
		return String.valueOf(value);
	}

	public static String toString(int value, int radix) {
		if (radix == 10 || radix < CharacterHelper.MIN_RADIX || radix > CharacterHelper.MAX_RADIX) {
			return String.valueOf(value);
		}
		return toRadixString(value, radix);
	}

	public static IntegerHelper valueOf(int i) {
		if (i > -129 && i < 128) {
			int rebase = i + 128;
			IntegerHelper result = BoxedValues.boxedValues[rebase];
			if (result == null) {
				result = BoxedValues.boxedValues[rebase] = new IntegerHelper(i);
			}
			return result;
		}
		return new IntegerHelper(i);
	}

	public static IntegerHelper valueOf(String s) throws NumberFormatException {
		return valueOf(s, 10);
	}

	public static IntegerHelper valueOf(String s, int radix) throws NumberFormatException {
		return IntegerHelper.valueOf(IntegerHelper.parseInt(s, radix));
	}

	private static native String toRadixString(int value, int radix) /*-{
																		return value.toString(radix);
																		}-*/;

	private static native String toUnsignedRadixString(int value, int radix) /*-{
																				// ">>> 0" converts the value to unsigned number.
																				return (value >>> 0).toString(radix);
																				}-*/;

	private final transient int value;

	public IntegerHelper(int value) {
		this.value = value;
	}

	public IntegerHelper(String s) {
		value = parseInt(s);
	}

	@Override
	public byte byteValue() {
		return (byte) value;
	}

	@Override
	public int compareTo(IntegerHelper b) {
		return compare(value, b.value);
	}

	@Override
	public double doubleValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof IntegerHelper) && (((IntegerHelper) o).value == value);
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return hashCode(value);
	}

	@Override
	public int intValue() {
		return value;
	}

	@Override
	public long longValue() {
		return value;
	}

	@Override
	public short shortValue() {
		return (short) value;
	}

	@Override
	public String toString() {
		return toString(value);
	}

}
