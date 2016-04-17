package com.diaop.util;

import java.util.HashMap;
import java.util.Map;

public enum SingletonInjectionMap {
    INSTANCE,
    ;
    
    private Map<String, Object> map = new HashMap<String, Object>();
    
    public Map<String, Object> get() {
    	return map;
    }
    
    public void set(String classname, Object instance) {
    	map.put(classname, instance);
    }
    
    public void remove() {
    	map.clear();
    }
}
