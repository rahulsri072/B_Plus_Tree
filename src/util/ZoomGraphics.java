package util;

import java.awt.*;

/**
 * The ZoomGraphics class implements java.awt.Graphics equivalents with zooming capabilities.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 */
public class ZoomGraphics {

	/**
	 * The width of the Graphics context.
	 */
	protected int graphicsWidth = 0;

	/**
	 * The height of the Graphics context.
	 */
	protected int graphicsHeight = 0;

	/**
	 * The scale for the mapping from the virtual screen to the Graphics context.
	 */
	protected double scale = 1;

	/**
	 * The graphics context.
	 */
	protected Graphics g = null;

	/**
	 * The minimum x coordinate value.
	 */
	protected double minX = 0;

	/**
	 * The minimum y coordinate value.
	 */
	protected double minY = 0;

	/**
	 * Constructs a ZoomGraphics.
	 */
	public ZoomGraphics() {
	}

	/**
	 * Sets the minimum x and y values.
	 * 
	 * @param minX
	 *            the minimum x value.
	 * @param minY
	 *            the minimum y value.
	 */
	public void setMinXY(double minX, double minY) {
		this.minX = minX;
		this.minY = minY;
	}

	/**
	 * Sets this ZoomGraphics context.
	 * 
	 * @param g
	 *            the Graphics context.
	 * @param graphicsWidth
	 *            the width of the Graphics context.
	 * @param graphicsHeight
	 *            the height of the Graphics context.
	 */
	public void set(Graphics g, int graphicsWidth, int graphicsHeight) {
		this.graphicsWidth = graphicsWidth;
		this.graphicsHeight = graphicsHeight;
		this.g = g;
	}

	/**
	 * Fills this ZoomGraphics context.
	 */
	public void fill() {
		if (g != null) {
			g.fillRect(0, 0, graphicsWidth, graphicsHeight);
		}
	}

	/**
	 * Sets this ZoomGraphics' current color.
	 * 
	 * @param c
	 *            the color.
	 */
	public void setColor(Color c) {
		if (g != null)
			g.setColor(c);
	}

	/**
	 * Returns the current stroke.
	 * 
	 * @return the current stroke.
	 */
	public java.awt.Stroke getStroke() {
		if (g != null) {
			return ((java.awt.Graphics2D) g).getStroke();
		} else
			return null;
	}

	/**
	 * Sets the stroke.
	 * 
	 * @param s
	 *            the stroke to use.
	 */
	public void setStroke(java.awt.Stroke s) {
		if (g != null) {
			((java.awt.Graphics2D) g).setStroke(s);
		}
	}

	/**
	 * Sets this ZoomGraphics' current font size.
	 * 
	 * @param size
	 *            the font size.
	 */
	public void setFontSize(int size) {
		if (g != null) {
			Font f = g.getFont();
			g.setFont(new Font(f.getFontName(), Font.PLAIN, size));
		}
	}

	/**
	 * Draws the text given by the specified string, using this ZoomGraphics' current font and color.
	 * 
	 * @param s
	 *            the text string.
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 */
	public void drawStrings(String[] s, double x, double y) {
		int height = g.getFontMetrics().getHeight();
		if (g != null) {
			for (int i = 0; i < s.length; i++) {
				g.drawString(s[i], getX(x) + 2, getY(y) + height * (i + 1));
			}
		}
	}

	/**
	 * Draws the text given by the specified string, using this ZoomGraphics' current font and color.
	 * 
	 * @param s
	 *            the text string.
	 * @param x
	 *            the x coordinate.
	 * @param y
	 *            the y coordinate.
	 * @param width
	 *            the width of the text area.
	 * @param height
	 *            the height of the text area.
	 */
	public void drawStrings(String[] s, double x, double y, double width, double height) {
		if (g != null) {
			g.setClip(getX(x), getY(y), (int) (width * scale), (int) (height * scale));
			drawStrings(s, x, y);
			g.setClip(0, 0, graphicsWidth, graphicsHeight);
		}
	}

