//=============================================================================
// Brief   : NamedNodeMap implementation for Berkeley DBXML
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Class XACMLNamedNodeMapAdaptor
 * @see NamedNodeMap
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 15/Mai/2011
 */
public class XACMLNamedNodeMapAdaptor implements NamedNodeMap{

    private Map<String, Node> map;
    private List<Node> nodes;

    public XACMLNamedNodeMapAdaptor(){
        map = new HashMap<String, Node>();
        nodes = new ArrayList<Node>();
    }

    public Node getNamedItem(String name) {
        return map.get(name);
    }

    public Node setNamedItem(Node arg) throws DOMException {
        Node res = map.put(arg.getNodeName(), arg);
        nodes.remove(res);
        nodes.add(arg);
        return res;
    }
    
    public Node removeNamedItem(String name) throws DOMException {
        Node res = map.remove(name);
        if(res!=null)
            nodes.remove(res);
        return res;
    }

    public Node item(int index) {
        return nodes.get(index);
    }

    public int getLength() {
        return nodes.size();
    }

    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
