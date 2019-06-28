package com.startek_eng.fm300sdk;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader ;
/**
 * Fm300SDKWrapper is the class designed for fingerprint capture device Fm300.
 * The following is sample codes for demostrate capture, saveimage process : 
 * <pre>
 * <code>
 * java.awt.Component parent = null;//current window component
 * FM300SDKWrapper s = null;
 * long hConnect = FM300SDKWrapper.FAIL;
 * int rtn,result =FM300SDKWrapper.FAIL;
 * s = FM300SDKWrapper.getInstance();
 * hConnect = s.FP_ConnectCaptureDriver(0);
 * long hFPImage = 0;
 * long hFPCapture=0;		
 * byte[] p_code = new byte[FM300SDKWrapper.FP_CODE_LENGTH];
 * if (hConnect>=FM300SDKWrapper.OK)
 * {
 *	System.out.println("\nPlease put your finger on the reader...");
 *	try{
 *
 *		if( (hFPCapture=s.FP_CreateCaptureHandle(hConnect))<0)
 *		{
 *			System.out.println("ERROR!! FP_CreateSnapHandle() failed!!");
 *			return FM300SDKWrapper.FAIL;
 *		}
 *		if( (hFPImage=s.FP_CreateImageHandle(hConnect,(byte)FM300SDKWrapper.GRAY_IMAGE,FM300SDKWrapper.LARGE))<0)
 *		{
 *			System.out.println("ERROR!! FP_CreateImageHandle() failed!!");
 *			s.FP_DestroyCaptureHandle(hConnect,hFPCapture);
 *			return FM300SDKWrapper.FAIL;
 *		}
 *		while(true)
 *		{   //check fingerprint is removed
 *			rtn=s.FP_CheckBlank(hConnect);
 *			if(rtn!=FM300SDKWrapper.FAIL)
 *				break;
 *			System.out.println("Please Remove your finger!!!");
 *		}//end while(true)
 *	          
 *		while((result=s.FP_Capture(hConnect,hFPCapture))!=FM300SDKWrapper.OK)//Capture fingerprint 
 *		{   //Show Image Status
 *			switch(result & FM300SDKWrapper.U_POSITION_CHECK_MASK)
 *			{
 *				case FM300SDKWrapper.U_POSITION_TOO_LOW:
 *					System.out.println("Put your finger higher");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_TOO_TOP:
 *					System.out.println("Put your finger lower");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_TOO_RIGHT:
 *					System.out.println("Put your finger further to the left");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_TOO_LEFT:
 *					System.out.println("Put your finger further to the right");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_TOO_LOW_RIGHT:
 *					System.out.println("Put your finger further to the upper left");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_TOO_LOW_LEFT:
 *					System.out.println("Put your finger further to the upper right");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_TOO_TOP_RIGHT:
 *					System.out.println("Put your finger further to the lower left");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_TOO_TOP_LEFT:
 *					System.out.println("Put your finger further to the lower right");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_OK:
 *					System.out.println("Position is OK");
 *					break;
 *				case FM300SDKWrapper.U_POSITION_NO_FP:
 *				default:
 *					System.out.println("Make a closer contact with the reader");
 *					break;
 *			} //end switch
 *         
 *			switch(result & FM300SDKWrapper.U_DENSITY_CHECK_MASK)
 *			{
 *				case FM300SDKWrapper.U_DENSITY_TOO_DARK:
 *					System.out.println("Wipe off excess moisture or put lighter");
 *					break;
 *				case FM300SDKWrapper.U_DENSITY_TOO_LIGHT:
 *					System.out.println("Moisten your finger or put heavier");
 *					break;
 *				case FM300SDKWrapper.U_DENSITY_AMBIGUOUS:
 *				default:
 *					System.out.println("Please examine your finger");
 *					break;
 *			}
 *                
 *			if( (rtn=s.FP_GetImage(hConnect,hFPImage) )!=FM300SDKWrapper.OK)
 *			{
 *				System.out.println("ERROR!! FP_GetImage() failed!!");
 *				break;		
 *			}                  
 *		} //end wile
 *		if(result==FM300SDKWrapper.OK){
 *			s.FP_SaveImage(hConnect,hFPImage,FM300SDKWrapper.BMP,"Snap.bmp");
 *			System.out.println("FP_Capture() OK [Snap.bmp:"+result+"]");
 *			rtn = s.FP_GetTemplate(hConnect,p_code,1,0);	//1: ISO 19794-2 format
 *			if(rtn==FM300SDKWrapper.OK)
 *			{
 *				System.out.println("FP_GetTemplate() OK");
 *				saveBytes2File("FP_TEM.dat",p_code);
 *			}
 *		}
 *	}
 *	catch (Exception e)
 *	{
 *		e.printStackTrace();
 *	}
 *	finally
 *	{
 *		if (hFPCapture>FM300SDKWrapper.OK)
 *		s.FP_DestroyCaptureHandle(hConnect,hFPCapture);
 *		if (hFPImage>FM300SDKWrapper.OK)
 *			s.FP_DestroyImageHandle (hConnect, hFPImage);
 *	}
 *	return result;
 * }
 * </code>
 * </pre>
 * @author STARTEK Engineering Inc.
 */
public class FM300SDKWrapper
{
	private FM300SDKWrapper() throws SensorConnectFailedException, LibraryLoadFailedException
	{
		super();
		loadRelatedLibrary();
		try
		{
			FP_DisconnectCaptureDriver(0);
		}
		catch (UnsatisfiedLinkError e)
		{
			throw (new LibraryLoadFailedException(e));
		}
		catch (Exception e)
		{
			throw (new LibraryLoadFailedException(e));
		}
	}
	/** 
     * getInstance get a singlton instance of FM300SDKWrapper
     */
	public static FM300SDKWrapper getInstance() throws SensorConnectFailedException,LibraryLoadFailedException
	{
		if (_instance == null)
		{
			_instance = new FM300SDKWrapper();
		}
		return _instance;
	}
	protected void finalize() throws Throwable
	{
		FP_DisconnectCaptureDriver(0);
		super.finalize();
	}
	private static FM300SDKWrapper _instance = null;
	private static long _h_sensorconnect=-1;
	//private static final String defaultLibarys[] = {"FingerPrinterDll.dll","fm300drv.dll","fm300api.dll","FM300SDKWrapper.dll"};
	//private static final String defaultLibarys[] = {"FingerPrinterDll.dll","fm300drv.dll","fm300api.dll","FM300SDKWrapper.dll"};
	//private static final String defaultLibarys[] = {"fm300drv.dll","fm300api.dll","FM300SDKWrapper.dll"};
	private static final String defaultLibarys[] = {"fpImgQ.dll","fm300drv.dll","fm300api.dll","FM300SDKWrapper.dll"};
    private static File currentDirectory = null;
    /**
     *
     */    
	private static SensorConnectFailedException SCFException = new SensorConnectFailedException("\r\nFailed to connect to STARTEK-Eng supported sensors\r\nPlease plug-in your sensor\r\nIf you have any problem about the sensor! Please contact sales@mail.startek-eng.com");
	
