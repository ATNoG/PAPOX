//=============================================================================
// Brief   : Berkeley's DB XML helper
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

import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.VerboseConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlInputStream;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlQueryContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlUpdateContext;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Module built to deal with Oracle Barkeley's XML native Database as an
 * abstraction layer.
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 29/Mai/2011
 */
public class BDBXmlDatabaseHelper {

    /*
     * For debugging purposes.
     */
    /** Defines verbose in BDB XML database component */
    private boolean dbxmlverbose = false;
    /** Defines debugging of this class */
    private boolean dbhelperdebug = false;
    /** PrintStream used to output debug messages */
    private PrintStream debugStream = System.out;
    /** Identifier to be put in debug messages */
    public static final String DEBUGCLASSNAME = "[BDBXmlDatabaseHelper] ";
    /*
     * Constants
     */
    /** Default path for environment */
    private static final String DEFAULTENVHOME = "data";
    /** Default container to be used */
    private static final String DEFAULTCONTAINER = "pt.fg.dbxml.container.dbxml";
    /*
     * Object variables
     */
    /** dbxml Environment configuration */
    private EnvironmentConfig environmentConfig;
    /** dbxml Environment */
    private Environment environment;
    /** dbxml XML manager configuration */
    private XmlManagerConfig xmlManagerConfig;
    /** dbxml XML manager */
    private XmlManager xmlManager;
    /** dbxml XML Container */
    private XmlContainer xmlContainer;
    /** Container to be used */
    private String containerName;
    /**
     * Map of prefixes and associated namespaces (used to query a document with qualified namespace).<br/>
     * Map<Prefix, Namespace>
     */
    private Map<String, String> namespaces;

