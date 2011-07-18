/*
//=============================================================================
// Brief   : Helper for AJAX communication and content management
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
*/

engine = {
    getReq:function(){
        var request = null;
        /**
         * Required to do because Internet Explorer is implemented in different
         * way than the concurrent browsers
         */
        try {
            request = new XMLHttpRequest();
        } catch (exception) {
            try {
                request = new ActiveXObject('MSXML2.XMLHTTP');
            } catch (exception) {
                request = new ActiveXObject('Microsoft.XMLHTTP');
            }
        }

        return request;
    },
    call:function(address, method, fnOnStateChange, content){
        var request = engine.getReq();
        //req.setRequestHeader('Content-Type', "application/xml");
        //open( METHOD , URL , ASYNCHRONOUS)
        request.open(method, address, true);

        request.onreadystatechange = function(){
            fnOnStateChange(request);
        };

        if(method.toLowerCase()=="get"){
            request.send(null);
        }else{
            // Simulate a form submition
            request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            request.setRequestHeader("Content-length", content.length);
            request.setRequestHeader("Connection", "close");
            request.send(content);
        }
    },
    getResources:function(context, category, fn){
        engine.call("PolicyAdminServlet?action=getResources&context=" + context + "&category=" + category, "GET", function(request){

            if(request.readyState == 4){ // Ready
                if(request.status==200){ // OK
                    var obj = {}; //Map<name, fullname>
                    var node = request.responseXML.firstChild;//document
                    
                    if(node!=null && node.nodeName=="resources"){
                        node = node.firstChild; //<resource>
                        while(node!=null){

                            if(node.nodeName=="resource"){

                                var subNode = node.firstChild;
                                var name = "";
                                var fullname = "";
                                while(subNode != null){
                                    if(subNode.nodeName=="shortname"){
                                        name = subNode.textContent;
                                    }else if(subNode.nodeName=="name"){
                                        fullname = subNode.textContent;
                                    }
                                    
                                    subNode = subNode.nextSibling;
                                }
                                if(name!=""){
                                    obj[name] = fullname;
                                }
                            }
                            node = node.nextSibling;
                        }
                    }
                    fn(obj);
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when loading policies.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }
            }
        });
    },
    createMessage:function(title, message){
        var clear = false;

        if(windowManager.windowExists(title)){
            clear = true;
        }

        var msg = windowManager.createWindow(title);

        msg.setHeight(120);
        msg.setWidth(400);
        msg.setX(100);
        msg.setY(100);
        msg.setTopMost(1500);

        if(clear){
            msg.setContent("");
        }

        var msgContainer = document.createElement("div");
        msgContainer.innerHTML = message;

        msg.addElement(msgContainer);
        
        var closeButton = document.createElement("input");
        closeButton.setAttribute("type", "button");
        closeButton.setAttribute("value", "OK");

        closeButton.addEventListener("click",function(){
            windowManager.closeWindow(msg);
        }, false);

        msg.addElement(closeButton);
        
    },
    getPolicySetChilds:function(parent){
        engine.call("PolicyAdminServlet?action=getPolicySet&policySetId=" + parent, "GET", function(request){
            if(request.readyState == 4){ // Ready
                if(request.status==200){ // OK
                    node = request.responseXML.firstChild;
                    //windowManager.createWindow(windowName, false);

                    var root = engine.createNodes(node, false);

                    engine.positionNodes(root);

                    nodeManager.drawOnCanvas();
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when loading policies.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }
            }
        });
    },
    getPolicyChilds:function(parent){
        engine.call("PolicyAdminServlet?action=getPolicy&policyId=" + parent, "GET", function(request){
            if(request.readyState == 4){ // Ready
                if(request.status==200){ // OK
                    node = request.responseXML.firstChild;
                    //windowManager.createWindow(windowName, false);

                    var root = engine.createNodes(node, false);

                    engine.positionNodes(root);

                    nodeManager.drawOnCanvas();
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when loading policies.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }
            }
        });

    },
    getRootPolicy:function(){
        engine.call("PolicyAdminServlet", "GET", function(request){
            
            if(request.readyState == 4){ // Ready
                if(request.status==200){ // OK
                    node = request.responseXML.firstChild;
                    //windowManager.createWindow(windowName, false);

                    var root = engine.createNodes(node, true);

                    engine.positionNodes(root);

                    nodeManager.drawOnCanvas();
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when loading policies.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }
            }
        });
    },
    removePolicySet:function(id){
        engine.call("PolicyAdminServlet", "POST", function(request){

            if(request.readyState == 4){ // Ready
                if(request.status==200){ // OK
                    node = request.responseXML.firstChild;
                    //windowManager.createWindow(windowName, false);

                    var root = engine.createNodes(node, true);

                    engine.positionNodes(root);

                    nodeManager.drawOnCanvas();
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when loading policies.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }
            }
        });
    },
    getTarget:function(elementType, elementId, fn){
        var elementTypeId;

        if(elementType=="PolicySet"){
            elementTypeId="policySetId";
        }

        if(elementType=="Policy"){
            elementTypeId="policyId";
        }

        if(elementType=="Rule"){
            elementTypeId="ruleId";
        }

        engine.call("PolicyAdminServlet?action=get" + elementType + "&" + elementTypeId + "=" + elementId, "GET", function(request){
            if(request.readyState == 4){ // Ready
                if(request.status==200){ // OK
                    if(request.responseXML!=null && request.responseXML.firstChild!=null){
                        node = request.responseXML.firstChild;

                        if(node.nodeName=="xacml:PolicySet" ||
                            node.nodeName=="xacml:Policy" ||
                            node.nodeName=="xacml:Rule"){

                            node = node.firstChild;

                            while(node.nodeName != "xacml:Target"){
                                node = node.nextSibling;
                            }

                            //Only if it finds target element
                            if(node.nodeName == "xacml:Target"){
                                fn(node);
                            }else{
                                engine.createMessage("Error!", "Problem occured when loading elements.<br/>Please contact the administrator if the problem persists.<br/>Error: Element without target!");
                            }
                        }else{
                            engine.createMessage("Error!", "Problem occured when loading elements.<br/>Please contact the administrator if the problem persists.<br/>Error: Invalid element loaded");
                        }

                    }else{
                        engine.createMessage("Error!", "Problem occured when loading elements.<br/>Please contact the administrator if the problem persists.<br/>Error: Invalid response");
                    }

                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when loading policies.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }
            }
        });
    },
    createNodes:function(node, isRoot){
        var type="";
        /**
         * Creates a window for this node, and recursivelly calls this method
         * for the childs.
         * Returns a window to be connected to parent.
         **/
        if(node.nodeName=="xacml:PolicySet"){
            windowName="PolicySet:" + node.getAttribute("PolicySetId");
            type = "policyset";
        }else if(node.nodeName=="xacml:Policy"){
            windowName="Policy:" + node.getAttribute("PolicyId");
            type = "policy";
        }else if(node.nodeName=="xacml:Rule"){
            windowName="Rule:" + node.getAttribute("RuleId");
            type = "rule";
        }else{
            /**
             * Other elements shouldn't be treated as nodes
             **/
            return null;
        }
        
        if(isRoot==null){
            isRoot=false;
        }

        var thisWindow = windowManager.createWindow(windowName, !isRoot);
        thisWindow.setContent("");

        if(type=="rule"){
            var effect = document.createElement("div");
            effect.innerHTML = "Effect: <b>" + node.getAttribute("Effect") + "</b>";
            thisWindow.addElement(effect);
        }


        /**
         * Get path from root to this node with links to select parent nodes
         **/
        thisWindow.toString = function(){
            var res = "";

            //Get all parents from this node (there should be only one)
            var parents = nodeManager.getParentNodes(thisWindow);
            //If it has, then return it
            if(parents.length > 0){
                res = parents[0].toString() + " &gt; ";
            }
            return res + "<a href='#' onClick='windowManager.focusWindow(windowManager.getWindow(\"" + thisWindow.getName() + "\"));'>" + thisWindow.getName() + "</a>";
        };

        if(isRoot){
            thisWindow.setX(10);
            thisWindow.setY(10);
        }

        /**
         * When this window will be closed, close also the childs
         **/
        thisWindow.setOnClose(function(){
            var childWindows = nodeManager.getChildNodes(thisWindow);
            for(i in childWindows){
                windowManager.closeWindow(childWindows[i]);
            }
            nodeManager.disconnectNode(thisWindow);
        });
        
        //Now create the childs
        var child = node.firstChild;
        var childWindow;

        while(child!=null){
            if(child.nodeName=="xacml:Target"){
                var target = new Target(child);

                var elementTarget = target.toElement(false);
                thisWindow.addElement(elementTarget);
            }else if(child.nodeName=="xacml:Description"){
                var description = document.createElement("div");
                if(child.textContent.length > 20){
                    description.setAttribute("title",child.textContent);
                    description.innerHTML = "Hover here to see description";
                }else{
                    description.innerHTML = child.textContent;
                }
                thisWindow.addElement(description);
            }else{
                childWindow = engine.createNodes(child);
                if(childWindow!=null){
                    nodeManager.connectNodes(thisWindow, childWindow);

                }else{
                    console.debug("[Engine] Window was not created because " + child.nodeName + " is not a main policy tree element");
                }
            }
            child = child.nextSibling;
        }

        //Adjust window size
        thisWindow.adjustSize();

        return thisWindow;
    },
    createTarget:function(node){
        /***
         * DEPRECATED!!!
         **/
        if(node.nodeName != "xacml:Target"){
            return null;
        }

        var targetElement = document.createElement("div");
        targetElement.setAttribute("class", "xacmlTarget");

        var anyOfNode = node.firstChild;
        var firstAnyOf = true;
        while(anyOfNode != null){
            /**
             * All AnyOf elements should match to match the element Target.
             * There should be an 'And' between AnyOf elements
             **/
            
            if(anyOfNode.nodeName=="xacml:AnyOf"){
                /**
                 * Create AnyOf element
                 **/
                var anyOfElement = document.createElement("div");
                anyOfElement.setAttribute("class","xacmlAnyOf");

                /**
                 * Creates an 'And' separator
                 **/
                if(!firstAnyOf){
                    targetElement.appendChild(engine.createSeparator("And"));
                }else{
                    firstAnyOf = false;
                }

                targetElement.appendChild(anyOfElement);

                var allOfNode = anyOfNode.firstChild;
                var firstAllOf = true;
                while(allOfNode!=null){
                    /**
                     * At least one AllOf element should match to match the element AnyOf.
                     * There should be an 'Or' between all AllOf elements.
                     **/

                    if(allOfNode.nodeName=="xacml:AllOf"){
                        /**
                         * Create AllOf element
                         **/

                        var allOfElement = document.createElement("div");
                        allOfElement.setAttribute("class","xacmlAllOf");
                        
                        if(!firstAllOf){
                            anyOfElement.appendChild(engine.createSeparator("Or"));
                        }else{
                            firstAllOf = false;
                        }
                        
                        anyOfElement.appendChild(allOfElement);
                        

                        var matchNode = allOfNode.firstChild;
                        var firstMatch = true;
                        while(matchNode!=null){
                            /**
                             * All Match elements should match to match the element AllOf.
                             * There should be an 'And' between all Match elements.
                             **/

                            if(matchNode.nodeName=="xacml:Match"){
                                /**
                                 * Create Match element
                                 **/
                                var matchElement = document.createElement("div");
                                matchElement.setAttribute("class","xacmlMatch");

                                /*
                                if(!firstMatch){
                                    matchElement.appendChild(engine.createSeparator("And"));
                                }else{
                                    firstMatch = false;
                                }
                                 */

                                /**
                                 * MatchId - Which kind of match (e.g.: string-equal)
                                 **/
                                var matchId = engine.getUriSimplified(matchNode.attributes["MatchId"].nodeValue);

                                /**
                                 * Category can be subject, action, resource or environment.
                                 **/
                                var category;
                                
                                /**
                                 * Attribute can be AttributeDesignator or AttributeSelector.
                                 * AttributeValue type is ignored here.
                                 **/
                                var attType;

                                var attId;

                                /**
                                 * Value to be compared with attribute designator/selector,
                                 * using the 'match id' kind of comparision
                                 **/
                                var value;


                                /**
                                 * Fulfill Match information
                                 **/
                                var attribute = matchNode.firstChild;
                                while(attribute!=null){
                                
                                    if(attribute.nodeName == "xacml:AttributeDesignator"){
                                        attType = "designator";

                                        attId = attribute.attributes["AttributeId"].nodeValue;
                                        
                                        category = engine.getUriSimplified(attribute.attributes["Category"].nodeValue);
                                        if(category.indexOf("subject")!=-1){
                                            category = "subject";
                                        }else if(category.indexOf("action")!=-1){
                                            category = "action";
                                        }else if(category.indexOf("resource")!=-1){
                                            category = "resource";
                                        }else if(category.indexOf("environment")!=-1){
                                            category = "environment";
                                        }

                                    }else if(attribute.nodeName == "xacml:AttributeSelector"){
                                        attType = "selector";

                                        category = engine.getUriSimplified(attribute.attributes["Category"].nodeValue);
                                        if(category.indexOf("subject")!=-1){
                                            category = "subject";
                                        }else if(category.indexOf("action")!=-1){
                                            category = "action";
                                        }else if(category.indexOf("resource")!=-1){
                                            category = "resource";
                                        }else if(category.indexOf("environment")!=-1){
                                            category = "environment";
                                        }

                                    }else{
                                        /**
                                         * AttributeValue
                                         **/
                                        value = attribute.textContent;
                                    }

                                    attribute = attribute.nextSibling;
                                }

                                /**
                                 * Creates the Match element
                                 **/
                                
                                /**
                                 * Image representing the category
                                 **/
                                var img = document.createElement("img");
                                matchElement.appendChild(img);
                                if(attType=="designator"){
                                    img.setAttribute("src","./img/" + category + "_" + attId + ".png");
                                    matchElement.setAttribute("title","Meaning: " + attId + " must be " + matchId + " to " + value);
                                }else if(attType=="selector"){
                                    img.setAttribute("src","./img/selector.png");
                                }

                                /**
                                 * AttributeValue
                                 **/
                                img = document.createElement("t");
                                img.innerHTML=value;

                                matchElement.appendChild(img);

                                
                                allOfElement.appendChild(matchElement);

                            }else{
                                console.log("[Engine] Invalid element in AllOf of " + node.parent.getName());
                            //invalid element...
                            }
                            matchNode = matchNode.nextSibling;
                        }
                    }else{
                        console.log("[Engine] Invalid element in AnyOf of " + node.parent.getName());
                    //invalid element...
                    }
                    allOfNode = allOfNode.nextSibling;
                }
            }else{
                console.log("[Engine] Invalid element in Target of " + node.parent.getName());
            //invalid element...
            }
            anyOfNode = anyOfNode.nextSibling;
        }
        return targetElement;
    },
    getUriSimplified:function(uri){
        var words = uri.split(":");
        return words[(words.length-1)];
    },
    createSeparator:function(type){
        console.log("[Engine] create separator " + type);
        var sep = document.createElement("div");
        sep.setAttribute("class", "separator" + type);
        sep.innerHTML=type;
        return sep;
    },
    positionNodes:function(node){
        /**
         * Position has to be set after windows are created. That happens because
         * when a child window is created, parent is not positioned yet. This
         * causes the child window to be positioned in relation to the initial
         * position of its parent, not to the final one.
         **/
        var dif = node.getWidth() + 10;
        var posX = node.getX() + dif;
        var posY = node.getY();

        /**
         * Get child nodes from node manager
         **/
        var childs = nodeManager.getChildNodes(node);

        for(i in childs){
            childs[i].setX(posX);
            childs[i].setY(posY);
            //posY += 150;

            posY += 20;
            posX += 20;

            engine.positionNodes(childs[i]);
        }
    }
}

