import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MainWindow {

	private JFrame frmQrCodeLogo;
	private JTextField textField;
	private JFileChooser fc = new JFileChooser();
	private QRGenWorker worker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
					MainWindow window = new MainWindow();
					window.frmQrCodeLogo.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	private JLabel lblNoResultsYet;
	private JLabel lblLogoImg;
	private JLabel lblPatternImg;
	private JSpinner spinner;
	private BufferedImage pattern;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQrCodeLogo = new JFrame();
		frmQrCodeLogo.setTitle("QR Code Logo Generator");
		frmQrCodeLogo.setBounds(100, 100, 637, 540);
		frmQrCodeLogo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel_4 = new JPanel();
		frmQrCodeLogo.getContentPane().add(panel_4, BorderLayout.SOUTH);

		JProgressBar progressBar = new JProgressBar();
		panel_4.add(progressBar);

		JButton btnNewButton = new JButton("Save");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				QRResult code = worker.getLastResult();
				if (code == null) return;
				try {
					System.out.println("Latest result: "
							+ code);
					
					int result = fc.showSaveDialog(frmQrCodeLogo);
					if(result == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						ImageIO.write(code.getBufferedImage(), "PNG", fc.getSelectedFile());
					 }
				} catch (Exception e1) {
					System.out.println("Can not fetch results");
				}
			}
		});

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (worker != null) worker.cancel(true);
			}
		});
		panel_4.add(btnCancel);
		panel_4.add(btnNewButton);

		JPanel panel = new JPanel();
		frmQrCodeLogo.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(2, 2, 0, 0));

		JLabel lblUrlToEncode = new JLabel("Text to encode");
		panel.add(lblUrlToEncode);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		lblUrlToEncode.setLabelFor(textField);

		JLabel lblNewLabel = new JLabel("Size");
		panel.add(lblNewLabel);

		spinner = new JSpinner();
		spinner.setValue(200);
		panel.add(spinner);
		lblNewLabel.setLabelFor(spinner);

		JPanel panel_3 = new JPanel();
		frmQrCodeLogo.getContentPane().add(panel_3, BorderLayout.CENTER);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));

		JPanel panel_1 = new JPanel();
		panel_3.add(panel_1);

		JButton btnLoadLogo = new JButton("Load logo...");
		btnLoadLogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(frmQrCodeLogo);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					lblLogoImg.setIcon(new ImageIcon(file.getPath()));
					lblLogoImg.setText("");
				} else {
					lblLogoImg.setIcon(null);
					lblLogoImg.setText("No image loaded");
				}
			}
		});
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		panel_1.add(btnLoadLogo);

		lblLogoImg = new JLabel("No image loaded");
		lblLogoImg.setHorizontalAlignment(SwingConstants.CENTER);
		// panel_1.add(lblNoImageLoaded, BorderLayout.CENTER);

		JScrollPane sc1 = new JScrollPane(lblLogoImg);
		panel_1.add(sc1);

		JButton btnLoadPattern = new JButton("Load pattern...");
		btnLoadPattern.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(frmQrCodeLogo);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					try {
						pattern = ImageIO.read(file);
						lblPatternImg.setIcon(new ImageIcon(pattern));
						lblPatternImg.setText("");
					} catch (Exception e1) {
						lblPatternImg.setIcon(null);
						lblPatternImg.setText("No image loaded");
						pattern = null;
					}
					

				} else {
					lblPatternImg.setIcon(null);
					lblPatternImg.setText("No image loaded");
					pattern = null;
				}
			}
		});
		panel_1.add(btnLoadPattern);

		lblPatternImg = new JLabel("No image loaded");
		lblPatternImg.setHorizontalAlignment(SwingConstants.CENTER);
		JScrollPane sc3 = new JScrollPane(lblPatternImg);
		panel_1.add(sc3);

		JPanel panel_2 = new JPanel();
		panel_3.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));

		JButton btnGenerateQr = new JButton("Generate QR");

		panel_2.add(btnGenerateQr, BorderLayout.NORTH);

		lblNoResultsYet = new JLabel("No results yet");
		lblNoResultsYet.setHorizontalAlignment(SwingConstants.CENTER);
		// panel_2.add(lblNoResultsYet, BorderLayout.CENTER);

		JScrollPane sc2 = new JScrollPane(lblNoResultsYet);
		panel_2.add(sc2, BorderLayout.CENTER);

		btnGenerateQr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				lblNoResultsYet.setIcon(null);
				lblNoResultsYet.setText("No results yet");

				if (worker != null)
					worker.cancel(true);
				worker = new QRGenWorker(lblNoResultsYet, textField.getText(),
						(Integer) spinner.getValue(), pattern);

				worker.addPropertyChangeListener(new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						System.out.println(evt.getPropertyName() + ": "
								+ evt.getOldValue() + " -> "
								+ evt.getNewValue());

					}
				});

			}
		});

	}

}
