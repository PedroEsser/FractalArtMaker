package gpuColorGradients;

import static guiUtils.GUIUtils.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import org.apache.bcel.generic.DADD;

import gui.GradientVisualizer;
import guiUtils.GUIUtils;
import guiUtils.JTuple;
import guiUtils.LabelTuple;
import guiUtils.LabelValueTuple;
import guiUtils.Weighted1DPanel;

public class GradientLoader extends JFrame {

	private static Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.GRAY, 3);
	private static Border SELECTED_BORDER = BorderFactory.createLineBorder(Color.WHITE, 3);
	
	private static final String GRADIENT_PATH = "gradients.bin";
	public static final Dimension DEFAULT_LOADER_SIZE = new Dimension(900, 600);
	private GradientFactoryGUI factory;
	private Weighted1DPanel mainPanel;
	private Weighted1DPanel savedGradientsPanel;
	private List<NameGradientTuple> savedGradients;
	private JTuple<JTextField, GradientVisualizer> selectedGradientGUI = null;

	private static GradientLoader INSTANCE;

	private GradientLoader(GradientFactoryGUI factory) {
		super();
		this.factory = factory;
		mainPanel = new Weighted1DPanel(false, 5, 0, 5, 0);
		savedGradientsPanel = new Weighted1DPanel(false, 5, 0, 5, 0);
		JScrollPane pane = new JScrollPane(savedGradientsPanel);

		mainPanel.addComponent(label("Saved Gradients"), .5);
		mainPanel.addComponent(pane, 5);
		
		Weighted1DPanel buttonPanel = new Weighted1DPanel(true);
		buttonPanel.addComponent(button("Set as gradient", a -> handleSet()));
		buttonPanel.addComponent(button("Delete", a -> handleDelete()));
		buttonPanel.addComponent(button("Add gradients", a -> handleAddGradients()));
		buttonPanel.addComponent(button("Save Changes", a -> saveGradients()));
		
		mainPanel.addComponent(buttonPanel, .5);
		mainPanel.addComponent(label("Presets"), .5);
		mainPanel.addComponent(getPresetPanel(), 2);
		
		

		this.add(mainPanel);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(DEFAULT_LOADER_SIZE);
		setLocationRelativeTo(null);
		addSavedGradients();
	}
	
	private void addSavedGradients() {
		loadSavedGradients();
		savedGradientsPanel.removeAll();
		for(NameGradientTuple t : savedGradients) {
			GradientVisualizer vis = new GradientVisualizer(t.gradient);
			JTuple<JTextField, GradientVisualizer> gradientGUI = new JTuple<>(new JTextField(t.name), vis);
			gradientGUI.setMaximumSize(new Dimension(9999, 200));
			gradientGUI.setBorder(DEFAULT_BORDER);
			vis.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					handleSelect(gradientGUI);
					super.mousePressed(e);
				}
			});
			
			savedGradientsPanel.addComponent(gradientGUI);
		}
		savedGradientsPanel.updateUI();
	}
	
	private void addGradient(String name, ColorGradient gradient) {
		GradientVisualizer vis = new GradientVisualizer(gradient);
		JTuple<JTextField, GradientVisualizer> gradientGUI = new JTuple<>(new JTextField(name), vis);
		gradientGUI.setBorder(DEFAULT_BORDER);
		vis.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				handleSelect(gradientGUI);
				super.mousePressed(e);
			}
		});
		
		savedGradientsPanel.addComponent(gradientGUI);
		savedGradients.add(new NameGradientTuple(name, gradient));
	}
	
	public static void addGradientEntry(String name, MultiGradient gradient) {
		gradient.updateGradientData();
		INSTANCE.addGradient(name, gradient);
		INSTANCE.saveGradients();
		INSTANCE.savedGradientsPanel.updateUI();
	}
	
	private Weighted1DPanel getPresetPanel() {
		Weighted1DPanel presetGradientsPanel = new Weighted1DPanel(false, 5, 0, 5, 0);
		
		GradientVisualizer lightGradientVisualizer = new GradientVisualizer(null);
		JTuple<JButton, GradientVisualizer> lightPreset = new JTuple<>(button("Add Light Gradient", a -> {
			factory.addGradient((ThreeChannelGradient)lightGradientVisualizer.getGradient());
		}), lightGradientVisualizer);
		
		presetGradientsPanel.addComponent(lightPreset);
		
		GradientVisualizer darkGradientVisualizer = new GradientVisualizer(null);
		JTuple<JButton, GradientVisualizer> darkPreset = new JTuple<>(button("Add Dark Gradient", a -> {
			factory.addGradient((ThreeChannelGradient)darkGradientVisualizer.getGradient());
		}), darkGradientVisualizer);
		
		presetGradientsPanel.addComponent(darkPreset);
		
		GradientVisualizer metallicGradientVisualizer = new GradientVisualizer(null);
		JTuple<JButton, GradientVisualizer> metallicPreset = new JTuple<>(button("Add Metallic Gradient", a -> {
			factory.addGradient((ThreeChannelGradient)metallicGradientVisualizer.getGradient());
		}), metallicGradientVisualizer);
		
		presetGradientsPanel.addComponent(metallicPreset);
		
		new Thread(() -> {
			float t = 0;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while(this.isVisible()) {
				lightGradientVisualizer.setGradient(GradientFactory.darkOrLightGradient(false, t).bounce(20));
				darkGradientVisualizer.setGradient(GradientFactory.darkOrLightGradient(true, t).bounce(20));
				metallicGradientVisualizer.setGradient(GradientFactory.mettalicGradient(t, 0).bounce(20));
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				t += 0.01;
			}
		}).start();
		
		return presetGradientsPanel;
	}
	
	private void handleSelect(JTuple<JTextField, GradientVisualizer> selected) {
		if(selectedGradientGUI != null)
			selectedGradientGUI.setBorder(DEFAULT_BORDER);
		selectedGradientGUI = selected;
		selectedGradientGUI.setBorder(SELECTED_BORDER);
	}
	
	private void handleDelete() {
		if(selectedGradientGUI == null)
			return;
		selectedGradientGUI.setBorder(null);
		savedGradientsPanel.remove(selectedGradientGUI);
		for(NameGradientTuple entry : savedGradients) 
			if(selectedGradientGUI.getLeft().getText().equals(entry.name) && selectedGradientGUI.getRight().getGradient() == entry.gradient) {
				savedGradients.remove(entry);
				break;
			}
		selectedGradientGUI = null;
		savedGradientsPanel.updateUI();
	}
	
	private MultiGradient getSelectedGradient() {
		if(selectedGradientGUI == null)
			return null;
		return (MultiGradient)selectedGradientGUI.getRight().getGradient();
	}
	
	private void handleAddGradients() {
		MultiGradient g = getSelectedGradient();
		if(g != null)
			for(int i = 0 ; i < g.getNumberOfGradients() ; i++) 
				factory.addGradient(g.getGradientAtIndex(i));
			
	}
	
	private void handleSet() {
		if(getSelectedGradient() != null)
			factory.setGradient(getSelectedGradient());
	}
	
	public static void open(GradientFactoryGUI factory) {
		if(INSTANCE == null)
			INSTANCE = new GradientLoader(factory);
		
		if(!INSTANCE.isVisible())
			INSTANCE.setVisible(true);
		
		SwingUtilities.invokeLater(() -> {
			INSTANCE.toFront();
			INSTANCE.repaint();
		});
	}
	
	public static GradientLoader getInstance() {
		if(INSTANCE == null)
			INSTANCE = new GradientLoader(null);
		return INSTANCE;
	}
	
	@Override
	public void dispose() {
		INSTANCE = null;
		super.dispose();
	}

	private void loadSavedGradients() {
		savedGradients = new ArrayList<NameGradientTuple>();
		File gradientFolder = new File(GRADIENT_PATH);
		if (!gradientFolder.exists()) {
			try {
				gradientFolder.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		ObjectInputStream ois = null;
		try {
			FileInputStream fis = new FileInputStream(gradientFolder);
			ois = new ObjectInputStream(fis);
			savedGradients = (ArrayList<NameGradientTuple>)ois.readObject();
		} catch (Exception e1) {
		}finally {
			if(ois != null)
				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public void saveGradients() {
		savedGradients = new ArrayList<GradientLoader.NameGradientTuple>();
		for(Component c : savedGradientsPanel.getComponents()) {
			JTuple<JTextField, GradientVisualizer> t = (JTuple<JTextField, GradientVisualizer>)c;
			savedGradients.add(new NameGradientTuple(t.getLeft().getText(), t.getRight().getGradient()));
		}
		
		ObjectOutputStream ous = null;
		try {
			FileOutputStream fis = new FileOutputStream(GRADIENT_PATH);
			ous = new ObjectOutputStream(fis);
			ous.writeObject(savedGradients);
		} catch (IOException e1) {
			e1.printStackTrace();
		}finally {
			if(ous != null)
				try {
					ous.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	static class NameGradientTuple implements Serializable {
		private static final long serialVersionUID = 1L;
		String name;
		ColorGradient gradient;

		public NameGradientTuple(String name, ColorGradient gradient) {
			super();
			this.name = name;
			this.gradient = gradient;
		}

	}

}
