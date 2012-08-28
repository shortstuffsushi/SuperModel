package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.grahammueller.supermodel.entity.Attribute;

public class EntityBodyPane extends JPanel implements ActionListener{

    private static final long serialVersionUID = 1L;

    private JScrollPane attrScrollPane;
    private JTable attributeTable;
    private DefaultTableModel attributeModel;
    private JLabel titleLabel;
    private int attributeCount = 0;

    public EntityBodyPane(String entityName) {
        super(new BorderLayout());
        setPreferredSize(new Dimension(400, 400));
        attrScrollPane = new JScrollPane();
        attributeTable = new JTable();

        attributeModel = new DefaultTableModel( new String [] { "Attribute", "Type" }, 0);
        attributeTable.setModel(attributeModel);
        attrScrollPane.setViewportView(attributeTable);

        // Set the second column to a combo box
        TableColumn typeColumn = attributeTable.getColumnModel().getColumn(1);
        JComboBox<Attribute.Type> comboBox = new JComboBox<Attribute.Type>();
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

        titleLabel = new JLabel(entityName);
        JPanel selectionPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectionPane.add(titleLabel);
        selectionPane.setPreferredSize(new Dimension(400, 20));

        add(selectionPane, BorderLayout.NORTH);
        add(attrScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setName(entityName);
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        titleLabel.setText(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String newEntityName = "attr" + attributeCount++;
        attributeModel.addRow(new String[] { newEntityName });
    }

    public void updateTitle(String updatedName) {
        titleLabel.setText(updatedName);
        setName(updatedName);
        repaint();
    }
}
