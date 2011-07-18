//=============================================================================
// Brief   : Iterable Node List implementation
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @see NodeList
 * @see Iterable
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 3/Abr/2011
 */
public class XmlValueNodeList implements NodeList, Iterable{
    List<Node> list;

    public XmlValueNodeList(){
        list = new ArrayList<Node>();
    }

    public XmlValueNodeList(XmlResults nodes) throws XmlException{
        this();
        
        while(nodes.hasNext()){
            list.add(new XmlValueNode(nodes.next()));
        }
    }

    public void add(XmlValueNode node){
        list.add(node);
    }

    public Node item(int index) {
        return list.get(index);
    }

    public int getLength() {
        return list.size();
    }

    public Iterator<Node> iterator() {
        return list.iterator();
    }

}
