package wv.webdav;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;

public abstract class WebDavItem {

    protected WebDavFolder parent;
    protected String name;

    protected WebDavItem(WebDavFolder parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    protected ResponseEntity<byte[]> routeRequest(String method, String reqUrl, int depth, Object body) {
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
    protected abstract ResponseEntity<byte[]> doPropFind(@NonNull String reqUrl, int depth, @Nullable Object body);

    @NonNull
    protected abstract ResponseEntity<byte[]> doGet(@NonNull String reqUrl);

    @NonNull
    protected ResponseEntity<byte[]> buildError(int errorCode, String errorMessage) {
        return ResponseEntity.status(errorCode).body(errorMessage.getBytes(StandardCharsets.UTF_8));
    }

}
