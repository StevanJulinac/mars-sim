/**
 * Mars Simulation Project
 * MainDetailPanel.java
 * @version 3.1.0 2017-10-20
 * @author Scott Davis
 */
package org.mars_sim.msp.ui.swing.tool.mission;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.mars_sim.msp.core.Msg;
import org.mars_sim.msp.core.UnitEvent;
import org.mars_sim.msp.core.UnitEventType;
import org.mars_sim.msp.core.UnitListener;
import org.mars_sim.msp.core.person.Person;
import org.mars_sim.msp.core.person.ai.mission.AreologyStudyFieldMission;
import org.mars_sim.msp.core.person.ai.mission.BiologyStudyFieldMission;
import org.mars_sim.msp.core.person.ai.mission.BuildingConstructionMission;
import org.mars_sim.msp.core.person.ai.mission.BuildingSalvageMission;
import org.mars_sim.msp.core.person.ai.mission.CollectIce;
import org.mars_sim.msp.core.person.ai.mission.CollectRegolith;
import org.mars_sim.msp.core.person.ai.mission.EmergencySupplyMission;
import org.mars_sim.msp.core.person.ai.mission.Exploration;
import org.mars_sim.msp.core.person.ai.mission.MeteorologyStudyFieldMission;
import org.mars_sim.msp.core.person.ai.mission.Mining;
import org.mars_sim.msp.core.person.ai.mission.Mission;
import org.mars_sim.msp.core.person.ai.mission.MissionEvent;
import org.mars_sim.msp.core.person.ai.mission.MissionEventType;
import org.mars_sim.msp.core.person.ai.mission.MissionListener;
import org.mars_sim.msp.core.person.ai.mission.MissionMember;
import org.mars_sim.msp.core.person.ai.mission.RescueSalvageVehicle;
import org.mars_sim.msp.core.person.ai.mission.Trade;
import org.mars_sim.msp.core.person.ai.mission.VehicleMission;
import org.mars_sim.msp.core.resource.AmountResource;
import org.mars_sim.msp.core.resource.ResourceUtil;
import org.mars_sim.msp.core.robot.Robot;
import org.mars_sim.msp.core.tool.Conversion;
import org.mars_sim.msp.core.vehicle.GroundVehicle;
import org.mars_sim.msp.core.vehicle.StatusType;
import org.mars_sim.msp.core.vehicle.Vehicle;
import org.mars_sim.msp.ui.swing.ImageLoader;
import org.mars_sim.msp.ui.swing.MainDesktopPane;
import org.mars_sim.msp.ui.swing.MarsPanelBorder;
import org.mars_sim.msp.ui.swing.tool.SpringUtilities;
import org.mars_sim.msp.ui.swing.tool.TableStyle;
import org.mars_sim.msp.ui.swing.tool.ZebraJTable;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebTextField;

/**
 * The tab panel for showing mission details.
 */
public class MainDetailPanel extends WebPanel implements ListSelectionListener, MissionListener, UnitListener {

	// Custom mission panel IDs.
	private final static String EMPTY = Msg.getString("MainDetailPanel.empty"); //$NON-NLS-1$

	// Private members
	private WebTextField descriptionTF;

//	private WebLabel descriptionLabel;
	private WebLabel designationLabel;
	private WebLabel typeLabel;
	private WebLabel startingLabel;
	private WebLabel phaseLabel;
	private WebLabel memberNumLabel;
	private WebLabel vehicleStatusLabel;
	private WebLabel speedLabel;
	private WebLabel distanceNextNavLabel;
	private WebLabel traveledLabel;

	private MemberTableModel memberTableModel;
	private JTable memberTable;

	private WebButton centerMapButton;
	private WebButton vehicleButton;

	private DecimalFormat formatter = new DecimalFormat(Msg.getString("MainDetailPanel.decimalFormat")); //$NON-NLS-1$
	private CardLayout customPanelLayout;

	private WebPanel missionCustomPane;

	private Mission currentMission;
	private Vehicle currentVehicle;
	private MissionWindow missionWindow;
	private MainDesktopPane desktop;

	private Map<String, MissionCustomInfoPanel> customInfoPanels;

//	private static int iceID = ResourceUtil.iceID;
//	private static int regolithID = ResourceUtil.regolithID;

	private static AmountResource iceAR = ResourceUtil.iceAR;
	private static AmountResource regolithAR = ResourceUtil.regolithAR;

