//=============================================================================
// Brief   : Factory for Mapper implementations
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

import java.util.Set;
import pt.fg.pap.classfactory.AvailableClass;
import pt.fg.pap.classfactory.ClassFactory;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 01-03-2011
 */
public final class MapperFactory extends ClassFactory {

    public MapperFactory(Set<AvailableClass> allclasses) {
    }

    @Override
    public ClassFactory get(String className) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public Set<AvailableClass> getAllSupportedClasses() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
