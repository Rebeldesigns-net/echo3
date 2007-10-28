ArcTest = { };

/**
 * TestComponent component.
 */
ArcTest.TestComponent = EchoCore.extend(EchoApp.Component, {

    componentType: "ArcTestComponent",
    
    globalInitialize: function() {
        EchoApp.ComponentFactory.registerType("ArcTestComponent", this);
    }
});

/**
 * TestContainer component.
 */
ArcTest.TestContainer = EchoCore.extend(EchoApp.Component, {

    componentType: "ArcTestContainer",

    globalInitialize: function() {
        EchoApp.ComponentFactory.registerType("ArcTestContainer", this);
    }
});

/**
 * TestPane component.
 */
ArcTest.TestPane = EchoCore.extend(EchoApp.Component, {

    componentType: "ArcTestPane",

    globalInitialize: function() {
        EchoApp.ComponentFactory.registerType("ArcTestPane", this);
    }
});

ArcTest.ComponentSync = { };

/**
 * Component rendering peer: TestComponent
 */
ArcTest.ComponentSync.TestComponent = EchoCore.extend(EchoArc.ComponentSync, {

    globalInitialize: function() {
        EchoRender.registerPeer("ArcTestComponent", this);
    },

    initialize: function() { },

    createBaseComponent: function() {
        var label = new EchoApp.Label();
        label.setProperty("text", "This is a freeclient label: " + this.component.getRenderProperty("text"));
        return label;
    }
});

/**
 * Component rendering peer: TestContainer
 */
ArcTest.ComponentSync.TestContainer = EchoCore.extend(EchoArc.ComponentSync, {

    globalInitialize: function() {
        EchoRender.registerPeer("ArcTestContainer", this);
    },

    initialize: function() { },
    
    createBaseComponent: function() {
        var contentPane = new EchoApp.ContentPane();
        for (var i = 0; i < this.component.children.length; ++i) {
            var windowPane = new EchoApp.WindowPane({
                positionX: new EchoApp.Extent(120 * (i % 4)),
                positionY: new EchoApp.Extent(120 * parseInt(i / 4)),
                width: new EchoApp.Extent(100),
                height: new EchoApp.Extent(100)
            });
            contentPane.add(windowPane);
            
            var childContainer = new EchoArc.ChildContainer({
                component: this.component.children[i]
            });
            windowPane.add(childContainer);
        }
        return contentPane;
    },
    
    getDomainElement: function() {
        return this._divElement;
    },
    
    renderAdd: function(update, parentElement) {
        EchoArc.ComponentSync.prototype.renderAdd.call(this, update, parentElement);
        this._divElement = document.createElement("div");
        this._divElement.style.cssText 
                = "position:relative; width:100%; height:450px; background-color: #3f3f6f; border: 1px #3f3f6f outset";
        parentElement.appendChild(this._divElement);
    },
    
    renderDispose: function(update) {
        EchoArc.ComponentSync.prototype.renderDispose.call(this, update);
        this._divElement = null;
    }
});

/**
 * Component rendering peer: TestPane
 */
ArcTest.ComponentSync.TestPane = EchoCore.extend(EchoArc.ComponentSync, {

    globalInitialize: function() {
        EchoRender.registerPeer("ArcTestPane", this);
    },

    initialize: function() {
        this._addedLabelCount = 0;
    },

    createBaseComponent: function() {
        var contentPane = new EchoApp.ContentPane();
        
        var windowPane = new EchoApp.WindowPane();
        windowPane.setProperty("title", "A FreeClient WindowPane");
        contentPane.add(windowPane);
        
        var mainColumn = new EchoApp.Column();
        mainColumn.setProperty("cellSpacing", new EchoApp.Extent("5px"));
        mainColumn.setProperty("insets", new EchoApp.Insets("10px"));
        windowPane.add(mainColumn);
        
        var controlsRow = new EchoApp.Row();
        controlsRow.setProperty("cellSpacing", new EchoApp.Extent("10px"));
        mainColumn.add(controlsRow);
        
        var addButton = new EchoApp.Button();
        addButton.setProperty("text", "Add Label");
        addButton.setProperty("background", new EchoApp.Color("#00ff00"));
        addButton.addListener("action", new EchoCore.MethodRef(this, this._processAddButton));
        controlsRow.add(addButton);
    
        var removeButton = new EchoApp.Button();
        removeButton.setProperty("text", "Remove Label");
        removeButton.setProperty("background", new EchoApp.Color("#ff0000"));
        removeButton.addListener("action", new EchoCore.MethodRef(this, this._processRemoveButton));
        controlsRow.add(removeButton);
        
        this._testColumn = new EchoApp.Column();
        mainColumn.add(this._testColumn);
    
        return contentPane;
    },
    
    getDomainElement:  function() {
        return this._divElement;
    },
    
    _processAddButton: function(e) {
        var label = new EchoApp.Label();
        label.setProperty("text", "Added Label " + ++this._addedLabelCount);
        this._testColumn.add(label);
    },
    
    _processRemoveButton: function(e) {
        var count = this._testColumn.getComponentCount();
        if (count > 0) {
            this._testColumn.remove(count - 1);
        }
    },
    
    renderAdd: function(update, parentElement) {
        EchoArc.ComponentSync.prototype.renderAdd.call(this, update, parentElement);
        this._divElement = document.createElement("div");
        this._divElement.style.cssText 
                = "position:relative; width:100%; height:450px; background-color: #3f3f6f; border: 1px #3f3f6f outset";
        parentElement.appendChild(this._divElement);
    },
    
    renderDispose: function(update) {
        EchoArc.ComponentSync.prototype.renderDispose.call(this, update);
        this._testColumn = null;
        this._divElement = null;
    }
});