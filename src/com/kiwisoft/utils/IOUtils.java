package com.kiwisoft.utils;

/**
 * Utilities for I/O operations.
 *
 * @author Stefan Stiller
 */
public class IOUtils
{
	public static final String IMAGE_BMP="image/bmp";
	public static final String IMAGE_JPEG="image/jpeg";
	public static final String IMAGE_GIF="image/gif";
	public static final String IMAGE_PNG="image/png";
	public static final String IMAGE_XBM="image/x-xbitmap";
	public static final String IMAGE_ICO="image/vnd.microsoft.icon";
	public static final String IMAGE_TIFF="image/tiff";

	public static final String TEXT_XML = "text/xml";
	public static final String TEXT_PLAIN = "text/plain";

	public static final String APPLICATION_JAVA_BYTE_CODE="application/java-byte-code";
	public static final String APPLICATION_JAVA_SERIALIZED_OBJECT="application/java-serialized-object";
	public static final String APPLICATION_PDF="application/pdf";
	public static final String APPLICATION_POSTSCRIPT = "application/postscript";
	public static final String APPLICATION_ZIP="application/zip";

	private IOUtils()
	{
	}

	/**
	 * Tries to determine the mime type from the given byte array.
	 *
	 * @param data The data.
	 * @return The mime type or <code>null</code> if the mime type couldn't be determined.
	 */
	public static String getMimeType(byte[] data)
	{
		if (data!=null)
		{
			byte a1=0, a2=0, a3=0, a4=0, b1=0, b2=0, b3=0, b4=0;
			if (data.length>2)
			{
				a1=data[0];
				a2=data[1];
				if (a1==66 && a2==77) return IMAGE_BMP;
			}
			if (data.length>3)
			{
				a3=data[2];
				if (a1==-1 && a2==-40 && a3==-1) return IMAGE_JPEG;
			}
			if (data.length>4)
			{
				a4=data[3];
				if (a1==71 && a2==73 && a3==70 && a4==56) return IMAGE_GIF;
				if (a1==35 && a2==100 && a3==101 && a4==102) return IMAGE_XBM;
				if (a1==-54 && a2==-2 && a3==-70 && a4==-66) return APPLICATION_JAVA_BYTE_CODE;
				if (a1==-84 && a2==-19 && a3==0 && a4==5) return APPLICATION_JAVA_SERIALIZED_OBJECT;
				if (a1==37 && a2==80 && a3==68 && a4==70) return APPLICATION_PDF;
				if (a1==0 && a2==0 && (a3==1 || a3==2) && a4==0) return IMAGE_ICO;
				if (a1==73 && a2==73 && a3==42 && a4==0) return IMAGE_TIFF;
				if (a1==77 && a2==77 && a3==0 && a4==42) return IMAGE_TIFF;
				if (a1==80 && a2==75 && a3==3 && a4==4) return APPLICATION_ZIP;
				if (a1==-59 && a2==-48 && a3==-45 && a4==-58) return APPLICATION_POSTSCRIPT;
			}
			if (data.length>5)
			{
				b1=data[4];
				if (a1==60 && a2==63 && a3==120 && a4==109 && b1==108) return TEXT_XML;
			}
			if (data.length>8)
			{
				b2=data[5];
				b3=data[6];
				b4=data[7];
				if (a1==-119 && a2==80 && a3==78 && a4==71 && b1==13 && b2==10 && b3==26 && b4==10) return IMAGE_PNG;
			}
			boolean binary=false;
			for (int i=0;i<1000 && i<data.length;i++)
			{
				byte b=data[i];
				if (b>=0 && b<32)
				{
					if (b==9 || b==10 || b==13) continue;
					binary=true;
					break;
				}
			}
			if (!binary) return TEXT_PLAIN;
		}
		return null;
	}
}
