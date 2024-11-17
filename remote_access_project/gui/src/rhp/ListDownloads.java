package rhp;

import java.io.File;

import javax.swing.table.DefaultTableModel;

public class ListDownloads {

    private static MainWindow MainWindow;
    static DefaultTableModel model = (DefaultTableModel) MainWindow.dlTable.getModel();
	
	public static void DownloadsList()
	{
		model.setRowCount(0);
		File folder = new File("downloads/");
		File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    //System.out.println("File " + listOfFiles[i].getName());
                    if (!listOfFile.toString().contains("DO_NOT_DELETE")) {
                        Object[] obj = new Object[]{listOfFile.getName(), listOfFile.getAbsoluteFile()};
                        MainWindow.dlModel.addRow(obj);

                    }
                }
            }
        }
    }
}
