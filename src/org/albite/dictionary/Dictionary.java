/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.albite.dictionary;

/**
 *
 * @author albus
 */
public abstract class Dictionary {

    public static final String  TYPE_BOOK_STRING        = "Book Dictionary";
    public static final String  TYPE_LOCAL_STRING       = "Local Dictionaries";
    public static final String  TYPE_WEB_STRING         = "Web Dictionaries";

    public static final int     TYPE_BOOK               = 0;
    public static final int     TYPE_LOCAL              = 1;
    public static final int     TYPE_WEB                = 2;

    protected static final String WORD_NOT_FOUND        = "Word not found.";

    /*
     * This one had better be a odd value, so that the suggestions would be
     * centered around a "best-find" word.
     */
    protected static final int  NUMBER_OF_SUGGESTIONS   = 21;

    String                      title;
    short                       language;

    /**
     * Looks up a word in the dictionary and returns its definition.
     *
     * @param lookingFor
     * @return
     */
    abstract public String getDefinition(String lookingFor)
            throws DictionaryException;

    /**
     * Looks up a word in the dictionary and returns either:
     * - one string, being the word definition
     * - several strings, being a list of suggestions.
     *
     * @param lookingFor
     * @return
     */
    abstract public String[] lookUp(String lookingFor)
            throws DictionaryException;

    public final String getTitle() {
        return title;
    }

    public final short getLanguage() {
        return language;
    }
}
