// Copyright (c) 2010 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion;

import com.amazon.ion.system.SystemFactory;

/**
 *
 */
public class IonSystemTest
    extends IonTestCase
{
    /**
     * Ensure that singleValue() can handle Unicode.
     */
    public void testSingleValueUtf8()
    {
        String utf8Text = "Tivoli Audio Model One weiß/silber";
        String data = '"' + utf8Text + '"';

        IonString v = (IonString) system().singleValue(data);
        assertEquals(utf8Text, v.stringValue());

        byte[] binary = encode(data);
        v = (IonString) system().singleValue(binary);
        assertEquals(utf8Text, v.stringValue());
    }

    /**
     * check for clone across two systems failing to
     * detach the child from the datagram constructing
     * the clone
     */
    public void testTwoSystemsClone()
    {
        IonSystem system1 = system();
        IonSystem system2 = SystemFactory.newSystem();

        IonValue v1 = system1.singleValue("just_a_symbol");
        IonValue v2 = system2.clone(v1);

        IonStruct s = system2.newEmptyStruct();
        s.add("field1", v2);
    }


}
