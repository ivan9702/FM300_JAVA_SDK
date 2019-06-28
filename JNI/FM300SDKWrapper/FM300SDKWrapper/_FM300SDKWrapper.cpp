#include <windows.h>
#include <jawt.h>
#include <jawt_md.h>
#include "jni.h"

#include <stdio.h>
#include <TCHAR.H>
#include "fp_api.h"
#include "com_startek_eng_fm300sdk_FM300SDKWrapper.h"

extern DWORD GetJAWTLib(char* sJAWTLib, int iBufferSize);
extern short decode_pcode(BYTE *p_code, long key, short Width, short Height, short reduce_to_num);
extern short encode_pcode(BYTE *p_code, long key, short Width, short Height, short reduce_to_num);
typedef jboolean(JNICALL *PJAWT_GETAWT)(JNIEnv*, JAWT*);

//static TCHAR AWT_LIB_Name[_MAX_PATH];
//static HMODULE g_hJAWT; // JAWT module handle
TCHAR AWT_LIB_Name[_MAX_PATH] = { 0 };
HMODULE g_hJAWT = NULL; // JAWT module handle
extern HWND GetJAVAHWND(JNIEnv *env, jobject obj, jobject jcomponent);

extern "C" short  Raw2GrayBMP(BYTE *bRawData, short nWidth, short nHeight, BYTE *bBmpData, long nBmpLength);
/*
The jni.h
file contains a set of definitions so that C++ programmers can write, for example:
	jclass cls = env->FindClass("java/lang/String");

	jbyte* m_pcode = (jbyte*)env->GetByteArrayElements(pcode,&iscopy);
	env->ReleaseByteArrayElements(pcode, m_pcode, 0);
instead of in C:
	jclass cls = (*env)->FindClass(env, "java/lang/String");

	jbyte* m_pcode =(*env)->GetByteArrayElements(env,pcode,&iscopy);
	(*env)->ReleaseByteArrayElements(env,pcode, m_pcode, 0);
*/
//FP_ConnectCaptureDriver
JNIEXPORT jlong JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1ConnectCaptureDriver(JNIEnv *env, jobject obj, jint reserved)
{
	jlong result = FAIL;
	result = (jlong)FP_ConnectCaptureDriver(reserved);
	if (result == 0)
		result = FAIL;
	/*if (g_hJAWT == NULL && GetJAWTLib(AWT_LIB_Name,_MAX_PATH) == 0)
	{
		_tprintf(_T("AWT_LIB_Name:%s"),AWT_LIB_Name);
		g_hJAWT = LoadLibrary((LPCTSTR)AWT_LIB_Name);
		if (g_hJAWT==NULL)
			_tprintf("LoadLibrary %S Failed ,ERRORCODE:0x%X\r\n",(LPCTSTR)AWT_LIB_Name,GetLastError());
	}*/
	return result;
}
//FP_DisconnectCaptureDriver
JNIEXPORT void JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1DisconnectCaptureDriver(JNIEnv *env, jobject obj, jlong handle)
{
	FP_DisconnectCaptureDriver((HCONNECT)handle);
	if (g_hJAWT != NULL)
	{
		FreeLibrary(g_hJAWT);
	}
}
//FP_Snap
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1Snap(JNIEnv *env, jobject obj, jlong handle)
{
	return FP_Snap((HCONNECT)handle);
}
//FP_CreateCaptureHandle
JNIEXPORT jlong JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1CreateCaptureHandle(JNIEnv *env, jobject obj, jlong handle)
{
	return (jlong)FP_CreateCaptureHandle((HCONNECT)handle);
}
//FP_Capture
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1Capture(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPCapture)
{
	return FP_Capture((HCONNECT)hConnect, (HFPCAPTURE)hFPCapture);
}

//extern int WINAPI FP_DestroyCaptureHandle(HCONNECT hConnect,HFPCAPTURE hFPCapture);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1DestroyCaptureHandle(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPCapture)
{
	return FP_DestroyCaptureHandle((HCONNECT)hConnect, (HFPCAPTURE)hFPCapture);
}

//extern int WINAPI FP_GetPrimaryCode(HCONNECT hConnect, BYTE* pcode);

JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetPrimaryCode(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pcode)
{
	jint result = -1;
	jboolean iscopy;

	jbyte* m_pcode = (jbyte*)env->GetByteArrayElements(pcode, &iscopy);
	if (m_pcode)
		result = FP_GetPrimaryCode((HCONNECT)hConnect, (BYTE*)m_pcode);
	//ReleaseByteArrayElements:
	//	The last argument to the ReleaseByteArrayElements function above can have the following values:
	//		0: Updates to the array from within the C code are reflected in the Java language copy.
	//		JNI_COMMIT: The Java language copy is updated, but the local jbyteArray is not freed.
	//		JNI_ABORT: Changes are not copied back, but the jbyteArray is freed. The value is used only if the array is obtained with a get mode of JNI_TRUE meaning the array is a copy.
	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, 0);

	return result;
}

//extern int WINAPI FP_GetTemplate(HANDLE hConnect,BYTE *minu_code,int mode,int key);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetTemplate(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pcode, jint mode, jint key)
{
	jint result = -1;
	jboolean iscopy;

	jbyte* m_pcode = (jbyte*)env->GetByteArrayElements(pcode, &iscopy);
	if (m_pcode)
		result = FP_GetTemplate((HCONNECT)hConnect, (BYTE*)m_pcode, mode, key);
	//ReleaseByteArrayElements:
	//	The last argument to the ReleaseByteArrayElements function above can have the following values:
	//		0: Updates to the array from within the C code are reflected in the Java language copy.
	//		JNI_COMMIT: The Java language copy is updated, but the local jbyteArray is not freed.
	//		JNI_ABORT: Changes are not copied back, but the jbyteArray is freed. The value is used only if the array is obtained with a get mode of JNI_TRUE meaning the array is a copy.
	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, 0);

	return result;
}

