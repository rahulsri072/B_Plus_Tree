package util;

import java.awt.Graphics;
import java.awt.event.*;

/**
 * A ZoomView can display various graphical objects on the screen with zooming and panning capabilities.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 */
public abstract class ZoomView extends BufferedImagePanel {

	/**
	 * Automatically generated serial version ID.
	 */
	private static final long serialVersionUID = 5949882869282877870L;

	/**
	 * The graphics context.
	 */
	protected ZoomGraphics zoomGraphics = new ZoomGraphics();

	protected boolean mouseDragged = false;

	public ZoomView() {

		MouseListener l = new MouseAdapter() {

			protected int prevX = 0;

			protected int prevY = 0;

			public void mouseClicked(MouseEvent e) {
				if (!mouseDragged) {
					if (e.getButton() == MouseEvent.BUTTON1 && !e.isControlDown())
						zoomGraphics.changeScale(1.2);
					else if (e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON1
							&& e.isControlDown())
						zoomGraphics.changeScale(1 / 1.2);
					repaint();
				}
			}

			public void mousePressed(MouseEvent e) {
				prevX = e.getX();
				prevY = e.getY();
				mouseDragged = false;
			}

			public void mouseReleased(MouseEvent e) {
				if (mouseDragged) {
					double deltaX = (prevX - e.getX()) / zoomGraphics.getScale();
					double deltaY = (prevY - e.getY()) / zoomGraphics.getScale();
					zoomGraphics.moveBy(deltaX, deltaY);
					repaint();
				}
			}
		};
		addMouseListener(l);
		MouseMotionListener m = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				mouseDragged = true;
			}
		};
		addMouseMotionListener(m);

	}

	/**
	 * Invoked by Swing to draw components. Applications should not invoke this method directly, but should instead use
	 * the repaint method to schedule the component for redrawing.
	 * 
	 * @param g
	 *            the Graphics context in which to paint.
	 */
	public void draw(Graphics g) {
		zoomGraphics.set(g, getWidth() - 1, getHeight() - 1);
		draw();
	}

	/**
	 * Invoked by Swing to draw components. Applications should not invoke this method directly, but should instead use
	 * the repaint method to schedule the component for redrawing.
	 * 
	 * @param g
	 *            the Graphics context in which to paint.
	 */
	public abstract void draw();

}
