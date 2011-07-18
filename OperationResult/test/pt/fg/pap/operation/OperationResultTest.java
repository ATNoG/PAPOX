//=============================================================================
// Brief   : Some JUnit tests on Operation Result implementation
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

import org.junit.Test;
import pt.fg.pap.operation.message.ErrorMessage;
import static org.junit.Assert.*;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 10-03-2011
 */
public class OperationResultTest {

    /**
     * Test of getResult method, of class OperationResult.
     */
    @Test
    public void testOpSuccess() {
        System.out.println("Operation Success test");
        OperationResult or;
        OperationStatus expected = OperationStatus.OPERATION_SUCCESS;
        OperationStatus result;

        String expectedErr = ""; //No errors...
        String resultErr;

        or = new OperationResult(OperationStatus.OPERATION_SUCCESS);
        
        result = or.getResult();
        
        assertEquals(expected, result);

        resultErr = or.getErrorDetails();

        assertEquals(expectedErr, resultErr);

        resultErr = or.getErrorType();

        assertEquals(expectedErr, resultErr);

        System.out.println("Passed...");
    }


    /**
     * Test of getResult method, of class OperationResult.
     */
    @Test
    public void testOpFailure() {
        System.out.println("Operation Failure test");
        OperationResult or;
        OperationStatus expected = OperationStatus.OPERATION_FAILURE;
        OperationStatus result;

        String expectedErr = "TestError";
        String resultErr;

        or = new OperationResult(OperationStatus.OPERATION_FAILURE,new ErrorMessage("TestError"));

        result = or.getResult();

        assertEquals(expected, result);

        resultErr = or.getErrorDetails();

        assertEquals(expectedErr, resultErr);

        expectedErr="Error";
        resultErr = or.getErrorType();

        assertEquals(expectedErr, resultErr);

        System.out.println("Passed...");
    }
}