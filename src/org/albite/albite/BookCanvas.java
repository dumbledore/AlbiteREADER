/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import org.albite.book.model.Book;
import org.albite.book.model.BookException;
import org.albite.book.model.Chapter;
import org.albite.book.view.Booklet;
import org.albite.book.view.Page;
import org.albite.font.AlbiteFont;
import org.albite.book.view.PageDummy;
import org.albite.book.view.PageText;
import org.albite.book.view.Region;
import org.albite.book.view.RegionText;
import org.albite.font.AlbiteFontException;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

/**
 *
 * @author Albus Dumbledore
 */
public class BookCanvas extends Canvas {
    private static final int TASK_NONE              = 0;
    private static final int TASK_MENU              = 1;
    private static final int TASK_LIBRARY           = 2;
    private static final int TASK_DICTIONARY        = 3;
    private static final int TASK_FONTSIZE          = 4;
    private static final int TASK_COLORSCHEME       = 5;
    
    public  static final int MENU_HEIGHT            = 45;
    public  static final int MARGIN_WIDTH           = 10;
    private static final int STATUS_BAR_SPACING     = 3;

    private static final int DRAG_TRESHOLD          = 40;
    private static final int MARGIN_CLICK_TRESHOLD  = 60;
    private static final long HOLD_TIME             = 800; //in millis

    private long startPointerHoldingTime;

    /*
     * Targeting at 60 FPS
     */
    private static final int    FRAME_TIME              = 1000 / 60;
    private static final float  MAXIMUM_SPEED           = 4F;

    private float               speedMultiplier         = 0.3F;
    private boolean             scrollingOnX            = true;
    private boolean             doNotSwapWH             = true;

    private int                 scrollNextPagePixels    = 55;
    private int                 scrollSamePagePixels    = 5;
    private int                 scrollStartBookPixels   = 30;
    private boolean             horizontalScrolling     = true;

    /**
     * If true, the pages will be in reversed order
     */
    private boolean             inverted                = false;

    private int                 pageCanvasPositionMin   = 0;
    private int                 pageCanvasPositionMax   = 0;

    private static final int AUTOSAVE_TIME          = 5 * 60 * 1000;

    private static final int MODE_DONT_RENDER       = 0;
    private static final int MODE_PAGE_LOCKED       = 1;
    private static final int MODE_PAGE_LOADING      = 2;
    private static final int MODE_PAGE_READING      = 3;
    private static final int MODE_PAGE_SCROLLING    = 4;
    private static final int MODE_BUTTON_PRESSING   = 5;
    private static final int MODE_WORD_SELECTED     = 6;
    private static final int MODE_MARKING_WORDS     = 7;

    private int mode = MODE_DONT_RENDER;

    /*
     * 180-degree rotation will not be supported as it introduces code
     * complexity, that is not quite necessary
     */
    public static final int ORIENTATION_0           = Sprite.TRANS_NONE;
    public static final int ORIENTATION_90          = Sprite.TRANS_ROT90;
    public static final int ORIENTATION_180         = Sprite.TRANS_ROT180;
    public static final int ORIENTATION_270         = Sprite.TRANS_ROT270;

    private int orientation = ORIENTATION_0;
    private boolean fullscreen = false;

    /* If true, orientation is changed automatically, i.e. using the
     * motion sensor (if available)
     *
     * If false, orientation is changed, only manually, i.e. through the menu.
     */
    private boolean otientationAuto = true;

    public static final int SCROLL_PREV         = 0;
    public static final int SCROLL_NEXT         = 1;
    public static final int SCROLL_SAME_PREV    = 2;
    public static final int SCROLL_SAME_NEXT    = 3;
    public static final int SCROLL_BOOK_START   = 4;
    public static final int SCROLL_BOOK_END     = 5;

    private volatile boolean repaintButtons     = true;

    private volatile boolean repaintStatusBar   = true;

    private volatile boolean repaintChapterNum  = false;
    private volatile boolean repaintProgressBar = false;
    private volatile boolean repaintClock       = false;
    
    private       char[] chapterNoChars  = {'#', '0', '0', '0'};
    private       int    pagesCount;
    private final char[] clockChars = {'0', '0', ':', '0', '0'};

    private              int statusBarHeight;
    private              int chapterNoWidth;
    private              int progressBarWidth;
    private              int progressBarHeight;
    private              int progressBarX;
    private              int clockWidth;

    private              int centerBoxSide;
    
    private ImageButton[]      buttons;
    private ImageButton        waitCursor;

    //input events
    private int xx = 0;
    private int yy = 0;
    private int xxPressed = 0;
    private int yyPressed = 0;
    private ImageButton buttonPressed = null;
    
    private ColorScheme         currentScheme;

    private AlbiteFont          fontPlain;
    private AlbiteFont          fontItalic;

    public static final byte   FONT_SIZE_12        = 0;
    public static final byte   FONT_SIZE_14        = 1;
    public static final byte   FONT_SIZE_16        = 2;
    public static final byte   FONT_SIZE_18        = 3;

    public static final byte[] FONT_SIZES = {12, 14, 16, 18};

    private boolean             fontGrowing = true;
    private byte                currentFontSizeIndex = FONT_SIZE_16;

    private AlbiteFont fontStatus;

    private Book currentBook;

    private ZLTextTeXHyphenator hyphenator = new ZLTextTeXHyphenator();;
    private Vector      dictionaries = new Vector(10);

    private Booklet     chapterBooklet;
    private PageCanvas  prevPageCanvas;
    private PageCanvas  currentPageCanvas;
    private PageCanvas  nextPageCanvas;
    private int         currentPageCanvasPosition;

    private Timer       timer;
    private TimerTask   scrollingTimerTask;
    private TimerTask   savingTimerTask;
    private TimerTask   clockTimerTask;
    private TimerTask   keysTimerTask;
    private TimerTask   pointerPressedTimerTask;
    private TimerTask   pointerReleasedTimerTask;
    private boolean     pointerPressedReady = true;
    private boolean     pointerReleasedReady = true;
    private boolean     keysReady = true;

    private AlbiteMIDlet app;

    private RecordStore rs;

    private boolean initialized = false;

