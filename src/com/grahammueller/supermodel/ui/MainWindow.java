package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.grahammueller.supermodel.entity.Entity;

public class MainWindow extends JFrame {
    public static void main(String args[]) { windowInstance =  new MainWindow(); }

    public MainWindow() {
        super("SuperModel");
        setVisible(true);
        setSize(600, 400);
        setLocation(200, 200);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(new MenuBar());

        EmptyPane emptyPane = new EmptyPane();
        _entityPane = new EntityPane();

        _rootLayout = new CardLayout();
        _rootEntityBodyPane = new JPanel(_rootLayout);
        _rootEntityBodyPane.add(emptyPane, "No Selection");

        add(_entityPane, BorderLayout.WEST);
        add(_rootEntityBodyPane, BorderLayout.CENTER);

        pack();
    }

    public static void addNewEntityBodyPane(Entity entity) {
        // Generate the new entity body pane
        EntityBodyPane newEntityPane = new EntityBodyPane(entity);

        // Add it to our card layout
        windowInstance._rootEntityBodyPane.add(newEntityPane, entity.getName());

        // Set it to the actively displayed item
        setSelectedEntityBodyPane(entity.getName());
    }

    public static void setSelectedEntityBodyPane(String entityName) {
        // Use the root entity body pane's layout to show the selected entity
        windowInstance._rootLayout.show(windowInstance._rootEntityBodyPane, entityName);
    }

    public static void updateEntityName(String oldName, String newName) {
        for (Component c : windowInstance._rootEntityBodyPane.getComponents()) {
            // We check old name again c.name in case
            // the components name is null
            if (oldName.equals(c.getName())) {
                c.setName(newName);
                windowInstance._rootEntityBodyPane.remove(c);
                windowInstance._rootEntityBodyPane.add(c, newName);
            }
        }
        setSelectedEntityBodyPane(newName);
    }

    public static void generateCodeFiles() {
        windowInstance._entityPane.generateCodeFiles();
    }

    private static final long serialVersionUID = 1L;
    private static MainWindow windowInstance;

    private EntityPane _entityPane;
    private JPanel _rootEntityBodyPane;
    private CardLayout _rootLayout;
}
