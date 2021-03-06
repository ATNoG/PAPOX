package pt.fg.pap.interfaces;
//=============================================================================
// Brief   : Plugin interface
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Information retriever interface
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
import java.util.List;
import pt.fg.pap.operation.OperationResult;

/**
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 01-03-2011
 */
public interface IInfoRetreiver {

    /**
     * Description of a specific resource
     *
     * @param id Resource identifier
     * @return Description
     */
    String getResourceDescription(String id);
    
    /**
     * Short name given to a resource. (e.g.: pt.ua.servers.WebServer1001 as ELearningServer)
     *
     * @param id
     * @return
     */
    String getResourceShortName(String id);

    /**
     * List of resource identifiers
     *
     * @return Set of resource identifiers
     */
    List<String> listResources();

    /**
     * List of resource identifiers from a category
     *
     * @param category
     * @return Set of resource identifiers
     */
    List<String> listResources(String category);

    /**
     * Specifies if InfoRetreiver does auto-mapping of its resources.
     * Mapping can be usefull to identify resources. (E.g.: map a computer into "computers" group)
     *
     * @return
     */
    boolean doesMapping();

    /**
     * Sets the instance responsible for mapping attributes.
     *
     * @param mapper
     * @return OperationResult
     */
    OperationResult setMapper(IMapper mapper);
}
