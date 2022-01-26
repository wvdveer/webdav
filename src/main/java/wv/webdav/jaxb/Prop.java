/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav.jaxb;

import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

public class Prop {

    private Date lastModified;
    private ResourceType resourceType;

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @XmlElement(nillable = true)
    public String getGetlastmodified() {
        return lastModified == null ? null : lastModified.toString();
    }

    public void setGetLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

}
