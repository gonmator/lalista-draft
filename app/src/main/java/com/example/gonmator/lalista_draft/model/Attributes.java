package com.example.gonmator.lalista_draft.model;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by gonmator on 07.05.17.
 */

public class Attributes {
    static private final Pattern PATTERN =
            Pattern.compile("([\\w.-]+)=\"([\\w!#$%&/()=?',.\\-;:`+^*]+)\"");
    public Attributes() {
        mAttributes = new HashMap<String, String>();
    }

    static public Attributes fromString(String str) {
        Attributes rv = new Attributes();
        String[] attributes_str = TextUtils.split(str, ";");
        for (int i = 0; i < attributes_str.length; i++) {
            String attribute_str = attributes_str[i];
            Matcher m = PATTERN.matcher(attribute_str);
            rv.setValue(m.group(1), m.group(2));
        }
        return rv;
            }
    public String toString() {
        String rs = "";
        Iterator<Map.Entry<String, String>> it = mAttributes.entrySet().iterator();
        if (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            rs += attributeToString(entry.getKey(), entry.getValue());
        }
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            rs += ";" + attributeToString(entry.getKey(), entry.getValue());
        }
        return rs;
    }

    public Set<String> getNames() {
        return mAttributes.keySet();
    }
    public String getValue(String name) {
        return mAttributes.get(name);
    }
    public void setValue(String name, String value) {
        mAttributes.put(name, value);
    }

    String attributeToString(String name, String value) {
        return name + "=\"" + value + "\"";
    }

    HashMap<String, String> mAttributes;
}
