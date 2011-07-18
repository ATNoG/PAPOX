//=============================================================================
// Brief   : Factory implementation as a Singleton Session Bean
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

package pt.fg.pap.beans;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import pt.fg.jcl.exceptions.InvalidPathToJarException;
import pt.fg.jcl.exceptions.NotReadableException;
import pt.fg.pap.classfactory.ClassFactory;
import pt.fg.pap.classfactory.factories.InfoRetreiverFactory;
import pt.fg.pap.classfactory.factories.PolicyRetreiverFactory;
import pt.fg.pap.exceptions.InvalidClassFactoryException;
import pt.fg.pap.interfaces.IInfoRetreiver;
import pt.fg.pap.interfaces.IPolicyRetreiver;

/**
 * Factory Singleton Session Bean<br/>
 * <br/>
 * Singleton required because how EJB works
 * @author Francisco Alexandre de Gouveia
 */
@Singleton
public class Factory implements FactoryLocal {
    /**
     * Policy Retreiver Factory
     **/
    private PolicyRetreiverFactory prf;
    /**
     * Information Retreiver Factory
     */
    private InfoRetreiverFactory irf;


    /**
     * Creates a new Policy Retreiver Factory.
     */
    private void getPolicyRetreiverFactory(){
        try {
            ClassFactory.setParentClassLoader(this.getClass().getClassLoader());
            prf = (PolicyRetreiverFactory) ClassFactory.getInstance(ClassFactory.PolicyRetreiverFactory);
        } catch (InvalidPathToJarException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotReadableException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidClassFactoryException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new Information Retreiver Factory.
     */
    private void getInfoRetreiverFactory(){
        try {
            ClassFactory.setParentClassLoader(this.getClass().getClassLoader());
            irf = (InfoRetreiverFactory) ClassFactory.getInstance(ClassFactory.InfoRetreiverFactory);
        } catch (InvalidPathToJarException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotReadableException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidClassFactoryException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads and retreives a Policy Retreiver from the Policy Retreiver Factory.
     * Creates a new factory if it doesn't exist (Singleton).
     * @param className
     * @return 
     */
    @Override
    public IPolicyRetreiver getPolicyRetreiver(String className) {
        if(prf==null){
            getPolicyRetreiverFactory();
        }
        try {
            return prf.get(className);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     * Loads and retreives a Information Retreiver from the Policy Information
     * Factory. Creates a new factory if it doesn't exist (Singleton).
     * @param className
     * @return 
     */
    @Override
    public IInfoRetreiver getInfoRetreiver(String className) {
        if(irf==null){
            getInfoRetreiverFactory();
        }
        try {
            return irf.get(className);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Factory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }


}
