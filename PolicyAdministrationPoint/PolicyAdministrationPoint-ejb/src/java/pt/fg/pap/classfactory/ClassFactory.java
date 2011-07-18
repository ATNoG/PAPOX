//=============================================================================
// Brief   : Class Factories Factory
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
package pt.fg.pap.classfactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import pt.fg.jcl.exceptions.InvalidPathToJarException;
import pt.fg.jcl.exceptions.NotReadableException;
import pt.fg.pap.classfactory.factories.MapperFactory;
import pt.fg.pap.classfactory.factories.PolicyRetreiverFactory;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pt.fg.pap.classfactory.factories.InfoRetreiverFactory;
import pt.fg.pap.exceptions.InvalidClassFactoryException;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 01-03-2011
 */
public abstract class ClassFactory {


    public static final String JAXP_SCHEMA_LANGUAGE =
            "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    
    public static final String W3C_XML_SCHEMA =
            "http://www.w3.org/2001/XMLSchema";

    public static final String JAXP_SCHEMA_SOURCE =
            "http://java.sun.com/xml/jaxp/properties/schemaSource";
    /**
     * Identifiers for the implemented Factories
     */
    public static final int InfoRetreiverFactory = 0;
    public static final int PolicyRetreiverFactory = 1;
    public static final int MapperFactory = 2;
    public static final int InfoRetreiverFactoryWeb = 3;
    public static final int PolicyRetreiverFactoryWeb = 4;
    public static final int MapperFactoryWeb = 5;
    
    /**
     * Folder where file modules.xml will be placed by default.
     * Sub-folders will have .jar files for each factory.
     */
    //private static final String path = "." + File.separator + "modules" + File.separator;
    //C:\modules

    private static final String path = "." + File.separator + "modules" + File.separator;// "C:" + File.separator + "modules" + File.separator;
    /**
     * Path to modules.xml file
     */
    private static String modules_xml = path + "modules.xml";

    /**
     * Path to modules.xml file in WEB
     */
    private static String modules_xml_web = "http://localhost:8080/modules/modules.xml";
    
    /**
     * Path to schema for modules.xml file
     */
    private static String schema_modules_xml = path + "schema.xsd";
    /**
     * Each factory will be stored in a map, with the identifier as the key.
     */
    private static Map<Integer, ClassFactory> classFactory = new HashMap<Integer, ClassFactory>();

    /*
     * For debugging purposes.
     */
    /** Defines debugging of this class */
    protected static boolean debug = true;
    /** PrintStream used to output debug messages */
    protected static PrintStream debugStream = System.out;
    /** Identifier to be put in debug messages */
    public static final String DEBUGCLASSNAME = "[ClassFactory] ";

    /**
     * Default parent ClassLoader to use.
     */
    private static ClassLoader parentcl = ClassLoader.getSystemClassLoader();

    public static void setParentClassLoader(ClassLoader parent){
        parentcl = parent;
    }

    /**
     * This method is used to retreive a factory according with type.
     * 
     * @param type Identifier for the desired Factory
     * @return ClassFactory returns the desired Factory
     * @throws Exception in case of wrong identifier
     */
    public static ClassFactory getInstance(int type) throws InvalidPathToJarException, NotReadableException, InvalidClassFactoryException {

        //Check if there is an instance of the requested Factory and returns it
        if (classFactory.containsKey(type)) {
            return classFactory.get(type);
        }

        // Reaching this line means that the Factory was not created yet
        Set<AvailableClass> cfg;

        switch (type) {
            case InfoRetreiverFactory:
                cfg = loadConfig("InfoRetreiver");
                classFactory.put(type, new InfoRetreiverFactory(cfg, false, parentcl));
                break;

            case InfoRetreiverFactoryWeb:
                cfg = loadConfig("InfoRetreiver");
                classFactory.put(type, new InfoRetreiverFactory(cfg, true, parentcl));
                break;

            case PolicyRetreiverFactory:
                cfg = loadConfig("PolicyRetreiver");
                classFactory.put(type, new PolicyRetreiverFactory(cfg, false, parentcl));
                break;
                
            case PolicyRetreiverFactoryWeb:
                cfg = loadConfigFromWeb("PolicyRetreiver");
                classFactory.put(type, new PolicyRetreiverFactory(cfg, true, parentcl));
                break;

            case MapperFactory:
                cfg = loadConfig("Mapper");
                classFactory.put(type, new MapperFactory(cfg));
                break;

            default:
                throw new InvalidClassFactoryException("Invalid type");
        }

        return getInstance(type);
    }

