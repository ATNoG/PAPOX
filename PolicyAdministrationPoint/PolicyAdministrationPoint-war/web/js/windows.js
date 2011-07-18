/*
//=============================================================================
// Brief   : Window/Node Manager
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

var windowManager = {
    container:null,
    openWindows:{},
    draggingWindow:null,
    movingWindow:false,
    activeWindow:null,
    list:{},
    offset:{
        x:0,
        y:0
    },
    containerOffset:{
        x:0,
        y:0
    },
    mousePos:{
        x:0,
        y:0
    },
    onMove:null,
    onClose:null,
    onFocus:null,
    onUnfocus:null,
    init:function(container){
        //Defines the element containing the windows
        windowManager.container = container;
			
        //Get container offset (usefull to make corrections between the container and body)
        windowManager.containerOffset = windowManager.getWindowPosition(container);

        //Gets mouse position
        //Based on: http://www.webreference.com/programming/javascript/mk/column2/ by Mark Kahn
        container.onmousemove = function(ev){
			
            if(ev.pageX || ev.pageY){
                windowManager.mousePos = {
                    x:ev.pageX,
                    y:ev.pageY
                };
            }
            else {
                windowManager.mousePos = {
                    x:ev.clientX + document.body.scrollLeft - document.body.clientLeft,
                    y:ev.clientY + document.body.scrollTop  - document.body.clientTop
                }
            }
				
            if(windowManager.draggingWindow!=null){
                windowManager.moveWindow(ev);
            }else{
                windowManager.moveAllWindows();
            }
        }
    },
    setOnMove:function(fn){
        windowManager.onMove = fn;
    },
    setOnClose:function(fn){
        windowManager.onClose = fn;
    },
    windowExists:function(windowName){
        return (windowManager.list[windowName]!=null);
    },
    createWindow:function(windowName, closeButton, x, y){

        //If it already exists
        if(windowManager.list[windowName]!=null){
            var w = windowManager.list[windowName];
            //do some operations to window? ...
            console.debug("[WindowManager] Window " + windowName + " already exists... retreiving the one that was found");
            return w;
        }

        var newWindow = document.createElement('div');
        var windowContent = document.createElement('div');
        var windowTitle = document.createElement('div');
			
        newWindow.setAttribute('class', 'window');

        windowContent.setAttribute('class','content');
						
        windowTitle.setAttribute('class','title');
			
        //Adds a close button to the window
        newWindow.appendChild(windowContent);
        newWindow.appendChild(windowTitle);
			
        //Insert (div) window in windowContainer
        windowManager.container.appendChild(newWindow);
	
        //To drag windows
        newWindow.addEventListener("mousedown", function(){
            windowManager.focusWindow(newWindow);
            windowManager.dragWindow(newWindow);
        },
        false);
		
        //To stop dragging window
        newWindow.addEventListener("mouseup", function(){
            windowManager.dropWindow()
        },
        false);
		
        //Get the position of the center of the window (useful for node connection)
        newWindow.centerPosition = function(){
            //Relative window position + (window size / 2)
            var x=newWindow.offsetLeft + (newWindow.offsetWidth/2);
            var y=newWindow.offsetTop + (newWindow.offsetHeight/2);

            return {
                x:x,
                y:y
            };
        }
			
        newWindow.toString = function(){
            return newWindow.getName();
        }

        newWindow.getName = function(){
            return newWindow.windowName;
        };

        newWindow.setName = function(newName){
            newWindow.windowName = newName;
            windowTitle.innerHTML = newName;
        };
        
        newWindow.setName(windowName);
        
        newWindow.setContent = function(content){
            windowContent.innerHTML = content;
        }
        
        newWindow.addElement = function(element){
            windowContent.appendChild(element);
        }

        newWindow.setX = function(x){
            newWindow.style.left = x + "px";
        };

        newWindow.setY = function(y){
            newWindow.style.top = y + "px";
        };

        newWindow.getX = function(){
            return newWindow.offsetLeft;
        }

        newWindow.getY = function(){
            return newWindow.offsetTop;
        }

        newWindow.setHeight = function(height){
            newWindow.style.height = height + "px";
        }

        newWindow.getHeight = function(){
            return newWindow.offsetHeight;
        }

        newWindow.setWidth = function(width){
            newWindow.style.width = width + "px";
        }

        newWindow.getWidth = function(){
            return newWindow.offsetWidth;
        }

        newWindow.adjustSize = function(){
            newWindow.setContentHeight(windowContent.scrollHeight);
            newWindow.setContentWidth(windowContent.scrollWidth);
        }

        newWindow.setContentWidth = function(width){
            //Difference between the window and the content, plus the new width
            var newWidth = (newWindow.getWidth() - windowContent.offsetWidth) + width;
            newWindow.setWidth(newWidth);
        }

        newWindow.setContentHeight = function(height){
            //Difference between the window and the content, plus the new height
            var newHeight = (newWindow.getHeight() - windowContent.offsetHeight) + height;
            newWindow.setHeight(newHeight);
        }

        newWindow.setTopMost = function(zIndex){
            this.topMost = true;
            newWindow.style.zIndex = zIndex;
        }

        newWindow.removeTopMost = function(){
            this.topMost = false;
            newWindow.style.zIndex = 1;
        }

        newWindow.isTopMost = function(){
            return this.topMost;
        }

        newWindow.setOnClose = function(fn){
            if(newWindow.onCloseRun==null){
                newWindow.onCloseRun = [];
            }
            newWindow.onCloseRun.push(fn);
        }

        newWindow.onClose = function(){
            /**
             * This method is used to define procedures specific to this singular window.
             * To define a procedure for all the windows, use instead windowManager.setOnClose(fn);
             **/
            if(newWindow.onCloseRun!=null){
                //Runs all the functions injected
                for(i in newWindow.onCloseRun){
                    newWindow.onCloseRun[i]();
                }
            }
        }

        newWindow.setOnFocus = function(fn){
            /**
             * This method is used to define procedures specific to this singular window.
             * To define a procedure for all the windows, use instead windowManager.setOnFocus(fn);
             **/
            
            if(newWindow.onFocusRun==null){
                newWindow.onFocusRun = [];
            }
            newWindow.onFocusRun.push(fn);
        }

        newWindow.onFocus = function(){
            var result = true;
            
            if(newWindow.onFocusRun!=null){
                //Runs all the functions injected
                for(i in newWindow.onFocusRun){
                    var r = newWindow.onFocusRun[i]();
                    
                    if(r==null){
                        r = true;
                    }

                    if(!r){
                        // If at least one of the functions returns false, then cancel focus
                        result = false;
                    }
                }
            }
            return result;
        }

        newWindow.setOnUnfocus = function(fn){
            /**
             * This method is used to define procedures specific to this singular window.
             * To define a procedure for all the windows, use instead windowManager.setOnUnfocus(fn);
             **/

            if(newWindow.onUnfocusRun==null){
                newWindow.onUnfocusRun = [];
            }
            newWindow.onUnfocusRun.push(fn);
        }

        newWindow.onUnfocus = function(){
            var result = true;
            if(newWindow.onUnfocusRun!=null){
                //Runs all the functions injected
                for(i in newWindow.onUnfocusRun){
                    var r = newWindow.onUnfocusRun[i]();

                    if(r==null){
                        r = true;
                    }

                    if(!r){
                        // If at least one of the functions returns false, then cancel unfocus
                        result = false;
                    }
                }
            }
            return result;
        }

        //Defines the onClose action
        if(closeButton)
            windowManager.putCloseButton(newWindow);

        //Put window on the top
        //this.focusWindow(newWindow);

        //Inserts in the map
        windowManager.list[windowName] = newWindow;

        if(x!=null && y!=null){
            newWindow.setX(x);
            newWindow.setY(y);
        }

        //Returns the (div)window element
        return newWindow;
    },
    getWindow:function(wName){
        return windowManager.list[wName];
    },
    putCloseButton:function(wnd){
        var closeButton = document.createElement('div');

        closeButton.setAttribute('class', 'button');
        closeButton.setAttribute('style', 'background-color:red;color:white;');
        closeButton.innerHTML = 'X';

        wnd.appendChild(closeButton);

        //Click event on close button
        closeButton.addEventListener("click", function(){
            windowManager.closeWindow(wnd);
        },
        false);
    },
    dragWindow:function(wnd){
        // Get window position
        var wpos = windowManager.getWindowPosition(wnd);
	
        // Defines offset between mouse pointer and window position (keep the mouse in the same position in the window while pressed)
        windowManager.offset.x = windowManager.mousePos.x - wpos.x;
        windowManager.offset.y = windowManager.mousePos.y - wpos.y;
	
        //Defines in windowsManager which window to move
        windowManager.draggingWindow=wnd;
    },
    dropWindow:function(){
        windowManager.draggingWindow=null;
    },
    moveWindow:function(){
        //Semaphore -> used to not override the last call, ignoring the current call if last one is still active.
        if(windowManager.movingWindow==true)
            return;

        windowManager.movingWindow = true;

        if(windowManager.onMove!=null)
            windowManager.onMove();

        /**
        * (A - B) - C
        *
        * A - Mouse position relative to body
        * B - Difference between mouse position and window placement
        * C - Difference between window container and body
        */
        var oX = (windowManager.mousePos.x - windowManager.offset.x) - windowManager.containerOffset.x;
        var oY = (windowManager.mousePos.y - windowManager.offset.y) - windowManager.containerOffset.y;
			
        // Moves window to specified coordinations
        //windowManager.draggingWindow.style.left = oX + 'px';
        //windowManager.draggingWindow.style.top = oY + 'px';
        windowManager.draggingWindow.setX(oX);
        windowManager.draggingWindow.setY(oY);

        setTimeout(function(){
            windowManager.movingWindow = false;
        }, 10);

    },
    moveAllWindows:function(){

        //TODO - Move all windows
        
    },
    getWindowPosition:function(wnd){
        var op = wnd;
        var left = 0;
        var top = 0;

        //Recursivelly gets all offsets from all parents to get the right position
        while(op!=null){
            left += op.offsetLeft;
            top += op.offsetTop;
            op = op.offsetParent;
        }
			
        return {
            x:left,
            y:top
        };
    },
    focusWindow:function(wnd){
        var focus = true;

        focus = wnd.onFocus();

        // Cancel focus
        if(!focus){
            return;
        }

        if(windowManager.onFocus!=null)
            focus = windowManager.onFocus(wnd) | true;

        // Cancel focus
        if(!focus){
            return;
        }

        if(this.activeWindow!=null){
            var unfocus = false;

            unfocus = this.activeWindow.onUnfocus();

            if(unfocus){
                if(windowManager.onUnfocus!=null){
                    unfocus=windowManager.onUnfocus(wnd);
                }

                if(unfocus){
                    if(!this.activeWindow.isTopMost()){
                        windowManager.activeWindow.style.zIndex=1;
                    }
                    windowManager.activeWindow.style.borderColor='';
                    windowManager.activeWindow.style.borderWidth='';
                }
            }
        }
        windowManager.activeWindow=wnd;
        if(!this.activeWindow.isTopMost()){
            windowManager.activeWindow.style.zIndex=2;
        }
        windowManager.activeWindow.style.borderColor='red';
        windowManager.activeWindow.style.borderWidth='3px';
    },
    closeWindow:function(wnd){
        wnd.onClose(); //Functions set in window.setOnClose (see windowManager.createWindow)

        windowManager.onClose(wnd); //Functions set in windowManager.setOnClose

        windowManager.list[wnd.getName()]=null; //Removes this window from the list

        windowManager.container.removeChild(wnd); //Removes the window from container
    },
    setOnFocus:function(fn){
        windowManager.onFocus = fn;
    },
    setOnUnfocus:function(fn){
        windowManager.onUnfocus = fn;
    }
}

//Wait for the window to load -> otherwise this script will be run when there are no elements
/*
window.addEventListener("DOMContentLoaded", function(){
	var content = document.getElementById("windowContainer");
	var canvas = document.getElementById('connector');
	
	nodeManager.init(canvas);
	
	windowManager.init(content);
	windowManager.setOnMove(nodeManager.drawOnCanvas);
	windowManager.setOnClose(nodeManager.disconnectNode);
	
},false);
*/

/*
function createWindows(){
    var a = windowManager.createWindow('PolicySet: RootPolicy');
    var b = windowManager.createWindow('PolicySet: RoleAdmin');
    var c = windowManager.createWindow('PolicySet: RoleUser');
    var d = windowManager.createWindow('PolicySet: RoleMod');
	
    nodeManager.connectNodes(a, b);
    nodeManager.connectNodes(a, c);
    nodeManager.connectNodes(a, d);
	
    a.setContent('teste');
	
    var element = document.createElement('div');
    element.setAttribute('onclick', 'windowManager.container.getElementById("w4").setContent="teste4"');
    element.innerHTML = 'click!';
	
    b.addElement(element);
}
*/