/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav.jaxb;

import javax.xml.bind.annotation.XmlElement;

public class Response {

    String href;
    PropStat propStat;

    @XmlElement
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    @XmlElement
    public PropStat getPropStat() {
        return propStat;
    }

    public void setPropStat(PropStat propStat) {
        this.propStat = propStat;
    }
}
