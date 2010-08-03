/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.albite;

import java.io.IOException;
import java.util.Calendar;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import org.albite.book.book.Book;
import org.albite.book.book.BookChapter;
import org.albite.font.BitmapFont;
import org.albite.book.elements.Page;
import org.albite.book.elements.PageView;
import org.albite.book.elements.Region;

/**
 *
 * @author Albus Dumbledore
 */
public class BookCanvas extends Canvas {
    private static AlbiteMIDlet app;
    private static Book currentBook;

          public static int STATUS_BAR_HEIGHT;      //set in the constructor as depends on font actual size (which may differ across devices
    final public static int MENU_HEIGHT             = 45;
    final public static int MARGIN_WIDTH            = 10;

    final public static int DRAG_TRESHOLD           = 40;
    final public static int MARGIN_CLICK_TRESHOLD   = 60;   //better be enough
    final public static long HOLD_TIME              = 800; //in millis
    protected long startHoldingTime;

    final public static int MODE_PAGE_LOCKED        = 0;
    final public static int MODE_PAGE_LOADING       = 1;
    final public static int MODE_PAGE_READING       = 2;
    final public static int MODE_PAGE_DRAGGING      = 3;
    final public static int MODE_WORD_SELECTED      = 4;
    final public static int MODE_MARKING_WORDS      = 5;
    final public static int MODE_BUTTON_PRESSING    = 6;

    private boolean repaintButtons                  = true;
    private boolean repaintStatusBar                = true;

    public static int MODES_COUNT = 6;

    ImageButton[]       buttons;
    ImageButton         waitCursor;
    
    final Calendar      calendar = Calendar.getInstance();

    public ColorProfile currentProfile;

    public BitmapFont   fontPlain;
    public BitmapFont   fontItalic;

           boolean      fontZoomIn = true;
    public byte         currentFontSizeIndex = BitmapFont.FONT_SIZE_16;

    BitmapFont fontStatus;

    private PageView prevPageView;
    private PageView currentPageView;
    private PageView nextPageView;

    //Actual page pointers are in the Page objects themselves (i.e. like a linkedlist)
    private Page firstPage;
    private Page lastPage;
    
    private AnimationThread animation;

    private int mode = 0;

    //input events
    private int xx = 0;
    private int yy = 0;
    private int xxPressed = 0;
    private int yyPressed = 0;
    private ImageButton buttonPressed = null;

    public BookCanvas(AlbiteMIDlet app) { //as its public it is not REALLY a singleton, but its certain that this one would not be called twice
        this.app = app;
    }

    public void initialize() {
        loadFont();

        loadStatusFont();
        STATUS_BAR_HEIGHT = fontStatus.lineHeight + 6;

        //Load menu images
        //Images cannot be stored normally (i.e. as Image objects) as they need
        //to be mutable: one should be able to select the color of the image
        //without affecting the alpha channel
        try {
            buttons = new ImageButton[5];
            buttons[0] = new ImageButton("/res/gfx/button_menu.raw", ImageButton.TASK_MENU);
            buttons[1] = new ImageButton("/res/gfx/button_library.raw", ImageButton.TASK_LIBRARY);
            buttons[2] = new ImageButton("/res/gfx/button_dict.raw", ImageButton.TASK_DICTIONARY);
            buttons[3] = new ImageButton("/res/gfx/button_font_size.raw", ImageButton.TASK_FONTSIZE);
            buttons[4] = new ImageButton("/res/gfx/button_color_profile.raw", ImageButton.TASK_COLORPROFILE);
            setupButtons();

            waitCursor = new ImageButton("/res/gfx/hourglass.raw", ImageButton.TASK_NONE);

            if (currentProfile == null) {
                ColorProfile day = ColorProfile.DEFAULT_DAY;
                ColorProfile night = ColorProfile.DEFAULT_NIGHT;

                //shall not forget these two lines
                connectProfiles(day, night);
                currentProfile = day;
            }
            applyColorProfile();
        } catch (IOException ioe) {ioe.printStackTrace();}
        System.gc();

        animation = new AnimationThread(this);
        animation.start(); //starts the animation thread. it wont hog the cpu
    }

