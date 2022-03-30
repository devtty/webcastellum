package org.webcastellum;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.servlet.FilterConfig;




// eventually -Djava.awt.headless=true must be used ...
public final class DefaultCaptchaGenerator implements CaptchaGenerator {

    private static final boolean DUMMY_CAPTCHAS = false;
    
    // TODO: da die sizes der images ja konfigurierbar sind, sollten sich die offsets daran prozentual ausrichten..damit die offsets dann nicht zu hoch bzw. zu niedrig sind....
    
    
    public static final String PARAM_LENGTH = "DefaultCaptchaGeneratorBaseLength";
    public static final String PARAM_IMAGE_WIDTH = "DefaultCaptchaGeneratorImageWidth";
    public static final String PARAM_IMAGE_HEIGHT = "DefaultCaptchaGeneratorImageHeight";
    public static final String PARAM_IMAGE_FORMAT = "DefaultCaptchaGeneratorImageFormat";
    public static final String PARAM_FONT_NAME_A = "DefaultCaptchaGeneratorFontNameA";
    public static final String PARAM_FONT_NAME_B = "DefaultCaptchaGeneratorFontNameB";
    public static final String PARAM_COMPLEXITY = "DefaultCaptchaGeneratorComplexity"; // between 1 (easy) and 5 (complex)

    // no "i", "I", "j", "l", "1",      "0", "O",       "Z", "2",       "5", "S",     "t", "r", "f"    "G","C","Q"      to avoid mismatches
    private static final char[] ALPHABET = new char[] {'a','b','c','d','e','g','h','k','m','n','p','q','u','v','w','x','y','A','B','D','E','F','H','J','K','L','M','N','P','R','T','U','V','W','X','Y','3','4','6','7','8','9'};
            
