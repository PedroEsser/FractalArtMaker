package guiUtils;

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
	protected void myPaint(Graphics2D g) {
		if(needsResizing()) {
			myResize(g);
			return;
		}
		g.drawImage(img, 0, 0, null);
	}
	
	public boolean needsResizing() {
		return this.getImg() == null || this.getPanelWidth() != this.getImg().getWidth() || this.getPanelHeight() != this.getImg().getHeight();
	}
	
	protected abstract void myResize(Graphics2D g);
	
}