    /**
     * Retreives all the classes configured in modules.xml
     * @return Set of class names from modules.xml
     */
    private static Set<AvailableClass> loadConfig(String classType) {

        File modules;
        DocumentBuilder db;
        DocumentBuilderFactory dbf;

        HashSet<AvailableClass> ac = new HashSet<AvailableClass>();

        try {
            debug("Loading modules from " + new File(path).getAbsolutePath());

            /**
             * Opens modules.xml file
             */
            modules = new File(modules_xml);

            dbf = DocumentBuilderFactory.newInstance();

            // Doesn't read comments in xml file
            dbf.setIgnoringComments(true);


            dbf.setNamespaceAware(true);
            dbf.setValidating(true);
            dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            dbf.setAttribute(JAXP_SCHEMA_SOURCE, new File(schema_modules_xml));

            db = dbf.newDocumentBuilder();

            Document doc = db.parse(modules);

            // Get all desired elements
            //NodeList nodes = doc.getDocumentElement().getElementsByTagNameNS(doc.getNamespaceURI(),classType);
            NodeList nodes = doc.getDocumentElement().getElementsByTagName(classType);

            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);

                // Get class name of each element
                String className = n.getAttributes().getNamedItem("name").getNodeValue();
                // Get description of each element
                String description = n.getTextContent();

                ac.add(new AvailableClass(className, description));
            }
        } catch (SAXException ex) {
            Logger.getLogger(ClassFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ClassFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (AvailableClass c : ac) {
            debug("Available class: " + c.getClassName());
            System.out.println(c.getClassName());
        }

        return ac;
    }

    /**
     * Retreives all the classes configured in modules.xml from WEB
     * @return Set of class names from modules.xml
     */
    private static Set<AvailableClass> loadConfigFromWeb(String classType) {

        InputStream modules;
        DocumentBuilder db;
        DocumentBuilderFactory dbf;

        HashSet<AvailableClass> ac = new HashSet<AvailableClass>();

        try {
            debug("Downloading modules.xml from web: " + modules_xml_web);

            /**
             * Opens modules.xml file
             */
            modules = new URL(modules_xml_web).openStream();

            dbf = DocumentBuilderFactory.newInstance();

            // Doesn't read comments in xml file
            dbf.setIgnoringComments(true);

            dbf.setNamespaceAware(true);
            dbf.setValidating(true);
            dbf.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            dbf.setAttribute(JAXP_SCHEMA_SOURCE, new File(schema_modules_xml));

            db = dbf.newDocumentBuilder();

            Document doc = db.parse(modules);

            // Get all desired elements
            //NodeList nodes = doc.getDocumentElement().getElementsByTagNameNS(doc.getNamespaceURI(),classType);
            NodeList nodes = doc.getDocumentElement().getElementsByTagName(classType);
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                NamedNodeMap attributes = n.getAttributes();

                // Get class name of each element
                String className = n.getAttributes().getNamedItem("name").getNodeValue();
                debug("Loading " + className);

                // If no location is set, then its not good for this factory
                if (attributes.getNamedItem("location") != null) {

                    debug("from " + attributes.getNamedItem("location"));

                    // Gets the location to look for
                    String location = attributes.getNamedItem("location").getNodeValue();

                    // Get description of each element
                    String description = n.getTextContent();

                    ac.add(new AvailableClass(className, description, location));
                }
            }
        } catch (SAXException ex) {
            Logger.getLogger(ClassFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClassFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(ClassFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (AvailableClass c : ac) {
            debug("Available class: " + c.getClassName());
        }

        return ac;
    }

    /**
     * Abstract method to get an instance of a specified class.
     *
     * @param className Class to instantiate
     * @return Object asked
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public abstract Object get(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

    /**
     * Retreives all supported and valid classes described in the modules.xml
     * @return
     */
    public abstract Set<AvailableClass> getAllSupportedClasses();

    protected String getPath() {
        return path;
    }

    /**
     * Activates debugging mode. Messages will be printed with <code>PrintStream</code>
     * object.
     *
     * @param stream
     */
    public static void activateDebugging(PrintStream stream) {
        debugStream = stream;
        debug = true;
    }

    /**
     * Activates debugging mode. Messages will be printed with <code>System.out</code>
     */
    public static void activateDebugging() {
        //Use default System.out as PrintStream
        activateDebugging(System.out);
    }

    /**
     * Disable debugging mode.
     */
    public static void desactivateDebugging() {
        debug = false;
    }

    /**
     * Prints message to defined stream.
     *
     * @param message
     */
    protected static void debug(String message) {
        if (debug) {
            debugStream.println(DEBUGCLASSNAME + message);
        }
    }
}
