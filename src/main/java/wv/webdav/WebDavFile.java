/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import wv.webdav.jaxb.CollectionResourceType;
import wv.webdav.jaxb.Response;

import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

public abstract class WebDavFile extends WebDavItem {

    /**
     * Create a WebDAV file
     *
     * @param parent folder containing the file
     * @param name   name given to the file, including extension
     */
    public WebDavFile(WebDavFolder parent, String name) {
        super(parent, name, new Date());
        if (parent != null) {
            parent.addFile(this);
        }
    }

    @Override
    protected ResponseEntity<byte[]> doGet(String reqUrl) {
        try {
            byte[] content = getContent();
            HttpHeaders hdrs = new HttpHeaders();
            hdrs.put(HttpHeaders.CONTENT_TYPE, List.of(getContentType()));
            hdrs.put(HttpHeaders.CONTENT_LENGTH, List.of(Integer.toString(content.length)));

            return new ResponseEntity<>(content, hdrs, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    @Override
    protected Response buildPropFindResponse(String reqUrl) throws MalformedURLException {
        Response response = super.buildPropFindResponse(reqUrl);

        // add content length
        response.getPropStat().getProp().setContentLength(getLengthForDirectory());

        return response;
    }

    /**
     * Get length for directory listing
     * As the default calls getContent(), this should be overidden
     * where this is a performance concern
     *
     * @return the length to show in the directory listing
     */
    public int getLengthForDirectory() {
        try {
            return getContent().length;
        } catch (WebDavException e) {
            return 0;
        }
    }

    /**
     * get the content type
     *
     * @return mime type of the content
     */
    public abstract String getContentType();

    /**
     * get the file contents
     *
     * @return the file contents
     */
    public abstract byte[] getContent() throws WebDavException;

}
