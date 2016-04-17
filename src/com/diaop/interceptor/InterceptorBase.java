package com.diaop.interceptor;

import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;

@CreatePattern(CreatePatternType.SINGLETON)
public class InterceptorBase {
    
    public void before() {System.out.println("before!"+this.getClass().getName());}
    public void after() {System.out.println("after!"+this.getClass().getName());}
}
