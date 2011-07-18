//=============================================================================
// Brief   : Servlet to retreive policies and informations
// Authors : Francisco Gouveia <fgouveia@av.it.pt>
//-----------------------------------------------------------------------------
// PAPOX (Policy Administration Point for OASIS XACML) - Presentation Layer
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
package pt.fg.pap.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import pt.fg.pap.beans.PolicyAdministrationRemote;
import pt.fg.pap.operation.OperationResult;
import pt.fg.pap.operation.OperationStatus;

/**
 *
 * @author Francisco Alexandre de Gouveia
 */
public class PolicyAdminServlet extends HttpServlet {

    private PolicyAdministrationRemote polAdm;
    private int tries = 0;

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletOutputStream out = response.getOutputStream();
        String action = request.getParameter("action");
        HttpSession session = request.getSession(true);
        try {
            //Get PolicyAdministration Bean
            polAdm = getPolicyAdminBean(session, false);

        } catch (Exception ex) {
            Logger.getLogger(PolicyAdminServlet.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        if (action == null) {
            action = "";
        } else {
            action = action.toLowerCase();
        }


        try {
            doAction(action, polAdm, request, response, out);
        } catch (EJBException e) {
            try {
                /**
                 * Forces creation of a new Bean
                 */
                polAdm = getPolicyAdminBean(session, true);
                /**
                 * Tries again
                 */
                tries++;
                if (tries < 2) {
                    doGet(request, response);
                }
            } catch (NamingException ex) {
                Logger.getLogger(PolicyAdminServlet.class.getName()).log(Level.SEVERE, null, ex);
            }

        } finally {
            //Close stream
            out.close();
        }
    }

    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void doAction(String action, PolicyAdministrationRemote polAdm,
            HttpServletRequest request, HttpServletResponse response, ServletOutputStream out) throws IOException {
        String id;
        String res;

        if (action.equals("")) {
            /**
             * Default action: Get root policy
             */
            res = polAdm.getRootPolicy();
            if (res != null && res.length() > 0) {
                response.setContentType("text/xml");
                out.print(res);
            } else {
                out.close();
                response.sendError(response.SC_INTERNAL_SERVER_ERROR, "Error loading root policy");
            }
        } else if (action.equals("getresources")) {
            /**
             * Get resources from Info retreiver
             */
            id = (String) request.getParameter("context");
            res = (String) request.getParameter("category");
            List<String> resources;
            if (res == null) {
                resources = polAdm.getResources(id);
            } else {
                resources = polAdm.getResources(id, res);
            }
            response.setContentType("text/xml");
            out.print("<resources>");
            if (resources != null) {
                for (int i = 0; i < resources.size(); i++) {
                    out.print("<resource>");
                    out.print("<name>" + resources.get(i) + "</name>");
                    out.print("<shortname>" + polAdm.getResourceShortName(id, resources.get(i)) + "</shortname>");
                    out.print("</resource>");
                }
            }
            out.print("</resources>");
        } else if (action.equals("getpolicyset")) {
            /**
             * Get policy set
             */
            id = (String) request.getParameter("policySetId");
            if (id != null && id.length() > 0) {
                response.setContentType("text/xml");
                out.print(polAdm.getPolicySet(id));
            }
        } else if (action.equals("getpolicy")) {
            /**
             * Get policy
             */
            id = (String) request.getParameter("policyId");
            if (id != null && id.length() > 0) {
                response.setContentType("text/xml");
                out.print(polAdm.getPolicy(id));
            }
        } else if (action.equals("getrule")) {
            /**
             * Get rule
             */
            id = (String) request.getParameter("ruleId");
            if (id != null && id.length() > 0) {
                response.setContentType("text/xml");
                out.print(polAdm.getRule(id));
            }
        } else if (action.equals("setpolicysettarget")) {
            /**
             * Set policySet target
             */
            String element;
            id = (String) request.getParameter("PolicySetId");
            element = (String) request.getParameter("element");
            if (id != null && id.length() > 0) {
                OperationResult r = polAdm.replaceTarget("PolicySet", id, element);
                if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                    out.print("<success><status>true</status></success>");
                } else {
                    out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
                }
            }
        } else if (action.equals("setpolicytarget")) {
            /**
             * Set policy target
             */
            String element;
            id = (String) request.getParameter("PolicyId");
            element = (String) request.getParameter("element");
            if (id != null && id.length() > 0) {
                OperationResult r = polAdm.replaceTarget("Policy", id, element);
                if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                    out.print("<success><status>true</status></success>");
                } else {
                    out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
                }
            }
        } else if (action.equals("setruletarget")) {
            /**
             * Set rule target
             */
            String element;
            id = (String) request.getParameter("RuleId");
            element = (String) request.getParameter("element");
            if (id != null && id.length() > 0) {
                OperationResult r = polAdm.replaceTarget("Rule", id, element);
                if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                    out.print("<success><status>true</status></success>");
                } else {
                    out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
                }
            }
        } else if (action.equals("createrule")) {
            /**
             * Create rule
             */
            String parentPolicy = request.getParameter("parentPolicy");
            String ruleId = request.getParameter("ruleId");
            String effect = request.getParameter("effect");
            String description = request.getParameter("description");

            OperationResult r = polAdm.createRule(parentPolicy, ruleId, effect, description);
            if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                out.print("<success><status>true</status></success>");
            } else {
                out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
            }
        } else if (action.equals("createpolicy")) {
            /**
             * Create policy
             */
            String parentPolicySet = request.getParameter("parentPolicySet");
            String policyId = request.getParameter("policyId");
            String ruleCombiningAlgorithm = request.getParameter("ruleCombiningAlgorithm");
            String version = request.getParameter("version");
            String description = request.getParameter("description");
            // Delegation is optional and not in the scope of this project

            OperationResult r = polAdm.createPolicy(parentPolicySet, policyId, ruleCombiningAlgorithm, version, description);
            if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                out.print("<success><status>true</status></success>");
            } else {
                out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
            }
        } else if (action.equals("createpolicyset")) {
            /**
             * Create policy set
             */
            String parentPolicySet = request.getParameter("parentPolicySet");
            String policySetId = request.getParameter("policySetId");
            String policyCombiningAlgorithm = request.getParameter("policyCombiningAlgorithm");
            String version = request.getParameter("version");
            String description = request.getParameter("description");
            // Delegation is optional and not in the scope of this project

            OperationResult r = polAdm.createPolicySet(parentPolicySet, policySetId, policyCombiningAlgorithm, version, description);
            if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                out.print("<success><status>true</status></success>");
            } else {
                out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
            }
        } else if (action.equals("removerule")) {
            /**
             * Remove rule
             */
            String ruleId = request.getParameter("ruleId");

            OperationResult r = polAdm.removeRule(ruleId);
            if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                out.print("<success><status>true</status></success>");
            } else {
                out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
            }
        } else if (action.equals("removepolicy")) {
            /**
             * Remove policy
             */
            String policyId = request.getParameter("policyId");

            OperationResult r = polAdm.removePolicy(policyId);
            if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                out.print("<success><status>true</status></success>");
            } else {
                out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
            }
        } else if (action.equals("removepolicyset")) {
            /**
             * Remove policy set
             */
            String policySetId = request.getParameter("policySetId");

            OperationResult r = polAdm.removePolicySet(policySetId);
            if (r.getResult() == OperationStatus.OPERATION_SUCCESS) {
                out.print("<success><status>true</status></success>");
            } else {
                out.print("<success><status>false</status><error>" + r.getErrorDetails().replace("<", "&lt").replace(">", "&gt;") + "</error></success>");
            }
        } else if (action.equals("addtarget")) {
            /**
             * Adds a target to a element
             */
            String targetPolicyElement = request.getParameter("elementId");

            String r;
            r = "<xacml:AnyOf>";
            /**
             * For each new group
             */
            r += "<xacml:AllOf>";

            r += "</xacml:AllOf>";
            r += "</xacml:AllOf>";

        }
    }

    private boolean loadBean(String className, HttpSession session, boolean forceCreate) throws NamingException {
        //HTTPSession works like a Map<String, Object>
        //In this case it will be used to store stateful bean
        //Class name is used as a key
        Object obj = null;

        if (!forceCreate) {
            obj = session.getAttribute("ejb" + className);
        }

        //If object exists, returns
        if (obj != null) {
            return false;
        }

        //Otherwise, look up for it in EJB Container

        //Create a context
        InitialContext ic = new InitialContext();

        //Lookup for the bean
        obj = ic.lookup("java:global/PolicyAdministrationPoint/PolicyAdministrationPoint-ejb/" + className);


        //Puts Bean in the session
        session.setAttribute("ejb" + className, obj);
        session.setMaxInactiveInterval(300);
        return true;
    }

    private PolicyAdministrationRemote getPolicyAdminBean(HttpSession session, boolean forceCreate) throws NamingException {
        String beanName = "PolicyAdministration";
        PolicyAdministrationRemote par;
        boolean createdNew = loadBean(beanName, session, forceCreate);

        par = (PolicyAdministrationRemote) session.getAttribute("ejb" + beanName);

        if (createdNew) {
            OperationResult r = par.setPolicyRetreiver(getPolicyRetreiverClassName());
            if (r.getResult() == OperationStatus.OPERATION_FAILURE) {
                System.out.println(r.getErrorDetails());
            }

            Map<String, String> l = getInfoRetreiversClassNames();
            for (String k : l.keySet()) {
                r = par.setInfoRetreiver(k, l.get(k));
                if (r.getResult() == OperationStatus.OPERATION_FAILURE) {
                    System.out.println(r.getErrorDetails());
                }
            }
        }
        return par;
    }

    /**
     * Will check user configuration and return the choosen PolicyRetreiver
     * @return Policy retreiver class name
     */
    private String getPolicyRetreiverClassName() {
        // TODO - Get policy retreiver from ProjectBean
        return "pt.fg.xacml.bdbxml.policyretreiver.PolicyRetreiver";
    }

    private Map<String, String> getInfoRetreiversClassNames() {
        //<category, class>
        Map il = new HashMap<String, String>();
        // TODO - Get info retreivers from ProjectBean
        il.put("dataTypes", "pt.fg.xacml.infoRetreiver.DataTypes");
        il.put("combiningAlgorithms", "pt.fg.xacml.infoRetreiver.CombiningAlgorithms");

        return il;
    }
}
