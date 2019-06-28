#define BYTE unsigned char
#define HFILE int

//data block
#pragma pack(push,1)
struct FILE_HEADER 
{
	unsigned short bfType;
	unsigned long bfSize;
	unsigned short bfReseved1;
	unsigned short bfReseved2;
	unsigned long bfOffBits;
};//14

struct INFO_HEADER 
{
	unsigned long biSize;
	unsigned long biWidth;
	unsigned long biHeight;
	unsigned short biPlanes;
	unsigned short biBitCount;

	unsigned long biCompression;
	unsigned long biSizeImage;
	unsigned long biXPelsPerMeter;
	unsigned long biYPelsPerMeter;
	unsigned long biClrUsed;
	unsigned long biClrImportant;
};//36+4=40

struct COLOR 
{
	BYTE rgbBlue;
	BYTE rgbGreen;
	BYTE rgbRed;
	BYTE rgbReserved;
};//4
#pragma pack(pop)