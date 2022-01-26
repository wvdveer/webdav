/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav.jaxb;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Multistatus {

    private List<Response> responses;

    @JacksonXmlElementWrapper(useWrapping = false)
    public List<Response> getResponse() {
        return responses;
    }

    public void setResponse(List<Response> responses) {
        this.responses = responses;
    }

}
