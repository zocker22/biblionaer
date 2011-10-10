package lokaleSpiele;

import importer.XmlToSpiel;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

import main.Biblionaer;

public class QuizFileModel extends AbstractTableModel {

	private static final long	serialVersionUID		= -7040042367775652371L;
	public static String		speicherOrtFuerSpiele	= "Biblionaer";

	private String				titles[]				= new String[] { "Quizname", "Laufzeit" };

	private Class<?>			types[]					= new Class[] { String.class, String.class };

	private Object				data[][];
	private File				dateiPfad[];

	public QuizFileModel() {
		// System herausfinden und falls noch nicht vorhanden den Ordner anlegen

		setFileStats( getSpeicherortSpiele() );
	}

	public static File getSpeicherortSpiele() {
		String subDir = null;
		File homeDir = FileSystemView.getFileSystemView().getHomeDirectory();

		if ( isMac() ) {
			subDir = "Library/Application Support/" + QuizFileModel.speicherOrtFuerSpiele;
		}

		else if ( isWindows() ) {
			subDir = "Application Data/" + QuizFileModel.speicherOrtFuerSpiele;

		}
		else if ( isUnix() ) {
			subDir = "." + QuizFileModel.speicherOrtFuerSpiele;
		}
		else {
			subDir = QuizFileModel.speicherOrtFuerSpiele;
		}

		File dir = new File( homeDir, subDir );
		if ( dir.exists() ) {
			if ( dir.isDirectory() ) {
				Biblionaer.meineKonsole.println( "Quiz Home-Dir ist: '" + dir.getAbsolutePath()
						+ "'", 4 );
			}
		}
		else {
			// Dann muss es wohl noch erstellt werden
			if ( dir.mkdirs() ) {
				Biblionaer.meineKonsole.println( "Verzeichnis zur Quizablage wurde erstellt.", 3 );
			}
			else {
				Biblionaer.meineKonsole.println(
						"Verzeichnis zur Quizablage konte nicht erstellt werden.", 2 );
			}
		}

		return dir;
	}

	// Implement the methods of the TableModel interface we're interested
	// in. Only getRowCount(), getColumnCount() and getValueAt() are
	// required. The other methods tailor the look of the table.
	public int getRowCount() {
		return data.length;
	}

	public int getColumnCount() {
		return titles.length;
	}

	public String getColumnName(int c) {
		return titles[c];
	}

	public Class<?> getColumnClass(int c) {
		return types[c];
	}

	public Object getValueAt(int r, int c) {
		return data[r][c];
	}

	/**
	 * Gibt abh�ngig zur �bergebenen Zeile, den Pfad zur�ck, wo dieses File
	 * gespeichert ist.
	 * 
	 * @param row
	 * @return PfadZurDatei
	 */
	public File getQuizFileLocationAt(int row) {
		return (File) dateiPfad[row];

	}

	// Our own method for setting/changing the current directory
	// being displayed. This method fills the data set with file info
	// from the given directory. It also fires an update event so this
	// method could also be called after the table is on display.
	public void setFileStats(File dir) {

		String files[] = dir.list( new FilenameFilter() {
			public boolean accept(File f, String s) {
				return new File( f, s ).isFile() && s.toLowerCase().endsWith( ".bqxml" );
			}
		} );

		data = new Object[files.length][titles.length];
		dateiPfad = new File[files.length];
		File rootPfad = getSpeicherortSpiele();

		for (int i = 0; i < files.length; i++) {
			// File tmp = new File( files[i] );
			data[i][0] = files[i];
			data[i][1] = "unused";
			dateiPfad[i] = new File( rootPfad, files[i] );
		}

		// Just in case anyone's listening...
		fireTableDataChanged();
	}

	public boolean addQuizFile(XmlToSpiel xmlImporterFile) {

		try {
			XmlToSpiel dasXMLImporterFile = xmlImporterFile;

			// Finde den n�chsten freien Speichernamen
			int i = 0;
			File saveTo = null;
			while (saveTo == null && i < 50) {
				i++;
				saveTo = new File( getSpeicherortSpiele().getAbsolutePath() + "/neuesSpiel_"
						+ Integer.toString( i ) + ".bqxml" );

				if ( saveTo.exists() ) {
					saveTo = null;
				}
			}

			if ( i >= 50 ) {
				Biblionaer.meineKonsole.println( "Es wurde nach " + Integer.toString( i )
						+ " versuchen abgebrochen, das Spiel zu speichern.", 2 );
			}
			else {
				if ( dasXMLImporterFile.getAnzahlFragen() > 0 ) {
					dasXMLImporterFile.saveSpielToFile( saveTo );
					Biblionaer.meineKonsole.println( "Es wurde noch ein neues Spiel angelegt.", 3 );
				}
				else {
					Biblionaer.meineKonsole.println(
							"Es wurde kein neues Spiel importiert, weil nur "
									+ Integer.toString( dasXMLImporterFile.getAnzahlFragen() )
									+ " Fragen zur heruntergeladen wurden.", 2 );
				}
			}
		}
		catch (MalformedURLException e) {
			Biblionaer.meineKonsole
					.println(
							"Beim Versuch ein neues Spiel herunterzuladen (im AdministratorSchirm), ist die falsche URL verwendet worden.\n"
									+ e.getMessage(), 1 );
			return false;
		}
		catch (IOException e2) {
			Biblionaer.meineKonsole.println(
					"Es trat ein Fehler beim speichern eines heruntergeladenen Spieles (im AdministratorSchirm) auf:\n"
							+ e2.getMessage(), 1 );
			return false;

		}
		finally {
			this.refreshInhalte();
		}

		return true;
	}

	public boolean removeQuizFile(int row) {

		if ( row <= dateiPfad.length ) {
			if ( dateiPfad[row].exists() ) {
				dateiPfad[row].delete();
				this.refreshInhalte();
				return true;
			}
		}
		return false;
	}

	public void refreshInhalte() {
		setFileStats( getSpeicherortSpiele() );
	}

	public static boolean isWindows() {

		String os = System.getProperty( "os.name" ).toLowerCase();
		// windows
		return (os.indexOf( "win" ) >= 0);

	}

	public static boolean isMac() {

		String os = System.getProperty( "os.name" ).toLowerCase();
		// Mac
		return (os.indexOf( "mac" ) >= 0);

	}

	public static boolean isUnix() {

		String os = System.getProperty( "os.name" ).toLowerCase();
		// linux or unix
		return (os.indexOf( "nix" ) >= 0 || os.indexOf( "nux" ) >= 0);

	}

}