//extern HFPIMAGE  WINAPI FP_CreateImageHandle( HCONNECT hConnect,BYTE bMode,WORD wSize);
JNIEXPORT jlong JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1CreateImageHandle(JNIEnv *env, jobject obj, jlong hConnect, jbyte bMode, jint wSize)
{
	return (jlong)FP_CreateImageHandle((HCONNECT)hConnect, bMode, (WORD)wSize);
}
//extern int WINAPI FP_GetImage(HCONNECT hConnect,HFPIMAGE hFPImage);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetImage(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPImage)
{
	return FP_GetImage((HCONNECT)hConnect, (HFPIMAGE)hFPImage);
}
//extern int WINAPI FP_DestroyImageHandle(HCONNECT hConnect,HFPIMAGE hFPImage);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1DestroyImageHandle(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPImage)
{
	return FP_DestroyImageHandle((HCONNECT)hConnect, (HFPIMAGE)hFPImage);
}

//extern HFPENROLL WINAPI FP_CreateEnrollHandle( HCONNECT hConnect,BYTE mode);
JNIEXPORT jlong JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1CreateEnrollHandle(JNIEnv *env, jobject obj, jlong hConnect, jbyte mode)
{
	return (jlong)FP_CreateEnrollHandle((HCONNECT)hConnect, mode);
}
//extern int WINAPI FP_Enroll( HCONNECT hConnect,HFPENROLL hFPEnroll,BYTE *pcode,BYTE *fpcode);

JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1Enroll(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPEnroll, jbyteArray pcode, jbyteArray fpcode)
{
	jint result = FAIL;
	jboolean iscopy;
	jbyte* m_pcode = (jbyte*)env->GetByteArrayElements(pcode, &iscopy);
	jbyte* m_fpcode = (jbyte*)env->GetByteArrayElements(fpcode, &iscopy);
	if (m_pcode && m_fpcode)
		result = FP_Enroll((HCONNECT)hConnect, (HFPIMAGE)hFPEnroll, (byte*)m_pcode, (byte*)m_fpcode);
	//ReleaseByteArrayElements:
	//	The last argument to the ReleaseByteArrayElements function above can have the following values:
	//		0: Updates to the array from within the C code are reflected in the Java language copy.
	//		JNI_COMMIT: The Java language copy is updated, but the local jbyteArray is not freed.
	//		JNI_ABORT: Changes are not copied back, but the jbyteArray is freed. The value is used only if the array is obtained with a get mode of JNI_TRUE meaning the array is a copy.
	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, JNI_ABORT);
	if (m_fpcode) env->ReleaseByteArrayElements(fpcode, m_fpcode, 0);
	return result;
}

//extern int WINAPI FP_EnrollEx(HANDLE hConnect,HFPENROLL hEnrlSet,BYTE *pcode,BYTE *fpcode,int mode)
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1EnrollEx(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPEnroll, jbyteArray pcode, jbyteArray fpcode, jint mode)
{
	jint result = FAIL;
	jboolean iscopy;
	jbyte* m_pcode = (jbyte*)env->GetByteArrayElements(pcode, &iscopy);
	jbyte* m_fpcode = (jbyte*)env->GetByteArrayElements(fpcode, &iscopy);
	if (m_pcode && m_fpcode)
		result = FP_EnrollEx((HCONNECT)hConnect, (HFPIMAGE)hFPEnroll, (byte*)m_pcode, (byte*)m_fpcode, mode);
	//ReleaseByteArrayElements:
	//	The last argument to the ReleaseByteArrayElements function above can have the following values:
	//		0: Updates to the array from within the C code are reflected in the Java language copy.
	//		JNI_COMMIT: The Java language copy is updated, but the local jbyteArray is not freed.
	//		JNI_ABORT: Changes are not copied back, but the jbyteArray is freed. The value is used only if the array is obtained with a get mode of JNI_TRUE meaning the array is a copy.
	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, JNI_ABORT);
	if (m_fpcode) env->ReleaseByteArrayElements(fpcode, m_fpcode, 0);
	return result;
}

//extern int WINAPI FP_DestroyEnrollHandle( HCONNECT hConnect,HFPENROLL hFPEnroll);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1DestroyEnrollHandle(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPEnroll)
{
	return FP_DestroyEnrollHandle((HCONNECT)hConnect, (HFPENROLL)hFPEnroll);
}

//extern int WINAPI FP_ImageMatch( HCONNECT hConnect,BYTE *fpcode,WORD wSecutity);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1ImageMatch(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray fpcode, jint wSecutity)
{
	jint result = FAIL;
	jboolean iscopy;

	jbyte* m_fpcode = env->GetByteArrayElements(fpcode, &iscopy);
	if (m_fpcode)
	{
		result = FP_ImageMatch((HCONNECT)hConnect, (byte*)m_fpcode, (WORD)wSecutity);
		env->ReleaseByteArrayElements(fpcode, m_fpcode, JNI_ABORT);
	}
	return result;
}
//extern int WINAPI FP_CodeMatch( HCONNECT hConnect,BYTE *pcode, BYTE *fpcode,WORD wSecutity);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1CodeMatch(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pcode, jbyteArray fpcode, jint wSecutity)
{
	jint result = FAIL;
	jboolean iscopy;
	jbyte* m_pcode = env->GetByteArrayElements(pcode, &iscopy);
	jbyte* m_fpcode = env->GetByteArrayElements(fpcode, &iscopy);
	if (m_pcode && m_fpcode)
	{
		result = FP_CodeMatch((HCONNECT)hConnect, (byte*)m_pcode, (byte*)m_fpcode, (WORD)wSecutity);
	}
	//ReleaseByteArrayElements:
	//	The last argument to the ReleaseByteArrayElements function above can have the following values:
	//		0: Updates to the array from within the C code are reflected in the Java language copy.
	//		JNI_COMMIT: The Java language copy is updated, but the local jbyteArray is not freed.
	//		JNI_ABORT: Changes are not copied back, but the jbyteArray is freed. The value is used only if the array is obtained with a get mode of JNI_TRUE meaning the array is a copy.
	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, JNI_ABORT);
	if (m_fpcode) env->ReleaseByteArrayElements(fpcode, m_fpcode, JNI_ABORT);

	return result;
}
//extern int WINAPI FP_ImageMatchEx( HCONNECT hConnect,BYTE *fpcode, WORD wSecutity,DWORD *dwScore);
JNIEXPORT jlong JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1ImageMatchEx(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray fpcode, jint wSecutity)
{
	jlong result = FAIL;
	jlong dwScore = 0;

	jboolean iscopy;
	jbyte* m_fpcode = env->GetByteArrayElements(fpcode, &iscopy);
	if (m_fpcode)
	{
		result = FP_ImageMatchEx((HCONNECT)hConnect, (unsigned char*)m_fpcode, (WORD)wSecutity, (DWORD*)&dwScore);
		//_tprintf("C++=>FP_ImageMatchEx:result:%X score=%X\r\n",result,dwScore);
		//if (result==OK)
		{
			result = dwScore;
		}
		env->ReleaseByteArrayElements(fpcode, m_fpcode, JNI_ABORT);
	}
	return result;
}

