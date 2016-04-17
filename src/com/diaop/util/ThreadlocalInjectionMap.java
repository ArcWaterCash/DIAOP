package com.diaop.util;

import java.util.HashMap;
import java.util.Map;

public enum ThreadlocalInjectionMap {
	INSTANCE,
	;
	
	private ThreadLocal<Map<String, Object>> tl = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected synchronized Map<String, Object> initialValue() {
			return new HashMap<String, Object>();
		}
	};
	
	public Map<String, Object> get() {
		return tl.get();
	}
	
	public void set(String classname, Object instance) {
		Map<String, Object> map = tl.get();
		map.put(classname, instance);
		tl.set(map);
	}
	
	public void remove() {
		tl.remove();
	}
}
