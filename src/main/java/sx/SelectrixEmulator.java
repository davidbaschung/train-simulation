package sx;

import apps.Main;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * @author benoitpointet
 */
class SelectrixEmulator implements SelectrixInterface {

	private static final Logger logger = Main.getSXLogger("Emulator");
	private static SelectrixInterface instance;
	private final byte[] state = new byte[NB_ADDRESS];
	private final AtomicBoolean[] locks = new AtomicBoolean[NB_ADDRESS];
	/* flag signifying that the parameters have been set */
	private boolean hasConfiguredPort = false;
	/* flag signifying that the port has been initialized, if not then it isA closed */
	private boolean hasInitializedPort = false;
	/* flag signifying that the last connection was valid */
	private boolean hasValidConnection = false;
	private int readCounter = 0;
	private int writeCounter = 0;
	private int initCounter = 0;

	@Override
	public boolean hasConfiguredPort() {
		return this.hasConfiguredPort;
	}

	@Override
	public boolean hasInitializedPort() {
		return this.hasInitializedPort;
	}

	@Override
	public boolean hasValidConnection() {
		return this.hasValidConnection;
	}

	@Override
	public boolean isEmulating() {
		return true;
	}

	@Override
	public void configPort(String portName, int baudRate, int dataBits,
	                       int stopBits, int parity) {

		if (!this.hasConfiguredPort && !this.hasInitializedPort) {
			/* private SerialParameters parameters; */
			String portName1 = portName;
			int baudRate1 = baudRate;
			int dataBits1 = dataBits;
			int stopBits1 = stopBits;
			int parity1 = parity;
			this.hasConfiguredPort = true;
		}
		initPort();
	}

	@Override
	public void initPort() {
		this.hasInitializedPort = true;
		this.hasValidConnection = true;
		for (int i = 0; i < state.length; i++) {
			state[i] = 0;
		}

		for (int i = 0; i < locks.length; i++) {
			locks[i] = new AtomicBoolean(false);
		}
	}

	@Override
	public void closePort() {
		this.hasInitializedPort = false;
		this.hasValidConnection = false;
	}

	@Override
	public void reinitPort() {
		closePort();
		initPort();
	}

	@Override
	public byte getStatusByte(byte address) {
		return state[address];
	}

	@Override
	public void setStatusByte(byte address, byte status) {
		if (hasValidConnection) {
			update(address, status);
		}
	}

	@Override
	public boolean getStatusBit(byte address, byte port) {
		return BnB.getBit(state[address], port);
	}

	@Override
	public void setStatusBit(byte address, byte port, boolean statusBit) {
		byte newStatusByte = BnB.setBit(state[address], port, statusBit);
		setStatusByte(address, newStatusByte);
	}

	private synchronized void update(byte address, byte status) {
		state[address] = status;
	}

	@Override
	public void writeLock(byte address) {
		while (locks[address].compareAndSet(false, true)) {
			Thread.yield();
		}
	}

	@Override
	public void writeUnlock(byte address) {
		locks[address].set(false);
	}
}