ToolBox = function(){
    var newWindow = windowManager.createWindow("Toolbox");
    
    newWindow.setContentWidth(450);
    newWindow.setContentHeight(50);
    newWindow.style.right = "10px";
    newWindow.style.bottom = "10px";
    newWindow.setTopMost(1000);
    newWindow.setContent("Select an element to show available options...");
    this.toolBoxWindow = newWindow;

    this.toolBoxWindow.setOnFocus(function(){
        //Cancels focus on this window (like this stays always on top and is never selected)
        return false;
    });


    this.setContext = function(context, id){
        var button;
        this.toolBoxWindow.setContent("");
        switch(context){
            case "xacml:PolicySet":
                this.toolBoxWindow.setName("ToolBox for policy set: " + id);

                /**
                 * Button to load childs
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Expand policy set");
                button.addEventListener("click", function(){
                    engine.getPolicySetChilds(id);
                }, false);

                this.toolBoxWindow.addElement(button);

                /**
                 * Button to remove policy set
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Remove policy set");
                button.addEventListener("click", function(){
                    if(confirm("Are you sure you want to remove \"" + id + "\" policy set?")){
                        removePolicySet(id);
                    }
                //else do nothing
                }, false);

                this.toolBoxWindow.addElement(button);

                /**
                 * Button to edit
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Target management");
                button.addEventListener("click", function(){
                    openTargetManagementWindow("PolicySet", id);
                //TODO - Edit policySet
                }, false);

                this.toolBoxWindow.addElement(button);

                /**
                 * Button to create policy set
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Create policy set");
                button.addEventListener("click", function(){
                    openCreatePolicySetWindow(id);
                }, false);

                this.toolBoxWindow.addElement(button);


                this.toolBoxWindow.addElement(button);

                /**
                 * Button to create policy
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Create policy");
                button.addEventListener("click", function(){
                    openCreatePolicyWindow(id);
                }, false);

                this.toolBoxWindow.addElement(button);

                break;
            case "xacml:Policy":
                this.toolBoxWindow.setName("ToolBox for policy: " + id);

                /**
                 * Button to load childs
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Expand policy");
                button.addEventListener("click", function(){
                    engine.getPolicyChilds(id);
                }, false);

                this.toolBoxWindow.addElement(button);

                /**
                 * Button to remove policy
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Remove policy");
                button.addEventListener("click", function(){
                    if(confirm("Are you sure you want to remove \"" + id + "\" policy?")){
                        removePolicy(id);
                    }
                //else do nothing
                }, false);

                this.toolBoxWindow.addElement(button);

                /**
                 * Button to edit
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Target management");
                button.addEventListener("click", function(){
                    //TODO - Edit policy
                    openTargetManagementWindow("Policy", id);
                }, false);

                this.toolBoxWindow.addElement(button);

                /**
                 * Button to create rule
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Create rule");
                button.addEventListener("click", function(){
                    openCreateRuleWindow(id);
                }, false);

                this.toolBoxWindow.addElement(button);

                break;
            case "xacml:Rule":
                this.toolBoxWindow.setName("ToolBox for rule: " + id);

                /**
                 * Button to remove rule
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Remove rule");
                button.addEventListener("click", function(){
                    if(confirm("Are you sure you want to remove \"" + id + "\" rule?")){
                        removeRule(id);
                    }
                //else do nothing
                }, false);

                this.toolBoxWindow.addElement(button);

                /**
                 * Button to edit
                 **/
                button = document.createElement("input");
                button.setAttribute("type","button");
                button.setAttribute("value","Target management");
                button.addEventListener("click", function(){
                    openTargetManagementWindow("Rule", id);
                }, false);

                this.toolBoxWindow.addElement(button);

                break;
        }
    }
}

