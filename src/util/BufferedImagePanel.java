package util;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 */
public abstract class BufferedImagePanel extends JPanel {

	/**
	 * Automatically generated serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The buffered image.
	 */
	protected BufferedImage bufferedImage = null;

	/**
	 * Constructs a BufferedImagePanel.
	 */
	public BufferedImagePanel() {
		ComponentListener c = new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				synchronized (this) {
					Dimension d = getSize();
					bufferedImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
					repaint();
				}
			}
		};
		addComponentListener(c);
	}

	/**
	 * Draws on the specified Graphics context.
	 * 
	 * @param g
	 *            the Graphics context.
	 */
	public abstract void draw(Graphics g);

	/**
	 * Repaints this BufferedImagePanel.
	 */
	public final synchronized void repaint() {
		if (bufferedImage != null) {
			Graphics g = bufferedImage.getGraphics();
			draw(g);
			super.repaint();
		}
	}

	/**
	 * Invoked by Swing to draw components. This method should not be invoked directly. To have this component redrawn,
	 * the repaint() method should be invoked instead.
	 * 
	 * @param g
	 *            the Graphics context in which to paint.
	 */
	public final synchronized void paint(Graphics g) {
		super.paint(g);
		if (bufferedImage != null) {
			g.drawImage(bufferedImage, 0, 0, null);
		}
	}

}