    /**
     * The function works successfully.
     */    
	public static final int    OK                          = 0;
    /**
     * The function works with errors.
     */    
	public static final int    FAIL                        =-1;
    /**
     * The function is canceled
     */    
	public static final int    CALCELLED                   =-1;

//	public static final int        SNAP_TIME_LIMIT         =100;
//	public static final int        SNAP_COUNT              =100;

//	public static final int    LPT1                        = 0;
//	public static final int    LPT2                        = 1;

//	public static final int    PRN_300DPI                  = 0;
//	public static final int    PRN_150DPI                  = 1;
//	public static final int    PRN_100DPI                  = 2;
//	public static final int    PRN_75DPI                   = 3;
//	public static final int    PRN_600DPI                  = 4;

	/*-------------------------------------------*\
	|   Return code of system integrator level    |
	\*-------------------------------------------*/
//	public static final int    S_DOMAIN_ERR                =-1;
//	public static final int    S_RANGE_ERR                 =-2;
    /**
     * Failed to allocate memory.
     */    
	public static final int    S_MEM_ERR                   =-3;
//	public static final int    S_FILE_ERR                  =-4;
//	public static final int    S_COMM_ERR                  =-5;
//	public static final int    S_CHKSUM_ERR                =-6;
//	public static final int    S_TIME_OUT                  =-7;
//	public static final int    S_PRINT_ERR                 =-8;

    /**
     * The driver is found invalid.
     */    
	public static final int    S_KEYCARD_CHECK_FAIL       =-10;
//	public static final int    S_EMPTY_IMAGE_SET          =-11;

    /**
     * The input is invalid.
     */    
	public static final int    S_FPSET_INVALID            =-21;
    /**
     * The input fp_code is illegal.
     */    
	public static final int    S_FPCODE_INVALID           =-22;
    /**
     * The input is valid.
     */    
	public static final int    S_FP_INVALID               =-23;
    /**
     * The input of the security is error.
     */    
	public static final int    S_SECURITY_ERR             =-24;

//	public static final int    S_NOT_SUPPORTED             =-1;
    /**
     * valid flag
     */    
	public static final int    S_VALID                     = 1;
    /**
     * invalid flag
     */    
	public static final int    S_INVALID                  =255;

//	public static final int     IDD_SNAP_MSG1              =412;
//	public static final int     IDD_SNAP_MSG2              =413;

//	public static final int     IDD_SNAP_GROUP             =421;

//	public static final int     IDD_ENRL_RLT               =431;

    /**
     * There is no fingerprint on the fimgerprint device.
     */ 
/*
	public static final int     IDS_POSITION_NO_FP         =501;
	public static final int     IDS_POSITION_TOO_LOW       =502;
	public static final int     IDS_POSITION_TOO_TOP       =503;
	public static final int     IDS_POSITION_TOO_RIGHT     =504;
	public static final int     IDS_POSITION_TOO_LEFT      =505;
	public static final int     IDS_POSITION_TOO_LOW_RIGHT  =506;
	public static final int     IDS_POSITION_TOO_LOW_LEFT   =507;
	public static final int     IDS_POSITION_TOO_TOP_RIGHT  =508;
	public static final int     IDS_POSITION_TOO_TOP_LEFT   =509;
	public static final int     IDS_POSITION_OK             =510;

	public static final int     IDS_DENSITY_TOO_DARK        =511;
	public static final int     IDS_DENSITY_TOO_LIGHT       =512;
	public static final int     IDS_DENSITY_AMBIGUOUS       =513;
	public static final int     IDS_DENSITY_OK              =514;
 */
	/*------------------------------*\
	|   Return code of user level    |
	\*------------------------------*/
    /**
     * The process failed due to the position of the input image which is too left.
     */    
	public static final int    U_LEFT                     =-41;
    /**
     * The process failed due to the position of the input image which is too right.
     */    
	public static final int    U_RIGHT                    =-42;
    /**
     * The process failed due to the position of the input image which is too up.
     */    
	public static final int    U_UP                       =-43;
    /**
     * The process failed due to the position of the input image which is too down.
     */    
	public static final int    U_DOWN                     =-44;

    /**
     * It is used to check the fingerprint position.
     */    
	public static final int    U_POSITION_CHECK_MASK      =0x00002F00;
    /**
     * No fingerprint on the fingerprint device.
     */    
	public static final int    U_POSITION_NO_FP           =0x00002000;
    /**
     * The fingerprint position is too low.
     */    
	public static final int    U_POSITION_TOO_LOW         =0x00000100;
    /**
     * The fingerprint position is too high.
     */    
	public static final int    U_POSITION_TOO_TOP         =0x00000200;
    /**
     * The fingerprint position is too right.
     */    
	public static final int    U_POSITION_TOO_RIGHT       =0x00000400;
    /**
     * The fingerprint position is too left.
     */    
	public static final int    U_POSITION_TOO_LEFT        =0x00000800;
    /**
     * The fingerprint position is too low and too right.
     */    
	public static final int    U_POSITION_TOO_LOW_RIGHT   =(U_POSITION_TOO_LOW|U_POSITION_TOO_RIGHT);
    /**
     * The fingerprint position is too low and too left.
     */    
	public static final int    U_POSITION_TOO_LOW_LEFT    =(U_POSITION_TOO_LOW|U_POSITION_TOO_LEFT);
    /**
     * The fingerprint position is too high and too right.
     */    
	public static final int    U_POSITION_TOO_TOP_RIGHT   =(U_POSITION_TOO_TOP|U_POSITION_TOO_RIGHT);
    /**
     * The fingerprint position is too high and too left.
     */    
	public static final int    U_POSITION_TOO_TOP_LEFT    =(U_POSITION_TOO_TOP|U_POSITION_TOO_LEFT);
    /**
     * The fingerprint position is correctly.
     */    
	public static final int     U_POSITION_OK             =0x00000000;

    /**
     * It is used to check fingerprint density.
     */    
	public static final int    U_DENSITY_CHECK_MASK       =0x000000E0;
    /**
     * The fingerprint density is too dark.
     */    
	public static final int    U_DENSITY_TOO_DARK         =0x00000020;
    /**
     * The fingerprint density is too light.
     */    
	public static final int    U_DENSITY_TOO_LIGHT        =0x00000040;
    /**
     * The fingerprint density is a little light.
     */    
	public static final int    U_DENSITY_LITTLE_LIGHT     =0x00000060;
    /**
     * The fingerprint is ambiguous.
     */    
	public static final int    U_DENSITY_AMBIGUOUS        =0x00000080;

//	public static final int    U_INSUFFICIENT_FP          =-31;
    /**
     * The process is not complet.
     */    
	public static final int    U_NOT_YET                  =-32;

				
    /**
     * This means enrollment of your fingerprint is successful and your fingerprint very clear, has stable features, and is suitable for later verification.
     */    
	public static final int    U_CLASS_A                 =65;
    /**
     * This signifies a successful enrollment. Your fingerprint is clear, has stable features, and is suitable for later verification.
     */    
	public static final int    U_CLASS_B                 =66;
    /**
     * A successful enrollment, indicating your fingerprint is average with enough stable features to make it suitable for later verification.
     */    
	public static final int    U_CLASS_C                 =67;
    /**
     * Although Class D indicates a successful enrollment, your fingerprint may not be very clear, or may not have very good features.
     * In this case, the false rejection rate in identifying this kind of fingerprint may be higher than that for Class A, Class B, or Class C fingerprint.
     */    
	public static final int    U_CLASS_D                 =68;
    /**
     * Class E indicates a marginally successful enrollment, your fingerprint, for some reason, is not clear. Although your fingerprint is suitable for later verification, the false rejection rate for this class of fingerprint is higher than the other fingerprint classes.
     */    
	public static final int    U_CLASS_E                 =69;
    /**
     * Indicates an unsuccessful enrollment, please try again. If the enrollment of this finger fails many times, try to enroll the other fingers. You may return to the snapping function to view the fingerprint images for each of your fingers, and choose the one with the highest quality and try to enroll that finger.
     */    
//	public static final int    U_CLASS_R				 =82;

