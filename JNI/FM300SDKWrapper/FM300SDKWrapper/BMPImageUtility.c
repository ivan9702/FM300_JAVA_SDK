#include <stdio.h>
#include <string.h>
#include <math.h>
#include "fp_api.h"
#include "image_bmp.h"

short  Raw2GrayBMP(BYTE *bRawData,short nWidth ,short nHeight,BYTE *bBmpData,long nBmpLength)
{
    short i;
    struct FILE_HEADER file_head;
    struct INFO_HEADER image_info;
    static struct COLOR color_map[256];
	int bmpSize = 0;
	int BMPTOTALSize = sizeof(struct FILE_HEADER)
				 +sizeof(struct INFO_HEADER)
				 +sizeof(struct COLOR)*256
				 +nWidth*nHeight;
	if (bBmpData==NULL 
		|| nBmpLength<BMPTOTALSize)
	{
		return FAIL;
	}

    file_head.bfType=19778;
    file_head.bfSize=BMPTOTALSize;
    file_head.bfReseved1=file_head.bfReseved2=0;
    file_head.bfOffBits= sizeof(struct FILE_HEADER)+sizeof(struct INFO_HEADER)+sizeof(struct COLOR)*256;
	
	memcpy(bBmpData+bmpSize,&file_head,sizeof(struct FILE_HEADER));
	bmpSize+=sizeof(struct FILE_HEADER);
	//fwrite( (const char *)&file_head.bfType,1,2,hFile);	
	//fwrite( (const char *)&file_head.bfSize,1,4,hFile);	
	//fwrite( (const char *)&file_head.bfReseved1,1,2,hFile);	
	//fwrite( (const char *)&file_head.bfReseved2,1,2,hFile);	
	//fwrite( (const char *)&file_head.bfOffBits,1,4,hFile);	


    image_info.biSize=40;
    image_info.biWidth=nWidth;
    image_info.biHeight=nHeight;
    image_info.biPlanes=1;
    image_info.biBitCount=8;
    image_info.biCompression=0;
    image_info.biSizeImage=nWidth*nHeight;
    image_info.biXPelsPerMeter=image_info.biYPelsPerMeter=19685;
    image_info.biClrUsed=image_info.biClrImportant=256;

	memcpy(bBmpData+bmpSize,&image_info,sizeof(struct INFO_HEADER));
	bmpSize+=sizeof(struct INFO_HEADER);
	//fwrite( (const char *)&image_info,1,sizeof(struct INFO_HEADER),hFile);
  
    for ( i=0; i<256; i++ )
    {
        color_map[i].rgbBlue=(BYTE)i;
        color_map[i].rgbGreen=(BYTE)i;
        color_map[i].rgbRed=(BYTE)i;
        color_map[i].rgbReserved=0;

		memcpy(bBmpData+bmpSize,&(color_map[i]),sizeof(struct COLOR));
		bmpSize+=sizeof(struct COLOR);
		//fwrite((const char *)&color_map[i],1,sizeof(struct COLOR),hFile);
    }
  
    for(i=nHeight-1;i>=0;i--)
    {
		memcpy(bBmpData+bmpSize,bRawData+(long)i*nWidth,nWidth);
		//fwrite((const char *)bRawData+(long)i*nWidth,1,nWidth,hFile);
		bmpSize+=nWidth;
    }
	if (bmpSize==BMPTOTALSize)
		return OK;
	else
		return FAIL;
}