    /**
     * Constructor with all the parameters. May be used to configure without
     * the default values.<br/>When parameters are null, default values are
     * used.<br/>
     * Default values are:
     * <ul>
     *  <li>
     *      <b>EnvironmentPath</b>: "./data"<br/>
     *  </li>
     *  <li>
     *      <u>EnvironmentConfig</u><br/>
     *      <ul>
     *          <li><b>AllowCreate</b>: true</li>
     *          <li><b>InitializeLogging</b>: true</li>
     *          <li><b>InitializeLocking</b>: true</li>
     *          <li><b>InitializeCache</b>: true</li>
     *          <li><b>Transactional</b>: true</li>
     *          <li><b>RunRecovery</b>: true</li>
     *      </ul>
     *  </li>
     *  <li>
     *      <u>XmlManagerConfig</u><br/>
     *      <ul>
     *          <li><b>AdoptEnvironment</b>: true</li>
     *          <li><b>AllowAutoOpen</b>: true</li>
     *          <li><b>AllowExternalAccess</b>: true</li>
     *      </ul>
     *  </li>
     *  <li>
     *      <b>ContainerName</b>: "pt.fg.dbxml.container.dbxml"<br/>
     *  </li>
     * </ul>
     *
     * @param environmentPath If null, default environment path will be used
     * @param environmentConfig If null, default configurations will be used
     * @param xmlManagerConfig If null, default configurations will be used
     * @param containerName If null, default container name will be used
     * @param containerType Defines if to store nodes or whole documents.
     * Use XmlContainer.NodeContainer or XmlContainer.WholedocContainer
     */
    public BDBXmlDatabaseHelper(String environmentPath,
            EnvironmentConfig environmentConfig,
            XmlManagerConfig xmlManagerConfig,
            String containerName,
            int containerType) throws IOException, DatabaseException {

        /*
         * If the argument <code>environmentPath</code> is null or empty,
         * default environment is used.
         */
        File envHome = new File((environmentPath == null || environmentPath.isEmpty()) ? DEFAULTENVHOME : environmentPath);

        /*
         * Checks if folder for environment already exists. If not, creates a
         * new one.
         * If there are no permissions to read or write, then an
         * <code>IOException</code> is thrown.
         */
        if (!envHome.exists()) {
            debug("Creating folder " + envHome.getAbsolutePath());

            envHome.mkdir();
        } else if (!(envHome.canRead() && envHome.canWrite())) {
            debug("Permission denied. Please check your permissions to read and write in the following path: " + envHome.getPath());
            throw new java.io.IOException("Permission denied. Please check your permissions to read and write in the following path: " + envHome.getPath());
        }

        /*
         * If argument <code>environmentConfig</code> will be null, then a
         * new <code>EnvironmentConfig</code> will be created with default
         * values.
         */
        if (environmentConfig == null) {
            debug("Creating default environment");
            //Creates a default environment
            this.environmentConfig = new EnvironmentConfig();
            this.environmentConfig.setAllowCreate(true);
            this.environmentConfig.setInitializeLogging(true);
            this.environmentConfig.setInitializeLocking(true);
            this.environmentConfig.setInitializeCache(true);
            this.environmentConfig.setTransactional(true);
            this.environmentConfig.setRunRecovery(true);
        } else {
            debug("Setting defined environment");
            this.environmentConfig = environmentConfig;
        }

        /*
         * Defines if database component will be running in verbose mode. With
         * verbose mode, more information will be shown on the screen.
         */
        this.environmentConfig.setVerbose(VerboseConfig.FILEOPS_ALL, dbxmlverbose);

        /*
         * Environment is created with the previsously created <code>envHome</code>
         * and <code>environmentConfig</code> objects
         */
        environment = new Environment(envHome, this.environmentConfig);

        /*
         * Configurations for xml manager. If <code>xmlManagerConfig</code>
         * parameter is null, then it will create default configurations.
         */
        if (xmlManagerConfig == null) {
            debug("Creating default xml manager configuration");
            //Creates a xml manager config with default values
            this.xmlManagerConfig = new XmlManagerConfig();
            this.xmlManagerConfig.setAdoptEnvironment(true);
            this.xmlManagerConfig.setAllowAutoOpen(true);
            this.xmlManagerConfig.setAllowExternalAccess(true);
        } else {
            debug("Setting defined xml manager configuration");
            this.xmlManagerConfig = xmlManagerConfig;
        }

        this.xmlManager = new XmlManager(this.environment, this.xmlManagerConfig);
        this.xmlManager.setDefaultContainerType(containerType);

        if (containerName == null || containerName.isEmpty()) {
            debug("Defining container: " + DEFAULTCONTAINER);
            this.containerName = DEFAULTCONTAINER;
        } else {
            debug("Defining container: " + containerName);
            this.containerName = containerName;
        }

        XmlContainerConfig xcc = new XmlContainerConfig();
        xcc.setTransactional(true);

        if (xmlManager.existsContainer(this.containerName) == 0) {
            /*
             * If the container does not exist, it is created
             */
            debug("Container not found: " + this.containerName + ". Creating it...");
            xmlContainer = xmlManager.createContainer(this.containerName, xcc);

            // If container is not closed then it will not be indexed
            xmlContainer.close();

        }
        debug("Opening container: " + this.containerName);
        xmlContainer = xmlManager.openContainer(this.containerName, xcc);

        /*
         * Initializes mapping of namespaces
         */
        namespaces = new HashMap<String, String>();
    }

    /**
     * Constructor with only default values.
     *
     * @throws IOException
     * @throws DatabaseException
     */
    public BDBXmlDatabaseHelper() throws IOException, DatabaseException {
        this(null, null, null, null, XmlContainer.NodeContainer);
    }

    /**
     * Constructor with default values appart from container name.
     *
     * @param container Container name
     * @throws IOException
     * @throws DatabaseException
     */
    public BDBXmlDatabaseHelper(String container) throws IOException, DatabaseException {
        this(null, null, null, container, XmlContainer.NodeContainer);
    }

