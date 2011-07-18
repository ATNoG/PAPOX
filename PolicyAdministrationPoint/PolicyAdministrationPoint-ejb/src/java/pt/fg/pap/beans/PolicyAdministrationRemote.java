//=============================================================================
// Brief   : Remote interface for Policy Administration Session Bean
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

import java.util.List;
import javax.ejb.Remote;
import pt.fg.pap.operation.OperationResult;

/**
 * Remote interface for Policy Administration Bean
 * @author Francisco Alexandre de Gouveia
 */
@Remote
public interface PolicyAdministrationRemote {

    String getRootPolicy();

    OperationResult setPolicyRetreiver(String policyRetreiverClassName);

    String getPolicySet(String policySetId);

    String getPolicy(String policyId);

    OperationResult createRule(String parentPolicy, String ruleId, String effect, String description);

    OperationResult createPolicy(String parentPolicySet, String policyId, String policyCombiningAlgorithm, String version, String description);

    OperationResult createPolicySet(String parentPolicySet, String policySetId, String policyCombiningAlgorithm, String version, String description);

    OperationResult removeRule(String ruleId);

    OperationResult removePolicy(String policyId);

    OperationResult removePolicySet(String policySetId);

    List getResources(String category);

    String getResourceShortName(String category, String resourceId);

    OperationResult setInfoRetreiver(String category, String infoRetreiverClassName);

    List getResources(String context, String category);

    String getRule(String ruleId);

    OperationResult replaceTarget(java.lang.String elementType, java.lang.String elementId, java.lang.String elementName);

}
