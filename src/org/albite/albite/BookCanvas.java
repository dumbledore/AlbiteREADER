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
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import org.albite.book.model.book.Book;
import org.albite.book.model.book.BookException;
import org.albite.book.model.book.Bookmark;
import org.albite.book.model.book.Chapter;
import org.albite.book.view.Booklet;
import org.albite.book.view.Page;
import org.albite.font.AlbiteFont;
import org.albite.book.view.DummyPage;
import org.albite.book.view.TextPage;
import org.albite.book.view.Region;
import org.albite.font.AlbiteFontException;
import org.geometerplus.zlibrary.text.hyphenation.ZLTextTeXHyphenator;

/**
 *
 * @author Albus Dumbledore
 */
public class BookCanvas extends Canvas {

    private static final int    TASK_NONE               = 0;
    private static final int    TASK_MENU               = 1;
    private static final int    TASK_LIBRARY            = 2;
    private static final int    TASK_DICTIONARY         = 3;
    private static final int    TASK_FONTSIZE           = 4;
    private static final int    TASK_COLORSCHEME        = 5;
    
    private static final int    MENU_HEIGHT             = 45;
    private static final int    STATUS_BAR_SPACING      = 3;

    /*
     * Some page settings
     */

    private              int    currentMarginWidth;
    private static final int    LINE_SPACING            = 2;
    private              int    currentLineSpacing      = LINE_SPACING;
    private boolean             renderImages;

    private static final int    DRAG_TRESHOLD           = 40;
    private static final int    MARGIN_CLICK_TRESHOLD   = 60;
    private static final int    HOLDING_TIME_MIN        = 250;
    private int                 currentHoldingTime      = HOLDING_TIME_MIN * 3;
    private long                startPointerHoldingTime;

    /*
     * Targeting at 60 FPS
     */
    private final int           frameTime;

    private static final float  MAXIMUM_SPEED           = 4F;

    private float               speedMultiplier         = 0.3F;
    private boolean             scrollingOnX            = true;

    private int                 scrollNextPagePixels    = 55;
    private int                 scrollSamePagePixels    = 5;
    private int                 scrollStartBookPixels   = 30;
    private boolean             smoothScrolling;
    private boolean             horizontalScrolling     = true;

    /**
     * If true, the pages will be in reversed order
     */
    private boolean             inverted                = false;

    private int                 pageCanvasPositionMin   = 0;
    private int                 pageCanvasPositionMax   = 0;

    private static final int    AUTOSAVE_TIME           = 5 * 60 * 1000;

    /**
     * Rendering is disabled
     */
    private static final int    MODE_DONT_RENDER        = 0;

    /**
     * Rendering is enabled, but user input is not being processed
     */
    private static final int    MODE_PAGE_LOCKED        = 1;

    /**
     * Same as MODE_PAGE_LOCKED, but displays a hour-glass icon on top
     */
    private static final int    MODE_PAGE_LOADING       = 2;

    /**
     * Rendering is enabled, ready to process user input
     */
    private static final int    MODE_PAGE_READING       = 3;

    /**
     * Rendering is enabled, user input is not processed,
     * scrolling animation is in progress
     */
    private static final int    MODE_PAGE_SCROLLING     = 4;

    /**
     * Rendering is enabled, only pointer dragging is being processed
     */
    private static final int    MODE_PAGE_DRAGGING      = 5;

    /**
     * Rendering is enabled, and a button has just been pressed
     */
    private static final int    MODE_BUTTON_PRESSING    = 6;

    /**
     * Rendering is enabled, and text is being selected
     */
    private static final int    MODE_TEXT_SELECTING     = 7;

    private int                 mode                    = MODE_DONT_RENDER;
    private final boolean       lightMode;

    /*
     * 180-degree rotation will not be supported as it introduces code
     * complexity, that is not quite necessary
     */
    public static final int     ORIENTATION_0           = Sprite.TRANS_NONE;
    public static final int     ORIENTATION_90          = Sprite.TRANS_ROT90;
    public static final int     ORIENTATION_180         = Sprite.TRANS_ROT180;
    public static final int     ORIENTATION_270         = Sprite.TRANS_ROT270;

    private int                 orientation             = ORIENTATION_0;
    private boolean             fullscreen;

    public static final int     SCROLL_PREV             = 0;
    public static final int     SCROLL_NEXT             = 1;
    public static final int     SCROLL_SAME_PREV        = 2;
    public static final int     SCROLL_SAME_NEXT        = 3;
    public static final int     SCROLL_BOOK_START       = 4;
    public static final int     SCROLL_BOOK_END         = 5;

