import java.awt.Color;
import java.awt.image.BufferedImage;

import com.google.zxing.qrcode.encoder.QRCode;

public class Comps {

	private static float colorDistance(int i1, int i2) {
		return ((new Color(i1).getRed() - new Color(i2).getRed()) / 256.0f);
	}

	private static float qualityGrade(BufferedImage i1, BufferedImage i2) {
		if (i1.getWidth() != i2.getWidth() || i1.getHeight() != i2.getHeight())
			throw new RuntimeException("Image size mismatch");

		int h = i1.getHeight();
		int w = i1.getWidth();
		float cnt = 0;
		for (int x = 0; x < h; x++)
			for (int y = 0; y < w; y++) {
				cnt += colorDistance(i1.getRGB(x, y), i2.getRGB(x, y));
			}

		return cnt / (w * h);
	}

	public static float qualityGrade(QRCode q, BufferedImage im, int X, int Y) {
		int w = im.getWidth();
		int h = im.getHeight();
		BufferedImage qPiece = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		for(int x=X; x<X+w; x++) for(int y=Y; y<Y+h; y++){
			int color = Color.WHITE.getRGB();
			if ( q.at(x, y) == 0 ) color = Color.BLACK.getRGB();
			qPiece.setRGB(x-X, y-Y, color);
		}
		
		return qualityGrade( qPiece, im );		
	}

}