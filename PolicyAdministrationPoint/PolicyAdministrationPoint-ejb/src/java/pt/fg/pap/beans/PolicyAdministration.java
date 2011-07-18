//=============================================================================
// Brief   : Policy Administration as a Stateful Session Bean
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

import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import pt.fg.pap.interfaces.IInfoRetreiver;
import pt.fg.pap.interfaces.IPolicyRetreiver;
import pt.fg.pap.operation.OperationResult;
import pt.fg.pap.operation.OperationStatus;
import pt.fg.pap.operation.message.ErrorMessage;
import pt.fg.pap.operation.message.WarningMessage;
import pt.fg.xacml.ElementFactory;

/**
 * Policy Administration Stateful Session Bean
 * 
 * @author Francisco Alexandre de Gouveia
 */
@Stateful
public class PolicyAdministration implements PolicyAdministrationRemote {

    @EJB
    private FactoryLocal factory;
    private IPolicyRetreiver policyRetreiver;
    /**
     * <Context, InfoRetreiver>
     */
    private HashMap<String, IInfoRetreiver> infoRetreivers;

    /**
     * Converts a Node object into a String
     *
     * @param n Node to be converted
     * @return String with XML
     */
    public String nodeToString(Node n) {
        String tag = "";
        NamedNodeMap attributes;

        tag += "<" + n.getNodeName();

        /* Attributes */
        attributes = n.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            tag += " " + attributes.item(i).getNodeName() + "=\"" + attributes.item(i).getNodeValue() + "\"";
        }

        tag += ">";

        /* ChildNodes */
        Node node = n.getFirstChild();

        while (node != null) {
            switch (node.getNodeType()) {
                case Node.ELEMENT_NODE:
                    tag += nodeToString(node);
                    break;
                case Node.TEXT_NODE:
                    String text = node.getNodeValue();
                    if (text.replace(" ", "").length() > 0) {
                        //Remove the spaces before
                        while (text.startsWith(" ")) {
                            text = text.substring(1);
                        }
                        //Remove the spaces after
                        while (text.endsWith(" ")) {
                            text = text.substring(0, text.length() - 1);
                        }

                        tag += text;
                    }
                    break;
            }
            node = node.getNextSibling();
        }

