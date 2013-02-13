package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
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
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.entity.EntityManagerListener;
import com.grahammueller.supermodel.entity.Relationship;

public class RelationshipPane extends JPanel  implements ActionListener, ListSelectionListener, PropertyChangeListener, EntityManagerListener {
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
        _relationshipTable.getSelectionModel().addListSelectionListener(this);
        _relationshipTable.addPropertyChangeListener(this);
        _relationshipPane.setViewportView(_relationshipTable);

        setEntityList();

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

        add(_relationshipPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setEntityList() {
        TableColumn typeColumn = _relationshipTable.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox();

        for (String entityName : EntityManager.nameList()) {
            comboBox.addItem(entityName);
        }

        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEntityName = "rltn" + relationshipCount++;

        _storedEntity.addRelationship(newEntityName, null);

        _relationshipModel.addRow(new String[] { newEntityName });
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Adjusting indicates mouse still down
        if (e.getValueIsAdjusting()) { return; }

        int selectedRow = _relationshipTable.getSelectedRow();

        MainWindow.setSelectedEntityBodyPane((String) _relationshipModel.getValueAt(selectedRow, 0));
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("tableCellEditor")) {
            if (_relationshipTable.getSelectedColumn() == 0) {
                int selectedRow = _relationshipTable.getSelectedRow();
                if (_relationshipTable.isEditing()) {
                    _storedName = (String) _relationshipModel.getValueAt(selectedRow, 0);
                }
                else {
                    String newName = (String) _relationshipModel.getValueAt(selectedRow, 0);

                    // Nothing to update
                    if (_storedName.equals(newName)) { return; }

                    // If something goes wrong with the set,
                    // revert to the stored one and report the issue.
                    try {
                        if (!_storedEntity.updateRelationshipName(_storedName, newName)) {
                            JOptionPane.showMessageDialog(this, "Relationship name already in use.");
                            _relationshipModel.setValueAt(_storedName, selectedRow, 0);
                        }
                        else {
                            MainWindow.updateEntityName(_storedName, newName);
                        }
                    }
                    catch (IllegalArgumentException iae) {
                        JOptionPane.showMessageDialog(this, iae.getMessage());
                        _relationshipModel.setValueAt(_storedName, selectedRow, 0);
                    }
                }
            }
        }
    }

    @Override
    public void entityAdded(Entity e) {
        setEntityList();
    }

    @Override
    public void entityUpdated(Entity e, Map<String, Object> updates) {
        if ("name".equals(updates.get("name"))) {
            String oldName = (String) updates.get("old");
            String newName = (String) updates.get("new");

            for (Relationship relationship : _storedEntity.getRelationships()) {
                if (relationship.getValue().equals(oldName)) {
                    relationship.setValue(newName);
                }
            }

            for (int row = 0; row < _relationshipModel.getRowCount(); row++) {
                String relationship = (String) _relationshipModel.getValueAt(row, 1);

                if (relationship.equals(oldName)) {
                    _relationshipModel.setValueAt(newName, row, 0);
                }
            }
        }

        setEntityList();
    }

    @Override
    public void entityRemoved(Entity e) {
        setEntityList();
    }

    private static final long serialVersionUID = 1L;
    private static int relationshipCount = 0;

    private JScrollPane _relationshipPane;
    private JTable _relationshipTable;
    private DefaultTableModel _relationshipModel;
    private String _storedName;
    private Entity _storedEntity;
}
