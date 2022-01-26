/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav;

import java.util.Date;

public abstract class WebDavFile extends WebDavItem {

    public WebDavFile(WebDavFolder parent, String name) {
        super(parent, name, new Date());
    }

}
