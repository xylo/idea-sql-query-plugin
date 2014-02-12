package com.kiwisoft.sqlPlugin.dataLoad.wizard;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.kiwisoft.wizard.WizardDialog;
import com.kiwisoft.wizard.WizardPane;
import com.kiwisoft.sqlPlugin.dataLoad.DataLoadDescriptor;
import com.kiwisoft.utils.text.SourceTextPane;
import com.kiwisoft.utils.text.SyntaxDefinitionFactory;
import com.kiwisoft.utils.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Stefan1
 * Date: 17.05.2006
 * Time: 23:14:00
 * To change this template use File | Settings | File Templates.
 */
public class ScriptPage extends WizardPane
{
	private SourceTextPane scriptField;

	public ScriptPage(WizardDialog dialog)
	{
		super(dialog);
	}

	public String getTitle()
	{
		return "Script (Bean Shell)";
	}

	protected boolean canGoForward()
	{
		return true;
	}

	protected WizardPane getNextPane()
	{
		String script=scriptField.getText();
		if (!StringUtils.isEmpty(script)) return new ScriptConfirmationPage(getDialog());
		return new TargetColumnsPage(getDialog());
	}

	protected String getHelpTopic()
	{
		return "KiwiSQL.dataLoad.script";
	}

	public JComponent createComponent()
	{
		scriptField=new SourceTextPane(SyntaxDefinitionFactory.getInstance().getSyntaxDefinition("bsh"));
		scriptField.setToolTipText("<html><b>SQL Plugin Specific Bean Shell Commands:</b><br>" +
								   "<u>Accessing Row Data</u><br>" +
								   "<dl>" +
								   "<dt>Object row.get(String)<dd>Gets the row value in the given column." +
								   "<dt>row.set(String, Object)<dd>Sets the row value in the given column to the specified value." +
								   "</dl>" +
								   "<u>Logging</u><br>" +
								   "<dl>" +
								   "<dt>dataload.info(String)<dd>Writes a info message to the log file." +
								   "<dt>dataload.warning(String)<dd>Writes a warning message to the log file." +
								   "<dt>dataload.error(String)<dd>Writes an error message to the log file and reject the row." +
								   "</dl>" +
								   "</html>");
		return new JScrollPane(scriptField);
	}

	public void initData()
	{
		super.initData();
		DataLoadDescriptor descriptor=((DataLoadWizard)getDialog()).getDescriptor();
		scriptField.setText(descriptor.getScript());
	}

	public void saveData()
	{
		DataLoadDescriptor descriptor=((DataLoadWizard)getDialog()).getDescriptor();
		descriptor.setScript(scriptField.getText());
		super.saveData();
	}
}