    public BookCanvas(AlbiteMIDlet app) { //as its public it is not REALLY a singleton, but its certain that this one would not be called twice
        this.app = app;
        openRMSAndLoadData();
    }

    public final synchronized void initialize() {

        //prevent re-initialization
        if(initialized) {
            return;
        }

        loadFont();
        loadStatusFont();

        final int w = getWidth();

        centerBoxSide = w / 8;

        statusBarHeight = fontStatus.lineHeight + (STATUS_BAR_SPACING * 2);

        /* Clock: 00:00 = 5 chars */
        clockWidth = (fontStatus.maximumWidth * clockChars.length) + (STATUS_BAR_SPACING * 2);

        /*
         * We assume that there would be no more than 999 chapters
         */
        chapterNoWidth = (fontStatus.maximumWidth * chapterNoChars.length) + (STATUS_BAR_SPACING * 2);

        progressBarWidth = w - (STATUS_BAR_SPACING * 4);
        if (chapterNoWidth > clockWidth) {
            progressBarWidth -= chapterNoWidth * 2;
        } else {
            progressBarWidth -= clockWidth * 2;
        }

        progressBarX = (w - progressBarWidth) / 2;

        progressBarHeight = (statusBarHeight - (STATUS_BAR_SPACING * 2)) / 3;

        /*
         * Load menu images
         * Images cannot be stored normally (i.e. as Image objects) as they need
         * to be mutable: one should be able to select the color of the image
         * without affecting the alpha channel
         */
        buttons    = new ImageButton[5];
        buttons[0] = new ImageButton(
                "/res/gfx/button_menu.ali", TASK_MENU);
        buttons[1] = new ImageButton(
                "/res/gfx/button_library.ali", TASK_LIBRARY);
        buttons[2] = new ImageButton(
                "/res/gfx/button_dict.ali", TASK_DICTIONARY);
        buttons[3] = new ImageButton(
                "/res/gfx/button_font_size.ali", TASK_FONTSIZE);
        buttons[4] = new ImageButton(
                "/res/gfx/button_color_profile.ali", TASK_COLORSCHEME);

        if (buttons.length > 0) {
            int x = 0;
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setX(x);
                buttons[i].setY(0);
                x += buttons[i].getWidth();
            }
        }

        waitCursor = new ImageButton("/res/gfx/hourglass.ali", TASK_NONE);

        /* set default profiles if none selected */
        if (currentScheme == null) {
            ColorScheme day = ColorScheme.DEFAULT_DAY;
            ColorScheme night = ColorScheme.DEFAULT_NIGHT;
            day.link(night);
            currentScheme = day;
        }

        applyColorProfile();

        initializePageCanvases();

//        System.gc();

        timer = new Timer();

