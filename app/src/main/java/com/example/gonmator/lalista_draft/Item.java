package com.example.gonmator.lalista_draft;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * Created by gonmator on 22.04.17.
 */

public class Item extends Object {
    public Item() {
        mId = UUID.randomUUID();
        mItems = new Vector<Item>();
    }
    public Item(String description) {
        mId = UUID.randomUUID();
        mDescription = description;
        mItems = new Vector<Item>();
    }

    public UUID getId() {
        return mId;
    }

    public String getDescription() {
        return mDescription;
    }

    public Item getItem(int index) {
        return ((Vector<Item>)mItems).elementAt(index);
    }

    public List<Item> getItems() {
        return mItems;
    }

    public void addItem(String description) {
        mItems.add(new Item(description));
    }

    public void deleteItem(int index) {
        ((Vector)mItems).removeElementAt(index);
    }

    @Override
    public String toString() {
        return mDescription;
    }

    UUID mId;
    String mDescription;
    List<Item> mItems;
}
