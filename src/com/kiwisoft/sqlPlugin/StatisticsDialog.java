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
package com.kiwisoft.sqlPlugin;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.project.Project;

/**
 * @author Stefan Stiller
 * @version $Revision: 1.3 $, $Date: 2006/03/24 18:15:18 $
 */
public class StatisticsDialog extends DialogWrapper
{
	private JTextField tfNumber;
	private JTextField tfArithmetricMean;
	private JTextField tfGeometricMean;
	private JTextField tfStandardDeviation;
	private JTextField tfAverageDeviation;
	private JTextField tfSum;
	private JTextField tfMinimum;
	private JTextField tfMaximum;

	private JPanel pnlContent;

	public StatisticsDialog(Project project, String title)
	{
		super(project, false);
		setTitle(title);
		initializeComponents();
		init();
	}

	public void setNumber(int number)
	{
		tfNumber.setText(Integer.toString(number));
	}

	public void setArithmetricMean(double average)
	{
		tfArithmetricMean.setText(Double.toString(average));
	}

	public void setSum(double sum)
	{
		tfSum.setText(Double.toString(sum));
	}

	public void setStandardDeviation(double deviation)
	{
		tfStandardDeviation.setText(Double.toString(deviation));
	}

	public void setGeometricMean(double geometricMean)
	{
		tfGeometricMean.setText(Double.toString(geometricMean));
	}

	public void setMinimum(double min)
	{
		tfMinimum.setText(Double.toString(min));
	}

	public void setMaximum(double max)
	{
		tfMaximum.setText(Double.toString(max));
	}

	public void setAverageDeviation(double deviation)
	{
		tfAverageDeviation.setText(Double.toString(deviation));
	}

	public Action[] createActions()
	{
		return new Action[]{new CloseAction()};
	}

	public JComponent createCenterPanel()
	{
		return pnlContent;
	}

	private static JTextField createNumberField()
	{
		JTextField textField=new JTextField(20);
		textField.setEditable(false);
		textField.setHorizontalAlignment(JTextField.TRAILING);
		textField.setBackground(Color.white);
		return textField;
	}

	private void initializeComponents()
	{
		tfArithmetricMean=createNumberField();
		tfGeometricMean=createNumberField();
		tfNumber=createNumberField();
		tfStandardDeviation=createNumberField();
		tfAverageDeviation=createNumberField();
		tfSum=createNumberField();
		tfMinimum=createNumberField();
		tfMaximum=createNumberField();

		pnlContent=new JPanel(new GridBagLayout());

		int row=0;
		addField(pnlContent, row++, "Number:", tfNumber);
		addField(pnlContent, row++, "Sum:", tfSum);
		addField(pnlContent, row++, "Minimum:", tfMinimum);
		addField(pnlContent, row++, "Maximum:", tfMaximum);
		addField(pnlContent, row++, "Arithmetric Mean:", tfArithmetricMean);
		addField(pnlContent, row++, "Geometric Mean:", tfGeometricMean);
		addField(pnlContent, row++, "Standard Deviation:", tfStandardDeviation);
		addField(pnlContent, row, "Average Deviation:", tfAverageDeviation);
	}

	private void addField(JPanel pnlContent, int row, String name, JTextField textField)
	{
		pnlContent.add(new JLabel(name),
					   new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		pnlContent.add(textField,
					   new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
	}

	private class CloseAction extends AbstractAction
	{
		public CloseAction()
		{
			super("Close");
			putValue(DEFAULT_ACTION, Boolean.TRUE);
		}

		public void actionPerformed(ActionEvent e)
		{
			dispose();
		}
	}

}