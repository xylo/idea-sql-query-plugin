/*
 * Copyright (C) 2002-2006 Stefan Stiller
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.kiwisoft.sqlPlugin.settings;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.help.HelpManager;

import com.kiwisoft.sqlPlugin.Icons;
import com.kiwisoft.sqlPlugin.config.CSVExportConfiguration;
import com.kiwisoft.sqlPlugin.config.ExportConfiguration;
import com.kiwisoft.sqlPlugin.config.HTMLExportConfiguration;
import com.kiwisoft.sqlPlugin.config.SQLPluginAppConfig;
import com.kiwisoft.utils.StringUtils;
import com.kiwisoft.utils.gui.IconManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:12:25 $
 */
public class ExportConfigurationDialog extends DialogWrapper
{
    private JTabbedPane pnlTabs;
    private JTextField tfTableStyle;
    private JCheckBox cbAlternateStyles;
	private JCheckBox cbIncludeQuery;
    private JTextField tfInterval;
    private JTextField tfRowStyle2;
    private JTextField tfRowStyle1;
    private JTextField tfHeaderStyle;
    private JTextField tfCellStyle;
    private JComboBox cbxDelimiter;
    private JComboBox cbxTextQualifier;
	private JCheckBox cbForceQualifier;

	public ExportConfigurationDialog(Project project)
	{
		super(project, false);
		setTitle("Export Properties");
		initializeComponents();
		initializeData();
		init();
	}

    public JComponent createCenterPanel()
    {
        return pnlTabs;
    }

    public Action[] createActions()
    {
        return new Action[]{new HelpAction(), new ApplyAction(), new CancelAction()};
    }

    private void initializeData()
    {
        ExportConfiguration config=SQLPluginAppConfig.getInstance().getExportConfiguration();

		HTMLExportConfiguration htmlConfig=config.getHTML();
        tfTableStyle.setText(htmlConfig.getTableStyle());
        tfHeaderStyle.setText(htmlConfig.getHeaderStyle());
        tfRowStyle1.setText(htmlConfig.getRowStyle1());
        tfRowStyle2.setText(htmlConfig.getRowStyle2());
        tfCellStyle.setText(htmlConfig.getCellStyle());
        cbAlternateStyles.setSelected(htmlConfig.isAlternateRows());
        tfInterval.setText(String.valueOf(htmlConfig.getAlternateInterval()));
		cbIncludeQuery.setSelected(htmlConfig.isIncludeQuery());

		CSVExportConfiguration csvConfig=config.getCSV();
		cbForceQualifier.setSelected(csvConfig.isForceQualifier());
        ComboBoxModel comboBoxModel=cbxDelimiter.getModel();
        NamedCharacter selectedCharacter=null;
        for (int i=0;i<comboBoxModel.getSize();i++)
        {
            NamedCharacter character=(NamedCharacter) comboBoxModel.getElementAt(i);
            if (StringUtils.equal(character.getCharacter(), csvConfig.getDelimiter()))
            {
                selectedCharacter=character;
                break;
            }
        }
        if (selectedCharacter!=null) cbxDelimiter.setSelectedItem(selectedCharacter);
        else
        {
            selectedCharacter=new NamedCharacter(csvConfig.getDelimiter(), csvConfig.getDelimiter());
            cbxDelimiter.addItem(selectedCharacter);
            cbxDelimiter.setSelectedItem(selectedCharacter);
        }
        comboBoxModel=cbxTextQualifier.getModel();
        selectedCharacter=null;
        for (int i=0;i<comboBoxModel.getSize();i++)
        {
            NamedCharacter character=(NamedCharacter) comboBoxModel.getElementAt(i);
            if (StringUtils.equal(character.getCharacter(), csvConfig.getTextQualifier()))
            {
                selectedCharacter=character;
                break;
            }
        }
        if (selectedCharacter!=null) cbxTextQualifier.setSelectedItem(selectedCharacter);
        else
        {
            selectedCharacter=new NamedCharacter(csvConfig.getTextQualifier(), csvConfig.getTextQualifier());
            cbxTextQualifier.addItem(selectedCharacter);
            cbxTextQualifier.setSelectedItem(selectedCharacter);
        }
    }

