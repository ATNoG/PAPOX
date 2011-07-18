//=============================================================================
// Brief   : Factory for Information Retreiver implementations
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
package pt.fg.pap.classfactory.factories;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.fg.pap.classfactory.AvailableClass;
import pt.fg.pap.classfactory.ClassFactory;
import pt.fg.jcl.JClassLoader;
import pt.fg.jcl.exceptions.InvalidPathToJarException;
import pt.fg.jcl.exceptions.NotReadableException;
import pt.fg.pap.interfaces.IInfoRetreiver;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 01-03-2011
 */
public final class InfoRetreiverFactory extends ClassFactory {

    /**
     * Used to define a context.
     */
    private static final String CONTEXT = "InfoRetreiverFactory";
    /**
     * Inside module folder, where modules.xml is, there should be a
     * inforetreiver folder to search for IInfoRetreiver implementation.
     *<br/>
     * Currently its only used to describe the path, doesn't search jars in it.
     * <code>JClassLoader</code> already searches recursively in folders.
     */
    private static final String PATH = "inforetreiver";
    /**
     * JClassLoader - component that loads all the .jar files from a folder
     */
    private JClassLoader jcl;
    /**
     * Used later to give a description to the UI about the class
     */
    private Map<String, AvailableClass> classes;
    /**
     * Bad classes are classes that exist but are not IInfoRetreiver
     * implementations.
     */
    private List<String> badClasses;
    /**
     * To avoid problems with libs being loaded by different applications,
     * all instances of InfoRetreiverFactory will be single
     */
    private Map<String, IInfoRetreiver> instances;

    public InfoRetreiverFactory(Set<AvailableClass> allclasses, boolean web, ClassLoader parentClassLoader) throws InvalidPathToJarException, NotReadableException {
        classes = new HashMap<String, AvailableClass>();
        badClasses = new ArrayList<String>();
        instances = new HashMap<String, IInfoRetreiver>();

        if (!web) {
            jcl = JClassLoader.getInstance(CONTEXT, getPath(), parentClassLoader);
        } else {
            // Gather URL location from all AvailableClass objects
            List<URL> urls = new ArrayList<URL>();
            for (AvailableClass iclass : allclasses) {
                if (iclass.getLocation() != null) {
                    try {
                        urls.add(new URL(iclass.getLocation()));
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(InfoRetreiverFactory.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            jcl = JClassLoader.getInstance(CONTEXT, urls, parentClassLoader);
        }

        /**
         * Checks, from all the classes, which ones are valid in this context.
         * The ones that are valid, are put into the HashMap
         */
        for (AvailableClass iclass : allclasses) {
            try {
                /**
                 * To check if a class implements an interface without instantiating
                 * it, we can use ClassA.isAssignableFrom(ClassB). Like this
                 * we know that ClassB implements ClassA.
                 */
                Class t;
                t = jcl.getClass(iclass.getClassName());

                //Object t = get(iclass.getClassName());//new InfoRetreiver();
                debug("Loaded class " + iclass.getClassName() + ".");

                if (t == null) {
                    debug(iclass.getClassName() + " is null");
                } else {
                    debug("Instance: " + t.toString());

                    for (Class i : t.getClass().getInterfaces()) {
                        debug("Implements interfaces: " + i.getName());
                    }
                }

                //If there are no exceptions thrown, checks if class is an implementation of the interface
                //if (t instanceof IInfoRetreiver) {
                if (IInfoRetreiver.class.isAssignableFrom(t)) {
                    debug("is instance of IInfoRetreiver");
                    classes.put(iclass.getClassName(), iclass);
                }

                debug("is instance of IInfoRetreiver? " + (IInfoRetreiver.class.isAssignableFrom(t)));

            } catch (Exception ex) {
                //badClasses.add(iclass.getClassName());
                Logger.getLogger(InfoRetreiverFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public IInfoRetreiver get(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        IInfoRetreiver nclass;

        if (instances.containsKey(className)) {
            return instances.get(className);
        }

        if (badClasses.contains(className)) {
            throw new InstantiationException("Invalid class: " + className + ". Its not a InfoRetreiver class.");
        }
        
        if (!classes.containsKey(className)) {
            throw new ClassNotFoundException("Invalid class: " + className + " not found.");
        }

        nclass = (IInfoRetreiver) jcl.loadClass(className);

        if(nclass!=null){
            instances.put(className, nclass);
        }

        return nclass;
    }

    @Override
    public Set<AvailableClass> getAllSupportedClasses() {
        return new HashSet<AvailableClass>(classes.values());
    }

    @Override
    protected String getPath() {
        return super.getPath().concat(PATH);
    }

    /**
     * Prints message to defined stream.
     *
     * @param message
     */
    protected static void debug(String message) {
        if (debug) {
            debugStream.println(DEBUGCLASSNAME + "[InfoRetreiverFactory] " + message);
        }
    }
}
