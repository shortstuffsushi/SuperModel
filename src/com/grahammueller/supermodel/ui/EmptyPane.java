package com.grahammueller.supermodel.ui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class EmptyPane extends JPanel {

    private static final long serialVersionUID = 1L;

    public EmptyPane() {
        super();
        setName("Empty Pane");
        setPreferredSize(new Dimension(400, 400));
        add(new JLabel("No Selection"));
    }
}
