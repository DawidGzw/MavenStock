/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

CHART.classes = {};
CHART.classes.definitions = {};

CHART.classes.definitions.CanvasPainter = function(canvasElemID) {
    this.canvasElement = canvasElemID;
    this.canvasContext = this.canvasElement == null ? null : this.canvasElement.getContext("2d");
    this.backgroundColor = "#000000";
    this.textColor = "#ffffff";
    this.chartColor = "#25a81e";
    this.lastWidth;
    this.lastHeight;
    this.stockData;
};

CHART.classes.definitions.CanvasPainter.prototype = {
    setColors: function(background, text, chart) {

        if (background != null && background.length > 1) {
            this.backgroundColor = background;
        }
        if (text != null && text.length > 1) {
            this.textColor = text;
        }
        if (chart != null && chart.length > 1) {
            this.chartColor = chart;
        }
    },
    clearCanvas: function() {
        if (this.lastWidth != null && this.lastHeight != null) {
            this.canvasContext.clearRect(0, 0, this.lastWidth, this.lastHeight);
        }
    },
    paintBackground: function(screenRatio) {
        var width = this.canvasElement.width;
        var height = Math.floor(width * (1 / screenRatio));
        this.canvasElement.height = height;
        this.canvasContext.fillStyle = this.backgroundColor;
        this.canvasContext.fillRect(0, 0, width, height);
        this.lastWidth = width;
        this.lastHeight = height;
    },
    paintCanvas: function(screenRatio, page, resultsPerPage, valueRows) {
        this.paintBackground(screenRatio);
        if (this.stockData != null) {
            this.paintText(page, resultsPerPage, valueRows);
            this.paintChart(page, resultsPerPage, valueRows);
        }
    },
    setStockData: function(stockData) {
        this.stockData = stockData;
    },
    getStockData: function() {
        return this.stockData;
    },
    paintText: function(page, resultsPerPage, valueRows) {

    },
    paintChart: function(page, resultsPerPage, valueRows) {

    },
    getEndDateMonthFromStockData: function(index) {
        return this.stockData.shareValues[index].end.split("-")[1];
    },
    getEndDateDayFromStockData: function(index) {
        return this.stockData.shareValues[index].end.split("-")[0];
    },
    getEndDateYearFromStockData: function(index) {
        return this.stockData.shareValues[index].end.split("-")[2];
    },
    getCanvasPanelIdElement: function() {
        return this.canvasElement;
    },
    getColors: function() {
        return [this.backgroundColor, this.textColor, this.chartColor];
    },
    getWidth: function() {
        return this.lastWidth;
    },
    getHeight: function() {
        return this.lastHeight;
    },
    setHeight: function(height) {
        this.lastHeight = height;
    },
    setWidth: function(width) {
        this.lastWidth = width;
    }
};

CHART.classes.definitions.PricePainter = function(canvasID) {
    CHART.classes.definitions.CanvasPainter.call(this, canvasID);
};

CHART.classes.definitions.PricePainter.prototype = new CHART.classes.definitions.CanvasPainter();


CHART.classes.definitions.PricePainter.prototype.paintHorizontalLines = function(chartWidth, chartHeight, heightStepSize, stepSize, priceRows) {
    var ctx = this.canvasContext;
    var stock = this.stockData;
    ctx.strokeStyle = GLOBALS.utils.ColorFormatConverter.hexToRgba(this.textColor, 0.2);
    for (var i = 1; i < priceRows + 1; i++) {
        ctx.beginPath();
        ctx.moveTo(0, chartHeight - (i * heightStepSize));
        ctx.lineTo(chartWidth + 5, chartHeight - (i * heightStepSize));
        ctx.stroke();
        ctx.fillText(Number((parseFloat(stock.minMidTime) + ((i - 1) * stepSize)).toFixed(2)), chartWidth + 5, chartHeight - (i * heightStepSize));
    }
    ctx.strokeStyle = this.textColor;
};

CHART.classes.definitions.PricePainter.prototype.paintChartAxis = function(chartWidth, chartHeight) {
    var ctx = this.canvasContext;
    ctx.beginPath();
    ctx.moveTo(chartWidth, 0);
    ctx.lineTo(chartWidth, chartHeight);
    ctx.lineTo(0, chartHeight);
    ctx.stroke();
};

