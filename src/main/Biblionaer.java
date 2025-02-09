package main;

import java.awt.GraphicsEnvironment;

import quiz.Steuerung;
import window.AdministratorSchirm;
import window.Einstellungen;
import window.Konsole;
import window.SinglePlayerSchirm;
import window.WindowController;
import sound.SoundLib;

/**
 * Diese Klasse ist eine statische Bibliothek zur Verwaltung der Referenzen auf
 * die wichtigsten Objekte
 * 
 * @author steven
 */
public class Biblionaer {

	public static Steuerung			meineSteuerung;
	public static Einstellungen		meineEinstellungen;
	public static WindowController	meinWindowController;
	public static Konsole			meineKonsole;

	public static void main(String[] args) {
		// Steuerung erstellen und initiieren

		// Grafikarte zum skalieren der Bilder mit einsetzen
		// TURN THIS OFF TO SEE THE BEFORE & AFTER HARDWARE SCALING FPS
		// System.setProperty( "sun.java2d.ddscale", "true" );

	//	System.setProperty( "sun.java2d.accthreshold", "0" );
//		System.setProperty( "sun.java2d.translaccel", "true" );

		meineSteuerung = new Steuerung();

		meineKonsole = new Konsole();

		// Alle anderen Fenster und Objekte erstellen
		meineEinstellungen = new Einstellungen( "Einstellungen", meineSteuerung );
		meinWindowController = new WindowController();

		meinWindowController.addFrontendFenster( new SinglePlayerSchirm( "Der Biblionär", 678, 549, meineSteuerung ) );
		// meinWindowController.addBackendFenster(new
		// VollbildSchirm("Administrationsfenster", GraphicsEnvironment
		// .getLocalGraphicsEnvironment().getScreenDevices()[1]));

		meinWindowController.addBackendFenster( new AdministratorSchirm( "Administrationsfenster", GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice(), false ) );

		// Pre-Caching Sound
		SoundLib sl = SoundLib.getInstance();
		// sl.loadSound( "background", "sound/background3_ohnePegelung.wav" );
		// sl.loadSound( "gewonnen", "sound/gewonnen.wav" );
		// sl.loadSound( "richtig", "sound/richtig.wav" );
		// sl.loadSound( "verloren", "sound/verloren.wav" );

		meineSteuerung.starteNeuesBeispielSpiel();

		// meinWindowController.addFrontendFenster( new VollbildSchirm(
		// "Vollbildschirm",
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[1],
		// true ) );

		// meinWindowController.addFrontendFenster( new SinglePlayerSchirm(
		// "Hauptfenster", 678, 549,
		// meineSteuerung ) );

		// Weiter Einstellungen
		// meinHauptfenster.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		// Dialog äffnen und Standarspiel starten
		meineSteuerung.erstAufrufDerSteuerung();
	}
}
