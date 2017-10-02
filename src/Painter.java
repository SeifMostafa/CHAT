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
import java.util.ArrayList;
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
	private Graphics2D g2d;
	public Font font;
	private FontMetrics fm;
	private BufferedImage image;
	public Stack<String> words;
	private CharactersCustomization pane;
	private String lang;
	public JFrame frame;

	public Painter(Stack<String> WORDS) {
		super();
		this.words = WORDS;
		image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		g2d = image.createGraphics();

		font = new Font(Utils.FONTNAME, Font.PLAIN, 512);
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

		Utils.createdir(Utils.SpeechOutputPATH);
		Utils.createdir(Utils.ImagesOutputPATH);
		Utils.createdir(Utils.FVOutputPATH);
		Utils.createdir(Utils.TriggerPointsOutputPATH);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame = new JFrame(Utils.CharCustWindowTitle);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				pane = new CharactersCustomization();
				frame.add(pane);
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	public class CharactersCustomization extends JPanel {

		private static final long serialVersionUID = 1L;
		Direction[] directions;
		ArrayList<Point> TriggerPoints;
		Point touchedpoint = null;
		Point prev_touchedpoint = null;
		String word;
		int character_pos = 0;

		public CharactersCustomization() {
			word = words.pop();

			directions = new Direction[0];
			TriggerPoints = new ArrayList<>();
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
					directions = Utils.concatenate(directions, fillDirections(touchedpoint, prev_touchedpoint));
					TriggerPoints.add(touchedpoint);
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
					if (words.isEmpty()) {
						CleanSaveDirectionsAndTriggerPoints(TriggerPoints, directions, word);
						
						frame.dispose();
						JOptionPane.showMessageDialog(new JFrame(), "finished language characters!", "Thanks!",
								JOptionPane.INFORMATION_MESSAGE);
						Utils.UpdateStateInConfigFile(State.CHARSPAINTED);
						SeShatEditorMain.LangCharsFinishingPaint_FV_TR__Pressed();
					} else {
							CleanSaveDirectionsAndTriggerPoints(TriggerPoints, directions, word);
						word = words.pop();
						validate();
						repaint();
						directions = new Direction[0];
						prev_touchedpoint = null;
						TriggerPoints.clear();
					}
				}
			});
			add(btn_nxt, BorderLayout.EAST);
		}

		@Override
		public Dimension getPreferredSize() {

			return new Dimension((int) Utils.width, (int) Utils.height);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setFont(font);

			fm = g2d.getFontMetrics();
			int height = fm.getHeight();
			int width = fm.stringWidth(word);
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
			g2d.drawString(word, 0, fm.getAscent());
			g2d.dispose();
			g2d.drawImage(image, 0, 0, null);
			if (touchedpoint != null)
				g2d.drawOval(touchedpoint.x, touchedpoint.y, 10, 10);
			validate();
			repaint();
		}
		private void CleanSaveDirectionsAndTriggerPoints(ArrayList<Point> triggerpoints, Direction[] dirs, String word) {
			Direction [] last_dir = new Direction[1];
			last_dir[0] = Direction.END;
			dirs = Utils.concatenate(dirs,last_dir);
			List<Direction> list = Arrays.asList(dirs);
			Stack<Direction> fvStack = new Stack<Direction>();
			fvStack.addAll(list);
			Utils.createfile(Utils.FVOutputPATH + word + Utils.AppenddedToOutputFVfile);
			Utils.createfile(Utils.TriggerPointsOutputPATH + word + Utils.AppenddedToOutputTriggerPointsfile);
			
			Utils.writeDirectionStackTofile(fvStack, Utils.FVOutputPATH + word + character_pos +Utils.AppenddedToOutputFVfile);
			Utils.writePointsStackTofile(triggerpoints,
					Utils.TriggerPointsOutputPATH + word + character_pos + Utils.AppenddedToOutputTriggerPointsfile);
			
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
			return Utils.ComparePointsToCheckFV(p.getX(), p.getY(), prev.getX(), prev.getY());
		}
	}



	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
}