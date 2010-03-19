/* Copyright (c) 2007-2009 Amazon.com, Inc.  All rights reserved. */

package com.amazon.ion.impl;

import com.amazon.ion.IonCatalog;
import com.amazon.ion.IonException;
import com.amazon.ion.IonLoader;
import com.amazon.ion.IonReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * Implementation of the {@link IonLoader} interface.
 * <p>
 * This is an internal implementation class that should not be used directly.
 */
public class LoaderImpl
    implements IonLoader
{
    static final boolean USE_NEW_READERS = true;

    private final IonSystemImpl mySystem;
    private final IonCatalog    myCatalog;

    public LoaderImpl(IonSystemImpl system, IonCatalog catalog)
    {
        mySystem = system;
        myCatalog = catalog;
    }


    public IonSystemImpl getSystem()
    {
        return mySystem;
    }


    //=========================================================================
    // Loading from File

    public IonDatagramImpl load(File ionFile)
        throws IonException, IOException
    {
        FileInputStream fileStream = new FileInputStream(ionFile);
        try
        {
            return load(fileStream);
        }
        finally
        {
            fileStream.close();
        }
    }


    //=========================================================================
    // Loading from String


    public IonDatagramImpl load(String ionText)
        throws IonException
    {
        if (USE_NEW_READERS)
        {
            IonReader reader = mySystem.newSystemReader(ionText);
            try
            {
                IonDatagramImpl dg =
                    new IonDatagramImpl(mySystem, myCatalog, reader);

                return dg;
            }
            catch (IOException e)
            {
                // Wrap this because it shouldn't happen and we don't want to
                // propagate it.
                String message = "Error reading from string: " + e.getMessage();
                throw new IonException(message, e);
            }
        }

        StringReader reader = new StringReader(ionText);
        try
        {
            return load(reader);
        }
        catch (IOException e)
        {
            // Wrap this because it shouldn't happen and we don't want to
            // propagate it.
            String message = "Error reading from string: " + e.getMessage();
            throw new IonException(message, e);
        }
        finally
        {
            // This may not be necessary, but for all I know StringReader will
            // release some resources.
            reader.close();
        }
    }


    //=========================================================================
    // Loading from Reader

    public IonDatagramImpl load(Reader ionText)
        throws IonException, IOException
    {
        if (USE_NEW_READERS)
        {
            IonReader reader = mySystem.newSystemReader(ionText);
            try
            {
                IonDatagramImpl dg = new IonDatagramImpl(mySystem, myCatalog, reader);
                return dg;
            }
            catch (IOException e)
            {
                throw new IonException(e);
            }
        }
        return new IonDatagramImpl(mySystem, myCatalog, ionText);
    }


    //=========================================================================
    // Loading from byte[]

    public IonDatagramImpl load(byte[] ionData)
    {
        IonDatagramImpl dg = null;

        try
        {
            boolean isBinary = IonBinary.matchBinaryVersionMarker(ionData);
            if (USE_NEW_READERS && !isBinary) {
                IonReader reader = mySystem.newSystemReader(ionData);
// assert reader instanceof IonTextReaderImpl;
                assert reader.getIterationType().isText();
                dg = new IonDatagramImpl(mySystem, myCatalog, reader);
            }
            else {
                dg = new IonDatagramImpl(mySystem, myCatalog, ionData);
            }

            // Force symtab preparation  FIXME should not be necessary
            dg.byteSize();
        }
        catch (IOException e)
        {
            throw new IonException(e);
        }

        return dg;
    }


    //=========================================================================
    // Loading from InputStream

    public IonDatagramImpl load(InputStream ionData)
        throws IonException, IOException
    {
        PushbackInputStream pushback = new PushbackInputStream(ionData, 8);
        if (IonImplUtils.streamIsIonBinary(pushback)) {
            if (USE_NEW_READERS)
            {
                // Nothing special to do. SystemReader works fine to
                // materialize the top layer of the datagram.
                // The streaming APIs add no benefit.
            }

            SystemReader systemReader =
                mySystem.newBinarySystemReader(myCatalog, pushback);
            return new IonDatagramImpl(mySystem, systemReader);
        }

        // Input is text
        if (USE_NEW_READERS)
        {
            IonReader reader = mySystem.newSystemReader(pushback);
            assert reader.getIterationType().isText();
            //assert reader instanceof IonTextReaderImpl;
            try
            {
                IonDatagramImpl dg = new IonDatagramImpl(mySystem, myCatalog, reader);
                // Force symtab preparation  FIXME should not be necessary
                dg.byteSize();
                return dg;
            }
            catch (IOException e)
            {
                throw new IonException(e);
            }
        }

        Reader reader = new InputStreamReader(pushback, "UTF-8");
        return load(reader);
    }
}
