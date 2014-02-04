// Copyright (c) 2011-2013 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion;

/**
 * An error caused by a symbol ID that could not be translated into text
 * because it is not defined by the symbol table in context.
 *
 * @since IonJava R15
 */
public class UnknownSymbolException
    extends IonException
{
    private static final long serialVersionUID = 1L;

    private final int mySid;

    public UnknownSymbolException(int sid)
    {
        mySid = sid;
    }

    public int getSid()
    {
        return mySid;
    }

    @Override
    public String getMessage()
    {
        return "Unknown symbol text for $" + mySid;
    }
}
