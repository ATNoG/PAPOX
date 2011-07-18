/*
//=============================================================================
// Brief   : Node's connection manager. Also draws connections on canvas
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

var nodeManager = {
    Connection:function(a, b){
        //Object Node is a connection between two nodes ('a' and 'b')
        this.a = a;
        this.b = b;

        this.getA = function(){
            return this.a;
        }

        this.getB = function(){
            return this.b;
        }
    },
    nodes:[],
    canvas:null,
    init:function(canvas){
        nodeManager.canvas = canvas;

        // Defines 'toString' function
        nodeManager.Connection.prototype.toString = function(){
            return this.a + '(' + this.a.x() + ',' + this.a.y() + ')' + ' - ' + this.b + '(' + this.b.x() + ',' + this.b.y() + ')';
        };

        // Defines 'is' function
        nodeManager.Connection.prototype.is = function(a, b){
            /**
             * Checks if this connection connects 'a' and 'b' nodes
             **/
            if(this.a===a && this.b===b)
                return true;
				
            if(this.b===a && this.a===b)
                return true;

            return false;
        }

        nodeManager.Connection.prototype.has = function(a){
            /**
             * Checks if a connection has a node
             **/
            if(this.a===a || this.b===a)
                return true;
				
            return false;
        }
    },
    connectNodes:function(node1, node2){
        //Check if its not a self-connection
        if(node1===node2)
            return;

        //If already exists, do nothing
        if(nodeManager.exist(node1, node2))
            return;

        //Creates unidirectional connection between two nodes
        nodeManager.nodes[nodeManager.nodes.length] = new nodeManager.Connection(node1, node2);
    },
    disconnectNodes:function(node1, node2){
        for(n in nodeManager.nodes){
            if(nodeManager.nodes[n].is(node1, node2)){
                nodeManager.removeNode(n);
                return;
            }
        }
    },
    disconnectNode:function(node){
        // Flag to check if something changed
        var mod = false;

        // Searches for a connection that has the node
        for(n in nodeManager.nodes){
            if(nodeManager.nodes[n].has(node)){
                mod = true;
                // Removes the node
                nodeManager.removeNode(n);
            }
        }

        if(mod)
            nodeManager.disconnectNode(node);
    },
    exist:function(node1,node2){
        //Checks if a pair already exists
        for(n in nodeManager.nodes){
            if(nodeManager.nodes[n].is(node1, node2))
                return true;
        }
			
        return false;
    },
    removeNode:function(n){

        /**
         * (To be used internally (like a private method). To disconnect a node, use nodeManager.disconnectNode instead)
         * 
         * Removes an element from the list, moving all next elements one position back.
         **/
        while(n<(nodeManager.nodes.length-1)){
            nodeManager.nodes[n] = nodeManager.nodes[++n];
        }
        
        //Excludes last element
        nodeManager.nodes.splice(n,1);
		
    },
    getChildNodes:function(node){
        //Considering that child node is 'b'
        var lst=[];
        for(n in nodeManager.nodes){
            if(nodeManager.nodes[n].getA() === node){
                lst.push(nodeManager.nodes[n].getB());
            }
        }
        return lst;
    },
    getParentNodes:function(node){
        //Considering that parent node is 'a'
        var lst=[];
        for(n in nodeManager.nodes){
            if(nodeManager.nodes[n].getB() === node){
                lst.push(nodeManager.nodes[n].getA());
            }
        }
        return lst;
    },
    drawOnCanvas:function(){
        //-------Avoid concurrence
        if(nodeManager.drawing)
            return;

        nodeManager.drawing = true;

        //------------------------
        
        try{
            // http://dev.opera.com/articles/view/html-5-canvas-the-basics/
            // http://thinkvitamin.com/code/how-to-draw-with-html-5-canvas/
            if(nodeManager.canvas.getContext) {
                var context = nodeManager.canvas.getContext('2d');
                context.canvas.width=nodeManager.canvas.parentNode.offsetWidth;
                context.canvas.height=nodeManager.canvas.parentNode.offsetHeight;
                context.strokeStyle = '#000000';
                context.lineWidth=1;
		
                for(n in nodeManager.nodes){
                    var node = nodeManager.nodes[n];
						
                    context.beginPath();
						
                    context.moveTo(node.a.centerPosition().x, node.a.centerPosition().y);
                    context.lineTo(node.b.centerPosition().x, node.b.centerPosition().y);
						
                    context.stroke();
                    context.closePath();
                }
					
            }
        }catch(err){
            console.debug(err);
        }

        //else {HTML5 canvas not supported}

        //----- Waits 50 milliseconds before the next draw
        setTimeout(function(){
            nodeManager.drawing = false;
        },50);
        
    },
    allToString:function(separator){
        var res = '';
        for(n in nodeManager.nodes){
            res+=nodeManager.nodes[n] + separator;
        }
        return res;
    }
}
//Waits for the window to load -> otherwise this script will be run when there are no elements
/*
window.addEventListener("DOMContentLoaded", function(){
	var canvas = document.getElementById('connector');
	
	nodeManager.init(canvas);
	
	//Uncomment to test nodeManager
	//test();
},false);
*/




function test(){

    function Obj(name){
        nodeManager.name = name || 'default';
    }

    Obj.prototype.toString = function(){
        return (nodeManager.name);
    };

    var a = new Obj('A');
    var b = new Obj('B');

    a.x = function(){
        return 100;
    }
    a.y = function(){
        return 100;
    }

    b.x = function(){
        return 150;
    }
    b.y = function(){
        return 120;
    }

    nodeManager.connectNodes(a, b);

    nodeManager.connectNodes(b, a);

    nodeManager.connectNodes(b, b);

    nodeManager.connectNodes(a, a);

    console.debug('All:\n' + nodeManager.allToString('\n'));

    nodeManager.disconnectNodes(b, a);

    console.debug('All without A-B:\n' + nodeManager.allToString('\n'));

    nodeManager.connectNodes(a, b);

    console.debug('All with A-B again:\n' + nodeManager.allToString('\n'));

    nodeManager.disconnectNodes(b, a);

    console.debug('All without A-B:\n' + nodeManager.allToString('\n'));

    nodeManager.connectNodes(a,b);
    nodeManager.disconnectNodes(a,a);
    nodeManager.disconnectNodes(b,b);

    console.debug('Only A-B:\n' + nodeManager.allToString('\n'));

    nodeManager.drawOnCanvas();
}