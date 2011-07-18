//=============================================================================
// Brief   : Operation Result implementation
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
 * Usualy when we want to confirm if some function had success or not, we just
 * put some boolean result. That method is limited because it doesn't give us
 * any more information when the operation is unsuccessful. I've created
 * OperationResult to fill that gap, by having a message appart from the
 * operation status.
 * 
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 10-03-2011
 */
public final class OperationResult {

    private OperationStatus os;
    private IMessage msg;

    /**
     * Constructor without message
     * 
     * @param status Operation status can be failure or success.
     */
    public OperationResult(OperationStatus status) {
        os = status;
    }

    /**
     * Constructor with message
     * @see OperationStatus
     * @param status Operation status can be failure or success.
     * @param message Message about the operation
     */
    public OperationResult(OperationStatus status, IMessage message) {
        os = status;
        msg=message;
    }
    
    /**
     * Returns status. Can be failure or success
     * @return OperationStatus
     */
    public OperationStatus getResult(){
        return os;
    }

    /**
     * Returns message type, depending on the implementation.
     * @see IMessage
     * @return Error type
     */
    public String getErrorType(){
        if(msg==null){
            return "";
        }
        return msg.getMessageType();
    }

    /**
     * Returns message, if exists
     * @return Message
     */
    public String getErrorDetails(){
        if(msg==null){
            return "";
        }
        return msg.getMessage();
    }
}
