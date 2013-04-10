package com.grahammueller.supermodel.ui;

import javax.swing.table.DefaultTableModel;
import com.grahammueller.supermodel.entity.AttributeType;

/**
 * This class exists to make the check box functionality
 * work correctly for the Primary Key column of Attributes
 */
public class AttributePaneModel extends DefaultTableModel {
    public AttributePaneModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Class getColumnClass(int column) {
      switch (column) {
          case 0 : return String.class;
          case 1 : return AttributeType.class;
          case 2 : return Boolean.class;
      }

      return Object.class;
    }

    private static final long serialVersionUID = 1L;
}