	/**
	 * Constructor.
	 * 
	 * @param desktop the main desktop panel.
	 */
	public MainDetailPanel(MainDesktopPane desktop, MissionWindow missionWindow) {
		// User JPanel constructor.
		super();
		// Initialize data members.
		this.desktop = desktop;
		// 2016-09-24 Added missionWindow param
		this.missionWindow = missionWindow;

		// Set the layout.
		setLayout(new BorderLayout());

		WebScrollPane scrollPane = new WebScrollPane();
		scrollPane.setBorder(new MarsPanelBorder());
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setHorizontalScrollBarPolicy(WebScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);

		// Create the main panel.
		Box mainBox = Box.createVerticalBox();
		// JPanel mainPane = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		mainBox.setBorder(new MarsPanelBorder());
//		add(mainBox, BorderLayout.CENTER);
//		scrollPane.add(mainBox, BorderLayout.CENTER);
		scrollPane.setViewportView(mainBox);

		// Create the description panel.
		Box infoPane = new CustomBox();
		infoPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		// infoPane.setSize(new Dimension(200, 200));
		infoPane.setBorder(new MarsPanelBorder());
		mainBox.add(infoPane);

		WebPanel topPane = new WebPanel(new SpringLayout());
		infoPane.add(topPane);

		// Create the description label.
		WebLabel descriptionLabel0 = new WebLabel(Msg.getString("MainDetailPanel.title.description", WebLabel.LEFT)); //$NON-NLS-1$
		descriptionLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPane.add(descriptionLabel0);

		descriptionTF = new WebTextField("");
		descriptionTF.setEditable(false);
		descriptionTF.setColumns(25);
		// descriptionTF.setOpaque(false);
		// descriptionTF.setFont(new Font("Serif", Font.PLAIN, 10));
		descriptionTF.setToolTipText(Msg.getString("MainDetailPanel.tf.description")); //$NON-NLS-1$

//		descriptionLabel = new WebLabel("None", WebLabel.LEFT);
//		//descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
//		descriptionLabel.setToolTipText(Msg.getString("MainDetailPanel.description")); //$NON-NLS-1$

		String s = "";
		// Implement the missing descriptionLabel
		if (missionWindow.getCreateMissionWizard() != null) {
			s = Conversion.capitalize(missionWindow.getCreateMissionWizard().getTypePanel().getDesignation());// getDescription());
			descriptionTF.setText(s);
		}

		WebPanel wrapper0 = new WebPanel(new FlowLayout(FlowLayout.LEFT));
		wrapper0.add(descriptionTF);
		topPane.add(wrapper0);

		// Create the designation label.
		WebLabel designationLabel0 = new WebLabel(Msg.getString("MainDetailPanel.designation", WebLabel.LEFT)); //$NON-NLS-1$
		designationLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPane.add(designationLabel0);

		designationLabel = new WebLabel("", WebLabel.LEFT);
		WebPanel wrapper1a = new WebPanel(new FlowLayout(FlowLayout.LEFT));
		wrapper1a.add(designationLabel);
		topPane.add(wrapper1a);

		// Create the type label.
		WebLabel startingLabel0 = new WebLabel(Msg.getString("MainDetailPanel.startingMember", WebLabel.LEFT)); //$NON-NLS-1$
		startingLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPane.add(startingLabel0);
		// startingLabel0.setToolTipText(Msg.getString("MainDetailPanel.starting"));//$NON-NLS-1$

		startingLabel = new WebLabel("", WebLabel.LEFT);
		// typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		WebPanel wrapper1 = new WebPanel(new FlowLayout(FlowLayout.LEFT));
		wrapper1.add(startingLabel);
		topPane.add(wrapper1);

		// Create the type label.
		WebLabel typeLabel0 = new WebLabel(Msg.getString("MainDetailPanel.type", WebLabel.LEFT)); //$NON-NLS-1$
		typeLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPane.add(typeLabel0);
		// typeLabel0.setToolTipText(Msg.getString("MainDetailPanel.type"));//$NON-NLS-1$

		typeLabel = new WebLabel("", WebLabel.LEFT);
		// typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		WebPanel wrapper2 = new WebPanel(new FlowLayout(FlowLayout.LEFT));
		wrapper2.add(typeLabel);
		topPane.add(wrapper2);

		// Create the phase label.
		WebLabel phaseLabel0 = new WebLabel(Msg.getString("MainDetailPanel.phase", WebLabel.LEFT)); //$NON-NLS-1$
		phaseLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		topPane.add(phaseLabel0);

		phaseLabel = new WebLabel("", WebLabel.LEFT);
		// phaseLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		WebPanel wrapper3 = new WebPanel(new FlowLayout(FlowLayout.LEFT));
		wrapper3.add(phaseLabel);
		topPane.add(wrapper3);

		// Prepare SpringLayout
		SpringUtilities.makeCompactGrid(topPane, 5, 2, // rows, cols
				3, 1, // initX, initY
				25, 1); // xPad, yPad

		// Create the travel panel.
		Box travelBox = new CustomBox();
		travelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		// Prepare SpringLayout
		WebPanel travelPane = new WebPanel(new SpringLayout());
		travelPane.setSize(new Dimension(200, 300));
		// travelPane.setBorder(new MarsPanelBorder());
		travelBox.add(travelPane);
		mainBox.add(travelBox);

		// Create the vehicle panel.
		WebPanel vehiclePane = new WebPanel(new FlowLayout(0, 0, FlowLayout.LEADING));
		vehiclePane.setAlignmentX(Component.BOTTOM_ALIGNMENT);
		travelPane.add(vehiclePane);

		// Create center map button
		centerMapButton = new WebButton(ImageLoader.getIcon(Msg.getString("img.centerMap"))); //$NON-NLS-1$
		centerMapButton.setMargin(new Insets(2, 2, 2, 2));
		centerMapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentMission != null) {
					getDesktop().centerMapGlobe(currentMission.getCurrentMissionLocation());
				}
			}
		});
		centerMapButton.setToolTipText(Msg.getString("MainDetailPanel.gotoMarsMap")); //$NON-NLS-1$
		centerMapButton.setEnabled(false);
		vehiclePane.add(centerMapButton);

		// Create the vehicle label.
		WebLabel vehicleLabel = new WebLabel("   " + Msg.getString("MainDetailPanel.vehicle"), WebLabel.RIGHT); //$NON-NLS-1$
		vehiclePane.add(vehicleLabel);

		// Create the vehicle panel.
		vehicleButton = new WebButton("\t\t\t\t\t"); //$NON-NLS-1$
		vehicleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		vehicleButton.setVisible(false);
		vehicleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentMission instanceof VehicleMission) {
					// Open window for vehicle.
					VehicleMission vehicleMission = (VehicleMission) currentMission;
					Vehicle vehicle = vehicleMission.getVehicle();
					if (vehicle != null) {
						getDesktop().openUnitWindow(vehicle, false);
					}
				} else if (currentMission instanceof BuildingConstructionMission) {
					BuildingConstructionMission constructionMission = (BuildingConstructionMission) currentMission;
					List<GroundVehicle> constVehicles = constructionMission.getConstructionVehicles();
					if (constVehicles != null)
						if (!constVehicles.isEmpty() || constVehicles.size() > 0) {
							Vehicle vehicle = constVehicles.get(0);
							getDesktop().openUnitWindow(vehicle, false);
						}
				} else if (currentMission instanceof BuildingSalvageMission) {
					BuildingSalvageMission salvageMission = (BuildingSalvageMission) currentMission;
					List<GroundVehicle> constVehicles = salvageMission.getConstructionVehicles();
					if (constVehicles != null)
						if (!constVehicles.isEmpty() || constVehicles.size() > 0) {
							Vehicle vehicle = constVehicles.get(0);
							getDesktop().openUnitWindow(vehicle, false);
						}
				}
			}
		});
		WebPanel wrapper00 = new WebPanel(new FlowLayout(0, 0, FlowLayout.LEADING));
		wrapper00.add(vehicleButton);
		travelPane.add(wrapper00);

		// Create the vehicle status label.
		WebLabel vehicleStatusLabel0 = new WebLabel(Msg.getString("MainDetailPanel.vehicleStatus", WebLabel.LEFT));//$NON-NLS-1$ //$NON-NLS-2$
		// vehicleStatusLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		travelPane.add(vehicleStatusLabel0);

		vehicleStatusLabel = new WebLabel("", WebLabel.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
		// vehicleStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		WebPanel wrapper01 = new WebPanel(new FlowLayout(0, 0, FlowLayout.LEADING));
		wrapper01.add(vehicleStatusLabel);
		travelPane.add(wrapper01);

		// Create the speed label.
		WebLabel speedLabel0 = new WebLabel(Msg.getString("MainDetailPanel.vehicleSpeed", WebLabel.LEFT)); //$NON-NLS-1$ //$NON-NLS-2$
		// speedLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		travelPane.add(speedLabel0);

		speedLabel = new WebLabel("", WebLabel.LEFT); //$NON-NLS-1$ //$NON-NLS-2$
		// speedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		WebPanel wrapper02 = new WebPanel(new FlowLayout(0, 0, FlowLayout.LEADING));
		wrapper02.add(speedLabel);
		travelPane.add(wrapper02);

		// Create the distance next navpoint label.
		WebLabel distanceNextNavLabel0 = new WebLabel(
				Msg.getString("MainDetailPanel.distanceNextNavPoint", WebLabel.LEFT)); //$NON-NLS-1$
		// distanceNextNavLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		travelPane.add(distanceNextNavLabel0);

		distanceNextNavLabel = new WebLabel("", WebLabel.LEFT); //$NON-NLS-1$
		// distanceNextNavLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		WebPanel wrapper03 = new WebPanel(new FlowLayout(0, 0, FlowLayout.LEADING));
		wrapper03.add(distanceNextNavLabel);
		travelPane.add(wrapper03);

		// Create the traveled distance label.
		WebLabel traveledLabel0 = new WebLabel(Msg.getString("MainDetailPanel.distanceTraveled", WebLabel.LEFT)); //$NON-NLS-1$
		// traveledLabel0.setAlignmentX(Component.LEFT_ALIGNMENT);
		travelPane.add(traveledLabel0);

		traveledLabel = new WebLabel("", WebLabel.LEFT); //$NON-NLS-1$
		// traveledLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		WebPanel wrapper04 = new WebPanel(new FlowLayout(0, 0, FlowLayout.LEADING));
		wrapper04.add(traveledLabel);
		travelPane.add(wrapper04);

		// Prepare SpringLayout
		SpringUtilities.makeCompactGrid(travelPane, 5, 2, // rows, cols
				10, 1, // initX, initY
				3, 0); // xPad, yPad

		// Create the member panel.
		Box memberPane = new CustomBox();
		memberPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainBox.add(memberPane);

		// Create the member number label.
		memberNumLabel = new WebLabel(Msg.getString("MainDetailPanel.missionMembersMinMax", "", "", "", WebLabel.LEFT)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		memberNumLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		memberPane.add(memberNumLabel);

		// Create member bottom panel.
		WebPanel memberBottomPane = new WebPanel(new BorderLayout(0, 0));
		memberBottomPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		memberPane.add(memberBottomPane);

		// Prepare member list panel
		WebPanel memberListPane = new WebPanel(new BorderLayout(0, 0));
		memberListPane.setPreferredSize(new Dimension(100, 150));
		memberBottomPane.add(memberListPane, BorderLayout.CENTER);

		// Create scroll panel for member list.
		WebScrollPane memberScrollPane = new WebScrollPane();
		// memberScrollPane.setPreferredSize(new Dimension(300, 250));
		memberListPane.add(memberScrollPane, BorderLayout.CENTER);

		// Create member table model.
		memberTableModel = new MemberTableModel();

		// Create member table.
		// memberTable = new JTable(memberTableModel);
		memberTable = new ZebraJTable(memberTableModel);
		// memberTable.setPreferredSize(new Dimension(300, 250));
		memberTable.getColumnModel().getColumn(0).setPreferredWidth(40);
		memberTable.getColumnModel().getColumn(1).setPreferredWidth(150);
		memberTable.setRowSelectionAllowed(true);
		memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		memberTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					// Open window for selected person.
					int index = memberTable.getSelectedRow();

					MissionMember member = memberTableModel.getMemberAtIndex(index);
					Person person = null;
					Robot robot = null;
					if (member instanceof Person) {
						person = (Person) memberTableModel.getMemberAtIndex(index);
						if (person != null)
							getDesktop().openUnitWindow(person, false);

					} else if (member instanceof Robot) {
						robot = (Robot) memberTableModel.getMemberAtIndex(index);
						if (robot != null)
							getDesktop().openUnitWindow(robot, false);
					}

				}
			}
		});
		memberScrollPane.setViewportView(memberTable);

		// Create the mission custom panel.
		customPanelLayout = new CardLayout();
		// missionCustomPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		missionCustomPane = new WebPanel(customPanelLayout);
		// missionCustomPane.add(customPanelLayout);
		missionCustomPane.setBorder(new MarsPanelBorder());
		missionCustomPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		// scrollPane.add(missionCustomPane);
		mainBox.add(missionCustomPane);

		// Create custom empty panel.
		WebPanel emptyCustomPane1 = new WebPanel();
		missionCustomPane.add(emptyCustomPane1, EMPTY);

		customInfoPanels = new HashMap<String, MissionCustomInfoPanel>();


		// Create custom areology field mission panel.
		MissionCustomInfoPanel areologyFieldPanel = new AreologyStudyFieldMissionCustomInfoPanel(desktop);
		String areologyMissionName = AreologyStudyFieldMission.class.getName();
		customInfoPanels.put(areologyMissionName, areologyFieldPanel);
		missionCustomPane.add(areologyFieldPanel, areologyMissionName);

		// Create custom biology field mission panel.
		MissionCustomInfoPanel biologyFieldPanel = new BiologyStudyFieldMissionCustomInfoPanel(desktop);
		String biologyMissionName = BiologyStudyFieldMission.class.getName();
		customInfoPanels.put(biologyMissionName, biologyFieldPanel);
		missionCustomPane.add(biologyFieldPanel, biologyMissionName);
		
		// Create custom meteorology field mission panel.
		MissionCustomInfoPanel meteorologyFieldPanel = new MeteorologyStudyFieldMissionCustomInfoPanel(desktop);
		String meteorologyMissionName = MeteorologyStudyFieldMission.class.getName();
		customInfoPanels.put(meteorologyMissionName, meteorologyFieldPanel);
		missionCustomPane.add(meteorologyFieldPanel, meteorologyMissionName);
		
		// Create custom trade mission panel.
		MissionCustomInfoPanel tradePanel = new TradeMissionCustomInfoPanel();
		String tradeMissionName = Trade.class.getName();
		customInfoPanels.put(tradeMissionName, tradePanel);
		missionCustomPane.add(tradePanel, tradeMissionName);

		// Create custom mining mission panel.
		MissionCustomInfoPanel miningPanel = new MiningMissionCustomInfoPanel(desktop);
		String miningMissionName = Mining.class.getName();
		customInfoPanels.put(miningMissionName, miningPanel);
		missionCustomPane.add(miningPanel, miningMissionName);

		// Create custom construction mission panel.
		MissionCustomInfoPanel constructionPanel = new ConstructionMissionCustomInfoPanel(desktop);
		String constructionMissionName = BuildingConstructionMission.class.getName();
		customInfoPanels.put(constructionMissionName, constructionPanel);
		missionCustomPane.add(constructionPanel, constructionMissionName);

		// Create custom salvage mission panel.
		MissionCustomInfoPanel salvagePanel = new SalvageMissionCustomInfoPanel(desktop);
		String salvageMissionName = BuildingSalvageMission.class.getName();
		customInfoPanels.put(salvageMissionName, salvagePanel);
		missionCustomPane.add(salvagePanel, salvageMissionName);

		// Create custom exploration mission panel.
		MissionCustomInfoPanel explorationPanel = new ExplorationCustomInfoPanel();
		String explorationMissionName = Exploration.class.getName();
		customInfoPanels.put(explorationMissionName, explorationPanel);
		missionCustomPane.add(explorationPanel, explorationMissionName);

		
		// Create custom collect regolith mission panel.
		MissionCustomInfoPanel collectRegolithPanel = new CollectResourcesMissionCustomInfoPanel(regolithAR);
		String collectRegolithMissionName = CollectRegolith.class.getName();
		customInfoPanels.put(collectRegolithMissionName, collectRegolithPanel);
		missionCustomPane.add(collectRegolithPanel, collectRegolithMissionName);

		// Create custom collect ice mission panel.
		MissionCustomInfoPanel collectIcePanel = new CollectResourcesMissionCustomInfoPanel(iceAR);
		String collectIceMissionName = CollectIce.class.getName();
		customInfoPanels.put(collectIceMissionName, collectIcePanel);
		missionCustomPane.add(collectIcePanel, collectIceMissionName);

		// Create custom rescue/salvage vehicle mission panel.
		MissionCustomInfoPanel rescuePanel = new RescueMissionCustomInfoPanel(desktop);
		String rescueMissionName = RescueSalvageVehicle.class.getName();
		customInfoPanels.put(rescueMissionName, rescuePanel);
		missionCustomPane.add(rescuePanel, rescueMissionName);

		// Create custom emergency supply mission panel.
		MissionCustomInfoPanel emergencySupplyPanel = new EmergencySupplyMissionCustomInfoPanel();
		String emergencySupplyMissionName = EmergencySupplyMission.class.getName();
		customInfoPanels.put(emergencySupplyMissionName, emergencySupplyPanel);
		missionCustomPane.add(emergencySupplyPanel, emergencySupplyMissionName);
	}

	/**
	 * Implemented from ListSelectionListener. Note: this is called when a mission
	 * is selected on MissionWindow's mission list.
	 */
	public void valueChanged(ListSelectionEvent e) {

		TableStyle.setTableStyle(memberTable);

		// Remove mission and unit listeners.
		if (currentMission != null)
			currentMission.removeMissionListener(this);
		if (currentVehicle != null)
			currentVehicle.removeUnitListener(this);

		// Get the selected mission.
		Mission mission = (Mission) ((JList<?>) e.getSource()).getSelectedValue();

		if (mission != null) {
			// Update mission info in UI.

			if (missionWindow.getCreateMissionWizard() != null
					&& missionWindow.getCreateMissionWizard().getTypePanel().getDesignation() != null) {
				String now = missionWindow.getCreateMissionWizard().getTypePanel().getDesignation();// .getDescription();
				// Set the description based on what has been input by the mission creation
				// wizard
				mission.setDescription(now);
				descriptionTF.setText(now);
			}

			else if (mission.getDescription() != null) {
				descriptionTF.setText(Conversion.capitalize(mission.getDescription()));
			}

//			String previous = Conversion.capitalize(mission.getDescription());

//			String desTF = descriptionTF.getText();
//			
//			if (desTF != null && oldDes != null && !desTF.equals(oldDes) && !desTF.equals("None")) {
//				// Set the description based on what has been input in the textfield
//				mission.setDescription(desTF);
//				descriptionTF.setText(oldDes);
//			}
//			
//			else if (missionWindow.getCreateMissionWizard() != null 
//					&& missionWindow.getCreateMissionWizard().getTypePanel().getDescription() != null) {
//				String s = missionWindow.getCreateMissionWizard().getTypePanel().getDescription();
//				//descriptionTF.setText(s);
//				// Set the description based on what has been input by the mission creation wizard
//				mission.setDescription(s);
//			}
//			
//			else if (oldDes != null && desTF != null && desTF.equals("None")) {
//				descriptionTF.setText(Conversion.capitalize(oldDes));
//			}

			designationLabel.setText(mission.getFullMissionDesignation());

			typeLabel.setText(mission.getName());

			startingLabel.setText(mission.getStartingMember().getName()); // $NON-NLS-1$

			String phaseText = mission.getPhaseDescription();
			phaseLabel.setToolTipText(phaseText);
			if (phaseText.length() > 48)
				phaseText = phaseText.substring(0, 48) + "...";
			phaseLabel.setText(phaseText); // $NON-NLS-1$

			int memberNum = mission.getMembersNumber();
			int minMembers = mission.getMinMembers();
			String maxMembers = ""; //$NON-NLS-1$

			if (mission instanceof VehicleMission) {
				maxMembers = "" + mission.getMissionCapacity(); //$NON-NLS-1$
			} else {
				maxMembers = Msg.getString("MainDetailPanel.unlimited"); //$NON-NLS-1$
			}

			memberNumLabel
					.setText(Msg.getString("MainDetailPanel.missionMembersMinMax", memberNum, minMembers, maxMembers)); //$NON-NLS-1$
			memberTableModel.setMission(mission);
			centerMapButton.setEnabled(true);

			// Update mission vehicle info in UI.
			boolean isVehicle = false;
			if (mission instanceof VehicleMission) {
				VehicleMission vehicleMission = (VehicleMission) mission;
				Vehicle vehicle = vehicleMission.getVehicle();
				if (vehicle != null) {
					isVehicle = true;
					vehicleButton.setText(vehicle.getName());
					vehicleButton.setVisible(true);
					StatusType s = vehicle.getStatus();
					// if (s == null)
					// s = "Not Applicable";
					vehicleStatusLabel.setText(s.getName());
					speedLabel.setText(Msg.getString("MainDetailPanel.kmhSpeed", formatter.format(vehicle.getSpeed()))); //$NON-NLS-1$
					try {
						int distanceNextNav = (int) vehicleMission.getCurrentLegRemainingDistance();
						distanceNextNavLabel.setText(Msg.getString("MainDetailPanel.kmNextNavPoint", distanceNextNav)); //$NON-NLS-1$
					} catch (Exception e2) {
					}
					int travelledDistance = (int) vehicleMission.getTotalDistanceTravelled();
					int totalDistance = (int) vehicleMission.getTotalDistance();
					traveledLabel.setText(Msg.getString("MainDetailPanel.kmTraveled", //$NON-NLS-1$
							travelledDistance, totalDistance));
					vehicle.addUnitListener(this);
					currentVehicle = vehicle;
				}
			} else if (mission instanceof BuildingConstructionMission) {
				// Display first of mission's list of construction vehicles.
				BuildingConstructionMission constructionMission = (BuildingConstructionMission) mission;
				List<GroundVehicle> constVehicles = constructionMission.getConstructionVehicles();
				if (constVehicles != null)
					if (!constVehicles.isEmpty() || constVehicles.size() > 0) {
						Vehicle vehicle = constVehicles.get(0);
						isVehicle = true;
						vehicleButton.setText(vehicle.getName());
						vehicleButton.setVisible(true);
						vehicleStatusLabel.setText(vehicle.getStatus().getName()); // $NON-NLS-1$
						speedLabel.setText(
								Msg.getString("MainDetailPanel.kmhSpeed", formatter.format(vehicle.getSpeed()))); //$NON-NLS-1$
						distanceNextNavLabel.setText(Msg.getString("MainDetailPanel.kmNextNavPoint", "0")); //$NON-NLS-1$ //$NON-NLS-2$
						traveledLabel.setText(Msg.getString("MainDetailPanel.kmTraveled", "0", "0")); //$NON-NLS-1$ //$NON-NLS-2$
						vehicle.addUnitListener(this);
						currentVehicle = vehicle;
					}
			} else if (mission instanceof BuildingSalvageMission) {
				// Display first of mission's list of construction vehicles.
				BuildingSalvageMission salvageMission = (BuildingSalvageMission) mission;
				List<GroundVehicle> constVehicles = salvageMission.getConstructionVehicles();
				if (constVehicles != null)
					if (!constVehicles.isEmpty() || constVehicles.size() > 0) {
						Vehicle vehicle = constVehicles.get(0);
						isVehicle = true;
						vehicleButton.setText(vehicle.getName());
						vehicleButton.setVisible(true);
						vehicleStatusLabel.setText(vehicle.getStatus().getName()); // $NON-NLS-1$
						speedLabel.setText(
								Msg.getString("MainDetailPanel.kmhSpeed", formatter.format(vehicle.getSpeed()))); //$NON-NLS-1$
						distanceNextNavLabel.setText(Msg.getString("MainDetailPanel.kmNextNavPoint", "0")); //$NON-NLS-1$ //$NON-NLS-2$
						traveledLabel.setText(Msg.getString("MainDetailPanel.kmTraveled", "0", "0")); //$NON-NLS-1$ //$NON-NLS-2$
						vehicle.addUnitListener(this);
						currentVehicle = vehicle;
					}
			}

			if (!isVehicle) {
				// Clear vehicle info.
				vehicleButton.setVisible(false);
				vehicleStatusLabel.setText(""); //$NON-NLS-1$ //$NON-NLS-2$
				speedLabel.setText(Msg.getString("MainDetailPanel.kmhSpeed", "0")); //$NON-NLS-1$ //$NON-NLS-2$
				distanceNextNavLabel.setText(Msg.getString("MainDetailPanel.kmNextNavPoint", "0")); //$NON-NLS-1$ //$NON-NLS-2$
				traveledLabel.setText(Msg.getString("MainDetailPanel.kmTraveled", "0", "0")); //$NON-NLS-1$ //$NON-NLS-2$
				currentVehicle = null;
			}

			// Add mission listener.
			mission.addMissionListener(this);
			currentMission = mission;
		} else {
			// Clear mission info in UI.
			descriptionTF.setText(""); //$NON-NLS-1$ //$NON-NLS-2$
			typeLabel.setText(""); //$NON-NLS-1$ //$NON-NLS-2$
			phaseLabel.setText(""); //$NON-NLS-1$ //$NON-NLS-2$
			memberNumLabel.setText(""); //$NON-NLS-1$ //$NON-NLS-2$
			memberTableModel.setMission(null);
			centerMapButton.setEnabled(false);
			vehicleButton.setVisible(false);
			vehicleStatusLabel.setText(""); //$NON-NLS-1$ //$NON-NLS-2$
			speedLabel.setText(Msg.getString("MainDetailPanel.kmhSpeed", "0")); //$NON-NLS-1$ //$NON-NLS-2$
			distanceNextNavLabel.setText(Msg.getString("MainDetailPanel.kmNextNavPoint", "0")); //$NON-NLS-1$ //$NON-NLS-2$
			traveledLabel.setText(Msg.getString("MainDetailPanel.kmTraveled", "0", "0")); //$NON-NLS-1$ //$NON-NLS-2$
			currentMission = null;
			currentVehicle = null;
			customPanelLayout.show(missionCustomPane, EMPTY);
		}

		// Update custom mission panel.
		updateCustomPanel(mission);
	}

	/**
	 * Update the custom mission panel with a mission.
	 * 
	 * @param mission the mission.
	 */
	private void updateCustomPanel(Mission mission) {
		boolean hasMissionPanel = false;
		if (mission != null) {
			String missionClassName = mission.getClass().getName();
			if (customInfoPanels.containsKey(missionClassName)) {
				hasMissionPanel = true;
				MissionCustomInfoPanel panel = customInfoPanels.get(missionClassName);
				customPanelLayout.show(missionCustomPane, missionClassName);
				panel.updateMission(mission);
			}
		}

		if (!hasMissionPanel)
			customPanelLayout.show(missionCustomPane, EMPTY);
	}

	/**
	 * Mission event update.
	 */
	public void missionUpdate(MissionEvent e) {
		SwingUtilities.invokeLater(new MissionEventUpdater(e, this));
	}

	/**
	 * Update the custom mission panels with a mission event.
	 * 
	 * @param e the mission event.
	 */
	private void updateCustomPanelMissionEvent(MissionEvent e) {
		Mission mission = (Mission) e.getSource();
		if (mission != null) {
			String missionClassName = mission.getClass().getName();
			if (customInfoPanels.containsKey(missionClassName)) {
				customInfoPanels.get(missionClassName).updateMissionEvent(e);
			}
		}
	}

	/**
	 * Catch unit update event.
	 * 
	 * @param event the unit event.
	 */
	public void unitUpdate(UnitEvent event) {
		if (event.getSource() instanceof Vehicle) {
			SwingUtilities.invokeLater(new VehicleInfoUpdater(event));
		}
	}

	/**
	 * Gets the main desktop.
	 * 
	 * @return desktop.
	 */
	private MainDesktopPane getDesktop() {
		return desktop;
	}

	private class MissionEventUpdater implements Runnable {

		private MissionEvent event;
		private MainDetailPanel panel;

		private MissionEventUpdater(MissionEvent event, MainDetailPanel panel) {
			this.event = event;
			this.panel = panel;
		}

		public void run() {
			Mission mission = (Mission) event.getSource();
			MissionEventType type = event.getType();

			// Update UI based on mission event type.
			if (type == MissionEventType.NAME_EVENT)
				typeLabel.setText(mission.getName()); // $NON-NLS-1$
			else if (type == MissionEventType.DESIGNATION_EVENT) {
				// Implement the missing descriptionLabel
				if (missionWindow.getCreateMissionWizard() != null) {
					String s = missionWindow.getCreateMissionWizard().getTypePanel().getDesignation();// .getDescription();
					if (s == null) {
						s = "None";
					} else {
						s = Conversion.capitalize(s);
					}
					descriptionTF.setText(s);
				} else {
					String s = mission.getDescription();
					if (s == null)
						s = "None";
					descriptionTF.setText(s);
				}
			} else if (type == MissionEventType.PHASE_DESCRIPTION_EVENT) {
				String phaseText = mission.getPhaseDescription();
				if (phaseText.length() > 45)
					phaseText = phaseText.substring(0, 45) + "...";
				phaseLabel.setText(phaseText); // $NON-NLS-1$
			} else if (type == MissionEventType.ADD_MEMBER_EVENT || type == MissionEventType.REMOVE_MEMBER_EVENT
					|| type == MissionEventType.MIN_MEMBERS_EVENT || type == MissionEventType.CAPACITY_EVENT) {
				int memberNum = mission.getMembersNumber();
				int minMembers = mission.getMinMembers();
				String maxMembers = ""; //$NON-NLS-1$
				if (mission instanceof VehicleMission) {
					maxMembers = "" + mission.getMissionCapacity(); //$NON-NLS-1$
				} else {
					maxMembers = Msg.getString("MainDetailPanel.unlimited"); //$NON-NLS-1$
				}
				memberNumLabel.setText(Msg.getString("MainDetailPanel.missionMembersMinMax", //$NON-NLS-1$
						memberNum, minMembers, maxMembers));
				memberTableModel.updateMembers();
			} else if (type == MissionEventType.VEHICLE_EVENT) {
				Vehicle vehicle = ((VehicleMission) mission).getVehicle();
				if (vehicle != null) {
					vehicleButton.setText(vehicle.getName());
					vehicleButton.setVisible(true);
					StatusType s = vehicle.getStatus();
					// if (s == null)
					// s = "Not Applicable";
					vehicleStatusLabel.setText(s.getName());
					speedLabel.setText(Msg.getString("MainDetailPanel.kmhSpeed", formatter.format(vehicle.getSpeed()))); //$NON-NLS-1$
					vehicle.addUnitListener(panel);
					currentVehicle = vehicle;
				} else {
					vehicleButton.setVisible(false);
					vehicleStatusLabel.setText("Not Applicable");
					speedLabel.setText(Msg.getString("MainDetailPanel.kmhSpeed", "0")); //$NON-NLS-1$ //$NON-NLS-2$
					if (currentVehicle != null)
						currentVehicle.removeUnitListener(panel);
					currentVehicle = null;
				}
			} else if (type == MissionEventType.DISTANCE_EVENT) {
				VehicleMission vehicleMission = (VehicleMission) mission;
				try {
					int distanceNextNav = (int) vehicleMission.getCurrentLegRemainingDistance();
					distanceNextNavLabel.setText(Msg.getString("MainDetailPanel.kmNextNavPoint", distanceNextNav)); //$NON-NLS-1$
				} catch (Exception e2) {
				}
				int travelledDistance = (int) vehicleMission.getTotalDistanceTravelled();
				int totalDistance = (int) vehicleMission.getTotalDistance();
				traveledLabel.setText(Msg.getString("MainDetailPanel.kmTraveled", travelledDistance, //$NON-NLS-1$
						totalDistance));
			}

			// Update custom mission panel.
			updateCustomPanelMissionEvent(event);
		}
	}

	/**
	 * Inner class for updating vehicle info.
	 */
	private class VehicleInfoUpdater implements Runnable {

		private UnitEvent event;

		private VehicleInfoUpdater(UnitEvent event) {
			this.event = event;
		}

		public void run() {
			// Update vehicle info in UI based on event type.
			UnitEventType type = event.getType();
			Vehicle vehicle = (Vehicle) event.getSource();
			if (type == UnitEventType.STATUS_EVENT) {
				StatusType s = vehicle.getStatus();
				// if (s == null)
				// s = "Not Applicable";
				vehicleStatusLabel.setText(s.getName());
			} else if (type == UnitEventType.SPEED_EVENT)
				speedLabel.setText(Msg.getString("MainDetailPanel.kmhSpeed", formatter.format(vehicle.getSpeed()))); //$NON-NLS-1$
		}
	}

	/**
	 * A custom box container inner class.
	 */
	private static class CustomBox extends Box {

		/**
		 * Constructor
		 */
		private CustomBox() {
			super(BoxLayout.Y_AXIS);
			setBorder(new MarsPanelBorder());
		}

		/**
		 * Gets the maximum size for the component.
		 * 
		 * @return dimension.
		 */
		public Dimension getMaximumSize() {
			Dimension result = getPreferredSize();
			result.width = Short.MAX_VALUE;
			return result;
		}
	}

	/**
	 * Table model for mission members.
	 */
	private class MemberTableModel extends AbstractTableModel implements UnitListener {

		/** default serial id. */
		private static final long serialVersionUID = 1L;

		// Private members.
		private Mission mission;
		private List<MissionMember> members;

		/**
		 * Constructor.
		 */
		private MemberTableModel() {
			mission = null;
			members = new ArrayList<MissionMember>();
		}

		/**
		 * Gets the row count.
		 * 
		 * @return row count.
		 */
		public int getRowCount() {
			return members.size();
		}

		/**
		 * Gets the column count.
		 * 
		 * @return column count.
		 */
		public int getColumnCount() {
			return 2;
		}

		/**
		 * Gets the column name at a given index.
		 * 
		 * @param columnIndex the column's index.
		 * @return the column name.
		 */
		public String getColumnName(int columnIndex) {
			if (columnIndex == 0)
				return Msg.getString("MainDetailPanel.column.name"); //$NON-NLS-1$
			else if (columnIndex == 1)
				return Msg.getString("MainDetailPanel.column.task"); //$NON-NLS-1$
			else
				return Msg.getString("unknown"); //$NON-NLS-1$
		}

		/**
		 * Gets the value at a given row and column.
		 * 
		 * @param row    the table row.
		 * @param column the table column.
		 * @return the value.
		 */
		public Object getValueAt(int row, int column) {
			if (row < members.size()) {
				Object array[] = members.toArray();
				MissionMember member = (MissionMember) array[row];
				if (column == 0)
					return member.getName();
				else
					return member.getTaskDescription();
			} else
				return Msg.getString("unknown"); //$NON-NLS-1$
		}

		/**
		 * Sets the mission for this table model.
		 * 
		 * @param newMission the new mission.
		 */
		void setMission(Mission newMission) {
			this.mission = newMission;
			updateMembers();
		}

		/**
		 * Catch unit update event.
		 * 
		 * @param event the unit event.
		 */
		public void unitUpdate(UnitEvent event) {
			UnitEventType type = event.getType();
			MissionMember member = (MissionMember) event.getSource();
			int index = getIndex(members, member);
			if (type == UnitEventType.NAME_EVENT) {
				SwingUtilities.invokeLater(new MemberTableUpdater(index, 0));
			} else if ((type == UnitEventType.TASK_DESCRIPTION_EVENT) || (type == UnitEventType.TASK_EVENT)
					|| (type == UnitEventType.TASK_ENDED_EVENT) || (type == UnitEventType.TASK_SUBTASK_EVENT)
					|| (type == UnitEventType.TASK_NAME_EVENT)) {
				SwingUtilities.invokeLater(new MemberTableUpdater(index, 1));
			}
		}

		private int getIndex(Collection<?> col, Object obj) {
			int result = -1;
			Object array[] = col.toArray();
			int size = array.length;

			for (int i = 0; i < size; i++) {
				if (array[i].equals(obj)) {
					result = i;
					break;
				}
			}

			return result;
		}

		/**
		 * Update mission members.
		 */
		void updateMembers() {
			if (mission != null) {
				clearMembers();
				members = new ArrayList<MissionMember>(mission.getMembers());
				Collections.sort(members, new Comparator<MissionMember>() {

					@Override
					public int compare(MissionMember o1, MissionMember o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				});
				Iterator<MissionMember> i = members.iterator();
				while (i.hasNext()) {
					MissionMember member = i.next();
					member.addUnitListener(this);
				}
				SwingUtilities.invokeLater(new MemberTableUpdater());
			} else {
				if (members.size() > 0) {
					clearMembers();
					SwingUtilities.invokeLater(new MemberTableUpdater());
				}
			}
		}

		/**
		 * Clear all members from the table.
		 */
		private void clearMembers() {
			if (members != null) {
				Iterator<MissionMember> i = members.iterator();
				while (i.hasNext()) {
					MissionMember member = i.next();
					member.removeUnitListener(this);
				}
				members.clear();
			}
		}

		/**
		 * Gets the mission member at a given index.
		 * 
		 * @param index the index.
		 * @return the mission member.
		 */
		MissionMember getMemberAtIndex(int index) {
			if ((index >= 0) && (index < members.size())) {
				return (MissionMember) members.toArray()[index];
			} else {
				return null;
			}
		}

		/**
		 * Inner class for updating member table.
		 */
		private class MemberTableUpdater implements Runnable {

			private int row;
			private int column;
			private boolean entireData;

			private MemberTableUpdater(int row, int column) {
				this.row = row;
				this.column = column;
				entireData = false;
			}

			private MemberTableUpdater() {
				entireData = true;
			}

			public void run() {
				if (entireData) {
					fireTableDataChanged();
				} else {
					fireTableCellUpdated(row, column);
				}
			}
		}
	}

	public MissionWindow getMissionWindow() {
		return missionWindow;
	}
}