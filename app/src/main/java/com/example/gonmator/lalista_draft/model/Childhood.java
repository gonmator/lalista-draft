package com.example.gonmator.lalista_draft.model;

/**
 * Created by gonmator on 05.05.17.
 */

public class Childhood {
    Childhood(String parent, String child) {
        this.parent = parent;
        this.child = child;
    }

    public String getParent() {
        return parent;
    }

    public String getChild() {
        return child;
    }

    String parent;
    String child;
}
