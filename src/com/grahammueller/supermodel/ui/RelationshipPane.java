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
import java.util.List;
import java.util.Map;
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
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.entity.EntityManagerListener;
import com.grahammueller.supermodel.entity.Relationship;

public class RelationshipPane extends JPanel  implements ActionListener, PropertyChangeListener, ItemListener, EntityManagerListener {
    public RelationshipPane(Entity entity) {
        super(new BorderLayout());

        EntityManager.registerForEntityUpdates(this);

        setPreferredSize(new Dimension(400, 190));
        _storedEntity = entity;
        _relationshipTable = new JTable();

        _relationshipPane = new JScrollPane();
        _relationshipModel = new DefaultTableModel(new String[] { "Relationship", "Destination Class" }, 0);
        _relationshipTable.setModel(_relationshipModel);
        _relationshipTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _relationshipTable.addPropertyChangeListener(this);
        _relationshipPane.setViewportView(_relationshipTable);

        setEntityList();

        _addButton = new JButton("+");
        _addButton.addActionListener(this);
        _removeButton = new JButton("-");
        _removeButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 10));
        buttonPanel.setPreferredSize(new Dimension(400, 40));
        buttonPanel.add(_addButton);
        buttonPanel.add(_removeButton);

        // Add them dummies
        for (int i = 1; i < 8; i++) {
            buttonPanel.add(new JPanel());
        }

        add(_relationshipPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setEntityList() {
        TableColumn typeColumn = _relationshipTable.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox();
        comboBox.addItemListener(this);

        for (Entity entity : EntityManager.getAllEntities()) {
            comboBox.addItem(entity.getName());
        }

        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(_addButton)) {
            String newEntityName = "rltn" + _relationshipCount++;
            Entity defaultedEntity = EntityManager.getAllEntities().get(0);

            try {
                _storedEntity.addRelationship(newEntityName, defaultedEntity);
            }
            catch (IllegalArgumentException iae) {
                JOptionPane.showMessageDialog(this, iae.getMessage());
                return;
            }

            _relationshipModel.addRow(new Object[] { newEntityName, defaultedEntity.getName() });

            // Force selection for Combo Box
            int adjustedIndex = _relationshipModel.getRowCount() - 1;
            _relationshipTable.setRowSelectionInterval(adjustedIndex, adjustedIndex);
        }
        else if (e.getSource().equals(_removeButton)) {
            // If there are not currently any Relationships,
            // then we've nothing to remove.
            if (_relationshipModel.getRowCount() == 0) {
                return;
            }

            int selectedRow = _relationshipTable.getSelectedRow();
            Relationship rltn = _storedEntity.getRelationships().get(selectedRow);

            if (JOptionPane.showConfirmDialog(this, "Remove " + rltn.getName() + "?") == JOptionPane.OK_OPTION) {
                _storedEntity.removeRelationship(rltn.getName());
                _relationshipModel.removeRow(selectedRow);

                if (_relationshipModel.getRowCount() > 0) {
                    int newSelection = selectedRow == 0 ? 0 : selectedRow - 1;
                    _relationshipTable.setRowSelectionInterval(newSelection, newSelection);
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("tableCellEditor")) {
            int selectedRow = _relationshipTable.getSelectedRow();

            if (_relationshipTable.isEditing()) {
                _storedName = (String) _relationshipModel.getValueAt(selectedRow, 0);
            }
            else {
                if (_relationshipTable.getSelectedColumn() == 0) {
                    String newName = (String) _relationshipModel.getValueAt(selectedRow, 0);

                    // Nothing to update
                    if (_storedName.equals(newName)) { return; }

                    // If something goes wrong with the set,
                    // revert to the stored one and report the issue.
                    try {
                        _storedEntity.updateRelationshipName(_storedName, newName);
                    }
                    catch (IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this, iae.getMessage());
                        _relationshipModel.setValueAt(_storedName, selectedRow, 0);
                    }
                }
                else if (_relationshipTable.getSelectedColumn() == 1) {
                    String rltnName = (String) _relationshipModel.getValueAt(selectedRow, 0);
                    Entity rltnType = EntityManager.getEntityByName((String) _relationshipTable.getValueAt(selectedRow, 1));

                    // Nothing to update
                    if (rltnType == _storedType) { return; }

                    try {
                        _storedEntity.updateRelationshipEntity(rltnName, rltnType);
                    }
                    catch (IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this, iae.getMessage());
                        _relationshipModel.setValueAt(_storedType, selectedRow, 1);
                    }
                }
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            _storedType = EntityManager.getEntityByName((String) e.getItem());
        }
    }

    @Override
    public void entityAdded(Entity e) {
        setEntityList();
    }

    @Override
    public void entityUpdated(Entity e, Map<String, Object> updates) {
        if (updates.get("name").equals("name")) {
            String oldName = (String) updates.get("old");
            String newName = (String) updates.get("new");

            for (int row = 0; row < _relationshipModel.getRowCount(); row++) {
                String relationship = (String) _relationshipModel.getValueAt(row, 1);

                if (relationship.equals(oldName)) {
                    _relationshipModel.setValueAt(newName, row, 1);
                }
            }
        }
        else if (updates.get("name").equals("relationships-cleared") && e.equals(_storedEntity)) {
            for (int row = 0; row < _relationshipModel.getRowCount(); row++) {
                _relationshipModel.removeRow(row);
            }
        }

        setEntityList();
    }

    @Override
    public void entityRemoved(Entity e) {
        // Check for discrepancies indicating one of our Relationships was removed
        List<Relationship> relationships = _storedEntity.getRelationships();
        if (_relationshipModel.getRowCount() != relationships.size()) {
            // Reset our table model with the remaining Relationships
            _relationshipModel = new DefaultTableModel(new String[] { "Relationship", "Destination Class" }, 0);

            for (Relationship rltn : relationships) {
               _relationshipModel.addRow(new Object[] { rltn.getName(), rltn.getEntity().getName() });
            }

            _relationshipTable.setModel(_relationshipModel);
        }

        // Update combo box
        setEntityList();
    }

    private static final long serialVersionUID = 1L;

    private int _relationshipCount = 0;
    private JScrollPane _relationshipPane;
    private JTable _relationshipTable;
    private DefaultTableModel _relationshipModel;
    private String _storedName;
    private Entity _storedType;
    private Entity _storedEntity;
    private JButton _addButton;
    private JButton _removeButton;
}
