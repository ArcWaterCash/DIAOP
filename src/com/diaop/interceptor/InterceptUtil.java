package com.diaop.interceptor;

import java.lang.reflect.Method;

import com.diaop.annotation.Pointcut;
import com.diaop.exception.DiaopException;
import com.diaop.util.DiUtil;

public class InterceptUtil {
    
    public static InterceptorBase getPointcut(Class c, String methodname, String... paramtypes) throws DiaopException {
        try {
        	Class[] types = null;
        	if (paramtypes != null) {
        		types = new Class[paramtypes.length];
            	for (int i = 0; i < paramtypes.length; i++) {
            		types[i] = Class.forName(paramtypes[i]);
            	}
        	}
        	
        	Method m = c.getMethod(methodname, types);
        	
        	Pointcut pointcut = m.getAnnotation(Pointcut.class);
        	if (pointcut == null) return null;
        	
        	return DiUtil.getInstanceInjectedProperties(pointcut.value());
		} catch (SecurityException e) {
			throw new DiaopException(e);
		} catch (ClassNotFoundException e) {
			throw new DiaopException(e);
		} catch (NoSuchMethodException e) {
			throw new DiaopException(e);
		}
    }
    
}
