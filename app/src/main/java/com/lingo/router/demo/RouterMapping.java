package com.lingo.router.demo;

import java.util.HashMap;

public class RouterMapping {

    public static HashMap<String, String> get() {
        HashMap<String, String> map = new HashMap<>();
        map.putAll(RouterMapping_1.get());
        return map;
    }
}