	/**
	 * Draws a line, using the current color, between the points <code>(x1,&nbsp;y1)</code> and
	 * <code>(x2,&nbsp;y2)</code> in this graphics context's coordinate system.
	 * 
	 * @param x1
	 *            the first point's <i>x</i> coordinate.
	 * @param y1
	 *            the first point's <i>y</i> coordinate.
	 * @param x2
	 *            the second point's <i>x</i> coordinate.
	 * @param y2
	 *            the second point's <i>y</i> coordinate.
	 */
	public void drawLine(double x1, double y1, double x2, double y2) {
		if (g != null)
			g.drawLine(getX(x1), getY(y1), getX(x2), getY(y2));
	}

	/**
	 * Fills the specified rectangle. The left and right edges of the rectangle are at <code>x</code> and
	 * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom edges are at <code>y</code> and
	 * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting rectangle covers an area <code>width</code> pixels
	 * wide by <code>height</code> pixels tall. The rectangle is filled using the graphics context's current color.
	 * 
	 * @param x
	 *            the <i>x</i> coordinate of the rectangle to be filled.
	 * @param y
	 *            the <i>y</i> coordinate of the rectangle to be filled.
	 * @param width
	 *            the width of the rectangle to be filled.
	 * @param height
	 *            the height of the rectangle to be filled.
	 */
	public void fillRect(double x, double y, double width, double height) {
		if (g != null) {
			g.fillRect(getX(x), getY(y), (int) (width * scale), (int) (height * scale));
		}
	}

	/**
	 * Draws the outline of the specified rectangle. The left and right edges of the rectangle are at <code>x</code> and
	 * <code>x&nbsp;+&nbsp;width</code>. The top and bottom edges are at <code>y</code> and
	 * <code>y&nbsp;+&nbsp;height</code>. The rectangle is drawn using the graphics context's current color.
	 * 
	 * @param x
	 *            the <i>x</i> coordinate of the rectangle to be drawn.
	 * @param y
	 *            the <i>y</i> coordinate of the rectangle to be drawn.
	 * @param width
	 *            the width of the rectangle to be drawn.
	 * @param height
	 *            the height of the rectangle to be drawn.
	 */
	public void drawRect(double x, double y, double width, double height) {
		if (g != null) {
			g.drawRect(getX(x), getY(y), (int) (width * scale), (int) (height * scale));
		}
	}

	/**
	 * Returns the current scale for the mapping from the virtual screen to the Graphics context.
	 * 
	 * @return the current scale for the mapping from the virtual screen to the Graphics context.
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Changes the scale by the specified ratio.
	 * 
	 * @param ratio
	 *            the ration by which to change the scale.
	 */
	public void changeScale(double ratio) {
		scale *= ratio;
	}

	/**
	 * Moves this ZoomGraphics by the specified amount.
	 * 
	 * @param deltaX
	 *            the amount to move along the x-axis.
	 * @param deltaY
	 *            the amount to move along the y-axis.
	 */
	public void moveBy(double deltaX, double deltaY) {
		minX += deltaX;
		minY += deltaY;
	}

	/**
	 * Returns the corresponding x-position on the Graphics context for the specified x-position on the virtual screen.
	 * 
	 * @param x
	 *            the x-position on the virtual screen.
	 * @return the corresponding x-position on the Graphics context for the specified x-position on the virtual screen.
	 */
	protected int getX(double x) {
		return (int) ((x - minX) * scale);
	}

	/**
	 * Returns the corresponding y-position on the Graphics context for the specified y-position on the virtual screen.
	 * 
	 * @param y
	 *            the y-position on the virtual screen.
	 * @return the corresponding y-position on the Graphics context for the specified y-position on the virtual screen.
	 */
	protected int getY(double y) {
		return (int) ((y - minY) * scale);
	}

}
