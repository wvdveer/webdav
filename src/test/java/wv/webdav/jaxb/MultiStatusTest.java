package wv.webdav.jaxb;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MultiStatusTest {

    @Test
    public void testMultiStatus() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        Multistatus ms = new Multistatus();
        ms.setResponse(List.of(new Response()));
        ms.getResponse().get(0).setHref("test");
        PropStat propStat = new PropStat();
        propStat.setStatus("HTTP/1.1 200 OK");
        Prop prop = new Prop();
        CollectionResourceType resourceType = new CollectionResourceType();
        prop.setResourceType(resourceType);
        prop.setGetLastModified(new Date(1643182658000L));
        propStat.setProp(prop);
        ms.getResponse().get(0).setPropStat(propStat);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        xmlMapper.writeValue(baos, ms);
        String content = baos.toString();

        Assert.isTrue(content.contains("<response>"), "missing response");
        Assert.doesNotContain(content, "<response><response>", "incorrectly wrapped");
        Assert.isTrue(content.contains("<collection/>"), "missing response");
    }

}
