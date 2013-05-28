package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import com.grahammueller.supermodel.entity.Attribute;
import com.grahammueller.supermodel.entity.AttributeType;
import com.grahammueller.supermodel.entity.Entity;

public class AttributePane extends JPanel  implements ActionListener, PropertyChangeListener, ItemListener {
    public AttributePane(Entity entity) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(400, 190));

        _storedEntity = entity;

        _attributeModel = new AttributePaneModel(new String[] { "Attribute", "Type", "Primary Key" }, 0);

        _attributeTable = new JTable();
        _attributeTable.setModel(_attributeModel);
        _attributeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _attributeTable.addPropertyChangeListener(this);

        _attributePane = new JScrollPane();
        _attributePane.setViewportView(_attributeTable);

        _addButton = new JButton("+");
        _addButton.addActionListener(this);
        _removeButton = new JButton("-");
        _removeButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 10));
        buttonPanel.setPreferredSize(new Dimension(400, 40));
        buttonPanel.add(_addButton);
        buttonPanel.add(_removeButton);

        setupAttributeTypeSelector();

        // Add them dummies
        for (int i = 1; i < 8; i++) {
            buttonPanel.add(new JPanel());
        }

        add(_attributePane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupAttributeTypeSelector() {
        JComboBox comboBox = new JComboBox();
        comboBox.addItem(AttributeType.BLOB);
        comboBox.addItem(AttributeType.BOOLEAN);
        comboBox.addItem(AttributeType.DATE);
        comboBox.addItem(AttributeType.DOUBLE);
        comboBox.addItem(AttributeType.FLOAT);
        comboBox.addItem(AttributeType.INTEGER);
        comboBox.addItem(AttributeType.LONG);
        comboBox.addItem(AttributeType.STRING);
        comboBox.addItem(AttributeType.UNDEFINED);
        comboBox.addItemListener(this);

        TableColumn typeColumn = _attributeTable.getColumnModel().getColumn(1);
        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(_addButton)) {
            String newEntityName = "attr" + _attributeCount++;

            _storedEntity.addAttribute(newEntityName, AttributeType.UNDEFINED);

            _attributeModel.addRow(new Object[] { newEntityName, AttributeType.UNDEFINED, Boolean.FALSE });

            // Force selection for Combo Box
            int adjustedIndex = _attributeModel.getRowCount() - 1;
            _attributeTable.setRowSelectionInterval(adjustedIndex, adjustedIndex);
        }
        else if (e.getSource().equals(_removeButton)) {
            // If there are not currently any Attributes,
            // then we've nothing to remove.
            if (_attributeModel.getRowCount() == 0) {
                return;
            }

            int selectedRow = _attributeTable.getSelectedRow();
            Attribute attr = _storedEntity.getAttributes().get(selectedRow);

            String warningMessage = String.format("Remove %s?%s", attr.getName(), attr.isPrimaryKey() ? "\n(Note: Removing the PK will result in all Relationships being dropped)" : "");
            if (JOptionPane.showConfirmDialog(this, warningMessage) == JOptionPane.OK_OPTION) {
                _storedEntity.removeAttribute(attr.getName());
                _attributeModel.removeRow(selectedRow);

                if (_attributeModel.getRowCount() > 0) {
                    int newSelection = selectedRow == 0 ? 0 : selectedRow - 1;
                    _attributeTable.setRowSelectionInterval(newSelection, newSelection);
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("tableCellEditor")) {
            int selectedRow = _attributeTable.getSelectedRow();

            if (_attributeTable.isEditing()) {
                _storedName = (String) _attributeModel.getValueAt(selectedRow, 0);
            }
            else {
                if (_attributeTable.getSelectedColumn() == 0) {
                    String newName = (String) _attributeModel.getValueAt(selectedRow, 0);

                    // Nothing to update
                    if (_storedName.equals(newName)) { return; }

                    // If something goes wrong with the set,
                    // revert to the stored one and report the issue.
                    try {
                        _storedEntity.updateAttributeName(_storedName, newName);
                    }
                    catch (IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this, iae.getMessage());
                        _attributeModel.setValueAt(_storedName, selectedRow, 0);
                    }
                }
                else if (_attributeTable.getSelectedColumn() == 1) {
                    String attrName = (String) _attributeModel.getValueAt(selectedRow, 0);
                    AttributeType attrType = (AttributeType) _attributeModel.getValueAt(selectedRow, 1);

                    // Nothing to update
                    if (attrType == _storedType) { return; }

                    try {
                        _storedEntity.updateAttributeType(attrName, attrType);

                        if (attrType != AttributeType.INTEGER && attrType != AttributeType.LONG) {
                            _attributeModel.setValueAt(false, selectedRow, 2);
                        }
                    }
                    catch (IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this, iae.getMessage());
                        _attributeModel.setValueAt(_storedType, selectedRow, 1);
                    }
                }
                else if (_attributeTable.getSelectedColumn() == 2) {
                    String attrName = (String) _attributeModel.getValueAt(selectedRow, 0);
                    boolean isPrimaryKey = (Boolean) _attributeModel.getValueAt(selectedRow, 2);

                    try {
                        _storedEntity.setPrimaryKey(attrName, isPrimaryKey);
                    }
                    catch (IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this, iae.getMessage());
                        _attributeModel.setValueAt(false, selectedRow, 2);
                    }
                }
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            _storedType = (AttributeType) e.getItem();
        }
    }

    private static final long serialVersionUID = 1L;

    private int _attributeCount = 0;
    private JButton _addButton;
    private JButton _removeButton;
    private JScrollPane _attributePane;
    private JTable _attributeTable;
    private DefaultTableModel _attributeModel;
    private String _storedName;
    private AttributeType _storedType;
    private Entity _storedEntity;
}
