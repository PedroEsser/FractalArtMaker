package utils;

import java.awt.Point;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;

public class Rectangle implements Iterable<Point>{

	public final int x1, y1, x2, y2;
	
	public Rectangle(int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
	
	public Rectangle(int x, int y, int square) {
		this(x, y, x + square, y + square);
	}

    public void loop(Consumer<Point> function) {
    	for(Point p : this) 
    		function.accept(p);
    }

	@Override
	public Iterator<Point> iterator() {
		return new Iterator2D();
	}
	
	private class Iterator2D implements Iterator<Point>{

		private int x, y;
		
		private Iterator2D() {
			this.x = x1;
			this.y = y1;
		}
		
		@Override
		public boolean hasNext() {
			return x != x2 || y != y2-1;
		}

		@Override
		public Point next() {
			if(x == x2) {
				x = x1;
				y++;
			}
			return new Point(x++, y);
		}
		
	}
	
}
