package gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class ImagePanel extends Panel {

	protected BufferedImage img;
	
	public ImagePanel(BufferedImage img) {
		super();
		this.img = img;
	}
	
	public ImagePanel() {
		this(null);
	}
	
	public BufferedImage getImg() {
		return img;
	}

	public void setImg(BufferedImage img) {
		this.img = img;
	}
	
	public void updateImage(BufferedImage img){
		this.img = img;
		repaint();
	}

	@Override
	void myPaint(Graphics2D g) {
		if(needsResizing()) {
			myResize();
			return;
		}
		g.drawImage(img, 0, 0, null);
	}
	
	protected boolean needsResizing() {
		return this.getImg() == null || this.getPanelWidth() != this.getImg().getWidth() || this.getPanelHeight() != this.getImg().getHeight();
	}
	
	protected abstract void myResize();
	
}
