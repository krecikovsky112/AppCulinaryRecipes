package com.appculinaryrecipes.youtube.search;

public class Response {

    Item[] items;

    public Item getItem(int index) {
        return items[index];
    }
}
