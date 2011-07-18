//=============================================================================
// Brief   : Named Node Map implementation
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

import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @see NamedNodeMap
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 3/Abr/2011
 */
public class NamedXmlValueNodeMap implements NamedNodeMap{

    HashMap<String, Node> map;
    HashMap<String, HashMap<String,Node>> nsmap;
    ArrayList<Node> list;


    public NamedXmlValueNodeMap(){
        map = new HashMap<String, Node>();
        nsmap = new HashMap<String, HashMap<String, Node>>();
        list = new ArrayList<Node>();
    }

    public Node getNamedItem(String name) {
        return map.get(name);
    }

    public Node setNamedItem(Node arg) throws DOMException {
        Node r = map.put(arg.getNodeName(), arg);
        list.add(arg);
        return r;
    }

    public Node removeNamedItem(String name) throws DOMException {
        Node r = map.remove(name);
        list.remove(r);
        return r;
    }

    public Node item(int index) {
        return list.get(index);
    }

    public int getLength() {
        return list.size();
    }

    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return nsmap.get(namespaceURI).get(localName);
    }

    public Node setNamedItemNS(Node arg) throws DOMException {
        if(!nsmap.containsKey(arg.getNamespaceURI())){
            nsmap.put(arg.getNamespaceURI(), new HashMap<String, Node>());
        }
        Node r = nsmap.get(arg.getNamespaceURI()).put(arg.getNodeName(), arg);
        list.add(arg);
        return r;
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        Node r = null;
        if(nsmap.containsKey(namespaceURI)){
            r = nsmap.get(namespaceURI).remove(localName);
            list.remove(r);
            //If namespace has no elements, gets removed
            if(nsmap.get(namespaceURI).isEmpty()){
                nsmap.remove(namespaceURI);
            }
        }
        return r;
    }

}