    /**
     * Inserts a document from a file, with a unique name, into the container.
     *
     * @param uniqueName Unique name
     * @param fileName Path to xml file
     * @return
     */
    public boolean putDocument(String uniqueName, String fileName) {
        try {
            debug("Putting document: " + uniqueName + " (" + fileName + ")");
            //Need an update context for the put.
            XmlUpdateContext theContext = xmlManager.createUpdateContext();
            XmlInputStream xStream = xmlManager.createLocalFileInputStream(fileName);
            xmlContainer.putDocument(uniqueName, xStream, theContext);
            xmlContainer.sync();
            return true;
        } catch (XmlException ex) {
            debug("Error putting document... " + ex.getMessage());
        }
        // If this line is reached, it means there was an exception
        return false;
    }

    /**
     * Inserts XmlDocument into the container.
     * 
     * @param xmlDoc
     * @return
     */
    public boolean putDocument(XmlDocument xmlDoc) {
        try {
            debug("Putting document: " + xmlDoc.getName());

            xmlContainer.putDocument(xmlDoc);

            xmlContainer.sync();
            return true;
        } catch (XmlException ex) {
            debug("Error occured when putting document: " + ex.getMessage());
        }
        // If this line is reached, it means there was an exception
        return false;
    }

    /**
     * Removes a document from the container
     *
     * @param docName Document name to be removed
     * @return true if the document was removed
     */
    public boolean removeDocument(String docName) {
        try {
            debug("Removing document: " + docName);
            XmlUpdateContext updateCtx = xmlManager.createUpdateContext();
            xmlContainer.deleteDocument(docName, updateCtx);
            xmlContainer.sync();
        } catch (XmlException ex) {
            debug("Error occured when removing document: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Add a node into another node. If more than one node found in the
     * document, all of them will be used.
     *
     * @param nodes Node to be added
     * @param where XPath expression representing the destiny node.
     * @return
     * @throws XmlException
     */
    public XmlResults addNode(String nodes, String where) throws XmlException {
        nodes = prepareArguments(nodes);
        where = prepareArguments(where);
        String query = "for $i in " + where + " return insert nodes " + nodes + " into $i";
        return query(query);
    }

    /**
     * Add a node into another node. If more than one node found in the
     * document, all of them will be used.
     *
     * @param nodes Node to be added
     * @param where XPath expression representing the destiny node.
     * @return
     * @throws XmlException
     */
    public XmlResults addNodeAsFirst(String nodes, String where) throws XmlException {
        nodes = prepareArguments(nodes);
        where = prepareArguments(where);
        String query = "for $i in " + where + " return insert nodes " + nodes + " as first into $i";
        return query(query);
    }
    
    /**
     * Add a node into another node. If more than one node found in the
     * document, all of them will be used.
     *
     * @param nodes Node to be added
     * @param where XPath expression representing the destiny node.
     * @return
     * @throws XmlException
     */
    public XmlResults addNodeAsLast(String nodes, String where) throws XmlException {
        nodes = prepareArguments(nodes);
        where = prepareArguments(where);
        String query = "for $i in " + where + " return insert nodes " + nodes + " as last into $i";
        return query(query);
    }

    /**
     * Add a node after another node. If more than one node found in the
     * document, all of them will be used.
     *
     * @param nodes Nodes to be added
     * @param which XPath expression identifying the node in the document where
     * nodes will be added after
     * @return
     * @throws XmlException
     */
    public XmlResults addNodeAfter(String nodes, String which) throws XmlException {
        nodes = prepareArguments(nodes);
        which = prepareArguments(which);
        String query = "for $i in " + which + " return insert nodes " + nodes + " after $i";
        return query(query);
    }

    /**
     * Replace the content of a node with new content. If more than one node is
     * found, all of them will be replaced.
     *
     * @param newNodes New nodes
     * @param where Old nodes (XPath expression to find it in the document)
     * @return
     * @throws XmlException
     */
    public XmlResults updateNode(String newNodes, String where) throws XmlException {
        newNodes = prepareArguments(newNodes);
        where = prepareArguments(where);
        String query = "for $i in " + where + " return replace node $i with " + newNodes;
        return query(query);
    }

    /**
     * Removes nodes from the document. If more than one is found, all of them
     * will be deleted.
     *
     * @param which XPath expression to identify the node in the document
     * @return
     * @throws XmlException
     */
    public XmlResults removeNode(String which) throws XmlException {
        which = prepareArguments(which);
        String query = "delete nodes " + which;
        return query(query);
    }

    /**
     * Creates a XmlDocument from a string. Useful if xml documents are not
     * stored in files.
     *
     * @param name Unique name
     * @param content xml document content
     * @return XmlDocument
     * @throws XmlException
     */
    public XmlDocument createDocument(String name, String content) throws XmlException {
        XmlDocument xd = xmlManager.createDocument();
        xd.setContent(content);
        xd.setName(content);
        return xd;
    }

    /**
     * Map a new namespace with specified prefix as a key.
     * @param prefix Prefix
     * @param namespace Namespace
     */
    public void addNamespace(String prefix, String namespace) {
        namespaces.put(prefix, namespace);
    }

    /**
     * Remove namespace by its prefix.
     *
     * @param prefix Prefix
     */
    public void removeNamespace(String prefix) {
        namespaces.remove(prefix);
    }

    /**
     * Removes all namespaces.
     */
    public void clearNamespaces() {
        namespaces.clear();
    }

    /**
     * Executes a XQuery expression. Use <code>addNamespace</code> to assign
     * some prefix with required namespace.
     *
     * @param XQuery XQuery expression
     * @return Result of XQuery
     * @throws XmlException
     */
    public synchronized XmlResults query(String xQuery) throws XmlException {
        XmlResults result;
        XmlQueryExpression xqe;

        debug("Preparing XQuery: " + xQuery);
        XmlQueryContext xqc = xmlManager.createQueryContext(XmlQueryContext.Eager);

        for (String prefix : namespaces.keySet()) {
            xqc.setNamespace(prefix, namespaces.get(prefix));
            debug("Loaded namespace " + namespaces.get(prefix) + " with prefix " + prefix);
        }

        /**
         * Avoid using 'collection("xpto.pt.context")' on each query
         */
        xqc.setDefaultCollection(containerName);

        xqe = xmlManager.prepare(xQuery, xqc);

        debug("Executing query...");
        result = xqe.execute(xqc);


        return result;
    }

    
    /**
     * Get environment configuration of this object's db instance
     *
     * @return the environmentConfig
     */
    public EnvironmentConfig getEnvironmentConfig() {
        return environmentConfig;
    }

    /**
     * Get environment of this object's db instance
     *
     * @return the environment
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Get xml manager configuration of this object's db instance
     *
     * @return the xmlManagerConfig
     */
    public XmlManagerConfig getXmlManagerConfig() {
        return xmlManagerConfig;
    }

    /**
     * Get xml manager of this object's db instance
     *
     * @return the xmlManager
     */
    public XmlManager getXmlManager() {
        return xmlManager;
    }

    /**
     * Get container used by this object's db instance
     * @return the xmlContainer
     */
    public XmlContainer getXmlContainer() {
        return xmlContainer;
    }

    /**
     * Closes container.
     *
     * @throws XmlException
     */
    public void close() throws XmlException {
        if (xmlContainer != null) {
            xmlContainer.close();
        }
    }

    /**
     * Activates debugging mode. Messages will be printed with <code>PrintStream</code>
     * object.
     *
     * @param stream
     */
    public void activateDebugging(PrintStream stream) {
        debugStream = stream;
        dbhelperdebug = true;
    }

    /**
     * Activates debugging mode. Messages will be printed with <code>System.out</code>
     */
    public void activateDebugging() {
        //Use default System.out as PrintStream
        activateDebugging(System.out);
    }

    /**
     * Disable debugging mode.
     */
    public void desactivateDebugging() {
        dbhelperdebug = false;
    }

    /**
     * Prints message to defined stream.
     *
     * @param message
     */
    private void debug(String message) {
        if (dbhelperdebug) {
            debugStream.println(DEBUGCLASSNAME + message);
        }
    }

    /**
     * Replaces any " character with '
     */
    private String prepareArguments(String in) {
        return in.replace("'", "\"");
    }
}