        tag += "</" + n.getNodeName() + ">";
        return tag;
    }

    /**
     * Defines which policy retreiver to use
     * @param policyRetreiverClassName
     * @return 
     */
    @Override
    public OperationResult setPolicyRetreiver(String policyRetreiverClassName) {

        /**
         * Works!!!!
         * Problems solved:
         *      - Parent classloader defined
         *      - Singleton EJB loads classes
         *
         */
        policyRetreiver = factory.getPolicyRetreiver(policyRetreiverClassName);
        if (policyRetreiver == null) {
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("A problem has been found when loading policy retreiver module: " + policyRetreiverClassName));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    /**
     * Defines which information retreiver to use, per category
     * @param category
     * @param infoRetreiverClassName
     * @return 
     */
    @Override
    public OperationResult setInfoRetreiver(String category, String infoRetreiverClassName) {
        if (infoRetreivers == null) {
            infoRetreivers = new HashMap<String, IInfoRetreiver>();
        }

        IInfoRetreiver ir = factory.getInfoRetreiver(infoRetreiverClassName);

        if (ir != null) {
            infoRetreivers.put(category, ir);
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new ErrorMessage("A problem has been found when loading info retreiver module: " + infoRetreiverClassName));
        }

        return new OperationResult(OperationStatus.OPERATION_SUCCESS);
    }

    /**
     * Retreives the root policy element
     * @return 
     */
    @Override
    public String getRootPolicy() {
        //get root policy (set) with depth 1 to get the first childs
        Node rootPolicy = policyRetreiver.getRootPolicy(1);
        return nodeToString(rootPolicy);
    }

    /**
     * Retreives policy set element
     * @param policySetId
     * @return 
     */
    @Override
    public String getPolicySet(String policySetId) {
        //get policy set with depth 1 to get the first childs
        Node res = policyRetreiver.getPolicySet(policySetId, 1);
        return nodeToString(res);
    }

    /**
     * Retreives policy element
     * @param policyId
     * @return 
     */
    @Override
    public String getPolicy(String policyId) {
        //get policy with depth 1 to get the first childs
        Node res = policyRetreiver.getPolicy(policyId, 1);
        return nodeToString(res);
    }

    /**
     * Retreives rule element
     * @param ruleId
     * @return 
     */
    @Override
    public String getRule(String ruleId) {
        //get rule
        Node res = policyRetreiver.getRule(ruleId, 1);
        return nodeToString(res);
    }

    /**
     * Creates a new rule element
     * @param parentPolicy
     * @param ruleId
     * @param effect
     * @param description
     * @return 
     */
    @Override
    public OperationResult createRule(String parentPolicy, String ruleId, String effect, String description) {
        //Checks if a rule with the same RuleId already exists
        if (policyRetreiver.ruleExist(ruleId)) {
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new WarningMessage("A rule with the same RuleId already exists"));
        }

        //Creates a rule
        String rule = ElementFactory.createRule(ruleId, effect, description);
        //Insert the rule into the policy
        OperationResult res = policyRetreiver.insertElementIntoPolicyAsLast(parentPolicy, rule);

        return res;
    }

    /**
     * Creates a new policy element
     * @param parentPolicySet
     * @param policyId
     * @param ruleCombiningAlgorithm
     * @param version
     * @param description
     * @return 
     */
    @Override
    public OperationResult createPolicy(String parentPolicySet, String policyId, String ruleCombiningAlgorithm, String version, String description) {
        //Checks if a policy with the same PolicyId already exists
        if (policyRetreiver.policyExist(policyId)) {
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new WarningMessage("A policy with the same PolicyId already exists"));
        }

        //Creates a policy
        String policy = ElementFactory.createPolicy(policyId, ruleCombiningAlgorithm, version, description);
        //Insert the policy into the policySet
        OperationResult res = policyRetreiver.insertElementIntoPolicySetAsLast(parentPolicySet, policy);

        return res;
    }

    /**
     * Creates a new policy set element
     * @param parentPolicySet
     * @param policySetId
     * @param policyCombiningAlgorithm
     * @param version
     * @param description
     * @return 
     */
    @Override
    public OperationResult createPolicySet(String parentPolicySet, String policySetId, String policyCombiningAlgorithm, String version, String description) {
        //Checks if a policy set with the same PolicySetId already exists
        if (policyRetreiver.policySetExist(policySetId)) {
            return new OperationResult(OperationStatus.OPERATION_FAILURE, new WarningMessage("A policy set with the same PolicySetId already exists"));
        }

        //Creates a policy set
        String policySet = ElementFactory.createPolicySet(policySetId, policyCombiningAlgorithm, version, description);
        //Insert the policy set into another policySet
        OperationResult res = policyRetreiver.insertElementIntoPolicySetAsLast(parentPolicySet, policySet);

        return res;
    }

    /**
     * Recreates a target for an element. (A way to edit, as xacml:AllOf and xacml:AnyOf elements don't have identifiers)
     * @param elementType
     * @param elementId
     * @param elementName
     * @return 
     */
    @Override
    public OperationResult replaceTarget(String elementType, String elementId, String elementName) {


        if (elementType.equals("PolicySet")) {
            policyRetreiver.removeElementFromPolicyTreeElement(elementId, "xacml:Target");
            return policyRetreiver.insertElementIntoPolicySetAsFirst(elementId, elementName);
        } else if (elementType.equals("Policy")) {
            //policyRetreiver.removeElementFromPolicyTreeElement(elementId, "xacml:Target");
            return policyRetreiver.insertElementIntoPolicyAsFirst(elementId, elementName);
        }
        if (elementType.equals("Rule")) {
            // TODO
        }

        return new OperationResult(OperationStatus.OPERATION_FAILURE);
    }

    /**
     * Removes a rule element
     * @param ruleId
     * @return 
     */
    @Override
    public OperationResult removeRule(String ruleId) {
        //Removes the rule
        OperationResult res = policyRetreiver.removeRule(ruleId);

        return res;
    }

    /**
     * Removes a policy element
     * @param policyId
     * @return 
     */
    @Override
    public OperationResult removePolicy(String policyId) {
        //Removes the policy
        OperationResult res = policyRetreiver.removePolicy(policyId);

        return res;
    }

    /**
     * Removes a policy set element
     * @param policySetId
     * @return 
     */
    @Override
    public OperationResult removePolicySet(String policySetId) {
        //Removes the policy set
        OperationResult res = policyRetreiver.removePolicySet(policySetId);
        return res;
    }

    /**
     * Retreives a list of all the resources from a context
     * @param context
     * @return 
     */
    @Override
    public List getResources(String context) {
        return getResources(context, null);
    }

    /**
     * Retreives a list of resources by category and context
     * @param context
     * @param category
     * @return 
     */
    @Override
    public List getResources(String context, String category) {

        if (infoRetreivers.containsKey(context)) {
            if (category != null && category.length() > 0) {
                return infoRetreivers.get(context).listResources(category);
            } else {
                return infoRetreivers.get(context).listResources();
            }
        }

        // Null if category not found
        return null;
    }

    /**
     * Retreives resource short name
     * @param context
     * @param resourceId
     * @return 
     */
    @Override
    public String getResourceShortName(String context, String resourceId) {
        if (infoRetreivers.containsKey(context)) {
            return infoRetreivers.get(context).getResourceShortName(resourceId);
        }

        //Nothing if category not found
        return "";
    }
}