    public void paint(Graphics g) {

        if (repaintButtons)
            drawButtons(g);

        if (repaintStatusBar)
            drawStatusBar(g);

        switch (mode) {

            //this way one may implement layers
            case MODE_PAGE_READING:
            case MODE_PAGE_DRAGGING:
            case MODE_PAGE_LOCKED:

                if (currentPageView != null) { //i.e. all loaded
                    g.setColor(currentProfile.canvasBackgroupColor);
                    g.fillRect(0, MENU_HEIGHT, getWidth(), currentPageView.getHeight());
                    g.drawImage(currentPageView.getImage(), currentPageView.x, MENU_HEIGHT, Graphics.TOP | Graphics.LEFT);
                    g.drawImage(nextPageView.getImage(), nextPageView.x, MENU_HEIGHT, Graphics.TOP | Graphics.LEFT);
                    g.drawImage(prevPageView.getImage(), prevPageView.x, MENU_HEIGHT, Graphics.TOP | Graphics.LEFT);
                }
                break;

            case MODE_PAGE_LOADING:
                //draw loading cursor on top
                waitCursor.draw(g, (getWidth()-waitCursor.width)/2, (getHeight()-waitCursor.height)/2);

        }
    }

    private void setupButtons() {
        if (buttons.length > 0) {
            int x = 0;
            for (int i=0; i<buttons.length; i++) {
                buttons[i].x = x;
                buttons[i].y = 0;
                x += buttons[i].width;
            }
        }
    }
    private void drawButtons(Graphics g) {
        g.setColor(currentProfile.canvasBackgroupColor);
        g.fillRect(0, 0, getWidth(), MENU_HEIGHT);

        if (buttons.length > 0) {
            for (int i=0; i<buttons.length; i++)
                buttons[i].draw(g, buttons[i].x, buttons[i].y);
            repaintButtons = false;
        }
    }

    private void drawStatusBar(Graphics g) {
        final int w = getWidth();
        final int h = getHeight();
        
        g.fillRect(0, h - STATUS_BAR_HEIGHT, w, STATUS_BAR_HEIGHT);
        g.setColor(currentProfile.statusBarTextColor);
        char[] clock = (calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE)).toCharArray();

        fontStatus.drawChars(g, currentProfile.statusBarTextColor, clock, w-3-fontStatus.charsWidth(clock, 0, clock.length), h-3 -fontStatus.lineHeight);
//        g.drawString((currentPage.getStart()/Book.getBook().getCurrentChapter().getSize())*100 + "%", 5, h-3, Graphics.LEFT | Graphics.BOTTOM);

