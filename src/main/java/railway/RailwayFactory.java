package railway;

import apps.Main;
import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import routing.RouteFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a configured Railway and all its components,
 * based on the config/railway.xml description.
 */
public class RailwayFactory {

	/* SINGLETON ARCHITECTURE */
	private static RailwayFactory instance;

	private RailwayFactory() {
	}

	public static RailwayFactory getInstance() {
		if (instance == null) {
			instance = new RailwayFactory();
		}
		return instance;
	}

	/**
	 * @return a configured railway
	 */
	public Railway getConfiguredRailway() {

		Railway railway = Railway.instance();
		Document config = null;

		SAXBuilder builder = new SAXBuilder();
		try {
			config = builder.build("config/railway.xml");
		} catch (Exception ex) {
			Main.getRailwayLogger("RailwayFactory").severe("Cannot load XML file");
		}

		// get all sector configurations and create corresponding sectors in railway
		List<Element> sectorElements = config.getRootElement().getChild("sectors").getChildren("sector");
		for (Element sectorElement : sectorElements) {

			String id = sectorElement.getAttributeValue("id");
			byte address = Byte.parseByte(sectorElement.getAttributeValue("address"));
			byte bitpos = Byte.parseByte(sectorElement.getAttributeValue("bitpos"));
			int length = Integer.parseInt(sectorElement.getAttributeValue("length"));

			Sector sector = new Sector(id, address, bitpos, length);

			railway.addSector(sector);
		}

		// get all switches configurations and create corresponding switches in railway
		List<Element> switchElements = config.getRootElement().getChild("switches").getChildren("switch");
		for (Element switchElement : switchElements) {
			String id = switchElement.getAttributeValue("id");
			byte address = (byte) Integer.parseInt(switchElement.getAttributeValue("address"));
			byte bitpos = (byte) Integer.parseInt(switchElement.getAttributeValue("bitpos"));
			Sector s = railway.getSectorById(switchElement.getAttributeValue("sector"));
			railway.addSwitch(new Switch(id, address, bitpos, s));
			//tells the sector that it contains a switch
			s.setHasSwitch(true);
		}

		// get all blocks configurations and create corresponding blocks in railway
		List<Element> blockElements = config.getRootElement().getChild("blocks").getChildren("block");
		for (Element eachBlockElement : blockElements) {
			String id = eachBlockElement.getAttributeValue("id");
			String start = eachBlockElement.getAttributeValue("start");
			String end = eachBlockElement.getAttributeValue("end");
			byte maxSpeed = (byte) Integer.parseInt(eachBlockElement.getAttributeValue("speed"));
			boolean dir = Integer.parseInt(eachBlockElement.getAttributeValue("dir")) != 0;
			ArrayList<Sector> blockSectors = new ArrayList();
			List<Element> blockSectorsElements = eachBlockElement.getChildren("sector");
			for (Element eachSectorElement : blockSectorsElements) {
				blockSectors.add(railway.getSectorById(eachSectorElement.getAttributeValue("ref")));
				if (railway.getSectorById(eachSectorElement.getAttributeValue("ref")) == null) {
					Main.getRailwayLogger("RailwayFactory").warning("Sector " + eachSectorElement.getAttributeValue("ref")
							+ " is not valid in block " + id);
				}
			}
			//configure switch positions
			ArrayList<SwitchPosition> blockSwitchPositions = new ArrayList();
			List<Element> blockSwitchPositionElements = eachBlockElement.getChildren("switch-position");
			for (Element eachSwitchPositionElement : blockSwitchPositionElements) {
				Switch sw = railway.getSwitchById(eachSwitchPositionElement.getAttributeValue("ref"));
				boolean pos = Integer.parseInt(eachSwitchPositionElement.getAttributeValue("pos")) != 0;
				blockSwitchPositions.add(new SwitchPosition(sw, pos));
				if (sw == null) {
					Main.getRailwayLogger("RailwayFactory").warning("Switch " + eachSwitchPositionElement.getAttributeValue("ref")
							+ " is not valid in block " + id);
				}
			}
			railway.addBlock(new Block(id, start, end, dir, maxSpeed, blockSectors, blockSwitchPositions));
		}

		/*
		 * Construction of the YBlocks
		 */
		Main.getRailwayLogger("RailwayFactory").info("Starting to generate the YBlocks");
		int nbYBlocks = 0;
		for (int indexA = 0; indexA < railway.getBlocks().size(); indexA++) {
			for (int indexB = 0; indexB < railway.getBlocks().size(); indexB++) {
				Block a = railway.getBlocks().get(indexA);
				Block b = railway.getBlocks().get(indexB);
				if (YBlock.isYBlockPossible(a, b)) {
					YBlock yb = new YBlock(a, b);
					railway.getBlocks().add(yb);
					nbYBlocks++;
				}
			}
		}
		Main.getRailwayLogger("RailwayFactory").info(nbYBlocks + " YBlocks have been generated");

		// get alls block-connection elements and complete corresponding blocks in railway
		//List<Element> nextBlockElements = config.getRootElement().getChild("blocks").getChildren("block");
		for (Block block : railway.getBlocks()) {
			block.addNextBlocks(railway.getBlocksByStartId(block.getEndId()));
		}

		// the graph may be made
		for (Block block : railway.getBlocks()) {
			RouteFactory.instance().addNode(block);
		}
		RouteFactory.instance().connectGraph();


		// get alls station elements and create corresponding stations in railway
		List<Element> stationElements = config.getRootElement().getChild("stations").getChildren("station");
		for (Element eachStationElement : stationElements) {
			String id = eachStationElement.getAttributeValue("id");
			Station s = new Station(id);
			railway.addStation(s);
			List<Element> platformSignalElements = eachStationElement.getChildren("platform-signal");
			for (Element el : platformSignalElements) {
				s.addSignal(el.getAttributeValue("ref"));
			}
		}

		// get alls signal elements and create corresponding signals in railway
		List<Element> signalElements = config.getRootElement().getChild("signals").getChildren("signal");
		for (Element signalElement : signalElements) {
			String id = signalElement.getAttributeValue("id");
			byte address = (byte) Integer.parseInt(signalElement.getAttributeValue("address"));

			Signal s = new Signal(id, address);

			Element g1 = signalElement.getChild("g1");
			if (g1.getAttributeValue("pos").equals("NA")) {
				s.setExistsg1(false);
			} else {
				s.setExistsg1(true);
				s.setG1bitpos((byte) Integer.parseInt(g1.getAttributeValue("pos")));
			}

			Element g2 = signalElement.getChild("g2");
			if (g2.getAttributeValue("pos").equals("NA")) {
				s.setExistsg2(false);
			} else {
				s.setExistsg2(true);
				s.setG2bitpos((byte) Integer.parseInt(g2.getAttributeValue("pos")));
			}

			Element r1 = signalElement.getChild("r1");
			if (r1.getAttributeValue("pos").equals("NA")) {
				s.setExistsr1(false);
			} else {
				s.setExistsr1(true);
				s.setR1bitpos((byte) Integer.parseInt(r1.getAttributeValue("pos")));
			}

			Element r2 = signalElement.getChild("r2");
			if (r2.getAttributeValue("pos").equals("NA")) {
				s.setExistsr2(false);
			} else {
				s.setExistsr2(true);
				s.setR2bitpos((byte) Integer.parseInt(r2.getAttributeValue("pos")));
			}

			Element o1 = signalElement.getChild("o1");
			if (o1.getAttributeValue("pos").equals("NA")) {
				s.setExistso1(false);
			} else {
				s.setExistso1(true);
				s.setO1bitpos((byte) Integer.parseInt(o1.getAttributeValue("pos")));
			}

			Element o2 = signalElement.getChild("o2");
			if (o2.getAttributeValue("pos").equals("NA")) {
				s.setExistso2(false);
			} else {
				s.setExistso2(true);
				s.setO2bitpos((byte) Integer.parseInt(o2.getAttributeValue("pos")));
			}

			Element num = signalElement.getChild("num");
			if (num.getAttributeValue("pos").equals("NA")) {
				s.setExistsnum(false);
			} else {
				s.setExistsnum(true);
				s.setNumbitpos((byte) Integer.parseInt(num.getAttributeValue("pos")));
			}
			s.setStop();
			railway.addSignal(s);


		}


		/** attribute signal to blocks */
		for (Block b : railway.getBlocks()) {
			if (!b.containsStop()) {
				String signalStart = b.getStartId();
				String signalEnd = b.getEndId();
				Signal s = railway.getSignalById(signalStart);
				Signal e = railway.getSignalById(signalEnd);
				b.setStartSignal(s);
				b.setEndSignal(e);
			} else {
				YBlock yb = (YBlock) b;
				yb.setFirstSignal(Railway.instance().getSignalById(yb.getStartId()));
				yb.setStopSignal(Railway.instance().getSignalById(yb.getStopId()));
				yb.setEndSignal(Railway.instance().getSignalById(yb.getEndId()));
			}
		}

		// get all locomtive elements and create corresponding locomotives in railway
		List<Element> locElements = config.getRootElement().getChild("locomotives").getChildren("locomotive");
		for (Element eachlocElement : locElements) {
			String locId = eachlocElement.getAttributeValue("id");
			byte locAddress = (byte) Integer.parseInt(eachlocElement.getAttributeValue("address"));
			int length = Integer.parseInt(eachlocElement.getAttributeValue("length"));
			int inertia = Integer.parseInt(eachlocElement.getAttributeValue("inertia"));
			railway.addLocomotive(new Locomotive(locId, locAddress, length, inertia));
		}

		// get all lines
		List<Element> lineElements = config.getRootElement().getChild("lines").getChildren("line");
		for (Element each : lineElements) {
			ArrayList<Station> stations = new ArrayList<>();
			List<Element> stationRefs = each.getChildren("station");
			for (Element ref : stationRefs) {
				Station s = railway.getStationById(ref.getAttributeValue("ref"));
				stations.add(s);
			}
			railway.getLines().add(new Line(each.getAttributeValue("id"), stations));

		}

		// get all train elements and create corresponding trains
		List<Element> trainElements = config.getRootElement().getChild("trains").getChildren("train");
		for (Element each : trainElements) {
			// id
			String trainId = each.getAttributeValue("id");

			//loc
			Locomotive trainLoc = railway.getLocomotiveById(each.getChild("locomotive").getAttributeValue("ref"));

			//line
			Line line = railway.getLineById(each.getChild("line").getAttributeValue("ref"));

			//position
			String sitnalId = each.getChild("signal").getAttributeValue("ref");

			//insertion
			Train t = new Train(trainId, trainLoc);
			t.setLine(line);
			t.setSignalId(sitnalId);
			railway.getTrains().add(t);
		}

		// railway is finaly ready
		Main.getRailwayLogger("RailwayFactory").finest("Railway configured");
		return railway;
	}
}