// Wait to load the page (there are required elements to be loaded)
window.addEventListener("DOMContentLoaded", function(){
    var content = document.getElementById("windowContainer");
    var canvas = document.getElementById('connector');
    
    nodeManager.init(canvas);

    windowManager.init(content);
    /**
     * When a window moves, canvas is redrawn
     **/
    windowManager.setOnMove(nodeManager.drawOnCanvas);

    /**
     * When a window closes, canvas is redrawn
     **/
    windowManager.setOnClose(function(){
        /**
         * Waits 60 milliseconds so that canvas is redrawn only after all windows
         * and its childs are closed
         */
        setTimeout(nodeManager.drawOnCanvas,60);
    });

    /**
     * Create a toolbox
     **/
    var t = new ToolBox();

    windowManager.setOnFocus(function(w){
        var wName = w.getName();
        var args;
        var type;
        var id;

        document.getElementById("subtitle").innerHTML=w.toString();

        if(wName.match("PolicySet*")){
            type = "xacml:PolicySet";
            args = wName.split(":");
            id = args[args.length-1];
        }else if(wName.match("Policy*")){
            type = "xacml:Policy";
            args = wName.split(":");
            id = args[args.length-1];
        } else if(wName.match("Rule*")){
            type = "xacml:Rule";
            args = wName.split(":");
            id = args[args.length-1];
        }

        t.setContext(type, id);

        return true;
    });

    t.toolBoxWindow.setOnFocus(function(){
        /**
         * Cancels focus. As window specific procedures are called first,
         * windowManager.onFocus() is never called.
         **/
        return false;
    });

    /**
     * Get root policy using AJAX
     */
    engine.getRootPolicy();

//windowManager.closeWindow(load);
},false);

