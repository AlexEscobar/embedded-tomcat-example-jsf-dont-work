/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.innotech.xraymanagerapp.controller.export;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.Opener;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Alexander Escobar L.
 */
public class IMIFileOpener extends Opener {
    
    
	public ImagePlus openUsingImageIO(File f) {
		ImagePlus imp;
		BufferedImage img = null;
//		File f = new File(path);
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			IJ.error("Open Using ImageIO", ""+e);
		} 
		if (img==null)
			return null;
		if (img.getColorModel().hasAlpha()) {
			int width = img.getWidth();
			int height = img.getHeight();
			BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics g = bi.getGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,width,height);
			g.drawImage(img, 0, 0, null);
			img = bi;
		}
		imp = new ImagePlus(f.getName(), img);
		FileInfo fi = new FileInfo();
		fi.fileFormat = FileInfo.IMAGEIO;
		fi.fileName = f.getName();
		fi.directory = f.getParent()+File.separator;
		imp.setFileInfo(fi);
		return imp;
	}
    
}
