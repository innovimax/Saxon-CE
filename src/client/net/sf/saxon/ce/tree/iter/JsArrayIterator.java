package client.net.sf.saxon.ce.tree.iter;

import client.net.sf.saxon.ce.Configuration;
import client.net.sf.saxon.ce.expr.LastPositionFinder;
import client.net.sf.saxon.ce.js.IXSLFunction;
import client.net.sf.saxon.ce.om.Item;
import client.net.sf.saxon.ce.om.SequenceIterator;
import client.net.sf.saxon.ce.trans.XPathException;
import client.net.sf.saxon.ce.value.Value;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import java.util.logging.Logger;

/**
* Class JsArrayIterator, iterates over a sequence of items held in a Javascript array
*/

public class JsArrayIterator
        extends Value implements UnfailingIterator, LastPositionFinder {

    int index=0;
    int length;
    Item current = null;
    JsArray list = null;
    Configuration config;
    static Logger logger = Logger.getLogger("JsArrayIterator");

    /**
     * Create a JsArrayIterator over a given List
     * @param list the list: all objects in the list must be instances of {@link Item}
     */

    public JsArrayIterator(JsArray list, Configuration config) {
        index = 0;
        this.list = list;
        this.length = list.length();
        this.config = config;
    }

   /**
     * Create a JsArrayIterator over the leading part of a given List
     * @param list the list: all objects in the list must be instances of {@link Item}
     * @param length the number of items to be included
     */

    public JsArrayIterator(JsArray list, int length, Configuration config) {
        index = 0;
        this.list = list;
        this.length = length;
        this.config = config;
    }

    public Item next() {
        if (index >= length) {
            current = null;
            index = -1;
            length = -1;
            return null;
        }
        Object obj = getObject(index++, list);
        try {
        current = IXSLFunction.convertFromJavaScript(obj, config).next();
        } catch(XPathException xe) {
        	// only warn because this is an unfailingIterator implementation
        	logger.warning("Failed to convert JS object: " + obj.toString() + " to XDM item.");
        }
        return current;
    }
    
    private final native Object getObject(int index, JsArray jsa) /*-{
       return jsa[index];
    }-*/;
    
    public final native JavaScriptObject getUnderlyingArray() /*-{
       return list;
    }-*/;

    public Item current() {
        return current;
    }

    public int position() {
        return index;
    }

    public int getLastPosition() {
        return length;
    }

    public SequenceIterator getAnother() {
        return new JsArrayIterator(list, config);
    }

    /**
     * Get properties of this iterator, as a bit-significant integer.
     *
     * @return the properties of this iterator. This will be some combination of
     *         properties such as {@link #GROUNDED}, {@link #LAST_POSITION_FINDER}. It is always
     *         acceptable to return the value zero, indicating that there are no known special properties.
     *         It is acceptable for the properties of the iterator to change depending on its state.
     */

    public int getProperties() {
        return LAST_POSITION_FINDER;
    }

	@Override
    /**
     * Return an iterator over this sequence.
     *
     * @return the required SequenceIterator, positioned at the start of the
     *     sequence
     */
	public SequenceIterator iterate() throws XPathException {
		return new JsArrayIterator(list, config);
	}

}

// This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. 
// If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
// This Source Code Form is “Incompatible With Secondary Licenses”, as defined by the Mozilla Public License, v. 2.0.
