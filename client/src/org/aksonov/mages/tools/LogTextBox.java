/***
 * Mages: Multiplayer Game Engine for mobile devices
 * Copyright (C) 2008 aksonov
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Contact: aksonov dot gmail dot com
 *
 * Author: Pavlo Aksonov
 */
package org.aksonov.mages.tools;

import android.content.Context;
import android.text.Editable;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.Scroller;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * This is a TextView that is Editable and by default scrollable, like EditText
 * without a cursor.
 * 
 * <p>
 * <b>XML attributes</b>
 * <p>
 * See {@link android.R.styleable#TextView TextView Attributes},
 * {@link android.R.styleable#View View Attributes}
 */
public class LogTextBox extends TextView {
	private final Context context;
    
    /**
	 * Instantiates a new log text box.
	 * 
	 * @param context
	 *            the context
	 */
    public LogTextBox(Context context) {
        this(context, null);
        init();
    }

    /**
	 * Instantiates a new log text box.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param inflateParams
	 *            the inflate params
	 */
    public LogTextBox(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
        init();
    }

    /**
	 * Instantiates a new log text box.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param inflateParams
	 *            the inflate params
	 * @param defStyle
	 *            the def style
	 */
    public LogTextBox(Context context, AttributeSet attrs, int defStyle) {  
    	super(context, attrs, defStyle);
    	this.context = context;
        init();
    }
    
    /**
	 * Inits the.
	 */
    private void init(){
    	setScroller(new Scroller(context));
    	
    }

    /* (non-Javadoc)
     * @see android.widget.TextView#getDefaultEditable()
     */
    
    protected boolean getDefaultEditable() {
        return true;
    }

    /* (non-Javadoc)
     * @see android.widget.TextView#getDefaultMovementMethod()
     */
    
    protected MovementMethod getDefaultMovementMethod() {
        return ScrollingMovementMethod.getInstance();
    }

    /* (non-Javadoc)
     * @see android.widget.TextView#getText()
     */
    
    public Editable getText() {
        return (Editable) super.getText();
    }

    /* (non-Javadoc)
     * @see android.widget.TextView#setText(java.lang.CharSequence, android.widget.TextView.BufferType)
     */
    
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }
}
