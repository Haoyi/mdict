package com.xiaoyan.mdict.dictionary;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.InflaterOutputStream;

import com.knziha.plod.dictionary.ripemd128;



public class  dictionaryUtility{
	
    
    public static class nodeComparable<T1 extends Comparable<T1>,T2> implements Comparable<nodeComparable<T1,T2>>{
    	public T1 key;
    	public T2 value;
    	public nodeComparable(T1 k,T2 v){
    		key=k;value=v;
    	}
    	public int compareTo(nodeComparable<T1,T2> other) {
    		if(key.getClass()==String.class) {
    			return ((String)key)
    					.toLowerCase().replace(" ","").replace("-","")
    					.compareTo(((String)other.key)    					
						.toLowerCase().replace(" ","").replace("-",""));
    		}
    		else
    			return this.key.compareTo(other.key);
    	}
    	public String toString(){
    		return key+"_"+value;
    	}
    }
	
	public static class packaging<T>{
		public packaging(boolean pEndianness, int pSize) {
			endianness = pEndianness;
			size = pSize;
		}
		T value;
		boolean endianness; //true:bigendian,false:littleendian
		int size;
	}
    
	public static int setChecksumValue(byte[] pChecksum,boolean pEndianness,int pSize) {
		int value = 0;
		int size = pSize;
		if (pEndianness) {
			int index = 0;
			while (index<size) {
				value <<= 8;
				value |= (pChecksum[index++] & 0x000000ff);
			}
		} else {
			int index = size-1;
			while (index>=0) {
				value <<= 8;
				value |= (pChecksum[index--] & 0x000000ff);
			}
		}
		return value;
	}
	
	public static int calcChecksum(byte[] bytes) {
        Adler32 a32 = new Adler32();
        a32.update(bytes);
        int sum = (int) a32.getValue();
        return sum;
    }
	public static int calcChecksum(byte[] bytes,int off,int len) {
        Adler32 a32 = new Adler32();
        a32.update(bytes,off,len);
        int sum = (int) a32.getValue();
        return sum;
    }
    public static byte[] zlibDecompress(byte[] encdata,int offset) {
	    try {
			    ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			    InflaterOutputStream inf = new InflaterOutputStream(out); 
			    inf.write(encdata,offset, encdata.length-offset); 
			    inf.close(); 
			    return out.toByteArray(); 
		    } catch (Exception ex) {
		    	ex.printStackTrace(); 
		    	return "ERROR".getBytes(); 
		    }
    }
    
    public static byte[] zlibDecompress(byte[] encdata,int offset,int ln) {
	    try {
			    ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			    InflaterOutputStream inf = new InflaterOutputStream(out); 
			    inf.write(encdata,offset, ln); 
			    inf.close(); 
			    return out.toByteArray(); 
		    } catch (Exception ex) {
		    	ex.printStackTrace(); 
		    	return "ERROR".getBytes(); 
		    }
    }
    
    static byte[] fastDecrypt(byte[] data,byte[] key){ 
	    long previous = 0x36;
	    for(int i=0;i<data.length;i++){
	    	int ddd = data[i]&0xff;
	    	long t = (ddd >> 4 | ddd << 4) & 0xff;
	        t = t ^ previous ^ (i & 0xff) ^ (key[(i % key.length)]&0xff);
	        previous = ddd;
	        data[i] = (byte) t;
        }
	    return data;
    }
    
    public static byte[] mdxDecrypt(byte[] pValue) throws IOException{
		ByteArrayOutputStream data = new ByteArrayOutputStream() ;
		data.write(pValue,4,4);
		data.write(ripemd128.packIntLE(0x3695));
	    byte[]  key = ripemd128.ripemd128(data.toByteArray());
	    data.reset();
	    data.write(pValue,0,8);
	    byte[] value = new byte[pValue.length-8];
	    System.arraycopy(pValue, 8, value, 0, pValue.length-8);
	    data.write(fastDecrypt(value, key));
	    return data.toByteArray();
    }
    
    public static byte[] toLH(int n) {  
    	  byte[] b = new byte[4];  
    	  b[0] = (byte) (n & 0xff);  
    	  b[1] = (byte) (n >> 8 & 0xff);  
    	  b[2] = (byte) (n >> 16 & 0xff);  
    	  b[3] = (byte) (n >> 24 & 0xff);  
    	  return b;  
    	} 
    

    
	public static short getShort(byte buf1, byte buf2) 
    {
        short r = 0;
        r |= (buf1 & 0x00ff);
        r <<= 8;
        r |= (buf2 & 0x00ff);
        return r;
    }
    
    public static int getInt(byte buf1, byte buf2, byte buf3, byte buf4) 
    {
        int r = 0;
        r |= (buf1 & 0x000000ff);
        r <<= 8;
        r |= (buf2 & 0x000000ff);
        r <<= 8;
        r |= (buf3 & 0x000000ff);
        r <<= 8;
        r |= (buf4 & 0x000000ff);
        return r;
    }
    public static long getLong(byte[] buf) 
    {
        long r = 0;
        r |= (buf[0] & 0xff);
        r <<= 8;
        r |= (buf[1] & 0xff);
        r <<= 8;
        r |= (buf[2] & 0xff);
        r <<= 8;
        r |= (buf[3] & 0xff);
        r <<= 8;
        r |= (buf[4] & 0xff);
        r <<= 8;
        r |= (buf[5] & 0xff);
        r <<= 8;
        r |= (buf[6] & 0xff);
        r <<= 8;
        r |= (buf[7] & 0xff);
        return r;
    }
    public static long getLong(byte buf1, byte buf2, byte buf3, byte buf4,byte buf11, byte buf21, byte buf31, byte buf41) 
    {
        long r = 0;
        r |= (buf1 & 0x000000ff);
        r <<= 8;
        r |= (buf2 & 0x000000ff);
        r <<= 8;
        r |= (buf3 & 0x000000ff);
        r <<= 8;
        r |= (buf4 & 0x000000ff);
        r <<= 8;
        r |= (buf11 & 0x000000ff);
        r <<= 8;
        r |= (buf21 & 0x000000ff);
        r <<= 8;
        r |= (buf31 & 0x000000ff);
        r <<= 8;
        r |= (buf41 & 0x000000ff);
        return r;
    }
    public static String byteTo16(byte bt){
        String[] strHex={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
        String resStr="";
        int low =(bt & 15);
        int high = bt>>4 & 15;
        resStr = strHex[high]+strHex[low];
        return resStr;
    }
    


    public static int toInt(byte[] buffer,int offset) {   
        int  values = 0;   
        for (int i = 0; i < 4; i++) {    
            values <<= 8; values|= (buffer[offset+i] & 0xff);   
        }   
        return values;  
     }     
    public static long toLong(byte[] buffer,int offset) {   
        long  values = 0;   
        for (int i = 0; i < 8; i++) {    
            values <<= 8; values|= (buffer[offset+i] & 0xff);   
        }   
        return values;  
     } 
    public static char toChar(byte[] buffer,int offset) {   
        char  values = 0;   
        for (int i = 0; i < 2; i++) {    
            values <<= 8; values|= (buffer[offset+i] & 0xff);   
        }   
        return values;  
     }
}
	


