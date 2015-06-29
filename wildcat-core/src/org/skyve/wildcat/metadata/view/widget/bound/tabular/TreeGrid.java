package org.skyve.wildcat.metadata.view.widget.bound.tabular;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.skyve.wildcat.util.UtilImpl;
import org.skyve.wildcat.util.XMLUtil;

@XmlRootElement(namespace = XMLUtil.VIEW_NAMESPACE)
@XmlType(namespace = XMLUtil.VIEW_NAMESPACE,
			propOrder = {"rootIdBinding"})
public class TreeGrid extends ListGrid {
	private static final long serialVersionUID = -94277478938378729L;

	private String rootIdBinding;
	
	public String getRootIdBinding() {
		return rootIdBinding;
	}

	@XmlAttribute(name = "rootIdBinding")
	public void setRootIdBinding(String rootIdBinding) {
		this.rootIdBinding = UtilImpl.processStringValue(rootIdBinding);
	}
}
