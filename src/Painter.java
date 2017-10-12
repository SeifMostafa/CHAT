import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import model.Direction;

public class Painter {
	private static final double TOLERANCE_SAME = 25;
	private Graphics2D g2d;
	public Font font;
	private FontMetrics fm;
	private BufferedImage image;
	public Stack<String> characters;
	private CharacterCustomization charCustPane;
	private String lang;
	public JFrame frame;
	int character_pos = 0;
	Loader loader;

	public Painter(Stack<String> Characters,Loader loadr) {
		super();
		this.characters = Characters;
		this.loader = loadr;
		image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		g2d = image.createGraphics();

		font = new Font(SeShatEditorMain.utils.FONTNAME, Font.PLAIN, 512);
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

		

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame(SeShatEditorMain.utils.CharCustWindowTitle);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				charCustPane = new CharacterCustomization();
				frame.add(charCustPane);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	public class CharacterCustomization extends JPanel {

		private static final long serialVersionUID = 1L;
		Direction[] directions,pre_directions;
		Point touchedpoint = null;
		Point prev_touchedpoint = null;
		String character;
		
		public CharacterCustomization() {
			character = characters.pop();

			directions = new Direction[0];
			MouseAdapter ma = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {

				}

				@Override
				public void mouseDragged(MouseEvent e) {
				
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					touchedpoint = e.getPoint();
					directions = SeShatEditorMain.utils.concatenate(directions, fillDirections(touchedpoint, prev_touchedpoint));
					prev_touchedpoint = touchedpoint;
				}
			};

			addMouseListener(ma);
			addMouseMotionListener(ma);
			setLayout(new BorderLayout());
			JButton btn_nxt;
			btn_nxt = new JButton("NEXT");
			btn_nxt.setPreferredSize(new Dimension(500, 500));
			btn_nxt.setVisible(true);
			btn_nxt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (characters.isEmpty()) {
						CleanSaveDirections( directions, character);
						
						frame.dispose();
						JOptionPane.showMessageDialog(new JFrame(), "finished language characters!", "Thanks!",
								JOptionPane.INFORMATION_MESSAGE);
						SeShatEditorMain.utils.UpdateStateInConfigFile(State.CHARSPAINTED);
						SeShatEditorMain.LangCharsFinishingPaint_FV_TR__Pressed();
					} else {
							CleanSaveDirections( directions, character);
							character = characters.pop();
						validate();
						repaint();
						pre_directions = new Direction[directions.length];
						for(int i=0;i<directions.length;i++){
							pre_directions[i] = directions[i];
						}
						directions = new Direction[0];
						prev_touchedpoint = null;
					}
				}
			});
			add(btn_nxt, BorderLayout.EAST);
			
			JButton btn_same_as_previous;
			btn_same_as_previous = new JButton("Same As Previous");
			btn_same_as_previous.setPreferredSize(new Dimension(100, 100));
			btn_same_as_previous.setVisible(true);
			btn_same_as_previous.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					directions = pre_directions;
					if (characters.isEmpty()) {
						CleanSaveDirections( directions, character);
						
						frame.dispose();
						JOptionPane.showMessageDialog(new JFrame(), "finished language characters!", "Thanks!",
								JOptionPane.INFORMATION_MESSAGE);
						SeShatEditorMain.utils.UpdateStateInConfigFile(State.CHARSPAINTED);
						SeShatEditorMain.LangCharsFinishingPaint_FV_TR__Pressed();
					} else {
							CleanSaveDirections( directions, character);
							character = characters.pop();
						validate();
						repaint();
						pre_directions = new Direction[directions.length];
						for(int i=0;i<directions.length;i++){
							pre_directions[i] = directions[i];
						}
						directions = new Direction[0];
						prev_touchedpoint = null;
					}
				}
			});
			add(btn_same_as_previous, BorderLayout.PAGE_END);
		
		}

		@Override
		public Dimension getPreferredSize() {

			return new Dimension((int) SeShatEditorMain.utils.width, (int) SeShatEditorMain.utils.height);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setFont(font);

			fm = g2d.getFontMetrics();
			int height = fm.getHeight();
			int width = fm.stringWidth(character);
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2d.setFont(font);

			fm = g2d.getFontMetrics();
			
			g2d.drawString(character, 0, fm.getAscent());
			g2d.dispose();
			g2d.drawImage(image, 0, 0, null);
			if (touchedpoint != null)
				g2d.drawOval(touchedpoint.x, touchedpoint.y, 10, 10);
			validate();
			repaint();
		}
		private void CleanSaveDirections(Direction[] dirs, String character) {
			Direction [] last_dir = new Direction[1];
			last_dir[0] = Direction.END;
			dirs = SeShatEditorMain.utils.concatenate(dirs,last_dir);
			List<Direction> list = Arrays.asList(dirs);
			Stack<Direction> fvStack = new Stack<Direction>();
			fvStack.addAll(list);
			loader.writeDirectionStackTofile(fvStack,  character + character_pos);
			if(character_pos == 3){
				character_pos = 0;	
			}else{
				character_pos++;
			}
		}
	}

	private Direction[] fillDirections(Point p, Point prev) {
		if (prev == null) {
			Direction[] directions = new Direction[1];
			directions[0] = Direction.INIT;
			return directions;
		} else {
			return ComparePointsToCheckFV(p.getX(), p.getY(), prev.getX(), prev.getY());
		}
	}


	private  Direction[] ComparePointsToCheckFV(double x1, double y1, double x2, double y2) {
		Direction direction[] = new Direction[2];
		if (x1 > x2)
			direction[0] = Direction.RIGHT;
		else if (x1 < x2)
			direction[0] = Direction.LEFT;
		else if(Math.abs(x1-x2) <= TOLERANCE_SAME)
			direction[0] = Direction.SAME;
		else direction[0] = null;

		if (y1 > y2)
			direction[1] = Direction.DOWN;
		else if (y1 < y2)
			direction[1] = Direction.UP;
		else if(Math.abs(y1-y2) <= TOLERANCE_SAME)
			direction[0] = Direction.SAME;
		else
			direction[1] = null;
		return direction;
	}
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
}