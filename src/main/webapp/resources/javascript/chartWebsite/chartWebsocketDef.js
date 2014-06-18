/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


CHART.websockets = {
    createWebsocketClientEndpoint: function() {
        var wsUri = "ws://localhost:8080/MavenStock/shareprovider";
        var websocket = new WebSocket(wsUri);

        websocket.onerror = function(evt) {
            onError(evt);
        };

        function onError(evt) {
            writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
        }


        function writeToScreen(message) {
            output.innerHTML += message + "<br>";
        }

        websocket.onmessage = function(evt) {
            onMessage(evt);
        };

        function sendText(json) {
            console.log("sending text: " + json);
            websocket.send(json);
        }

        function areDatesValid(dateFrom, dateTo) {
            var dateTab1 = dateFrom.split("-");
            var dateTab2 = dateTo.split("-");
            if (dateTab1.length < 3 || dateTab2.length < 3){
                console.log("date format is wrong");
                return false;
            }
            var date1 = new Date(parseInt(dateTab1[2]), parseInt(dateTab1[1]), parseInt(dateTab1[0]));
            var date2 = new Date(parseInt(dateTab2[2]), parseInt(dateTab2[1]), parseInt(dateTab2[0]));
            if ((date1.getTime() - date2.getTime()) > 0) {
                return false;
                console.log("date range is wrong");
            }
            return true;
        }

        function sendRequestJson(evt) {
            var dateFrom = document.getElementById("chartsForm:dateFromCal_input").value;
            var dateTo = document.getElementById("chartsForm:dateToCal_input").value;
            var mode = document.getElementById("chartsForm:sharesScaler_input").value;
            var share = document.getElementById("chartsForm:sharesSelector_input").value;
            var shareName = document.getElementById("chartsForm:sharesSelector_label").innerHTML;
            if (!areDatesValid(dateFrom, dateTo) || share === "" || share === "") {
                return;
            }
            var jsonAsk = JSON.stringify({
                "shareID": share,
                "mode": mode,
                "dateFrom": dateFrom,
                "dateTo": dateTo,
                "shareName": shareName
            });
            sendText(jsonAsk);
        }

        function onMessage(evt) {
            console.log("message received: "+evt.data);
            var json  = JSON.parse(evt.data);
            CHART.classes.instantances.canvasPanelsManager.setWebSocketOperationResult(json);
            CHART.classes.events.updateChartNavigationOptions();
        }
        
        var element = document.getElementById("chartsForm:chartUpdateButton");
        GLOBALS.utils.EventUtil.addHandler(element, "click", sendRequestJson);
    }
    
};

CHART.websockets.createWebsocketClientEndpoint();