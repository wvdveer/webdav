package wv.webdav.jaxb;

import javax.xml.bind.annotation.XmlElement;

public class PropStat {

    private String status;
    private Prop prop;

    @XmlElement
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlElement
    public Prop getProp() {
        return prop;
    }

    public void setProp(Prop prop) {
        this.prop = prop;
    }
}