	/*---------------------------------*\
	|   Definition of security level    |
	\*---------------------------------*/
//	public static final int    AUTO_SECURITY		  =0;
    /**
     * Security A is the safest matching mode.
     */    
	public static final int    SECURITY_A                   =1;
    /**
     * Security B is looser matching mode than security A.
     */    
	public static final int    SECURITY_B                   =2;
    /**
     * Security C is looser matching mode than security B.
     */    
	public static final int    SECURITY_C                   =3;
    /**
     * Security D is looser matching mode than security C.
     */    
	public static final int    SECURITY_D                   =4;
    /**
     * Security D is the loosest matching mode.
     */    
	public static final int    SECURITY_E                   =5;
//	public static final int    SECURITY_M                  =30;
//	public static final int    SECURITY_R                   =40;

//	public static final int    FP_IMAGE_WIDTH             =256;
//	public static final int    FP_IMAGE_HEIGHT            =256;
//	public static final int    GRAY_IMAGE_SIZE        =(((int)FP_IMAGE_WIDTH)*(FP_IMAGE_HEIGHT));
//	public static final int    BIN_IMAGE_SIZE          =(GRAY_IMAGE_SIZE/8);

    /**
     * Set the handel size to large(400x400).
     */    
	public static final int		LARGE                      =10;
    /**
     * Set the handel size to large(200x200).
     */    
	public static final int		SMALL                      =11;
    /**
     * Set the image format to RAW.
     */    
	public static final int    RAW                         =12;
    /**
     * Set the image format to BMP.
     */    
	public static final int    BMP                         =13;
	
    /**
     * Set the image format to ISO 19794-4.
     */ 	
	public static final int    ISO                         =0;

    /**
     * Set the handle modet to gray.
     */    
	public static final int    GRAY_IMAGE                  =8;
    /**
     * Set the handle modet to binary.
     */    
	public static final int    BIN_IMAGE                   =1;

    /**
     * The maximum value in image gray level.
     */    
	public static final int    GRAY_LEVEL                  =256;
    /**
     * Gray_level/256
     */    
	public static final int    GRAY_STEP                   =(256/GRAY_LEVEL);

	/*---------------------------------*\
	|   Definition of enrollment mode   |
	\*---------------------------------*/
    /**
     * The enroll mode which is used in FP_Enroll.
     */    
	public static final int    DEFAULT_MODE              =0x00;
    /**
     * The length of primary code
     */    
	//public static final int    FP_CODE_LENGTH            =256;
	public static final int    FP_CODE_LENGTH            =512;	//for iso m1 format support
	 /**
     * The length of ISO/IEC 19794-4 header.
     */    
	public static final int    ISO_HEADER_LEN            =46;

	
	 /**
     * The width of the image in pixel.
     */    
	public static final int    Width             =256;	
	
	 /**
     * The height of the image in pixel.
     */    
	public static final int    Height            =324;
	
	 /**
     * Gray level image uses 8 bits per pixel.
     */    
	public static final int    Bits_per_Pixel    =8;
	
	
	
	
	
	/*---------------------------------*\
	|   Definition of Sensor Type       |
	\*---------------------------------*/
//	public static final int    SENSORTYPE_AREA             = 0;
//	public static final int    SENSORTYPE_CHIPLINE         = 1;
     /**
      * The FP_ConnectCaptureDriver() connects the capture driver of the fingerprint device.
      * Please connect the capture driver when your program is initialized, and disconnect the
      * capture driver before terminating your program.
      * <BR><BR>PS.This function must be called before the other API is used. Please disconnect the capture
      * driver when program is finished.
      * @return <li> Handle of the driver : if the connection succeeds.
      * <li> NULL : if connection failed.
      * @param reserved this value is now not in use. Please let it be 0
      * @throws Exception Error occurred on calling native library
      */ 
	public synchronized native long FP_ConnectCaptureDriver(int reserved) throws Exception;	//return Device handle
    /**
     * The FP_DisconnectCaptureDriver() disconnects the capture driver of the fingerprint
     * device.
     * @param hConnect The FP_DisconnectCaptureDriver() disconnects the capture driver of the fingerprint
     * device.
     */    
	public synchronized native void FP_DisconnectCaptureDriver (long hConnect);

    /**
     * To snap a fingerprint from the fingerprint device to the main memory by fingerprint
     * image quality control process. The fingerprint quality control cycle needs several frames of
     * images to judge the quality of the fingerprint. This function will return status of the fingerprint
     * after a cycle of quality judgment.
     *  <BR><BR>PS.This function snaps a good-enough fingerprint image from the fingerprint device to the
     * main memory. You should use a while loop to run this function and stop if a valid fingerprint
     * has successfully been grabbed.
     * @param hConnect The FP_DisconnectCaptureDriver() disconnects the capture driver of the fingerprint
     * device.
     * @throws Exception Error occurred on calling native library
     * @return <li> OK : a valid fingerprint has successfully been snapped.
     * <li> S_KEYCARD_CHECK_FAIL : The driver is found invalid.
     * <li> fp_condition : Report the position and moisture condition of the
     * fingerprint.
     * The high-byte of fp_conditon is the moisture information,
     * and the low-byte of fp_condition is the position
     * information. You may use U_DENSITY_CHECK_MASK
     * and U_POSITION_CHECK_MASK to check them by
     * bit-and operation respectively. The action and its
     * meanings is listed below :
     * <BR>( fp_condition & U_POSITION_CHECK_MASK )
     *  <li>U_POSITION_NO_FP no fingerprint on the fingerprint reader.
     * <li> U_POSITION_TOO_LOW fingerprint is too low relative to the center of optical reader.
     *  <li>U_POSITION_TOO_TOP fingerprint is too high relative to the optical reader.
     *  <li>U_POSITION_TOO_RIGHT fingerprint is deviated to the right of the optical reader.
     *  <li>U_POSITION_TOO_LEFT fingerprint is deviated to the left of the optical reader.
     *  <li>U_POSITION_TOO_LOW_RIGHT fingerprint is deviated to the lower right of the optical reader.
     *  <li>U_POSITION_TOO_LOW_LEFT fingerprint is deviated to the lower left of the optical reader.
     *  <li>U_POSITION_TOO_TOP_RIGHT fingerprint is deviated to the upper right of the optical reader.
     *  <li>U_POSITION_TOO_TOP_LEFT fingerprint is deviated to the upper left of the optical reader.
     * <BR><BR>( fp_condition & U_DENSITY_CHECK_MASK )
     * <li>U_DENSITY_TOO_DARK fingerprint is too dark to identify
     *  <li>U_DENSITY_TOO_LIGHT fingerprint is too light to identify
     *  <li>U_DENSITY_AMBIGUOUS fingerprint can not be identified
     */    
	public synchronized native int FP_Snap(long hConnect)throws Exception;
	
