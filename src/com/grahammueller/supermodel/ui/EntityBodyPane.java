package com.grahammueller.supermodel.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.grahammueller.supermodel.entity.Entity;

public class EntityBodyPane extends JPanel {
    public EntityBodyPane(Entity entity) {
        super(new BorderLayout());

        _storedEntity = entity;

        setPreferredSize(new Dimension(400, 400));
        _attributePane = new AttributePane(entity);
        _relationPane = new RelationshipPane(entity);

        _titleLabel = new JLabel(_storedEntity.getName());
        JPanel selectionPane = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectionPane.add(_titleLabel);
        selectionPane.setPreferredSize(new Dimension(400, 20));

        add(selectionPane, BorderLayout.NORTH);
        add(_attributePane, BorderLayout.CENTER);
        add(_relationPane, BorderLayout.SOUTH);

        setName(_storedEntity.getName());
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        _titleLabel.setText(name);
    }

    private static final long serialVersionUID = 1L;

    private AttributePane _attributePane;
    private RelationshipPane _relationPane;
    private JLabel _titleLabel;
    private Entity _storedEntity;
}