function openSettingsWindow(){
    var wSett = windowManager.createWindow("Settings", true, 100, 100);
    wSett.setTopMost(1001);
    wSett.setOnFocus(function(){
        //Not to be selected
        return false;
    });

/**
     * Load settings from AJAX
     */
}

function openCreateRuleWindow(policyId){
    var background = document.createElement("div");
    background.style.zIndex = 1002;
    background.style.width = "100%";
    background.style.height = "100%";
    background.style.top = "0px";
    background.style.left = "0px";
    background.style.backgroundImage = "url(img/windowbackground.png)";
    background.style.position = "absolute";
    windowManager.container.appendChild(background);

    var createWindow = windowManager.createWindow("Create Rule", true);
    createWindow.setTopMost(1003);
    createWindow.setOnFocus(function(){
        return false;
    });
    createWindow.setOnClose(function(){
        windowManager.container.removeChild(background);
    });

    var struct = document.createElement("table");
    var td;
    var tr;
    var input;
    var option;
    createWindow.setContent("");

    createWindow.addElement(struct);

    // New row --------------------------------- Rule name
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.style.width = "100px";
    td.innerHTML = "Rule ID";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","text");
    var ruleName = input;
    td.appendChild(input);
    input = document.createElement("b");
    input.innerHTML = " Must be unique!";
    input.style.fontSize = "10px";
    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Effect
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Effect";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("select");
    var effect = input;

    option = document.createElement("option");
    option.innerHTML = "Permit";
    input.appendChild(option);

    option = document.createElement("option");
    option.innerHTML = "Deny";
    input.appendChild(option);

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Description
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Description";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("textarea");
    var description = input;
    input.style.width = "220px";
    input.style.height= "80px";

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Save/Cancel
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    td.style.textAlign = "right";

    // Save button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Save and continue");
    input.addEventListener("click", function(){
        var content = "action=createRule" +
        "&parentPolicy=" + policyId +
        "&ruleId=" + ruleName.value +
        "&effect=" + effect.value +
        "&description=" + description.value;

        engine.call("PolicyAdminServlet", "POST", function(ev){
            // Saved!!!
            if(ev.readyState == 4){ // Ready
                if(ev.status==200){ // OK

                    var result = ev.responseXML.firstChild;
                    
                    if(result != null){
                        /**
                         * First child is the status.
                         * Second child is an error message, if exists.
                         */
                        if(result.firstChild.textContent == "true"){

                            /**
                             * TODO:
                             *  Steps:
                             *      - Save Rule
                             *      - Close window
                             *      - Create a Target Creation window
                             *      - Save Target
                             *      - Close window
                             *      - parentPolicy -> getChilds
                             */
                            engine.getPolicyChilds(policyId);
                            openTargetManagementWindow("Rule", ruleName.value);
                        }else{
                            alert("Error\n" + result.firstChild.nextSibling.textContent);
                            //Doesn't close the window
                            return;
                        }
                    }
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when saving rule.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }

                windowManager.closeWindow(createWindow);
            }
        }, content);
    }, false);

    td.appendChild(input);

    //Cancel button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Cancel");
    
    input.addEventListener("click", function(){
        windowManager.closeWindow(createWindow);
    }, false);

    td.appendChild(input);

    tr.appendChild(td);

    struct.appendChild(tr);

    var height = struct.offsetHeight;
    var width = struct.offsetWidth;

    createWindow.setContentHeight(height);
    createWindow.setContentWidth(width);

    height = createWindow.getHeight();
    width = createWindow.getWidth();

    createWindow.setY((windowManager.container.offsetHeight/2)-(height/2));
    createWindow.setX((windowManager.container.offsetWidth/2)-(width/2));
}

/**
 * Creates a window for policy creation.
 *
 * @param policySetId Identifier of parent policy set
 **/
function openCreatePolicyWindow(policySetId){
    var background = document.createElement("div");
    background.style.zIndex = 1002;
    background.style.width = "100%";
    background.style.height = "100%";
    background.style.top = "0px";
    background.style.left = "0px";
    background.style.backgroundImage = "url(img/windowbackground.png)";
    background.style.position = "absolute";
    windowManager.container.appendChild(background);

    var createWindow = windowManager.createWindow("Create Policy", true);
    createWindow.setTopMost(1003);
    createWindow.setOnFocus(function(){
        return false;
    });
    createWindow.setOnClose(function(){
        windowManager.container.removeChild(background);
    });

    var struct = document.createElement("table");
    var td;
    var tr;
    var input;
    var option;
    createWindow.setContent("");

    createWindow.addElement(struct);

    // New row --------------------------------- Policy Id
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.style.width = "100px";
    td.innerHTML = "Policy ID";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","text");
    var policyId = input;
    td.appendChild(input);
    input = document.createElement("b");
    input.innerHTML = " Must be unique!";
    input.style.fontSize = "10px";
    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Version
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.style.width = "100px";
    td.innerHTML = "Version";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","text");
    var version = input;
    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Rule combining algorithm
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Rule comb. algorithm";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("select");
    var ruleCombAlg = input;

    //TODO: Get rule combining algorithm's from servlet
    //for each{
    option = document.createElement("option");
    option.setAttribute("value","urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:permit-overrides");
    option.innerHTML = "permit-overrides";
    input.appendChild(option);
    //}

    option = document.createElement("option");
    option.setAttribute("value","urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:deny-overrides");
    option.innerHTML = "deny-overrides";
    input.appendChild(option);

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Description
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Description";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("textarea");
    var description = input;
    input.style.width = "220px";
    input.style.height= "80px";

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Save/Cancel
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    td.style.textAlign = "right";

    // Save button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Save and continue");
    input.addEventListener("click", function(){
        var content = "action=createPolicy" +
        "&parentPolicySet=" + policySetId +
        "&policyId=" + policyId.value +
        "&version=" + version.value +
        "&ruleCombiningAlgorithm=" + ruleCombAlg.value +
        "&description=" + description.value;

        engine.call("PolicyAdminServlet", "POST", function(ev){
            // Saved!!!
            if(ev.readyState == 4){ // Ready
                if(ev.status==200){ // OK

                    var result = ev.responseXML.firstChild;

                    if(result != null){
                        /**
                         * First child is the status.
                         * Second child is an error message, if exists.
                         */
                        if(result.firstChild.textContent == "true"){

                            /**
                             * TODO:
                             *  Steps:
                             *      - Save
                             *      - Close window
                             *      - Create a Target Creation window
                             *      - Save
                             *      - Close window
                             *      - parentPolicy -> getChilds
                             */
                            engine.getPolicySetChilds(policySetId);
                            openTargetManagementWindow("Policy", policyId.value);
                        }else{
                            alert("Error\n" + result.firstChild.nextSibling.textContent);
                            //Doesn't close the window
                            return;
                        }
                    }
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when saving rule.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }

                windowManager.closeWindow(createWindow);
            }
        }, content);
    }, false);

    td.appendChild(input);

    //Cancel button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Cancel");

    input.addEventListener("click", function(){
        windowManager.closeWindow(createWindow);
    }, false);

    td.appendChild(input);

    tr.appendChild(td);

    struct.appendChild(tr);

    var height = struct.offsetHeight;
    var width = struct.offsetWidth;

    createWindow.setContentHeight(height);
    createWindow.setContentWidth(width);

    height = createWindow.getHeight();
    width = createWindow.getWidth();

    createWindow.setY((windowManager.container.offsetHeight/2)-(height/2));
    createWindow.setX((windowManager.container.offsetWidth/2)-(width/2));
}


