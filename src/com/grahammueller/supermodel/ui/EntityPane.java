package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
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
import com.grahammueller.supermodel.gen.Generator;

public class EntityPane extends JPanel implements ActionListener, ListSelectionListener, PropertyChangeListener {
    public EntityPane() {
        super(new BorderLayout());
        setName("Entity Pane");
        setPreferredSize(new Dimension(200, 400));

        _entities = new ArrayList<Entity>();

        _entityScrollPane = new JScrollPane();
        _entityTable = new JTable();
        _entityModel = new DefaultTableModel( new String [] { "Entity" }, 0);

        _entityTable.setModel(_entityModel);
        _entityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _entityTable.getSelectionModel().addListSelectionListener(this);
        _entityTable.addPropertyChangeListener(this);
        _entityScrollPane.setViewportView(_entityTable);

        JButton addButton = new JButton("+");
        addButton.addActionListener(this);
        JButton removeButton = new JButton("-");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
        buttonPanel.setPreferredSize(new Dimension(200, 40));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        // Add them dummies
        for (int i = 1; i < 3; i++) {
            buttonPanel.add(new JPanel());
        }

        add(_entityScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEntityName = "NewEntity" + entityCount++;

        Entity newEntity = new Entity(newEntityName);
        _entities.add(newEntity);

        _entityModel.addRow(new String[] { newEntityName });

        MainWindow.addNewEntityBodyPane(newEntity);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Adjusting indicates mouse still down
        if (e.getValueIsAdjusting()) return;

        int selectedRow = _entityTable.getSelectedRow();

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
                    if (!_entities.get(selectedRow).setName(newName)) {
                        JOptionPane.showMessageDialog(this, "Entity name already in use.");
                        _entityModel.setValueAt(_storedName, selectedRow, 0);
                    }
                    else {
                        MainWindow.updateEntityName(_storedName, newName);
                    }
                }
                catch (IllegalArgumentException iae) {
                    JOptionPane.showMessageDialog(this, iae.getMessage());
                    _entityModel.setValueAt(_storedName, selectedRow, 0);
                }
            }
        }
    }

    public void generateCodeFiles() {
        try {
            Generator.generateEntitiesFiles(_entities, "/Users/gmueller/Desktop");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static final long serialVersionUID = 1L;
    private static int entityCount = 0;

    private DefaultTableModel _entityModel;
    private JScrollPane _entityScrollPane;
    private JTable _entityTable;
    private String _storedName;
    private List<Entity> _entities;
}
