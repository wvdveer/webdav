/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import wv.webdav.jaxb.Prop;
import wv.webdav.jaxb.PropStat;
import wv.webdav.jaxb.Response;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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
    protected abstract ResponseEntity<byte[]> doPropFind(@NonNull String reqUrl, Depth depth, @Nullable Object body);

    @NonNull
    protected abstract ResponseEntity<byte[]> doGet(@NonNull String reqUrl);

    @NonNull
    protected ResponseEntity<byte[]> buildError(int errorCode, String errorMessage) {
        return ResponseEntity.status(errorCode).body(errorMessage.getBytes(StandardCharsets.UTF_8));
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
