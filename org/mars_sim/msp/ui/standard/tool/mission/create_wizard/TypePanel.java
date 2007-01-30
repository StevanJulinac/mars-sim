package org.mars_sim.msp.ui.standard.tool.mission.create_wizard;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TypePanel extends WizardPanel implements ItemListener {

	private final static String NAME = "Mission Type";
	
	private JComboBox typeSelect;
	private JLabel descriptionInfoLabel;
	private JLabel descriptionLabel;
	private JTextField descriptionField;
	
	TypePanel(CreateMissionWizard wizard) {
		// Use WizardPanel constructor.
		super(wizard);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JLabel typeInfoLabel = new JLabel("Select mission type.");
		typeInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(typeInfoLabel);
		
		JPanel typePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		typePane.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(typePane);
		
		JLabel typeLabel= new JLabel("Type: ");
		typePane.add(typeLabel);
		
		String[] missionTypes = MissionDataBean.getMissionTypes();
		String[] displayMissionTypes = new String[missionTypes.length + 1];
		displayMissionTypes[0] = "";
		for (int x = 0; x < missionTypes.length; x++) displayMissionTypes[x + 1] = missionTypes[x];
		typeSelect = new JComboBox(displayMissionTypes);
		typeSelect.addItemListener(this);
		typePane.add(typeSelect);
		
		add(Box.createVerticalStrut(10));
		
		descriptionInfoLabel = new JLabel("Edit mission description (optional).");
		descriptionInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		descriptionInfoLabel.setEnabled(false);
		add(descriptionInfoLabel);
		
		JPanel descriptionPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		descriptionPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(descriptionPane);
		
		descriptionLabel = new JLabel("Description: ");
		descriptionLabel.setEnabled(false);
		descriptionPane.add(descriptionLabel);
		
		descriptionField = new JTextField(20);
		descriptionField.setEnabled(false);
		descriptionPane.add(descriptionField);
		
		add(Box.createVerticalGlue());
	}
	
	public void itemStateChanged(ItemEvent e) {
		String selectedMission = (String) typeSelect.getSelectedItem();
		descriptionField.setText(MissionDataBean.getMissionDescription(selectedMission));
		boolean enableDescription = (typeSelect.getSelectedIndex() != 0);
		descriptionInfoLabel.setEnabled(enableDescription);
		descriptionLabel.setEnabled(enableDescription);
		descriptionField.setEnabled(enableDescription);
		getWizard().nextButton.setEnabled(enableDescription);
	}
	
	String getPanelName() {
		return NAME;
	}
	
	void commitChanges() {
		getWizard().missionBean.setType((String) typeSelect.getSelectedItem());
		getWizard().missionBean.setDescription(descriptionField.getText());
	}
	
	void clearInfo() {
		// No previous panel to this one.
	}
	
	void updatePanel() {
		// No previous panel to this one.
	}
}