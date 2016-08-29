package com.wangzhe.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jivesoftware.util.Log;

public class FileUtil {
	
	/**
	 * @param source
	 * @param dest
	 * @return
	 */
	public static boolean copy(InputStream in, OutputStream out)
	{
		try
		{
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1)
			{
				out.write(buf, 0, len);
			}

		} catch (FileNotFoundException ex)
		{
			Log.error("file not found" + ex.getMessage());
			return false;
		} catch (IOException e)
		{
			Log.error("io exception: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public static boolean saveFile(InputStream in, String filePath){
		try {
			OutputStream out = new FileOutputStream(filePath);
			FileUtil.copy(in, out);
			out.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static InputStream getFileInputStream(String filePath){
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}
}
