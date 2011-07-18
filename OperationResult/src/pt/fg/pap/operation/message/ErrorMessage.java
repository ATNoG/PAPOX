//=============================================================================
// Brief   : Error message implementation
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Operation Result
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

package pt.fg.pap.operation.message;

import pt.fg.pap.operation.IMessage;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 10-03-2011
 */
public final class ErrorMessage implements IMessage {

    private String message;

    public ErrorMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getMessageType() {
        return "Error";
    }

}
