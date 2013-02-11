package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class AttributePane extends JPanel  implements ActionListener, ListSelectionListener, PropertyChangeListener {
    public AttributePane(Entity entity) {
        super(new BorderLayout());

        setPreferredSize(new Dimension(400, 190));
        _storedEntity = entity;

        _attributePane = new JScrollPane();
        _attributeModel = new DefaultTableModel(new String[] { "Attribute", "Type" }, 0);

        _attributeTable = new JTable();
        _attributeTable.setModel(_attributeModel);
        _attributeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _attributeTable.getSelectionModel().addListSelectionListener(this);
        _attributeTable.addPropertyChangeListener(this);
        _attributePane.setViewportView(_attributeTable);

        JComboBox comboBox = new JComboBox();
        comboBox.addItem(Attribute.Type.TEXT);
        comboBox.addItem(Attribute.Type.INTEGER_PRIMARY_KEY);
        comboBox.addItem(Attribute.Type.NUMERIC);
        comboBox.addItem(Attribute.Type.BLOB);

        // Set the second column to a combo box
        TableColumn typeColumn = _attributeTable.getColumnModel().getColumn(1);
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

        add(_attributePane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEntityName = "attr" + attributeCount++;

        _storedEntity.addAttribute(newEntityName, null);

        _attributeModel.addRow(new String[] { newEntityName });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Adjusting indicates mouse still down
        if (e.getValueIsAdjusting()) { return; }

        int selectedRow = _attributeTable.getSelectedRow();

        MainWindow.setSelectedEntityBodyPane((String) _attributeModel.getValueAt(selectedRow, 0));
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("tableCellEditor")) {
            if (_attributeTable.getSelectedColumn() == 0) {
                int selectedRow = _attributeTable.getSelectedRow();
                if (_attributeTable.isEditing()) {
                    _storedName = (String) _attributeModel.getValueAt(selectedRow, 0);
                }
                else {
                    String newName = (String) _attributeModel.getValueAt(selectedRow, 0);

                    // Nothing to update
                    if (_storedName.equals(newName)) { return; }

                    // If something goes wrong with the set,
                    // revert to the stored one and report the issue.
                    try {
                        if (!_storedEntity.updateAttributeName(_storedName, newName)) {
                            JOptionPane.showMessageDialog(this, "Attribute name already in use.");
                            _attributeModel.setValueAt(_storedName, selectedRow, 0);
                        }
                        else {
                            MainWindow.updateEntityName(_storedName, newName);
                        }
                    }
                    catch (IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this, iae.getMessage());
                        _attributeModel.setValueAt(_storedName, selectedRow, 0);
                    }
                }
            }
        }
    }

    private static final long serialVersionUID = 1L;
    private static int attributeCount = 0;

    private JScrollPane _attributePane;
    private JTable _attributeTable;
    private DefaultTableModel _attributeModel;
    private String _storedName;
    private Entity _storedEntity;
}
