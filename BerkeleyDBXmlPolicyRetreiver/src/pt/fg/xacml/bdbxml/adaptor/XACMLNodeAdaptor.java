//=============================================================================
// Brief   : Node implementation for Berkeley DBXML
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

package pt.fg.xacml.bdbxml.adaptor;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * Class XACMLNodeAdaptor
 * @see Node
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 3/Mai/2011
 */
public class XACMLNodeAdaptor implements Node {

    /**
     * Node name
     */
    private String nodeName;
    /**
     * Node value
     */
    private String nodeValue;
    /**
     * Node type
     */
    private short nodeType;
    /**
     * Text content
     */
    private String textContent;
    /**
     * Attributes
     */
    private XACMLNamedNodeMapAdaptor attributes;//private Map<String, String> attributes;
    /**
     * Parent node
     */
    private XACMLNodeAdaptor parent;
    /**
     * List of child nodes
     */
    private List<Node> childs;
    /*
     * Next sibling
     */
    private XACMLNodeAdaptor nextSibling;
    /**
     * Previous sibling
     */
    private XACMLNodeAdaptor previousSibling;

    /**
     * Magic number -> unlimited depth to 'dig' in nodes (-1)
     */
    public final static int UNLIMITED_DEPTH = -1;

    public XACMLNodeAdaptor(XmlValue xmlValue) throws XmlException{
        /**
         * XmlValue node -> xmlValue
         * XACMLNodeAdaptor parent -> null
         * int depth -> no limit (-1)
         */
        this(xmlValue, null, UNLIMITED_DEPTH);
    }

    public XACMLNodeAdaptor(XmlValue xmlValue, int depth) throws XmlException {
        this(xmlValue, null, depth);
    }

    public XACMLNodeAdaptor(XmlValue xmlValue, XACMLNodeAdaptor parent, int depth) throws XmlException {
        this.nodeName = xmlValue.getNodeName();
        this.nodeValue = xmlValue.getNodeValue();
        this.nodeType = xmlValue.getNodeType();
        this.parent = parent;
        this.nextSibling = null;
        this.previousSibling = null;

        debug("Creating node"); //Debug needs NodeName and parent to work

        setAttributes(xmlValue);

        getChilds(xmlValue, depth);

    }

    private void setAttributes(XmlValue xmlValue) throws XmlException {
        //Attributes
        debug("Setting attributes");
        XmlResults nodeAtt = xmlValue.getAttributes();
        attributes = new XACMLNamedNodeMapAdaptor();
        while(nodeAtt.hasNext()){
            XmlValue v = nodeAtt.next();
            debug("[Attribute] " + v.getNodeName() + ": " + v.getNodeValue());
            attributes.setNamedItem(new XACMLNodeAdaptor(v,-1));
        }
        /*
        attributes = new HashMap<String, String>();
        XmlResults nodeAttributes = xmlValue.getAttributes();
        while (nodeAttributes.hasNext()) {
            //Node attribute = new XACMLNodeAdaptor(nodeAttributes.next(), 0);
            XmlValue v = nodeAttributes.next();
            //debug("[Attribute] " + v.getNodeName() + ": " + v.getNodeValue());
            attributes.put(v.getNodeName(), v.getNodeValue());
        }
         */
    }

