#include <windows.h>
#include "fp_api.h"
/*
	finalizeFPCode
		pcode -> decode (785388) -> minu_code -> encode(23545953) -> fpcode

  	decode_pcode(p_code, 785388L, 256, 256, 50))
	encode_pcode(p_code, 23545953L, 256, 256, 50))

	fp_enroll
		pcode1-> decode (785388) ->minu_code1
		pcode2-> decode (785388) ->minu_code2 (minu_code1+minu_code2+minu_code3) -> encode(23545953) -> fpcode
		pcode2-> decode (785388) ->minu_code3
*/
short decode_pcode(BYTE *p_code, long key, short Width, short Height, short reduce_to_num) 	
{
	static BYTE buf[ FP_CODE_LENGTH ];
	static BYTE mask[4];
	BYTE chksum=0;
	BYTE a_char;
	short i, j,k;
	short rtn_hdl = 100;
	//char debug_str[256];

	//sprintf(debug_str,"decode_pcode %d %d %d %d %d ",*p_code,*(p_code+1),*(p_code+2),*(p_code+3),*(p_code+4));
	//MessageBox(NULL,debug_str,NULL,NULL);

	memset( buf, 0, FP_CODE_LENGTH );
	if ( memcmp( p_code, buf, FP_CODE_LENGTH ) == 0 )
		return S_FPCODE_INVALID;


	mask[0] = ( BYTE ) ( key / 0x1000000 );
	mask[1] = ( BYTE ) ( ( key % 0x1000000 ) / 0x10000 );
	mask[2] = ( BYTE ) ( ( key % 0x10000 ) / 0x100 );
	mask[3] = ( BYTE ) key ;

	//sprintf(debug_str,"decode_pcode %d %d %d %d ",mask[0],mask[1],mask[2],mask[3]);
	//MessageBox(NULL,debug_str,NULL,NULL);

	memcpy( buf, p_code, FP_CODE_LENGTH );

	for ( i=0; i<FP_CODE_LENGTH; i++ )
	{
		a_char = ( BYTE ) i;
		buf[i] = ( BYTE ) ( buf[i] ^ a_char );
	}
    
	for ( i=0; i<(FP_CODE_LENGTH-1); i++ )
		chksum = chksum ^ buf[i];

	//sprintf(debug_str,"decode_pcode chksum %d ",chksum);
	//MessageBox(NULL,debug_str,NULL,NULL);

	if ( buf[FP_CODE_LENGTH-1] != chksum )
		return S_FPCODE_INVALID;

	k=FP_CODE_LENGTH/4;
	for ( i=0; i<k; i++ )
		for ( j=0; j<4; j++ )
			buf[i*4+j] = ( BYTE ) ( buf[i*4+j] ^ mask[j] );

	//sprintf(debug_str,"decode_pcode buf[0] = %d reduce_to_num = %d ",buf[0],reduce_to_num);
	//MessageBox(NULL,debug_str,NULL,NULL);

	if ( buf[0] > reduce_to_num )
		return( -1000 - rtn_hdl - 1 );

	//MessageBox(NULL,"decode_pcode 3",NULL,NULL);

	i=1;
	for ( j=0; j<(short)buf[0]; j++ )
	{
		if ( (buf[ i ] > Width-2 ) ||(buf[i]<1) )
			return( -1000 - rtn_hdl - 2 );
		i++;
		if ( (buf[ i ] > Height-2 )|| (buf[i]<1) )
			return( -1000 - rtn_hdl - 3 );          
		i++;
		if( ( buf[ i ] >= 16 ) )
			return( -1000 - rtn_hdl - 4 );             
		i++;
	}

	memcpy( p_code, buf, FP_CODE_LENGTH );

	return OK;
}

short encode_pcode(BYTE *p_code, long key, short Width, short Height, short reduce_to_num)  //fpcode : 23545953L pcode  : 785388L
{
	static BYTE  mask[4];
	BYTE a_char;
	short i, j,k;
	short rtn_hdl = 90;
	//char debug_str[256];

	//sprintf(debug_str,"encode_pcode %d %d ",p_code[0],reduce_to_num);
	//MessageBox(NULL,debug_str,NULL,NULL);	

	if ( ( p_code[0] > reduce_to_num ) || ( p_code[0] < 4 ) )
		return( -1000 - rtn_hdl - 1 );
       
	i=1;
	for ( j=0; j<p_code[0]; j++ )
	{
		if ( (p_code[ i ] > Width-2 ) || (p_code[i]<1) )
			return( -1000 - rtn_hdl - 2 );
		i++;
		if ( ( p_code[ i ] > Height-2) || (p_code[i]<1) )
			return( -1000 - rtn_hdl - 3 );
		i++;
		if ( ( p_code[ i ] >= 16) )
			return( -1000 - rtn_hdl - 4 );
		i++;
	}

	mask[0] = ( BYTE ) ( key / 0x1000000 );
	mask[1] = ( BYTE ) ( ( key % 0x1000000 ) / 0x10000 );
	mask[2] = ( BYTE ) ( ( key % 0x10000 ) / 0x100 );
	mask[3] = ( BYTE ) key ;

	//sprintf(debug_str,"decode_pcode %d %d %d %d ",mask[0],mask[1],mask[2],mask[3]);
	//MessageBox(NULL,debug_str,NULL,NULL);    

	k=FP_CODE_LENGTH/4;
	for ( i=0; i<k; i++ )
		for ( j=0; j<4; j++ )
			p_code[i*4+j] = ( BYTE ) ( p_code[i*4+j] ^ mask[j] );

	p_code[FP_CODE_LENGTH-1] = 0;
	for ( i=0; i<(FP_CODE_LENGTH-1); i++ )
		p_code[FP_CODE_LENGTH-1] = p_code[FP_CODE_LENGTH-1] ^ p_code[i];

	for ( i=0; i<FP_CODE_LENGTH; i++ )
	{
		a_char = ( BYTE ) i;
		p_code[i] = ( BYTE ) (  p_code[i] ^ a_char );
	}
	return OK;
}