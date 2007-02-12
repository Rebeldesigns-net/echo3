package nextapp.echo.webcontainer.sync.property;

import org.w3c.dom.Element;

import nextapp.echo.app.layout.ColumnLayoutData;
import nextapp.echo.webcontainer.OutputContext;
import nextapp.echo.webcontainer.PropertySynchronizePeer;
import nextapp.echo.webcontainer.sync.SyncUtil;

public class ColumnLayoutDataPeer
implements PropertySynchronizePeer {

    /**
     * @see nextapp.echo.webcontainer.PropertySynchronizePeer#toProperty(org.w3c.dom.Element)
     */
    public Object toProperty(Element propertyElement) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see nextapp.echo.webcontainer.PropertySynchronizePeer#toXml(
     *      nextapp.echo.webcontainer.OutputContext, org.w3c.dom.Element, java.lang.Object)
     */
    public void toXml(OutputContext out, Element propertyElement, Object propertyValue) {
        ColumnLayoutData layoutData = (ColumnLayoutData) propertyValue;
        propertyElement.setAttribute("t", "LayoutData");
        SyncUtil.toXml(out, propertyElement, "background", layoutData.getBackground());
        SyncUtil.toXml(out, propertyElement, "backgroundImage", layoutData.getBackgroundImage());
        SyncUtil.toXml(out, propertyElement, "insets", layoutData.getInsets());
    }
}
