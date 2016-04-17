package com.diaop.test;

import com.diaop.annotation.AutoInjection;
import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;
import com.diaop.annotation.Pointcut;

@CreatePattern(CreatePatternType.PROTOTYPE)
public class TestServicePrototype {
	
	@AutoInjection
	TestLogicPrototype logicProt;
	
	@AutoInjection
	TestLogicSingleton logicSingle;
	
	@AutoInjection
	TestLogicThreadLocal logicThread;
	
	@Pointcut
	public String test(String callerName) {
		System.out.println(this.getClass().getName());
		System.out.println(logicProt.test(this.getClass().getName()));
		
		System.out.println(logicSingle.test(this.getClass().getName()));
		
		System.out.println(logicThread.test(this.getClass().getName()));
		
		return callerName;
	}
}
