package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class EntityBodyPane extends JPanel {

    private static final long serialVersionUID = 1L;

    private JScrollPane attrScrollPane;
    private JTable attributeTable;
    private JLabel titleLabel;

    public EntityBodyPane(String entityName) {
        super(new BorderLayout());
        setName(entityName);
        setPreferredSize(new Dimension(400, 400));
        attrScrollPane = new JScrollPane();
        attributeTable = new JTable();

        attributeTable.setModel(new DefaultTableModel( new String [] { "Attribute", "Type" }, 0));
        attrScrollPane.setViewportView(attributeTable);

        JButton addButton = new JButton("+");
        JButton removeButton = new JButton("-");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 15));
        buttonPanel.setPreferredSize(new Dimension(600, 40));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        // Add them dummies
        for (int i = 1; i < 13; i++) {
            buttonPanel.add(new JPanel());
        }

        titleLabel = new JLabel(entityName);
        JPanel selectionPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectionPane.add(titleLabel);
        selectionPane.setPreferredSize(new Dimension(400, 20));

        add(selectionPane, BorderLayout.NORTH);
        add(attrScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateTitle(String updatedName) {
        titleLabel.setText(updatedName);
        setName(updatedName);
        repaint();
    }
}
