package gui;

import apps.Main;
import events.OnTrainMustStop;
import events.OnTrainStopped;
import railway.*;

import static events.OnTrainMustSlowDown.LIMIT;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static railway.Train.states.*;


/**
 * Main interface to interact with the railway and the simulation
 */
public class GUI extends JFrame implements Runnable {
	/** Railway variable, to access the instance */
	private final Railway railway;
	/** Currently selected train, can be managed with components of the GUI */
	Train train;
	/** List of the trains to select*/
	public JComboBox<String> cbChoiceTrain;
	/** List of the lines to select for the current train */
	public JComboBox<Line> chLine;
	/** Speed slider, allows the user to set the speed of the current train */
	public static JSlider slSpeed;
	/** Emergency stop / continue button */
	public JButton bEmergencyStop;
	/** Lights checkbox for the current train */
	private JCheckBox chbLights;
	/** Direction checkbox for the current train */
	JCheckBox chDirection;
	/** Stop at the next station checkbox, for the current train */
	JCheckBox stopAtStation;
	/** Speed monitor */
	private JProgressBar speedometer;
	/** List of the stations to select a destination */
	public static JComboBox<String> cbChoiceStation;
	/** Button to request a route to a choosed detination */
	public RouteRequesterButton routeRequester;
	/** Button to start the Line of the train */
	public CommutingButton commuting;
	/** All the panels inside the GUI */
	private JPanel vertHolder;
	private JPanel mapRightHolder;
	private JPanel mapRowHolder;
	private JPanel loggerRowHolder;
	private JPanel trainColHolder;
	private JPanel cbHolder;
	/** quit flag */
	private boolean quit;

	public GUI() {

		try {
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		UIManager.put("TextField.disabledForeground", Color.BLACK);
		setTitle("Process Control IN.4028, UNIFR        Railway Project, SP 2020        Group 9, Alex Guardini, David Baschung");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
		quit = false;
		setLayout(new BorderLayout());

		this.railway = Railway.instance();

		speedometer = new JProgressBar();
		cbChoiceTrain = new JComboBox<>();
		bEmergencyStop = new JButton();
		chbLights = new JCheckBox();
		stopAtStation = new JCheckBox();
		chDirection = new JCheckBox();

		vertHolder = new JPanel();
		vertHolder.setLayout(new GridLayout(2, 1));
		mapRowHolder = new JPanel();
		mapRowHolder.setLayout(new GridLayout(1, 2));
		loggerRowHolder = new JPanel();
		loggerRowHolder.setLayout(new GridLayout(1, 3));
		trainColHolder = new JPanel();
		trainColHolder.setLayout(new GridLayout(3, 1));
		mapRightHolder = new JPanel();
		mapRightHolder.setLayout(new GridLayout(1, 4));
		cbHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));

		RailwayMap.instance().setSize(new Dimension(1000, 600));
		mapRowHolder.add(RailwayMap.instance());

		for (Train t : railway.getTrains()) {
			cbChoiceTrain.addItem(t.getId());
		}
		cbChoiceTrain.setSelectedIndex(0);
		cbChoiceTrain.setBackground(Color.WHITE);
		train = railway.getTrainById(cbChoiceTrain.getSelectedItem().toString());
		/**Selecting another train in the JComboBox applies the train's properties to each component of the GUI */
		cbChoiceTrain.addActionListener(e -> {
			train = railway.getTrainById(cbChoiceTrain.getSelectedItem().toString());
			cbChoiceStation.setSelectedItem(train.getStationName()!=null?train.getStationName(): cbChoiceStation.getItemAt(0));
			chDirection.setSelected(train.getLocomotive().getDirection());
			chbLights.setSelected(train.getLocomotive().isLightOn());
			stopAtStation.setSelected(train.getStopAtStation());
			switch (train.getState()) {
				case MANUAL: RoutingButton.setAllInstancesToManualState(); break;
				case WAITROUTING :
				case WAITCOMMUTING:
					RoutingButton.setAllInstancesToWaitingState(); break;
				case ROUTING : routeRequester.setAutoState(); break;
				case COMMUTING : commuting.setAutoState(); break;
			}
			slSpeed.setValue(train.getLocomotive().getDesiredSpeed());
		});
		trainColHolder.add(cbChoiceTrain);

