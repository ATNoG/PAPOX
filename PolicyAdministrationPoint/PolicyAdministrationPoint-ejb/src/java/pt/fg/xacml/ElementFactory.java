//=============================================================================
// Brief   : XML generator for XACML elements
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
package pt.fg.xacml;
/**
 * Generates a string of XACML elements
 *
 * @author Francisco Alexandre de Gouveia
 * @version 0.1
 * @since 20/Mai/2011
 */
public class ElementFactory {

    /**
     * Generates a xacml:Rule element
     * @param ruleId
     * @param effect
     * @param description
     * @return 
     */
    public static String createRule(String ruleId, String effect, String description){
        String element = "";

        if(ruleId.length()>0 &&
                (effect.equals("Permit") || effect.equals("Deny"))){
            element += "<xacml:Rule RuleId=\"" + ruleId + "\" Effect=\"" + effect + "\">";
            /**
             * Description element
             */
            element += createDescription(description);
                /**
                 * Target is an obligatory element
                 */
                element += "<xacml:Target/>";
            element += "</xacml:Rule>";
        }
        
        return element;
    }

    /**
     * Generates a xacml:Policy element
     * @param policyId
     * @param ruleCombiningAlgorithm
     * @param version
     * @param description
     * @return 
     */
    public static String createPolicy(String policyId, String ruleCombiningAlgorithm, String version, String description){
        String element = "";

        if(policyId.length()>0){
            element += "<xacml:Policy";
                element += " PolicyId=\"" + policyId + "\"";
                element += " RuleCombiningAlgId=\"" + ruleCombiningAlgorithm + "\"";
                element += " version=\"" + version + "\"";
            element += ">";
            /**
             * Description element
             */
            element += createDescription(description);
                /**
                 * Target is an obligatory element
                 */
                element += "<xacml:Target/>";
            element +="</xacml:Policy>";
        }

        return element;
    }

    /**
     * Generates a xacml:PolicySet element
     * @param policySetId
     * @param policyCombiningAlgorithm
     * @param version
     * @param description
     * @return 
     */
    public static String createPolicySet(String policySetId, String policyCombiningAlgorithm, String version, String description){
        String element = "";

        if(policySetId.length()>0){
            element += "<xacml:PolicySet";
                element += " PolicySetId=\"" + policySetId + "\"";
                element += " PolicyCombiningAlgId=\"" + policyCombiningAlgorithm + "\"";
                element += " version=\"" + version + "\"";
            element += ">";
            /**
             * Description element
             */
            element += createDescription(description);
                /**
                 * Target is an obligatory element
                 */
                element += "<xacml:Target/>";
            element +="</xacml:PolicySet>";
        }

        return element;
    }

    /**
     * Generates a xacml:Description element
     * @param description
     * @return 
     */
    public static String createDescription(String description){
        String element = "";

        if(description==null || description.length() == 0){
            return element;
        }

        element += "<xacml:Description>";
        element += description;
        element += "</xacml:Description>";

        return element;
    }

}
