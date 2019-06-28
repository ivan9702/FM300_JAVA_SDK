#include <jni.h>
#include <jawt.h>
#include <jawt_md.h>
#include <windows.h>
#include <stdio.h>
#include <TCHAR.H>
#include "fp_api.h"

#define REGISTRY_JRE_KEYNAME            "SOFTWARE\\JavaSoft\\Java Runtime Environment"
#define REGISTRY_JRE_JAVAHOME_VALUENAME "JavaHome"
extern HMODULE g_hJAWT;
extern TCHAR AWT_LIB_Name[_MAX_PATH];
typedef jboolean (JNICALL *PJAWT_GETAWT)(JNIEnv*, JAWT*);

//Example Information form : http://forum.java.sun.com/thread.jspa?threadID=570068&messageID=2819768
DWORD GetJAWTLib(TCHAR* sJAWTLib, int iBufferSize)
{
    HKEY hKey;
    TCHAR lpValueName[MAX_PATH];
    unsigned long lpType;
    unsigned long lpcbData;
    unsigned char sCurrentVersion[MAX_PATH];
    unsigned char sJREJavaHome[MAX_PATH];
    
    lpcbData = MAX_PATH;
    //_stprintf(lpValueName, "%s%c", REGISTRY_JRE_KEYNAME, '\0');
	_stprintf_s(lpValueName, sizeof(lpValueName) ,"%s%c", REGISTRY_JRE_KEYNAME, '\0');
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpValueName, 0, KEY_READ, &hKey)!=ERROR_SUCCESS) {
        MessageBox(NULL, "Java Runtime Environment cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        return FAIL;
    }    
    
    if (RegQueryValueEx(hKey, "CurrentVersion", NULL, &lpType, sCurrentVersion, &lpcbData)!=ERROR_SUCCESS) {
        MessageBox(NULL, "Java Runtime Environment current version cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        RegCloseKey(hKey);
        return FAIL;
    }    
    
    lpcbData = MAX_PATH;
    lpValueName[0] = '\0';
    
    //_stprintf(lpValueName, "%s\\%s%c", REGISTRY_JRE_KEYNAME, sCurrentVersion, '\0');
	_stprintf_s(lpValueName,sizeof(lpValueName), "%s\\%s%c", REGISTRY_JRE_KEYNAME, sCurrentVersion, '\0');
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpValueName, 0, KEY_READ, &hKey)!=ERROR_SUCCESS) {
        MessageBox(NULL, "Java Runtime Environment cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        return FAIL;
    }
 
    if (RegQueryValueEx(hKey, REGISTRY_JRE_JAVAHOME_VALUENAME, NULL, &lpType, sJREJavaHome, &lpcbData)!=ERROR_SUCCESS) {
        MessageBox(NULL, "JRE JavaHome property cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        RegCloseKey(hKey);
        return FAIL;
    }
    
    RegCloseKey(hKey);
    
    //_stprintf(sJAWTLib, _T("%s\\bin\\jawt.dll"), sJREJavaHome);
	_stprintf_s(sJAWTLib,sizeof(sJAWTLib), _T("%s\\bin\\jawt.dll"), sJREJavaHome);
    //_tprintf("==sJREJavaHome: %s\r\n",sJREJavaHome);
    //_tprintf("==sJAWTLib: %s\r\n",sJAWTLib);
    return OK;
}
HWND GetJAVAHWND(JNIEnv *env, jobject obj, jobject jcomponent)
{
	HWND result = NULL;
	if (g_hJAWT==NULL && GetJAWTLib(AWT_LIB_Name,_MAX_PATH) == 0)
	{
		g_hJAWT = LoadLibrary((LPCTSTR)AWT_LIB_Name);
		//if (g_hJAWT==NULL)
			//_tprintf("LoadLibrary %S Failed ,ERRORCODE:0x%X\r\n",AWT_LIB_Name,GetLastError());
	}
		
	if (g_hJAWT!=NULL)
	{
		PJAWT_GETAWT                  pjawt_GetAWT;
		JAWT                          jawt;
		JAWT_DrawingSurface*          jawt_DrawingSurface = NULL;
		JAWT_DrawingSurfaceInfo*      jawt_DrawingSurfaceInfo = NULL;
		JAWT_Win32DrawingSurfaceInfo* jawt_Win32DrawingSurfaceInfo;
		jint lock;
		__try
		{
			pjawt_GetAWT = (PJAWT_GETAWT)GetProcAddress(g_hJAWT, "_JAWT_GetAWT@8");
			if (pjawt_GetAWT==NULL) 
			{
				//_tprintf("ERR!! GetProcAddress : %d",pjawt_GetAWT);
				__leave;
			}else
			{
				jawt.version = JAWT_VERSION_1_3;//JAWT_VERSION_1_4;
				if (pjawt_GetAWT(env, &jawt) == JNI_FALSE) 
				{
					//_tprintf("ERR!! pjawt_GetAWT : %d",JNI_FALSE);
					__leave;
				}else
				{
					jawt_DrawingSurface = jawt.GetDrawingSurface(env, jcomponent);
					if (jawt_DrawingSurface==NULL) 
					{
						//_tprintf("ERR!! GetDrawingSurface : %d",jawt_DrawingSurface);
						__leave;
					}else
					{
						lock = jawt_DrawingSurface->Lock(jawt_DrawingSurface);
						if((lock & JAWT_LOCK_ERROR) != 0)
						{
							//_tprintf("ERR!! Lock : %d",lock);
							__leave;
						}else
						{
							jawt_DrawingSurfaceInfo = jawt_DrawingSurface->GetDrawingSurfaceInfo(jawt_DrawingSurface);
							if (jawt_DrawingSurfaceInfo==NULL) {
								//_tprintf("ERR!! GetDrawingSurfaceInfo : %d",jawt_DrawingSurfaceInfo);
								__leave;
							}else
							{
								jawt_Win32DrawingSurfaceInfo = (JAWT_Win32DrawingSurfaceInfo*)jawt_DrawingSurfaceInfo->platformInfo;
								result = jawt_Win32DrawingSurfaceInfo->hwnd;
							}
						}
					}
				}
			}
		}__finally
		{
			if (jawt_DrawingSurface)
			{
				if (jawt_DrawingSurfaceInfo)
					jawt_DrawingSurface->FreeDrawingSurfaceInfo(jawt_DrawingSurfaceInfo);
				if (!((lock & JAWT_LOCK_ERROR) != 0))
					jawt_DrawingSurface->Unlock(jawt_DrawingSurface);
				jawt.FreeDrawingSurface(jawt_DrawingSurface);
			}
		}
	}
	if (g_hJAWT)
		FreeLibrary(g_hJAWT);
	//_tprintf("HWND:%d\r\n",result);
	return result;
}
/*
Information form : http://forum.java.sun.com/thread.jspa?threadID=570068&messageID=2819768
You can get the information from the registry:

#define REGISTRY_JRE_KEYNAME            "SOFTWARE\\JavaSoft\\Java Runtime Environment"
#define REGISTRY_JRE_JAVAHOME_VALUENAME "JavaHome"
 
typedef jboolean (JNICALL *PJAWT_GETAWT)(JNIEnv*, JAWT*);
...
DWORD GetJAWTLib(char* sJAWTLib, int iBufferSize)
{
    HKEY hKey;
    char lpValueName[MAX_PATH];
    long lpType;
    long lpcbData;
    char sCurrentVersion[MAX_PATH];
    char sJREJavaHome[MAX_PATH];
    
    lpcbData = MAX_PATH;
    sprintf(lpValueName, "%s%c", REGISTRY_JRE_KEYNAME, '\0');
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpValueName, 0, KEY_READ, &hKey)!=ERROR_SUCCESS) {
        MessageBox(NULL, "Java Runtime Environment cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        return -1;
    }    
    
    if (RegQueryValueEx(hKey, "CurrentVersion", NULL, &lpType, sCurrentVersion, &lpcbData)!=ERROR_SUCCESS) {
        MessageBox(NULL, "Java Runtime Environment current version cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        RegCloseKey(hKey);
        return -1;
    }    
    
    lpcbData = MAX_PATH;
    lpValueName[0] = '\0';
    
    sprintf(lpValueName, "%s\\%s%c", REGISTRY_JRE_KEYNAME, sCurrentVersion, '\0');
    if (RegOpenKeyEx(HKEY_LOCAL_MACHINE, lpValueName, 0, KEY_READ, &hKey)!=ERROR_SUCCESS) {
        MessageBox(NULL, "Java Runtime Environment cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        return -1;
    }
 
    if (RegQueryValueEx(hKey, REGISTRY_JRE_JAVAHOME_VALUENAME, NULL, &lpType, sJREJavaHome, &lpcbData)!=ERROR_SUCCESS) {
        MessageBox(NULL, "JRE JavaHome property cannot be found.\n", "Error", MB_OK | MB_ICONERROR);
        RegCloseKey(hKey);
        return -1;
    }
    
    RegCloseKey(hKey);
    
    sprintf(sJAWTLib, "%s\\bin\\jawt.dll", sJREJavaHome);
    
    return 0;
}



The you can load dynamically the DLL:

    PJAWT_GETAWT                  pjawt_GetAWT;
    JAWT                          jawt;
    JAWT_DrawingSurface*          jawt_DrawingSurface = NULL;
    JAWT_DrawingSurfaceInfo*      jawt_DrawingSurfaceInfo = NULL;
    JAWT_Win32DrawingSurfaceInfo* jawt_Win32DrawingSurfaceInfo;
    jint lock;
    
    if (hJAWTLib!=NULL) {
        pjawt_GetAWT = (PJAWT_GETAWT)GetProcAddress(hJAWTLib, "_JAWT_GetAWT@8");
        if (pjawt_GetAWT==NULL) {
            return FALSE;
        }    
        
        jawt.version = JAWT_VERSION_1_4;
        if (pjawt_GetAWT(g_env, &jawt) == JNI_FALSE) {
            return FALSE;
        }
        
        jawt_DrawingSurface = jawt.GetDrawingSurface(g_env, jobject_Component);
        if (jawt_DrawingSurface==NULL) {
            return FALSE;
        }
        
        lock = jawt_DrawingSurface->Lock(jawt_DrawingSurface);
        if((lock & JAWT_LOCK_ERROR) != 0) {
            return FALSE;
        }
        
        jawt_DrawingSurfaceInfo = jawt_DrawingSurface->GetDrawingSurfaceInfo(jawt_DrawingSurface);
        if (jawt_DrawingSurfaceInfo==NULL) {
            return FALSE;
        }    
        
        jawt_Win32DrawingSurfaceInfo = (JAWT_Win32DrawingSurfaceInfo*)jawt_DrawingSurfaceInfo->platformInfo;
        
        // Do something... 
        
        jawt_DrawingSurface->FreeDrawingSurfaceInfo(jawt_DrawingSurfaceInfo);
        jawt_DrawingSurface->Unlock(jawt_DrawingSurface);
        jawt.FreeDrawingSurface(jawt_DrawingSurface);
        
    } else {
        return FALSE;
    }        


*/