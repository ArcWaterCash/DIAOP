package com.diaop.test;

import com.diaop.annotation.AutoInjection;
import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;
import com.diaop.annotation.Mock;
import com.diaop.annotation.Pointcut;

@Mock(TestServiceSingletonMock.class)
@CreatePattern(CreatePatternType.SINGLETON)
public class TestServiceSingleton {

	@AutoInjection
	TestLogicPrototype logicProt;
	
	@AutoInjection
	TestLogicSingleton logicSingle;
	
	@AutoInjection
	TestLogicThreadLocal logicThread;
	
	@Pointcut
	public void test() {
		System.out.println(this.getClass().getName());
		logicProt.test();
		
		logicSingle.test();
		
		logicThread.test();
	}
}
