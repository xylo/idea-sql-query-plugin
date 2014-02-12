package com.kiwisoft.utils.gui.progress;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;

/**
 * Specialized UI to paint LookAndFeel defined severity colors.
 *
 * @author Stefan Stiller
 * @version $Revision: 6$, $Date: 22.04.05 08:33:56$
 */
public class ProgressAnimationUI extends ComponentUI
{
	private static final int BLOCK_WIDTH=8;

	public static final String NO_BLOCKS_MODE="NoBlocksMode";

	private final static Color GREEN_COLOR=new Color(0, 200, 0);
	private final static Color YELLOW_COLOR=new Color(240, 160, 0);
	private final static Color RED_COLOR=new Color(200, 0, 0);

	public final static Color BLUE_COLOR=new Color(100, 100, 240);

	private Color baseColor;
	private Color[] colorGradients;

	/**
	 * @noinspection UNUSED_SYMBOL
	 */
	public static ComponentUI createUI(JComponent c)
	{
		return new ProgressAnimationUI();
	}

	public void paint(Graphics g, JComponent c)
	{
		super.paint(g, c);
		paint((ProgressAnimation)c, g);
	}

	protected void paint(ProgressAnimation progressAnimation, Graphics g)
	{
		Dimension size=progressAnimation.getSize();

		if (progressAnimation.isOpaque()) g.setColor(Color.white);
		else g.setColor(progressAnimation.getBackground());
		g.fillRect(0, 0, size.width, size.height);

		switch (progressAnimation.getMode())
		{
			case ProgressAnimation.CONTINUOSLY:
				paintContinuosly(progressAnimation, g);
				break;
			case ProgressAnimation.INCREASING:
				paintIncreasing(progressAnimation, g);
				break;
		}

		if (progressAnimation.isOpaque())
		{
			g.setColor(Color.darkGray);
			g.drawRect(0, 0, size.width-1, size.height-1);
		}
	}

	private void updateColorGradients(ProgressAnimation progressAnimation, int thickness)
	{
		Color barColor=getBarColor(progressAnimation);
		if (colorGradients==null || colorGradients.length!=thickness || !barColor.equals(baseColor))
		{
			baseColor=barColor;
			colorGradients=getColorGradients(barColor.darker(), barColor.brighter(), thickness);
		}
	}

	private void paintIncreasing(ProgressAnimation progressAnimation, Graphics g)
	{
		boolean horizontal=progressAnimation.getOrientation()==SwingConstants.HORIZONTAL;
		Dimension size=progressAnimation.getSize();
		int length=horizontal ? size.width-4 : size.height-4;
		int thickness=horizontal ? size.height-4 : size.width-4;
		updateColorGradients(progressAnimation, thickness);
		int fill=progressAnimation.getMaximum()>0 ? (length*progressAnimation.getProgress())/progressAnimation.getMaximum() : length;
		int pMiddle, pBorder;
		int blockWidth;
		if (Boolean.TRUE.equals(progressAnimation.getClientProperty(NO_BLOCKS_MODE))) blockWidth=fill-1;
		else blockWidth=BLOCK_WIDTH;
		int p=2;
		while (p<=1+fill)
		{
			if (p+blockWidth>1+fill)
			{
				pMiddle=1+fill;
				pBorder=1+fill;
			}
			else
			{
				pMiddle=p+blockWidth;
				pBorder=p+blockWidth-1;
			}
			g.setColor(colorGradients[0]);
			if (horizontal) g.drawLine(p+1, 2, pBorder, 2);
			else g.drawLine(2, size.height-p-2, 2, size.height-pBorder-1);
			for (int i=1; i<thickness-1; i++)
			{
				g.setColor(colorGradients[i]);
				if (horizontal) g.drawLine(p, 2+i, pMiddle, 2+i);
				else g.drawLine(2+i, size.height-p-1, 2+i, size.height-pMiddle-1);
			}
			g.setColor(colorGradients[colorGradients.length-1]);
			if (horizontal) g.drawLine(p+1, 1+colorGradients.length, pBorder, 1+colorGradients.length);
			else g.drawLine(1+colorGradients.length, size.height-p-2, 1+colorGradients.length, size.height-pBorder-1);
			p+=blockWidth+3;
		}
	}

	private void paintContinuosly(ProgressAnimation progressAnimation, Graphics g)
	{
		boolean horizontal=progressAnimation.getOrientation()==SwingConstants.HORIZONTAL;
		Dimension size=progressAnimation.getSize();
		int length=horizontal ? size.width-4 : size.height-4;
		int thickness=horizontal ? size.height-4 : size.width-4;
		updateColorGradients(progressAnimation, thickness);
		int p=progressAnimation.getPosition();
		if (p>length+3*BLOCK_WIDTH) progressAnimation.setPosition(p=0);
		int p2m=2+p;
		int p1m=p2m-3*BLOCK_WIDTH;
		int p1b=p1m+1;
		int p2b=p2m-1;
		p1m=Math.max(2, Math.min(p1m, length+1));
		p1b=Math.max(2, Math.min(p1b, length+1));
		p2m=Math.max(2, Math.min(p2m, length+1));
		p2b=Math.max(2, Math.min(p2b, length+1));
		g.setColor(colorGradients[0]);
		if (horizontal) g.drawLine(p1b, 2, p2b, 2);
		else g.drawLine(2, size.height-p1b-1, 2, size.height-p2b-1);
		for (int i=1; i<thickness-1; i++)
		{
			g.setColor(colorGradients[i]);
			if (horizontal) g.drawLine(p1m, 2+i, p2m, 2+i);
			else g.drawLine(2+i, size.height-p1m-1, 2+i, size.height-p2m-1);
		}
		g.setColor(colorGradients[colorGradients.length-1]);
		if (horizontal) g.drawLine(p1b, 1+colorGradients.length, p2b, 1+colorGradients.length);
		else g.drawLine(1+colorGradients.length, size.height-p1b-1, 1+colorGradients.length, size.height-p2b-1);
	}

	private Color getBarColor(ProgressAnimation progressAnimation)
	{
		Color barColor=progressAnimation.getBarColor();
		if (barColor==null)
		{
			switch (progressAnimation.getSeverity())
			{
				case ProgressAnimation.OK:
					return GREEN_COLOR;
				case ProgressAnimation.WARNING:
					return YELLOW_COLOR;
				case ProgressAnimation.ERROR:
					return RED_COLOR;
				default:
					return Color.BLACK;
			}
		}
		else return barColor;
	}

	private static Color[] getColorGradients(Color color1, Color color2, int steps)
	{
		Color[] colors=new Color[steps];
		int red=color2.getRed()-color1.getRed();
		int blue=color2.getBlue()-color1.getBlue();
		int green=color2.getGreen()-color1.getGreen();
		double maxX=(double)steps/2;
		for (int i=0; i<steps; i++)
		{
			double x=((double)i-maxX)/maxX*0.5;
			double y=(Math.cos(x)-Math.cos(0.5))/(1-Math.cos(0.5))/1.3;
			colors[i]=new Color(Math.min(255, (int)(color1.getRed()+red*y)),
							Math.min(255, (int)(color1.getGreen()+green*y)),
							Math.min(255, (int)(color1.getBlue()+blue*y)));
		}
		return colors;
	}
}
