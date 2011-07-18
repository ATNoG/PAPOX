//=============================================================================
// Brief   : Java class loader implementation
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Java Class Loader
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

package pt.fg.jcl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.fg.jcl.exceptions.InvalidPathToJarException;
import pt.fg.jcl.exceptions.NotReadableException;

/** 
 * Java Class Loader<br/>
 * Java object that loads classes from a specified folder
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 28-02-2011
 *
 */
public final class JClassLoader {

    /**
     * Map with JClassLoader instances. Used to have different JClassLoaders
     * depending on the context.
     */
    private static final Map<String, JClassLoader> Instances = new HashMap<String, JClassLoader>();
    
    /**
     * Max depth is used to limit how deep getJars method can search for jar
     * files in a path.
     */
    private static final int MAX_DEPTH = 3;

    /**
     * Caches classes already loaded
     */
    private static final Map<String, Class> Classes = new HashMap<String, Class>();

    /**
     * Used by URLClassLoader to load modules from a folder <br/>
     * URLClassLoader uses an array of URL's to load jars, so this class path
     * is only used to reference a folder where jars are located, then getJars
     * method searches recursively in that path and retreives URL's with
     * paths to jar files.
     */
    private File classPath;

    /**
     * Used by URLClassLoader to load modules from a list of URLs
     */
    private URL[] modulesLocation;

    /**
     * URLClassLoader used to load classes from a list of jars
     */
    private URLClassLoader urlcl;

    /**
     * Parent class loader <br/>
     * Defining which ClassLoader to use as the parent is important. Class loaders
     * work in a hierarquical way, where childs inherit the access to load the
     * classes from the parents - but not in the oposite direction. It may be
     * desired to load a class from a sibling of the class loader in use for the
     * application.
     */
    private ClassLoader parentcl;

    /**
     * Default getInstance method. Uses default folder to look for the classes (./modules),
     * and class loader of this class
     * 
     * @param context Context defines which JClassLoader to use
     * @return JClassLoader
     * @throws InvalidPathToJarException
     * @throws NotReadableException
     */
    public static JClassLoader getInstance(String context) throws InvalidPathToJarException, NotReadableException {
        return getInstance(context, JClassLoader.class.getClassLoader());
    }

    /**
     * Default getInstance method. Uses default folder to look for the classes (./modules)
     *
     * @param context Context defines which JClassLoader to use
     * @param parentClassLoader Defines which ClassLoader to use as a parent
     * @return JClassLoader
     * @throws InvalidPathToJarException
     * @throws NotReadableException
     */
    public static JClassLoader getInstance(String context, ClassLoader parentClassLoader) throws InvalidPathToJarException, NotReadableException {
        if (Instances.containsKey(context)) {
            return Instances.get(context);
        }

        // If it does not exist, creates a new one with default path ./modules
        return getInstance(context, "." + File.separatorChar + "modules");
    }

    /**
     * Instance that looks for modules in file system
     *
     * @param context Context defines which JClassLoader to use
     * @param classPath Defines where to search for the jar files with the classes
     * @return JClassLoader
     * @throws InvalidPathToJarException
     * @throws NotReadableException
     */
    public static JClassLoader getInstance(String context, String classes_dir) throws InvalidPathToJarException, NotReadableException {
        return getInstance(context, classes_dir, JClassLoader.class.getClassLoader());
    }

    /**
     * Instance that looks for modules in file system
     *
     * @param context Context defines which JClassLoader to use
     * @param classPath Defines where to search for the jar files with the classes
     * @param parentClassLoader Defines which ClassLoader to use as a parent
     * @return JClassLoader
     * @throws InvalidPathToJarException
     * @throws NotReadableException
     */
    public static JClassLoader getInstance(String context, String classes_dir, ClassLoader parentClassLoader) throws InvalidPathToJarException, NotReadableException {
        JClassLoader instance;

        if (Instances.containsKey(context)) {
            return Instances.get(context);
        }

        // If it does not exist, creates a new one

        File dir = new File(classes_dir);

        if (!dir.isDirectory()) {
            throw new InvalidPathToJarException(classes_dir);
        }

        if (!dir.canRead()) {
            throw new NotReadableException();
        }
        instance = new JClassLoader(dir, parentClassLoader);
        Instances.put(context, instance);

        return instance;
    }

    /**
     * Instance that loads modules from a list of URLs
     *
     * @param context Context defines which JClassLoader to use
     * @param modules Array of URL's of jar files
     * @return JClassLoader
     * @throws InvalidPathToJarException
     * @throws NotReadableException
     */
    public static JClassLoader getInstance(String context, List<URL> modules) throws InvalidPathToJarException, NotReadableException {
        return getInstance(context, modules, JClassLoader.class.getClassLoader());
    }