//extern int WINAPI FP_CodeMatchEx( HCONNECT hConnect,BYTE *pcode, BYTE *fpcode, WORD wSecutity, DWORD *dwScore );
JNIEXPORT jlong JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1CodeMatchEx(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pcode, jbyteArray fpcode, jint wSecutity)
{
	jlong result = FAIL;
	jlong dwScore = 0;
	jboolean iscopy;
	jbyte* m_pcode = env->GetByteArrayElements(pcode, &iscopy);
	jbyte* m_fpcode = env->GetByteArrayElements(fpcode, &iscopy);
	if (m_pcode && m_fpcode)
	{
		result = FP_CodeMatchEx((HCONNECT)hConnect, (unsigned char*)m_pcode, (unsigned char*)m_fpcode, (WORD)wSecutity, (DWORD*)&dwScore);
		//_tprintf("C++=>FP_CodeMatchEx:result:%X score=%X\r\n",result,dwScore);
		//if (result==OK)
		{
			result = dwScore;
		}
	}

	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, JNI_ABORT);
	if (m_fpcode) env->ReleaseByteArrayElements(fpcode, m_fpcode, JNI_ABORT);
	//_tprintf("C++=>FP_CodeMatchEx:result:%X score=%X\r\n",result,dwScore);
	return result;
}

//extern int WINAPI FP_CheckBlank(HCONNECT hConnect);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1CheckBlank(JNIEnv *env, jobject obj, jlong hConnect)
{
	return FP_CheckBlank((HCONNECT)hConnect);
}
//extern int WINAPI FP_Diagnose(HCONNECT hConnect);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1Diagnose(JNIEnv *env, jobject obj, jlong handle)
{
	return FP_Diagnose((HCONNECT)handle);
}

//extern int WINAPI FP_SaveImage(HCONNECT hConnect,HFPIMAGE hFPImage,WORD wFileType,char *Filename);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1SaveImage(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPImage, jint wFileType, jstring Filename)
{
	jint result = FAIL;
	//	jboolean iscopy;
	const char *mfile;
	//mfile = env->GetStringChars(env, Filename, &iscopy); //unicode :not workable
	mfile = (char *)env->GetStringUTFChars(Filename, NULL);
	//_tprintf("\r\nFilename:%s\r\n",mfile);
	if (mfile)
	{
		result = FP_SaveImage((HCONNECT)hConnect, (HFPIMAGE)hFPImage, (WORD)wFileType, (char*)mfile);
		//env->ReleaseStringChars(env, Filename, mfile);	//unicode :not workable
		env->ReleaseStringUTFChars(Filename, mfile);
	}
	return result;
}

/*
 * Class:     com_startek_0005feng_fm300sdk_FM300SDKWrapper
 * Method:    FP_SaveISOImage
 * Signature: (JJILjava/lang/String;BB)I
 */

JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1SaveISOImage(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPImage, jint wFileType, jstring Filename, jbyte compRatio, jbyte fpPos)
{
	jint result = FAIL;
	//	jboolean iscopy;
	const char *mfile;
	//mfile = env->GetStringChars(env, Filename, &iscopy); //unicode :not workable
	mfile = (char *)env->GetStringUTFChars(Filename, NULL);
	//_tprintf("\r\nFilename:%s\r\n",mfile);
	if (mfile)
	{
		result = FP_SaveISOImage((HCONNECT)hConnect, (HFPIMAGE)hFPImage, ISO, (char*)mfile, compRatio, fpPos);
		//env->ReleaseStringChars(env, Filename, mfile);	//unicode :not workable
		env->ReleaseStringUTFChars(Filename, mfile);
	}
	return result;
}

//extern int WINAPI FP_DisplayImage(HCONNECT hConnect,HDC hDC,HFPIMAGE hFPImage,int nStartX,int nStartY,int nWidth,int nHeight);
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1DisplayImage(JNIEnv *env, jobject obj, jlong hConnect, jlong hDC, jlong hFPImage, jlong nStartX, jlong nStartY, jlong nWidth, jlong nHeight)
{
	jint result = FAIL;

	result = FP_DisplayImage((HCONNECT)hConnect, (HDC)hDC, (HFPIMAGE)hFPImage, (int)nStartX, (int)nStartY, (int)nWidth, (int)nHeight);
	return result;
}

JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1DisplayImage(JNIEnv *env, jobject obj, jlong hConnect, jobject canvas, jlong hFPImage, jint nStartX, jint nStartY, jshort nWidth, jshort nHeight)
{
	jint result = FAIL;

	if (g_hJAWT == NULL && GetJAWTLib(AWT_LIB_Name, _MAX_PATH) == 0)
	{
		g_hJAWT = LoadLibrary((LPCTSTR)AWT_LIB_Name);
		//if (g_hJAWT==0)
		//	_tprintf("LoadLibrary %S Failed ,ERRORCODE:0x%X\r\n",AWT_LIB_Name,GetLastError());
	}
	if (g_hJAWT != NULL)
	{
		PJAWT_GETAWT                  pjawt_GetAWT;
		JAWT                          jawt;
		JAWT_DrawingSurface*          jawt_DrawingSurface = NULL;
		JAWT_DrawingSurfaceInfo*      jawt_DrawingSurfaceInfo = NULL;
		JAWT_Win32DrawingSurfaceInfo* jawt_Win32DrawingSurfaceInfo;
		jint lock;

		pjawt_GetAWT = (PJAWT_GETAWT)GetProcAddress(g_hJAWT, "_JAWT_GetAWT@8");
		if (pjawt_GetAWT == NULL) {
			result = -1;
		}
		else
		{
			jawt.version = JAWT_VERSION_1_3;//JAWT_VERSION_1_4;
			if (pjawt_GetAWT(env, &jawt) == JNI_FALSE) {
				result = -2;
			}
			else
			{
				jawt_DrawingSurface = jawt.GetDrawingSurface(env, canvas);
				if (jawt_DrawingSurface == NULL) {
					result = -3;
				}
				else
				{
					lock = jawt_DrawingSurface->Lock(jawt_DrawingSurface);
					if ((lock & JAWT_LOCK_ERROR) != 0) {
						result = -4;
					}
					else
					{
						jawt_DrawingSurfaceInfo = jawt_DrawingSurface->GetDrawingSurfaceInfo(jawt_DrawingSurface);
						if (jawt_DrawingSurfaceInfo == NULL) {
							result = -5;
						}
						else
						{
							jawt_Win32DrawingSurfaceInfo = (JAWT_Win32DrawingSurfaceInfo*)jawt_DrawingSurfaceInfo->platformInfo;

							HWND hWnd = jawt_Win32DrawingSurfaceInfo->hwnd;
							HDC hDC = GetWindowDC(hWnd);
							// Gets the device context for drawing
							if (hDC != NULL)
							{
								result = FP_DisplayImage((HCONNECT)hConnect, hDC, (HFPIMAGE)hFPImage, (int)nStartX, (int)nStartY, (int)nWidth, (int)nHeight);
								ReleaseDC(hWnd, hDC);
							}
							else
							{
								result = -6;
							}
							jawt_DrawingSurface->FreeDrawingSurfaceInfo(jawt_DrawingSurfaceInfo);
						}
						jawt_DrawingSurface->Unlock(jawt_DrawingSurface);
					}
					jawt.FreeDrawingSurface(jawt_DrawingSurface);
				}
			}
		}
	}
	return result;
}
/*
JNIEXPORT jint
JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetImageData (JNIEnv *env, jobject obj, jlong hConnect, jbyteArray rawImageData, jshortArray imageWidthHeight)
{
	jint result = FAIL;
	jboolean iscopy;
	jbyte* m_rawImageData = env->GetByteArrayElements(rawImageData,&iscopy);
	jshort* m_imageWidthHeight = env->GetShortArrayElements(imageWidthHeight,&iscopy);

	jsize bufsize = env->GetArrayLength(rawImageData);

	BYTE* tmpRawData = FP_GetImageData((HCONNECT)hConnect,(int*)&m_imageWidthHeight[0],(int*)&m_imageWidthHeight[1]);
	if (bufsize>=m_imageWidthHeight[0]*m_imageWidthHeight[1])
	{
		memcpy(m_rawImageData,tmpRawData,m_imageWidthHeight[0]*m_imageWidthHeight[1]);
		//_tprintf("rawImageData buffer Size: %X ",bufsize);
		result = OK;
	}else
	{
		//_tprintf("ERROR:  too small RawData buffer size = %X  must > %X ",bufsize,m_imageWidthHeight[0]*m_imageWidthHeight[1]);
	}
	FP_FreeImageData((HCONNECT)hConnect,tmpRawData);
	//extern void WINAPI FP_FreeImageData(HANDLE hConnect,BYTE *bRawData);

	if (m_rawImageData) env->ReleaseByteArrayElements( rawImageData, m_rawImageData, 0);
	if (m_imageWidthHeight) env->ReleaseShortArrayElements( imageWidthHeight, m_imageWidthHeight, 0);
	return result;
}
*/
jobject NewImgObjFromjbArrayBuf(JNIEnv *env, jobject obj, jbyteArray sampleBuff, jint Width, jint Height) {
	//int Width=256,Height=324;
	//int Width=304,Height=344;
	int R, G, B;
	jint result = -1;
	jboolean iscopy;

	jintArray m_pixels = (jintArray)env->NewIntArray(Height * Width);
	jint *pixels = (jint*)env->GetIntArrayElements(m_pixels, &iscopy);

	jclass memoryimagesourceClass = NULL;
	jclass ContainClass = NULL;

	jmethodID cid_m;
	jmethodID cid_c;
	jmethodID cid_c_c;

	jobject componentObj;

	jobject imgProducer;
	jobject image;	//if i get...

	jbyte* m_sampleBuff = (jbyte*)env->GetByteArrayElements(sampleBuff, &iscopy);

	for (int y = 0; y < Height; y++)
		for (int x = 0; x < Width; x++) {
			R = G = B = m_sampleBuff[y*Width + x];
			pixels[y * Width + x] = (B & 0xff) | ((G << 8) & 0xff00) | ((R << 16) & 0xff0000) | 0xff000000;
		}
	if (pixels) env->ReleaseIntArrayElements(m_pixels, pixels, 0);

	if (ContainClass == NULL) {
		ContainClass = env->FindClass("java/awt/Container");
		if (ContainClass == NULL) {
			printf("\nno found Container");
			return NULL; /* exception thrown */
		}
	}

	if (memoryimagesourceClass == NULL) {
		//printf("\nbefore FindClass java/awt/image/MemoryImageSource");
		memoryimagesourceClass = env->FindClass("java/awt/image/MemoryImageSource");
		//printf("\nafter FindClass java/awt/image/MemoryImageSource ");
		if (memoryimagesourceClass == NULL) {
			return NULL; /* exception thrown */
		}
	}

	//Constructor, return void
	cid_m = env->GetMethodID(memoryimagesourceClass, "<init>", "(II[III)V");
	imgProducer = env->NewObject(memoryimagesourceClass, cid_m, Width, Height, m_pixels, 0, Width);
	if (imgProducer == NULL) {
		printf("\ngProducer is null");
	}

	cid_c = env->GetMethodID(ContainClass, "<init>", "()V");
	if (cid_c == NULL) {
		printf("\ncid_c is null\n");
	}

	componentObj = env->NewObject(ContainClass, cid_c, NULL);
	if (componentObj == NULL) {
		printf("\ncomponentObj is null\n");
	}
	cid_c_c = env->GetMethodID(ContainClass, "createImage", "(Ljava/awt/image/ImageProducer;)Ljava/awt/Image;");
	if (cid_c_c == NULL) {
		printf("\ncid_c_c is null");
	}

	image = env->CallObjectMethod(componentObj, cid_c_c, imgProducer);

	if (image == NULL) {
		printf("\nimage is null\n");
	}
	return image;
}

