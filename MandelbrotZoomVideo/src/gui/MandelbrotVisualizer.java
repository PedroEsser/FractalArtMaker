package gui;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class MandelbrotVisualizer extends JPanel{

	private final MandelbrotNavigator navigator;
	private JLabel label;
	
	public MandelbrotVisualizer(MandelbrotNavigator navigator) {
		super();
		this.navigator = navigator;
		this.addMouseListener(getMouseAdapter());
		this.addMouseWheelListener(e -> navigator.zoom(e.getUnitsToScroll()));
		label = new JLabel();
		setLayout(new GridLayout());
		add(label);
	}
	
	public void updateImage(BufferedImage img) {
		label.setIcon(new ImageIcon(img));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		if(init())
			navigator.initializeVisualizer();
		if(wasResized()) 
			navigator.checkResize();
		
		super.paintComponent(g);
	}
	
	private boolean init() {
		return label.getIcon() == null;
	}
	
	private boolean wasResized() {
		return navigator.getWidth() != this.getWidth() || navigator.getHeight() != this.getHeight();
	}
	
	private MouseAdapter getMouseAdapter() {
		return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {		//LEFT_CLICK
                	navigator.move(e.getPoint());
                }
            }
        };
	}
	
}
