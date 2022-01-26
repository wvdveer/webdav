/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav.jaxb;

import javax.xml.bind.annotation.XmlElement;

public class CollectionResourceType implements ResourceType {

    @XmlElement
    public String getCollection() {
        return null;
    }

}
