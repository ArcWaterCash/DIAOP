package com.diaop.interceptor;

import com.diaop.util.DiUtil;

public class ScriptCreator {
	private ScriptCreator() {

	}

	public static final String PRESCRIPT = "{";
	public static final String POSTSCRIPT = "}";
	
	public static final String DO_INJECT = DiUtil.class.getName()+".inject($0);";
	private static String GET_POINTCUT = InterceptorBase.class.getName()+" _i = "+InterceptUtil.class.getName()+".getPointcut($class,replacestr);";
	public static final String DO_BEFORE = "if(_i!=null){_i.before();}";
	public static final String DO_AFTER = "if(_i!=null){_i.after();}";
	
	public static String GET_POINTCUT(String methodname, String... paramClassnames) {
		String dq = "\"";
		StringBuilder builder = new StringBuilder();
		builder.append(dq).append(methodname).append(dq);
		for (String name : paramClassnames) {
			builder.append(",");
			builder.append(dq).append(name).append(dq);
		}
		if (paramClassnames.length == 0) {
			builder.append(",null");
		}
		String paramstr = builder.toString();
		return GET_POINTCUT.replaceAll("replacestr", paramstr);
	}
}
