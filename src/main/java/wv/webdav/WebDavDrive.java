package wv.webdav;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class WebDavDrive extends WebDavFolder {

    public static final String DEPTH_HDR = "Depth";
    public static final String PARENT_FLDR = "..";

    private String drivePath;

    /**
     * Create a drive / root folder for WebDAV
     *
     * @param drivePath should contain the path of the drive, including the web context,
     *                  but excluding host and port.  This is removed from the path in
     *                  RequestEntity to determine the referenced WebDavItem
     */
    public WebDavDrive(@NonNull String drivePath) {
        super(null, ".");
        if (!drivePath.endsWith("/")) {
            drivePath = drivePath + "/";
        }
        this.drivePath = drivePath;
    }

    /**
     * Handle a WebDAV client request
     *
     * @param req the request from the WebDAV client
     * @return the response to send to the WebDAV client
     */
    @NonNull
    public ResponseEntity<byte[]> handleRequest(@NonNull HttpServletRequest req) {
        String reqPath = req.getRequestURI();
        if (!reqPath.toLowerCase().startsWith(drivePath.toLowerCase().substring(0, drivePath.length() - 1))) {
            return buildError(HttpStatus.BAD_REQUEST.value(), "Invalid drive path");
        }
        String methodName = req.getMethod();
        String[] itemPath;
        if (reqPath.length() <= drivePath.length()) {
            //  referencing root folder
            itemPath = new String[0];
        } else {
            String itemPathAsString = reqPath.substring(drivePath.length());
            itemPath = itemPathAsString.split("/");
            itemPath = gleanParentFolderReferences(itemPath);
        }
        Depth depth = Depth.Infinity;
        if (req.getHeader(DEPTH_HDR) != null) {
            depth = Depth.fromString(req.getHeader(DEPTH_HDR));
        }
        byte[] body = null;
        if (req.getContentLength() > 0) {
            int sz = req.getContentLength();
            char[] bodyOfChar = new char[sz];
            try {
                int szRead= req.getReader().read(bodyOfChar, 0, sz);
                if  (szRead < sz) {
                    return buildError(HttpStatus.BAD_REQUEST.value(), "Invalid content");
                }
                String bodyAsString = new String(bodyOfChar);
                body = bodyAsString.getBytes(StandardCharsets.UTF_8);
            } catch (IOException e) {
                return buildError(HttpStatus.BAD_REQUEST.value(), "Invalid content");
            }
        }
        try {
            if (methodName.equalsIgnoreCase(HttpMethod.PUT.name()) ||
                    methodName.equalsIgnoreCase(HttpMethod.DELETE.name())) {
                // these methods are processed against the containing folder rather than the item
                String[] containingPath = Arrays.copyOfRange(itemPath, 0, itemPath.length - 1);
                WebDavItem webDavItem = getContainedItem(containingPath);
                if (webDavItem instanceof WebDavFolder) {
                    WebDavFolder containingFolder = (WebDavFolder) webDavItem;
                    return containingFolder.routeRequest(req.getMethod(), req.getRequestURL().toString(), depth, body);
                } else {
                    return buildError(HttpStatus.NOT_FOUND.value(), "Not a folder");
                }
            } else {
                WebDavItem webDavItem = getContainedItem(itemPath);

                return webDavItem.routeRequest(req.getMethod(), req.getRequestURL().toString(), depth, body);
            }
        } catch (FileNotFoundException e) {
            return buildError(HttpStatus.NOT_FOUND.value(), "File not found: " + e.getMessage());
        }
    }

    private String[] gleanParentFolderReferences(String[] itemPath) {
        List<String> result = Arrays.asList(itemPath);
        while (result.contains(PARENT_FLDR)) {
            if (result.get(0).equalsIgnoreCase(PARENT_FLDR)) {
                // remove parent references from start
                result.remove(0);
            } else {
                int pos = result.lastIndexOf(PARENT_FLDR);
                result.remove(pos);
                result.remove(pos - 1);
            }
        }
        return result.toArray(new String[0]);
    }

}
