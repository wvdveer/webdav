/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav.jaxb;

import com.fasterxml.jackson.annotation.JsonInclude;
import javax.xml.bind.annotation.XmlElement;

public class CollectionResourceType implements ResourceType {

    @JsonInclude(JsonInclude.Include.ALWAYS)
    @XmlElement
    public String getCollection() {
        return null;
    }

}
