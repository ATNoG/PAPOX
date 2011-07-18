//=============================================================================
// Brief   : Exception for invalid class factory
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// PAPOX (Policy Administration Point for OASIS XACML) - Business Layer
//
// Copyright (C) 2011 Universidade Aveiro
// Copyright (C) 2011 Instituto de Telecomunicações - Pólo Aveiro
// Copyright (C) 2011 Portugal Telecom Inovação
//
// This software is distributed under a license. The full license
// agreement can be found in the file LICENSE in this distribution.
// This software may not be copied, modified, sold or distributed
// other than expressed in the named license agreement.
//
// This software is distributed without any warranty.
//=============================================================================
package pt.fg.pap.exceptions;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 17/Mar/2011
 */
public class InvalidClassFactoryException extends Exception {

    /**
     * Creates a new instance of <code>InvalidClassFactoryException</code> without detail message.
     */
    public InvalidClassFactoryException() {
    }


    /**
     * Constructs an instance of <code>InvalidClassFactoryException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public InvalidClassFactoryException(String msg) {
        super(msg);
    }
}
