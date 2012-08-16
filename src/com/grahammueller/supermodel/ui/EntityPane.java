package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class EntityPane extends JPanel implements ActionListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;
    private static int entityCount = 0;

    private DefaultTableModel entityModel;
    private JScrollPane entityScrollPane;
    private JTable entityTable;
    private int previouslySelectedRow = -1;

    public EntityPane() {
        super(new BorderLayout());
        setName("Entity Pane");
        setPreferredSize(new Dimension(200, 400));

        entityScrollPane = new JScrollPane();
        entityTable = new JTable();
        entityModel = new DefaultTableModel( new String [] { "Entity" }, 0);

        entityTable.setModel(entityModel);
        entityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entityTable.getSelectionModel().addListSelectionListener(this);
        entityScrollPane.setViewportView(entityTable);

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

        add(entityScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEntityName = "NewEntity" + entityCount++;
        entityModel.addRow(new String[] { newEntityName });
        MainWindow.addNewEntityBodyPane(newEntityName);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        // Adjusting indicates mouse still down
        if (e.getValueIsAdjusting()) return;

        int selectedRow = entityTable.getSelectedRow();

        if (selectedRow == previouslySelectedRow) return;

        MainWindow.setSelectedEntityBodyPane((String) entityModel.getValueAt(selectedRow, 0));
    }
}
