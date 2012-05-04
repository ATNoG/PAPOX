package pt.fg.pap.interfaces;
//=============================================================================
// Brief   : Plugin interface
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// Interface for policy retrieving
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
import org.w3c.dom.Node;
import pt.fg.pap.operation.OperationResult;

/**
 * Interface to implement a policy retreiver. This abstraction allows to have
 * policies in files, databases or any other. Component should be responsible
 * for xml and schema validation, following OASIS XACMLv3 schema.
 *
 * @see <a href="http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=xacml#CURRENT">OASIS XACML webpage</a>
 * @see <a href="http://docs.oasis-open.org/xacml/3.0/xacml-core-v3-schema-wd-17.xsd">OASIS XACMLv3 SCHEMA (version last checked on 01 May 2011)</a>
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 10-03-2011
 */
public interface IPolicyRetreiver {

    /**
     * Root policy is the policy on which PDP starts the search. Root policy can
     * be a PolicySet element or a Policy element.
     *
     * @param depth Depth to get elements
     * @return Node with the result
     */
    Node getRootPolicy(int depth);

    /**
     * Gets policy tree element identified by its identifier.<br/>
     * Policy tree element can be:
     * <ul>
     *  <li><b>PolicySet</b> identified by <i>PolicySetId</i> </li>
     *  <li><b>Policy</b> identified by <i>PolicyId</i></li>
     *  <li><b>Rule</b> identified by <i>RuleId</i></li>
     * </ul>
     * @param id Identifier of policy tree element.
     * @param depth Depth to get policy tree elements inside the policy tree
     * element in the response.
     * @return Node with the result
     */
    Node getPolicyTreeElement(String id, int depth);

    /**
     * Gets PolicySet element identified by PolicySetId.
     * @param id Identifier of PolicySet element.
     * @param depth Depth to get PolicySet's elements in the response.
     * @return Node with the result
     */
    Node getPolicySet(String policySetId, int depth);

    /**
     * Gets Policy element identified by PolicyId.
     * @param id Identifier of Policy element.
     * @param depth Depth to get Policy's elements in the response.
     * @return Node with the result
     */
    Node getPolicy(String policyId, int depth);
    
    /**
     * Gets Rule element identified by RuleId.
     * @param id Identifier of Rule element.
     * @param depth Depth to get Rule's elements in the response.
     * @return Node with the result
     */
    Node getRule(String ruleId, int depth);

