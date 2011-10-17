import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


	public  class QRResult{
		private static int stampCnt = 0;
		int stamp;
		
		BitMatrix matrix;
		BufferedImage pattern;
		
		public QRResult(BitMatrix matrix, BufferedImage pattern){
			stamp = stampCnt++;
			this.matrix = matrix;
			this.pattern = pattern;
		}
		public BufferedImage getBufferedImage(){
			BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);
			if (pattern == null) return image;
			
	        BufferedImage dimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);  
	        Graphics2D g = dimg.createGraphics();
	        g.setComposite(AlphaComposite.Src);  
	        g.drawImage(image, null, 0, 0);  
	        g.dispose();  
	        int patternW = pattern.getWidth();
	        int patternH = pattern.getHeight();
	        for(int i = 0; i < dimg.getHeight(); i++) {  
	            for(int j = 0; j < dimg.getWidth(); j++) {  
	                if(dimg.getRGB(j, i) == Color.BLACK.getRGB()) {  
	                	dimg.setRGB(j, i, pattern.getRGB(j % patternW, i % patternH));  
	                }
	            }  
	        }
	        return dimg;  
		}

		public Icon getIcon(){
			return new ImageIcon(getBufferedImage());
		}
	}