    /**
     * Instance that loads modules from a list of URLs
     *
     * @param context Context defines which JClassLoader to use
     * @param modules Array of URL's of jar files
     * @return JClassLoader
     * @throws InvalidPathToJarException
     * @throws NotReadableException
     */
    public static JClassLoader getInstance(String context, List<URL> modules, ClassLoader parentClassLoader) throws InvalidPathToJarException, NotReadableException {
        JClassLoader instance;

        if (Instances.containsKey(context)) {
            return Instances.get(context);
        }

        // If it does not exist, creates a new one

        instance = new JClassLoader(modules, parentClassLoader);
        Instances.put(context, instance);

        return instance;
    }


    /**
     * Loads a jar from a URL
     *
     * @deprecated Creates an instance for each request (not a good behavior)
     * @param jarLocation URL of jar file location
     */
    public static Object loadClass(String className, URL jarLocation) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        URLClassLoader tempURLCL;

        tempURLCL = URLClassLoader.newInstance(new URL[]{ jarLocation });

        return tempURLCL.loadClass(className).newInstance();
    }

    /**
     * Loads a jar from a URL, specifying parent class loader
     *
     * @deprecated Creates an instance for each request (not a good behavior)
     * @param jarLocation URL of jar file location
     */
    public static Object loadClass(String className, URL jarLocation, ClassLoader classloader) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        URLClassLoader tempURLCL;

        tempURLCL = URLClassLoader.newInstance(new URL[]{ jarLocation }, classloader);

        return tempURLCL.loadClass(className).newInstance();
    }

    //--------------------- Non-static --------------------------------------

    /**
     * @param classPath Where the jar files are stored
     */
    private JClassLoader(File classes_dir, ClassLoader parentClassLoader) {
        this.classPath = classes_dir;
        this.modulesLocation = null;
        this.parentcl = parentClassLoader;

        refresh();
    }

    /**
     * @param modules Modules' URL
     */
    private JClassLoader(List<URL> modules, ClassLoader parentClassLoader){
        this.classPath = null;
        this.modulesLocation = modules.toArray(new URL[]{});
        this.parentcl = parentClassLoader;
        
        refresh();
    }

    /**
     * Retreives a class located in URLClassLoader, or any of its parents.
     * @param className Identifier of the class to retreive
     * @return
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    public Class getClass(String className) throws ClassNotFoundException, IllegalAccessException{
        if(className.startsWith("java.")){
            throw new IllegalAccessException("Not allowed to instatiate " + className);
        }

        /**
         * If it was already loaded
         */
        if(Classes.containsKey(className)){
            return Classes.get(className);
        }

        Class newClass = urlcl.loadClass(className);
        /**
         * Caches the new loaded class
         */
        Classes.put(className, newClass);
        
        return newClass;
    }


    /**
     * Retreives a new instance of a class located in URLClassLoader,
     * or any of its parents.
     *
     * @param className Identifier of the class to retreive
     * @return
     * @throws ClassNotFoundException
     * @throws InstantclassiationException
     * @throws IllegalAccessException
     */
    public Object loadClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        if(className.startsWith("java.")){
            throw new IllegalAccessException("Not allowed to instatiate " + className);
        }
        return ((Class)getClass(className)).newInstance();
    }


    /**
     * Loads/reloads jar files from URL's
     */
    public void refresh() {
        List<URL> urls;
        URL[] url = null;

        if(classPath!=null){
            // Loads from directory
            urls = getJars(classPath, 0);
            url = urls.toArray(new URL[]{});
        }else{
            // Loads from URLs
            url = modulesLocation;
        }

        urlcl = URLClassLoader.newInstance(url,parentcl);
    }

    /**
     * Recursively searches for jar files in directories
     * 
     * @param path Path to search for jar files / directories
     * @param depth Current searching depth
     */
    private List<URL> getJars(File path, int depth) {
        ArrayList<URL> urls = new ArrayList<URL>();

        //If the depth limit is reach, operation is finished here
        if (depth > MAX_DEPTH) {
            return urls;
        }

        for (File jar : path.listFiles()) {

            System.out.println("[JClassLoader] Loading jar " + jar.getAbsolutePath());
            if (jar.canRead()){
                //If its a directory, searches for files inside until the maximum depth
                if (jar.isDirectory()) {
                    urls.addAll(getJars(jar, depth + 1));
                } else {
                    //If its a file, check if it is valid
                    if (jar.getName().endsWith(".jar")) {
                        try {
                            urls.add(jar.toURI().toURL());
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(JClassLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        }
        return urls;
    }
}