    /**
     * Allocate memory and initialize the parameters for snapping.
     *  <BR><BR>PS.This function allocates memory and initializes some parameters for snapping. This
     * function must be called before using FP_Capture to snap a fingerprint image to main
     * memory, otherwise the system will crash. When you want to leave the snap process, you
     * MUST call FP_DestroyCaptureHandle to release the handle.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @throws Exception Error occurred on calling native library
     * @return <li> HFPCAPTURE : If succeeds, the return value is a handle to the allocated
     * memory and the parameters for snapping.
     * <li> NULL : The driver is found invalid.
     */    
	public synchronized native long FP_CreateCaptureHandle(long hConnect)throws Exception;	//return Capture handle
    /**
     * To snap a fingerprint from the fingerprint device to the main memory by a fingerprint
     * image quality control process. The fingerprint quality control cycle needs several frames of
     * images and will continuously return the status of the fingerprint after each frame of image
     * captured.
     *  <BR><BR>PS.This function snaps a fingerprint image from the fingerprint device to the main memory.
     * You should use a while loop to run this function and stop if a valid fingerprint has
     * successfully been grabbed. This function must be used together with
     * FP_CreateCaptureHandle and FP_DestroyCaptureHandle().
     * <BR>The fingerprint device must be connected to the PC and 60K memory is required in run time
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPCapture A handle, which is created by FP_CreateCaptureHandle(),
     * to the allocated memory and the parameters for snapping.
     * @throws Exception Error occurred on calling native library
     * @return <li> OK : a valid fingerprint has successfully been snapped.
     * <li> S_KEYCARD_CHECK_FAIL : The driver is found invalid.
     * <li> fp_condition : Report the position and moisture condition of the
     * fingerprint.
     * The high-byte of fp_conditon is the moisture information,
     * and the low-byte of fp_condition is the position
     * information. You may use U_DENSITY_CHECK_MASK
     * and U_POSITION_CHECK_MASK to check them by
     * bit-and operation respectively. The action and its
     * meanings is listed below :
     * <BR>( fp_condition & U_POSITION_CHECK_MASK )
     *  <li>U_POSITION_NO_FP no fingerprint on the fingerprint reader.
     * <li> U_POSITION_TOO_LOW fingerprint is too low relative to the center of optical reader.
     *  <li>U_POSITION_TOO_TOP fingerprint is too high relative to the optical reader.
     *  <li>U_POSITION_TOO_RIGHT fingerprint is deviated to the right of the optical reader.
     *  <li>U_POSITION_TOO_LEFT fingerprint is deviated to the left of the optical reader.
     *  <li>U_POSITION_TOO_LOW_RIGHT fingerprint is deviated to the lower right of the optical reader.
     *  <li>U_POSITION_TOO_LOW_LEFT fingerprint is deviated to the lower left of the optical reader.
     *  <li>U_POSITION_TOO_TOP_RIGHT fingerprint is deviated to the upper right of the optical reader.
     *  <li>U_POSITION_TOO_TOP_LEFT fingerprint is deviated to the upper left of the optical reader.
     * <BR><BR>( fp_condition & U_DENSITY_CHECK_MASK )
     * <li>U_DENSITY_TOO_DARK fingerprint is too dark to identify
     *  <li>U_DENSITY_TOO_LIGHT fingerprint is too light to identify
     *  <li>U_DENSITY_AMBIGUOUS fingerprint can not be identified
     */    
	public synchronized native int FP_Capture(long hConnect,long hFPCapture)throws Exception;
    /**
     * Release the handle of the allocated memory and the parameters for snapping.
     *  <BR><BR>PS.This function releases the handle created by FP_CreateCaptureHandle(). Make sure to
     * call this function when you want to leave the snap process.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPCapture A handle, which is created by FP_CreateCaptureHandle,
     * to the allocated memory and the parameters for snapping.
     * @return <li> OK : if succeeds.
     * <li> FAIL : The hFPCapture is invalid.
     */    
	public synchronized native int FP_DestroyCaptureHandle(long hConnect,long hFPCapture );

    /**
     * This function converts the fingerprint image in main memory to a 256 bytes primary
     * fingerprint code that can roughly represent the feature of a fingerprint.
     *  <BR><BR>PS.This function converts the fingerprint image in main memory to a 256 bytes primary
     * fingerprint code that can roughly represent the feature of a fingerprint.
     * <li>You should call FP_Snap() or first to snap a fingerprint to the main
     * memory.
     * <li>You should allocate 256 bytes memory for p_code
     * <li>130K run time memory is required for this function.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param p_code The primary code, which is the extracted minutia of the
     * fingerprint from the image of main memory.
     * @throws Exception Error occurred on calling native library
     * @return <li> OK : input image has been processed successfully.
     * <li> S_MEM_ERR : insufficient memory for processing.
     * <li> U_LEFT, U_RIGHT, U_UP, U_DOWN : The process failed due to the
     * bad positioning of the input image.
     * <li> S_KEYCARD_CHECK_FAIL : the driver is invalid.
     */    
	public synchronized native int FP_GetPrimaryCode(long hConnect, byte[] p_code)throws Exception;
	
     /**
     * This function converts the fingerprint image in main memory to a 
     * fingerprint code that represent the feature of a fingerprint.
     *  <BR><BR>PS.This function converts the fingerprint image in main memory to a 512 bytes primary
     * fingerprint code that can roughly represent the feature of a fingerprint.
     * <li>You should call FP_Snap() or first to snap a fingerprint to the main
     * memory.
     * <li>You should allocate 512 bytes memory for minu_code
     * <li>2MB run time memory is required for this function.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param p_code The primary code, which is the extracted minutia of the
     * fingerprint from the image of main memory.
     * @param mode pcode, set 0; ISO-19794-2, please set 1; Ansi M1 378, set 2
     * @param reserved Please set 0
     * @throws Exception Error occurred on calling native library
     * @return <li> OK : input image has been processed successfully.
     * <li> S_MEM_ERR : insufficient memory for processing.
     * <li> U_LEFT, U_RIGHT, U_UP, U_DOWN : The process failed due to the
     * bad positioning of the input image.
     * <li> S_KEYCARD_CHECK_FAIL : the driver is invalid.
     */	
	public synchronized native int FP_GetTemplate(long hConnect, byte[] minu_code, int mode, int reserved)throws Exception;
	
	public synchronized native int FP_GetEncryptedTemplate(long hConnect, byte[] encrypted_minu_code,int mode,int key, byte[] iv, byte[] encrypted_skey)throws Exception;

