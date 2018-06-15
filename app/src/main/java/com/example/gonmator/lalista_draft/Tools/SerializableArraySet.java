package com.example.gonmator.lalista_draft.Tools;

import android.support.v4.util.ArraySet;

import java.io.Serializable;

/**
 * Created by gonmator on 12.11.17.
 */

public class SerializableArraySet<Type> implements Serializable {
    private ArraySet<Type> mArraySet;

    public SerializableArraySet() {
        mArraySet = new ArraySet<>();
    }


}