        initialized = true;
    }

    private void initializePageCanvases() {
        final int w;
        final int h;

        switch(orientation) {
            case ORIENTATION_0:
                /* portrait normal mode */
                if (!fullscreen) {
                    w = getWidth() - (2 * MARGIN_WIDTH);
                    h = getHeight() - MENU_HEIGHT - statusBarHeight;
                    break;
                }
                /*
                 * No break, we go to fullscreen mode
                 */

            case ORIENTATION_180:
                /* portrait fullscreen mode */
                w = getWidth() - (2 * MARGIN_WIDTH);
                h = getHeight() - (2 * MARGIN_WIDTH);
                break;

            case ORIENTATION_90:
            case ORIENTATION_270:
                /* landscape fullscreen mode */
                w = getHeight() - (2 * MARGIN_WIDTH);
                h = getWidth() - (2 * MARGIN_WIDTH);
                break;

            default:
                w = 0;
                h = 0;
        }

        /* Remove old canvases if present! */
        currentPageCanvas   = null;
        nextPageCanvas      = null;
        prevPageCanvas      = null;

//        System.gc();

        currentPageCanvas   = new PageCanvas(w, h);
        nextPageCanvas      = new PageCanvas(w, h);
        prevPageCanvas      = new PageCanvas(w, h);

        currentPageCanvasPosition = 0;

        System.out.println((
                Runtime.getRuntime().totalMemory()
                - Runtime.getRuntime().freeMemory())
                + " of " + Runtime.getRuntime().totalMemory());
    }

    protected final void paint(Graphics g) {
        if (mode != MODE_DONT_RENDER) {
            final int w = getWidth();
            final int h = getHeight();

            if (!fullscreen) {
                if (repaintButtons) {
                    drawButtons(w, h, g);
                }

                if (repaintStatusBar) {
                    repaintStatusBar = false;

                    g.setColor(currentScheme.colors[
                            ColorScheme.COLOR_BACKGROUND]);

                    g.fillRect(0, h - statusBarHeight, w, statusBarHeight);

                    drawChapterNum(w, h, g);
                    drawProgressBar(w, h, g);
                    drawClock(w, h, g);
                } else {
                    /* If not the whole status bar is to be updated,
                     check if parts of it are
                     */

                    if (repaintChapterNum) {
                        drawChapterNum(w, h, g);
                    }

                    if (repaintProgressBar) {
                        drawProgressBar(w, h, g);
                    }

                    if (repaintClock) {
                        drawClock(w, h, g);
                    }
                }
            }

            switch (mode) {

                //this way one may implement layers
                case MODE_PAGE_READING:
                case MODE_PAGE_SCROLLING:
                case MODE_PAGE_LOCKED:

                    final int anchor = Graphics.TOP | Graphics.LEFT;

                    final Image imageC = currentPageCanvas.getImage();
                    final Image imageP = prevPageCanvas.getImage();
                    final Image imageN = nextPageCanvas.getImage();

                    final int imageWidth = imageC.getWidth();
                    final int imageHeight = imageC.getHeight();

//                    if (
//                            orientation == ORIENTATION_90
//                            || orientation == ORIENTATION_270) {
//                        doNotSwapWH = false;
//                    } else {
//                        doNotSwapWH = true;
//                    }

                    int x = 0;
                    int y = 0;

                    switch(orientation) {
                        case ORIENTATION_0:

                            if (!fullscreen) {
                                g.setClip(0, MENU_HEIGHT, w, imageHeight);
                                x = (scrollingOnX
                                        ? MARGIN_WIDTH
                                            + currentPageCanvasPosition
                                        : MARGIN_WIDTH);

                                y = (scrollingOnX
                                        ? MENU_HEIGHT
                                        : MENU_HEIGHT
                                            + currentPageCanvasPosition);
                            break;
                            }

                        /*
                         * No break here. We pass to fullscreen mode
                         */

                        case ORIENTATION_90:
                        case ORIENTATION_180:
                        case ORIENTATION_270:
                            g.setClip(0, 0, w, h);
                            x = (scrollingOnX
                                    ? MARGIN_WIDTH + currentPageCanvasPosition
                                    : MARGIN_WIDTH);

                            y = (scrollingOnX
                                    ? MARGIN_WIDTH
                                    : MARGIN_WIDTH + currentPageCanvasPosition);
                            break;
                    }

                    g.setColor(
                            currentScheme.colors[
                            ColorScheme.COLOR_BACKGROUND]);
                    g.fillRect(0, 0, w, h);
                    g.drawRegion(imageP, 0, 0, imageWidth, imageHeight,
                            orientation,
                            (scrollingOnX
                                ? x - (doNotSwapWH ? imageWidth : imageHeight)
//                                    - 2*MARGIN_WIDTH
                                    -MARGIN_WIDTH
                                : x),
                            (scrollingOnX
                                ? y
                                : y - (doNotSwapWH ? imageHeight : imageWidth)
//                                    - 2*MARGIN_WIDTH
                                    -MARGIN_WIDTH
                                ),
                            anchor);

                    g.drawRegion(imageC, 0, 0, imageWidth, imageHeight,
                            orientation, x, y, anchor);

                    g.drawRegion(imageN, 0, 0, imageWidth, imageHeight,
                            orientation,
                            (scrollingOnX
                                ? x + (doNotSwapWH ? imageWidth : imageHeight)
//                                    + 2*MARGIN_WIDTH
                                    +MARGIN_WIDTH
                                : x),
                            (scrollingOnX
                                ? y
                                : y + (doNotSwapWH ? imageHeight : imageWidth)
//                                    + 2*MARGIN_WIDTH
                                    +MARGIN_WIDTH
                                ),
                            anchor);

                    break;

                case MODE_PAGE_LOADING:
                    //draw loading cursor on top
                    waitCursor.draw(g, (w - waitCursor.getWidth()) / 2,
                            (h - waitCursor.getHeight()) / 2);
            }
        }
    }

    private void drawButtons(final int w, final int h, final Graphics g) {
        g.setColor(currentScheme.colors[ColorScheme.COLOR_BACKGROUND]);
        g.fillRect(0, 0, w, MENU_HEIGHT);

        if (buttons.length > 0) {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].draw(g, buttons[i].getX(), buttons[i].getY());
            }
            repaintButtons = false;
        }
    }

    private void drawChapterNum(final int w, final int h, final Graphics g) {

        repaintChapterNum = false;

        /*
         * Clearing background
         */
        g.setColor(
                currentScheme.colors[ColorScheme.COLOR_BACKGROUND]);

        g.fillRect(0, h - statusBarHeight, chapterNoWidth, statusBarHeight);

        /* drawing current chapter area */
        fontStatus.drawChars(g, currentScheme.colors[
                ColorScheme.COLOR_TEXT_STATUS], chapterNoChars,
                STATUS_BAR_SPACING, h - statusBarHeight + STATUS_BAR_SPACING,
                0, chapterNoChars.length);
    }

    private void drawProgressBar(final int w, final int h, final Graphics g) {

        repaintProgressBar = false;

        /*
         * Clearing background
         */
        g.setColor(
                currentScheme.colors[ColorScheme.COLOR_BACKGROUND]);

        g.fillRect(progressBarX, h - statusBarHeight,
                progressBarWidth, statusBarHeight);

        /* drawing progress bar */
        g.setColor(currentScheme.colors[ColorScheme.COLOR_TEXT_STATUS]);

        g.drawRect(
                progressBarX, h - ((statusBarHeight + progressBarHeight) / 2),
                progressBarWidth, progressBarHeight);

        final int barFilledWidth;
        
        if (pagesCount > 0) {
            barFilledWidth =
                    (int) (progressBarWidth
                    * (((float) chapterBooklet.getCurrentPageIndex() - 1)
                    / pagesCount));
        } else {
            barFilledWidth = progressBarWidth;
        }

        g.fillRect(
                progressBarX, h - ((statusBarHeight + progressBarHeight) / 2),
                barFilledWidth, progressBarHeight);
    }

    private void drawClock(final int w, final int h, final Graphics g) {

        repaintClock = false;

        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        final char[] clock = clockChars;

        clock[0] = (char) ('0' + (hour / 10));
        clock[1] = (char) ('0' + (hour % 10));
        clock[3] = (char) ('0' + (minute / 10));
        clock[4] = (char) ('0' + (minute % 10));

        final int clockPixelWidth = fontStatus.charsWidth(
                clock, 0, clock.length);

        /*
         * Clear background
         */
        g.setColor(currentScheme.colors[
                ColorScheme.COLOR_BACKGROUND]);

        g.fillRect(w - clockWidth, h - statusBarHeight, clockWidth,
                statusBarHeight);

        /*
         * Draw time
         */

        fontStatus.drawChars(g, currentScheme.colors[
                ColorScheme.COLOR_TEXT_STATUS], clock,
                w - clockPixelWidth - STATUS_BAR_SPACING,
                h - statusBarHeight + STATUS_BAR_SPACING);
    }

    private ImageButton findButtonPressed(final int x, final int y) {
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].buttonPressed(x, y)) {
                return buttons[i];
            }
        }
        return null;
    }

    protected final void pointerPressed(final int x, final int y) {
        if (pointerPressedReady) {
            pointerPressedReady = false;
            pointerPressedTimerTask =
                new TimerTask() {
                    public void run() {
                        processPointerPressed(x, y);
                        pointerPressedReady = true;
                    }
                };
            timer.schedule(pointerPressedTimerTask, 0);
        }
    }

    protected final void pointerReleased(final int x, final int y) {
        if (pointerReleasedReady) {
            pointerReleasedReady = false;
            pointerReleasedTimerTask =
                new TimerTask() {
                    public void run() {
                        processPointerReleased(x, y);
                        pointerReleasedReady = true;
                    }
                };
            timer.schedule(pointerReleasedTimerTask, 0);
        }
    }

    protected final void pointerDragged(final int x, final int y) {
        processPointerDragged(x, y);
    }

    protected final void keyPressed(final int k) {
        if (keysReady) {
            keysReady = false;
            keysTimerTask =
                new TimerTask() {
                    public void run() {
                        processKeys(k, false);
                        keysReady = true;
                    }
                };
            timer.schedule(keysTimerTask, 0);
        }
    }

    protected  final void keyRepeated(final int k) {
        if (keysReady) {
            keysReady = false;
            keysTimerTask =
                new TimerTask() {
                    public void run() {
                        processKeys(k, true);
                        keysReady = true;
                    }
                };
            timer.schedule(keysTimerTask, 0);
        }
    }

    private void processPointerPressed(final int x, final int y) {
        xx = xxPressed = x;
        yy = yyPressed = y;
        startPointerHoldingTime = System.currentTimeMillis();

        switch(mode) {
            case MODE_PAGE_READING:
                if (!fullscreen) {
                    /*
                     * Not in fullscreen, so it IS possible for the user
                     * to touch the buttons.
                     */
                    if (y <= MENU_HEIGHT) {
                        //has a button been pressed?
                        buttonPressed = findButtonPressed(x, y);
                        if (buttonPressed != null) {
                            mode = MODE_BUTTON_PRESSING;
                            buttonPressed.setColor(
                                    currentScheme.colors[
                                    ColorScheme.COLOR_MENU_PRESSED]);
                            repaintButtons = true;
                            repaint();
                            serviceRepaints();
                        }
                    }
                    break;
                }
        }
    }

    private final void processPointerReleased(final int x, final int y) {
        xx = x;
        yy = y;

        final int w = getWidth();
        final int h = getHeight();

        boolean holding =
            (System.currentTimeMillis() - startPointerHoldingTime > HOLD_TIME);

        switch(mode) {
            case MODE_PAGE_READING:

                final int xCenteredAbs = Math.abs(x - (w / 2));
                final int yCenteredAbs = Math.abs(y - (h / 2));

                final int p2 = progressBarWidth / 2;

                if (!fullscreen
                        && y > h - statusBarHeight && xCenteredAbs < p2) {

                    /*
                     * status bar area
                     */
                    if (holding) {

                        /*
                         * Show toc
                         */
                        app.calledOutside();
                        app.showToc();
                    } else {

                        /*
                         * Srcoll directly
                         */
                        goToPosition(
                                currentBook.getCurrentChapter(),
                                ((float) (x - progressBarX))
                                / ((float)progressBarWidth));
                    }

                } else if (!holding
                        &&(scrollingOnX ? x - w : y - h)
                        >
                        (!fullscreen && !scrollingOnX
                            ? -MARGIN_CLICK_TRESHOLD - statusBarHeight
                            : -MARGIN_CLICK_TRESHOLD)) {

                    /* Right Page position */

                    scheduleScrolling(SCROLL_NEXT);
                    mode = MODE_PAGE_SCROLLING;

                } else if (!holding
                        &&(scrollingOnX ? x : y) <
                        (!fullscreen && !scrollingOnX
                            ? MARGIN_CLICK_TRESHOLD + MENU_HEIGHT
                            : MARGIN_CLICK_TRESHOLD)) {

                    /* Left Page position */

                    mode = MODE_PAGE_SCROLLING;
                    scheduleScrolling(SCROLL_PREV);


                } else if (
                        !holding
                        && xCenteredAbs < centerBoxSide
                        && yCenteredAbs < centerBoxSide) {

                    /*
                     * Somewhere in the middle. Switch full screen
                     */
                    setOrientation(ORIENTATION_0, !fullscreen);
                }
                
                if (holding) {

                    /*
                     * find (x, y) coords from page's viewpoint,
                     * taking into account
                     * fullscreen mode / screen orientation
                     */

                    final int realx = getXonPage(x, y);
                    final int realy = getYonPage(x, y);

                    Region r =
                            chapterBooklet.getCurrentPage().getRegionAt(
                            realx, realy);

                    if (r != null) {
                        if (r instanceof RegionText) {
                            /*
                             * Get the text
                             */
                            final String text =
                                    ((RegionText) r).getText(
                                    chapterBooklet.getTextBuffer());

                            if (text != null) {
                                /*
                                 * Check if it's a word or a number
                                 */
                                boolean isNumber = true;
                                try {
                                    Double.parseDouble(text);
                                } catch (NumberFormatException e) {
                                    isNumber = false;
                                }

    //                                System.out.println(text + ", " + isNumber);
                                app.calledOutside();
                                app.setEntryForLookup(text);

                                if (isNumber) {
                                    /*
                                     * Show units converter,
                                     * with the number preentered
                                     */
                                    app.enterNumber();
                                } else {
                                    /*
                                     * Show dictionary,
                                     * with the word pre entered
                                     */
                                    app.enterWord();
                                }
                            }
                        }
                    }
                }

            break;

            case MODE_PAGE_SCROLLING:
                final int px = currentPageCanvasPosition;

                if (px == 0) {
                    stopScrolling();
                    mode = MODE_PAGE_READING;
                    break;
                }

                if (px < -DRAG_TRESHOLD) {
                    scheduleScrolling(SCROLL_NEXT);
                    break;
                }

                if (px > DRAG_TRESHOLD) {
                    scheduleScrolling(SCROLL_PREV);
                    break;
                }

                if (px > 0) {
                    scheduleScrolling(SCROLL_SAME_PREV);
                    break;
                }

                if (px <= 0) {
                    scheduleScrolling(SCROLL_SAME_NEXT);
                    break;
                }

                mode = MODE_PAGE_READING;
                break;

            case MODE_BUTTON_PRESSING:

                /*
                 * next is only a precaution. Pricipally, MODE_BUTTON_PRESSING
                 * is not expected to be executed at all in fullscreen
                 */
                if (!fullscreen) {
                    /*
                     * restore original color or the button
                     */
                    buttonPressed.setColor(currentScheme.colors[
                            ColorScheme.COLOR_MENU]);
                    repaintButtons = true;
                    repaint();
                    serviceRepaints();

                    if (buttonPressed == findButtonPressed(x, y)) {
                        app.calledOutside();

                        switch(buttonPressed.getTask()) {
                            case TASK_FONTSIZE:
                                if (holding) {
                                    app.setFontSize();
                                } else {
                                    cycleFontSizes();
                                }
                                break;

                            case TASK_COLORSCHEME:
                                if (holding) {
                                    app.setColorScheme();
                                } else {
                                    cycleColorSchemes();
                                }
                                break;

                            case TASK_LIBRARY:
                                app.openLibrary();
                                break;

                            case TASK_DICTIONARY:
                                app.setEntryForLookup("");
                                if (holding) {
                                    /* show unit converter */
                                    app.enterNumber();
                                } else {
                                    /* show dictionary */
                                    app.enterWord();
                                }
                                break;

                            case TASK_MENU:
                                if (holding) {
                                    //Exit midlet if user holds over the menu button
                                    app.quit();
                                } else {
                                    app.showMenu();
                                }
                                break;
                        }
                    }
                    buttonPressed = null;
                    mode = MODE_PAGE_READING;
                    break;
                }
        }
    }

    private void processPointerDragged(final int x, final int y) {
        switch(mode) {
            case MODE_PAGE_SCROLLING:
            case MODE_PAGE_READING:
                mode = MODE_PAGE_SCROLLING;
                stopScrolling();
                currentPageCanvasPosition += (scrollingOnX ? x - xx: y - yy);
                repaint();
                break;
        }

        /* It's essential that these values are updated
         * AFTER the switch statement! */
        xx = x;
        yy = y;
    }

    private void processKeys(final int k, final boolean repeated) {

        int kga = getGameAction(k);

        if (mode == MODE_PAGE_READING) {

            if (kga == (scrollingOnX ? LEFT : UP)) {
                scheduleScrolling(SCROLL_PREV);
                return;
            }

            if (kga == (scrollingOnX ? RIGHT : DOWN)) {
                scheduleScrolling(SCROLL_NEXT);
                return;
            }

            if (!repeated) {
                switch(kga) {

                    case FIRE:
                        /*
                         * Menu
                         */
                        app.calledOutside();
                        app.showMenu();
                        return;

                    case GAME_A:
                        //open library
                        app.calledOutside();
                        app.openLibrary();
                        return;

                    case GAME_B:
                        //open dictionary and unit converter
                        app.calledOutside();
                        app.setEntryForLookup("");
                        app.enterWord();
                        return;

                    case GAME_C:
                        cycleFontSizes();
                        return;

                    case GAME_D:
                        cycleColorSchemes();
                        return;
                }
            }
        }
    }

    public final boolean isBookOpen(String bookURL) {
        if (isBookOpen()
                && currentBook.getArchive().getFileURL().equals(bookURL)) {
            return true;
        }

        return false;
    }

    public final Book openBook(String bookURL)
            throws IOException, BookException {

        //If the book is already open, no need to load it again
        if (isBookOpen(bookURL)) {
            mode = MODE_PAGE_READING;
            return currentBook;
        }

        //try to open the book
        Book newBook = new Book();

        newBook.open(bookURL);

        //Try freeing resources before showing book
//        System.gc();

        //All was OK, let's close current book
        closeBook();

        currentBook = newBook;

        //load hyphenator according to book language
        hyphenator.load(currentBook.getLanguage());

        /*
         * Load the dictionaries
         */

        /*
         * Populate the tocList in app
         */
        final List toc = app.getToc();

        toc.deleteAll();

        final int count = currentBook.getChaptersCount();
        for (int i = 0; i < count; i++) {
            toc.append(currentBook.getChapter(i).getTitle(), null);
        }

        goToPosition(currentBook.getCurrentChapter(),
                currentBook.getCurrentChapterPosition());

        startAutomaticSaving();

        mode = MODE_PAGE_READING;

        return currentBook;
    }

    private void closeBook() {
        if (isBookOpen()) {
            stopAutomaticSaving();
            saveAllOptions();
            currentBook.close();
            currentBook = null;
            chapterBooklet = null;
        }
    }

    private void saveBookOptions() {
        if (isBookOpen()) {
            currentBook.saveUserData();
        }
    }

    private synchronized void saveAllOptions() {
        saveBookOptions();
        saveOptionsToRMS();
    }

    public final boolean isBookOpen() {
        return currentBook != null;
    }

    private void startAutomaticSaving() {
        if (savingTimerTask == null) {
            savingTimerTask = new TimerTask() {
                public void run() {
                    saveAllOptions();
                }
            };
            timer.schedule(savingTimerTask, AUTOSAVE_TIME, AUTOSAVE_TIME);
        }
    }

    private void stopAutomaticSaving() {
        if (savingTimerTask != null) {
            savingTimerTask.cancel();
            savingTimerTask = null;
        }
    }

    private void startClock() {
        if (clockTimerTask == null) {
            clockTimerTask = new TimerTask() {
                    public void run() {
                        updateClock();
                    }
                };
            timer.scheduleAtFixedRate(clockTimerTask, 60000, 60000);
        }
    }

    private void stopClock() {
        if (clockTimerTask != null) {
            clockTimerTask.cancel();
            clockTimerTask = null;
        }
    }

    private void scheduleScrolling(final int scrollMode) {
        if (scrollingTimerTask == null) {
            scrollingTimerTask = new TimerTask() {
                private int dx;
                private boolean fullPage;

                public void run() {
                    switch(scrollMode) {
                        case SCROLL_PREV:
                            dx = scrollNextPagePixels;
                            fullPage = true;
                            break;
                        case SCROLL_NEXT:
                            dx = -scrollNextPagePixels;
                            fullPage = true;
                            break;
                        case SCROLL_SAME_NEXT:
                            dx = scrollSamePagePixels;
                            fullPage = false;
                            break;
                        case SCROLL_SAME_PREV:
                            dx = -scrollSamePagePixels;
                            fullPage = false;
                            break;
                        case SCROLL_BOOK_END:
                            dx = scrollStartBookPixels;
                            fullPage = false;
                            break;
                        case SCROLL_BOOK_START:
                            dx = -scrollStartBookPixels;
                            fullPage = false;
                            break;
                        default:
                            dx = 0;
                            fullPage = false;
                            break;
                    }

                    scrollPages(dx, fullPage);
                }
            };

            timer.schedule(scrollingTimerTask, FRAME_TIME, FRAME_TIME);
        }
    }

    private synchronized void stopScrolling() {
        if (scrollingTimerTask != null) {
            scrollingTimerTask.cancel();
            scrollingTimerTask = null;
        }
    }

    /**
     * Scrolls the three PageCanvases across the screen.
     *
     * @param dx Relative amount to scroll
     * @param fullPage If true, the tree scroll to the next/previous page.
     * If false, scrolls back to the current page
     *
     */
    protected final void scrollPages(int dx, boolean fullPage) {
        currentPageCanvasPosition += dx;

        if (fullPage) {

            if (currentPageCanvasPosition >= pageCanvasPositionMax) {
                //loading prev page
                currentPageCanvasPosition = pageCanvasPositionMax;
                mode = MODE_PAGE_LOCKED;

                final Page page = chapterBooklet.getPrevPage();

                if (page instanceof PageDummy) {
                    PageDummy pd = (PageDummy)page;
                    handleDummyPage(pd.getType(), SCROLL_BOOK_START);
                }

                if (page instanceof PageText) {
                    PageText pt = (PageText)page;
                    byte ptType = pt.getType();

                    switch(ptType) {

                        case PageText.TYPE_IMAGE:
                        case PageText.TYPE_TEXT:
                            stopScrolling();
                            repaint();
                            serviceRepaints();
                            loadPrevPage();
                            repaint();
                            serviceRepaints();
                            return;
                    }
                }
            }

            if (currentPageCanvasPosition <= pageCanvasPositionMin) {
                //loading next page
                currentPageCanvasPosition = pageCanvasPositionMin;
                mode = MODE_PAGE_LOCKED;

                final Page page = chapterBooklet.getNextPage();

                if (page instanceof PageDummy) {
                    PageDummy pd = (PageDummy)page;
                    handleDummyPage(pd.getType(), SCROLL_BOOK_END);
                }

                if (page instanceof PageText) {
                    PageText pt = (PageText)page;
                    byte ptType = pt.getType();

                    switch(ptType) {

                        case PageText.TYPE_IMAGE:
                        case PageText.TYPE_TEXT:
                            stopScrolling();
                            repaint();
                            serviceRepaints();
                            loadNextPage();
                            repaint();
                            serviceRepaints();
                            return;
                    }
                }
            }

        repaint();
        serviceRepaints();

        } else {

            /* scrolling to the same page */
            if ((dx < 0 && currentPageCanvasPosition <= 0)
                    || (dx >= 0 && currentPageCanvasPosition >= 0)) {
                currentPageCanvasPosition = 0;
                stopScrolling();
                mode = MODE_PAGE_READING;
            }
            repaint();
            serviceRepaints();
        }
    }

    private void handleDummyPage(byte type, int bookScrollingDirection) {
        stopScrolling();

        switch (type) {
            case PageDummy.TYPE_CHAPTER_PREV:
                mode = MODE_PAGE_LOADING;
                repaint();
                serviceRepaints();
                goToLastPage(currentBook.getCurrentChapter().getPrevChapter());
                break;

            case PageDummy.TYPE_CHAPTER_NEXT:
                mode = MODE_PAGE_LOADING;
                repaint();
                serviceRepaints();
                goToFirstPage(currentBook.getCurrentChapter().getNextChapter());
                break;

            case PageDummy.TYPE_BOOK_START:
            case PageDummy.TYPE_BOOK_END:
                mode = MODE_PAGE_SCROLLING;
                scheduleScrolling(bookScrollingDirection);
                break;
        }

        repaint();
        serviceRepaints();
    }

    private void loadPrevPage() {
        currentPageCanvasPosition = 0;

        PageCanvas p = nextPageCanvas;
        nextPageCanvas = currentPageCanvas;
        currentPageCanvas = prevPageCanvas;
        prevPageCanvas = p;

        chapterBooklet.goToPrevPage();
        p.setPage(chapterBooklet.getPrevPage());

        p.renderPage(currentScheme);
        repaintProgressBar = true;
        mode = MODE_PAGE_READING;
    }

    private void loadNextPage() {
        currentPageCanvasPosition = 0;

        PageCanvas p = prevPageCanvas;
        prevPageCanvas = currentPageCanvas;
        currentPageCanvas = nextPageCanvas;
        nextPageCanvas = p;

        chapterBooklet.goToNextPage();
        p.setPage(chapterBooklet.getNextPage());

        p.renderPage(currentScheme);
        repaintProgressBar = true;
        mode = MODE_PAGE_READING;

        p.renderPage(currentScheme);
        repaintProgressBar = true;
        mode = MODE_PAGE_READING;
    }

    private void loadChapter(Chapter chapter) {

        if (chapter != currentBook.getCurrentChapter()
                || chapterBooklet == null) {
            System.out.println("Loading chapter " + chapter.getTitle() + "...");
            /* chapter changed or book not loaded at all */
            currentBook.unloadChaptersBuffers();
            currentBook.setCurrentChapter(chapter);
            int z = currentBook.getChapterNumber(chapter) + 1;
            System.out.println(z);
            updateChapterNum(z);
            reflowPages();
        }
    }

    public final void goToFirstPage(final int chapterNumber) {
        final Chapter c = currentBook.getChapter(chapterNumber);
        goToFirstPage(c);
    }

    private final void goToFirstPage(final Chapter chapter) {
        loadChapter(chapter);
        chapterBooklet.goToFirstPage();
        renderPages();
    }

    public final void goToLastPage(final int chapterNumber) {
        final Chapter c = currentBook.getChapter(chapterNumber);
        goToLastPage(c);
    }

    private final void goToLastPage(final Chapter chapter) {
        loadChapter(chapter);
        chapterBooklet.goToLastPage();
        renderPages();
    }

    public final void goToPosition(final Chapter chapter, final int position) {
        loadChapter(chapter);
        chapterBooklet.goToPosition(position);
        renderPages();
    }

    public final void goToPosition(
            final int chapterNumber, final float percent) {

        final Chapter c = currentBook.getChapter(chapterNumber);
        goToPosition(c, percent);
    }

    private final void goToPosition(final Chapter chapter, final float percent) {
        loadChapter(chapter);

        /*
         * Calculate position, using percent representation
         */
        final float f = (float) (percent - Math.floor(percent));
        final int position =
                (int) (percent * chapterBooklet.getTextBuffer().length);

        chapterBooklet.goToPosition(position);
        renderPages();
    }

    public final void goToSavedPosition(final int chapterNumber) {
        final Chapter c = currentBook.getChapter(chapterNumber);

        if (c != currentBook.getCurrentChapter()) {
            final int pos = c.getCurrentPosition();
            goToPosition(c, pos);
        }
    }

    private void renderPages() {

        currentPageCanvas.setPage(chapterBooklet.getCurrentPage());

        prevPageCanvas.setPage(chapterBooklet.getPrevPage());
        nextPageCanvas.setPage(chapterBooklet.getNextPage());

        prevPageCanvas.renderPage(currentScheme);
        currentPageCanvas.renderPage(currentScheme);
        nextPageCanvas.renderPage(currentScheme);

        currentPageCanvasPosition = 0;

        repaintProgressBar = true;

        mode = MODE_PAGE_READING;

        repaint();
        serviceRepaints();
    }

    private void reflowPages() {
        int mode_ = mode;
        mode = MODE_PAGE_LOADING;
        repaint();
        serviceRepaints();

        chapterBooklet = new Booklet(
                currentPageCanvas.getWidth(),
                currentPageCanvas.getHeight(),
                inverted,
                currentBook.getCurrentChapter(),
                currentBook.getArchive(),
                fontPlain,
                fontItalic,
                hyphenator);

        setupScrolling();

        pagesCount = chapterBooklet.getPagesCount() - 3;
        mode = mode_;
    }

    public void cycleColorSchemes() {
        currentScheme = currentScheme.getOther();
        applyColorProfile();
    }

    public void setScheme(final byte type, final float hue) {

        ColorScheme sc =
                ColorScheme.getScheme(type, currentScheme.isDay(), hue);

        final ColorScheme other = currentScheme.getOther();
        sc.link(other);
        currentScheme = sc;
        applyColorProfile();
    }

    private void applyColorProfile() {

        //apply to buttons
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setColor(
                    currentScheme.colors[ColorScheme.COLOR_MENU]);
        }

        repaintButtons = true;

        //apply to cursor
        waitCursor.setColor(
                currentScheme.colors[ColorScheme.COLOR_CURSOR_WAIT]);

        //apply to status bar
        repaintStatusBar = true;

        //apply to pages
        if (currentPageCanvas != null) {
            renderPages();
        }
    }

    private void loadFont() {
        int currentFontSize = FONT_SIZES[currentFontSizeIndex];
        fontPlain = loadFont("droid-serif_" + currentFontSize);
        fontItalic = loadFont("droid-serif_it_" + currentFontSize);
    }

    private void cycleFontSizes() {
        if (currentFontSizeIndex == 0) {
            fontGrowing = true;
        }

        if (currentFontSizeIndex == FONT_SIZES.length-1) {
            fontGrowing = false;
        }

        if (fontGrowing) {
            currentFontSizeIndex++;
        } else {
            currentFontSizeIndex--;
        }

        loadFont();
        int start = chapterBooklet.getCurrentPage().getStart();
        reflowPages();
        goToPosition(currentBook.getCurrentChapter(), start);
    }

    public void setFontSize(byte fontSizeIndex) {
        if (currentFontSizeIndex > fontSizeIndex) {
            fontGrowing = false;
        } else if (currentFontSizeIndex < fontSizeIndex) {
            fontGrowing = true;
        }

        currentFontSizeIndex = fontSizeIndex;

        loadFont();
        int start = chapterBooklet.getCurrentPage().getStart();
        reflowPages();
        goToPosition(currentBook.getCurrentChapter(), start);
    }

    private void loadStatusFont() {
        fontStatus = loadFont("status");
    }

    private AlbiteFont loadFont(String fontName) {
        AlbiteFont font;
        try {
            font = new AlbiteFont(fontName);
        } catch (IOException ioe) {
            throw new RuntimeException("Couldn't load font.");
        } catch (AlbiteFontException afe) {
            throw new RuntimeException("Couldn't load font.");
        }
        return font;
    }

    public void hideNotify() {
        stopAutomaticSaving();
        stopClock();
    }

    public void showNotify() {
        /* force repaint of menu items */
        repaintButtons = true;
        repaintStatusBar = true;
        startClock();
        startAutomaticSaving();
    }

    private void openRMSAndLoadData() {
        try {
            rs = RecordStore.openRecordStore("bookcanvas", true);

            if (rs.getNumRecords() > 0) {
                /*
                 * Deserialize first record
                 */
                byte[] data = rs.getRecord(1);
                DataInputStream din = new DataInputStream(
                        new ByteArrayInputStream(data));
                try {
                    /*
                     * Loading color schemes
                     */

                    ColorScheme sc1 = ColorScheme.load(din);
                    ColorScheme sc2 = ColorScheme.load(din);

                    sc1.link(sc2);
                    currentScheme = sc1;

                    /*
                     * Loading font size
                     */
                    currentFontSizeIndex = din.readByte();

                    /*
                     * Loading scrolling options
                     */
                    speedMultiplier = din.readFloat();
                    horizontalScrolling = din.readBoolean();

                    /*
                     * Loading screen mode options
                     */
                    orientation = din.readInt();
                    fullscreen = din.readBoolean();

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }

        } catch (RecordStoreException rse) {
            //no saving is possible
            rse.printStackTrace();
        }
    }

    protected final void saveOptionsToRMS() {
        //If bookCanvas has been opened AT ALL
        if (isBookOpen()) {
            try {
                //serialize first record
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                DataOutputStream dout = new DataOutputStream(boas);
                try {
                    /*
                     * Save color schemes
                     */
                    currentScheme.save(dout);
                    currentScheme.getOther().save(dout);

                    /*
                     * Save font size
                     */
                    dout.writeByte(currentFontSizeIndex);

                    /*
                     * Save scrolling options
                     */
                    dout.writeFloat(speedMultiplier);
                    dout.writeBoolean(horizontalScrolling);

                    /*
                     * Save screen mode options
                     */
                    dout.writeInt(orientation);
                    dout.writeBoolean(fullscreen);

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }

                byte[] data = boas.toByteArray();

                if (rs.getNumRecords() > 0) {
                    rs.setRecord(1, data, 0, data.length);
                } else {
                    rs.addRecord(data, 0, data.length);
                }
            } catch (RecordStoreException rse) {
                //no saving is possible
                rse.printStackTrace();
            }
        }
    }

    private void closeRMS() {
        try {
            rs.closeRecordStore();

        } catch (RecordStoreException rse) {
            //no saving is possible
            rse.printStackTrace();
        }
    }

    public final void close() {
        timer.cancel();
        saveOptionsToRMS();
        closeBook();
        closeRMS();
    }

    protected final void updateClock() {
        repaintClock = true;
        repaint();
    }


    public void setScrollingOptions(
            final float speedMultiplier,
            final boolean horizontalScrolling) {

        this.speedMultiplier = speedMultiplier;
        this.horizontalScrolling = horizontalScrolling;

        setupScrolling();
    }

    private void setupScrolling() {

        scrollNextPagePixels = (int)
                (MAXIMUM_SPEED * speedMultiplier * FRAME_TIME);

        /*
         * These values are calculated as a fraction
         * of the normal page scrolling
         */
        scrollSamePagePixels = (int) (scrollNextPagePixels / 8);
        scrollStartBookPixels = (int) (scrollNextPagePixels / 2);

        /*
         * If something by any chane is zero, one wouldn't be
         * able to scroll at all
         */
        if (scrollNextPagePixels == 0) {
            scrollNextPagePixels = 1;
        }

        if (scrollSamePagePixels == 0) {
            scrollSamePagePixels = 1;
        }

        if (scrollStartBookPixels == 0) {
            scrollStartBookPixels = 1;
        }

        /*
         * Set which direction on should scroll around
         */
        if (orientation == ORIENTATION_0 || orientation == ORIENTATION_180) {
            scrollingOnX = horizontalScrolling;
        } else {
            scrollingOnX = !horizontalScrolling;
        }

        /*
         * Do we need to inverted the ordering of pages?
         */
        switch (orientation) {

            case ORIENTATION_0:
                inverted = false;
                break;

            case ORIENTATION_90:
                inverted = (horizontalScrolling ? false : true);
                break;

            case ORIENTATION_180:
                inverted = true;
                break;

            case ORIENTATION_270:
                inverted = (horizontalScrolling ? true : false);
                break;

        }

        doNotSwapWH = orientation ==
                ORIENTATION_0 || orientation == ORIENTATION_180;

        /*
         * Set min/max where prev/next page must be loaded
         */

        if ((scrollingOnX && doNotSwapWH) || (!scrollingOnX && !doNotSwapWH)) {
            pageCanvasPositionMax =
                    currentPageCanvas.getWidth() + MARGIN_WIDTH;
        } else {
            pageCanvasPositionMax =
                    currentPageCanvas.getHeight() + MARGIN_WIDTH;
        }
        pageCanvasPositionMin = -pageCanvasPositionMax;
    }

    public void setOrientation(final int orientation, boolean fullscreen) {

        if (orientation != ORIENTATION_0) {
            /*
             * When not in 0-degree view, always use fullscreen.
             */
            fullscreen = true;
        }

        if (this.orientation != orientation
                || this.fullscreen != fullscreen) {

            mode = MODE_PAGE_LOCKED;
            this.orientation = orientation;
            this.fullscreen = fullscreen;

            final int currentPos = chapterBooklet.getCurrentPage().getStart();
            initializePageCanvases();

            /*
             * Call before calling reflow Pages,
             * as inverted value should be setup before that
             */
            setupScrolling();

            reflowPages();
            goToPosition(currentBook.getCurrentChapter(), currentPos);
            repaintButtons = true;
            repaintStatusBar = true;
            repaint();
            mode = MODE_PAGE_READING;
        }
    }

    private int getXonPage(final int x, final int y) {
        final int w = getWidth();
        final int h = getHeight();

        switch (orientation) {
            case ORIENTATION_0:
                return x - MARGIN_WIDTH;

            case ORIENTATION_180:
                return w - x - MARGIN_WIDTH;

            case ORIENTATION_90:
                return y - MARGIN_WIDTH;

            case ORIENTATION_270:
                return h - y - MARGIN_WIDTH;
        }

        return x;
    }

    private int getYonPage(final int x, final int y) {
        final int w = getWidth();
        final int h = getHeight();

        switch (orientation) {
            case ORIENTATION_0:
                if (fullscreen) {
                    return y - MARGIN_WIDTH;
                } else {
                    return y - MENU_HEIGHT;
                }

            case ORIENTATION_180:
                return h - y - MARGIN_WIDTH;

            case ORIENTATION_90:
                return w - x - MARGIN_WIDTH;

            case ORIENTATION_270:
                return x - MARGIN_WIDTH;
        }

        return y;
    }

    private void updateChapterNum(final int chapterNo) {

        final char[] chapterNoCharsF = chapterNoChars;

        final int currentChapterNo = chapterNo;

        int i = 1;
        if (currentChapterNo > 99) {
            chapterNoCharsF[i] = (char) ('0' + (currentChapterNo / 100));
            i++;
        }
        if (currentChapterNo > 9) {
            chapterNoCharsF[i] = (char) ('0' + ((currentChapterNo % 100) / 10));
            i++;
        }

        chapterNoCharsF[i] = (char) ('0' + ((currentChapterNo % 100) % 10));
        i++;

        for (; i < chapterNoCharsF.length; i++) {
            chapterNoCharsF[i] = ' ';
        }

        chapterNoChars = chapterNoCharsF;
        repaintChapterNum = true;
    }

    public void fillBookInfo(final Form f) {
        currentBook.fillBookInfo(f);
    }

    public boolean getHorizontalScalling() {
        return horizontalScrolling;
    }

    public int getScrollingSpeed() {
        return (int) (speedMultiplier * 100);
    }
}