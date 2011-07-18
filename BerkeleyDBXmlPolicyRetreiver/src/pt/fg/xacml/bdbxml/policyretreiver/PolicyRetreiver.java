//=============================================================================
// Brief   : Policy Retreiver implementation using Berkeley's DBXML
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

import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pt.fg.dbtools.bdbxml.BDBXmlDatabaseHelper;
import pt.fg.xacml.bdbxml.adaptor.XACMLNodeAdaptor;
import pt.fg.pap.interfaces.IPolicyRetreiver;
import pt.fg.pap.operation.OperationResult;
import pt.fg.pap.operation.OperationStatus;
import pt.fg.pap.operation.message.ErrorMessage;

/**
 * Class PolicyRetreiver
 * @see IPolicyRetreiver
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 3/Mai/2011
 */
public class PolicyRetreiver implements IPolicyRetreiver {

    private BDBXmlDatabaseHelper db;
    private final static String CONTAINER = "pt.fg.xacml.policies.dbxml";
    private final static String rootXmlDoc = "RootPolicy.xml";
    private final static String Namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17";

    public PolicyRetreiver() {
        try {
            db = new BDBXmlDatabaseHelper(CONTAINER);
            db.addNamespace("xacml", Namespace);
            db.activateDebugging();

        } catch (Exception ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            //return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("Failed to load Barkeley DB XML Database.\nDetails: " + ex.getMessage()));
        }
    }

