package gui;


import information.Fonts;
import information.InformationCenter;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * The Class InfoDialog. Opens dialog window for showing information of MC-Cone software.
 */
public class InfoDialog extends PropertiesDialog{
	protected final static Logger LOGGER = Logger.getLogger("MCCLogger");
	public InfoDialog(JFrame frame, GUI gui, Point point) {
		super(frame, gui, point);
		initDialog();
	}

	/**
	 * Hides Dialog window;
	 * @param saveChanges boolean value has to give although it is not doing anything.
	 */
	protected void hideDialog(boolean saveChanges){
		this.setVisible(false);
		dispose();
	}

	/* (non-Javadoc)
	 * @see gui.PropertiesDialog#initCenterPanels()
	 */
	protected JPanel initCenterPanels(){
		JPanel infoPanel=new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		infoPanel.setBackground(Color_schema.dark_40);

		JTextArea textArea = new JTextArea();
		textArea.setMinimumSize(new Dimension(380,400));
		textArea.setPreferredSize(new Dimension(380,400));
		textArea.setMaximumSize(new Dimension(380,400));
		textArea.setBackground(Color_schema.dark_40);
		textArea.setForeground(Color_schema.white_230);
		textArea.setFont(Fonts.p16);

		final JEditorPane editor = new JEditorPane();
		editor.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		editor.setEditable(false);
		editor.setMinimumSize(new Dimension(350,400));
		editor.setPreferredSize(new Dimension(350,400));
		editor.setMaximumSize(new Dimension(350,400));
		editor.setBackground(Color_schema.dark_40);
		editor.setForeground(Color_schema.white_230);
		editor.setFont(Fonts.p16);
		editor.setEditable(false);
		
		URL infoURL = InformationCenter.class.getResource("/information/html/program_info.html");
		if (infoURL != null) {
		    try {
		      
		        
		        String imagePath = getClass().getResource("/images/MC-Cone_small_200.png").toString();
		        String codeText = getPageAsString(infoURL.getPath());
		        String newCodeText= codeText.replaceFirst("/images/MC-Cone_small_200.png", imagePath);
		    //    System.out.println(codeText);
		        
		        editor.setText(newCodeText);
		        
		     //   editor.setPage(infoURL);
		        
		        
		    } catch (Exception e) {
		    	LOGGER.severe("Attempted to read a bad URL: " + infoURL);
		    }
		} else {
		    LOGGER.severe("Couldn't find file: program_info.html");
		}


		editor.addHyperlinkListener(new HyperlinkListener() {
		    public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

		        	if(Desktop.isDesktopSupported()) {
		        	    try {
							Desktop.getDesktop().browse(e.getURL().toURI());
						
							
						} catch (IOException e1) {
							LOGGER.severe("Can't open the link. Not supported by Operation system!");
							e1.printStackTrace();
						} catch (URISyntaxException e1) {

							LOGGER.severe("Can't open the link. Not supported by Operation system!");
							e1.printStackTrace();
						}
		        	}
		        	else{
		        		LOGGER.severe("Can't open the link. Not supported by Operation system!");
		        	}
		        }
		    }
		});
		infoPanel.add(editor);
		return infoPanel;
	}

	/* (non-Javadoc)
	 * @see gui.PropertiesDialog#initDownPanel()
	 */
	protected JPanel initDownPanel() throws Exception{
		// Setup buttons:
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBackground(Color_schema.dark_30);
		buttonPanel.setMinimumSize(new Dimension(50,40));

		// Button for OK
		JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(120,30));
		okButton.setBackground(Color_schema.dark_20);

		okButton.setFocusable(false);
		MouseListenerCreator.addMouseListenerToNormalButtons(okButton);  // add listener when button pressed -> change visualization of button
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hideDialog(false); // close dialog -> boolean value not used but has to be given.
			}
		});

		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(okButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(20,0)));
		
		return buttonPanel;
	}

	/* (non-Javadoc)
	 * @see gui.PropertiesDialog#initUPPanels()
	 */
	protected JPanel initUPPanels(){
		try {
			// contains title panel
			JPanel upperBackPanel = new JPanel();
			upperBackPanel.setLayout(new BoxLayout(upperBackPanel,BoxLayout.PAGE_AXIS));
			upperBackPanel.setMaximumSize(new Dimension(panelWidth, 30));
			upperBackPanel.setMinimumSize(new Dimension(panelWidth, 30));
			upperBackPanel.setPreferredSize(new Dimension(panelWidth, 30));
			upperBackPanel.add(initTitlePanel("About MC-Cone"));

			return upperBackPanel;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe("Error in creating title for the window!");
			return null;
		}
	}
	
	private String getPageAsString(String url){
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader(url));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
		}
		return contentBuilder.toString();
	}


}