CHART.classes.definitions.PricePainter.prototype.getSkipStepSize = function(resultsPerPage) {
    switch (resultsPerPage) {
        case 20:
            return 1;
        case 50:
            return 2;
        case 100:
            return 5;
        case 200:
            return 10;
    }
};

CHART.classes.definitions.PricePainter.prototype.paintVerticalLines = function(page, resultsPerPage, widthStepSize, chartHeight) {
    var ctx = this.canvasContext;
    var stockValueArray = this.stockData.shareValues;
    var checkable = stockValueArray.length >= resultsPerPage * (page - 1) + 1;

    var textDescSkippingRange = this.getSkipStepSize(resultsPerPage);
    var preBeginIndex = (page - 1) * resultsPerPage - 1;
    var maxIndex = resultsPerPage * page + 1;
    var currentMonth = null;
    var currentYear = null;
    if (preBeginIndex < 0) {
        currentMonth = this.getEndDateMonthFromStockData(preBeginIndex + 1);
        currentYear = this.getEndDateYearFromStockData(preBeginIndex + 1);
    }
    else{
        currentMonth = this.getEndDateMonthFromStockData(preBeginIndex);
        currentYear = this.getEndDateYearFromStockData(preBeginIndex);       
    }
    ctx.strokeStyle = GLOBALS.utils.ColorFormatConverter.hexToRgba(this.textColor, 0.2);
    for (var i = preBeginIndex + 1; i < maxIndex; i++) {
        ctx.beginPath();
        ctx.moveTo((i - preBeginIndex) * widthStepSize, 0);
        ctx.lineTo((i - preBeginIndex) * widthStepSize, chartHeight + 10);
        ctx.stroke();
        if (checkable && (i % textDescSkippingRange == 0)) {

            var newMonth = this.getEndDateMonthFromStockData(i);
            var newYear = this.getEndDateYearFromStockData(i);
            if (currentMonth != newMonth) {
                if (currentYear != newYear) {
                    ctx.fillText(newYear, (i - preBeginIndex) * widthStepSize - 5, chartHeight + 20);
                    currentYear = newYear;
                    currentMonth = newMonth;
                }
                else {
                    ctx.fillText(PrimeFaces.locales['en'].monthNamesShort[parseInt(newMonth) - 1], (i - preBeginIndex) * widthStepSize - 5, chartHeight + 20);
                    currentMonth = newMonth;
                }
            }
            else {
                ctx.fillText(this.getEndDateDayFromStockData(i), (i - preBeginIndex) * widthStepSize - 5, chartHeight + 20);
            }
        }
        checkable = stockValueArray.length > i + 1;
    }
    ctx.strokeStyle = this.textColor;
};

CHART.classes.definitions.PricePainter.prototype.getPriceStepSize = function() {
};

CHART.classes.definitions.PricePainter.prototype.paintText = function(page, resultsPerPage, priceRows) {
    var ctx = this.canvasContext;
    var color = this.textColor;
    var width = this.lastWidth;
    var height = this.lastHeight;
    var chartHeight = height - 40;
    var chartWidth = width - 50;
    ctx.strokeStyle = color;
    ctx.fillStyle = color;
    ctx.lineWidth = 2;
    this.paintChartAxis(chartWidth, chartHeight);
    var stepSize = this.getPriceStepSize(priceRows);
    var heightStepSize = chartHeight / (priceRows + 1);
    var widthStepSize = chartWidth / (resultsPerPage + 1);
    ctx.lineWidth = 0.3;
    ctx.font = "10pt Arial";
    this.paintHorizontalLines(chartWidth, chartHeight, heightStepSize, stepSize, priceRows);
    this.paintVerticalLines(page, resultsPerPage, widthStepSize, chartHeight);
};

CHART.classes.definitions.LinePainter = function(canvasID) {
    CHART.classes.definitions.PricePainter.call(this, canvasID);
};

CHART.classes.definitions.LinePainter.prototype = new CHART.classes.definitions.PricePainter();

CHART.classes.definitions.LinePainter.prototype.getPriceStepSize = function(priceRows) {
    return (parseFloat(this.stockData.maxMidTime) - parseFloat(this.stockData.minMidTime)) / (priceRows - 1);
};

