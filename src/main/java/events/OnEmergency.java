package events;

import gui.GUI;
import railway.Train;

//public class OnEmergency extends Observer {
//
//	private static GUI gui = null;
//
//	public OnEmergency(Train t) {
//		super(t);
//		this.start();
//	}
//
//	@Override
//	protected boolean test() {
////		boolean testResult = (todo revmoe
////				train.getRoute().getNextBlock().isOccupied()
////			&&	! train.getOnBlockLeft().isBlockLeft()
////		);
////		if (train.getOnBlockLeft() != null)
////			train.getOnBlockLeft().setBlockLeft(false);
////		return testResult;
//		return (train.getRoute().getSecondNextBlock().isOccupied());
//	}
//
//	@Override
//	protected void process() {
//		gui.bEmergencyStop.doClick();
//		train.getLogger().info("An emergency stop occured, the second next block was not safe");
//		if (!train.getRoute().getCurrentBlock().getEndId().equals(train.getRoute().getLastBlock().getEndId()))
//			train.detectEmergencies();
//	}
//
//	public static void setGui(GUI gui) {
//		OnEmergency.gui = gui;
//	}
//}
