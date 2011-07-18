//=============================================================================
// Brief   : Functions and Data Types retreiver
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Information Retreiver Module for PAPOX (Policy Administration Point for
// Oasis XACML)
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

package pt.fg.xacml.infoRetreiver;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import pt.fg.pap.interfaces.IInfoRetreiver;
import pt.fg.pap.interfaces.IMapper;
import pt.fg.pap.operation.OperationResult;
import pt.fg.pap.operation.OperationStatus;
import pt.fg.pap.operation.message.WarningMessage;

/**
 * Class DataTypes
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 23/Mai/2011
 */
public class DataTypes implements IInfoRetreiver {

    // <category <name, data type> >
    private HashMap<String, HashMap<String, DataType>> dataTypes;
    private static URL file;
    private static final String pathToFile = "/pt/fg/xacml/supported/supported.xml";

    private class DataType {

        private String shortName;
        private String name;
        private String description;

        public DataType(Node element) {
            this.shortName = element.getAttributes().getNamedItem("ShortName").getNodeValue();
            Node subelement = element.getFirstChild();
            while (subelement != null) {
                if (subelement.getNodeName().equals("att:Description")) {
                    this.description = subelement.getTextContent();
                } else if (subelement.getNodeName().equals("att:FullName")) {
                    this.name = subelement.getTextContent();
                }
                subelement = subelement.getNextSibling();
            }
        }

        public DataType(String shortName, String name, String description) {
            this.shortName = shortName;
            this.name = name;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public String getName() {
            return name;
        }

        public String getShortName() {
            return shortName;
        }
    }
    // Used to map resources by category
    private IMapper mapper;

    public DataTypes() {
        dataTypes = new HashMap<String, HashMap<String, DataType>>();
        file = this.getClass().getResource(pathToFile);

        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Node doc = db.parse(file.openStream()).getFirstChild();

            //Gets and sets prefix of the namespace
            //doc.setPrefix(doc.lookupPrefix("pt:fg:xacml:config:supported"));

            if (doc.getNodeName().equals("att:All")) {
                Node node = doc.getFirstChild();
                while (node != null) {
                    if (node.getNodeName().equals("att:Category")) {
                        String category = node.getAttributes().getNamedItem("Name").getNodeValue();
                        // Creates a Hash Map per Category
                        dataTypes.put(category, new HashMap<String, DataType>());

                        Node resource = node.getFirstChild();
                        while (resource != null) {
                            if (resource.getNodeName().equals("att:Element")) {
                                //Adds each element to the category
                                DataType d = new DataType(resource);

                                dataTypes.get(category).put(d.getName(), d);

                            }
                            resource = resource.getNextSibling();
                        }
                    }
                    node = node.getNextSibling();
                }
            }

        } catch (SAXException ex) {
            Logger.getLogger(DataTypes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataTypes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(DataTypes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getResourceDescription(String id) {
        //Seeks for the resource id in categories and returns the first one found
        for (String cat : dataTypes.keySet()) {
            if (dataTypes.get(cat).containsKey(id)) {
                return dataTypes.get(cat).get(id).getDescription();
            }
        }
        return null;
    }

    public String getResourceShortName(String id) {
        //Seeks for the resource id in categories and returns the first one found
        for (String cat : dataTypes.keySet()) {
            if (dataTypes.get(cat).containsKey(id)) {
                return dataTypes.get(cat).get(id).getShortName();
            }
        }
        return null;
    }

    public List<String> listResources() {
        ArrayList<String> res = new ArrayList<String>();

        //Gets all resources from all categories
        for (String cat : dataTypes.keySet()) {
            res.addAll(dataTypes.get(cat).keySet());
        }

        return res;
    }

    public List<String> listResources(String category) {
        ArrayList<String> res = new ArrayList<String>();

        if (dataTypes.containsKey(category)) {
            //Gets all resources from the category
            res.addAll(dataTypes.get(category).keySet());
        }
        return res;
    }

    public boolean doesMapping() {
        return true;
    }

    public OperationResult setMapper(IMapper mapper) {
        if (mapper == null) {
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new WarningMessage("Null mapper"));
        }
        this.mapper = mapper;

        //Map elements into categories in mapper object
        for (String cat : dataTypes.keySet()) {

            for (String resource : dataTypes.get(cat).keySet()) {
                mapper.addResourceToCategory(resource, cat);
            }

        }
        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }
}