		chbLights.setText("Lights");
		/** Checking the box "Lights" sets the Lights of the train*/
		chbLights.addActionListener(actionEvent -> {
			train.getLocomotive().setLight(chbLights.isSelected());
		});
		cbHolder.add(chbLights);


		stopAtStation.setText("Stop at stations");
		/** Checking the box "Stop at stations" makes the current train stop at the next station*/
		stopAtStation.addActionListener(actionEvent -> {
			train.setStopAtStation(stopAtStation.isSelected());
		});
		cbHolder.add(stopAtStation);


		mapRightHolder.add(trainColHolder);
		cbChoiceStation = new JComboBox<>();
		for (Station s : Railway.instance().getStations())
			cbChoiceStation.addItem(s.getId());

		chDirection.setText("Reverse");
		/** Checking the box "Reverse" inverses the direction of the train.
		 * (For the same boolean value, the direction remains the same, even when we re-select the train in the list) */
		chDirection.addActionListener(actionEvent -> {
			railway.getObservers().add(new OnTrainStopped(train));
			train.getLocomotive().reachSpeed((byte) 0);
			train.getLocomotive().setDirection( ! train.getLocomotive().getDirection());
		});
		cbHolder.add(chDirection);

		trainColHolder.add(cbHolder);

		slSpeed = new JSlider(JSlider.VERTICAL, 0, 31, 0); // (must remain above routeRequester)
		OnTrainMustStop.gui = this;
		Train.gui = this;
		routeRequester = new RouteRequesterButton(this);
		/** On click, requests the route for the current train to the choosed destination.
		 * It sets the state of the train, and some variables to tell the Main method to start a Route.
		 * When the train is already in ROUTING state, we can go back to MANUAL mode and remove the Route and Observers.
		 * Only the clicked button will remain activated and it adapts the GUI.
		 */
		routeRequester.addActionListener(e -> {
			if ( train.getState() == MANUAL && cbChoiceStation.getSelectedItem() != null) {
				train.setState(WAITROUTING);
				train.setRouteChosen(true);
				train.setMoving(false);
				train.setStationName(cbChoiceStation.getSelectedItem().toString());
				RoutingButton.setAllInstancesToWaitingState();
				slSpeed.setValue(0);
			} else { // (when ROUTING / COMMUTING only as WAIT disabled)
				train.setState(MANUAL);
				train.setRouteChosen(false);
				if (train.getRoute() != null){
					for (Block b : train.getRoute().getBlocks()){
						b.unlockSectors();
					}
				}
				slSpeed.setValue(0);
				train.getLocomotive().reachSpeed((byte) slSpeed.getValue());
				if (train.getRoute() != null){
					train.setSignalId(train.getRoute().getCurrentBlock().getEndId());
				}
				train.setRoute(null);
				Main.getTrainLogger(train).info(train.getSignalId());
				for (int i=0; i<railway.getObservers().size(); ){
					if (railway.getObservers().get(i).getTrain().getId().equals(train.getId())) {
						railway.getObservers().get(i).pauseThread();
						railway.getObservers().remove(railway.getObservers().get(i));
					} else {
						i++;
					}
				}
				RoutingButton.setAllInstancesToManualState();
				slSpeed.setValue(0);
			}
		});

		trainColHolder.add(cbChoiceStation);