    /**
     * To allocate memory and initialize the parameters for FP_IMAGE_SET defined in the
     * include files with the specified mode and size.
     *  <BR><BR>PS.This function allocates memory for the desired image. After calling this function, you can
     * call FP_GetImage() to get a binary or gray fingerprint image from main memory depending
     * on the specified mode. You may use the simple supporting functions FP_DisplayImage() to
     * display the image or FP_SaveImage() to save the image as file.
     * <BR><BR>You MUST remember to call FP_DestroyImageHandle() to release the system resources
     * when the handle to FP_IMAGE_SET is no longer in use.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param mode BIN_IMAGE (Binary) or GRAY_IMAGE (gray)
     * @param wSize LARGE (256x256) or SMALL (128x128) in size.
     * @throws Exception Error occurred on calling native library
     * @return <li> HFPIMAGE : If succeeds, return a handle to the FP_IMAGE_SET.
     * <li> NULL : Failed to create.
     */    
	public synchronized native long FP_CreateImageHandle(long hConnect,byte mode,int wSize)throws Exception;//return image handle
    /**
     * Load the fingerprint image from the main memory.
     *  <BR><BR>PS.This function gets a fingerprint image depending on the mode (binary or gray) and the
     * size (large or small) specified in FP_CreateImageHandle(). You may use the simple support
     * functions like FP_DisplayImage() to display the image or FP_SaveImage() to save the
     * image as BMP or RAW files.
     * <BR>Please note:
     * <BR>i.   You should first snap a fingerprint to the main memory.
     * <BR>ii.  You should call FP_CreateImageHandle() to get a handle to the image set.
     * <BR>iii. You should call FP_DestroyImageHandle() to release the handle when
     *         FP_GetImage() is no longer in use.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPImage A handle, created by FP_CreateImageHandle, to FP_IMAGE_SET defined in the include files
     * @throws Exception Error occurred on calling native library
     * @return <li> OK : Get a fingerprint image successfully.
     * <li> S_FP_INVALID : The input handle is not valid or illegal.
     * <li> S_MEM_ERR : Unable to allocate memory while processing.
     * <li> S_KEYCARD_CHECK_FAIL : Driver is found invalid.
     */    
	public synchronized native int FP_GetImage( long hConnect,long hFPImage)throws Exception;
    /**
     * To release the handle, which is created by FP_CreateImageHandle(), to FP_IMAGE_SET defined in the include files.
     *  <BR><BR>PS.Release the handle created by FP_CreateImageHandle. The function MUST be called
     * when FP_GetImage() is no longer in use.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPImage A handle created by FP_CreateImageHandle().
     * @return <li> OK : The handle is released successfully.
     * <li> S_FP_INVALID : The handle is invalid.
     */    
	public synchronized native int FP_DestroyImageHandle(long hConnect,long hFPImage);

    /**
     * To allocate memory and initialize the parameters for FP_ENRL_SET defined in the
     * include files with the specified mode.
     *  <BR><BR>PS.This function creates and initializes a handle for the enrollment set. Only after this
     * function has been called, you can start the enrollment process by calling FP_Enroll(),
     * otherwise the system will crash .Make sure to call FP_DestroyEnrollHandle() to free the
     * system resources when you want to leave the enrollment process.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param mode A reserved value, it must be set as DEFAULT_MODE.
     * @throws Exception Error occurred on calling native library
     * @return <li> HFPENROLL : If succeeds, a handle to FP_ENRL_SET will be returned.
     * <li> NULL : Failed to create HFPENROLL
     */    
	public synchronized native long FP_CreateEnrollHandle(long hConnect,byte mode)throws Exception;//return enroll handle
    /**
     * Generate a final fingerprint code of 256 bytes.
     *  <BR><BR>PS.This function generates the final fingerprint code fp_code from several input p_code by
     * collecting their common features. The purpose of enrollment is to get enough stable
     * characteristics to represent the corresponding fingerprint.
     * <BR><BR>More explanations will be needed for the working process of enrollment. At first, you
     * must call FP_CreateEnrollHandle() to create a pointer to FP_ENRL_SET. Then you should
     * call FP_DestroyEnrollHandle() to release the system resource. Basically, the kernel
     * process of enrollment works in a continuous loop as following:
     * <BR>1. Input a p_code, which is generated by calling FP_Snap() or () and .
     * <BR>2. Send the input p_code to hFPEnroll by calling FP_Enroll()
     * <BR>3. If the return value is not one of the qualities defined, repeat step 1 and
     * step 2 until the quality of the fingerprint is derived.>
     * <BR>4. If you have tried more than 6 times and still cannot derive the quality of
     * the finger, it means that the finger you have chosen to enroll may not be
     * good enough. You should change to another finger and restart the
     * enrollment.
     * <BR>5. If you want to improve the enrolled quality, you can continue executing
     * from step 1 to step 3 to get a better final fingerprint code with better
     * quality. Of course, it depends on the requirements of your application.
     * <BR>6. If you have tried to enhance the enrolled quality more than 3 times but
     * the quality still remains in a certain class without any improvement, it
     * seems that the enrolled quality has been stable. Any attempt to
     * enhancement may be in vain. You should stop the enrollment with the
     * stable enrolled quality. If you are not satisfied with the current enrolled
     * quality, choose another finger and restart the enrollment.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPEnroll A handle, created by FP_CreateEnrollHandle(), to
     * FP_ENRL_SET defined in the include files
     * @param p_code A primary code of 256 bytes derived from
     * FP_GetPrimaryCode().
     * @param fp_code The final fingerprint code to represent the feature of a
     * fingerprint if the enrollment is successful.
     * @throws Exception Error occurred on calling native library
     * @return <li> U_CLASS_A, U_CLASS_B, U_CLASS_C, U_CLASS_D,
     * U_CLASS_E : The quality of enrolled fingerprint.
     * <li> U_NOT_YET : Enrollment is not completed yet.
     * <li> S_FP_INVALID : The p_code is invalid.
     * <li> S_FPSET_INVALID : Invalid input handle.
     */    
	public synchronized native int FP_Enroll(long hConnect,long hFPEnroll,byte[] p_code,byte[] fp_code)throws Exception;

/**
     * Generate a final fingerprint code of 512 bytes with specified format.
     *  <BR><BR>PS.This function generates the final fingerprint code fp_code from several input p_code by
     * collecting their common features. The purpose of enrollment is to get enough stable
     * characteristics to represent the corresponding fingerprint.
     * <BR><BR>More explanations will be needed for the working process of enrollment. At first, you
     * must call FP_CreateEnrollHandle() to create a pointer to FP_ENRL_SET. Then you should
     * call FP_DestroyEnrollHandle() to release the system resource. Basically, the kernel
     * process of enrollment works in a continuous loop as following:
     * <BR>1. Input a p_code, which is generated by calling FP_Snap() or () and .
     * <BR>2. Send the input p_code to hFPEnroll by calling FP_Enroll()
     * <BR>3. If the return value is not one of the qualities defined, repeat step 1 and
     * step 2 until the quality of the fingerprint is derived.>
     * <BR>4. If you have tried more than 6 times and still cannot derive the quality of
     * the finger, it means that the finger you have chosen to enroll may not be
     * good enough. You should change to another finger and restart the
     * enrollment.
     * <BR>5. If you want to improve the enrolled quality, you can continue executing
     * from step 1 to step 3 to get a better final fingerprint code with better
     * quality. Of course, it depends on the requirements of your application.
     * <BR>6. If you have tried to enhance the enrolled quality more than 3 times but
     * the quality still remains in a certain class without any improvement, it
     * seems that the enrolled quality has been stable. Any attempt to
     * enhancement may be in vain. You should stop the enrollment with the
     * stable enrolled quality. If you are not satisfied with the current enrolled
     * quality, choose another finger and restart the enrollment.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPEnroll A handle, created by FP_CreateEnrollHandle(), to
     * FP_ENRL_SET defined in the include files
     * @param p_code A primary code of 256 bytes derived from
     * FP_GetPrimaryCode().
     * @param fp_code The final fingerprint code to represent the feature of a
     * fingerprint if the enrollment is successful.
     * @throws Exception Error occurred on calling native library
     * @return <li> U_CLASS_A, U_CLASS_B, U_CLASS_C, U_CLASS_D,
     * U_CLASS_E : The quality of enrolled fingerprint.
     * <li> U_NOT_YET : Enrollment is not completed yet.
     * <li> S_FP_INVALID : The p_code is invalid.
     * <li> S_FPSET_INVALID : Invalid input handle.
     */    
	public synchronized native int FP_EnrollEx(long hConnect,long hFPEnroll,byte[] p_code,byte[] fp_code, int mode)throws Exception;
	public synchronized native int FP_EnrollEx_Encrypted(long hConnect, long hFPEnroll, byte[] encrypted_fpcode, int mode, byte[] iv, byte[] encrypted_skey)throws Exception;;	
	