    private void getChilds(XmlValue xmlValue, int depth) throws XmlException {
        /**
         * Used to connect siblings
         */
        XACMLNodeAdaptor newChild = null;
        XACMLNodeAdaptor oldChild = null;
        //debug("Creating childs");
        //Childs
        childs = new ArrayList<Node>();
        XmlValue nodeChild = xmlValue.getFirstChild();
        while (nodeChild != null && nodeChild.isNode()) {
            /**
             * Depending on element name and depth, different approach is used:
             *
             * -> PolicySet/Policy/Rule - get as deep as defined by depth
             * -> When depth is -1, no depth limit exists
             *
             */
            switch (nodeChild.getNodeType()) {
                case XmlValue.ELEMENT_NODE:
                    //debug("Adding child");
                    if (depth == UNLIMITED_DEPTH
                            || !(nodeChild.getNodeName().equals("xacml:PolicySet")
                            || nodeChild.getNodeName().equals("xacml:Policy")
                            || nodeChild.getNodeName().equals("xacml:Rule"))) {

                        newChild = new XACMLNodeAdaptor(nodeChild, this, UNLIMITED_DEPTH);
                        childs.add(newChild);

                        if (oldChild != null) {
                            oldChild.setNextSibling(newChild);
                            newChild.setPreviousSibling(oldChild);
                        }
                        oldChild = newChild;

                    } else if (depth > 0) {

                        newChild = new XACMLNodeAdaptor(nodeChild, this, depth - 1);
                        childs.add(newChild);

                        if (oldChild != null) {
                            oldChild.setNextSibling(newChild);
                            newChild.setPreviousSibling(oldChild);
                        }
                        oldChild = newChild;
                    }

                    nodeChild = nodeChild.getNextSibling();
                    break;
                case XmlValue.CDATA_SECTION_NODE:
                case XmlValue.TEXT_NODE:
                    //Check if there are only empty spaces
                    if (nodeChild.getNodeValue().replaceAll(" ", "").length() > 0) {
                        debug("Adding text value: " + nodeChild.getNodeValue());
                        childs.add(new XACMLNodeAdaptor(nodeChild, this, 0));
                    }
                    nodeChild = nodeChild.getNextSibling();
                    break;
                default:
                    //The end
                    nodeChild = null;
            }
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getNodeValue() throws DOMException {
        return nodeValue;
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        this.nodeValue = nodeValue;
    }

    public short getNodeType() {
        return nodeType;
    }

    public Node getParentNode() {
        return parent;
    }

    public NodeList getChildNodes() {
        return null;
    }

    public Node getFirstChild() {
        if (childs.isEmpty()) {
            return null;
        }
        return childs.get(0);
    }

    public Node getLastChild() {
        return childs.get(childs.size());
    }

    public void setPreviousSibling(XACMLNodeAdaptor node) {
        this.previousSibling = node;
    }

    public Node getPreviousSibling() {
        return this.previousSibling;
    }

    public void setNextSibling(XACMLNodeAdaptor node) {
        this.nextSibling = node;
    }

    public Node getNextSibling() {
        return this.nextSibling;
    }

    public NamedNodeMap getAttributes() {
        return attributes;
    }

    public Document getOwnerDocument() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node removeChild(Node oldChild) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node appendChild(Node newChild) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasChildNodes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node cloneNode(boolean deep) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void normalize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSupported(String feature, String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getNamespaceURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPrefix() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPrefix(String prefix) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getLocalName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasAttributes() {
        if(attributes==null)
            return false;

        return (attributes.getLength()>0);
    }

    public String getBaseURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTextContent() throws DOMException {
        if(textContent==null){
            //throw new DOMException(DOMException., nodeName);
        }

        return textContent;
    }

    public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSameNode(Node other) {
        if(!getNodeName().equals(other.getNodeName())){
            return false;
        }
        
        if(getNodeType()!=other.getNodeType()){
            return false;
        }

        if(!getNodeValue().equals(other.getNodeValue())){
            return false;
        }

        

        return true;
    }

    public String lookupPrefix(String namespaceURI) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String lookupNamespaceURI(String prefix) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isEqualNode(Node arg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getFeature(String feature, String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getUserData(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    private PrintStream debugOut = System.out;
    private boolean enableDebug = true;

    public void setDebugging(boolean setting) {
        enableDebug = setting;
    }

    public void setDebugStream(PrintStream os) {
        debugOut = os;
    }

    private void debug(String in) {
        if (enableDebug) {
            debugOut.println("[NodeAdaptor:" + getTreeDescription() + "] " + in);
        }
    }

    public String getTreeDescription() {
        if (parent == null) {
            return nodeName;
        } else {
            return parent.getTreeDescription() + " > " + nodeName;
        }
    }
}