/*
 * Class:     com_startek_eng_fm300sdk_FM300SDKWrapper
 * Method:    createimage
 * Signature: ([BII)Ljava/awt/Image;
 */
JNIEXPORT jobject JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_createimage
(JNIEnv *env, jobject obj, jbyteArray sampleBuff, jint w, jint h) {
	printf("JNI createImage\n");
	return NewImgObjFromjbArrayBuf(env, obj, sampleBuff, w, h);
}

//
JNIEXPORT jint
JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetEnrollCode(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pcode, jbyteArray fpcode, jshort nWidth, jshort nHeight)
{
	jint result = FAIL;
	jboolean iscopy;
	jbyte* m_pcode = env->GetByteArrayElements(pcode, &iscopy);
	jbyte* m_fpcode = env->GetByteArrayElements(fpcode, &iscopy);
	jsize pcode_size = env->GetArrayLength(pcode);
	jsize fpcode_size = env->GetArrayLength(fpcode);

	if (m_pcode && m_fpcode &&
		pcode_size >= FP_CODE_LENGTH && fpcode_size >= FP_CODE_LENGTH)
	{
		memcpy(m_fpcode, m_pcode, FP_CODE_LENGTH);
		//_tprintf("FP_1GetEnrollCode: W:%X, H:%X pcodeSize:%X, fpCodeSize:%X",nWidth,nHeight,pcode_size,fpcode_size);
		if ((result = decode_pcode((byte*)m_fpcode, 785388L, nWidth, nHeight, 50)) == OK)
		{
			m_fpcode[253] = U_CLASS_C;
			if ((result = encode_pcode((byte*)m_fpcode, 23545953L, nWidth, nHeight, 50)) == OK)
			{
				result = OK;
			}
			else result = S_FPCODE_INVALID;
		}
		else result = S_FP_INVALID;
	}
	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, JNI_ABORT);
	if (m_fpcode) env->ReleaseByteArrayElements(fpcode, m_fpcode, 0);
	return result;
}
//extern int WINAPI FP_GetPrimaryCodeEx(HANDLE hConnect,BYTE *bRawData,int nWidth,int nHeight,BYTE *p_code);
/*
JNIEXPORT jint
JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetPrimaryCodeEx(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray bRawData, jshort nWidth, jshort nHeight, jbyteArray p_code)
{
	jint result=FAIL;
	jboolean iscopy;
	jbyte* m_bRawData = env->GetByteArrayElements(bRawData,&iscopy);
	jbyte* m_pcode = env->GetByteArrayElements(p_code,&iscopy);

	jsize bufsize = env->GetArrayLength(bRawData);
	if (m_pcode && m_bRawData && bufsize>=nWidth*nHeight)
	{
		result = FP_GetPrimaryCodeEx((HCONNECT)hConnect,(BYTE *)m_bRawData,nWidth,nHeight,(BYTE *)m_pcode);
	}
	if (m_pcode) env->ReleaseByteArrayElements( p_code, m_pcode, 0);
	if (m_bRawData) env->ReleaseByteArrayElements( bRawData, m_bRawData, JNI_ABORT);
	return result;
}
*/
/*JNIEXPORT jint JNICALL
Java_FM300SDKWrapper_testByteArray (JNIEnv *env, jobject obj, jbyteArray test_bytes)
{
	jboolean iscopy;
	jbyte* m_pcode = env->GetByteArrayElements(test_bytes,&iscopy);
	int i;
	for (i=0;i<256;i++)
	{
		m_pcode[i] = i;
		_tprintf("%d",(byte)m_pcode[i]);
	}
	//ReleaseByteArrayElements:
	//	The last argument to the ReleaseByteArrayElements function above can have the following values:
	//		0: Updates to the array from within the C code are reflected in the Java language copy.
	//		JNI_COMMIT: The Java language copy is updated, but the local jbyteArray is not freed.
	//		JNI_ABORT: Changes are not copied back, but the jbyteArray is freed. The value is used only if the array is obtained with a get mode of JNI_TRUE meaning the array is a copy.

	env-> ReleaseByteArrayElements( test_bytes,m_pcode , 0);
	return 0;
}
JNIEXPORT jint JNICALL
Java_FM300SDKWrapper_testLongReference(JNIEnv *env, jobject obj, jobject tt)
{
	jclass clsLong = env->GetObjectClass(env, tt);
	assert(clsLong);
	jfieldID fid = env->GetFieldID(env, clsLong, "s", "J");
	assert(fid);
	jlong jnum = env->GetLongField(env, tt, fid);

	_tprintf("%l\n", jnum);
	jnum = 34;
	env->SetLongField(env, tt, fid, jnum);

	//jclass Class_Long = env->GetObjectClass("java/lang/Long");
	//if (Class_Long!=NULL)
	{
	//	Class_Long = (jclass) env->NewGlobalRef(Class_Long);
	}
	return 0;
//tt = 1999;
//return tt;
}*/
/*
JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_DLG_1FPEnroll
  (JNIEnv *env, jobject obj, jlong hConnect, jobject jcomponent, jbyteArray codeBuffer, jint codeLength)
{
	jint result=FAIL;
	jboolean iscopy;
	jbyte* _codeBuffer = env->GetByteArrayElements(codeBuffer,&iscopy);
	HWND _hWND = GetJAVAHWND(env,obj,jcomponent);
	jsize _bufsize = env->GetArrayLength(codeBuffer);
	if (_codeBuffer)
	{
		result = DLG_FPEnroll((HCONNECT)hConnect,_hWND,(BYTE*)_codeBuffer,_bufsize,
								NULL,NULL,NULL);
	}
	if (_codeBuffer) env->ReleaseByteArrayElements( codeBuffer, _codeBuffer, 0);
	return result;
}
*/
/*
JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_DLG_1FPSnap
  (JNIEnv *env, jobject obj, jlong hConnect, jobject jcomponent, jbyteArray codeBuffer, jint codeLength)
{
	jint result=FAIL;
	jboolean iscopy;
	jbyte* _codeBuffer = env->GetByteArrayElements(codeBuffer,&iscopy);
	HWND _hWND = GetJAVAHWND(env,obj,jcomponent);
	jsize _bufsize = env->GetArrayLength(codeBuffer);
	if (_codeBuffer)
	{
		result = DLG_FPSnap((HCONNECT)hConnect,_hWND,(BYTE*)_codeBuffer,_bufsize,
								NULL);
	}
	if (_codeBuffer) env->ReleaseByteArrayElements( codeBuffer, _codeBuffer, 0);
	return result;
}
*/
//HANDLE hConnect,BYTE *img_buf  ,short Img_Width,short Img_Height,BYTE ImgCompAlgo,BYTE FpPos,BYTE *img_code
/*
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1RawtoISOimage(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray img_buf, jshort Img_Width, jshort Img_Height,jbyte ImgCompAlgo, jbyte FpPos, jbyteArray img_code)
{
	jint result=FAIL;
	jboolean iscopy;
	jbyte* m_img_buf = env->GetByteArrayElements(img_buf, &iscopy);
	jbyte* m_img_code = env->GetByteArrayElements(img_code, &iscopy);

	result=FP_RawtoISOimage((HCONNECT)hConnect, (unsigned char*)m_img_buf, Img_Width, Img_Height, 0, FpPos, (unsigned char*) m_img_code);
	if (m_img_buf) env->ReleaseByteArrayElements(img_buf, m_img_buf, JNI_COMMIT);
	if (m_img_code) env->ReleaseByteArrayElements(img_code, m_img_code, JNI_COMMIT);
	return result;
}
*/

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1RawDataToBMP
(JNIEnv *env, jobject obj, jbyteArray rawImageData, jshort nWidth, jshort nHeight, jbyteArray bmpImageData)
{
	jint result = FAIL;
	jboolean iscopy;
	jbyte* m_rawImageData = env->GetByteArrayElements(rawImageData, &iscopy);
	jbyte* m_bmpImageData = env->GetByteArrayElements(bmpImageData, &iscopy);

	jsize rawsize = env->GetArrayLength(rawImageData);
	jsize bufsize = env->GetArrayLength(bmpImageData);

	if (rawsize == nWidth * nHeight && bufsize > rawsize)
	{
		//_tprintf("rawImageData buffer Size: %X ",bufsize);
		result = Raw2GrayBMP((unsigned char*)m_rawImageData
			, nWidth
			, nHeight
			, (unsigned char*)m_bmpImageData
			, bufsize);
	}
	else
	{
		//_tprintf("ERROR:  too small RawData buffer size = %X  must > %X ",bufsize,imageWidth*imageHeight);
	}

	if (m_rawImageData) env->ReleaseByteArrayElements(rawImageData, m_rawImageData, JNI_ABORT);
	if (m_bmpImageData) env->ReleaseByteArrayElements(bmpImageData, m_bmpImageData, 0);
	return result;
}

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetImageDimension(JNIEnv *env, jobject obj, jlong hConnect, jshortArray imageWidthHeight)
{
	jboolean iscopy;
	jshort* m_imageWidthHeight = env->GetShortArrayElements(imageWidthHeight, &iscopy);
	FP_GetImageDimension((HCONNECT)hConnect, (int*)&(m_imageWidthHeight[0]), (int*)&(m_imageWidthHeight[1]));
	if (m_imageWidthHeight) env->ReleaseShortArrayElements(imageWidthHeight, m_imageWidthHeight, 0);
	return OK;
}

