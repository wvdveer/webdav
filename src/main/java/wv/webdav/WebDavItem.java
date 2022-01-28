/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import wv.webdav.jaxb.Multistatus;
import wv.webdav.jaxb.Prop;
import wv.webdav.jaxb.PropStat;
import wv.webdav.jaxb.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public abstract class WebDavItem {

    protected WebDavFolder parent;
    protected String name;
    protected Date lastModified;

    protected WebDavItem(WebDavFolder parent, String name, Date lastModified) {
        this.parent = parent;
        this.name = name;
        this.lastModified = lastModified;
    }

    protected ResponseEntity<byte[]> routeRequest(String method, String reqUrl, Depth depth, Object body) {
        switch (method) {
            case "PROPFIND":
                return doPropFind(reqUrl, depth, body);
            case "GET":
                return doGet(reqUrl);
            default:
                return buildError(HttpStatus.BAD_REQUEST.value(), "Unsupported method: " + method);
        }
    }

    @NonNull
    protected ResponseEntity<byte[]> doPropFind(String reqUrl, Depth depth, Object body) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            Multistatus ms = new Multistatus();
            ms.setResponse(buildPropFindResponses(reqUrl, depth));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            xmlMapper.writeValue(baos, ms);
            byte[] content = baos.toByteArray();
            HttpHeaders hdrs = new HttpHeaders();
            hdrs.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.TEXT_XML_VALUE));
            hdrs.put(HttpHeaders.CONTENT_LENGTH, List.of(Integer.toString(content.length)));

            return new ResponseEntity<>(content, hdrs, HttpStatus.OK);

        } catch (IOException e) {
            return buildError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @NonNull
    protected abstract ResponseEntity<byte[]> doGet(@NonNull String reqUrl);

    @NonNull
    protected ResponseEntity<byte[]> buildError(int errorCode, String errorMessage) {
        return ResponseEntity.status(errorCode).body(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

    protected List<Response> buildPropFindResponses(String reqUrl, Depth depth) throws MalformedURLException {
        return List.of(buildPropFindResponse(reqUrl));
    }

    @NonNull
    protected Response buildPropFindResponse(String reqUrl) throws MalformedURLException {
        Response response = new Response();
        URL url = new URL(reqUrl);
        String pathToItem = getUriPathToItem();
        String href = url.getProtocol() + "://" + url.getAuthority() +
                (pathToItem.startsWith("/") ? "" : "/") +
                pathToItem;
        response.setHref(href);
        PropStat propStat = new PropStat();
        propStat.setStatus("HTTP/1.1 200 OK");
        Prop prop = new Prop();
        prop.setGetLastModified(lastModified);
        propStat.setProp(prop);
        response.setPropStat(propStat);
        return response;
    }

    protected String getUriPathToItem() {
        if (this instanceof WebDavDrive) {
            String pathToReturn = ((WebDavDrive) this).getDrivePath();
            if (pathToReturn.endsWith("/")) {
                pathToReturn = pathToReturn.substring(0, pathToReturn.length()-1);
            }
            return pathToReturn;
        } else {
            return parent.getUriPathToItem() + "/" + name;
        }
    }

}
