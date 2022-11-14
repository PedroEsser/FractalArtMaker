package gpuColorGradients;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import gui.GradientVisualizer;
import guiUtils.LabelValueTuple;

public class MultiGradientEditor extends GradientEditor{

	private int selectedIndex = -1;
	private Consumer<ThreeChannelGradient> selectCallback;
	private LabelValueTuple offsetParameter;
	
	public MultiGradientEditor(MultiGradient gradient) {
		super(gradient);
		gradient.updateGradientData();
		visualizer.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) { handleClick(e.getPoint()); }
			public void mousePressed(MouseEvent e) { visualizer.grabFocus(); };
		});
		visualizer.addKeyListener(new KeyHandler());
		offsetParameter = new LabelValueTuple("Offset", gradient.getOffset());
		offsetParameter.setValueChangeListener(d -> updateGradient());
		this.addComponent(offsetParameter, 0.5);
		this.addKeyListener(new KeyHandler());
	}
	
	@Override
	public MultiGradient getGradient() {
		return (MultiGradient)gradient;
	}
	
	@Override
	public void setGradient(ColorGradient gradient) {
		assert gradient instanceof MultiGradient;
		super.setGradient(gradient);
		offsetParameter.setValue(((MultiGradient)gradient).getOffset());
		selectedIndex = -1;
	}
	
	@Override
	protected void addVisualizer() {
		visualizer = new MyVisualizer(gradient);
		this.addComponent(visualizer, 5);
	}
	
	public void addGradient(ThreeChannelGradient g) {
		getGradient().addGradient(g);
		selectGradient(getGradient().getNumberOfGradients()-1);
	}
	
	public ThreeChannelGradient getSelectedGradient() {
		return getGradient().getGradientAtIndex(selectedIndex);
	}
	
	public void setSelectCallback(Consumer<ThreeChannelGradient> selectCallback) {
		this.selectCallback = selectCallback;
	}
	
	private void handleClick(Point p) {
		float percent = (float)visualizer.percentAtX(p.x);
		int index = getGradient().getIndexForGradientAt(percent);
		selectGradient(index);
	}

	private void selectGradient(int index) {
		selectedIndex = index;
		if(selectCallback != null && selectedIndex != -1)
			selectCallback.accept(getSelectedGradient());
	}
	
	public void removeSelectedGradient() {
		if(getGradient().getNumberOfGradients() == 1)
			return;
		getGradient().removeGradient(getSelectedGradient());
		selectGradient(Math.min(selectedIndex, getGradient().getNumberOfGradients()-1));
	}
	
	public void swapPositions(int i, int j) {
		getGradient().swap(i, j);
		selectGradient(j);
	}
	
	@Override
	protected void updateGradient() {
		getGradient().offseted(offsetParameter.getValueAsFloat()).updateGradientData();
		super.updateGradient();
	}
	
	class KeyHandler extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			int n = getGradient().getNumberOfGradients();
			if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
				swapPositions(selectedIndex, (selectedIndex + 1) % n);
			}else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
				swapPositions(selectedIndex, (selectedIndex - 1 + n) % n);
			}else if(e.getKeyCode() == KeyEvent.VK_DELETE) {
				removeSelectedGradient();
			}
		}
	}
	
	class MyVisualizer extends GradientVisualizer{

		public MyVisualizer(ColorGradient gradient) {
			super(gradient);
		}
		
		@Override
		protected void myPaint(Graphics2D g) {
			super.myPaint(g);
			if(selectedIndex == -1)
				return;
			
			float[] interval = MultiGradientEditor.this.getGradient().getIntervalForGradient(getSelectedGradient());
			float range = getGradient().getRange();
			Rectangle r = g.getClipBounds();
			g.setColor(Color.WHITE);
			
			for(int i = 0 ; i < range ; i++) {
				int left = (int)((i + interval[0]) / range * r.width) + r.x;
				int right = (int)((i + interval[1]) / range * r.width) + r.x;
				g.drawRect(left, r.y, right - left, r.height-1);
				g.drawRect(left+1, r.y + 1, right - left - 2, r.height-3);
				g.drawRect(left+2, r.y + 2, right - left - 4, r.height-5);
			}
		}
		
	}
	
}