    /**
     * To release the handle, which is created by FP_CreateEnrollHandle(), to
     * FP_ENRL_SET defined in the include files.
     *  <BR><BR>PS.This function releases the handle created by FP_CreateEnrollHandle(). Call this
     * function only if FP_Enroll() is no longer in use.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver().
     * @param hFPEnroll A handle, which is created by FP_CreateEnrollHandle(), to
     * FP_ENRL_SET defined in the include files.
     * @return <li> OK : Handle is released successfully.
     * <li> S_FPSET_INVALID : Input handle is invalid.
     */    
	public synchronized native int FP_DestroyEnrollHandle(long hConnect,long hFPEnroll);

    /**
     * To match the fingerprint image in main memory with the final fingerprint code which is
     * generated through enrollment.
     *  <BR><BR>PS.This function verifies the fingerprint image in the main memory with the final fingerprint
     * code generated through the enrollment. The argument security sets the threshold that
     * determines whether this verification can be passed. To call this function, you should first
     * snap a fingerprint to the main memory, and input the fp_code generated by FP_Enroll(), but
     * not by FP_GetPrimaryCode() only. Please note that p_code and fp_code are quite
     * different in their content and cannot be misplaced.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param fp_code The final fingerprint code generated through the enrollment.
     * @param nSecurity A parameter to set the threshold that determines whether the
     * verification can be passed. The 8 security levels with False
     * Acceptance Rate ( FAR ) are ranged from the strictest
     * 1/100,000( Security A ) to the loosest 1/100( Security E ).
     * <LI>SECURITY_A :Verification passes as long as the minutiae matching score is over
     * the threshold. The FAR of security A is 1/100,000.
     * <LI>SECURITY_B :The FAR of security A is 1/10,000.
     * <LI>SECURITY_C :The FAR of security A is 3/10,000.
     * <LI>SECURITY_D :The FAR of security A is 1/1,000.
     * <LI>SECURITY_E :The FAR of security A is 1/100.
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : The verification of fingerprint image with final fingerprint code
     * meets the requirement of security.
     * <LI>FAIL : The fingerprint image is not identical with the final fingerprint
     * code on the required security.
     * <LI>S_MEM_ERR : Insufficient memory for image processing.
     * <LI>S_FPCODE_INVALID : the input fp_code is illegal.
     * <LI>S_SECURITY_ERR : improper security level setting.
     */    
	public synchronized native int FP_ImageMatch(long hConnect,byte[] fp_code,int nSecurity)throws Exception;
    /**
     * Match the input fingerprint code with an existing enrolled fingerprint code.
     *  <BR><BR>PS.This function verifies the p_code generated by FP_GetPrimaryCode() with the final
     * fingerprint code fp_code generated through the enrollment. The argument security sets the
     * threshold that determines whether this verification can be passed. Please note that p_code
     * and fp_code are quite different in their content and cannot be misplaced.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param p_code The fingerprint code generated by using
     * FP_GetPrimaryCode().
     * @param fp_code The final fingerprint code generated through the enrollment.
     * @param nSecurity A parameter to set the threshold that determines whether the
     * verification can be passed. See FP_ImageMatch() for details.
     * @throws Exception Error occurred on calling native library
     * @return <LI> OK : The verification of fingerprint image with final fingerprint code
     * meets the requirement of security.
     * <LI> FAIL : The fingerprint image is not identical with the final fingerprint
     * code on the required security.
     * <LI> S_MEM_ERR : Insufficient memory for image processing.
     * <LI> S_FPCODE_INVALID : The input *fp_code is illegal.
     * <LI> S_SECURITY_ERR : Improper security level setting.
     */    
	public synchronized native int FP_CodeMatch(long hConnect,byte[] p_code,byte[] fp_code,int nSecurity)throws Exception;
	//public synchronized native int FP_ImageMatchEx(long hConnect,byte[] fp_code ,int security,long nScore)throws Exception;
	//public synchronized native int FP_CodeMatchEx(long hConnect,byte[] p_code,byte[] fp_code,int nSecurity,long nScore)throws Exception;//nScore will be a return value
    /**
     * Verify the fingerprint image in the main memory with the final enrolled fingerprint code.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param fp_code The final fingerprint code generated through the enrollment.
     * @param security A parameter to set the threshold that determines whether
     * the verification can be passed. See FP_ImageMatch() for
     * more details.
     * @throws Exception Error occurred on calling native library
     * @return <LI> score : The similarity of two fingerprints to be compared. The
     * higher score means a higher similarity.
     */    
	public synchronized native long FP_ImageMatchEx(long hConnect,byte[] fp_code ,int security)throws Exception;//return score
    /**
     * To verify two fingerprint code, while one is generated through () and the other through
     * the enrollment.
     *  <BR><BR>PS.This function verifies the p_code generated by FP_GetPrimaryCode() with the final
     * fingerprint code fp_code generated through the enrollment . The argument security sets the
     * threshold that determines whether this verification can be passed. Please note that p_code
     * and fp_code are quite different in their content, they cannot be misplaced.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param p_code The fingerprint code generated through
     * FP_GetPrimaryCode().
     * @param fp_code The final fingerprint code generated through the enrollment.
     * @param nSecurity A parameter to set the threshold that determines where the
     * verification can be passed. See FP_ImageMatch() for details.
     * @throws Exception Error occurred on calling native library
     * @return <LI> OK : The verification of fingerprint image with final fingerprint code
     * meets the requirement of security.
     * <LI> FAIL : The fingerprint image is not identical with the final fingerprint
     * code on the required security.
     * <LI> S_MEM_ERR : Insufficient memory for image processing.
     * <LI> S_FPCODE_INVALID : The input *fp_code is illegal.
     * <LI> S_SECURITY_ERR : Improper security level setting.
     */    
	public synchronized native long FP_CodeMatchEx(long hConnect,byte[] p_code,byte[] fp_code,int nSecurity)throws Exception;//return score

