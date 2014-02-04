// Copyright (c) 2009-2013 Amazon.com, Inc.  All rights reserved.

package com.amazon.ion;

/**
 * An {@link IonCatalog} that can be updated.
 */
public interface IonMutableCatalog extends IonCatalog {
    /**
     * Adds a symbol table to this catalog.  This interface does not define the
     * behavior of this method if this catalog already contains a table with
     * the same name and version.
     *
     * @param sharedTable must be shared but not a system table or
     *        substitute table.
     */
    public void putTable(SymbolTable sharedTable);
}
