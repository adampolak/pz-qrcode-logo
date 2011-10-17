import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;



public class QRGenWorker extends SwingWorker<QRResult, QRResult> {
	
	private JLabel displayLabel = null;
	private QRResult lastResult = null;
	BufferedImage pattern = null;
	private String text;
	private int dim;
	
	public QRGenWorker(JLabel displayLabel, String text, int dim, BufferedImage pattern){
		this.displayLabel = displayLabel;
		this.text = text;
		this.dim = dim;
		this.pattern = pattern;
		this.execute();
	}
	
	
	@Override
	protected QRResult doInBackground() throws Exception {
		System.out.println("Background work...");
		while(! isCancelled()){
			System.out.println("Sleeping...");
			Thread.sleep(1000);
			
			// TODO: jakis kod, ktory cos robi konkretnego?
			QRCodeWriter writer = new QRCodeWriter();

			BitMatrix matrix = writer.encode(text,
					BarcodeFormat.QR_CODE,
					dim, dim);


			System.out.println("Publishing...");
			publish(new QRResult(matrix, pattern));
		}
		System.out.println("Cancelled...");
		return null;
	}

	@Override
	protected void process(List<QRResult> chunks){
		if (chunks.size() == 0) return;
		QRResult r = chunks.get(0);
		for(QRResult i : chunks){
			if ( i.stamp > r.stamp ) r = i;
		}
		synchronized(this){
			lastResult = r;
		}
		
		displayLabel.setText("");
		displayLabel.setIcon(r.getIcon());
		
		System.out.println("Process " + r.stamp);
	}
	
	synchronized public QRResult getLastResult(){
		return lastResult;
	}


}
