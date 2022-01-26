/*
 * Copyright (c)  2022 Ward van der Veer.  Licensed under the Apache Licence.
 */

package wv.webdav;

public enum Depth {

    Zero ("0"),
    One ("1"),
    OneNoRoot ("1,noroot"),
    Infinity ("infinity"),
    InfinityNoRoot ("infinity,noroot");

    private String text;

    Depth(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static Depth fromString(String text) {
        for (Depth b : Depth.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Invalid Depth " + text + " supplied");
    }

    public boolean isNoRoot() {
        return this == Depth.OneNoRoot || this == Depth.InfinityNoRoot;
    }

}
