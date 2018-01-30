import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;



public class ImageSynthesizer2 {
	
	static boolean[] flipOptions = {false, true};
	static double[] scaleOptions = {1.0,0.8,1.2};
	static double[] rotateOptions = {0,-40,-20,20,40};
	static double[] blurOptions = {0,5,10};
	//boolean[] sharpenOptions = {false, true};
	static double[] gammaOptions = {1,0.5, 3.0};
	
	public static void synthesize(String inDir, String outDir, String classname)
	{	
		//list original files
		List<File> originalFiles = (List<File>) FileUtils.listFiles(new File(inDir), new String[]{"JPG","jpg"}, true);
		System.out.println("@ Processing class "+classname+" of "+originalFiles.size()+" images.");
		int originalCount = 0;
		int currentPercentDone = 0;
		int counting=0;
		for(File originalFile: originalFiles)
		{	
			Opener opener = new Opener();  
			ImagePlus imp = opener.openImage(originalFile.getAbsolutePath());
			//save original image
			IJ.saveAs(imp, "JPG", outDir+"/"+classname+"."+originalCount+".0.JPG");  
			int synCount = 1;
			 
			//flip
			for(boolean flip: flipOptions)
			{	
				for(double scalex: scaleOptions)
				{	for(double scaley: scaleOptions)
					{
						for(double rotate: rotateOptions)
						{
							for(double blur: blurOptions)
							{
								for(double gamma: gammaOptions)
								{	ImagePlus dup = imp.duplicate();
									ImageProcessor ip = dup.getProcessor();
									if(flip) ip.flipHorizontal();
									
									//resizing
									int new_width = (int)(ip.getWidth() * scalex);  
									int new_height = (int)(ip.getHeight() * scaley);
									ip.setInterpolate(true); // bilinear  
									ImageProcessor ip2 = ip.resize(new_width, new_height); // of the same type as the original 
									dup.setProcessor(dup.getTitle(), ip2);
									
									//rotating
									ip2.setInterpolate(true); // bilinear 
									ip2.setBackgroundValue(0);
									ip2.rotate(rotate);
									
									//blurring
									ip2.blurGaussian(blur);
									
									//setting gamma
									ip2.gamma(gamma);
									
									//save
									IJ.saveAs(dup, "JPG", outDir+"/"+classname+"."+originalCount+"."+synCount+".JPG");  
									synCount++;
									counting++;
									if(counting==50000)
									{
										return;
									}
								}
							}
						}
					}
				}
			}
			
			
			originalCount++;
			int newPercentDone = 100*originalCount/originalFiles.size();
			if(newPercentDone>currentPercentDone)
			{	currentPercentDone = newPercentDone;
				System.out.println("@ Done "+originalCount+"/"+originalFiles.size()+" = "+currentPercentDone+"%");
			}
		}
	}
	
	public static void main(String[] args)
	{		
		//synthesize("C:/Users/Mwit1203/Desktop/data_manipulation/src/img_type10", "C:/Users/Mwit1203/Desktop/data_manipulation/src/img_type10_OUT", "J");
		synthesize("D:/img_type11", "D:/img_type11_OUT", "K");
		synthesize("D:/img_type12", "D:/img_type12_OUT", "L");
		synthesize("D:/img_type13", "D:/img_type13_OUT", "M");
		synthesize("D:/img_type14", "D:/img_type14_OUT", "N");
		//synthesize(args[0], args[1], args[2]);
	}

}
