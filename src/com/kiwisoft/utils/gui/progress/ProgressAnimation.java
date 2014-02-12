package com.kiwisoft.utils.gui.progress;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * @author Stefan Stiller
 * @version $Revision: 8$, $Date: 21.04.05 16:40:50$
 */
public class ProgressAnimation extends JComponent
{
	static
	{
		UIManager.put("ProgressAnimationUI", ProgressAnimationUI.class.getName());
	}
	
	public static final int CONTINUOSLY=1;
	public static final int INCREASING=2;

	public static final int OK=0;
	public static final int WARNING=1;
	public static final int ERROR=2;

	private int step=3;
	private int orientation;
	private int mode=CONTINUOSLY;
	private Color barColor;

	private int severity=OK;
	private int progress;
	private int max=100;
	private int position;
	private boolean repaint;

	public ProgressAnimation()
	{
		this(SwingConstants.HORIZONTAL);
	}

	public ProgressAnimation(int orientation)
	{
		setOpaque(true);
		this.orientation=orientation;
		setMinimumSize(new Dimension(15, 15));
		if (orientation==SwingConstants.HORIZONTAL)
			setPreferredSize(new Dimension(200, 15));
		else
			setPreferredSize(new Dimension(30, 50));
		updateUI();
		initalizePosition();
		new RepaintThread().start();
	}

	//+++++++++++++++++++ l&f overrides ++++++++++++++++++++++++++++
	private static final String uiClassID="ProgressAnimationUI";

	/**
	 * Overridden for custom UI
	 */
	public ProgressAnimationUI getUI()
	{
		return (ProgressAnimationUI)ui;
	}

	/**
	 * Overridden for custom UI
	 */
	public void setUI(ProgressAnimationUI ui)
	{
		super.setUI(ui);
	}

	/**
	 * Overridden for custom UI
	 */
	public void updateUI()
	{
		setUI((ProgressAnimationUI)UIManager.getUI(this));
	}

	/**
	 * Overridden for custom UI
	 */
	public String getUIClassID()
	{
		return uiClassID;
	}
	//++++++++++++++++++++++++ end access colors ++++++++++++++++
	public void setMode(int mode)
	{
		this.mode=mode;
		initalizePosition();
	}

	public void resetSeverity()
	{
		severity=OK;
		notifyRepaint();
	}

	public void setSeverity(int severity)
	{
		if (severity>this.severity)
		{
			this.severity=severity;
			notifyRepaint();
		}
	}

	public void setMaximum(int max)
	{
		this.progress=0;
		this.max=max;
		notifyRepaint();
	}

	public int getMaximum()
	{
		return max;
	}

	private void initalizePosition()
	{
		switch (mode)
		{
			case CONTINUOSLY:
				position=0;
				break;
			case INCREASING:
				progress=0;
		}
		notifyRepaint();
	}

	public void setStep(int step)
	{
		this.step=step;
	}

	public void setProgress(int value)
	{
		progress=value;
		notifyRepaint();
	}

	public void increaseProgress()
	{
		increaseProgress(1);
	}

	public void increaseProgress(int increment)
	{
		if (mode==INCREASING)
		{
			progress+=increment;
			notifyRepaint();
		}
	}

	public void updateProgress()
	{
		if (mode==CONTINUOSLY)
		{
			position+=step;
			notifyRepaint();
		}
	}

	public void setPosition(int position)
	{
		this.position=position;
	}

	public int getProgress()
	{
		return progress;
	}

	public int getOrientation()
	{
		return orientation;
	}

	public void notifyRepaint()
	{
		synchronized (this)
		{
			repaint=true;
		}
	}

	public int getMode()
	{
		return mode;
	}

	public int getSeverity()
	{
		return severity;
	}

	public int getPosition()
	{
		return position;
	}

	public Color getBarColor()
	{
		return barColor;
	}

	public void setBarColor(Color barColor)
	{
		this.barColor=barColor;
	}

	private class RepaintThread extends Thread
	{
		public RepaintThread()
		{
			super("ProgressAnimation.RepaintThread");
		}

		public void run()
		{
			while (true)
			{
				if (repaint)
				{
					synchronized (this)
					{
						repaint=false;
						repaint();
					}
				}
				try
				{
					sleep(50);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	}
}
