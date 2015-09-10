package information;

import gui.GUI;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Fonts {
	public static Font inconsolata16b;
	public static Font b14;
	public static Font b15;
	public static Font b16;
	public static Font b17;
	public static Font b18;
	public static Font b19;
	public static Font b20;
	public static Font b21;
	public static Font b22;
	public static Font p14;
	public static Font p15;
	public static Font p16;
	public static Font p17;
	public static Font p18;
	public static Font p19;
	public static Font p20;
	private static String fontName="Consolas";
	public static void initFonts(){
	//	String path= "/information/fonts/Inconsolata.otf";

		try {
/*
			b14=new Font(fontName,Font.BOLD,14);
			b15=new Font(fontName,Font.BOLD,15);
			b16=new Font(fontName,Font.BOLD,16);
			b17=new Font(fontName,Font.BOLD,17);
			p14=new Font(fontName,Font.PLAIN,14);
			p15=new Font(fontName,Font.PLAIN,15);
			p16=new Font(fontName,Font.PLAIN,16);
			p17=new Font(fontName,Font.PLAIN,17);
			*/

			b14=getSourceSansProBold(14.0f);
			b15=getSourceSansProBold(15.0f);
			b16=getSourceSansProBold(16.0f);
			b17=getSourceSansProBold(17.0f);
			b18=getSourceSansProBold(18.0f);
			b19=getSourceSansProBold(19.0f);
			b20=getSourceSansProBold(20.0f);
			b21=getSourceSansProBold(21.0f);
			b22=getSourceSansProBold(22.0f);
			p14=getSourceSansPro(14.0f);
			p15=getSourceSansPro(15.0f);
			p16=getSourceSansPro(16.0f);
			p17=getSourceSansPro(17.0f);
			p18=getSourceSansPro(18.0f);
			p19=getSourceSansPro(19.0f);
			p20=getSourceSansPro(20.0f);



		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static Font getInconsolata(float size) throws FontFormatException, IOException{
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/Inconsolata.otf"));
		return font.deriveFont(size);
	}

	private static Font getInconsolataBold(float size) throws FontFormatException, IOException{
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/Inconsolata-Bold.ttf"));
		return font.deriveFont(size);
	}
	private static Font getDejaVuSansMono(float size) throws FontFormatException, IOException{
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/DejaVuSansMono.ttf"));
		return font.deriveFont(size);
	}
	private static Font getDejaVuSansMonoBold(float size) throws FontFormatException, IOException{
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/DejaVuSansMono-Bold.ttf"));
		return font.deriveFont(size);
	}
	private static Font getSourceSansProBold(float size) throws FontFormatException, IOException{
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/SourceSansPro-Bold.otf"));
		return font.deriveFont(size);
	}

	private static Font getSourceSansPro(float size) throws FontFormatException, IOException{
		Font font = Font.createFont(Font.TRUETYPE_FONT, Fonts.class.getResourceAsStream("/information/fonts/SourceSansPro-Regular.otf"));
		return font.deriveFont(size);
	}

}
