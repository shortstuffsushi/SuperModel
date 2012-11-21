package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.Entity;

public class EntityBodyPane extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {

    private static final long serialVersionUID = 1L;

    private JScrollPane attrScrollPane;
    private JTable attributeTable;
    private DefaultTableModel attributeModel;
    private JLabel titleLabel;
    private int attributeCount = 0;
    private Entity storedEntity;
    private String storedName;

    public EntityBodyPane(Entity entity) {
        super(new BorderLayout());
        
        storedEntity = entity;
        
        setPreferredSize(new Dimension(400, 400));
        attrScrollPane = new JScrollPane();
        attributeTable = new JTable();

        attributeModel = new DefaultTableModel( new String [] { "Attribute", "Type" }, 0);
        attributeTable.setModel(attributeModel);
        attributeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attributeTable.getSelectionModel().addListSelectionListener(this);
        attributeTable.addPropertyChangeListener(this);
        attrScrollPane.setViewportView(attributeTable);

        // Set the second column to a combo box
        TableColumn typeColumn = attributeTable.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(Attribute.Type.TEXT);
        comboBox.addItem(Attribute.Type.INTEGER_PRIMARY_KEY);
        comboBox.addItem(Attribute.Type.NUMERIC);
        comboBox.addItem(Attribute.Type.BLOB);
        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));

        JButton addButton = new JButton("+");
        addButton.addActionListener(this);
        JButton removeButton = new JButton("-");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 10));
        buttonPanel.setPreferredSize(new Dimension(400, 40));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        // Add them dummies
        for (int i = 1; i < 8; i++) {
            buttonPanel.add(new JPanel());
        }

        titleLabel = new JLabel(storedEntity.getName());
        JPanel selectionPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectionPane.add(titleLabel);
        selectionPane.setPreferredSize(new Dimension(400, 20));

        add(selectionPane, BorderLayout.NORTH);
        add(attrScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setName(storedEntity.getName());
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        titleLabel.setText(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEntityName = "attr" + attributeCount++;
        
    	storedEntity.addAttribute(newEntityName, null);
        
        attributeModel.addRow(new String[] { newEntityName });
    }

    public void updateTitle(String updatedName) {
        titleLabel.setText(updatedName);
        setName(updatedName);
        repaint();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Adjusting indicates mouse still down
        if (e.getValueIsAdjusting()) return;

        int selectedRow = attributeTable.getSelectedRow();

        MainWindow.setSelectedEntityBodyPane((String) attributeModel.getValueAt(selectedRow, 0));
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("tableCellEditor")) {
        	if (attributeTable.getSelectedColumn() == 0) {
	            int selectedRow = attributeTable.getSelectedRow();
	            if (attributeTable.isEditing()) {
	                storedName = (String) attributeModel.getValueAt(selectedRow, 0);
	            }
	            else {
	                String newName = (String) attributeModel.getValueAt(selectedRow, 0);
	                
	                // If something goes wrong with the set,
	                // revert to the stored one and report the issue.
	                try {
		                if (!storedEntity.updateAttributeName(storedName, newName)) {
		                	JOptionPane.showMessageDialog(this, "Attribute name already in use.");
		                	attributeModel.setValueAt(storedName, selectedRow, 0);
		                }
		                else {
		                	MainWindow.updateEntityName(storedName, newName);
		                }
	                }
	                catch (IllegalArgumentException iae) {
	                	JOptionPane.showMessageDialog(this, iae.getMessage());
	                	attributeModel.setValueAt(storedName, selectedRow, 0);
	                }
	            }
        	}
        }
    }
}
