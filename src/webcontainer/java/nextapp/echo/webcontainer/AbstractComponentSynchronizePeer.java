package nextapp.echo.webcontainer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nextapp.echo.app.Component;
import nextapp.echo.app.reflect.ComponentIntrospector;
import nextapp.echo.app.reflect.IntrospectorFactory;
import nextapp.echo.app.update.ServerComponentUpdate;
import nextapp.echo.app.util.Context;

/**
 * Default abstract implementation of <code>ComponentSynchronizePeer</code>.
 * Provides implementations of all methods less <code>getComponentClass()</code>.
 * Determines properties to render to client by quertying a <code>Component</code>'s
 * local style and using a <code>ComponentIntrospector</code> to determine whether
 * those properties 
 */
public abstract class AbstractComponentSynchronizePeer 
implements ComponentSynchronizePeer {

    private Set additionalProperties = null;
    private Set stylePropertyNames = null;
    private Set indexedPropertyNames = null;
    private Set referencedProperties = null;
    private String clientComponentType;

    public AbstractComponentSynchronizePeer() {
        super();
        clientComponentType = getComponentClass().getName();
        if (clientComponentType.startsWith("nextapp.echo.app.")) {
            // Use relative class name automatically for nextapp.echo.app objects.
            int lastDot = clientComponentType.lastIndexOf(".");
            clientComponentType = clientComponentType.substring(lastDot + 1);
        }
        
        try {
            stylePropertyNames = new HashSet();
            indexedPropertyNames = new HashSet();
            Class componentClass = getComponentClass();
            ComponentIntrospector ci = (ComponentIntrospector) IntrospectorFactory.get(componentClass.getName(),
                    componentClass.getClassLoader());
            Iterator propertyNameIt = ci.getPropertyNames();
            while (propertyNameIt.hasNext()) {
                String propertyName = (String) propertyNameIt.next();
                if (ci.getStyleConstantName(propertyName) != null) {
                    stylePropertyNames.add(propertyName);
                    if (ci.isIndexedProperty(propertyName)) {
                        indexedPropertyNames.add(propertyName);
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            // Should never occur.
            throw new RuntimeException("Internal error.", ex);
        }
    }
    
    /**
     * Adds a non-indexed output property.  
     * 
     * @see #addOutputProeprty(java.lang.String, boolean)
     */
    public void addOutputProperty(String propertyName) {
        addOutputProperty(propertyName, false);
    }

    /**
     * Adds an output property.  
     * Property names added via this method will be returned by the 
     * <code>getOutputPropertyName()</code> method of this class.
     * If the indexed flag is set, the <code>isOutputPropertyIndexed</code>
     * method will also return true for thsi property name
     * 
     * @param propertyName the property name to add
     * @param indexed a flag indicating whether the property is indexed
     */
    public void addOutputProperty(String propertyName, boolean indexed) {
        if (additionalProperties == null) {
            additionalProperties = new HashSet();
        }
        additionalProperties.add(propertyName);
        if (indexed) {
            indexedPropertyNames.add(propertyName);
        }
    }

    /**
     * Default implementation: return full class name if component is not in core Echo package.
     * Return relative name for base Echo classes.
     * Overriding this method is not generally recommended, due to potential client namespace issues.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getClientComponentType()
     */
    public String getClientComponentType() {
        return clientComponentType;
    }
    
    /**
     * Returns the (most basic) supported component class.
     * 
     * @return the (most basic) supported component class
     */
    public abstract Class getComponentClass();
    
    /**
     * Returns null.  Implementations should override if they wish
     * to provide event data.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getEventDataClass(java.lang.String)
     */
    public Class getEventDataClass(String eventType) {
        return null;
    }

    /**
     * Returns an empty iterator.  Implementations should override if they
     * wish to support immediate event types.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getImmediateEventTypes(Context, Component)
     */
    public Iterator getImmediateEventTypes(Context context, Component component) {
        return Collections.EMPTY_SET.iterator();
    }

    /**
     * Returns any property from the local style of the <code>Component</code>.
     * Implementations should override if they wish to support additional properties.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getOutputProperty(nextapp.echo.app.util.Context,
     *      nextapp.echo.app.Component, java.lang.String)
     */
    public Object getOutputProperty(Context context, Component component, String propertyName, int propertyIndex) {
        if (propertyIndex == -1) {
            return component.getLocalStyle().getProperty(propertyName);
        } else {
            return component.getLocalStyle().getIndexedProperty(propertyName, propertyIndex);
        }
    }
    
    /**
     * Returns the indices of any indexed property from the local style of the <code>Component</code>.
     * Implementations should override if they wish to support additional properties.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getOutputPropertyIndices(nextapp.echo.app.util.Context, 
     *      nextapp.echo.app.Component, java.lang.String)
     */
    public Iterator getOutputPropertyIndices(Context context, Component component, String propertyName) {
        return component.getLocalStyle().getPropertyIndices(propertyName);
    }
    
    /**
     * Returns null.
     * Implementations should override if they wish to set properties on the client by invoking 
     * specific methods other than setProperty()/setIndexedProperty().
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getOutputPropertyMethodName(
     *      nextapp.echo.app.util.Context, nextapp.echo.app.Component, java.lang.String)
     */
    public String getOutputPropertyMethodName(Context context, Component component, String propertyName) {
        return null;
    }

    /**
     * Returns the names of all properties currently set in the component's local <code>Style</code>,
     * in addition to any properties added by invoking <code>addOutputProperty()</code>.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getOutputPropertyNames(Context, nextapp.echo.app.Component)
     */
    public Iterator getOutputPropertyNames(Context context, Component component) {
        final Iterator styleIterator = component.getLocalStyle().getPropertyNames();
        final Iterator additionalPropertyIterator 
                = additionalProperties == null ? null : additionalProperties.iterator();
        
        return new Iterator() {
        
            /**
             * @see java.util.Iterator#hasNext()
             */
            public boolean hasNext() {
                return styleIterator.hasNext() || 
                        (additionalPropertyIterator != null && additionalPropertyIterator.hasNext());
            }
        
            /**
             * @see java.util.Iterator#next()
             */
            public Object next() {
                if (styleIterator.hasNext()) {
                    return styleIterator.next();
                } else {
                    return additionalPropertyIterator.next(); 
                }
            }
        
            /**
             * @see java.util.Iterator#remove()
             */
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
   
    /**
     * Returns null.  Implementations receiving input properties should override.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getInputPropertyClass(java.lang.String)
     */
    public Class getInputPropertyClass(String propertyName) {
        return null;
    }
    
    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#getUpdatedOutputPropertyNames(nextapp.echo.app.util.Context,
     *      nextapp.echo.app.Component,
     *      nextapp.echo.app.update.ServerComponentUpdate)
     */
    public Iterator getUpdatedOutputPropertyNames(Context context, Component component, 
            ServerComponentUpdate update) {
        String[] updatedPropertyNames = update.getUpdatedPropertyNames();
        Set propertyNames = new HashSet();
        //FIXME. not particularly efficient.
        for (int i = 0; i < updatedPropertyNames.length; ++i) {
            if (stylePropertyNames.contains(updatedPropertyNames[i])
                    || (additionalProperties != null && additionalProperties.contains(updatedPropertyNames[i]))) {
                propertyNames.add(updatedPropertyNames[i]);
            }
        }
        return propertyNames.iterator();
    }

    /**
     * Does nothing.  Implementations requiring initialization should override this method.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#init(Context)
     */
    public void init(Context context) {
        // Do nothing.
    }

    /**
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#isIndexedProperty(nextapp.echo.app.util.Context, 
     *      nextapp.echo.app.Component, java.lang.String)
     */
    public boolean isOutputPropertyIndexed(Context context, Component component, String propertyName) {
        return indexedPropertyNames.contains(propertyName);
    }

    /**
     * Returns true for any property set as rendered-by-reference via the
     * <code>setOutputPropertyReferenced()</code> method.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#isOutputPropertyReferenced(
     *      nextapp.echo.app.util.Context, nextapp.echo.app.Component, java.lang.String)
     */
    public boolean isOutputPropertyReferenced(Context context, Component component, String propertyName) {
        return referencedProperties != null && referencedProperties.contains(propertyName);
    }
        
    /**
     * Does nothing.  Implementations handling events should overwrite this method.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#processEvent(nextapp.echo.app.util.Context,
     *      nextapp.echo.app.Component, java.lang.String, java.lang.Object)
     */
    public void processEvent(Context context, Component component, String eventType, Object eventData) {
        // Do nothing.
    }

    /**
     * Sets the rendered-by-reference state of a property.
     * <code>isOutputPropertyReferenced</code> will return true for any property set as
     * referenced using this method.
     * 
     * @param propertyName the propertyName
     * @param newValue true if the property should be rendered by reference
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#isOutputPropertyReferenced(
     *      nextapp.echo.app.util.Context, nextapp.echo.app.Component, java.lang.String)
     */
    public void setOutputPropertyReferenced(String propertyName, boolean newValue) {
        if (newValue) {
            if (referencedProperties == null) {
                referencedProperties = new HashSet();
            }
            referencedProperties.add(propertyName);
        } else {
            if (referencedProperties != null) {
                referencedProperties.remove(propertyName);
            }
        }
    }
    
    /**
     * Does nothing.  Implementations that receive input from the client should override this method.
     * 
     * @see nextapp.echo.webcontainer.ComponentSynchronizePeer#storeInputProperty(nextapp.echo.app.util.Context, 
     *      nextapp.echo.app.Component, java.lang.String, int, java.lang.Object)
     */
    public void storeInputProperty(Context context, Component component, String propertyName, int index, Object newValue) {
        // Do nothing.
    }
}