    /**
     * To check if there is any fingerprint on the reader.
     *  <BR><BR>PS.This function is very useful in the enrollment process. To get stable and real features of
     * a fingerprint during the enrollment, the user must remove his finger from the reader once a
     * fingerprint has been snapped and put it down again on the reader after FP_Enroll has
     * successfully been processed for this snapped fingerprint image. You can check if a
     * fingerprint has actually been lifted off the reader by using this function.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK There is no fingerprint on the reader.
     * <LI>FAIL There is a fingerprint on the reader.
     * <LI>S_MEM_ERR Failed to allocate memory.
     */    
	public synchronized native int FP_CheckBlank(long hConnect)throws Exception;
    /**
     * Diagnose if the fingerprint device is OK.
     *  <BR><BR>PS.This function diagnoses your fingerprint device. Before testing, please clean the prism
     * and make sure that there is no finger on the reader.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK The fingerprint device is OK.
     * <LI>FAIL There is problem with your fingerprint system.
     */    
	public synchronized native int FP_Diagnose(long hConnect)throws Exception;

    /**
     * Save the fingerprint image as a BMP or RAW file.
     *  <BR><BR>PS.This function saves the image as a BMP or RAW file with the specified filename. The
     * size and mode of the image are determined while calling FP_CreateImageHandle().
     * <BR>You must call FP_CreateImageHandle() and FP_GetImage() first to derive a 'legal' and
     * 'valid' image handle and do remember to call FP_DestroyImageHandle() to free the handle.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPImage The handle to the FP_IMAGE_SET defined in the include
     * files.
     * @param FileType The image can be saved as a bitmap (BMP) file or a raw
     * (RAW) file.
     * @param szFilename The filename to be saved as.
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : the image is saved successfully.
     * <LI>S_FILE_ERR : failed to open the file.
     * <LI>S_FP_INVALID : input hFPImage is invalid.
     * <LI>S_MEM_ERR : failed to allocate memory.
     */    
	public synchronized native int FP_SaveImage(long hConnect,long hFPImage,int FileType ,String szFilename)throws Exception;//char[]
	
    /**
     * Save the fingerprint image as a ISO 19794-4 format.
     *  <BR><BR>PS.This function saves the image as a ISO 19794-4 format with the specified filename. The
     * size and mode of the image are determined while calling FP_CreateImageHandle().
     * <BR>You must call FP_CreateImageHandle() and FP_GetImage() first to derive a 'legal' and
     * 'valid' image handle and do remember to call FP_DestroyImageHandle() to free the handle.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param hFPImage The handle to the FP_IMAGE_SET defined in the include
     * files.
     * @param FileType The image can be saved as a iso 19794-4 (ISO) file. 
     * @param szFilename The filename to be saved as.
     * @param compRatio The compress ratio. Currently only support none-compress default as 0.
     * @param fpPos The finger position number.
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : the image is saved successfully.
     * <LI>S_FILE_ERR : failed to open the file.
     * <LI>S_FP_INVALID : input hFPImage is invalid.
     * <LI>S_MEM_ERR : failed to allocate memory.
     */    
	public synchronized native int FP_SaveISOImage(long hConnect,long hFPImage,int FileType ,String szFilename, byte compRatio, byte fpPos)throws Exception;//char[]
	
		
	//public synchronized native int FP_DisplayImage(long hConnect,long hDC,long hFPImage ,int nStartX ,int nStartY,int nDestWidth,int nDestHeight)throws Exception;
    /**
     * Display the image handle on a device context.
     *  <BR><BR>The function displays the image handle on a device context. To call this function, you
     * should first create a handle to the FP_IMAGE_SET using FP_CreateImageHandle() and
     * then call FP_GetImage() to load the image from the main memory. Finally DONOT forget to
     * call FP_DestroyImageHandle() to release the image handle.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param canvas Identifies the device context.
     * @param hFPImage The handle to the FP_IMAGE_SET defined in include
     * files
     * @param nStartX The start position of the image to be displayed
     * @param nStartY The start position of the image to be displayed
     * @param nDestWidth The size of the image to be displayed
     * @param nDestHeight The size of the image to be displayed
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK If succeeds
     * <LI>FAIL Otherwise.
     */    
	public synchronized native int FP_DisplayImage(long hConnect,java.awt.Canvas canvas,long hFPImage ,int nStartX ,int nStartY,short nDestWidth,short nDestHeight)throws Exception;

    /**
     * Get the width and the height of the fingerprint image from the main memory.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param nWidth_Height The width and the height of the fingerprint image
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : Get the width and the height successfully.
     * <LI>FAIL : Fail to get the width and the height.
     */    
    public synchronized native int FP_GetImageDimension(long hConnect,short[] nWidth_Height)throws Exception;
    
    /**
     * Get the RAW data of the fingerprint image from the main memory.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param bRawData The RAW data of the fingerprint image from the main memory.
     * @param nWidth The width which is got from FP_GetImageDimension
     * @param nHeight The height which is got from FP_GetImageDimension
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : Get the RAW data of the fingerprint image from the main memory successfully.
     * <LI>FAIL : Fail to get the RAW data of the fingerprint image from the main memory.
     */    
    public synchronized native int FP_GetImageData(long hConnect,byte[] bRawData,short nWidth, short nHeight)throws Exception;

	public synchronized native int FP_SaveISOminutia(long hConnect,String szFilename,byte[] bRawData)throws Exception;

	public synchronized native int FP_LoadISOminutia(long hConnect,String szFilename,byte[] bRawData)throws Exception;
    
	public synchronized native java.awt.Image createimage(byte[] bRawData,int Width, int Height)throws Exception;
	//public synchronized native int FP_GetPrimaryCodeEx(long hConnect, byte[] bRawData,short nWidth, short nHeight, byte[] p_code)throws Exception;
	//public synchronized native int FP_GetEnrollCode(long hConnect, byte[] p_code,byte[] fp_code,short nImgWidth, short nImgHeight)throws Exception;
    