    private volatile boolean    repaintButtons          = true;

    private volatile boolean    repaintStatusBar        = true;

    private volatile boolean    repaintChapterNum       = false;
    private volatile boolean    repaintProgressBar      = false;
    private volatile boolean    repaintClock            = false;
    
    private char[]              chapterNoChars          = {'#', '0', '0', '0'};
    private int                 pagesCount;
    private final char[]        clockChars = {'0', '0', ':', '0', '0'};

    private int                 statusBarHeight;
    private int                 chapterNoWidth;
    private int                 progressBarWidth;
    private int                 progressBarHeight;
    private int                 progressBarX;
    private int                 clockWidth;

    private int                 centerBoxSide;
    
    private ImageButton[]       buttons;
    private ImageButton         waitCursor;

    //input events
    private int                 xx                      = 0;
    private int                 yy                      = 0;
    private int                 xxpressed               = 0;
    private int                 yypressed               = 0;
    private ImageButton         buttonPressed           = null;

    private int                 regionSelectedFirst     = -1;
    private int                 regionSelectedLast      = -1;
    
    private ColorScheme         currentScheme;

    private AlbiteFont          fontPlain;
    private AlbiteFont          fontItalic;

    public final byte[]         fontSizes;

    private boolean             fontGrowing             = true;
    private byte                currentFontSizeIndex;

    private AlbiteFont          fontStatus;

    private Book                currentBook;

    private ZLTextTeXHyphenator hyphenator;

    private Booklet             chapterBooklet;
    private PageCanvas          prevPageCanvas;
    private PageCanvas          currentPageCanvas;
    private PageCanvas          nextPageCanvas;
    private int                 currentPageCanvasPosition;

    private Timer               timer;
    private TimerTask           scrollingTimerTask;
    private TimerTask           savingTimerTask;
    private TimerTask           clockTimerTask;
    private TimerTask           keysTimerTask;
    private TimerTask           pointerPressedTimerTask;
    private TimerTask           pointerReleasedTimerTask;
    private boolean             pointerPressedReady     = true;
    private boolean             pointerReleasedReady    = true;
    private boolean             keysReady               = true;

    private AlbiteMIDlet        app;

    private RecordStore         rs;

    private boolean             initialized             = false;

    public BookCanvas(final AlbiteMIDlet app) {
        this.app = app;

        /*
         * Initialize default values, that depend on whether
         * app is in light mode
         */
        lightMode = app.lightMode();

        currentMarginWidth = (lightMode ? 5 : 10);

        renderImages = (lightMode ? false : true);

        frameTime = 1000 / (lightMode ? 30 : 60);

        fontSizes = (lightMode
                ? new byte[] {12}
                : new byte[] {12, 14, 16, 18});

        currentFontSizeIndex = (lightMode ? (byte) 0 : (byte) 2);

        fullscreen = (lightMode ? true : false);

        smoothScrolling = (lightMode ? false : true);

        /*
         * Load custom data from RMS
         */
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

        waitCursor = new ImageButton("/res/gfx/hourglass.ali", TASK_NONE);

        /* set default profiles if none selected */
        if (currentScheme == null) {
            ColorScheme day = ColorScheme.DEFAULT_DAY;
            ColorScheme night = ColorScheme.DEFAULT_NIGHT;
            day.link(night);
            currentScheme = day;
        }

        /*
         * Load menu images
         * Images cannot be stored normally (i.e. as Image objects) as they need
         * to be mutable: one should be able to select the color of the image
         * without affecting the alpha channel
         */
        loadButtons();

        applyColorProfile();

        initializePageCanvases();

        /*
         * Update the inverted mode and the max scrolling values
         * Can't be done at any place before, for the canvases must have been
         * already initialized
         */
        setupScrolling();

        timer = new Timer();

        initialized = true;
    }

    private void initializePageCanvases() {
        final int w;
        final int h;

        if (!fullscreen) {
            w = getWidth() - (2 * currentMarginWidth);
            h = getHeight() - MENU_HEIGHT - statusBarHeight;
        } else {
            w = getWidth() - (2 * currentMarginWidth);
            h = getHeight() - ( 2 * currentMarginWidth);
        }

        /*
         * Free memory before claiming it!
         */

        currentPageCanvas = null;
        nextPageCanvas = null;
        prevPageCanvas = null;

        currentPageCanvas   = new PageCanvas(w, h, orientation);
        nextPageCanvas      = new PageCanvas(w, h, orientation);
        prevPageCanvas      = new PageCanvas(w, h, orientation);

        currentPageCanvasPosition = 0;
    }

