//=============================================================================
// Brief   : Node implementation
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

package pt.fg.dbtools.bdbxml.impl;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

/**
 * @see Node
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 3/Abr/2011
 */
public class XmlValueNode implements Node {

    XmlValue node;

    public XmlValueNode(XmlValue node) {
        assert(node!=null);
        this.node = node;
    }

    public String getNodeName() {
        try {
            if (node.isNode()) {
                return node.getNodeName();
            }
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getNodeValue() throws DOMException {
        try {
            if (node.isNode()) {
                return node.getNodeValue();
            }
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public short getNodeType() {
        try {
            if (node.isNode()) {
                return node.getNodeType();
            }
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported.");
    }

    public Node getParentNode() {
        try {
            return new XmlValueNode(node.getParentNode());
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public NodeList getChildNodes() {
        XmlValueNodeList list = new XmlValueNodeList();
        try {
            if (node.isNode()) {
                XmlValue v = node.getFirstChild();
                while (v != null) {
                    list.add(new XmlValueNode(v));
                    v = v.getNextSibling();
                }
            }
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public Node getFirstChild() {
        try {
            if (node.isNode()) {
                XmlValue v = node.getFirstChild();
                if (v != null && v.isNode()) {
                    return new XmlValueNode(v);
                }
            }
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Node getLastChild() {
        try {
            return new XmlValueNode(node.getLastChild());
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Node getPreviousSibling() {
        try {
            if(node.isNode())
                return new XmlValueNode(node.getPreviousSibling());
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Node getNextSibling() {
        try {
            if(node.isNode())
                return new XmlValueNode(node.getNextSibling());
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public NamedNodeMap getAttributes() {
        NamedNodeMap map = new NamedXmlValueNodeMap();

        try {
            XmlResults att = node.getAttributes();
            while (att.hasNext()) {
                Node v = new XmlValueNode(att.next());
                if (v.getNamespaceURI() != null && !v.getNamespaceURI().isEmpty()) {
                    map.setNamedItemNS(v);
                } else {
                    map.setNamedItem(v);
                }
            }
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        return map;
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
        try {
            if (!node.isNode()) {
                return false;
            }

            if(node.getFirstChild()==null || !node.getFirstChild().isNode()){
                return false;
            }
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
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
        try {
            return node.getNamespaceURI();
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported.");
    }

    public String getPrefix() {
        try {
            return node.getPrefix();
        } catch (XmlException ex) {
            Logger.getLogger(XmlValueNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new UnsupportedOperationException("Not supported.");
    }

    public void setPrefix(String prefix) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getLocalName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean hasAttributes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getBaseURI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public short compareDocumentPosition(Node other) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getTextContent() throws DOMException {
        String text = "";

        if (getNodeName().equals("#text")) {
            text = text.concat(getNodeValue());
        }
        
        Node n = getFirstChild();
        while (n != null) {
            text = text.concat(n.getTextContent());
            n = n.getNextSibling();
        }
        
        return text;
    }

    public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSameNode(Node other) {
        throw new UnsupportedOperationException("Not supported yet.");
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
}