    @Override
    public Node getRootPolicy(int depth) {

        Node result = null;

        try {
            /**
             * Query to get the root node. Can only be a PolicySet or Policy to
             * be valid
             */
            XmlResults query = db.query("collection()/xacml:PolicySet|collection()/xacml:Policy");

            /**
             * If has no result, then it means that there is no root policy
             * created
             */
            if (!query.hasNext()) {

                /**
                 * Load a basic policySet sample from package
                 */
                //InputStream in = getClass().getResourceAsStream("/pt/fg/xacml/resources/policy.xml");
                //For testing purposes
                InputStream in = getClass().getResourceAsStream("/pt/fg/xacml/resources/policyset.xml");

                Scanner s = new Scanner(in);
                String policy = s.nextLine();
                while (s.hasNextLine()) {
                    policy += s.nextLine();
                }

                //Create a XmlDocument from the string
                XmlDocument doc = db.createDocument(rootXmlDoc, policy);

                //Inserts document into database
                db.putDocument(doc);

                //Trying again to get a root policy
                return getRootPolicy(depth);
            }

            /**
             * Inject into a Node implementation
             */
            //result = new XmlValueNode(query.next());
            result = new XACMLNodeAdaptor(query.next(), 1);

        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Node getPolicyTreeElement(String id, int depth) {
        Node result = null;

        try {
            XmlResults query = db.query("collection()//*[@PolicySetId=\"" + id + "\" or PolicyId=\"" + id + "\" or RuleId=\"" + id + "\"]");

            if (!query.hasNext()) {
                /**
                 * Rule doesn't exist
                 */
                return null;
            }


            /**
             * Inject into a Node implementation
             */
            result = new XACMLNodeAdaptor(query.next(), depth);

            /**
             * Namespace has to be declared
             */
            putNamespace(result);

        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Node getPolicySet(String policySetId, int depth) {

        Node result = null;

        try {
            XmlResults query = db.query("collection()//xacml:PolicySet[@PolicySetId=\"" + policySetId + "\"]");

            if (!query.hasNext()) {
                /**
                 * PolicySet doesn't exist
                 */
                return null;
            }


            /**
             * Inject into a Node implementation
             */
            result = new XACMLNodeAdaptor(query.next(), depth);

            /**
             * Namespace has to be set
             */
            putNamespace(result);
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Node getPolicy(String policyId, int depth) {

        Node result = null;

        try {
            XmlResults query = db.query("collection()//xacml:Policy[@PolicyId=\"" + policyId + "\"]");

            if (!query.hasNext()) {
                /**
                 * Policy doesn't exist
                 */
                return null;
            }


            /**
             * Inject into a Node implementation
             */
            result = new XACMLNodeAdaptor(query.next(), depth);

            /**
             * Namespace has to be declared
             */
            putNamespace(result);

        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Node getRule(String ruleId, int depth) {
        Node result = null;

        try {
            XmlResults query = db.query("collection()//xacml:Rule[@RuleId=\"" + ruleId + "\"]");

            if (!query.hasNext()) {
                /**
                 * Rule doesn't exist
                 */
                return null;
            }


            /**
             * Inject into a Node implementation
             */
            result = new XACMLNodeAdaptor(query.next(), depth);

            /**
             * Namespace has to be declared
             */
            putNamespace(result);

        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public OperationResult insertElementIntoPolicySetAsFirst(String policySetId, String element) {
        try {
            db.addNodeAsFirst(element, "collection()//xacml:PolicySet[@PolicySetId=\"" + policySetId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to insert element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult insertElementIntoPolicySetAsLast(String policySetId, String element) {
        try {
            db.addNodeAsLast(element, "collection()//xacml:PolicySet[@PolicySetId=\"" + policySetId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to insert element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult insertElementIntoPolicySetAfterElement(String policySetId, String elementId, String element) {
        try {
            /**
             * Inside policySet there can be PolicySet or Policy elements
             */
            db.addNodeAfter(element, "collection()//xacml:PolicySet[@PolicySetId=\"" + policySetId + "\"]/*[@PolicySetId=\"" + elementId + "\" or @PolicyId=\"" + elementId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to insert element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult insertElementIntoPolicyAsFirst(String policyId, String element) {
        try {
            db.addNodeAsFirst(element, "collection()//xacml:Policy[@PolicyId=\"" + policyId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to insert element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult insertElementIntoPolicyAsLast(String policyId, String element) {
        try {
            db.addNodeAsLast(element, "collection()//xacml:Policy[@PolicyId=\"" + policyId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to insert element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult insertElementIntoPolicyAfterElement(String policyId, String elementId, String element) {
        try {
            /**
             * Inside policySet there can be Rule elements
             */
            db.addNodeAfter(element, "collection()//xacml:Policy[@PolicyId=\"" + policyId + "\"]/xacml:Rule[@RuleId=\"" + elementId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to insert element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult removeElementFromPolicyTreeElement(String elementId, String elementName) {
        try {
            db.removeNode("collection()//*[@PolicySetId=\"" + elementId + "\" or PolicyId=\"" + elementId + "\" or RuleId=\"" + elementId + "\"]/" + elementName);
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to remove element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult removePolicySet(String policySetId) {
        try {
            db.removeNode("collection()//xacml:PolicySet[@PolicySetId=\"" + policySetId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to remove element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult removePolicy(String policyId) {
        try {
            db.removeNode("collection()//xacml:Policy[@PolicyId=\"" + policyId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to remove element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public OperationResult removeRule(String ruleId) {
        try {
            db.removeNode("collection()//xacml:Rule[@RuleId=\"" + ruleId + "\"]");
        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("It was not possible to remove element. Possible reasons: " + ex.getMessage()));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    public Boolean policySetExist(String policySetId) {
        boolean result = false;
        try {
            XmlResults query = db.query("count(collection()//xacml:PolicySet[@PolicySetId=\"" + policySetId + "\"])");

            XmlValue value = query.peek();

            if(value!=null){
                /**
                 * If at least one exists, returns true
                 */
                result = (value.asNumber()>0);
            }

        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Boolean policyExist(String policyId) {
        boolean result = false;
        try {
            XmlResults query = db.query("count(collection()//xacml:Policy[@PolicyId=\"" + policyId + "\"])");
            
            XmlValue value = query.peek();

            if(value!=null){
                /**
                 * If at least one exists, returns true
                 */
                result = (value.asNumber()>0);
            }

        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public Boolean ruleExist(String ruleId) {
        boolean result = false;
        try {
            XmlResults query = db.query("count(collection()//xacml:Rule[@RuleId=\"" + ruleId + "\"])");

            XmlValue value = query.peek();

            if(value!=null){
                /**
                 * If at least one exists, returns true
                 */
                result = (value.asNumber()>0);
            }

        } catch (XmlException ex) {
            Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }



    private void putNamespace(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        boolean hasNameSpace = false;
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals("xmlns:xacml")) {
                hasNameSpace = true;
            }
        }

        if (!hasNameSpace) {

            try {
                DocumentBuilder dbldr = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document x = dbldr.newDocument();
                Attr a = x.createAttribute("xmlns:xacml");
                a.setValue(Namespace);

                //Put into element the new attribute xmlns:xacml
                attributes.setNamedItem(a);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(PolicyRetreiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
