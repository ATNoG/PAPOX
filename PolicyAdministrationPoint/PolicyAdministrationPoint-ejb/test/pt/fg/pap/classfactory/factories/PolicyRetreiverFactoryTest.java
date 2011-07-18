//=============================================================================
// Brief   : JUnit tests for Policy Retreiver Factory
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
package pt.fg.pap.classfactory.factories;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;
import pt.fg.jcl.exceptions.InvalidPathToJarException;
import pt.fg.jcl.exceptions.NotReadableException;
import pt.fg.pap.classfactory.AvailableClass;
import pt.fg.pap.classfactory.ClassFactory;
import pt.fg.pap.exceptions.InvalidClassFactoryException;
import pt.fg.pap.interfaces.IPolicyRetreiver;

/**
 *
 * @author Francisco Alexandre de Gouveia
 */
public class PolicyRetreiverFactoryTest {

    private PolicyRetreiverFactory prf;

    public PolicyRetreiverFactoryTest() throws InvalidPathToJarException, NotReadableException, InvalidClassFactoryException {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of get method, of class PolicyRetreiverFactory.
     */
    @Test @Ignore
    public void testGet() throws InvalidPathToJarException, NotReadableException, InvalidClassFactoryException {
        String expected = "";
        IPolicyRetreiver pr;

        prf = (PolicyRetreiverFactory) ClassFactory.getInstance(ClassFactory.PolicyRetreiverFactory);

        try {
            pr = prf.get("pt.fg.xacml.bdbxml.policyretreiver.PolicyRetreiver");

            

            //pr = new PolicyRetreiver();
            Node x = pr.getRootPolicy(1);

            if (x != null) {

                System.out.println("Node name: " + x.getNodeName());
                //System.out.println("Node content: " + nc.nodeToString(x));

            }else{
                System.out.println("No Root Policy available");
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PolicyRetreiverFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(PolicyRetreiverFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PolicyRetreiverFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    /**
     * Test of get method, of class PolicyRetreiverFactory.
     */
    @Test
    public void testGetByWeb() throws InvalidPathToJarException, NotReadableException, InvalidClassFactoryException {
        String expected = "";
        IPolicyRetreiver pr;

        prf = (PolicyRetreiverFactory) ClassFactory.getInstance(ClassFactory.PolicyRetreiverFactoryWeb);

        try {
             pr = prf.get("pt.fg.xacml.bdbxml.policyretreiver.PolicyRetreiver");

            //pr = new PolicyRetreiver();
            Node x = pr.getRootPolicy(1);

            if (x != null) {

                System.out.println("Node name: " + x.getNodeName());
                //System.out.println("Node content: " + nc.nodeToString(x));

            }else{
                System.out.println("No Root Policy available");
            }

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PolicyRetreiverFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(PolicyRetreiverFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PolicyRetreiverFactoryTest.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    /**
     * Test of getAllSupportedClasses method, of class PolicyRetreiverFactory.
     */
    @Test
    @Ignore
    public void testGetAllSupportedClasses() {
        System.out.println("getAllSupportedClasses");

        Set<AvailableClass> ac = prf.getAllSupportedClasses();

        for (AvailableClass c : ac) {
            System.out.println("Class: " + c.getClassName());
        }
    }
}
