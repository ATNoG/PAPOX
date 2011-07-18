//=============================================================================
// Brief   : JUnit tests for Berkeley's DB XML Database Helper
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Berkeley DB XML helper module
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

package pt.fg.dbtools.bdbxml;

import com.sleepycat.dbxml.XmlValue;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlResults;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Francisco Gouveia
 */
public class BDBXmlDatabaseHelperTest {

    private static BDBXmlDatabaseHelper db;
    private static final String CONTAINER = "pt.fg.dbxml.test";
    //--- Common parameters
    private static final String docName = "testdoc1";
    private static final String xml = "<tests><a><test>b</test></a><test id='1'>A</test><test id='2'>B</test><test id='3'>C</test></tests>";
    //--- Common variables
    private XmlDocument docXml;

    public BDBXmlDatabaseHelperTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        db = new BDBXmlDatabaseHelper(CONTAINER);
        db.activateDebugging();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        db.desactivateDebugging();
    }

    @Before
    public void setUp() throws XmlException {
        docXml = db.createDocument(docName, xml);

        if (!db.putDocument(docXml)) {
            //If document already exists
            db.removeDocument(docXml.getName());

            if (!db.putDocument(docXml)) {
                //If it was another type of error, it will happen again
                System.out.println("Error inserting document...");
            }
        }
    }

    @After
    public void tearDown() throws XmlException {
        // Cleans the test document from repository
        db.removeDocument(docXml.getName());
    }

    /**
     * Test of addNodeAfter method, of class BDBXmlDatabaseHelper.
     */
    @Test
    public void testAddNodeAfter() throws XmlException {
        System.out.println("##########################");
        System.out.println("# addNodeAfter");

        int expected = 4;

        // Inserts node after <test id='1' />
        db.addNodeAfter("<test id='1.1'>a</test>", "collection('" + CONTAINER + "')//test[@id='1']");

        // Gets all test nodes from collection
        XmlResults res = db.query("collection('" + CONTAINER + "')//test");

        assertEquals(expected, res.size());

    }

    /**
     * Test of addNodeInto method, of class BDBXmlDatabaseHelper.
     */
    @Test
    public void testAddNodeInto() throws XmlException {
        System.out.println("##########################");
        System.out.println("# addNodeInto");

        int expected = 4;

        // Inserts node after <test id='1' />
        db.addNode("<test id='1.1'>a</test>", "collection('" + CONTAINER + "')/tests");

        // Gets all test nodes from collection
        XmlResults res = db.query("collection(\"" + CONTAINER + "\")//test");

        assertEquals(expected, res.size());

    }

    /**
     * Test of addNodeAsFirst method, of class BDBXmlDatabaseHelper.
     */
    @Test
    public void testAddNodeAsFirst() throws XmlException {
        System.out.println("##########################");
        System.out.println("# addNodeAsFirst");

        String expected = "aABC";

        // Inserts node after <test id='1' />
        db.addNodeAsFirst("<test id='1.1'>a</test>", "collection(\"" + CONTAINER + "\")/tests");

        // Gets all test nodes from collection
        XmlResults res = db.query("collection(\"" + CONTAINER + "\")//test");

        String result = valuesToString(res);

        assertEquals(expected, result);
    }

    /**
     * Test of addNodeAsLast method, of class BDBXmlDatabaseHelper.
     */
    @Test
    public void testAddNodeAsLast() throws XmlException {
        System.out.println("##########################");
        System.out.println("# addNodeAsLast");

        String expected = "AC";

        // Inserts node after <test id='1' />
        db.removeNode("collection('" + CONTAINER + "')//test[@id='2']");

        // Gets all test nodes from collection
        XmlResults res = db.query("collection(\"" + CONTAINER + "\")//test");

        String result = valuesToString(res);

        assertEquals(expected, result);
    }

    /**
     * Test of addNodeAsLast method, of class BDBXmlDatabaseHelper.
     */
    @Test
    public void testRemoveNode() throws XmlException {
        System.out.println("##########################");
        System.out.println("# removeNode");

        String expected = "ABCa";

        // Inserts node after <test id='1' />
        db.addNodeAsLast("<test id='1.1'>a</test>", "collection('" + CONTAINER + "')/tests");

        // Gets all test nodes from collection
        XmlResults res = db.query("collection(\"" + CONTAINER + "\")//test");

        String result = valuesToString(res);

        assertEquals(expected, result);
    }

    public String resultsToString(XmlResults in) {
        String res = "";
        try {
            while (in.hasNext()) {
                res += getResults(in.next());
            }
        } catch (XmlException ex) {
            Logger.getLogger(BDBXmlDatabaseHelperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    private String getResults(XmlValue xv) {
        String res = "";
        try {
            switch (xv.getType()) {
                case XmlValue.NODE:
                    switch (xv.getNodeType()) {
                        case XmlValue.ATTRIBUTE_NODE:
                            res += "\nAttribute";
                            res += "\n" + xv.getNodeName() + ":" + xv.getNodeValue();
                            break;
                        case XmlValue.CDATA_SECTION_NODE:
                            res += "\nCDATA";
                            res += "\nValue: " + xv.getNodeValue();
                            break;
                        case XmlValue.COMMENT_NODE:
                            res += "\nCOMMENT";
                            break;
                        case XmlValue.DOCUMENT_NODE:
                            res += "\nDOCUMENT";
                            res += "\nNode name: " + xv.getNodeName();
                            break;
                        case XmlValue.DOCUMENT_TYPE_NODE:
                            res += "\nDOCUMENT_TYPE";
                            res += "\nNode name: " + xv.getNodeName();
                            break;
                        case XmlValue.DOCUMENT_FRAGMENT_NODE:
                            res += "\nDOCUMENT_FRAGMENT";
                            res += "\nNode name: " + xv.getNodeName();
                            break;
                        case XmlValue.ELEMENT_NODE:
                            res += "\nELEMENT";
                            res += "\nNode name: " + xv.getNodeName();
                            res += resultsToString(xv.getAttributes());
                            //res += resultsToString(xv.getResults());
                            res += getResults(xv.getFirstChild());
                            break;
                        case XmlValue.TEXT_NODE:
                            res += "\nTEXT";
                            res += "\nNode name: " + xv.getNodeName();
                            res += "\nValue: " + xv.getNodeValue();
                            break;
                    }
                    break;
                case XmlValue.STRING:
                    res += xv.asString();
                    break;
                default:
                    res += "INVALID";
                    break;
            }
        } catch (XmlException ex) {
            Logger.getLogger(BDBXmlDatabaseHelperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    public String valuesToString(XmlResults in) {
        String res = "";
        try {
            while (in.hasNext()) {
                res += getValues(in.next());
            }
        } catch (XmlException ex) {
            Logger.getLogger(BDBXmlDatabaseHelperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

    private String getValues(XmlValue xv) {
        String res = "";
        try {
            switch (xv.getType()) {
                case XmlValue.NODE:
                    switch (xv.getNodeType()) {
                        case XmlValue.CDATA_SECTION_NODE:
                        case XmlValue.TEXT_NODE:
                            res += xv.getNodeValue();
                            break;
                        case XmlValue.ELEMENT_NODE:
                            res += getValues(xv.getFirstChild());
                            break;
                    }
                    break;
            }
        } catch (XmlException ex) {
            Logger.getLogger(BDBXmlDatabaseHelperTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
}
