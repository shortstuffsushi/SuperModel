package com.grahammueller.supermodel.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import com.grahammueller.supermodel.gen.sqliteorm.ClassGenerator;
import com.grahammueller.supermodel.gen.sqliteorm.TableGenerator;

public class MenuBar extends JMenuBar implements ActionListener {
    public MenuBar() {
        super();

        _genCode = new JMenuItem("Generate Code files", KeyEvent.VK_G);
        _genCode.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        _genCode.addActionListener(this);

        _genDB = new JMenuItem("Generate Database", KeyEvent.VK_D);
        _genDB.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        _genDB.addActionListener(this);

        _exit = new JMenuItem("Exit", KeyEvent.VK_Q);
        _exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        _exit.addActionListener(this);

        JMenu menu = new JMenu("Generator");
        menu.setMnemonic(KeyEvent.VK_G);
        menu.add(_genCode);
        menu.add(_genDB);
        menu.addSeparator();
        menu.add(_exit);

        add(menu);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == _genCode) {
            generateCodeFiles();
        }
        else if (e.getSource() == _genDB) {
            generateDatabaseFile();
        }
        else if (e.getSource() == _exit) {
            System.exit(0);
        }
    }

    private void generateCodeFiles() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retVal = chooser.showOpenDialog(this);

        if (retVal != JFileChooser.APPROVE_OPTION) { return; }

        File directory = chooser.getSelectedFile();

        try {
            ClassGenerator.generateEntitiesFiles(directory, false);
        }
        catch (Exception e) {
            if (e instanceof IllegalStateException) {
                JOptionPane.showMessageDialog(this, e.getMessage());
                return;
            }

            if (e instanceof IllegalArgumentException) {
                try {
                    if (JOptionPane.showConfirmDialog(this, "Overwrite existing files?") == JOptionPane.OK_OPTION) {
                        ClassGenerator.generateEntitiesFiles(directory, true);
                    }
                }
                catch (Exception inner) {
                    inner.printStackTrace();
                }
            }
        }
    }

    private void generateDatabaseFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int retVal = chooser.showSaveDialog(this);

        if (retVal != JFileChooser.APPROVE_OPTION) { return; }

        File file = chooser.getSelectedFile();

        try {
            TableGenerator.generateTables(file.getParent(), file.getName());
        }
        catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                try {
                    if (JOptionPane.showConfirmDialog(this, "Overwrite existing database file?") == JOptionPane.OK_OPTION) {
                        TableGenerator.generateTables(file.getParent(), file.getName());
                    }
                }
                catch (Exception inner) {
                    inner.printStackTrace();
                }
            }
            else {
                e.printStackTrace();
            }
        }
    }

    private static final long serialVersionUID = 1L;

    private JMenuItem _genCode, _genDB, _exit;
}
