//=============================================================================
// Brief   : JUnit tests for class factory
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
package pt.fg.classfactory;

import pt.fg.pap.classfactory.ClassFactory;
import pt.fg.pap.classfactory.factories.MapperFactory;
import pt.fg.pap.classfactory.factories.PolicyRetreiverFactory;
import pt.fg.pap.classfactory.factories.InfoRetreiverFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 02-03-2011
 */
public class ClassFactoryTest {


    /**
     * Test of getInstance method, of class ClassFactory.
     * Tests also if correct Factory was loaded.
     */
    @Test
    public void testValidGetInstance() throws Exception {
        System.out.println("----------- testValidGetInstance");

        System.out.println("Case 1: type 0 (InfoRetreiverFactory)");
        if(!(ClassFactory.getInstance(ClassFactory.InfoRetreiverFactory) instanceof InfoRetreiverFactory))
            fail("Loaded wrong factory");

        System.out.println("Case 2: type 1 (PolicyRetreiverFactory)");
        if(!(ClassFactory.getInstance(ClassFactory.PolicyRetreiverFactory) instanceof PolicyRetreiverFactory))
            fail("Loaded wrong factory");

        System.out.println("Case 3: type 2 (MappingFactory)");
        if(!(ClassFactory.getInstance(ClassFactory.MapperFactory) instanceof MapperFactory))
            fail("Loaded wrong factory");

        // If it arrives here, then everything is working as it should
        System.out.println("Success!\n");
    }


    /**
     * Test of getInstance method with invalid parameters, of class ClassFactory.
     */
    @Test
    public void testInvalidGetInstance() {
        System.out.println("----------- testInvalidGetInstance");

        int[] invalidtypes = {-10,-1,4,10};
        System.out.println("Testing invalid parameters to get a Factory");

        int counter=1;
        for(int i : invalidtypes)
        {
            System.out.println("Case " + counter++ + ": type " + i + "");

            try {
                ClassFactory.getInstance(i);
                fail("Didn't throw and exception when it should");
            } catch (Exception ex) {
                System.out.println("Invalid (as expected)");
            }
        }
        
        // If it arrives here, then everything is working as it should
        System.out.println("Success!\n");
    }
    
}
