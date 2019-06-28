
import com.startek_eng.fm300sdk.FM300SDKWrapper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.BufferedReader;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.awt.image.MemoryImageSource;



class FM300SDKGUIDemo
{
	private static Image image2;
	private static FM300SDKWrapper s = null;
	private static long hConnect = FM300SDKWrapper.FAIL;
	private static JFrame frame = new JFrame("FM300 Fingerprint Demo");
	private static JDialog frame1 = new JDialog(frame, "FM300 Fingerprint Capture");
	private static JLabel label = new JLabel();
	private static JLabel status = new JLabel("FM 300 Fingerprint Demo\n");
	private static JLabel status1 = new JLabel("Finger Position Status");
	private static JLabel status2 = new JLabel("Finger Density Status");
	
    private static int Snap(FM300SDKWrapper s,long hConnect)
	{
        if (s==null)
        {
            System.out.println("ERROR!! FC320SDKWrapper is not initialed");
            return FM300SDKWrapper.FAIL;
        }
        if (hConnect<0)
        {
            System.out.println("ERROR!! Device not connected");
            return FM300SDKWrapper.FAIL;
        }
		long hFPImage =0;
        long hFPCapture=0;
		int rtn,result =FM300SDKWrapper.FAIL;

        //System.out.println("\nPlease put your finger on the reader...");
		
		status1.setText("\nPlease put your finger on the reader...");
		try
		{
            if( (hFPCapture=s.FP_CreateCaptureHandle(hConnect))<0)
			{
            	status1.setText("ERROR!! FP_CreateSnapHandle() failed!!");
				return FM300SDKWrapper.FAIL;
			}
            if( (hFPImage=s.FP_CreateImageHandle(hConnect,(byte)FM300SDKWrapper.GRAY_IMAGE,FM300SDKWrapper.LARGE))<0)
			{
            	status1.setText("ERROR!! FP_CreateImageHandle() failed!!");
				s.FP_DestroyCaptureHandle(hConnect,hFPCapture);
				return FM300SDKWrapper.FAIL;
			}
			//Please Remove Finger
			while(true)
			{   //check fingerprint is removed
				rtn=s.FP_CheckBlank(hConnect);
				if(rtn!=FM300SDKWrapper.FAIL)
					break;
				status1.setText("Please Remove your finger!!!");
			}//end while(true)
            String status_str,den_str;
			while((result=s.FP_Capture(hConnect,hFPCapture))!=FM300SDKWrapper.OK)//Capture fingerprint 
            {   //Show Image Status
                switch(result & FM300SDKWrapper.U_POSITION_CHECK_MASK)
                {
                    case FM300SDKWrapper.U_POSITION_TOO_LOW:
                    	
                    	status_str="Put your finger higher";
                        break;
                    case FM300SDKWrapper.U_POSITION_TOO_TOP:
                    	status_str="Put your finger lower";
                        break;
                    case FM300SDKWrapper.U_POSITION_TOO_RIGHT:
                    	status_str="Put your finger further to the left";
                        break;
                    case FM300SDKWrapper.U_POSITION_TOO_LEFT:
                    	status_str="Put your finger further to the right";
                        break;
                    case FM300SDKWrapper.U_POSITION_TOO_LOW_RIGHT:
                    	status_str="Put your finger further to the upper left";
                        break;
                    case FM300SDKWrapper.U_POSITION_TOO_LOW_LEFT:
                    	status_str="Put your finger further to the upper right";
                        break;
                    case FM300SDKWrapper.U_POSITION_TOO_TOP_RIGHT:
                    	status_str="Put your finger further to the lower left";
                        break;
                    case FM300SDKWrapper.U_POSITION_TOO_TOP_LEFT:
                    	status_str="Put your finger further to the lower right";
                        break;
                    case FM300SDKWrapper.U_POSITION_OK:
                    	status_str="Position is \nOK";
                        break;
                    case FM300SDKWrapper.U_POSITION_NO_FP:
                    default:
                    	status_str="Make a closer contact with the reader";
                        break;
                } //end switch
                
                switch(result & FM300SDKWrapper.U_DENSITY_CHECK_MASK)
                {
                    case FM300SDKWrapper.U_DENSITY_TOO_DARK:
                    	den_str="\nWipe off excess moisture or put lighter";
                        break;
                    case FM300SDKWrapper.U_DENSITY_TOO_LIGHT:
                    	den_str="\nMoisten your finger or put heavier";
                        break;
                    case FM300SDKWrapper.U_DENSITY_AMBIGUOUS:
                    default:
                    	den_str="\nPlease examine your finger";
                        break;
                }
                status1.setText(status_str);
                status1.paintImmediately(status1.getVisibleRect());
                status2.setText(den_str);
                status2.paintImmediately(status2.getVisibleRect());
                if( (rtn=s.FP_GetImage(hConnect,hFPImage) )!=FM300SDKWrapper.OK)
                {
                	status1.setText("ERROR!! FP_GetImage() failed!!");
                    break;		
                }                  
            } //end wile
			
			//show image
			short[] nWidth_Height = new short[2];
			rtn=s.FP_GetImageDimension(hConnect,nWidth_Height);
			int Width=nWidth_Height[0],Height=nWidth_Height[1]; 
			byte[] bRawData = new byte[nWidth_Height[0]*nWidth_Height[1]];
			rtn=s.FP_GetImageData(hConnect,bRawData,nWidth_Height[0],nWidth_Height[1]);
			image2=s.createimage(bRawData,Width,Height);					
		    label.setIcon(new ImageIcon(image2.getScaledInstance(Width,Height, Image.SCALE_DEFAULT)));
		    status1.paintImmediately(status1.getVisibleRect());
		    
            if(result==FM300SDKWrapper.OK)
            {
                s.FP_SaveImage(hConnect,hFPImage,FM300SDKWrapper.BMP,"Snap.bmp");
                status.setText("FP_Capture() OK [Snap.bmp:"+result+"]");
        		status.paintImmediately(status.getVisibleRect());
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
            if (hFPCapture>FM300SDKWrapper.OK)
				s.FP_DestroyCaptureHandle(hConnect,hFPCapture);
            if (hFPImage>FM300SDKWrapper.OK)
                s.FP_DestroyImageHandle (hConnect, hFPImage);
		}
        return result;
	}
    
    private static byte[] readBytesFromFile(String filePath)
    {
        byte[] result = null;
        try
        {
            java.io.FileInputStream in = new java.io.FileInputStream(filePath);
            byte[] tmpCode = new byte[FM300SDKWrapper.FP_CODE_LENGTH];
            if (in.read(tmpCode)==FM300SDKWrapper.FP_CODE_LENGTH)
                result = tmpCode;
            in.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    private static int saveBytes2File(String filePath,byte[] data)
    {
        int result =FM300SDKWrapper.FAIL ;
        try
        {
            java.io.FileOutputStream out = new java.io.FileOutputStream(filePath);
            out.write(data);
            out.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
    
    private static int Enroll_SampleCode(FM300SDKWrapper s,long hConnect)
	{
        if (s==null)
        {
            status1.setText("ERROR!! FM210SDKWrapper is not initialed");
            return FM300SDKWrapper.FAIL;
        }
        if (hConnect<FM300SDKWrapper.OK)
        {
        	status1.setText("ERROR!! Device not connected");
            return FM300SDKWrapper.FAIL;
        }
		long hFPEnroll = FM300SDKWrapper.FAIL;
        int rtn=FM300SDKWrapper.FAIL;
		int result = FM300SDKWrapper.FAIL;
        byte[] p_code = new byte[FM300SDKWrapper.FP_CODE_LENGTH];
		byte[] fp_code = new byte[FM300SDKWrapper.FP_CODE_LENGTH];
        
		try
		{
            if ( (hFPEnroll = s.FP_CreateEnrollHandle( hConnect,(byte)FM300SDKWrapper.DEFAULT_MODE) )<FM300SDKWrapper.OK) 
            {
                System.out.println("ERROR!! FP_CreateEnrollHandle() failed!");
                return  FM300SDKWrapper.FAIL;					         
            }
            for(int i=0;i<6;i++)
            {
            	status.setText("Enroll " + (i+1) + " capture..");
            	status.paintImmediately(status.getVisibleRect());
                rtn = Snap(s,hConnect);
                
                if(rtn==FM300SDKWrapper.OK)
				{
                	status1.setText("FP_Capture() OK");
					//rtn = s.FP_GetPrimaryCode(hConnect,p_code);
					rtn = s.FP_GetTemplate(hConnect,p_code,1,0);	//1: ISO 19794-2 format
					if(rtn==FM300SDKWrapper.OK)
					{
						status1.setText("FP_GetTemplate() OK");
						//rtn  = s.FP_Enroll(hConnect,hFPEnroll, p_code, fp_code);
						rtn  = s.FP_EnrollEx(hConnect,hFPEnroll, p_code, fp_code,1);
						if(rtn==FM300SDKWrapper.U_CLASS_A || rtn==FM300SDKWrapper.U_CLASS_B)
						{   //save enrolled fingerprint template
							//saveBytes2File("Enroll.dat",fp_code);
							s.FP_SaveISOminutia(hConnect,"Enroll.dat",fp_code);
							status.setText("Enroll OK [Enroll.dat]");
							break;
						}
					}
				}
			}  //end for
            if(rtn!=FM300SDKWrapper.U_CLASS_A && rtn!=FM300SDKWrapper.U_CLASS_B)
            	status.setText("Enroll fail!!");
		}  //end try
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (hFPEnroll>=FM300SDKWrapper.OK)
				s.FP_DestroyEnrollHandle(hConnect,hFPEnroll);
		}
        return result;
	}
    
    private static long Match_SmapleCode(FM300SDKWrapper s,long hConnect)
    {
        if (s==null)
        {
        	status1.setText("ERROR!! FC320SDKWrapper is not initialed");
            return FM300SDKWrapper.FAIL;
        }
        if (hConnect<FM300SDKWrapper.OK)
        {
        	status1.setText("ERROR!! Device not connected");
            return FM300SDKWrapper.FAIL;
        }
        byte[] fp_code = new byte[FM300SDKWrapper.FP_CODE_LENGTH];
        byte[] p_code = new byte[FM300SDKWrapper.FP_CODE_LENGTH];
        long result= FM300SDKWrapper.FAIL;
		int rtn=FM300SDKWrapper.FAIL;
        long score = 0;
        
		try
        {
        //if enrolled file existed
        //fp_code=readBytesFromFile("Enroll.dat");
		rtn=s.FP_LoadISOminutia(hConnect,"Enroll.dat",fp_code);
        //if (fp_code==null)
		if (rtn!=FM300SDKWrapper.OK)
        {
        	status.setText("ERROR!! Fingerprint not enrolled [Read Enroll.dat failed]");
            return FM300SDKWrapper.FAIL;
        }
        
			rtn = Snap(s,hConnect);
            if(rtn==FM300SDKWrapper.OK)
            {
            	status1.setText("FP_Capture() OK");
                //rtn = s.FP_GetPrimaryCode(hConnect,p_code);
                rtn = s.FP_GetTemplate(hConnect,p_code,1,0);
                if (rtn==FM300SDKWrapper.OK)
                {
                    score = s.FP_CodeMatchEx(hConnect,p_code,fp_code,FM300SDKWrapper.SECURITY_C);
                    status.setText("Match score="+score);
					result = score;
                }else
                {
                	status.setText("ERROR!! FP_GetTemplate() failed!!");
                }
            }
        }catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
		}
        return result;       
    }
    
  
	public static void main(String[] args)
	{
		
		
		try
		{
			
			s = FM300SDKWrapper.getInstance();
			if (s==null)
			{
				System.out.println("ERROR!! Failed on getting Instance of FM210SDKWrapper");
			}
			hConnect = s.FP_ConnectCaptureDriver(0);
			if (hConnect>=FM300SDKWrapper.OK)
			{
				System.out.println("Device Connect OK");
			}else
			{
				System.out.println("ERROR!! Device Connect Failed");
				return;
			}
////////////////////////////////////////////////////////
			
			
			
			label.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Fingerprint Image", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N
			label.setPreferredSize(new Dimension(264, 344));
			JPanel centerpanel = new JPanel();
			centerpanel.setPreferredSize(new Dimension(350,100));
			
			status.setPreferredSize(new Dimension(320, 40));
			status.setFont(new Font("Times new roman",1,16));
			status.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
			
			centerpanel.add(status);
			
			
			status1.setPreferredSize(new Dimension(320, 40));
			status1.setFont(new Font("Times new roman",1,12));
			status1.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
			centerpanel.add(status1,BorderLayout.AFTER_LAST_LINE);
			status2.setPreferredSize(new Dimension(320, 40));
			status2.setFont(new Font("Times new roman",1,12));
			status2.setBorder(BorderFactory.createLineBorder(Color.BLACK)); 
			centerpanel.add(status2,BorderLayout.AFTER_LAST_LINE);
			JPanel capturebtn = new JPanel();
			final JButton capture = new JButton("Capture");
			capture.setPreferredSize(new Dimension(100,50));
			capturebtn.add(capture);
			
			capture.addActionListener(
				new java.awt.event.ActionListener() 
				{

					public void actionPerformed(java.awt.event.ActionEvent evt) {
						try {
							
							jButtoncaptureActionPerformed(evt);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					private void jButtoncaptureActionPerformed(ActionEvent evt) throws Exception {
			
					
						status.setText("Capture Start...");
						status.paintImmediately(status.getVisibleRect());
						
						Snap(s,hConnect);
						
						
					}
				}
			);
			
			final JButton enroll = new JButton("Enroll");
			enroll.setPreferredSize(new Dimension(100,50));
			capturebtn.add(enroll);
			enroll.addActionListener(
				new java.awt.event.ActionListener() 
				{
	
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						try {
							
							jButtonenrollActionPerformed(evt);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
	
					private void jButtonenrollActionPerformed(ActionEvent evt) throws Exception {
					
						int result;
		//				CaptureDetails details= new CaptureDetails(image2,fp_captured, NFIQval);
						status.setText("start enroll capture \n");
						
		//				result=ba.close();
					//	frame1.setVisible(false);
						Enroll_SampleCode(s,hConnect);
						status1.paintImmediately(status1.getVisibleRect());
						
		//				callback.onCapture(details);
	
					}
				}
			);
			
			final JButton match = new JButton("Match");
			match.setPreferredSize(new Dimension(100,50));
			capturebtn.add(match);
			match.addActionListener(
				new java.awt.event.ActionListener() 
				{
	
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						try {
							
							jButtonmatchActionPerformed(evt);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
	
					private void jButtonmatchActionPerformed(ActionEvent evt) throws Exception {
					
						int result;
		//				CaptureDetails details= new CaptureDetails(image2,fp_captured, NFIQval);
						status.setText("Start match capture \n");
						status.paintImmediately(status.getVisibleRect());
						
		//				result=ba.close();
					//	frame1.setVisible(false);
						Match_SmapleCode(s,hConnect);
						status1.paintImmediately(status1.getVisibleRect());
						
		//				callback.onCapture(details);
	
					}
				}
			);

			final JButton OK_done = new JButton("Done");
			OK_done.setPreferredSize(new Dimension(100,50));
			capturebtn.add(OK_done);
			OK_done.addActionListener(
				new java.awt.event.ActionListener() 
				{

					public void actionPerformed(java.awt.event.ActionEvent evt) {
						try {
							
							jButtonOK_doneActionPerformed(evt);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					private void jButtonOK_doneActionPerformed(ActionEvent evt) throws Exception {
					
						int result;
		//				CaptureDetails details= new CaptureDetails(image2,fp_captured, NFIQval);
						
		//				result=ba.close();
						frame1.setVisible(false);
		//				callback.onCapture(details);
						if (hConnect>=FM300SDKWrapper.OK)
						{
							s.FP_DisconnectCaptureDriver(hConnect);
							hConnect = FM300SDKWrapper.FAIL;
							System.out.println("Device DisConnect OK1");
						}
						//jLabelBiometric.setIcon(new ImageIcon(image2.getScaledInstance(74,94 , Image.SCALE_DEFAULT)));
						//jLabelBiometricFile.setText(bioCaptures.toString());
						//passfpdetails(biometricPosition, image2);
					}
				}
			);

			
			frame1.add(centerpanel, BorderLayout.EAST);   // Display status 
			frame1.add(label, BorderLayout.WEST);         // Display fingerprint image
			frame1.add(capturebtn, BorderLayout.SOUTH);   // Display capture button
			
			frame1.pack();
			frame1.setBounds(100, 100, 600, 480);
		    frame1.setAlwaysOnTop(true);
			frame1.setResizable(false);
			//frame.setModal(true);
			//frame1.setVisible(true);
			//frame1.toFront();
			//frame1.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

			
			frame1.setModal(true);
			frame1.setVisible(true);				
////////////////////////////////////////////////////////			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if ( s!=null && hConnect>=FM300SDKWrapper.OK)
			{
				s.FP_DisconnectCaptureDriver(hConnect);
				hConnect = FM300SDKWrapper.FAIL;
				System.out.println("Device DisConnect OK2");
			}
			
			System.exit(0);
		}
		
	}
}