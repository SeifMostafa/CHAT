import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import model.Direction;

public class CharactersCustomization extends JFrame {
	private Graphics2D g2d;
	public Font font;
	private FontMetrics fm;
	private BufferedImage image;
	public Stack<Character> Characters;
	public Stack<String> character_txt;

	private static final long serialVersionUID = 1L;
	Direction[] directions;
	ArrayList<Point> TriggerPoints;
	Point touchedpoint = null;
	Point prev_touchedpoint = null;
	char character;

	public CharactersCustomization(Stack<String> WORDS) {
		this.character_txt = WORDS;
		image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		g2d = image.createGraphics();

		font = new Font("Level One Logica", Font.PLAIN, 512);
		g2d.setFont(font);
		fm = g2d.getFontMetrics();

		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);
		fm = g2d.getFontMetrics();
		g2d.dispose();

		character = Characters.pop().charValue();

		validate();
		repaint();

		directions = new Direction[0];
		TriggerPoints = new ArrayList<>();

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				repaint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				touchedpoint = e.getPoint();
				System.out.println("Start: " + touchedpoint);
				directions = Utils.concatenate(directions, fillDirections(touchedpoint, prev_touchedpoint));
				TriggerPoints.add(touchedpoint);
				prev_touchedpoint = touchedpoint;
				System.out.println("Release!");
			}
		};

		addMouseListener(ma);
		addMouseMotionListener(ma);
		JButton btn_nxt;
		btn_nxt = new JButton("NEXTWORD");
		btn_nxt.setBounds(500, 500, 100, 100);
		btn_nxt.setVisible(true);
		btn_nxt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Ok,It's fine!");
				CleanSaveDirectionsAndTriggerPoints(TriggerPoints, directions, character);
				character = Characters.pop().charValue();
				validate();
				repaint();
				directions = new Direction[0];
				prev_touchedpoint = null;
				TriggerPoints.clear();
			}
		});
		add(btn_nxt);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public Dimension getPreferredSize() {

		return new Dimension((int) Utils.width, (int) Utils.height);
	}

	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setFont(font);

		fm = g2d.getFontMetrics();
		int height = fm.getHeight();
		int width = fm.stringWidth(""+character);
		image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g2d.setFont(font);

		fm = g2d.getFontMetrics();
		g2d.drawString(""+character, 0, fm.getAscent());
		g2d.dispose();
		g2d.drawImage(image, 0, 0, null);
		if (touchedpoint != null)
			g2d.drawOval(touchedpoint.x, touchedpoint.y, 10, 10);
		validate();
		repaint();
	}

	private Direction[] fillDirections(Point p, Point prev) {
		if (prev == null) {
			Direction[] directions = new Direction[1];
			directions[0] = Direction.INIT;
			return directions;
		} else {
			return Utils.ComparePointsToCheckFV(p.getX(), p.getY(), prev.getX(), prev.getY());
		}
	}

	private void CleanSaveDirectionsAndTriggerPoints(ArrayList<Point> triggerpoints, Direction[] dirs, char word) {
		List<Direction> list = Arrays.asList(dirs);
		Stack<Direction> fvStack = new Stack<Direction>();
		fvStack.addAll(list);
		Utils.writeDirectionStackTofile(fvStack, Utils.FVOutputPATH + word + Utils.AppenddedToOutputFVfile);
		Utils.writePointsStackTofile(triggerpoints,
				Utils.TriggerPointsOutputPATH + word + Utils.AppenddedToOutputTriggerPointsfile);
	}
}