/*
JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetImageData
  (JNIEnv *env, jobject obj, jlong hConnect, jbyteArray rawImageData, jshort nWidth, jshort nHeight)
{
	jint result = FAIL;
	jboolean iscopy;
	jbyte* m_rawImageData = env->GetByteArrayElements(rawImageData,&iscopy);
	jint imageWidth;
	jint imageHeight;

	jsize bufsize = env->GetArrayLength(rawImageData);

	BYTE* tmpRawData = FP_GetImageData((HCONNECT)hConnect,(int*)&imageWidth,(int*)&imageHeight);
	//_tprintf("FP_GetImageData:W:%d,H:%d \r\n INPUT:W:%d,H:%d bufsize:%d\r\n",imageWidth,imageHeight,nWidth,nHeight,bufsize);
	if (bufsize==imageWidth*imageHeight
		&& nWidth == imageWidth
		&& nHeight == imageHeight)
	{
		memcpy(m_rawImageData,tmpRawData,sizeof(unsigned char)*imageWidth*imageHeight);
		//_tprintf("rawImageData buffer Size: %X ",bufsize);
		result = OK;
	}else
	{
		//_tprintf("ERROR:  too small RawData buffer size = %X  must > %X ",bufsize,imageWidth*imageHeight);
	}
	FP_FreeImageData((HCONNECT)hConnect,tmpRawData);

	if (m_rawImageData) env->ReleaseByteArrayElements( rawImageData, m_rawImageData, 0);
	return result;
}
*/

