package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private static MainWindow windowInstance;

    private JPanel rootEntityBodyPane;
    private CardLayout rootLayout;

    // Generate the Main Window
    public static void main(String args[]) { windowInstance =  new MainWindow(); }

    public MainWindow() {
        setTitle("SuperModel");
        setVisible(true);
        setSize(600, 400);
        setLocation(200, 200);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        EmptyPane emptyPane = new EmptyPane();
        EntityPane entityPane = new EntityPane();

        rootLayout = new CardLayout();
        rootEntityBodyPane = new JPanel(rootLayout);
        rootEntityBodyPane.add(emptyPane, "No Selection");

        add(entityPane, BorderLayout.WEST);
        add(rootEntityBodyPane, BorderLayout.CENTER);

        pack();
    }

    public static void addNewEntityBodyPane(String entityName) {
        // Generate the new entity body pane
        EntityBodyPane newEntityPane = new EntityBodyPane(entityName);

        // Add it to our card layout
        windowInstance.rootEntityBodyPane.add(newEntityPane, entityName);

        // Set it to the actively displayed item
        setSelectedEntityBodyPane(entityName);
    }

    public static void setSelectedEntityBodyPane(String entityName) {
        // Use the root entity body pane's layout to show the selected entity
        windowInstance.rootLayout.show(windowInstance.rootEntityBodyPane, entityName);
    }

    public static void updateEntityName(String oldName, String newName) {
        for (Component c : windowInstance.rootEntityBodyPane.getComponents()) {
            // We check oldname again c.name in case
            // the components name is null
            if (oldName.equals(c.getName())) {
                c.setName(newName);
                windowInstance.rootEntityBodyPane.remove(c);
                windowInstance.rootEntityBodyPane.add(c, newName);
            }
        }
        setSelectedEntityBodyPane(newName);
    }
}
