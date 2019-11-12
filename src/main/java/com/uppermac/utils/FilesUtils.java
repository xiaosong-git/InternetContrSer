package com.uppermac.utils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;

import org.apache.commons.lang3.StringUtils;

public class FilesUtils {
	/**
	 * 生成Byte流 TODO
	 * 
	 * @history @knownBugs @param @return @exception
	 */
	public static byte[] getBytesFromFile(File file) {
		byte[] ret = null;
		try {
			if (file == null) {
				// log.error("helper:the file is null!");
				return null;
			}
			FileInputStream in = new FileInputStream(file);
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] b = new byte[4096];
			int n;
			while ((n = in.read(b)) != -1) {
				out.write(b, 0, n);
			}
			in.close();
			out.close();
			ret = out.toByteArray();
		} catch (IOException e) {
			// log.error("helper:get bytes from file process error!");
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * 把流生成图片 TODO
	 * 
	 * @history @knownBugs @param @return @exception
	 */
	public static File getFileFromBytes(byte[] files, String outputFile, String fileName) {
		File ret = null;

		BufferedOutputStream stream = null;
		try {
			if (StringUtils.isBlank(fileName)) {
				ret = new File(outputFile);
			} else {
				ret = new File(outputFile +"/"+ fileName);
			}
			File fileParent = ret.getParentFile();
		
			
			if (!fileParent.exists()) {
				fileParent.mkdirs();
			}
			ret.createNewFile();

			FileOutputStream fstream = new FileOutputStream(ret);

			stream = new BufferedOutputStream(fstream);

			stream.write(files);

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// log.error("helper:get file from byte process error!");
					e.printStackTrace();
				}
			}
		}
		return ret;
	}

	/***
	 * 根据路径获取
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] getPhoto(String path) {
		byte[] data = null;
		FileImageInputStream input = null;
		try {
			input = new FileImageInputStream(new File(path));
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int numBytesRead = 0;
			while ((numBytesRead = input.read(buf)) != -1) {
				output.write(buf, 0, numBytesRead);
			}
			data = output.toByteArray();
			output.close();
			input.close();
		} catch (FileNotFoundException ex1) {
			ex1.printStackTrace();
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}
		return data;
	}
}
