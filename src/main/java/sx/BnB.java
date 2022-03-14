package sx;


/**
 * BnB (Bits and Bytes) is a class that provides static methods to work with integers as Bits and Bytes.
 *
 * @author benoitpointet
 */
final class BnB {

	/**
	 * @param b a byte value (0..255)
	 * @param n
	 * @return bit at position n of byte b.
	 */
	public static boolean getBit(byte b, byte n) {
		return ((b >> n) & 0b1) != 0;
	}

	/**
	 * @param b a byte value (0..255)
	 * @param n a bit position (0..7)
	 * @param v a bit value (0,1)
	 * @return the result of setting bit n of byte b to value v
	 */
	public static byte setBit(byte b, byte n, boolean v) {
		byte newB = b;
		if (v) {
			newB |= 0b1 << n;
		} else {
			newB &= ~(0b1 << n);
		}
		return newB;
	}

	/**
	 * @Deprecated
	 */
	static int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * @Deprecated
	 */
	public static boolean isBit(int b) {
		return (b == 0 || b == 1);
	}

	/**
	 * @Deprecated
	 */
	public static boolean isByte(int b) {
		return isWordOfLength(b, 8);
	}

	public static boolean isWordOfLength(int w, int l) {
		return (w >= 0 && w < Math.pow(2, l));
	}
}