    protected final void paint(final Graphics g) {
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

            final int anchor = Graphics.TOP | Graphics.LEFT;

            final Image imageC = currentPageCanvas.getImage();
            final Image imageP = prevPageCanvas.getImage();
            final Image imageN = nextPageCanvas.getImage();

            final int imageWidth = imageC.getWidth();
            final int imageHeight = imageC.getHeight();

            int x = 0;
            int y = 0;

            switch(orientation) {
                case ORIENTATION_0:

                    if (!fullscreen) {
                        g.setClip(0, MENU_HEIGHT, w, imageHeight);
                        x = (scrollingOnX
                                ? currentMarginWidth
                                    + currentPageCanvasPosition
                                : currentMarginWidth);

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
                            ? currentMarginWidth + currentPageCanvasPosition
                            : currentMarginWidth);

                    y = (scrollingOnX
                            ? currentMarginWidth
                            : currentMarginWidth + currentPageCanvasPosition);
                    break;
            }

            g.setColor(
                    currentScheme.colors[
                    ColorScheme.COLOR_BACKGROUND]);
            g.fillRect(0, 0, w, h);
            g.drawImage(imageP,
                    (scrollingOnX ? x - imageWidth - currentMarginWidth : x),
                    (scrollingOnX ? y : y - imageHeight - currentMarginWidth),
                    anchor);
            g.drawImage(imageC, x, y, anchor);

            g.drawImage(imageN,
                    (scrollingOnX ? x + imageWidth + currentMarginWidth : x),
                    (scrollingOnX ? y : y + imageHeight + currentMarginWidth),
                    anchor);

            if (mode == MODE_PAGE_LOADING) {
                waitCursor.drawRotated(
                        g,
                        (w - waitCursor.getWidth()) / 2,
                        (h - waitCursor.getHeight()) / 2,
                        orientation);
            }
        }
    }

    private void drawButtons(final int w, final int h, final Graphics g) {
        g.setColor(currentScheme.colors[ColorScheme.COLOR_BACKGROUND]);
        g.fillRect(0, 0, w, MENU_HEIGHT);

        if (buttons != null) {
            if (buttons.length > 0) {
                for (int i = 0; i < buttons.length; i++) {
                    buttons[i].draw(g, buttons[i].getX(), buttons[i].getY());
                }
                repaintButtons = false;
            }
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
        g.setColor(currentScheme.colors[ColorScheme.COLOR_BACKGROUND]);

        g.fillRect(progressBarX, h - statusBarHeight,
                progressBarWidth, statusBarHeight);

        /* drawing progress bar */
        g.setColor(currentScheme.colors[ColorScheme.COLOR_TEXT_STATUS]);

        g.drawRect(
                progressBarX, h - ((statusBarHeight + progressBarHeight) / 2),
                progressBarWidth - 1, progressBarHeight);

        final int barFilledWidth;

        if (pagesCount > 0) {
            barFilledWidth =
                    (int) (progressBarWidth
                    * (((float) chapterBooklet.getCurrentPageIndex() - 1)
                    / pagesCount));
        } else {
            /*
             * This should never happen, how could there be 0 pages?!
             */
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
        if (buttons != null) {
            for (int i = 0; i < buttons.length; i++) {
                if (buttons[i].buttonPressed(x, y)) {
                    return buttons[i];
                }
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

    protected final void keyRepeated(final int k) {
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
        xxpressed = xx = x;
        yypressed = yy = y;

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

    private void processPointerReleased(final int x, final int y) {

        xx = x;
        yy = y;

        final int w = getWidth();
        final int h = getHeight();

        if (mode == MODE_TEXT_SELECTING) {
            if (regionSelectedFirst != -1 && regionSelectedLast != -1) {

                final Region r =
                        chapterBooklet.getCurrentPage().getRegionForIndex(
                        regionSelectedFirst);

                final int start = (
                        r != null
                        ? r.getPosition()
                        : chapterBooklet.getCurrentPage().getStart());

                final String text =
                        chapterBooklet.getCurrentPage().getTextForBookmark(
                        chapterBooklet.getTextBuffer(),
                        regionSelectedFirst, regionSelectedLast);

                app.calledOutside();
                app.setCurrentBookmarkOptions(start, text);
                app.addBookmarkAutomatically();
            }

            regionSelectedFirst = -1;
            regionSelectedLast = -1;
            mode = MODE_PAGE_READING;
            currentPageCanvas.renderPage(currentScheme);
            repaint();
            return;
        }

        boolean holding =
            (System.currentTimeMillis() - startPointerHoldingTime >
            currentHoldingTime);

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
                         * Srcoll directly
                         */
                        goToPosition(
                                currentBook.getCurrentChapter(),
                                ((float) (x - progressBarX))
                                / ((float)progressBarWidth));
                    } else {

                        /*
                         * Show toc
                         */
                        app.calledOutside();
                        app.showToc();
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
                        /*
                         * Get the text
                         */
                        final String text =
                                r.getText(chapterBooklet.getTextBuffer());

                        if (text != null) {
                            app.calledOutside();
                            app.setEntryForLookup(text);
                            app.lookupWordOrNumber();
                        }
                    }
                }

            break;

            case MODE_PAGE_DRAGGING:
//            case MODE_PAGE_SCROLLING:
                final int px = currentPageCanvasPosition;

                if (px == 0) {
                    stopScrolling();
                    mode = MODE_PAGE_READING;
                    break;
                }

                mode = MODE_PAGE_SCROLLING;

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
                                if (holding || lightMode) {
                                    /* show unit converter */
                                    app.enterNumber();
                                } else {
                                    /* show dictionary */
                                    app.enterWord();
                                }
                                break;

                            case TASK_MENU:
                                if (holding) {
                                    /*
                                     * Exit midlet, if user holds
                                     * over the menu button
                                     */
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
        if (mode == MODE_PAGE_READING) {

            boolean holding =
                    (System.currentTimeMillis() - startPointerHoldingTime >
                        currentHoldingTime);

            if (holding) {
                /*
                 * Multiple selection mode
                 */
                mode = MODE_TEXT_SELECTING;

                final int xonpage = getXonPage(xxpressed, yypressed);
                final int yonpage = getYonPage(xxpressed, yypressed);
                
                int first = chapterBooklet.getCurrentPage().getRegionIndexAt(
                        xonpage, yonpage);

                regionSelectedFirst = first;
            }
        }

        if (mode == MODE_TEXT_SELECTING && regionSelectedFirst != -1) {
            final int xonpage = getXonPage(x, y);
            final int yonpage = getYonPage(x, y);

            final int last = chapterBooklet.getCurrentPage().getRegionIndexAt(
                    xonpage, yonpage);

            if (last != -1) {
                regionSelectedLast = last;
                currentPageCanvas.renderPage(currentScheme);
                currentPageCanvas.renderPageSelected(
                        currentScheme, regionSelectedFirst, regionSelectedLast);
                repaint();
            }

            return;
        }

        switch(mode) {
//            case MODE_PAGE_SCROLLING:
            case MODE_PAGE_READING:
                mode = MODE_PAGE_DRAGGING;
                /* FALLING THROUGH */
                
            case MODE_PAGE_DRAGGING:
//                mode = MODE_PAGE_SCROLLING;
//                stopScrolling();
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

    public final boolean isBookOpen(final String bookURL) {

        if (isBookOpen() && currentBook.getURL().equalsIgnoreCase(bookURL)) {
            return true;
        }
        return false;
    }

    public final Book openBook(final String bookURL)
            throws IOException, BookException {

        /*
         * If the book is already open, no need to load it again
         */
        if (isBookOpen(bookURL)) {
            mode = MODE_PAGE_READING;
            return currentBook;
        }

        /*
         * try to open the book
         */
        Book newBook = null;

        newBook = Book.open(bookURL, app.lightMode());

        /*
         * All was OK, let's close current book
         */
        closeBook();

        currentBook = newBook;

        /*
         * load hyphenator according to book language
         */
        loadHyphenator(currentBook.getLanguage());

        /*
         * Reset the Toc
         */
        app.resetToc();

        /*
         * Populate the Toc in app
         */
        final List toc = app.getToc();

        final int count = currentBook.getChaptersCount();
        for (int i = 0; i < count; i++) {
            toc.append(currentBook.getChapter(i).getTitle(), null);
        }

        /*
         * Go to position and effectively reflow chapter
         */
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
            try {
                currentBook.close();
            } catch (IOException e) {}
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

            timer.schedule(scrollingTimerTask, frameTime, frameTime);
        }
    }

    private synchronized void stopScrolling() {
        if (scrollingTimerTask != null) {
            scrollingTimerTask.cancel();
            scrollingTimerTask = null;
        }
    }

    private int nonLineaScroll(final int dx) {
        final float part;

        if (dx < 0) {
            part = ((float) (-currentPageCanvasPosition)) / ((float) pageCanvasPositionMax);
        } else {
            part = ((float) currentPageCanvasPosition) / ((float) pageCanvasPositionMax);
        }

        /*
         * Calculate the speed
         */
        int dxNew = dx;

        if (part > 0.3) {
            /*
             * Activate non-linear mode
             */
            if (dxNew < 0) {
                dxNew = -dxNew;
            }

            dxNew = (int) (((-1.8 * part) + 1.9) * dxNew);

            if (dxNew == 0) {
                dxNew = 1;
            }
            
            if (dx < 0) {
                dxNew = -dxNew;
            }
        }
        
        return dxNew;
    }

    /**
     * Scrolls the three PageCanvases across the screen.
     *
     * @param dx Relative amount to scroll
     * @param fullPage If true, the tree scroll to the next/previous page.
     * If false, scrolls back to the current page
     *
     */
    protected final void scrollPages(int dx, final boolean fullPage) {

        if (smoothScrolling) {
            dx = nonLineaScroll(dx);
        }
        
        currentPageCanvasPosition += dx;

        if (fullPage) {

                if (currentPageCanvasPosition >= pageCanvasPositionMax) {

                /*
                 * loading prev page
                 */
                currentPageCanvasPosition = pageCanvasPositionMax;
                mode = MODE_PAGE_LOCKED;

                final Page page = chapterBooklet.getPrevPage();

                if (page instanceof DummyPage) {
                    DummyPage pd = (DummyPage)page;
                    handleDummyPage(pd.getType(), SCROLL_BOOK_START);
                }

                if (page instanceof TextPage) {
                    stopScrolling();
                    loadPrevPage();
                    return;
                }
            }

            if (currentPageCanvasPosition <= pageCanvasPositionMin) {
                
                /*
                 * loading next page
                 */
                currentPageCanvasPosition = pageCanvasPositionMin;
                mode = MODE_PAGE_LOCKED;

                final Page page = chapterBooklet.getNextPage();

                if (page instanceof DummyPage) {
                    DummyPage pd = (DummyPage)page;
                    handleDummyPage(pd.getType(), SCROLL_BOOK_END);
                }

                if (page instanceof TextPage) {
                    stopScrolling();
                    loadNextPage();
                    return;
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

    private void handleDummyPage(
            final byte type, final int bookScrollingDirection) {

        stopScrolling();

        switch (type) {
            case DummyPage.TYPE_CHAPTER_PREV:
                renderWaitCursor();
                goToLastPage(currentBook.getCurrentChapter().getPrevChapter());
                break;

            case DummyPage.TYPE_CHAPTER_NEXT:
                renderWaitCursor();
                goToFirstPage(currentBook.getCurrentChapter().getNextChapter());
                break;

            case DummyPage.TYPE_BOOK_START:
            case DummyPage.TYPE_BOOK_END:
                mode = MODE_PAGE_SCROLLING;
                scheduleScrolling(bookScrollingDirection);
                break;
        }
//        repaint();
//        serviceRepaints();
    }

    private void loadPrevPage() {
        chapterBooklet.goToPrevPage();

        Page prev = chapterBooklet.getPrevPage();

        if (prev instanceof TextPage
                && (((TextPage) prev).hasImage())) {
            mode = MODE_PAGE_LOADING;
        }
        
        repaint();
        serviceRepaints();

        currentPageCanvasPosition = 0;

        PageCanvas p = nextPageCanvas;
        nextPageCanvas = currentPageCanvas;
        currentPageCanvas = prevPageCanvas;
        prevPageCanvas = p;

        p.setPage(prev);
        p.renderPage(currentScheme);

        repaintProgressBar = true;
        mode = MODE_PAGE_READING;

        repaint();
        serviceRepaints();
    }

    private void loadNextPage() {
        chapterBooklet.goToNextPage();

        Page next = chapterBooklet.getNextPage();

        if (next instanceof TextPage
                && (((TextPage) next).hasImage())) {
            mode = MODE_PAGE_LOADING;
        }

        repaint();
        serviceRepaints();
        
        currentPageCanvasPosition = 0;

        PageCanvas p = prevPageCanvas;
        prevPageCanvas = currentPageCanvas;
        currentPageCanvas = nextPageCanvas;
        nextPageCanvas = p;


        p.setPage(next);

        p.renderPage(currentScheme);
        
        repaintProgressBar = true;
        mode = MODE_PAGE_READING;

        repaint();
        serviceRepaints();
    }

    private void loadChapter(final Chapter chapter) {

        if (chapter != currentBook.getCurrentChapter()
                || chapterBooklet == null) {

            /* chapter changed or book not loaded at all */
            currentBook.unloadChaptersBuffers();
            currentBook.setCurrentChapter(chapter);
            updateChapterNum(chapter.getNumber() + 1);
            renderWaitCursor();
            reflowPages();
            mode = MODE_PAGE_READING;
        }
    }

    public final void goToFirstPage(final int chapterNumber) {
        final Chapter c = currentBook.getChapter(chapterNumber);
        goToFirstPage(c);
    }

    private void goToFirstPage(final Chapter chapter) {
        loadChapter(chapter);
        chapterBooklet.goToFirstPage();
        renderPages();
    }

    public final void goToLastPage(final int chapterNumber) {
        final Chapter c = currentBook.getChapter(chapterNumber);
        goToLastPage(c);
    }

    private void goToLastPage(final Chapter chapter) {
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

    private void goToPosition(
            final Chapter chapter, final float percent) {

        loadChapter(chapter);

        /*
         * Calculate position, using percent representation
         */
        final float f = (float) (percent - Math.floor(percent));
//        final int position =
//                (int) (percent * chapterBooklet.getTextBuffer().length);
//
//        chapterBooklet.goToPosition(position);
        final int page =
                (int) (percent * chapterBooklet.getPagesCount());
        chapterBooklet.goToPage(page);
        renderPages();
    }

    public final void goToPosition(final Bookmark bookmark) {
        if (bookmark != null) {
            goToPosition(bookmark.getChapter(), bookmark.getPosition());
        }
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

    private void renderWaitCursor() {
        mode = MODE_PAGE_LOADING;
        repaint();
        serviceRepaints();
    }

    private void reflowPages() {
        /*
         * Free memory before claiming it!
         */
        chapterBooklet = null;

        chapterBooklet = new Booklet(
                currentPageCanvas.getPageWidth(),
                currentPageCanvas.getPageHeight(),
                inverted,
                currentBook.getCurrentChapter(),
                currentBook.getArchive(),
                fontPlain,
                fontItalic,
                currentLineSpacing,
                renderImages,
                hyphenator,
                currentBook.getParser());

        setupScrolling();

        pagesCount = chapterBooklet.getPagesCount() - 3;
    }

    public final void cycleColorSchemes() {
        currentScheme = currentScheme.getOther();
        applyColorProfile();
    }

    public final void setScheme(final byte type, final float hue) {

        ColorScheme sc =
                ColorScheme.getScheme(type, currentScheme.isDay(), hue);

        final ColorScheme other = currentScheme.getOther();
        sc.link(other);
        currentScheme = sc;
        applyColorProfile();
    }

    private void colorizeButtons() {

        if (buttons != null) {
            for (int i = 0; i < buttons.length; i++) {
                buttons[i].setColor(
                        currentScheme.colors[ColorScheme.COLOR_MENU]);
            }

            repaintButtons = true;
        }
    }

    private void applyColorProfile() {

        /*
         * apply to buttons
         */
        colorizeButtons();

        /*
         * apply to cursor
         */
        waitCursor.setColor(
                currentScheme.colors[ColorScheme.COLOR_CURSOR_WAIT]);

        /*
         * apply to status bar
         */
        repaintStatusBar = true;

        /*
         * apply to pages
         */
        if (currentPageCanvas != null) {
            renderPages();
        }
    }

    private void loadFont() {
        int currentFontSize = fontSizes[currentFontSizeIndex];
        fontPlain = loadFont("droid-serif_" + currentFontSize);
        fontItalic = loadFont("droid-serif_it_" + currentFontSize);
    }

    private void cycleFontSizes() {
        if (fontSizes.length > 1) {
            if (currentFontSizeIndex == 0) {
                fontGrowing = true;
            }

            if (currentFontSizeIndex == fontSizes.length-1) {
                fontGrowing = false;
            }

            if (fontGrowing) {
                currentFontSizeIndex++;
            } else {
                currentFontSizeIndex--;
            }

            loadFont();

            /*
             * Reflow the chapter
             */
            reflowChapter();
        }
    }

    public final void setFontSize(final byte fontSizeIndex) {

        if (fontSizes.length > 1) {
            if (currentFontSizeIndex > fontSizeIndex) {
                fontGrowing = false;
            } else if (currentFontSizeIndex < fontSizeIndex) {
                fontGrowing = true;
            }

            currentFontSizeIndex = fontSizeIndex;

            loadFont();

            /*
             * Reflow the chapter
             */
            reflowChapter();
        }
    }

    private void loadStatusFont() {
        fontStatus = loadFont("status");
    }

    private AlbiteFont loadFont(final String fontName) {

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

    public final void hideNotify() {
        stopAutomaticSaving();
        stopClock();
    }

    public final void showNotify() {
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
                    smoothScrolling = din.readBoolean();
                    horizontalScrolling = din.readBoolean();
                    currentHoldingTime = din.readInt();

                    /*
                     * Loading screen mode options
                     */
                    orientation = din.readInt();
                    fullscreen = din.readBoolean();

                    /*
                     * Write page options
                     */
                    currentMarginWidth = din.readInt();
                    currentLineSpacing = din.readInt();
                    renderImages = din.readBoolean();

                } catch (IOException ioe) {}
            }

        } catch (RecordStoreException rse) {}
    }

    protected final void saveOptionsToRMS() {

        /*
         * Has the bookcanvas been opened AT ALL?
         */
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
                    dout.writeBoolean(smoothScrolling);
                    dout.writeBoolean(horizontalScrolling);
                    dout.writeInt(currentHoldingTime);

                    /*
                     * Save screen mode options
                     */
                    dout.writeInt(orientation);
                    dout.writeBoolean(fullscreen);

                    /*
                     * Write page options
                     */
                    dout.writeInt(currentMarginWidth);
                    dout.writeInt(currentLineSpacing);
                    dout.writeBoolean(renderImages);

                } catch (IOException ioe) {}

                byte[] data = boas.toByteArray();

                if (rs.getNumRecords() > 0) {
                    rs.setRecord(1, data, 0, data.length);
                } else {
                    rs.addRecord(data, 0, data.length);
                }
            } catch (RecordStoreException rse) {}
        }
    }

    private void closeRMS() {
        try {
            rs.closeRecordStore();
        } catch (RecordStoreException rse) {}
    }

    public final void close() {
        timer.cancel();
        saveOptionsToRMS();
        closeBook();
        closeRMS();
    }

    private void updateClock() {
        repaintClock = true;
        repaint();
    }

    public final void setScrollingOptions(
            final float speedMultiplier,
            final boolean smoothScrolling,
            final boolean horizontalScrolling) {

        this.speedMultiplier = speedMultiplier;
        this.smoothScrolling = smoothScrolling;
        this.horizontalScrolling = horizontalScrolling;

        setupScrolling();
    }

    private void setupScrolling() {

        scrollNextPagePixels = (int)
                (MAXIMUM_SPEED * speedMultiplier * frameTime);

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

        /*
         * Set min/max where prev/next page must be loaded
         */
        if (scrollingOnX) {
            pageCanvasPositionMax =
                    currentPageCanvas.getWidth() + currentMarginWidth;
        } else {
            pageCanvasPositionMax =
                    currentPageCanvas.getHeight() + currentMarginWidth;
        }

        pageCanvasPositionMin = -pageCanvasPositionMax;
    }

    public final void setOrientation(
            final int orientation, boolean fullscreen) {

        if (orientation != ORIENTATION_0) {
            /*
             * When not in 0-degree view, always use fullscreen.
             */
            fullscreen = true;
        }

        if (this.orientation != orientation
                || this.fullscreen != fullscreen) {

            renderWaitCursor();
            this.orientation = orientation;
            this.fullscreen = fullscreen;
            loadButtons();
            reloadPages();
        }
    }

    private void loadButtons() {

        if (orientation == ORIENTATION_0 && fullscreen == false) {

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

            colorizeButtons();
        } else {
            buttons = null;
        }
    }

    private void reloadPages() {
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
        mode = MODE_PAGE_READING;
        repaint();
        serviceRepaints();
    }

    private int getXonPage(final int x, final int y) {
        final int w = getWidth();
        final int h = getHeight();

        switch (orientation) {
            case ORIENTATION_0:
                return x - currentMarginWidth;

            case ORIENTATION_180:
                return w - x - currentMarginWidth;

            case ORIENTATION_90:
                return y - currentMarginWidth;

            case ORIENTATION_270:
                return h - y - currentMarginWidth;
        }

        return x;
    }

    private int getYonPage(final int x, final int y) {
        final int w = getWidth();
        final int h = getHeight();

        switch (orientation) {
            case ORIENTATION_0:
                if (fullscreen) {
                    return y - currentMarginWidth;
                } else {
                    return y - MENU_HEIGHT;
                }

            case ORIENTATION_180:
                return h - y - currentMarginWidth;

            case ORIENTATION_90:
                return w - x - currentMarginWidth;

            case ORIENTATION_270:
                return x - currentMarginWidth;
        }

        return y;
    }

    private void updateChapterNum(final int chapterNo) {

        /*
         * update chapter's number
         */
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

    public final void fillBookInfo(final Form f) {
        currentBook.fillBookInfo(f);
    }

    public final boolean getHorizontalScalling() {
        return horizontalScrolling;
    }

    public final boolean getSmoothScrolling() {
        return smoothScrolling;
    }

    public final int getScrollingSpeed() {
        return (int) (speedMultiplier * 100);
    }

    public final void updatePageSettings(
            final int margin,
            final int lineSpacing,
            final boolean images) {

        if (margin == currentMarginWidth
                && lineSpacing == currentLineSpacing
                && images == renderImages) {

            /*
             * Nothing has changed
             */
            return;
        }

        this.currentMarginWidth = margin;
        this.currentLineSpacing = lineSpacing;
        this.renderImages = images;

        renderWaitCursor();
        reloadPages();
    }

    public final void setupNewBookmark() {
        final Page p =
                chapterBooklet.getCurrentPage();

        app.setCurrentBookmarkOptions(
                p.getStart(),
                p.getTextForBookmark(chapterBooklet.getTextBuffer()));
    }

    public final int getCurrentMargin() {
        return currentMarginWidth;
    }

    public final int getCurrentLineSpacing() {
        return currentLineSpacing;
    }

    public final boolean rendersImages() {
        return renderImages;
    }

    public final Book getCurrentBook() {
        return currentBook;
    }

    public final void setHoldingTimeByMultiplier(final int multiplier) {
        currentHoldingTime = HOLDING_TIME_MIN * (multiplier + 1);
    }

    public final int getHoldingTimeMultiplier() {
        return currentHoldingTime / HOLDING_TIME_MIN;
    }

    public final int getFontSizeIndex() {
        return currentFontSizeIndex;
    }

    /*
     * This one is pretty a bit dirty, but there's no time for it.
     */
    public final int getScreenMode() {

        switch (orientation) {
            case ORIENTATION_0:
                if (!fullscreen) {
                    return 0;
                } else {
                    return 1;
                }

            case ORIENTATION_90:
                return 2;

            case ORIENTATION_180:
                return 3;

            case ORIENTATION_270:
                return 4;

            default:
                return 0;
        }
    }

    private void reflowChapter() {
        int start = chapterBooklet.getCurrentPage().getStart();
        renderWaitCursor();
        reflowPages();
        goToPosition(currentBook.getCurrentChapter(), start);
        mode = MODE_PAGE_READING;
    }

    public final void setBookLanguage(final String language) {
        if (currentBook.setLanguage(language)) {
            /*
             * Reload the hyphenator
             */
            loadHyphenator(language);

            /*
             * Reflow the chapter
             */
            reflowChapter();
        }
    }

    private void loadHyphenator(final String language) {

        if (language == null) {
            hyphenator = null;
        }
        
        final String currentLanguage =
                (hyphenator != null ? hyphenator.getLanguage() : null);

        if ((hyphenator != null && currentLanguage == null)
                || language == null
                || language.equalsIgnoreCase(currentLanguage)) {
            return;
        }

        try {
            hyphenator = new ZLTextTeXHyphenator(language);
        } catch (IOException e) {
            hyphenator = null;
        }
    }

    public final void setAutoBookLanguage() {
        setBookLanguage(currentBook.getDefaultLanguage());
    }

    public final void setChapterEncoding(final String encoding) {

        final Chapter chapter = currentBook.getCurrentChapter();

        if (chapter.setEncoding(encoding)) {
            /*
             * reflow the chapter
             */
            reflowChapter();
        }
    }

    public final void setAutoChapterEncoding() {
        setChapterEncoding(Chapter.AUTO_ENCODING);
    }
}