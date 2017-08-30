package com.example.gonmator.lalista_draft.model;

import com.example.gonmator.lalista_draft.model.Attributes;

import java.util.UUID;

/**
 * Created by gonmator on 04.05.17.
 */

public class Lista {
    private UUID mLid;
    private String mDescription;
    private Attributes mAttributes;

    public Lista() {
        this("");
    }
    public Lista(String description) {
        this(description, new Attributes());
    }
    public Lista(String description, Attributes attributes) {
        this(UUID.randomUUID(), description, attributes);
    }
    public Lista(UUID lid, String description, Attributes attributes) {
        mLid = lid;
        mDescription = description;
        mAttributes = attributes;
    }
    public Lista(String lid_str, String description, String attributes_str) {
        this(UUID.fromString(lid_str), description, Attributes.fromString(attributes_str));
    }

    public UUID getLid() {
        return mLid;
    }

    public String getLidStr() {
        return mLid.toString();
    }

    public String getDescription() {
        return mDescription;
    }

    public Attributes getAttributes() {
        return mAttributes;
    }

    public String getAttributesStr() {
        return mAttributes.toString();
    }
}
