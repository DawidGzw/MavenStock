/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// technika naśladująca przestrzenie nazw.
// Teraz aby użyć klasy EventUils należy 
// odwołać się do niej tak : GLOBALS.utils.EventUtils
var GLOBALS = {
    utils: {
// służy enkapsulacji funkcji
// związanych ze zdarzeniami 
// aby odsepartować logikę działań na zdarzeniach
// od typu przeglądarki
        EventUtil: {
// dodanie obsługi zdarzenia do elementu
            addHandler: function(element, type, handler) {
                if (element.addEventListener) {
                    element.addEventListener(type, handler, false);
                }
                else if (element.attachEvent) {
                    element.attachEvent("on" + type, handler);
                }
                else {
                    element["on" + type] = handler;
                }
            },
            // usuwanie  obsługi zdarzenia z elementu 
            removeHandler: function(element, type, handler) {
                if (element.removeEventListener) {
                    element.removeEventListener(type, handler, false);
                }
                else if (element.detachEvent) {
                    element.detachEvent("on" + type, handler);
                }
                else {
                    element["on" + type] = null;
                }
            },
            // zdarzenie
            getEvent: function(event) {
                return event ? event : window.event;
            },
            // obiekt, który wywołał zdarzenie np (<button> jeśli kliknięto przycisk)
            getTarget: function(event) {
                return event.target || event.srcElement;
            },
            // nie wykonuj domyślnej akcji dla elementu (np. powstrzymuje wysłanie formularza po naciśnięciu przycisku)
            preventDefault: function(event) {
                if (event.preventDefault) {
                    event.preventDefault();
                } else {
                    event.returnValue = false;
                }
            },
            // nie przesyłaj zdarzenia dalej do innych obiektów w drzewie
            stopPropagation: function(event) {
                if (event.stopPropagation) {
                    event.stopPropagation();
                } else {
                    event.cancelBubble = true;
                }
            },
            // tylko dla mouseover i mouseout zwraca element powiązany ze zdarzeniem 
            // (np. kursor wychodzi w z elementu div i znajduje się nad elementem body,
            // wtedy zwraca body jako powiązany ze zdarzeniem element)
            getRelatedTarget: function(event) {
                if (event.relatedTarget) {
                    return event.relatedTarget;
                } else if (event.toElement) {
                    return event.toElement;
                } else if (event.fromElement) {
                    return event.fromElement;
                } else {
                    return null;
                }
            },
            //zwraca kod wciśnietego przysisku myszki
            getButton: function(event) {
                if (document.implementation.hasFeature("MouseEvents", "2.0")) {
                    return event.button;
                } else {
                    switch (event.button) {
                        case 0:
                        case 1:
                        case 3:
                        case 5:
                        case 7:
                            return 0;
                        case 2:
                        case 6:
                            return 2;
                        case 4:
                            return 1;
                    }
                }
            },
            // zwraca wartość znormalizowaną przesunięcia kółka myszki
            getWheelDelta: function(event) {
                if (event.wheelDelta) {
                    return (client.engine.opera && client.engine.opera < 9.5 ?
                            -event.wheelDelta : event.wheelDelta);
                } else {
                    return -event.detail * 40;
                }
            },
            //zwaraca kod wciśnietego klawisza
            getCharCode: function(event) {
                if (typeof event.charCode == "number") {
                    return event.charCode;
                } else {
                    return event.keyCode;
                }
            }
        },
        //pomocnicza klasa realizująca technikę dziedziczenia - "Parasitic Combination Inheritence"
        // chodzi w niej o to aby nie wywoływać dwukrotnie komstruktora klasy bazowej
        PrototypeInheritance: {
            // kopiuje prototyp klasy bazowej
            // ustawia referencje do konstruktora na kostruktor klasy pochodnej
            // i przypisuje jej ten prototyp
            inheritPrototype: function(subType, superType) {
                var prototype = new Object(superType.prototype);
                prototype.constructor = subType;
                subType.prototype = prototype;
            }


        },
        ColorFormatConverter: {
            componentToHex: function(c) {
                var hex = c.toString(16);
                return hex.length == 1 ? "0" + hex : hex;
            },
            rgbToHex: function(r, g, b) {
                return "#" + componentToHex(r) + componentToHex(g) + componentToHex(b);
            },
            hexToRgb: function(hex) {
                var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
                return result ? "rgb(" + parseInt(result[1], 16) + ", " + parseInt(result[2], 16) + ", " + parseInt(result[3], 16) + ")" : null;
            },
            hexToRgba: function(hex, opacity) {
                var result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
                return result ? "rgba(" + parseInt(result[1], 16) + ", " + parseInt(result[2], 16) + ", " + parseInt(result[3], 16) + ", " + opacity + ")" : null;
            }
        }
    },
    events: {
        //zdarzenie przewijające lewe menu i pasek główny razem ze stroną
        addScrollerToHeader: function() {
            var onscrollHandler = function(event) {
                var offset = window.pageYOffset;
                var left = document.getElementById("left");
                var container = document.getElementById("content");
                var containerHeight = container.offsetHeight;
                if (100 >= offset) {
                    document.getElementById("fixedBar").style.top = (100 - offset) + "px";
                    left.style.top = (150 - offset) + "px";
                    left.style.height = (containerHeight - (150 - offset)) + "px";
                }
                else {

                    if (document.getElementById("fixedBar").style.top !== "0px") {
                        document.getElementById("fixedBar").style.top = "0px";
                        left.style.top = "50px";
                    }
                    var newHeight = (containerHeight + 180 - offset);
                    left.style.height = newHeight + "px";
                }

            };
            GLOBALS.utils.EventUtil.addHandler(window, "scroll", onscrollHandler);
            GLOBALS.utils.EventUtil.addHandler(window, "load", onscrollHandler);
        },
        addResizerToWindow: function() {
            var fitPageOnResize = function(event) {
                var width = window.innerWidth;
                var container = document.getElementById("container");
                var content = document.getElementById("content");
                var left = document.getElementById("left");
                container.style.width = "100%";
                content.style.width = "80%";
                left.style.width = "20%";
            };
            GLOBALS.utils.EventUtil.addHandler(window, "resize", fitPageOnResize);
            GLOBALS.utils.EventUtil.addHandler(window, "load", fitPageOnResize);
        },
        closingMenuEvent: function() {
            var content = document.getElementById("content");
            content.setAttribute("style", "margin-left: auto; margin-right: auto;");
            this.headerLeftMenuImageHoverEvent();
            
        },
        openingMenuEvent: function() {
            var content = document.getElementById("content");
            content.setAttribute("style", "float: right;");
            this.openingMenuResizer();
            this.addTransitionsToMenu();
        },
        
        headerLeftMenuImageHoverEvent: function(evt) {
            var elem = document.getElementById("leftMenuImageOpen");
            var leftMenu = document.getElementById("menuShowSpan");
                var mouseOverEventHandler = function(evt) {
                    var classIndex = null;
                    var classes = null;
                    classes = elem.className.split(" ");
                    classIndex = classes.indexOf("leftMenuImageOpenHover");
                    if (classIndex == -1) {
                        elem.className += " leftMenuImageOpenHover";
                    }
                }
                GLOBALS.utils.EventUtil.addHandler(leftMenu, 'mouseover', mouseOverEventHandler);
                var mouseOutEventHandler = function(evt) {
                    var classIndex = null;
                    var classes = null;
                    classes = elem.className.split(" ");
                    classIndex = classes.indexOf("leftMenuImageOpenHover");
                    if (classIndex != -1) {
                        classes.splice(classIndex, 1);
                        elem.className = classes.join(' ');
                    }
                }
                GLOBALS.utils.EventUtil.addHandler(leftMenu, 'mouseout', mouseOutEventHandler);
            },
            
        addHeaderLeftMenuImageHoverEventOnLoad: function() {
            GLOBALS.utils.EventUtil.addHandler(window, "load", this.headerleftMenuImageHoverEvent);
        },
        menuResizerWithTransitionsAdd: function() {
            this.openingMenuResizer();
            this.addTransitionsToMenu();
        },
        openingMenuResizer: function() {
            var offset = window.pageYOffset;
            var container = document.getElementById("content");
            var containerHeight = container.offsetHeight;
            var left = document.getElementById("left");
            if (100 >= offset) {
                left.style.top = (150 - offset) + "px";
            }
            else {
                left.style.top = "50px";

            }
            var newHeight = (containerHeight + 180 - offset);
            left.style.height = newHeight + "px";
        },
        addTransitionEffects: function() {
            GLOBALS.utils.EventUtil.addHandler(window, "load", this.addTransitionsToMenu);
        },
        addTransitionsToMenu: function(event) {
            var left = document.getElementById("left");
            var transitionStart = function(event) {
                event.stopPropagation();
                var links = left.getElementsByTagName('ul')[0].getElementsByTagName('li');
                var classIndex = null;
                var classes = null;
                var closer = document.getElementById("menuClose").getElementsByTagName('a')[0].getElementsByTagName("span")[0];
                classes = closer.className.split(" ");
                classIndex = classes.indexOf("closeLinkImg");
                if (classIndex == -1) {
                    closer.className += " closeLinkImg";
                }
                for (var i = 0; i < links.length; i++) {
                    classes = links[i].getElementsByTagName('a')[0].className.split(" ");
                    classIndex = classes.indexOf("red-transition");
                    if (classIndex == -1) {
                        links[i].getElementsByTagName('a')[0].className += " red-transition";
                    }
                }
            }
            var transitionStop = function(event) {
                event.stopPropagation();
                var links = left.getElementsByTagName('ul')[0].getElementsByTagName('li');
                var classIndex = null;
                var classes = null;
                var closer = document.getElementById("menuClose").getElementsByTagName('a')[0].getElementsByTagName("span")[0];
                classes = closer.className.split(" ");
                classIndex = classes.indexOf("closeLinkImg");
                if (classIndex != -1) {
                    classes.splice(classIndex, 1);
                    closer.className = classes.join(' ');
                }
                for (var i = 0; i < links.length; i++) {
                    classes = links[i].getElementsByTagName('a')[0].className.split(" ");
                    classIndex = classes.indexOf("red-transition");
                    if (classIndex != -1) {
                        classes.splice(classIndex, 1);
                        links[i].getElementsByTagName('a')[0].className = classes.join(' ');
                    }
                }
            }
            GLOBALS.utils.EventUtil.addHandler(left, "mouseover", transitionStart);
            GLOBALS.utils.EventUtil.addHandler(left, "mouseout", transitionStop);
        }

    }
};
