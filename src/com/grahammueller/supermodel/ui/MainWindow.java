package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private JScrollPane entityScrollPane;
    private JScrollPane attrScrollPane;
    private JTable entityTable;
    private JTable attributeTable;

    // Generate the Main Window
    public static void main(String args[]) { new MainWindow(); }

    public MainWindow() {
        setTitle("SuperModel");
        setVisible(true);
        setSize(600, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createEntityPane();
        createAttributePane();

        pack();
    }

    private void createEntityPane() {
        entityScrollPane = new JScrollPane();
        entityTable = new JTable();

        entityTable.setModel(new DefaultTableModel( new String [] { "Entity" }, 0));
        entityTable.setBackground(Color.WHITE);
        entityScrollPane.setViewportView(entityTable);
        entityScrollPane.setPreferredSize(new Dimension(200, 400));

        JButton addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((DefaultTableModel)entityTable.getModel()).addRow(new String[] { "NewEntity" });
            }
        });
        JButton removeButton = new JButton("-");
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
        buttonPanel.setPreferredSize(new Dimension(200, 40));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        // Add them dummies
        for (int i = 1; i < 3; i++) {
            buttonPanel.add(new JPanel());
        }

        JPanel entityPane = new JPanel(new BorderLayout());
        entityPane.add(entityScrollPane, BorderLayout.CENTER);
        entityPane.add(buttonPanel, BorderLayout.SOUTH);

        add(entityPane, BorderLayout.WEST);
    }

    private void createAttributePane() {
        attrScrollPane = new JScrollPane();
        attributeTable = new JTable();

        attributeTable.setModel(new DefaultTableModel( new String [] { "Attribute", "Type" }, 0));
        attributeTable.setBackground(Color.WHITE);
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

        JPanel attributePane = new JPanel(new BorderLayout());
        attributePane.add(attrScrollPane, BorderLayout.CENTER);
        attributePane.add(buttonPanel, BorderLayout.SOUTH);

        add(attributePane, BorderLayout.CENTER);
    }
}
