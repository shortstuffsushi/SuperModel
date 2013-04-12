package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;

public class EntityPane extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {
    public EntityPane() {
        super(new BorderLayout());
        setName("Entity Pane");
        setPreferredSize(new Dimension(200, 400));

        _entities = new ArrayList<Entity>();

        _entityScrollPane = new JScrollPane();
        _entityTable = new JTable();
        _entityModel = new DefaultTableModel(new String[] { "Entity" }, 0);

        _entityTable.setModel(_entityModel);
        _entityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _entityTable.getSelectionModel().addListSelectionListener(this);
        _entityTable.addPropertyChangeListener(this);
        _entityScrollPane.setViewportView(_entityTable);

        _addButton = new JButton("+");
        _addButton.addActionListener(this);
        _removeButton = new JButton("-");
        _removeButton.addActionListener(this);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
        buttonPanel.setPreferredSize(new Dimension(200, 40));
        buttonPanel.add(_addButton);
        buttonPanel.add(_removeButton);

        // Add them dummies
        for (int i = 1; i < 3; i++) {
            buttonPanel.add(new JPanel());
        }

        add(_entityScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(_addButton)) {
            String newEntityName = "NewEntity" + _entityCount++;

            Entity newEntity = new Entity(newEntityName);
            _entities.add(newEntity);

            _entityModel.addRow(new String[] { newEntityName });
        }
        else if (e.getSource().equals(_removeButton)) {
            // If there are not currently any Entities,
            // then we've nothing to remove.
            if (_entityModel.getRowCount() == 0) {
                return;
            }

            int selectedRow = _entityTable.getSelectedRow();
            Entity entity = _entities.get(selectedRow);

            if (JOptionPane.showConfirmDialog(this, "Remove " + entity.getName() + "?") == JOptionPane.OK_OPTION) {
                EntityManager.removeEntity(entity);

                _entities.remove(entity);
                _entityModel.removeRow(selectedRow);

                if (_entities.size() > 0) {
                    int newSelection = selectedRow == 0 ? 0 : selectedRow - 1;
                    _entityTable.setRowSelectionInterval(newSelection, newSelection);
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Adjusting indicates mouse still down
        if (e.getValueIsAdjusting()) { return; }

        int selectedRow = _entityTable.getSelectedRow();

        // Fix selection
        if (selectedRow == -1) {
            // If there aren't any Entities, don't select
            if (_entities.size() == 0) {
                return;
            }

            selectedRow = 0;
        }

        MainWindow.setSelectedEntityBodyPane((String) _entityModel.getValueAt(selectedRow, 0));
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals("tableCellEditor")) {
            int selectedRow = _entityTable.getSelectedRow();
            if (_entityTable.isEditing()) {
                _storedName = (String) _entityModel.getValueAt(selectedRow, 0);
            }
            else {
                String newName = (String) _entityModel.getValueAt(selectedRow, 0);

                // If something goes wrong with the set,
                // revert to the stored one and report the issue.
                try {
                    _entities.get(selectedRow).setName(newName);
                }
                catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(this, iae.getMessage());
                    _entityModel.setValueAt(_storedName, selectedRow, 0);
                }
            }
        }
    }

    private static final long serialVersionUID = 1L;

    private int _entityCount;
    private DefaultTableModel _entityModel;
    private JScrollPane _entityScrollPane;
    private JTable _entityTable;
    private JButton _addButton;
    private JButton _removeButton;
    private String _storedName;
    private List<Entity> _entities;
}
