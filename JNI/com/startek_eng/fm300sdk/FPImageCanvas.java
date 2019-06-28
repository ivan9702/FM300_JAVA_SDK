/*
 * FPImageCanvas.java
 *
 * Created on 2007年10月25日, 下午 3:38
 */
package com.startek_eng.fm300sdk;

import java.awt.image.ColorModel;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
/**
 *
 * @author  liaocl
 */

public class FPImageCanvas extends javax.swing.JPanel//Canvas implements Runnable
{
	protected static short FP_IMGSIZE_Height = 64;
	protected static short FP_IMGSIZE_Width = 64;

	private ColorModel colorModel = null;

	private Image fpImage=null;
	
	public FPImageCanvas()
	{
		super();
        byte gray[] = new byte[256];
		for (int i = 0; i < gray.length; i++)
		{
			gray[i] = (byte) i; 
		}
		colorModel = new java.awt.image.IndexColorModel(8, 256, gray, gray, gray); 
		//byte[] map = new byte[] {(byte)(255),(byte)(0)};
		//colorModel = new java.awt.image.IndexColorModel(1, 2, map, map, map);
	}
	public void drawFPImage(byte[] rawData,int imgWidth, int imgHeight)
	{
		this.setVisible(true);
		java.awt.image.MemoryImageSource mis = new java.awt.image.MemoryImageSource(imgWidth, imgHeight, colorModel, 
													 rawData, 0, imgWidth); 
		fpImage = createImage(mis); 
		this.repaint();
	}
	public synchronized void paint(Graphics g)
	{
		if (fpImage == null)
		{
			g.setColor(new Color(255, 255, 255) );
            //g.setColor(new Color(0, 0, 0) );
			g.fillRect(0,0,this.getWidth(),this.getHeight());
			//g.clearRect(0,0,this.getWidth(),this.getHeight());
		}else
		{
			g.drawImage(fpImage, 0,0,this.getWidth(),this.getHeight(),this);
		}
	}
	public void update(Graphics g)
	{
		paint(g);
	}
	protected synchronized void clear()
	{
		fpImage = null;
		this.repaint();
	}/*
	protected void run()
	{
		this.repaint();
	}*/
	public Dimension getPreferredSize() { 
		return new Dimension(FP_IMGSIZE_Width, FP_IMGSIZE_Height); 
	}
    /*
    public static boolean showDialog(java.awt.Component parent,String title,byte[] bRawData, short nWidth,short nHeight)
    {
        //Create the panel
        FPImageCanvas panel = new FPImageCanvas();
        panel.drawFPImage(bRawData,nWidth,nHeight);
        //Get Parent Frame
        java.awt.Frame parentFrame = null;
        //Initial the Dialog
        if (parent!=null)
            parentFrame = javax.swing.JOptionPane.getFrameForComponent(parent);

        javax.swing.JDialog m_dialog = new javax.swing.JDialog(parentFrame,title,true);

        m_dialog.setSize(400,400);
        m_dialog.setContentPane(panel);
        if (parentFrame!=null)
        {
            if (parentFrame.isVisible())
            {   //Setup the position of the dialog
                java.awt.Point p = parentFrame.getLocationOnScreen();
                java.awt.Dimension d = parentFrame.getSize();
                m_dialog.setLocation((d.width-m_dialog.getSize().width)/2+(int)p.getX(),(d.height - m_dialog.getSize().height)/2+(int)p.getY());
            }else
            {
                java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                java.awt.Dimension windowSize = m_dialog.getSize();

                int windowX = Math.max(0, (screenSize.width  - windowSize.width ) / 2);
                int windowY = Math.max(0, (screenSize.height - windowSize.height) / 2);

                m_dialog.setLocation(windowX, windowY);
            }
        }
        m_dialog.setVisible(true);//Dialog Show will hand-up until execute dialogFPEnroll.setVisible(false) iif create JDialog with modal 'true'
        return true;
    }*/
}
