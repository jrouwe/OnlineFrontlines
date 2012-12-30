package onlinefrontlines.utils;

import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.servlet.http.*;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Class that stores images uploaded by users
 * 
 * @author jorrit
 * 
 * Copyright (C) 2009-2013 Jorrit Rouwe
 * 
 * This file is part of Online Frontlines.
 *
 * Online Frontlines is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Online Frontlines is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Online Frontlines.  If not, see <http://www.gnu.org/licenses/>.
 */
public class UploadedImageManager 
{
	/**
	 * Singleton instance
	 */
	private static UploadedImageManager instance = new UploadedImageManager();
	
	/**
	 * Maximum image size
	 */
	private static final int MAX_FILE_SIZE = 25 * 1024;
	
	/**
	 * Exception generated by adding images
	 */
	public static class AddException extends Exception
	{
		private static final long serialVersionUID = 0;

		public AddException(String message)
		{
			super(message);
		}
	}
	
	/**
	 * Supported image type
	 */
	private static class ImageType
	{
		public String contentType;
		public String extension;
		
		public ImageType(String contentType, String extension)
		{
			this.contentType = contentType;
			this.extension = extension;
		}
	}
	
	/**
	 * Supported types
	 */
	private static ImageType[] supportedTypes =
	{
		new ImageType("image/png", "png"),
		new ImageType("image/x-png", "png"),
		new ImageType("image/jpeg", "jpg"),
		new ImageType("image/pjpeg", "jpg"),
		new ImageType("image/bmp", "bmp"),
		new ImageType("image/gif", "gif")
	};
	
	/**
	 * Singleton access
	 */
	public static UploadedImageManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Add new image to the server
	 * 
	 * @param prefix Prefix for the image
	 * @param id Id for the image
	 * @param in Input stream that contains the image data
	 * @param contentType File type of the image
	 * @throws AddException 
	 */
	public void addImage(String prefix, int id, InputStream in, String contentType) throws AddException
	{
		// Find type
		ImageType type = null;
		for (ImageType t : supportedTypes)
			if (t.contentType.equals(contentType))
			{
				type = t;
				break;
			}
		if (type == null)
			throw new AddException("invalidFileType");
		
		// Remove previous image
		removeImage(prefix, id);
		
		try
		{
			// Copy file 
			String outFileName = GlobalProperties.getInstance().getString("images.folder") + "/" + prefix + "_" + Integer.toString(id) + "." + type.extension;
			File outFile = new File(outFileName);
	        OutputStream out = new FileOutputStream(outFile);    
			try
			{
		        byte[] buf = new byte[1024];
		        int len;
		        while ((len = in.read(buf)) > 0) 
		            out.write(buf, 0, len);        
			}
			finally
			{
		        out.close();
			}
		}
		catch (IOException e)
		{
			throw new AddException("failedFileUpload");
		}
	}
	
	/**
	 * Add image from http request
	 * 
	 * @param prefix Prefix for the image
	 * @param id Id for the image
	 * @param request Request that contains the image data
	 * @param width Max with for the image
	 * @param height Max height for the image
	 * @throws AddException
	 */
	@SuppressWarnings("unchecked")
	public void addImage(String prefix, int id, HttpServletRequest request, int width, int height) throws AddException
	{
		try
		{
			// Get files from request
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        factory.setSizeThreshold(MAX_FILE_SIZE);
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        upload.setSizeMax(MAX_FILE_SIZE);	        
	        List<FileItem> items = (List<FileItem>)upload.parseRequest(request);
	
	        // Find correct attachment
	        for (FileItem item : items) 
	            if (!item.isFormField()) 
	            {
	                // Skip file uploads that don't have a file name - meaning that no file was selected.
	                if (item.getName() == null || item.getName().trim().length() < 1)
	                	continue;
	                
	                try
	                {
		        		// Read image to see if it is correct
		                InputStream is = item.getInputStream();
		                try
		                {
		                	BufferedImage image = ImageIO.read(is);
		                	if (image == null)
		                		throw new AddException("imageCorrupt");
			        		if (image.getHeight() != height || image.getWidth() != width)
			        			throw new AddException("invalidResolution");
		                }
		                finally
		                {
		                	is.close();
		                }
	
		        		// Add the image
		        		is = item.getInputStream();
		                try
		                {
		                	addImage(prefix, id, is, item.getContentType());
		                }
		                finally
		                {
		                	is.close();
		                }
		                break;
	                }
	                catch (IOException e)
	                {
	                	throw new AddException("imageCorrupt");
	                }
	            }
	    } 
		catch (FileUploadException e) 
		{
			throw new AddException("failedFileUpload");
		}
	}
	
	/**
	 * Remove previously uploaded image
	 * 
	 * @param prefix Prefix for the image
	 * @param id Id for the image
	 */
	public void removeImage(String prefix, int id)
	{
		// Find image
		String imageName  = prefix + "_" + Integer.toString(id);		
		for (ImageType t : supportedTypes)
		{
			String fullName = imageName + "." + t.extension;
			new File(GlobalProperties.getInstance().getString("images.folder") + "/" + fullName).delete();
		}
	}
		
	/**
	 * Get URL to pass to the web page for downloading the image
	 * 
	 * @param prefix Prefix for the image
	 * @param id Id of the image
	 * @return Path relative to the webapp
	 */
	public String getImageURL(String prefix, int id)
	{		
		// Find image
		String imageName = prefix + "_" + Integer.toString(id);		
		for (ImageType t : supportedTypes)
		{
			String fullName = imageName + "." + t.extension;
			if (new File(GlobalProperties.getInstance().getString("images.folder") + "/" + fullName).exists())
				return GlobalProperties.getInstance().getString("images.url") + "/" + fullName;
		}

		// Not found
		return GlobalProperties.getInstance().getString("images.url") + "/" + prefix + "_dummy.gif";
	}
}
