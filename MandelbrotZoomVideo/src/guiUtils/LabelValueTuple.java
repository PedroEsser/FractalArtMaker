package guiUtils;

import java.awt.AWTException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class LabelValueTuple extends LabelTuple<JNumberField>{
	
	private double sensitivity = 1d/(1 << 12);
	private Point lastMousePosition = null;
	private DocumentListener listener;
	
	public LabelValueTuple(String label, double value) {
		super(label, new JNumberField(value));
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				handleMouseDrag(e.getPoint());
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if(lastMousePosition != null) {
					lastMousePosition = null;
					setValue(GUIUtils.roundIfCloseToMultipleOf(getValue(), 0.1, 0.02), true);
				}
			}
		});
	}
	
	public void setLabel(String label) {
		this.getLeft().setText(label);
	}
	
	public void setValue(double value, boolean pretty) {
		this.getRight().setText(pretty ? GUIUtils.prettyDouble(value, 3) : value + "");
	}
	
	public void setValue(double value) {
		setValue(value, false);
	}
	
	public String getLabel() {
		return this.getLeft().getText();
	}
	
	public double getValue() {
		return this.getRight().getValue();
	}
	
	public float getValueAsFloat() {
		return (float)this.getValue();
	}
	
	private void handleMouseDrag(Point mouse) {
		if(!getRight().validParse())
			return;
		
		if(lastMousePosition != null) {
			double diff = lastMousePosition.x - mouse.x;
			diff = -diff * sensitivity * (Math.abs(getValue()) + 1);
			setValue(getValue() + diff, true);
		}
		Rectangle bounds = LabelValueTuple.this.getBounds();
		if(mouse.x < 0) {
			lastMousePosition = new Point(bounds.width, mouse.y);
			Point screen = LabelValueTuple.this.getLocationOnScreen();
			moveMouse(new Point(lastMousePosition.x + screen.x, lastMousePosition.y + screen.y));
		}else if(mouse.x > bounds.width) {
			lastMousePosition = new Point(0, mouse.y);
			Point screen = LabelValueTuple.this.getLocationOnScreen();
			moveMouse(new Point(lastMousePosition.x + screen.x, lastMousePosition.y + screen.y));
		}else
			lastMousePosition = mouse;
		
	}
	
	private void moveMouse(Point p) {
		try {
			new Robot().mouseMove(p.x, p.y);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		getRight().setEditable(false);
	}
	
	public void setValueChangeListener(Consumer<Double> c) {
		if(listener != null)
			this.getRight().getDocument().removeDocumentListener(listener);
		listener = new MyDocumentListener(c);
		this.getRight().getDocument().addDocumentListener(listener);
	}
	
	private class MyDocumentListener implements DocumentListener{
		private Consumer<Double> c;
		
		public MyDocumentListener(Consumer<Double> c) {
			this.c = c;
		}
		
		private void handleChange() {
			if(getRight().validParse())
				c.accept(getValue());
		}
		
		public void insertUpdate(DocumentEvent e) { handleChange(); }
		public void removeUpdate(DocumentEvent e) { handleChange(); }
		public void changedUpdate(DocumentEvent e) { handleChange(); }
		
	}
	
}
