//=============================================================================
// Brief   : Some JUnit tests
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Berkeley DB XML Policy Retreiver Module for PAPOX (Policy Administration
// Point for Oasis XACML)
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

package pt.fg.xacml.bdbxml.policyretreiver;

import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;
import pt.fg.pap.interfaces.IPolicyRetreiver;
/**
 * Required to assert
 */
import static org.junit.Assert.*;

/**
 *
 * @author Francisco Alexandre de Gouveia
 */
public class PolicyRetreiverTest {

    private IPolicyRetreiver pr;

    public PolicyRetreiverTest() throws IOException {
        pr = new PolicyRetreiver();
        //pr.init();
    }

    /**
     * Test of getRootPolicy method, of class PolicyRetreiver.
     */
    @Test
    @Ignore
    public void testGetRootPolicy() {
        Node rootPolicy = pr.getPolicySet("Users", 1);//pr.getRootPolicy(1);

        if(rootPolicy!=null){
            System.out.println("Res: " + rootPolicy.getNodeName());
        
        }else{
            System.out.println("No root policy available...");
        }
    }

    @Test
    public void exists(){
        boolean expected;
        boolean result;

        /**
         * Checking if an existent rule exists
         *
         * Expected result = true (yes, it exists)
         */
        expected = true;
        result = pr.ruleExist("AccessPages");
        assertEquals(expected, result);

        /**
         * Checking if an inexistent rule exists
         *
         * Expected result = false (no)
         */
        expected = false;
        result = pr.ruleExist("EatACookie");
        assertEquals(expected, result);

        /**
         * Checking if an existent policy exists
         *
         * Expected result = true (yes, it exists)
         */
        expected = true;
        result = pr.policyExist("Access");
        assertEquals(expected, result);

        /**
         * Checking if an inexistent policy exists
         *
         * Expected result = false (no)
         */
        expected = false;
        result = pr.policyExist("Eat");
        assertEquals(expected, result);


        /**
         * Checking if an existent policy set exists
         *
         * Expected result = true (yes, it exists)
         */
        expected = true;
        result = pr.policySetExist("Users");
        assertEquals(expected, result);

        /**
         * Checking if an inexistent policy set exists
         *
         * Expected result = false (no)
         */
        expected = false;
        result = pr.policySetExist("Animals");
        assertEquals(expected, result);

    }

}