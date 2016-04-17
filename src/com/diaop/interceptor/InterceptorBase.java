package com.diaop.interceptor;

import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;

@CreatePattern(CreatePatternType.SINGLETON)
public class InterceptorBase {
    
    public void before() {}
    public void after() {}
}