    /*
     * The DLG_FPEnroll is used to enroll one fingerprint(Capture three times).The DLG_FPEnroll will popup a dialog for fingerprint enrollment.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param com parent UI component
     * @param isocodeBuffer The byte array buffer for final fingerprint code after finishing fingerprint enrollment.
     * @param bfLength The size of the isocodeBuffer
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : Enroll a fingerprint successfully.
     * <LI>S_FPCODE_INVALID : bfLength is smaller than the size of the primary code.
     * <LI>ERROR_ALREADY_IN : the dialog has been shown
     * <LI>FAIL : Fail to enroll fingerprint.
         
    public synchronized native int DLG_FPEnroll(long hConnect,java.awt.Component com,byte[] isocodeBuffer,int bfLength)throws Exception;*/
    //,PSetImageProc proc1,PSetImageProc proc2, PSetImageProc proc3
    /*
     * The DLG_Snap is used to snap a fingerprint and generate a primary code of the fingerprint.
     * The DLG_Snap will popup a dialog for fingerprint capture.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param com parent UI component
     * @param isocodeBuffer A primary code of 256 bytes derived from FP_GetPrimaryCode().
     * @param bfLength The size of a primary code derived from FP_GetPrimaryCode().
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : Snap a fingerprint and generate a primary code of the fingerprint successfully.
     * <LI>S_FPCODE_INVALID : bfLength is smaller than the size of the primary code.
     * <LI>ERROR_ALREADY_IN : the dialog has been shown
     * <LI>FAIL : Fail to snap a fingerprint and generate a primary code of the fingerprint
         
    public synchronized native int DLG_FPSnap(long hConnect,java.awt.Component com,byte[] isocodeBuffer,int bfLength)throws Exception;*/
//public synchronized native int FP_RawtoISOimage(byte[] bRawData,short nWidth,short nHeight,byte[] bBMPData) throws Exception;
    /**
     * Change the RAW data to the BMP fomat.
     * @param bRawData The RAW data of the fingerprint image from the main memory.
     * @param nWidth The width which is got from FP_GetImageDimension
     * @param nHeight The height which is got from FP_GetImageDimension
     * @param bBMPData The BMP format data of the fingerprint image from the RAW data.
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : Get the BMP format data of the fingerprint image from the RAW data successfully.
     * <LI>FAIL : Fail to get the BMP format data of the fingerprint from the main memory.
     */   
    public synchronized native int FP_RawDataToBMP(byte[] bRawData,short nWidth,short nHeight,byte[] bBMPData) throws Exception;
    
    /**
     * Change the RAW data to the ISO-19794-4 fomat.
     * @param hConnect The handle returned by FP_ConnectCaptureDriver()
     * @param img_buf The RAW data of the fingerprint image from the main memory.
     * @param Img_Width The width which is got from FP_GetImageDimension
     * @param Img_Height The height which is got from FP_GetImageDimension
     * @param ImgCompAlgo The compression percentage, please set 0 in this edition
     * @param FpPos Which finger is, please set 0 in this edition
     * @param img_code The ISO format data of the fingerprint image from the RAW data.
     * @throws Exception Error occurred on calling native library
     * @return <LI>OK : Get the ISO format data of the fingerprint image from the RAW data successfully.
     * <LI>FAIL : Fail to get the ISO format data of the fingerprint from the main memory.
     */   
    
	public synchronized native int FP_RawtoISOimage(long hConnect, byte[] img_buf, short Img_Width, short Img_Height, byte ImgCompAlgo, byte FpPos, byte[] img_code) throws Exception;
    
	public synchronized native int FP_GetImageQuality(long hConnect) throws Exception;
	public synchronized native int FP_SetPublicKey(long hConnect, byte[] pPublicKey, int KeyLen) throws Exception;
	public synchronized native int FP_SetSessionKey(long hConnect, byte[] pSessionKey) throws Exception;
	public synchronized native int FP_GetDeleteData(long hConnect, byte[] UserId, int fpIndex, byte[] encDeleteData, int[] p_enc_len) throws Exception;
	public synchronized native int FP_SetLowSpeed() throws Exception;
	public synchronized native int FP_printf(String str) throws Exception;
	
	private static final void showDbgMsg(Object obj)
	{
		System.out.println(obj);
	}
	private static void loadRelatedLibrary() throws LibraryLoadFailedException
	{
		int loadOK = OK;
		String tmpStr = null;
		ArrayList tmpLibArray = new ArrayList();
		Exception catchedException = null;
		
		for (int i=0;i<defaultLibarys.length;i++)
		{
			tmpLibArray.add(defaultLibarys[i]);
		}
		{
			loadOK = OK;
			InputStream in = null;
			
			//copy file
			FileOutputStream out=null;
			File tmpFile=null;
			File dllFile=null;
			String createPath = null;
			String placePath = null;
			byte[] buf = new byte[10000];
			for (Iterator i=tmpLibArray.iterator();i.hasNext()&&loadOK==OK;)
			{
				tmpStr = (String)i.next();
				in = FM300SDKWrapper.class.getResourceAsStream("lib/"+tmpStr);
				//showDbgMsg( FC320SDKWrapper.class.getResource("lib/"+tmpStr));
				if (in!=null)
				{
					try
					{
						dllFile = File.createTempFile(tmpStr,null);//,tmplibDir);
						dllFile.deleteOnExit();
						//First time, check and initial placePath 
						if (placePath==null)
						{
							placePath = dllFile.getAbsolutePath();
							createPath = placePath;
							placePath = placePath.substring(0,placePath.lastIndexOf(tmpStr))+"STARTEK_FM300/";
							tmpFile = new File(placePath);
							tmpFile.deleteOnExit();
							currentDirectory = tmpFile;
							if (!(tmpFile.exists()&&tmpFile.isDirectory()))
							{
								//Create Directory
								tmpFile.mkdirs();
							}else
							{
								//Remove all files from the directory
								if (currentDirectory!=null && currentDirectory.isDirectory())
								{
									try
									{
										File[] tmpFiles = currentDirectory.listFiles();
										for (int x=0;x<tmpFiles.length;x++)
										{
											tmpFiles[x].delete();
										}
									}
									catch (Exception e)
									{
									}
								}
							}
							//System.setProperty("java.library.path",placePath);
						}
						out = new FileOutputStream(dllFile);
						for (int n=in.read(buf);n!=-1;n=in.read(buf))
						{
							out.write(buf,0,n);
						}
						out.close();
						in.close();
						//Load WSABrowserAPIWrapper Only 
						if (tmpStr.compareTo(defaultLibarys[defaultLibarys.length-1])!=0)
						{
							File tmp = new File(placePath+tmpStr);
							tmp.deleteOnExit();
							try
							{
								if (tmp.exists())
									tmp.delete();
								if (!dllFile.renameTo(tmp))
								{
									dllFile.delete();
								}
							}
							catch (Exception e)
							{
							}
							System.load(tmp.getAbsolutePath());
						}else
						{
							File tmp = new File(placePath+dllFile.getName());
							
							dllFile.renameTo(tmp);
							//Try System load
							System.load(tmp.getAbsolutePath());
							//showDbgMsg(tmp.getAbsolutePath());
						}
						
					}
					catch (UnsatisfiedLinkError e)
					{
						File tmp = new File(placePath+tmpStr);
						if (!tmp.exists())
						{
							loadOK = FAIL;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						catchedException = e;
						loadOK = FAIL;
					}finally
					{
						try
						{
							if (out!=null)
								out.close();
							if (in!=null)
								in.close();
						}
						catch (java.io.IOException e)
						{
							//ignore it
						}
						try
						{
							File tmp = new File(createPath);
							if (tmp.exists())
							{
								tmp.delete();
							}
						}
						catch (Exception e)
						{
							//ignore it
						}
					}
				}
			}
		}
		if (loadOK == FAIL)
		{
			tmpStr = "Can't load those librarys "+tmpLibArray.toString();
			if (catchedException!=null)
				throw new LibraryLoadFailedException(tmpStr,catchedException);
			else
				throw new LibraryLoadFailedException(tmpStr);
		}
	}
}