    private static final Random RANDOM = new Random(); // not security relevant here
    
            
    private String format, fontA, fontB;
    private byte length;
    private short width, height;
    private byte closeness; // the higher the value the closer the chars are rendered to each other
    private byte linesBefore, linesAfter;
    private boolean rasterBefore, rasterAfter;
    
    
    public void setFilterConfig(FilterConfig filterConfig) throws FilterConfigurationException {
        final ConfigurationManager configManager = ConfigurationUtils.createConfigurationManager(filterConfig);
        { // length
            final String value = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_LENGTH, "7");
            try {
                this.length = Byte.parseByte(value);
                if (this.length <= 0) throw new FilterConfigurationException("Length must be positive");
            } catch (NumberFormatException e) {
                throw new FilterConfigurationException("Unable to parse value into byte: "+value, e);
            }
        }
        { // width
            final String value = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_IMAGE_WIDTH, "160");
            try {
                this.width = Short.parseShort(value);
                if (this.width <= 0) throw new FilterConfigurationException("Width must be positive");
            } catch (NumberFormatException e) {
                throw new FilterConfigurationException("Unable to parse value into short: "+value, e);
            }
        }
        { // height
            final String value = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_IMAGE_HEIGHT, "60");
            try {
                this.height = Short.parseShort(value);
                if (this.height <= 0) throw new FilterConfigurationException("Height must be positive");
            } catch (NumberFormatException e) {
                throw new FilterConfigurationException("Unable to parse value into short: "+value, e);
            }
        }
        // format
        this.format = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_IMAGE_FORMAT, "jpeg");
        // font
        this.fontA = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_FONT_NAME_A, "Dialog");
        this.fontB = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_FONT_NAME_B, "Arial");
        { // complexity
            final String value = ConfigurationUtils.extractOptionalConfigValue(configManager, PARAM_COMPLEXITY, "3");
            try {
                final byte complexity = Byte.parseByte(value);
                if (this.height <= 0) throw new FilterConfigurationException("Complexity must be positive");
                if (complexity == 1) {
                    this.rasterBefore = true;
                    this.rasterAfter = false;
                    this.closeness = 3;
                    this.linesBefore = 1;
                    this.linesAfter = 1;
                } else if (complexity == 2) {
                    this.rasterBefore = true;
                    this.rasterAfter = false;
                    this.closeness = 4;
                    this.linesBefore = 2;
                    this.linesAfter = 2;
                } else if (complexity == 3) {
                    this.rasterBefore = true;
                    this.rasterAfter = false;
                    this.closeness = 5;
                    this.linesBefore = 3;
                    this.linesAfter = 3;
                } else if (complexity == 4) {
                    this.rasterBefore = true;
                    this.rasterAfter = true;
                    this.closeness = 6;
                    this.linesBefore = 4;
                    this.linesAfter = 4;
                } else if (complexity == 5) {
                    this.rasterBefore = true;
                    this.rasterAfter = true;
                    this.closeness = 7;
                    this.linesBefore = 5;
                    this.linesAfter = 5;
                } else throw new FilterConfigurationException("Complexity must be between 1 (easy) and 5 (complex)");
            } catch (NumberFormatException e) {
                throw new FilterConfigurationException("Unable to parse value into byte: "+value, e);
            }
        }
    }

    private void drawRaster(final Graphics2D graphics, final int stepsX, final int stepsY) {
        graphics.setColor(Color.LIGHT_GRAY);
        final int offsetStepY = RANDOM.nextInt(8);
        final int stepY = this.height / stepsY;
        for (int i = 0; i < stepsY; i++) {
            final int y = (offsetStepY + i * stepY) - 3 + RANDOM.nextInt(6);
            graphics.drawLine(0, y, this.width, y);
        }
        final int offsetStepX = RANDOM.nextInt(12);
        final int stepX = this.width / stepsX;
        for (int i = 0; i < stepsX; i++) {
            final int x = (offsetStepX + i * stepX) - 3 + RANDOM.nextInt(6);
            graphics.drawLine(x, 0, x, this.height);
        }
    }

    
    
    
    private float randomRotation() {
        return (float) -0.25+(RANDOM.nextFloat()/2F);
    }
    
    
    // see http://www.andrewtimberlake.com/blog/2006/06/ for some good tips
    public Captcha generateCaptcha() throws CaptchaGenerationException {
        // generate text
        final int captchaLength = this.length-1+RANDOM.nextInt(2);
        final char[] chars = new char[captchaLength];
        final int max = ALPHABET.length-1;
        //Matcher matcher = null;
        for (int x=0; x<100; x++) { // = not more than 100 tries
            // create string
            for (byte b=0; b<captchaLength; b++) chars[b] = ALPHABET[ RANDOM.nextInt(max) ];
            // check string
            //if (matcher == null) matcher = CryptoUtils.PATTERN_UNWANTED_RANDOM_CONTENT.matcher( new String(chars) );
            //else matcher.reset( new String(chars) );
            if (!WordMatchingUtils.matchesWord(CryptoUtils.UNWANTED_RANDOM_CONTENT, new String(chars), WebCastellumFilter.TRIE_MATCHING_THRSHOLD)) break;
        }
        //matcher = null;
        
        // create image
        final BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_BGR);
        final Graphics2D graphics = image.createGraphics();
        final Color backgroudColor = new Color(200+RANDOM.nextInt(45),200+RANDOM.nextInt(45),200+RANDOM.nextInt(45));
        final Color darker = backgroudColor.darker();
        final Color brighter = backgroudColor.brighter();
        final Paint backgroudPaint = new GradientPaint(5+RANDOM.nextInt(80),5+RANDOM.nextInt(80),darker, this.width-5-RANDOM.nextInt(80),this.height-5-RANDOM.nextInt(80),brighter, true);
        graphics.setColor(backgroudColor);
        graphics.setPaint(backgroudPaint);
        graphics.fillRect(0, 0, this.width, this.height);
        if (!DUMMY_CAPTCHAS) {
            // rasterBefore
            if (this.rasterBefore) drawRaster(graphics, 15 + RANDOM.nextInt(10), 5 + RANDOM.nextInt(7));
            // lines + ovals before
            graphics.setColor(darker);
            for (byte b=0; b<this.linesBefore; b++) {
                graphics.drawLine(RANDOM.nextInt(80), RANDOM.nextInt(80), this.width-RANDOM.nextInt(80), this.height-RANDOM.nextInt(80));
                graphics.drawOval(RANDOM.nextInt(80), RANDOM.nextInt(80), 40+RANDOM.nextInt(80), 40+RANDOM.nextInt(80));
            }
            graphics.setColor(brighter);
            for (byte b=0; b<this.linesBefore; b++) {
                graphics.drawLine(RANDOM.nextInt(80), RANDOM.nextInt(80), this.width-RANDOM.nextInt(80), this.height-RANDOM.nextInt(80));
                graphics.drawOval(RANDOM.nextInt(80), RANDOM.nextInt(80), 40+RANDOM.nextInt(80), 40+RANDOM.nextInt(80));
            }
        }
        // text
        Font fontBigA = new Font(this.fontA, Font.BOLD|Font.ITALIC, 18+RANDOM.nextInt(1));
        Font fontSmallA = new Font(this.fontA, Font.BOLD, 17+RANDOM.nextInt(1));
        Font fontBigB = new Font(this.fontB, Font.BOLD|Font.ITALIC, 18+RANDOM.nextInt(1));
        Font fontSmallB = new Font(this.fontB, Font.BOLD, 17+RANDOM.nextInt(1));
        if (!DUMMY_CAPTCHAS) {
            fontBigA = fontBigA.deriveFont(AffineTransform.getRotateInstance(randomRotation()));
            fontSmallA = fontSmallA.deriveFont(AffineTransform.getRotateInstance(randomRotation()));
            fontBigB = fontBigB.deriveFont(AffineTransform.getRotateInstance(randomRotation()));
            fontSmallB = fontSmallB.deriveFont(AffineTransform.getRotateInstance(randomRotation()));
        }
        final Color colorA = darker.darker();
        final Color colorB = colorA.darker();
        final int offsetX = RANDOM.nextInt(20)+20;
        final int offsetY = RANDOM.nextInt(25)+10;
        final int spacing = ((this.width-8)-this.closeness*chars.length) / chars.length;
        for (byte b=0; b<chars.length; b++) {
            final char c = chars[b];
            graphics.setColor( RANDOM.nextBoolean() ? colorA : colorB );
            graphics.setFont( RANDOM.nextBoolean() ? RANDOM.nextBoolean()?fontBigA:fontBigB : RANDOM.nextBoolean()?fontSmallA:fontSmallB );
            graphics.drawString(""+c, offsetX+b*spacing+RANDOM.nextInt(5), offsetY+RANDOM.nextInt(15));
        }
        if (!DUMMY_CAPTCHAS) {
            // lines + ovals after
            graphics.setColor(darker);
            for (byte b=0; b<this.linesAfter; b++) {
                graphics.drawLine(RANDOM.nextInt(80), RANDOM.nextInt(80), this.width-RANDOM.nextInt(80), this.height-RANDOM.nextInt(80));
                graphics.drawOval(RANDOM.nextInt(80), RANDOM.nextInt(80), 40+RANDOM.nextInt(80), 40+RANDOM.nextInt(80));
            }
            // rasterAfter
            if (this.rasterAfter) drawRaster(graphics, 3 + RANDOM.nextInt(3), 2 + RANDOM.nextInt(2));
        }
        // finish
        final  ByteArrayOutputStream sink = new ByteArrayOutputStream(); // does not need to be closed
        try {
            ImageIO.write(image, this.format, sink);
        } catch (IOException e) {
            throw new CaptchaGenerationException("Unable to write image bytes from captcha buffered image", e);
        }
        return new Captcha( String.valueOf(chars), sink.toByteArray(), this.width, this.height, this.format );
    }
    

    
    
    /** /
    // just for local testing
    public static final void main(String[] args) throws Exception {
        String folder = "/tmp";
        if (args.length > 0) folder = args[0];
        final CaptchaGenerator generator = new DefaultCaptchaGenerator();
        generator.setFilterConfig(new FilterConfig(){
            public String getFilterName() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            public javax.servlet.ServletContext getServletContext() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            public java.util.Enumeration getInitParameterNames() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            public String getInitParameter(String key) {
                if (PARAM_COMPLEXITY.equals(key)) return "4";
                return null;
            }
        });
        final long timer = System.currentTimeMillis();
        for (int i=0; i<10; i++) {
            final Captcha captcha = generator.generateCaptcha();
            final java.io.File file = new java.io.File(folder+"/bild"+i+"."+captcha.getImageFormat());
            java.io.BufferedOutputStream output = null;
            try {
                output = new java.io.BufferedOutputStream( new java.io.FileOutputStream(file) );
                output.write( captcha.getImage() );
            } finally {
                if (output != null) try { output.close(); } catch (IOException ignored) {}
            }
        }
        System.out.println("Result in "+(System.currentTimeMillis()-timer)+" ms");
    }/**/
    
}
