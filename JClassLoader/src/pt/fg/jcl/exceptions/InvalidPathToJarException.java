//=============================================================================
// Brief   : "Invalid path to jar" exception
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Java Class Loader
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

package pt.fg.jcl.exceptions;

import java.io.IOException;

/**
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 28-02-2011
 */
public final class InvalidPathToJarException extends IOException{

    private String path;

    public InvalidPathToJarException(String path) {
        this.path = path;
    }

    public InvalidPathToJarException() {
    }



    @Override
    public String getMessage() {
        return "The path to Jar files is not valid: " + path;
    }

}
