package com.grahammueller.supermodel.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBar extends JMenuBar implements ActionListener {
    public MenuBar() {
        super();

        _build = new JMenuItem("Generate Code files", KeyEvent.VK_B);
        _build.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
        _build.addActionListener(this);

        _exit = new JMenuItem("Exit", KeyEvent.VK_Q);
        _exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        _exit.addActionListener(this);


        JMenu menu = new JMenu("Generator");
        menu.setMnemonic(KeyEvent.VK_G);
        menu.add(_build);
        menu.addSeparator();
        menu.add(_exit);

        add(menu);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == _build) {
            MainWindow.generateCodeFiles();
        }
        else if (e.getSource() == _exit) {
            System.exit(0);
        }
    }

    private static final long serialVersionUID = 1L;

    private JMenuItem _build, _exit;
}