    private boolean applyData()
    {
        Integer htmlChangeRow;
        try
        {
            htmlChangeRow=Integer.valueOf(tfInterval.getText());
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(null, "Value must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
            pnlTabs.setSelectedIndex(0);
            tfInterval.requestFocus();
            return false;
        }
        String delimiter;
        try
        {
            delimiter=((NamedCharacter)cbxDelimiter.getSelectedItem()).getCharacter();
        }
        catch (NullPointerException e)
        {
            JOptionPane.showMessageDialog(null, "No delimiter selected.", "Error", JOptionPane.ERROR_MESSAGE);
            pnlTabs.setSelectedIndex(1);
            cbxDelimiter.requestFocus();
            return false;
        }
        String textQualifier;
        try
        {
            textQualifier=((NamedCharacter)cbxTextQualifier.getSelectedItem()).getCharacter();
        }
        catch (NullPointerException e)
        {
            JOptionPane.showMessageDialog(null, "No text qualifier selected.", "Error", JOptionPane.ERROR_MESSAGE);
            pnlTabs.setSelectedIndex(1);
            cbxTextQualifier.requestFocus();
            return false;
        }

		ExportConfiguration config=SQLPluginAppConfig.getInstance().getExportConfiguration();
		HTMLExportConfiguration htmlConfig=config.getHTML();
        htmlConfig.setTableStyle(tfTableStyle.getText());
        htmlConfig.setHeaderStyle(tfHeaderStyle.getText());
        htmlConfig.setRowStyle1(tfRowStyle1.getText());
        htmlConfig.setRowStyle2(tfRowStyle2.getText());
        htmlConfig.setCellStyle(tfCellStyle.getText());
        htmlConfig.setAlternateRows(cbAlternateStyles.isSelected());
        htmlConfig.setAlternateInterval(htmlChangeRow.intValue());
		htmlConfig.setIncludeQuery(cbIncludeQuery.isSelected());
		CSVExportConfiguration csvConfig=config.getCSV();
        csvConfig.setDelimiter(delimiter);
        csvConfig.setTextQualifier(textQualifier);
		csvConfig.setForceQualifier(cbForceQualifier.isSelected());
        return true;
    }

    private void initializeComponents()
    {
        pnlTabs=new JTabbedPane();
        int tabIndex=0;
        pnlTabs.insertTab("HTML", IconManager.getIcon(Icons.HTML_FILE),
            createHTMLPanel(), null, tabIndex++);
        pnlTabs.insertTab("CSV", IconManager.getIcon(Icons.CSV_FILE),
            createCSVPanel(), null, tabIndex);
    }

    private JPanel createCSVPanel()
    {
        Vector delimiters=new Vector();
        delimiters.add(new NamedCharacter("Comma", ","));
        delimiters.add(new NamedCharacter("Semicolon", ";"));
        delimiters.add(new NamedCharacter("Tabulator", "\t"));
        delimiters.add(new NamedCharacter("Space", " "));
		delimiters.add(new NamedCharacter("Pipe", "|"));

        Vector textQualifiers=new Vector();
        textQualifiers.add(new NamedCharacter("Quotes", "\""));
        textQualifiers.add(new NamedCharacter("Single Quotes", "'"));

        cbxDelimiter=new JComboBox(delimiters);
        cbxTextQualifier=new JComboBox(textQualifiers);
		cbForceQualifier=new JCheckBox();

        JPanel panel=new JPanel(new GridBagLayout());
		panel.putClientProperty("help.topic", "KiwiSQL.csvExportPanel");
        panel.add(new JLabel("Delimiter:"),
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        panel.add(cbxDelimiter,
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        panel.add(new JLabel("Text Qualifier:"),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        panel.add(cbxTextQualifier,
            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        panel.add(new JLabel("Always Use Text Qualifier:"),
            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        panel.add(cbForceQualifier,
            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));


        return panel;
    }

    private JPanel createHTMLPanel()
    {
		tfTableStyle=new JTextField(40);
        tfCellStyle=new JTextField(40);
        tfHeaderStyle=new JTextField(40);
        tfRowStyle1=new JTextField(40);
        tfRowStyle2=new JTextField(40);
		tfInterval=new JTextField(3);
		cbAlternateStyles=new JCheckBox();
		cbIncludeQuery=new JCheckBox();

        JPanel pnlStyles=new JPanel(new GridBagLayout());
        pnlStyles.setBorder(new TitledBorder("Styles"));
        pnlStyles.add(new JLabel("Table:"),
            new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 4, 2, 4), 0, 0));
        pnlStyles.add(tfTableStyle,
            new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
        pnlStyles.add(new JLabel("Table Cells:"),
            new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(7, 4, 2, 4), 0, 0));
        pnlStyles.add(tfCellStyle,
            new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
        pnlStyles.add(new JLabel("Table Header:"),
            new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(7, 4, 2, 4), 0, 0));
        pnlStyles.add(tfHeaderStyle,
            new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
        pnlStyles.add(new JLabel("Table Row #1:"),
            new GridBagConstraints(0, 6, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(7, 4, 2, 4), 0, 0));
        pnlStyles.add(tfRowStyle1,
            new GridBagConstraints(0, 7, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
        pnlStyles.add(new JLabel("Table Row #2:"),
            new GridBagConstraints(0, 8, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(7, 4, 2, 4), 0, 0));
        pnlStyles.add(tfRowStyle2,
            new GridBagConstraints(0, 9, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 4, 4), 0, 0));

        JPanel panel=new JPanel(new GridBagLayout());
		panel.putClientProperty("help.topic", "KiwiSQL.htmlExportPanel");
		panel.add(pnlStyles,
            new GridBagConstraints(0, 0, 4, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 4, 2, 4), 0, 0));
        panel.add(new JLabel("Alternate Row Styles:"),
            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 4, 2), 0, 0));
        panel.add(cbAlternateStyles,
            new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 2, 4, 4), 0, 0));
        panel.add(new JLabel("Interval:"),
            new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(4, 7, 4, 2), 0, 0));
        panel.add(tfInterval,
            new GridBagConstraints(3, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 2, 4, 4), 0, 0));
		panel.add(new JLabel("Include Query:"),
			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 4, 2), 0, 0));
		panel.add(cbIncludeQuery,
			new GridBagConstraints(1, 2, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(4, 2, 4, 4), 0, 0));

        return panel;
    }

    private class ApplyAction extends AbstractAction
    {
        public ApplyAction()
        {
            super("Ok");
	        putValue(DEFAULT_ACTION, Boolean.TRUE);
        }

        public void actionPerformed(ActionEvent e)
        {
            if (applyData()) dispose();
        }
    }

    private class CancelAction extends AbstractAction
    {
        public CancelAction()
        {
            super("Cancel");
        }

        public void actionPerformed(ActionEvent e)
        {
            dispose();
        }
    }

    private class HelpAction extends AbstractAction
    {
        public HelpAction()
        {
            super("Help");
        }

        public void actionPerformed(ActionEvent e)
        {
			JComponent tab=(JComponent)pnlTabs.getSelectedComponent();
			String topic=(String)tab.getClientProperty("help.topic");
			if (topic==null) topic="KiwiSQL.exportSettingsDialog";
			HelpManager.getInstance().invokeHelp(topic);
        }
    }

    private static class NamedCharacter
    {
        private String name;
        private String delimiter;

        public NamedCharacter(String name, String delimiter)
        {
            this.name=name;
            this.delimiter=delimiter;
        }

        public String toString()
        {
            return name;
        }

        public String getCharacter()
        {
            return delimiter;
        }

        public boolean equals(Object o)
        {
            if (this==o) return true;
            if (!(o instanceof NamedCharacter)) return false;

            NamedCharacter namedCharacter=(NamedCharacter) o;

			return !(delimiter!=null ? !delimiter.equals(namedCharacter.delimiter) : namedCharacter.delimiter!=null);
		}

        public int hashCode()
        {
            return (delimiter!=null ? delimiter.hashCode() : 0);
        }
    }
}