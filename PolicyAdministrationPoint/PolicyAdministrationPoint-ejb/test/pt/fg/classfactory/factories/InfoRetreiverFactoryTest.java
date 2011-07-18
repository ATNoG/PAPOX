//=============================================================================
// Brief   : JUnit tests for Information Retreiver Factory
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
package pt.fg.classfactory.factories;

import pt.fg.pap.classfactory.factories.InfoRetreiverFactory;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import pt.fg.pap.classfactory.AvailableClass;
import pt.fg.pap.classfactory.ClassFactory;
import pt.fg.pap.interfaces.IInfoRetreiver;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 02-03-2011
 */
public class InfoRetreiverFactoryTest {

    private static InfoRetreiverFactory irf;

    @BeforeClass
    public static void setUpClass() throws Exception {
        irf = (InfoRetreiverFactory) ClassFactory.getInstance(ClassFactory.InfoRetreiverFactory);
    }

    /**
     * Test of getAllSupportedClasses method, of class InfoRetreiverFactory.
     */
    @Ignore
    @Test
    public void testGetAllSupportedClasses() {
        System.out.println("------------- testGetAllSupportedClasses");
        Set<AvailableClass> ac = irf.getAllSupportedClasses();

        boolean case1 = false;
        boolean expectedCase1 = true;

        boolean case2 = false;
        boolean expectedCase2 = false;


        for (AvailableClass c : ac) {
            if (c.getClassName().equals("pt.fg.ldapir.LDAPInfoRetreiver")) {
                case1 = true;
            }
            if (c.getClassName().equals("pt.fg.ldapir.LDAPNotInfoRetreiver")) {
                case2 = true;
            }
        }

        System.out.print("Case 1: Checking if a valid class is supported... ");
        assertEquals("Failed...", expectedCase1, case1);

        System.out.println("Passed");
        System.out.print("Case 2: Checking if a invalid class is supported... ");
        assertEquals("Failed...", expectedCase2, case2);

        System.out.println("Passed");

        System.out.println("Success!");
    }

    /**
     * Test of get method, of class InfoRetreiverFactory.
     */
    @Ignore
    @Test
    public void testGet() throws Exception {
        System.out.println("------------- testGet");

        boolean fail = false;

        Object case1;
        Object case2;

        try {
            System.out.print("Case 1: Trying to instantiate a valid class... ");
            case1 = irf.get("pt.fg.ldapir.LDAPInfoRetreiver");
            assertTrue(case1 instanceof IInfoRetreiver);
            System.out.println("Passed");
        } catch (InstantiationException ex) {
            System.out.println("Failed");
            fail=true;
        }

        try {
            System.out.print("Case 1: Trying to instantiate a invalid class... ");
            case2 = irf.get("pt.fg.ldapir.LDAPNotInfoRetreiver");
            assertTrue(case2 instanceof IInfoRetreiver);
            System.out.println("Failed");
            fail=true;
        } catch (InstantiationException ex) {
            System.out.println("Passed");
        }

        if(!fail)
            System.out.println("Success!");
    }
}