/**
 * Creates a window for policy creation.
 *
 * @param policySetId Identifier of parent policy set
 **/
function openCreatePolicySetWindow(policySetId){
    var background = document.createElement("div");
    background.style.zIndex = 1002;
    background.style.width = "100%";
    background.style.height = "100%";
    background.style.top = "0px";
    background.style.left = "0px";
    background.style.backgroundImage = "url(img/windowbackground.png)";
    background.style.position = "absolute";
    windowManager.container.appendChild(background);

    var createWindow = windowManager.createWindow("Create Policy Set", true);
    createWindow.setTopMost(1003);
    createWindow.setWidth(500);
    createWindow.setOnFocus(function(){
        return false;
    });
    createWindow.setOnClose(function(){
        windowManager.container.removeChild(background);
    });

    var struct = document.createElement("table");
    var td;
    var tr;
    var input;
    var option;
    createWindow.setContent("");

    createWindow.addElement(struct);

    // New row --------------------------------- PolicySet Id
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.style.width = "160px";
    td.innerHTML = "PolicySet ID";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","text");
    var newPolicySetId = input;
    td.appendChild(input);
    input = document.createElement("b");
    input.innerHTML = " Must be unique!";
    input.style.fontSize = "10px";
    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Version
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Version";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","text");
    var version = input;
    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Policy combining algorithm
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Policy comb. algorithm";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("select");
    var policyCombAlg = input;

    //for each{
    
    option = document.createElement("option");
    option.setAttribute("value","");
    option.innerHTML = "Retreiving algorithms...";
    input.appendChild(option);
    
    //}

    engine.getResources("combiningAlgorithms", "PolicyCombiningAlgorithm", function(obj){
        
        policyCombAlg.innerHTML="";
        for(name in obj){
            option = document.createElement("option");
            option.setAttribute("value",obj[name]);
            option.innerHTML = name;
            policyCombAlg.appendChild(option);
        }
    });

    

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Description
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Description";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("textarea");
    var description = input;
    input.style.width = "220px";
    input.style.height= "80px";

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Save/Cancel
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    td.style.textAlign = "right";

    // Save button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Save and continue");
    input.addEventListener("click", function(){
        var content = "action=createPolicySet" +
        "&parentPolicySet=" + policySetId +
        "&policySetId=" + newPolicySetId.value +
        "&version=" + version.value +
        "&policyCombiningAlgorithm=" + policyCombAlg.value +
        "&description=" + description.value;

        engine.call("PolicyAdminServlet", "POST", function(ev){
            // Saved!!!
            if(ev.readyState == 4){ // Ready
                if(ev.status==200){ // OK

                    var result = ev.responseXML.firstChild;

                    if(result != null){
                        /**
                         * First child is the status.
                         * Second child is an error message, if exists.
                         */
                        if(result.firstChild.textContent == "true"){

                            /**
                             * TODO:
                             *  Steps:
                             *      - Save
                             *      - Close window
                             *      - Create a Target Creation window
                             *      - Save
                             *      - Close window
                             *      - parentPolicy -> getChilds
                             */
                            engine.getPolicySetChilds(policySetId);
                            openTargetManagementWindow("PolicySet", newPolicySetId.value);
                        }else{
                            alert("Error\n" + result.firstChild.nextSibling.textContent);
                            //Doesn't close the window
                            return;
                        }
                    }
                }else if(request.status == 500){
                    engine.createMessage("Error!", "Problem occured when saving rule.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                }

                windowManager.closeWindow(createWindow);
            }
        }, content);
    }, false);

    td.appendChild(input);

    //Cancel button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Cancel");

    input.addEventListener("click", function(){
        windowManager.closeWindow(createWindow);
    }, false);

    td.appendChild(input);

    tr.appendChild(td);

    struct.appendChild(tr);

    var height = struct.offsetHeight;
    var width = struct.offsetWidth;

    createWindow.setContentHeight(height);
    createWindow.setContentWidth(width);

    height = createWindow.getHeight();
    width = createWindow.getWidth();

    createWindow.setY((windowManager.container.offsetHeight/2)-(height/2));
    createWindow.setX((windowManager.container.offsetWidth/2)-(width/2));
}

function updateTarget(elementType, elementId, targetContainer, targetWindow){

    engine.getTarget(elementType, elementId, function(node){
    
        try{
            var target = new Target(node, function(){
                targetContainer.innerHTML = "";
                targetContainer.appendChild(target.toElement(true));
                
                var buttonSave;
                var buttonCancel;
                var div;

                div = document.createElement("div");

                buttonSave = document.createElement("input");
                buttonSave.setAttribute("type","button");
                buttonSave.setAttribute("value","Save");

                var content = "action=set" + elementType + "Target";
                content += "&" + elementType + "Id=" + elementId;
                content += "&element=" + target.toString();

                buttonSave.addEventListener("click", function(){
                    engine.call("PolicyAdminServlet", "POST", function(ev){
        
                        if(ev.readyState == 4){ // Ready
                            if(ev.status==200){ // OK

                                var result = ev.responseXML.firstChild;

                                if(result != null){
                                    /**
                                     * First child is the status.
                                     * Second child is an error message, if exists.
                                     */
                                    if(result.firstChild.textContent == "true"){
                                        windowManager.closeWindow(targetWindow);
                                    }else{
                                        alert("Error\n" + result.firstChild.nextSibling.textContent);
                                    }
                                }
                            }else if(request.status == 500){
                                engine.createMessage("Error!", "Problem occured when saving the target.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
                            }
                        }
                    }, content);

                }, false);

                div.appendChild(buttonSave);

                buttonCancel = document.createElement("input");
                buttonCancel.setAttribute("type","button");
                buttonCancel.setAttribute("value","Cancel");

                buttonCancel.addEventListener("click", function(){
                    windowManager.closeWindow(targetWindow);
                }, false);

                div.appendChild(buttonCancel);

                targetContainer.appendChild(div);

                targetWindow.setContentWidth(targetContainer.offsetWidth);
                targetWindow.setContentHeight(targetContainer.offsetHeight);
            });

            if(target != null){
                target.update();

            }else{
                targetContainer.innerHTML = "Error loading target...";
            }

        }catch(ex){
            console.log(ex);
        }

    //targetContainer.appendChild(engine.createTarget(node));
    });
}

/**
 * Creates a window for policy creation.
 *
 * @param elementType Target's parent element type (PolicySet, Policy or Rule)
 * @param elementId Identifier of parent element
 * @param background Background of the window. Its used to block interactions with the nodes.
 **/
function openTargetManagementWindow(elementType, elementId, background){
    //If its not created, creates one
    if(background==null){
        background = document.createElement("div");
        background.style.zIndex = 1002;
        background.style.width = "100%";
        background.style.height = "100%";
        background.style.top = "0px";
        background.style.left = "0px";
        background.style.backgroundImage = "url(img/windowbackground.png)";
        background.style.position = "absolute";
        windowManager.container.appendChild(background);
    }
    var createWindow = windowManager.createWindow("Target management: " + elementType + ":" + elementId, true);
    createWindow.setContentWidth(900);
    createWindow.setTopMost(1003);
    createWindow.setOnFocus(function(){
        return false;
    });
    createWindow.setOnClose(function(){
        windowManager.container.removeChild(background);
    });

    var struct = document.createElement("table");
    struct.style.width = "100%";
    var td;
    var tr;
    createWindow.setContent("");

    createWindow.addElement(struct);

    tr = document.createElement("tr");
    struct.appendChild(tr);

    // New column -> Where target will be
    td = document.createElement("td");
    tr.appendChild(td);

    td.innerHTML = "Loading target...";

    updateTarget(elementType, elementId, td, createWindow);

    var height = struct.offsetHeight;
    var width = struct.offsetWidth;

    createWindow.setContentHeight(height);
    createWindow.setContentWidth(width);

    height = createWindow.getHeight();
    width = createWindow.getWidth();

    createWindow.setY((windowManager.container.offsetHeight/2)-(height/2));
    createWindow.setX((windowManager.container.offsetWidth/2)-(width/2));
}

function openCreateMatchWindow(instance){
    var createWindow = windowManager.createWindow("Create a match", true);
    createWindow.setTopMost(1005);
    createWindow.setWidth(1000);
    createWindow.setOnFocus(function(){
        return false;
    });

    var struct = document.createElement("table");
    var td;
    var tr;
    var input;
    var option;
    createWindow.setContent("");

    createWindow.addElement(struct);

    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.style.width = "180px";
    td.innerHTML = "Attribute source";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("select");
    var typeAttribute = input;

    input = document.createElement("option");
    input.setAttribute("value","AttributeDesignator");
    input.innerHTML = "Attribute Designator";
    typeAttribute.appendChild(input);

    input = document.createElement("option");
    input.setAttribute("value","AttributeSelector");
    input.innerHTML = "Attribute Selector";
    typeAttribute.appendChild(input);

    td.appendChild(typeAttribute);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Category
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Category";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("select");
    var category = input;

    option = document.createElement("option");
    option.setAttribute("value","urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
    option.innerHTML = "Resource";
    input.appendChild(option);

    option = document.createElement("option");
    option.setAttribute("value","urn:oasis:names:tc:xacml:3.0:attribute-category:action");
    option.innerHTML = "Action";
    input.appendChild(option);

    option = document.createElement("option");
    option.setAttribute("value","urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
    option.innerHTML = "Access-subject";
    input.appendChild(option);

    option = document.createElement("option");
    option.setAttribute("value","urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject");
    option.innerHTML = "Recipient-subject";
    input.appendChild(option);

    option = document.createElement("option");
    option.setAttribute("value","urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject");
    option.innerHTML = "Intermediary-subject";
    input.appendChild(option);


    engine.getResources("dataTypes", "DataTypes", function(obj){

        dataType.innerHTML="";
        for(name in obj){
            option = document.createElement("option");
            option.setAttribute("value",obj[name]);
            option.innerHTML = name;
            dataType.appendChild(option);
        }

        var height = struct.offsetHeight;
        var width = struct.offsetWidth;

        createWindow.setContentHeight(height);
        createWindow.setContentWidth(width);

        height = createWindow.getHeight();
        width = createWindow.getWidth();
    });

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);



    // New row --------------------------------- Attribute ID
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Attribute ID";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","text");
    var attributeId = input;
    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Match id
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Comparision method";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("select");
    var matchId = input;


    option = document.createElement("option");
    option.setAttribute("value","");
    option.innerHTML = "Retreiving datatypes...";
    input.appendChild(option);

    engine.getResources("dataTypes", "Functions", function(obj){

        matchId.innerHTML="";
        for(name in obj){
            option = document.createElement("option");
            option.setAttribute("value",obj[name]);
            option.innerHTML = name;
            matchId.appendChild(option);
        }


        var height = struct.offsetHeight;
        var width = struct.offsetWidth;

        createWindow.setContentHeight(height);
        createWindow.setContentWidth(width);

        height = createWindow.getHeight();
        width = createWindow.getWidth();
    });

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Data type
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Data type";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("select");
    var dataType = input;

    option = document.createElement("option");
    option.setAttribute("value","");
    option.innerHTML = "Retreiving datatypes...";
    input.appendChild(option);


    engine.getResources("dataTypes", "DataTypes", function(obj){

        dataType.innerHTML="";
        for(name in obj){
            option = document.createElement("option");
            option.setAttribute("value",obj[name]);
            option.innerHTML = name;
            dataType.appendChild(option);
        }

        var height = struct.offsetHeight;
        var width = struct.offsetWidth;

        createWindow.setContentHeight(height);
        createWindow.setContentWidth(width);

        height = createWindow.getHeight();
        width = createWindow.getWidth();
    });

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- AttributeValue
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Expected value";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","text");
    var value = input;
    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Must Be Present
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    td.innerHTML = "Must be in response";
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    input = document.createElement("input");
    input.setAttribute("type","checkbox");
    var mustBePresent = input;

    td.appendChild(input);
    tr.appendChild(td);

    struct.appendChild(tr);

    // New row --------------------------------- Save/Cancel
    tr = document.createElement("tr");

    // New column
    td = document.createElement("td");
    tr.appendChild(td);

    //New column
    td = document.createElement("td");
    td.style.textAlign = "right";

    // Save button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Continue");
    input.addEventListener("click", function(){
        var match = new Match(null, instance.onUpdate);
        match.matchId = matchId.value;

        if(typeAttribute.value == "AttributeDesignator"){
            match.attDesOrSel = new AttributeDesignator(null, instance.onUpdate);
            match.attDesOrSel.category = category.value;
            match.attDesOrSel.attributeId = attributeId.value;
            match.attDesOrSel.dataType = dataType.value;
            match.attDesOrSel.mustBePresent = (mustBePresent.value=="on")?true:false;
        } else {
        }

        match.attValue = new AttributeValue(null, instance.onUpdate);
        match.attValue.dataType = dataType.value;
        match.attValue.value = value.value;

        instance.addMatch(match);

        instance.update();
        windowManager.closeWindow(createWindow);
    }, false);

    td.appendChild(input);

    //Cancel button
    input = document.createElement("input");
    input.setAttribute("type", "button");
    input.setAttribute("value", "Cancel");

    input.addEventListener("click", function(){
        windowManager.closeWindow(createWindow);
    }, false);

    td.appendChild(input);

    tr.appendChild(td);

    struct.appendChild(tr);

    var height = struct.offsetHeight;
    var width = struct.offsetWidth;

    createWindow.setContentHeight(height);
    createWindow.setContentWidth(width);

    height = createWindow.getHeight();
    width = createWindow.getWidth();

    createWindow.setY((windowManager.container.offsetHeight/2)-(height/2));
    createWindow.setX((windowManager.container.offsetWidth/2)-(width/2));

}

function removePolicySet(policySetId){
    var content = "action=removePolicySet";
    content += "&policySetId=" + policySetId;
    engine.call("PolicyAdminServlet", "POST", function(ev){
        
        if(ev.readyState == 4){ // Ready
            if(ev.status==200){ // OK

                var result = ev.responseXML.firstChild;

                if(result != null){
                    /**
                         * First child is the status.
                         * Second child is an error message, if exists.
                         */
                    if(result.firstChild.textContent == "true"){
                        windowManager.closeWindow(windowManager.getWindow("PolicySet:" + policySetId));
                    }else{
                        alert("Error\n" + result.firstChild.nextSibling.textContent);
                    }
                }
            }else if(request.status == 500){
                engine.createMessage("Error!", "Problem occured when saving rule.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
            }
        }
    }, content);
}

function removePolicy(policyId){
    var content = "action=removePolicy";
    content += "&policyId=" + policyId;
    engine.call("PolicyAdminServlet", "POST", function(ev){

        if(ev.readyState == 4){ // Ready
            if(ev.status==200){ // OK

                var result = ev.responseXML.firstChild;

                if(result != null){
                    /**
                         * First child is the status.
                         * Second child is an error message, if exists.
                         */
                    if(result.firstChild.textContent == "true"){
                        windowManager.closeWindow(windowManager.getWindow("Policy:" + policyId));
                    }else{
                        alert("Error\n" + result.firstChild.nextSibling.textContent);
                    }
                }
            }else if(request.status == 500){
                engine.createMessage("Error!", "Problem occured when saving rule.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
            }
        }
    }, content);
}

function removeRule(ruleId){
    var content = "action=removeRule";
    content += "&ruleId=" + ruleId;
    engine.call("PolicyAdminServlet", "POST", function(ev){

        if(ev.readyState == 4){ // Ready
            if(ev.status==200){ // OK

                var result = ev.responseXML.firstChild;

                if(result != null){
                    /**
                         * First child is the status.
                         * Second child is an error message, if exists.
                         */
                    if(result.firstChild.textContent == "true"){
                        windowManager.closeWindow(windowManager.getWindow("Rule:" + ruleId));
                    }else{
                        alert("Error\n" + result.firstChild.nextSibling.textContent);
                    }
                }
            }else if(request.status == 500){
                engine.createMessage("Error!", "Problem occured when saving rule.<br/>Please contact the administrator if the problem persists.<br/>Error: 500");
            }
        }
    }, content);
}

/****
 * START Target elements
 */
// Target's constructor
function Target(node, onUpdate){

    if(node.nodeName!="xacml:Target"){
        throw new ExceptionCode("Invalid target element");
    }


    if(onUpdate != null){
        this.onUpdate = onUpdate;
    }

    this.anyOf = new Array();

    if(node != null){
        var newAnyOf;
        node = node.firstChild;
        while(node != null){
            try{
                newAnyOf = new AnyOf(node, onUpdate);
                if(newAnyOf!=null)
                    this.anyOf.push(newAnyOf);
            }catch(ex){
            //Do nothing
            }
            node = node.nextSibling;
        }
    }
}

Target.prototype.addAnyOf = function(anyOf){
    this.anyOf.push(anyOf);
}

Target.prototype.toString = function(){
    var xml = "";

    xml += "<xacml:Target>";
        
    for(i in this.anyOf){
        xml += this.anyOf[i].toString();
    }
        
    xml += "</xacml:Target>";

    return xml;
}

/**
     * Creates a table with the elements
     *
     * @param edit True if there should be administration buttons to edit
     **/
Target.prototype.toElement = function(edit) {
    var element = document.createElement("div");
    element.setAttribute("class","xacmlTarget");
    var first = true;
    var separator;

    for(i in this.anyOf){
        if(first){
            first = false;
        }else{
            //Create a separator between groups with AND word
            separator = document.createElement("div");
            element.appendChild(separator);
            separator.innerHTML = "AND";
        }
        element.appendChild(this.anyOf[i].toElement(edit));
    }

    if(edit){
        var addGroup = document.createElement("input");
        addGroup.setAttribute("type", "button");
        addGroup.setAttribute("value","Create obligatory target");

        /***
         * Required as javascript loses instance context
         **/
        var instance = this;

        addGroup.addEventListener("click", function(){
            instance.addAnyOf(new AnyOf(null, instance.onUpdate));
            instance.update();
        }, false);


        separator = document.createElement("div");

        element.appendChild(separator);

        separator.appendChild(addGroup);
    }

    return element;
}

Target.prototype.update = function(){
    if(this.onUpdate != null){
        this.onUpdate();
    }
}

// AnyOf's constructor
function AnyOf(node, onUpdate){
    this.allOf = new Array();
    if(onUpdate != null){
        this.onUpdate = onUpdate;
    }
    
    if(node != null){
        if(node.nodeName!="xacml:AnyOf"){
            throw new ExceptionCode("Invalid AnyOf element");
        }

        var newAllOf
        node = node.firstChild;
        while(node != null){
            try{
                newAllOf = new AllOf(node, onUpdate);
                if(newAllOf!=null)
                    this.allOf.push(newAllOf);
            }catch(ex){
            //Do nothing
            }
            node = node.nextSibling;
        }
    }

}
    
AnyOf.prototype.addAllOf = function(allOf){
    this.allOf.push(allOf);
}

AnyOf.prototype.toString = function(){
    var xml = "";

    xml += "<xacml:AnyOf>";
        
    for(i in this.allOf){
        xml += this.allOf[i].toString();
    }

    xml += "</xacml:AnyOf>";

    return xml;
}

AnyOf.prototype.update = function(){
    if(this.onUpdate != null){
        this.onUpdate();
    }
}
/**
     * Creates a row with the groups
     *
     * @param edit True if there should be administration buttons to edit
     **/
AnyOf.prototype.toElement = function(edit) {
    var element = document.createElement("div");
    element.setAttribute("class","xacmlAnyOf");
    var first = true;
    var separator;

    for(i in this.allOf){
        if(first){
            first = false;
        }else{
            //Create a separator between groups with AND word
            separator = document.createElement("div");
            element.appendChild(separator);

            separator.innerHTML = "OR";
        }
        element.appendChild(this.allOf[i].toElement(edit));
    }

    if(edit){
        var addGroup = document.createElement("input");
        addGroup.setAttribute("type", "button");
        addGroup.setAttribute("value","Create alternative group");

        /***
         * Required as javascript loses instance context
         **/
        var instance = this;

        addGroup.addEventListener("click", function(){
            instance.addAllOf(new AllOf(null, instance.onUpdate));
            instance.update();
        }, false);

        separator = document.createElement("div");

        element.appendChild(separator);

        separator.appendChild(addGroup);
    }

    return element;
}

// AllOf's constructor
function AllOf(node, onUpdate){

    if(onUpdate != null){
        this.onUpdate = onUpdate;
    }
    this.match = new Array();

    if(node != null){

        if(node.nodeName!="xacml:AllOf"){
            throw new ExceptionCode("Invalid AllOf element");
        }

        if(node!=null){
            var newMatch;
            node = node.firstChild;
            while(node != null){
                try{
                    newMatch = new Match(node, onUpdate);
                    if(newMatch!=null)
                        this.match.push(newMatch);
                }catch(ex){
                //Do nothing
                }
                node = node.nextSibling;
            }
        }
    }
}

AllOf.prototype.addMatch = function(match){
    this.match.push(match);
}
    
AllOf.prototype.update = function(){
    if(this.onUpdate != null){
        this.onUpdate();
    }
}

AllOf.prototype.toString = function(){
    var xml = "";

    xml += "<xacml:AllOf>";

    for(i in this.match){
        xml += this.match[i].toString();
    }

    xml += "</xacml:AllOf>";

    return xml;
}

/**
     * Creates a row with the matches
     *
     * @param edit True if there should be administration buttons to edit
     **/
AllOf.prototype.toElement = function(edit) {
    var element = document.createElement("div");
    element.setAttribute("class","xacmlAllOf");
    var separator;

    for(i in this.match){
        element.appendChild(this.match[i].toElement(edit));
    }

    if(edit){
        var addGroup = document.createElement("input");
        addGroup.setAttribute("type", "button");
        addGroup.setAttribute("value","Add match");
        
        /***
         * Required as javascript loses instance context
         **/
        var instance = this;

        addGroup.addEventListener("click", function(){
            openCreateMatchWindow(instance);
        //instance.addMatch(new Match(null, instance.onUpdate));
        //instance.update();
        }, false);
        
        separator = document.createElement("div");

        element.appendChild(separator);

        separator.appendChild(addGroup);
    }

    return element;
}

// Match's constructor
function Match(node, onUpdate){
    if(onUpdate != null){
        this.onUpdate = onUpdate;
    }

    if(node != null){
        if(node.nodeName!="xacml:Match"){
            throw new ExceptionCode("Invalid Match element");
        }

        this.matchId = node.attributes["MatchId"].nodeValue;

        node = node.firstChild;

        while(node!=null){
            if(node.nodeName=="xacml:AttributeValue"){
                this.attValue = new AttributeValue(node, onUpdate);
            }else if(node.nodeName=="xacml:AttributeDesignator"){
                this.attDesOrSel = new AttributeDesignator(node, onUpdate);
            }else if(node.nodeName=="xacml:AttributeSelector"){
                this.attDesOrSel = new AttributeSelector(node, onUpdate);
            }

            node = node.nextSibling;
        }
    }
}

Match.prototype.update = function(){
    if(this.onUpdate != null){
        this.onUpdate();
    }
}

/**
     * Creates a row with the matches
     *
     * @param edit True if there should be administration buttons to edit
     **/
Match.prototype.toElement = function(edit) {
    var element = document.createElement("div");
    element.setAttribute("class","xacmlMatch");

    var img = document.createElement("img");
    var imgFile = "";
    var category = "";

    var test = this.attDesOrSel.category;
    if(test == "urn:oasis:names:tc:xacml:3.0:attribute-category:resource"){

        if(this.attDesOrSel.attributeId == "file"){
            imgFile = "resource_file.png";
            category = "file";
        }else{
            imgFile = "resource.png";
            category = "resource";
        }
    }else if(test == "urn:oasis:names:tc:xacml:3.0:attribute-category:action"){
        imgFile = "action.png";
        category = "action";
    }else if(test == "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject" ||
        test == "urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject" ||
        test == "urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject"){

        if(this.attDesOrSel.attributeId == "group"){
            imgFile = "subject_group.png";
            category = "group";
        }else if(this.attDesOrSel.attributeId == "user"){
            imgFile = "subject_user.png";
            category = "user";
        }else{
            //TODO - create a more generic image for subject
            imgFile = "subject_user.png";
            category = "subject";
        }
    }

    img.setAttribute("src", "./img/" + imgFile);
    element.appendChild(img);

    var desc = document.createElement("t");
    desc.innerHTML = this.attValue.value;
    element.appendChild(desc);

    element.setAttribute("title", category + " must be " + engine.getUriSimplified(this.matchId) + " to " + this.attValue.value);

    return element;
        
    /*
        if(edit){
            var addGroup = document.createElement("input");
            addGroup.setAttribute("type", "button");
            addGroup.setAttribute("value","Add match");

            separator = document.createElement("div");

            element.appendChild(separator);

            separator.appendChild(addGroup);
        }
        */
    return element;
}

Match.prototype.toString = function(){
    var xml = "";

    xml += "<xacml:Match";

    xml +=" MatchId=\"" + this.matchId + "\"";

    xml += ">";

    xml += this.attValue.toString();
    xml += this.attDesOrSel.toString();
        
    xml += "</xacml:Match>";

    return xml;
}

// AttributeSelector's constructor
function AttributeSelector(node, onUpdate){
    if(onUpdate != null){
        this.onUpdate = onUpdate;
    }

    if(node!=null){
        if(node.nodeName!="xacml:AttributeSelector"){
            throw new ExceptionCode("Invalid AttributeSelector element");
        }
        this.category = node.attributes["Category"].nodeValue;
        this.path = node.attributes["Path"].nodeValue;
        this.dataType = node.attributes["DataType"].nodeValue;
        this.mustBePresent = node.attributes["MustBePresent"].nodeValue;
        this.value = node.textContent;
    }
}

AttributeSelector.prototype.update = function(){
    if(this.onUpdate != null){
        this.onUpdate();
    }
}

// AttributeDesignator's constructor
function AttributeDesignator(node, onUpdate){
    if(onUpdate != null){
        this.onUpdate = onUpdate;
    }

    
    if(node!=null){
        if(node.nodeName!="xacml:AttributeDesignator"){
            throw new ExceptionCode("Invalid AttributeDesignator element");
        }

        this.category = node.attributes["Category"].nodeValue;
        this.attributeId = node.attributes["AttributeId"].nodeValue;
        this.dataType = node.attributes["DataType"].nodeValue;
        this.mustBePresent = node.attributes["MustBePresent"].nodeValue;
    }
}

AttributeDesignator.prototype.update = function(){
    if(this.onUpdate != null){
        this.onUpdate();
    }
}

AttributeDesignator.prototype.toString = function(){
    var xml = "";

    xml += "<xacml:AttributeDesignator";

    xml += " Category=\"" + this.category + "\"";
    xml += " AttributeId=\"" + this.attributeId + "\"";
    xml += " DataType=\"" + this.dataType + "\"";
    xml += " MustBePresent=\"" + this.mustBePresent + "\"";
        
    xml += " />";

    return xml;
}

// AttributeValue's constructor
function AttributeValue(node, onUpdate){
    if(onUpdate != null){
        this.onUpdate = onUpdate;
    }
    if(node!=null){
        if(node.nodeName!="xacml:AttributeValue"){
            throw new ExceptionCode("Invalid AttributeValue element");
        }

        this.dataType = node.attributes["DataType"].nodeValue;
        this.value = node.textContent;
    }
}

AttributeValue.prototype.update = function(){
    if(this.onUpdate != null){
        this.onUpdate();
    }
}

AttributeValue.prototype.setValue = function(value){
    this.value = value;
}

AttributeValue.prototype.setDataType = function(dataType){
    this.dataType = dataType;
}

AttributeValue.prototype.toString = function(){
    var xml = "";

    xml += "<xacml:AttributeValue";

    xml += " DataType=\"" + this.dataType + "\"";

    xml += ">";

    xml += this.value; //Sub elements

    xml += "</xacml:AttributeValue>";
    return xml;
}

/****
 * END Target elements
 */