/*
 * Class:     com_startek_eng_fm300sdk_FM300SDKWrapper
 * Method:    FP_SaveISOminutia
 * Signature: (JLjava/lang/String;[B)I
 */
 /*
 JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1SaveISOminutia
 (JNIEnv *env, jobject obj, jlong hConnect, jstring Filename, jbyteArray minu_code){
	 jint result = FAIL;
	 const char *mfile;
	 jboolean iscopy;
	 jbyte* m_code = env->GetByteArrayElements(minu_code,&iscopy);
	 //mfile = env->GetStringChars(env, Filename, &iscopy); //unicode :not workable
	 mfile = (char *)env->GetStringUTFChars(Filename, NULL);
	 //_tprintf("\r\nFilename:%s\r\n",mfile);
	 if (mfile)
	 {
		 result = FP_SaveISOminutia((HCONNECT)hConnect,(char*)mfile,(unsigned char*)m_code);
		 //env->ReleaseStringChars(env, Filename, mfile);	//unicode :not workable
		 env->ReleaseStringUTFChars(Filename, mfile);
	 }
	 if (m_code) env->ReleaseByteArrayElements( minu_code, m_code, JNI_ABORT);
	 return result;
 };
 */
 /*
  * Class:     com_startek_eng_fm300sdk_FM300SDKWrapper
  * Method:    FP_LoadISOminutia
  * Signature: (JLjava/lang/String;[B)I
  */
  /*
  JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1LoadISOminutia
  (JNIEnv *env, jobject obj, jlong hConnect, jstring Filename, jbyteArray minu_code){
	  jint result = FAIL;
	  const char *mfile;
	  jboolean iscopy;
	  jbyte* m_code = env->GetByteArrayElements(minu_code,&iscopy);
	  //mfile = env->GetStringChars(env, Filename, &iscopy); //unicode :not workable
	  mfile = (char *)env->GetStringUTFChars(Filename, NULL);
	  //_tprintf("\r\nFilename:%s\r\n",mfile);
	  if (mfile)
	  {
		  result = FP_LoadISOminutia((HCONNECT)hConnect,(char*)mfile,(unsigned char*)m_code);
		  //env->ReleaseStringChars(env, Filename, mfile);	//unicode :not workable
		  env->ReleaseStringUTFChars(Filename, mfile);
	  }
	  if (m_code) env->ReleaseByteArrayElements( minu_code, m_code, 0);
	  return result;
  };
  */
  /*
   * Class:     com_startek_eng_fm300sdk_FM300SDKWrapper
   * Method:    FP_GetNFIQ
   * Signature: (J)I
   */
   /*
   JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetImageQuality
   (JNIEnv *env, jobject obj, jlong hConnect){
	   int nfiq;
	   nfiq=FP_GetImageQuality((HCONNECT)hConnect);
	   return nfiq;
   };
   */

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetEncryptedTemplate
(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray encrypted_minu_code, jint mode, jint key, jbyteArray iv, jbyteArray encrypted_skey)
{
	jlong result = FAIL;
	jboolean iscopy;
	jbyte* m_encrypted_minu_code = (jbyte*)env->GetByteArrayElements(encrypted_minu_code, &iscopy);
	jbyte* m_iv = (jbyte*)env->GetByteArrayElements(iv, &iscopy);
	jbyte* m_encrypted_skey = (jbyte*)env->GetByteArrayElements(encrypted_skey, &iscopy);

	if (m_encrypted_minu_code && m_iv && m_encrypted_skey)
		result = FP_GetEncryptedTemplate((HCONNECT)hConnect, (BYTE *)m_encrypted_minu_code, mode, key, (BYTE*)m_iv, (BYTE*)m_encrypted_skey);

	if (m_encrypted_minu_code) env->ReleaseByteArrayElements(encrypted_minu_code, m_encrypted_minu_code, 0);
	if (m_iv) env->ReleaseByteArrayElements(iv, m_iv, 0);
	if (m_encrypted_skey) env->ReleaseByteArrayElements(encrypted_skey, m_encrypted_skey, 0);

	return result;
}