		commuting = new CommutingButton(this);
		/** On click, starts the Line assigned to the current train and requests the first Route
		 * It sets the state of the train, and some variables to tell the Main method to start a Line (COMMUTING).
		 * When the train is already in COMMUTING state, we can go back to MANUAL mode, stop the Line, remove the Route
		 * and the Observers. Only the clicked button will remain activated and it adapts the GUI.
		 */
		commuting.addActionListener(e -> {
			if ( train.getState() == MANUAL && cbChoiceTrain.getSelectedItem() != null) {
				train.setState(WAITCOMMUTING);
				train.setIsFirstStation(true);
				train.setRouteChosen(true);
				train.setMoving(false);
				train.setLine((Line) chLine.getSelectedItem());
				train.setStationName(train.getLine().getCurrentStation().getId());
				RoutingButton.setAllInstancesToWaitingState();
				slSpeed.setValue(0);
			} else { // (when ROUTING / COMMUTING only as WAIT disabled)
				train.setState(MANUAL);
				train.setRouteChosen(false);
				if (train.getRoute() != null) {
					for (Block b : train.getRoute().getBlocks()){
						b.unlockSectors();
					}
				}
				slSpeed.setValue(0);
				train.getLocomotive().reachSpeed((byte) slSpeed.getValue());
				if (train.getRoute() != null){
					train.setSignalId(train.getRoute().getCurrentBlock().getEndId());
				}
				train.setRoute(null);
				for (int i=0; i<railway.getObservers().size(); ){
					if (railway.getObservers().get(i).getTrain().getId().equals(train.getId())) {
						railway.getObservers().get(i).pauseThread();
						railway.getObservers().remove(railway.getObservers().get(i));
					} else {
						i++;
					}
				}
				RoutingButton.setAllInstancesToManualState();
				slSpeed.setValue(0);
			}
		});

		chLine = new JComboBox<>();
		for (Line line : railway.getLines()){
			chLine.addItem(line);
		}

		trainColHolder.add(chLine);
		trainColHolder.add(routeRequester);
		trainColHolder.add(commuting);

		RouteRequesterButton.setAllInstancesToManualState(); //Set initial design to all GUI components.

		slSpeed.setMinorTickSpacing(1);
		slSpeed.setMajorTickSpacing(5);
		/** When the user drags the slider's value, the "desired speed" is set */
		slSpeed.addChangeListener(e -> {
			if (train != null)
				if (train.getSLOWING_DOWN()){
					if (train.getLocomotive().getSpeed() > LIMIT) {
						train.getLocomotive().reachSpeed((byte) LIMIT);
					}
				} else {
					train.getLocomotive().reachSpeed((byte) slSpeed.getValue());
				}
		});
		mapRightHolder.add(slSpeed);


		speedometer = new JProgressBar(JProgressBar.VERTICAL, 0, 31);
		speedometer.setSize(new Dimension(10, 300));
		mapRightHolder.add(speedometer);


		bEmergencyStop = new JButton("<html><center>EMERGENCY<br />STOP</center></html>");
		bEmergencyStop.setFont(new Font(bEmergencyStop.getFont().getName(),Font.BOLD,bEmergencyStop.getFont().getSize()+10));
		bEmergencyStop.setBackground(Color.RED);
		bEmergencyStop.setBorder(new LineBorder(Color.RED));
		bEmergencyStop.setSize(new Dimension(200, 300));
		/** When the emergency button is pressed, a flag is set to stop everything */
		bEmergencyStop.addActionListener(e -> {
			if ( ! railway.isEmergencyStopped()){
				railway.emergencyStop();
				bEmergencyStop.setText("<html><center>EMERGENCY<br />CONTINUE</center></html>");
				bEmergencyStop.setBackground(new Color(255,150,150));
			} else {
				railway.emergencyContinue();
				bEmergencyStop.setText("<html><center>EMERGENCY<br />STOP</center></html>");
				bEmergencyStop.setBackground(Color.RED);
			}
		});
		mapRightHolder.add(bEmergencyStop);
		mapRowHolder.add(mapRightHolder);

		for (Train t : railway.getTrains()) {
			TrainLogPanel logPanel = new TrainLogPanel();
			t.setLoggerWindowHandler(logPanel);
			loggerRowHolder.add(logPanel);
		}

		vertHolder.add(mapRowHolder);
		vertHolder.add(loggerRowHolder);

		this.add(vertHolder);
		this.setSize(1024, 768);
		this.pack();
		this.setVisible(true);


		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit = true;
				railway.emergencyStop();
				e.getWindow().dispose();
				Main.quit();
			}
		});

		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		while (!quit) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			speedometer.setValue(train.getLocomotive().getSpeed());
		}
	}
}