CHART.classes.definitions.LinePainter.prototype.paintChart = function(page, resultsPerPage, priceRows) {
    var ctx = this.canvasContext;
    var color = this.textColor;
    var width = this.lastWidth;
    var height = this.lastHeight;
    var chartHeight = height - 40;
    var chartWidth = width - 50;
    var widthStepSize = chartWidth / (resultsPerPage + 1);
    var stock = this.stockData;
    var fullRange = (parseFloat(stock.maxMidTime) - parseFloat(stock.minMidTime));
    var min = stock.minMidTime;
    var heightStepPixelSize = chartHeight / (priceRows + 1);
    var chartRange = chartHeight - 2 * heightStepPixelSize;
    var stockValueArray = stock.shareValues;
    var beginIndex = (page - 1) * resultsPerPage;
    var maxIndex = resultsPerPage * page;
    var currentInstanceHeight = 0;
    var beginningPosition = 0;
    if (beginIndex == 0) {
        currentInstanceHeight = chartRange - (((parseFloat(stockValueArray[beginIndex].closeValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        beginningPosition = widthStepSize
    }
    else {
        currentInstanceHeight = chartRange - (((parseFloat(stockValueArray[beginIndex - 1].closeValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
    }
    ctx.fillStyle = GLOBALS.utils.ColorFormatConverter.hexToRgba(color, 0.3);
    ctx.beginPath();
    ctx.moveTo(beginningPosition, currentInstanceHeight);
    ctx.lineWidth = 3;
    ctx.strokeStyle = this.chartColor;
    var firstPointHeight = currentInstanceHeight;
    for (var i = beginIndex; i < maxIndex + 1 && stockValueArray[i] != null; i++) {
        var currentInstanceHeight = chartRange - (((parseFloat(stockValueArray[i].closeValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        ctx.lineTo((i - beginIndex + 1) * widthStepSize, currentInstanceHeight);
    }
    ctx.stroke();
    ctx.lineTo((i - beginIndex) * widthStepSize, chartHeight);
    ctx.lineTo(0, chartHeight);
    ctx.lineTo(0, firstPointHeight);
    ctx.closePath();
    ctx.fill();
};


CHART.classes.definitions.BarPainter = function(canvasID) {
    CHART.classes.definitions.PricePainter.call(this, canvasID);
};

CHART.classes.definitions.BarPainter.prototype = new CHART.classes.definitions.PricePainter();

CHART.classes.definitions.BarPainter.prototype.getPriceStepSize = function(priceRows) {
    return (parseFloat(this.stockData.maxAllTime) - parseFloat(this.stockData.minAllTime)) / (priceRows - 1);
};

CHART.classes.definitions.BarPainter.prototype.paintChart = function(page, resultsPerPage, priceRows) {
    var ctx = this.canvasContext;
    var width = this.lastWidth;
    var height = this.lastHeight;
    var chartHeight = height - 40;
    var chartWidth = width - 50;
    var widthStepSize = chartWidth / (resultsPerPage + 1);
    var stock = this.stockData;
    var fullRange = (parseFloat(stock.maxAllTime) - parseFloat(stock.minAllTime));
    var min = stock.minAllTime;
    var heightStepPixelSize = chartHeight / (priceRows + 1);
    var chartRange = chartHeight - 2 * heightStepPixelSize;
    var stockValueArray = stock.shareValues;
    //var currentInstanceHeight = chartRange - (((parseFloat(stockValueArray[0].closeValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
    var beginIndex = (page - 1) * resultsPerPage;
    var maxIndex = resultsPerPage * page;
    ctx.lineWidth = 2;
    ctx.strokeStyle = this.chartColor;
    for (var i = beginIndex; i < maxIndex && stockValueArray[i] != null; i++) {
        var closeHeightPosition = chartRange - (((parseFloat(stockValueArray[i].closeValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        var openHeightPosition = chartRange - (((parseFloat(stockValueArray[i].openValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        var maxHeightPosition = chartRange - (((parseFloat(stockValueArray[i].maxValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        var minHeightPosition = chartRange - (((parseFloat(stockValueArray[i].minValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        ctx.beginPath();
        ctx.moveTo((i - beginIndex + 1) * widthStepSize, maxHeightPosition);
        ctx.lineTo((i - beginIndex + 1) * widthStepSize, minHeightPosition);
        ctx.moveTo((i - beginIndex + 1) * widthStepSize, openHeightPosition);
        ctx.lineTo((i - beginIndex + 1) * widthStepSize - widthStepSize / 3 - 1, openHeightPosition);
        ctx.moveTo((i - beginIndex + 1) * widthStepSize, closeHeightPosition);
        ctx.lineTo((i - beginIndex + 1) * widthStepSize + widthStepSize / 3 + 1, closeHeightPosition);
        ctx.stroke();
        ctx.closePath();
    }
};


CHART.classes.definitions.CandlePainter = function(canvasID) {
    CHART.classes.definitions.PricePainter.call(this, canvasID);
};

CHART.classes.definitions.CandlePainter.prototype = new CHART.classes.definitions.PricePainter();

CHART.classes.definitions.CandlePainter.prototype.getPriceStepSize = function(priceRows) {
    return (parseFloat(this.stockData.maxAllTime) - parseFloat(this.stockData.minAllTime)) / (priceRows - 1);
};

CHART.classes.definitions.CandlePainter.prototype.paintChart = function(page, resultsPerPage, priceRows) {
    var ctx = this.canvasContext;
    var width = this.lastWidth;
    var height = this.lastHeight;
    var chartHeight = height - 40;
    var chartWidth = width - 50;
    var widthStepSize = chartWidth / (resultsPerPage + 1);
    var stock = this.stockData;
    var fullRange = (parseFloat(stock.maxAllTime) - parseFloat(stock.minAllTime));
    var min = stock.minAllTime;
    var heightStepPixelSize = chartHeight / (priceRows + 1);
    var chartRange = chartHeight - 2 * heightStepPixelSize;
    var stockValueArray = stock.shareValues;
    //var currentInstanceHeight = chartRange - (((parseFloat(stockValueArray[0].closeValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
    ctx.lineWidth = 2;
    ctx.strokeStyle = this.chartColor;
    ctx.fillStyle = this.chartColor;
    var beginIndex = (page - 1) * resultsPerPage;
    var maxIndex = resultsPerPage * page;
    for (var i = beginIndex; i < maxIndex && stockValueArray[i] != null; i++) {
        var closeHeightPosition = chartRange - (((parseFloat(stockValueArray[i].closeValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        var openHeightPosition = chartRange - (((parseFloat(stockValueArray[i].openValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        var maxHeightPosition = chartRange - (((parseFloat(stockValueArray[i].maxValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        var minHeightPosition = chartRange - (((parseFloat(stockValueArray[i].minValue) - min) / fullRange) * chartRange) + heightStepPixelSize;
        var widthPosition = (i - beginIndex + 1) * widthStepSize;
        var candleWidth = widthStepSize / 5 + 1;
        ctx.beginPath();
        ctx.moveTo(widthPosition, maxHeightPosition);
        ctx.lineTo(widthPosition, Math.min(openHeightPosition, closeHeightPosition));
        ctx.stroke();
        ctx.closePath();
        if (Math.min(openHeightPosition, closeHeightPosition) == openHeightPosition)
            this.paintFullCandle(openHeightPosition, closeHeightPosition, widthPosition, candleWidth);
        else
            this.paintEmptyCandle(openHeightPosition, closeHeightPosition, widthPosition, candleWidth);
        ctx.beginPath();
        ctx.moveTo(widthPosition, Math.max(openHeightPosition, closeHeightPosition));
        ctx.lineTo(widthPosition, minHeightPosition);
        ctx.stroke();
        ctx.closePath();
    }
};

CHART.classes.definitions.CandlePainter.prototype.paintEmptyCandle = function(openPosition, closePosition, widthPosition, candleWidth) {
    var ctx = this.canvasContext;
    ctx.strokeRect(widthPosition - candleWidth, openPosition, candleWidth * 2, closePosition - openPosition);
};

CHART.classes.definitions.CandlePainter.prototype.paintFullCandle = function(openPosition, closePosition, widthPosition, candleWidth) {
    var ctx = this.canvasContext;
    ctx.fillRect(widthPosition - candleWidth, closePosition, candleWidth * 2, openPosition - closePosition);
};



CHART.classes.definitions.ChartPanel = function(elementID, aspectRatio, painter, valueRows) {
    this.screenRatio = aspectRatio;
    this.valueRowsNumber = valueRows;
    this.elementID = document.getElementById(elementID);
    this.canvasPainter = painter;
    this.toggleButton = document.getElementById(elementID + "_toggler");
};

CHART.classes.definitions.ChartPanel.prototype = {
    isOpen: function() {
        return ((" " + this.elementID.className + " ").indexOf(" " + "ui-panel-collapsed-h" + " ") === -1);
    },
    setFullWidth: function() {
        this.elementID.style.width = "99%";
        this.canvasPainter.canvasElement.width = this.elementID.clientWidth;
    },
    fitOnResize: function() {
        if (this.isOpen()) {
            this.setFullWidth();
        }
    },
    setValueRowsNumber: function(rows) {
        this.valueRowsNumber = rows;
    },
    repaint: function(page, resultsPerPage) {
        this.canvasPainter.clearCanvas();
        this.canvasPainter.paintCanvas(this.screenRatio, page, resultsPerPage, this.valueRowsNumber);
    },
    setColors: function(background, text, chart) {
        this.canvasPainter.setColors(background, text, chart);
    },
    setAspectRatio: function(ratio) {
        this.screenRatio = ratio;
    },
    setStockData: function(stockData) {
        this.canvasPainter.setStockData(stockData);
    },
    changePainter: function(painterName) {
        var stockData = this.canvasPainter.getStockData();
        var id = this.canvasPainter.getCanvasPanelIdElement();
        var lastWidth = this.canvasPainter.getWidth();
        var lastHeight = this.canvasPainter.getHeight();
        var colors = this.canvasPainter.getColors();
        var painter = new CHART.classes.definitions.CanvasPaintersFactory().getPainter(painterName, id);
        painter.setWidth(lastWidth);
        painter.setHeight(lastHeight);
        painter.setStockData(stockData);
        painter.setColors(colors[0], colors[1], colors[2]);
        this.canvasPainter = painter;
    }
};

CHART.classes.definitions.CanvasPaintersFactory = function() {
};

CHART.classes.definitions.CanvasPaintersFactory.prototype = {
    getPainter: function(painterName, id) {
        var painter;
        switch (painterName) {
            case 'candle':
                painter = new CHART.classes.definitions.CandlePainter(id);
                break;
            case 'line':
                painter = new CHART.classes.definitions.LinePainter(id);
                break;
            case 'bar':
                painter = new CHART.classes.definitions.BarPainter(id);
                break;
        }
        return painter;

    }
};


CHART.classes.definitions.CanvasPanelsManager = function() {
    this.canvasPanelsCollection = new Array();
    this.maxPageNum = 1;
    this.page = 1;
    this.resultsPerPage = 50;
    this.resultsNumber;
};

CHART.classes.definitions.CanvasPanelsManager.prototype = {
    addAllPanels: function(panels) {
        var background = document.getElementById("chartProperties:colorPicker1_input").value;
        var text = document.getElementById("chartProperties:colorPicker2_input").value;
        var chart = document.getElementById("chartProperties:colorPicker3_input").value;

        if (background !== null && background.length > 0) {
            background = ('#' + background);
        }
        else {
            background = null;
        }
        if (text !== null && text.length > 0) {
            text = ('#' + text);
        }
        else {
            text = null;
        }
        if (chart !== null && chart.length > 0) {
            chart = ('#' + chart);
        }
        else {
            chart = null;
        }
        var manager = this;
        var eventAddingFunction = function(panel) {
            GLOBALS.utils.EventUtil.addHandler(panel.toggleButton, "click", function() {
                setTimeout(function() {
                    panel.fitOnResize();
                    panel.repaint(manager.page, manager.resultsPerPage);
                    GLOBALS.events.openingMenuResizer();
                }, 550);
                GLOBALS.events.openingMenuResizer();
            });
            manager.canvasPanelsCollection.push(panel);
            panel.setColors(background, text, chart);
            panel.repaint(manager.page, manager.resultsPerPage);
        }
        if (panels instanceof Array) {
            for (var i = 0; i < panels.length; i++) {
                var panel = panels[i];
                eventAddingFunction(panel);
            }
        }
        else {
            eventAddingFunction(panels);
        }
    },
    setWebSocketOperationResult: function(result) {
        this.page = 1;
        this.resultsNumber = result.shareValues.length;
        this.maxPageNum = Math.ceil(result.shareValues.length / this.resultsPerPage);
        for (var i = 0; i < this.canvasPanelsCollection.length; i++) {
            this.canvasPanelsCollection[i].setStockData(result);
        }
        this.canvasStateUpdate();
    },
    setResultsPerPage: function(resultsNum) {
        this.resultsPerPage = resultsNum;
        this.maxPageNum = Math.ceil(this.resultsNumber / this.resultsPerPage);
        this.page = 1;
        this.canvasStateUpdate();
    },
    setAspectRatio: function(ratio, panelNum) {
        this.canvasPanelsCollection[panelNum].setAspectRatio(ratio);
        this.canvasPanelsCollection[panelNum].repaint(this.page, this.resultsPerPage);
    },
    setValueRowsNumber: function(rows, panelNum) {
        this.canvasPanelsCollection[panelNum].setValueRowsNumber(rows);
    },
    getSize: function() {
        return this.canvasPanelsCollection.length;
    },
    getElement: function(num) {
        if (num >= this.canvasPanelsCollection.length)
            return null;
        return this.canvasPanelsCollection[num];
    },
    setColors: function(background, text, chart) {
        for (var i = 0; i < this.canvasPanelsCollection.length; i++) {
            this.canvasPanelsCollection[i].setColors(background, text, chart);
        }
        this.canvasStateUpdate();
    },
    canvasStateUpdate: function() {
        for (var i = 0; i < this.canvasPanelsCollection.length; i++) {
            this.canvasPanelsCollection[i].repaint(this.page, this.resultsPerPage);
        }
    },
    fitPanelsSizeOnResize: function() {
        for (var i = 0; i < this.canvasPanelsCollection.length; i++) {
            this.canvasPanelsCollection[i].fitOnResize();
        }
        this.canvasStateUpdate();
    },
    hasNextPage: function() {
        return this.page < this.maxPageNum;
    },
    hasPreviousPage: function() {
        return this.page > 1;
    },
    previousPage: function() {
        this.page--;
        this.canvasStateUpdate();
    },
    nextPage: function() {
        this.page++;
        this.canvasStateUpdate();
    },
    changePainter: function(painterName, panelNum) {
        this.getElement(panelNum).changePainter(painterName);
        this.canvasStateUpdate();
    }

};

CHART.classes.instantances = {
    canvasPanelsManager: null,
    initialize: function() {
        var canvasPainter = new CHART.classes.definitions.LinePainter(document.getElementById('mainChart'));
        var canvasPanel = new CHART.classes.definitions.ChartPanel("mainChartPanel", 16 / 6, canvasPainter, 10);
        this.canvasPanelsManager = new CHART.classes.definitions.CanvasPanelsManager();
        this.canvasPanelsManager.addAllPanels(canvasPanel);
        this.canvasPanelsManager.fitPanelsSizeOnResize();
    }
};

CHART.classes.events = {
    addOnLoadEvents: function() {
        var onLoadFunction = function(event) {
            CHART.classes.instantances.initialize();
            CHART.classes.events.addColorChangeEvent();
            CHART.classes.events.addBeanColorsToChooserButtons(event);
            CHART.classes.events.addCustomInputButtonEvents();
            CHART.classes.events.addNavigationChartButtonClickedEvents();
        };
        GLOBALS.utils.EventUtil.addHandler(window, "load", onLoadFunction);
    },
    addOnResizeEvents: function() {
        var onResizeFunction = function(event) {
            CHART.classes.instantances.canvasPanelsManager.fitPanelsSizeOnResize();
        };
        GLOBALS.utils.EventUtil.addHandler(window, "resize", onResizeFunction);
    },
    addColorChangeEvent: function() {
        var target = document.getElementById("chartProperties:colorChooseButton");
        var actionTargets = [document.getElementById("chartProperties:colorPicker1_input"),
            document.getElementById("chartProperties:colorPicker2_input"),
            document.getElementById("chartProperties:colorPicker3_input")];
        var action = function(event) {
            CHART.classes.instantances.canvasPanelsManager.setColors("#" + actionTargets[0].value,
                    "#" + actionTargets[1].value, "#" + actionTargets[2].value);
        };
        GLOBALS.utils.EventUtil.addHandler(target, "click", action);
    },
    applyScreenRatioToPanel: function(ratio, panelNum) {
        CHART.classes.instantances.canvasPanelsManager.setAspectRatio(ratio, panelNum);
        console.log("event works");
        GLOBALS.events.openingMenuResizer();
    },
    setResultsPerPage: function(results) {
        CHART.classes.instantances.canvasPanelsManager.setResultsPerPage(results);
        this.updateChartNavigationOptions();
    },
    setCanvasPainter: function(type, panelNum) {
        CHART.classes.instantances.canvasPanelsManager.changePainter(type, panelNum);
    },
    updateChartNavigationOptions: function() {
        var leftNavi = document.getElementById('leftChartChanger');
        var rightNavi = document.getElementById('rightChartChanger');
        if (CHART.classes.instantances.canvasPanelsManager.hasNextPage()) {
            CHART.classes.events.setChartNavigationElementVisible(rightNavi);
        }
        else {
            CHART.classes.events.setChartNavigationElementInvisible(rightNavi);
        }
        if (CHART.classes.instantances.canvasPanelsManager.hasPreviousPage()) {
            CHART.classes.events.setChartNavigationElementVisible(leftNavi);
        }
        else {
            CHART.classes.events.setChartNavigationElementInvisible(leftNavi);
        }
    },
    clearDateFields: function() {
        var fromDate = document.getElementById('chartsForm:dateFromCal_input');
        var toDate = document.getElementById('chartsForm:dateToCal_input');
        fromDate.value = "";
        toDate.value = "";
    },
    setChartNavigationElementVisible: function(element) {
        var classes = element.className.split(" ");
        var classIndex = classes.indexOf("hiddenClass");
        if (classIndex != -1) {
            classes.splice(classIndex, 1);
            element.className = classes.join(' ');
        }
    }
    ,
    setChartNavigationElementInvisible: function(element) {
        var classes = element.className.split(" ");
        var classIndex = classes.indexOf("hiddenClass");
        if (classIndex == -1) {
            element.className += " hiddenClass";
        }
    },
    addNavigationChartButtonClickedEvents: function() {
        var leftNavi = document.getElementById('leftChartChanger');
        var rightNavi = document.getElementById('rightChartChanger');
        GLOBALS.utils.EventUtil.addHandler(leftNavi, "click", function(evt) {
            CHART.classes.instantances.canvasPanelsManager.previousPage();
            CHART.classes.events.updateChartNavigationOptions();
        });
        GLOBALS.utils.EventUtil.addHandler(rightNavi, "click", function(evt) {
            CHART.classes.instantances.canvasPanelsManager.nextPage();
            CHART.classes.events.updateChartNavigationOptions();
        });
    },
    addCustomInputButtonEvents: function() {
        var target = document.getElementById("chartProperties:colorChooseButton");
        GLOBALS.utils.EventUtil.addHandler(target, "mouseover", function(event) {
            target.className += " ui-state-hover";
        });
        GLOBALS.utils.EventUtil.addHandler(target, "mouseout", function(event) {

            var classes = target.className.split(' ');
            var class_index_h = classes.indexOf("ui-state-hover");
            var class_index_a = classes.indexOf("ui-state-active");
            if (class_index_h != -1) {
                classes.splice(class_index_h, 1);
            }
            if (class_index_a != -1) {
                classes.splice(class_index_a, 1);
            }
            target.className = classes.join(' ');
        });
        GLOBALS.utils.EventUtil.addHandler(target, "mousedown", function(event) {
            var classes = target.className.split(' ');
            var class_index = classes.indexOf("ui-state-hover");
            if (class_index != -1) {
                classes.splice(class_index, 1);
            }
            target.className = classes.join(' ');
            target.className += " ui-state-active";
        });
        GLOBALS.utils.EventUtil.addHandler(target, "mouseup", function(event) {
            var classes = target.className.split(' ');
            var class_index = classes.indexOf("ui-state-active");
            if (class_index != -1) {
                classes.splice(class_index, 1);
                target.className = classes.join(' ');
                target.className += " ui-state-hover";
            }
        });
    },
    addBeanColorsToChooserButtons: function(event) {
        var buttonInnerFieldsColors = [document.getElementById("chartProperties:colorPicker1_livePreview"),
            document.getElementById("chartProperties:colorPicker2_livePreview"),
            document.getElementById("chartProperties:colorPicker3_livePreview")];
        var realJavaBeanColors = [document.getElementById("chartProperties:colorPicker1_input"),
            document.getElementById("chartProperties:colorPicker2_input"),
            document.getElementById("chartProperties:colorPicker3_input")];
        var defaultColors = ["black", "white", "green"];
        for (var i = 0; i < buttonInnerFieldsColors.length; i++) {
            var fieldColor = realJavaBeanColors[i].value;
            if (fieldColor != null && fieldColor.length > 0) {
                buttonInnerFieldsColors[i].style.backgroundColor = "#" + fieldColor;
            }
            else {
                buttonInnerFieldsColors[i].style.backgroundColor = defaultColors[i];
            }

        }
    }
};

CHART.classes.events.addOnLoadEvents();
CHART.classes.events.addOnResizeEvents();