    /**
     * Inserts an element as the first element into a PolicySet. <br/>
     * Possible elements are:
     * <ul>
     *  <li>Description</li>
     *  <li>PolicySetIssuer</li>
     *  <li>PolicySetDefaults</li>
     *  <li>Target</li>
     *  <li>PolicySet</li>
     *  <li>Policy</li>
     *  <li>PolicySetIdReference</li>
     *  <li>PolicyIdReference</li>
     *  <li>CombinerParameters</li>
     *  <li>PolicyCombinerParameters</li>
     *  <li>PolicySetCombinerParameters</li>
     *  <li>ObligationExpressions</li>
     *  <li>AdviceExpressions</li>
     * </ul>
     *
     * @param policySetId PolicySet element identifier
     * @param element Xml element to be inserted
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult insertElementIntoPolicySetAsFirst(String policySetId, String element);

    /**
     * Inserts an element as the last element into a PolicySet. <br/>
     * Possible elements are:
     * <ul>
     *  <li>Description</li>
     *  <li>PolicySetIssuer</li>
     *  <li>PolicySetDefaults</li>
     *  <li>Target</li>
     *  <li>PolicySet</li>
     *  <li>Policy</li>
     *  <li>PolicySetIdReference</li>
     *  <li>PolicyIdReference</li>
     *  <li>CombinerParameters</li>
     *  <li>PolicyCombinerParameters</li>
     *  <li>PolicySetCombinerParameters</li>
     *  <li>ObligationExpressions</li>
     *  <li>AdviceExpressions</li>
     * </ul>
     *
     * @param policySetId PolicySet element identifier
     * @param element Xml element to be inserted
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult insertElementIntoPolicySetAsLast(String policySetId, String element);

    /**
     * Inserts an element after another element into a PolicySet. <br/>
     * Possible elements are:
     * <ul>
     *  <li>Description</li>
     *  <li>PolicySetIssuer</li>
     *  <li>PolicySetDefaults</li>
     *  <li>Target</li>
     *  <li>PolicySet</li>
     *  <li>Policy</li>
     *  <li>PolicySetIdReference</li>
     *  <li>PolicyIdReference</li>
     *  <li>CombinerParameters</li>
     *  <li>PolicyCombinerParameters</li>
     *  <li>PolicySetCombinerParameters</li>
     *  <li>ObligationExpressions</li>
     *  <li>AdviceExpressions</li>
     * </ul>
     *
     * @param policySetId PolicySet element identifier
     * @param elementId Element identifier in which the new element will be
     * inserted after
     * @param element Xml element to be inserted
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult insertElementIntoPolicySetAfterElement(String policySetId, String elementId, String element);

    /**
     * Inserts an element as the first element into a Policy. <br/>
     * Possible elements are:
     * <ul>
     *  <li>Description</li>
     *  <li>PolicyIssuer</li>
     *  <li>PolicyDefaults</li>
     *  <li>Target</li>
     *  <li>CombinerParameters</li>
     *  <li>RuleCombinerParameters</li>
     *  <li>VariableDefinition</li>
     *  <li>Rule</li>
     *  <li>ObligationExpressions</li>
     *  <li>AdviceExpressions</li>
     * </ul>
     * 
     * @param policyId Policy element identifier
     * @param element Xml element to be inserted
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult insertElementIntoPolicyAsFirst(String policyId, String element);

    /**
     * Inserts an element as the last element into a Policy. <br/>
     * Possible elements are:
     * <ul>
     *  <li>Description</li>
     *  <li>PolicyIssuer</li>
     *  <li>PolicyDefaults</li>
     *  <li>Target</li>
     *  <li>CombinerParameters</li>
     *  <li>RuleCombinerParameters</li>
     *  <li>VariableDefinition</li>
     *  <li>Rule</li>
     *  <li>ObligationExpressions</li>
     *  <li>AdviceExpressions</li>
     * </ul>
     *
     * @param policyId Policy element identifier
     * @param element Xml element to be inserted
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult insertElementIntoPolicyAsLast(String policyId, String element);

    /**
     * Inserts an element after another element into a Policy. <br/>
     * Possible elements are:
     * <ul>
     *  <li>Description</li>
     *  <li>PolicyIssuer</li>
     *  <li>PolicyDefaults</li>
     *  <li>Target</li>
     *  <li>CombinerParameters</li>
     *  <li>RuleCombinerParameters</li>
     *  <li>VariableDefinition</li>
     *  <li>Rule</li>
     *  <li>ObligationExpressions</li>
     *  <li>AdviceExpressions</li>
     * </ul>
     *
     * @param policyId Policy element identifier
     * @param element Xml element to be inserted
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult insertElementIntoPolicyAfterElement(String policyId, String elementId, String element);

    /**
     * Removes an element from a policy tree element.
     * <br/>Element name can be defined like in XPATH (e.g.:<code>Rule[@RuleId="example"]</code> or just <code>Rule</code>)
     *
     * @param elementId Identifier of the policy tree element
     * @param elementName Name of the element to be removed
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult removeElementFromPolicyTreeElement(String elementId, String elementName);

    /**
     * Removes a PolicySet.
     * 
     * @param policySetId Identifier of the policySet to be removed
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult removePolicySet(String policySetId);

    /**
     * Removes a Policy.
     *
     * @param policyId Identifier of the policy to be removed
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult removePolicy(String policyId);

    /**
     * Removes a Rule.
     *
     * @param ruleId Identifier of the rule to be removed
     * @return OperationResult with the result. If operation is failed,
     * OperationResult result will be OperationStatus.OPERATION_FAILURE
     */
    OperationResult removeRule(String ruleId);

    /**
     * Checks if a policy set exists.
     * 
     * @param policySetId Identifier of the policy set to search for
     * @return True if policy set exists
     */
    Boolean policySetExist(String policySetId);

    /**
     * Checks if a policy exists.
     *
     * @param policyId Identifier of the policy to search for
     * @return True if policy exists
     */
    Boolean policyExist(String policyId);

    /**
     * Checks if a rule exists.
     * 
     * @param ruleId Identifier of the rule to search for
     * @return True if rule exists
     */
    Boolean ruleExist(String ruleId);

}