/*
JNIEXPORT jint JNICALL
Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetPrimaryCode(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pcode)
{
jint result=-1;
jboolean iscopy;

jbyte* m_pcode = (jbyte*)env->GetByteArrayElements(pcode,&iscopy);
if (m_pcode)
result = FP_GetPrimaryCode((HCONNECT)hConnect,(BYTE*)m_pcode);
//ReleaseByteArrayElements:
//	The last argument to the ReleaseByteArrayElements function above can have the following values:
//		0: Updates to the array from within the C code are reflected in the Java language copy.
//		JNI_COMMIT: The Java language copy is updated, but the local jbyteArray is not freed.
//		JNI_ABORT: Changes are not copied back, but the jbyteArray is freed. The value is used only if the array is obtained with a get mode of JNI_TRUE meaning the array is a copy.
if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, 0);

return result;
}

Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1CodeMatchEx(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pcode, jbyteArray fpcode, jint wSecutity)
{
	jlong result = FAIL;
	jlong dwScore = 0;
	jboolean iscopy;
	jbyte* m_pcode = env->GetByteArrayElements(pcode, &iscopy);
	jbyte* m_fpcode = env->GetByteArrayElements(fpcode, &iscopy);
	if (m_pcode && m_fpcode)
	{
		result = FP_CodeMatchEx((HCONNECT)hConnect, (unsigned char*)m_pcode, (unsigned char*)m_fpcode, (WORD)wSecutity, (DWORD*)&dwScore);
		//_tprintf("C++=>FP_CodeMatchEx:result:%X score=%X\r\n",result,dwScore);
		//if (result==OK)
		{
			result = dwScore;
		}
	}

	if (m_pcode) env->ReleaseByteArrayElements(pcode, m_pcode, JNI_ABORT);
	if (m_fpcode) env->ReleaseByteArrayElements(fpcode, m_fpcode, JNI_ABORT);
	//_tprintf("C++=>FP_CodeMatchEx:result:%X score=%X\r\n",result,dwScore);
	return result;
}
*/

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1EnrollEx_1Encrypted
(JNIEnv *env, jobject obj, jlong hConnect, jlong hFPEnroll, jbyteArray encrypted_minu_code, jint mode, jbyteArray iv, jbyteArray encrypted_skey)
{
	// FP_EnrollEx_Encrypted(HANDLE hConnect, HFPENROLL hEnrlSet, BYTE *encrypted_fpcode, int mode, BYTE *iv, BYTE *encrypted_skey);
	jlong result = FAIL;
	jboolean iscopy;

	jbyte* m_encrypted_minu_code = (jbyte*)env->GetByteArrayElements(encrypted_minu_code, &iscopy);
	jbyte* m_iv = (jbyte*)env->GetByteArrayElements(iv, &iscopy);
	jbyte* m_encrypted_skey = (jbyte*)env->GetByteArrayElements(encrypted_skey, &iscopy);

	if (m_encrypted_minu_code && m_iv && m_encrypted_skey)
		result = FP_EnrollEx_Encrypted((HCONNECT)hConnect, (HFPENROLL)hFPEnroll, (BYTE *)m_encrypted_minu_code, mode, (BYTE *)m_iv, (BYTE *)m_encrypted_skey);

	if (m_encrypted_minu_code) env->ReleaseByteArrayElements(encrypted_minu_code, m_encrypted_minu_code, 0);
	if (m_iv) env->ReleaseByteArrayElements(iv, m_iv, 0);
	if (m_encrypted_skey) env->ReleaseByteArrayElements(encrypted_skey, m_encrypted_skey, 0);

	return result;
}

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1SetPublicKey
(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pPublicKey, jint KeyLen)
{
	//FP_SetPublicKey(HANDLE hConnect, BYTE *pPublicKey, int KeyLen);
	jlong result = FAIL;
	jboolean iscopy;

	jbyte* m_pPublicKey = (jbyte*)env->GetByteArrayElements(pPublicKey, &iscopy);

	if (m_pPublicKey)
	{
		FP_SetPublicKey((HCONNECT)hConnect, (BYTE *)m_pPublicKey, KeyLen);
	}

	if (m_pPublicKey) env->ReleaseByteArrayElements(pPublicKey, m_pPublicKey, 0);

	return result;
}

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1SetSessionKey
(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray pSessionKey)
{
	//FP_SetSessionKey(HANDLE hConnect, BYTE *pSessionKey);
	jlong result = FAIL;
	jboolean iscopy;

	jbyte* m_pSessionKey = (jbyte*)env->GetByteArrayElements(pSessionKey, &iscopy);

	if (m_pSessionKey)
	{
		result = FP_SetSessionKey((HCONNECT)hConnect, (BYTE *)m_pSessionKey);
	}

	if (m_pSessionKey) env->ReleaseByteArrayElements(pSessionKey, m_pSessionKey, 0);

	return result;
}

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1GetDeleteData
(JNIEnv *env, jobject obj, jlong hConnect, jbyteArray UserId, jint fpIndex, jbyteArray encDeleteData, jintArray p_enc_len)
{
	//FP_GetDeleteData(HANDLE hConnect,TCHAR *UserId, int fpIndex, BYTE *encDeleteData, int *p_enc_len);
	jlong result = FAIL;
	jboolean iscopy;

	jbyte* m_UserId = (jbyte*)env->GetByteArrayElements(UserId, &iscopy);
	jbyte* m_encDeleteData = (jbyte*)env->GetByteArrayElements(encDeleteData, &iscopy);
	jint* m_p_enc_len = (jint*)env->GetIntArrayElements(p_enc_len, &iscopy);

	if (m_UserId && m_encDeleteData && m_p_enc_len)
	{
		result = FP_GetDeleteData((HCONNECT)hConnect, (TCHAR *)m_UserId, fpIndex, (BYTE *)m_encDeleteData, (int *)m_p_enc_len);
	}

	if (m_UserId) env->ReleaseByteArrayElements(UserId, m_UserId, 0);
	if (m_encDeleteData) env->ReleaseByteArrayElements(encDeleteData, m_encDeleteData, 0);
	if (m_p_enc_len) env->ReleaseIntArrayElements(p_enc_len, m_p_enc_len, 0);

	return result;
}

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1SetLowSpeed
(JNIEnv *env, jobject obj)
{
	jlong result = FAIL;

	//result = FP_SetLowSpeed();

	return result;
}

JNIEXPORT jint JNICALL Java_com_startek_1eng_fm300sdk_FM300SDKWrapper_FP_1printf
(JNIEnv *, jobject obj, jstring str)
{
	jlong result = FAIL;

	printf("Hello...there");

	return result;
}