        repaintStatusBar = false;
    }

    private ImageButton findButtonPressed(int x, int y) {
        for (int i=0; i<buttons.length; i++)
            if (buttons[i].buttonPressed(x, y))
                return buttons[i];
        return null;
    }

    public void pointerPressed(int x, int y) {
        xx = xxPressed = x;
        yy = yyPressed = y;
        startHoldingTime = System.currentTimeMillis();
        if (y <= MENU_HEIGHT) {
            //has a button been pressed?
            buttonPressed = findButtonPressed(x, y);
            if (buttonPressed != null) {
                mode = MODE_BUTTON_PRESSING;
                buttonPressed.setColor(currentProfile.menuButtonsPressedColor);
                repaintButtons = true;
                repaint();
                serviceRepaints();
            }
        }
    }

    public void pointerReleased(int x, int y) {
        xx = x;
        yy = y;

        final int w = getWidth();
        final int h = getHeight();

        boolean holding = false;
        if (System.currentTimeMillis() - startHoldingTime > HOLD_TIME)
            holding = true;
        
        switch(mode) {
            case MODE_PAGE_READING:
                //then it's somewhere in the page area
                if (holding) {
                    System.out.println("Dictionary;");
                //show menu for selected word (if a word is selected and not whitespace)
                    /*
                    System.out.println("Holding for word at " + (x-MARGIN_WIDTH) + "x" + (y-MENU_HEIGHT));
                    Region r = currentPageView.page.getRegionAt(x-MARGIN_WIDTH, y-MENU_HEIGHT);
//                    RegionText rt = (RegionText)r;
                    if (r == null)
                        System.out.println("No region found");
                    else
                        System.out.println("Region FOUND!");
                        System.out.println(r.getClass().getName());
                    System.out.println();
                     * 
                     */
                } else {
                    if (x > w-MARGIN_CLICK_TRESHOLD) {
                        //Right Page position
                        animation.animateScrollPage(-2);
                        mode = MODE_PAGE_DRAGGING;
                    }

                    if (x < MARGIN_CLICK_TRESHOLD) {
                        //Right Page position
                        animation.animateScrollPage(+2);
                        mode = MODE_PAGE_DRAGGING;
                    }
                }
                break;
                
            case MODE_PAGE_DRAGGING:
                final int cx = currentPageView.x - MARGIN_WIDTH;

                if (cx == 0) {
                    animation.suspend();
                    mode = MODE_PAGE_READING;
                    break;
                }
                
                if (cx < -DRAG_TRESHOLD) {
                    animation.animateScrollPage(-2);
                    break;
                }

                if (cx > DRAG_TRESHOLD) {
                    animation.animateScrollPage(2);
                    break;
                }

                if (cx > 0) {
                    animation.animateScrollPage(-1);
                    break;
                }

                if (cx <= 0) {
                    animation.animateScrollPage(1);
                    break;
                }

                mode = MODE_PAGE_READING;
                break;

            case MODE_BUTTON_PRESSING:

                buttonPressed.setColor(currentProfile.menuButtonsColor);
                repaintButtons = true;
                repaint();
                serviceRepaints();

                if (buttonPressed == findButtonPressed(x, y)) {
                    switch(buttonPressed.task) {
                        case ImageButton.TASK_FONTSIZE:
                            cycleFontSizes();
                            break;

                        case ImageButton.TASK_COLORPROFILE:
                            cycleColorProfiles();
                            break;

                        case ImageButton.TASK_LIBRARY:
                            openLibrary();
                            break;

                        case ImageButton.TASK_MENU:
                            if (holding) {
                                //Exit midlet if user holds over the menu button
                                app.exitMIDlet();
                            }
                    }
                }
                buttonPressed = null;
                mode = MODE_PAGE_READING;
                break;
        }
    }

    public void pointerDragged(int x, int y) {

        switch(mode) {
            case MODE_PAGE_DRAGGING:
            case MODE_PAGE_READING:
                mode = MODE_PAGE_DRAGGING;
                animation.suspend();
                scrollPages(x-xx, true);
                break;
        }

        //It's essential that these values are updated AFTER the switch statement!
        xx = x;
        yy = y;
//        repaint();
    }

    public void keyPressed(int k) {
        int kga = getGameAction(k);

        //there is a bug: if one presses the key too often, no one will lose the ability to change pages
        switch(mode) {
            case MODE_PAGE_READING:
                switch(kga) {
                    case LEFT:
//                    mode = MODE_PAGE_DRAGGING;
                        animation.animateScrollPage(2);
                        return;

                    case RIGHT:
//                    mode = MODE_PAGE_DRAGGING;
                        animation.animateScrollPage(-2);
                        return;

                    case FIRE:
                        //menu
                        return;

                    case GAME_A:
                        //open library
                        openLibrary();
                        return;

                    case GAME_B:
                        //open dictionary and unit converter
                        return;

                    case GAME_C:
                        //change font size
                        cycleFontSizes();
                        return;

                    case GAME_D:
                        //change color profile
                        cycleColorProfiles();
                        return;
                }
        }
    }

    void openBook(Book book) {
        closeBook();
        currentBook = book;
    }

    void closeBook() {
        if (bookOpen()) {
            currentBook.getCurrentChapter().setPosition(currentPageView.page.getStart());
            currentBook.close();
            currentBook = null;
            flushPages();
        }
    }

    public static Book getCurrentBook() {
        return currentBook;
    }

    public static boolean bookOpen() {
        return currentBook != null;
    }

    protected void scrollPages(int dx, boolean fullPage) {

        final int w = getWidth();
        currentPageView.x += dx;
        nextPageView.x    += dx;
        prevPageView.x    += dx;
        
        if (fullPage) {
            if (currentPageView.x <= -w + MARGIN_WIDTH) {
                serviceRepaints();
                currentPageView.x = -w + MARGIN_WIDTH;
                nextPageView.x = MARGIN_WIDTH;
                prevPageView.x = -2*w + MARGIN_WIDTH;
                mode = MODE_PAGE_LOCKED;

                switch (nextPageView.page.getMode()) {
                    case Page.PAGE_MODE_LEAVES_CHAPTER_NEXT:
                        animation.suspend();
                        mode = MODE_PAGE_LOADING;
                        repaint();
                        serviceRepaints();
//                        System.out.println("Going to next chapter");
                        goToFirstPage(currentBook.getCurrentChapter().getNextChapter());
                        break;

                    case Page.PAGE_MODE_LEAVES_CHAPTER_END_OF_BOOK:
//                        System.out.println("No next page so this is the last page of the book");
                        mode = MODE_PAGE_DRAGGING;
                        animation.animateScrollPage(3);
                    break;

                    case Page.PAGE_MODE_IMAGE:
                    case Page.PAGE_MODE_NORMAL:
                        animation.suspend();
                        repaint();
                        serviceRepaints(); //this removes the glitch from the page loading procedure
                        loadNextPage();
                        break;
                }
            } else {
                if (currentPageView.x >= w + MARGIN_WIDTH) {
                    currentPageView.x = w + MARGIN_WIDTH;
                    nextPageView.x = 2*w + MARGIN_WIDTH;
                    prevPageView.x = MARGIN_WIDTH;
                    mode = MODE_PAGE_LOCKED;

                    switch (prevPageView.page.getMode()) {
                        case Page.PAGE_MODE_LEAVES_CHAPTER_PREV:
                            animation.suspend();
                            mode = MODE_PAGE_LOADING;
                            repaint();
                            serviceRepaints();
//                            System.out.println("Going to prev chapter");
                            goToLastPage(currentBook.getCurrentChapter().getPrevChapter());
                            break;

                        case Page.PAGE_MODE_LEAVES_CHAPTER_START_OF_BOOK:
//                            System.out.println("No prev page so this is the first chapter");
                            mode = MODE_PAGE_DRAGGING;
                            animation.animateScrollPage(-3);
                        break;
                        
                        case Page.PAGE_MODE_IMAGE:
                        case Page.PAGE_MODE_NORMAL:
                            animation.suspend();
                            repaint();
                            loadPrevPage();
                            break;
                        }
                } else {
                    repaint();
                    serviceRepaints();
                }
            }
        } else {
            if ((dx < 0 && currentPageView.x <= MARGIN_WIDTH) || (dx >= 0 && currentPageView.x >= MARGIN_WIDTH)) {
                currentPageView.x = MARGIN_WIDTH;
                nextPageView.x = w + MARGIN_WIDTH;
                prevPageView.x = -w + MARGIN_WIDTH;
                animation.suspend();
                mode = MODE_PAGE_READING;
            }
            repaint();
        }
    }

    private void loadPrevPage() {
        PageView p = nextPageView;
        nextPageView = currentPageView;
        currentPageView = prevPageView;
        prevPageView = p;
        p.page = currentPageView.page.prevPage;
        p.renderPage();
        prevPageView.x = -getWidth() + MARGIN_WIDTH;
        mode = MODE_PAGE_READING;
//        System.out.println("The prev page is on");
    }
    
    private void loadNextPage() {
        PageView p = prevPageView;
        prevPageView = currentPageView;
        currentPageView = nextPageView;
        nextPageView = p;
        p.page = currentPageView.page.nextPage;
        p.renderPage();
        nextPageView.x = getWidth() + MARGIN_WIDTH;
        mode = MODE_PAGE_READING;
//        System.out.println("The next page is on");
    }
    
    private void flushPages() {
        if (firstPage != null) {
            Page page = firstPage;
            Page page_;
            while (page.nextPage != null) {
                page_ = page;
                page = page.nextPage;
                page_.nextPage = null;
            }

            while (page.prevPage != null) {
                page_ = page;
                page = page.prevPage;
                page_.prevPage = null;
            }
            firstPage = null;
        }
    }
    
    private void reflowPages() {
        //flushing pages
        flushPages();

        final BookChapter chapter = currentBook.getCurrentChapter();
        final int width = currentPageView.getWidth();
        final int height = currentPageView.getHeight();

        chapter.getTextBuffer();
        Page justAdded;
        Page current;

        //First dummy page (transition to prev chapter or opening of book)
        Page dummyStart = new Page(chapter, false);
        justAdded = dummyStart;

        //Real pages
        Page.IMAGES_QUEUE_POOL.removeAllElements();
        char[] buffer = chapter.getTextBuffer();
        int    bufferSize = chapter.getSize();
        for (int i=0; i<bufferSize;) { //there will be at least one 'real' (i.e. not dummy) page
            //current = new Page(buffer, bufferSize, i, fontPlain, fontItalic, currentProfile, width, height, imagesQueue, justAdded.lastHyphenatedWord);
             current = new Page(buffer, bufferSize, i, justAdded);

            if (!current.isEmpty()) {
                //page with content to render
                justAdded.nextPage = current;
                current.prevPage = justAdded;
                justAdded = current;

                int pageMode = current.getMode();
                switch (pageMode) {
                    case Page.PAGE_MODE_NORMAL:
                        i = current.getEnd();
                        break;
                    case Page.PAGE_MODE_IMAGE:
                        break;
                }
            } else{
                i = current.getEnd();
//                System.out.println("Empty page suppressed.");
            }
        }

        //Last dummy page (transition to next chapter or end of book)
        current = new Page(chapter, true);

        justAdded.nextPage = current;
        current.prevPage = justAdded;

        //fist/last page
        firstPage = dummyStart.nextPage;
        lastPage = current.prevPage;
    }

    private void loadChapter(BookChapter chapter) {
        if (chapter != currentBook.getCurrentChapter() || firstPage == null) { //chapter changed or book not loaded at all!
            currentBook.unloadChaptersBuffers();
            currentBook.setCurrentChapter(chapter);
            chapter.getTextBuffer(); //force text buffer loading;
            reflowPages();
            System.out.println("Memory statistics after loading chapter `" + chapter.getTitle() + "`");
            System.out.println("Total mem available: " + Runtime.getRuntime().totalMemory());
            System.out.println("Free  mem available: " + Runtime.getRuntime().freeMemory());
        }
    }

    public void goToFirstPage(BookChapter chapter) {
        loadChapter(chapter);

        prevPageView.page = firstPage.prevPage;
        currentPageView.page = firstPage;
        nextPageView.page = firstPage.nextPage;

        pagesInitialized();
    }

    public void goToLastPage(BookChapter chapter) {
        loadChapter(chapter);

        prevPageView.page = lastPage.prevPage;
        currentPageView.page = lastPage;
        nextPageView.page = lastPage.nextPage;

        pagesInitialized();
    }

    public void goToPosition(BookChapter chapter, int position) {

        if (position <= 0)
            goToFirstPage(chapter);

        if (position >= chapter.getSize())
            goToLastPage(chapter);

        loadChapter(chapter);

        Page foundPage = firstPage;
        while (foundPage.nextPage != null) {
            if (foundPage.contains(position))
                break;
            foundPage = foundPage.nextPage;
        }
        
        if (foundPage == lastPage.nextPage) {//something went wrong?!
            foundPage = firstPage;
        }

        currentPageView.page = foundPage;
        prevPageView.page = foundPage.prevPage;
        nextPageView.page = foundPage.nextPage;

        pagesInitialized();
    }

    private void pagesInitialized() {
        currentPageView.renderPage();
        prevPageView.renderPage();
        nextPageView.renderPage();

        currentPageView.x = MARGIN_WIDTH;
        prevPageView.x = MARGIN_WIDTH -getWidth();
        nextPageView.x = MARGIN_WIDTH +getWidth();

        mode = MODE_PAGE_READING;
        
        repaint();
        serviceRepaints();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        //check for mode validity
        if (mode < 0 || mode >= MODES_COUNT)
            throw new IllegalArgumentException();
        this.mode = mode;
    }

    public void openLibrary() {
        app.switchDisplayable(null, app.getFileBrowser());
    }
    
    public void cycleColorProfiles() {
        currentProfile = currentProfile.next;
        applyColorProfile();
    }

    public ColorProfile getCurrentProfile() {
        return currentProfile;
    }

    public void applyColorProfile() {

        //apply to buttons
        for (int i=0; i<buttons.length; i++)
            buttons[i].setColor(currentProfile.menuButtonsColor);
        repaintButtons = true;

        //apply to cursor
        waitCursor.setColor(currentProfile.cursorWaitColor);

        //apply to status bar
        repaintStatusBar = true;

        //apply to pages
        if (currentPageView != null)
            pagesInitialized();
    }

    private void loadFont() {
        //load font size from RMS?
        try {
            int currentFontSize = BitmapFont.FONT_SIZES[currentFontSizeIndex];
            Page.FONT_PLAIN  = fontPlain = new BitmapFont("droid-serif_" + currentFontSize);
            Page.FONT_ITALIC = fontItalic = new BitmapFont("droid-serif_it_" + currentFontSize);
            Page.FONT_INDENT = fontPlain.spaceWidth * 3;

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void cycleFontSizes() {
        if (currentFontSizeIndex == 0)
            fontZoomIn = true;
        if (currentFontSizeIndex == BitmapFont.FONT_SIZES.length-1)
            fontZoomIn = false;
        if (fontZoomIn)
            currentFontSizeIndex++;
        else
            currentFontSizeIndex--;
        loadFont();
        flushPages();
        firstPage = null;
        mode = MODE_PAGE_LOADING;
        repaint();
        serviceRepaints();
        goToPosition(currentBook.getCurrentChapter(), currentPageView.page.getStart());
    }

    private void loadStatusFont() {
        //load font size from RMS?
        try {
            fontStatus = new BitmapFont("status");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void beforeHide() {
        animation.suspend();
    }

    public void initiliazePageViews() {
        currentPageView   = new PageView(this);
        nextPageView      = new PageView(this);
        prevPageView      = new PageView(this);

        Page.WIDTH = currentPageView.getWidth();
        Page.HEIGHT = currentPageView.getHeight();

        currentPageView.x = MARGIN_WIDTH;
        nextPageView.x    = MARGIN_WIDTH + getWidth();
        prevPageView.x    = MARGIN_WIDTH - getWidth();
    }

    public void connectProfiles(ColorProfile cp1, ColorProfile cp2) {
        cp1.next = cp2;
        cp2.next = cp1;
    }

    protected void showNotify() {
        repaintButtons = true;
        repaintStatusBar = true;
    }
}