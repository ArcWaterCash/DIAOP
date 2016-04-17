package com.diaop.test;

import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;
import com.diaop.interceptor.InterceptorBase;

@CreatePattern(CreatePatternType.SINGLETON)
public class TestInterceptor extends InterceptorBase {
	public void before() {System.out.println("before!"+this.getClass().getName());}
    public void after() {System.out.println("after!"+this.getClass().getName());}
}
