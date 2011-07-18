//=============================================================================
// Brief   : Local interface for Factory Session Bean
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

import javax.ejb.Local;
import pt.fg.pap.interfaces.IInfoRetreiver;
import pt.fg.pap.interfaces.IPolicyRetreiver;

/**
 * Local interface for Factory
 * @author Francisco Alexandre de Gouveia
 */
@Local
public interface FactoryLocal {

    /**
     * Retreives a Policy Retreiver object
     * @param className
     * @return 
     */
    IPolicyRetreiver getPolicyRetreiver(String className);

    /**
     * Retreives a Information Retreiver object
     * @param className
     * @return 
     */
    IInfoRetreiver getInfoRetreiver(String className);

}
