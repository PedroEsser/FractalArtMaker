package main;

import javax.swing.*;

import com.formdev.flatlaf.FlatDarkLaf;

import gui.FractalNavigatorGUI;

public class main{
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(new FlatDarkLaf());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new FractalNavigatorGUI();
	}
	
}
