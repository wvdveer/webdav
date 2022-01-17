package wv.webdav.jaxb;

import javax.xml.bind.annotation.XmlElement;

public class Prop {

    private ResourceType resourceType;

    @XmlElement
    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

}
