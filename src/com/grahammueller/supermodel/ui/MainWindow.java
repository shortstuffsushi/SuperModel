package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.grahammueller.supermodel.entity.Entity;
import com.grahammueller.supermodel.entity.EntityManager;
import com.grahammueller.supermodel.entity.EntityManagerListener;

public class MainWindow extends JFrame implements EntityManagerListener {
    public static void main(String args[]) { windowInstance =  new MainWindow(); }

    public MainWindow() {
        super("SuperModel");
        setVisible(true);
        setSize(600, 400);
        setLocation(200, 200);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(new MenuBar());

        EntityManager.registerForEntityUpdates(this);

        _entityPane = new EntityPane();

        _rootLayout = new CardLayout();
        _rootEntityBodyPane = new JPanel(_rootLayout);
        _rootEntityBodyPane.add(new EmptyPane(), "No Selection");

        add(_entityPane, BorderLayout.WEST);
        add(_rootEntityBodyPane, BorderLayout.CENTER);

        pack();
    }

    public static void setSelectedEntityBodyPane(String entityName) {
        // Use the root entity body pane's layout to show the selected entity
        windowInstance._rootLayout.show(windowInstance._rootEntityBodyPane, entityName);
    }

    @Override
    public void entityAdded(Entity entity) {
        // Generate the new entity body pane
        EntityBodyPane newEntityPane = new EntityBodyPane(entity);

        // Add it to our card layout
        windowInstance._rootEntityBodyPane.add(newEntityPane, entity.getName());

        // Set it to the actively displayed item
        setSelectedEntityBodyPane(entity.getName());
    }

    @Override
    public void entityUpdated(Entity e, Map<String, Object> updateInfo) {
        if (updateInfo.get("name").equals("name")) {
            String oldName = (String) updateInfo.get("old");
            String newName = (String) updateInfo.get("new");
            for (Component c : _rootEntityBodyPane.getComponents()) {
                // We check old name again c.name in case the components name is null
                if (oldName.equals(c.getName())) {
                    c.setName(newName);
                    _rootEntityBodyPane.remove(c);
                    _rootEntityBodyPane.add(c, newName);
                }
            }
            setSelectedEntityBodyPane(newName);
        }
    }

    @Override
    public void entityRemoved(Entity e) {
        for(Component c : _rootEntityBodyPane.getComponents()) {
            if (e.getName().equals(c.getName())) {
                _rootEntityBodyPane.remove(c);
            }
        }
    }

    private static final long serialVersionUID = 1L;
    private static MainWindow windowInstance;

    private EntityPane _entityPane;
    private JPanel _rootEntityBodyPane;
    private CardLayout _rootLayout;
}
