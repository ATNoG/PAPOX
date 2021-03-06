//=============================================================================
// Brief   : Message type interface
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

package pt.fg.pap.operation;

/**
 * Interface of message to be used by OperationResult
 * 
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 10-03-2011
 */
public interface IMessage {
    
    /**
     * Returns a message
     * @return message
     */
    String getMessage();
    
    /**
     * Returns the message type (eg.: Warning, Error)
     * @return message type
     */
    String getMessageType();
}
