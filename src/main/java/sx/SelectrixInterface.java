package sx;

/**
 * @author benoitpointet
 */
public interface SelectrixInterface {

	byte NB_ADDRESS = 104;

	boolean hasConfiguredPort();

	boolean hasInitializedPort();

	boolean hasValidConnection();

	boolean isEmulating();

	void configPort(String portName, int baudRate, int dataBits, int stopBits, int parity);

	void initPort();

	void reinitPort();

	void closePort();

	byte getStatusByte(byte addressByte);

	void setStatusByte(byte address, byte status);

	boolean getStatusBit(byte address, byte position);

	void setStatusBit(byte address, byte position, boolean status);

	void writeLock(byte address);

	void writeUnlock(byte address);

	@Override
	String toString();
}
