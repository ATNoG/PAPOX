//=============================================================================
// Brief   : Available Class used by ClassFactory implementations
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

/**
 * Available class is used by factories, to have a list of classes that are 
 * considered as existent and valid
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 01-03-2011
 */
public final class AvailableClass {
    private String className;
    private String classDescription;
    private String location;

    /**
     * Constructor with class name and description
     * 
     * @param className
     * @param classDescription 
     */
    public AvailableClass(String className, String classDescription) {
        this.className = className;
        this.classDescription = classDescription;
        this.location = null;
    }
    
    /**
     * Constructor with class name, description and location (URL to jar file)
     * @param className
     * @param classDescription
     * @param location 
     */
    public AvailableClass(String className, String classDescription, String location) {
        this.className = className;
        this.classDescription = classDescription;
        this.location = location;
    }

    /**
     * Retreives class name
     * @return Class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * Retreives description
     * @return Description
     */
    public String getClassDescription() {
        return classDescription;
    }

    /**
     * Retreives location to jar file where this class is stored
     * @return Jar file location
     */
    public String getLocation() {
        return location;
    }
}
