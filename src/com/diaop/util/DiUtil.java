package com.diaop.util;


import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.diaop.annotation.AutoInjection;
import com.diaop.annotation.CreatePattern;
import com.diaop.annotation.CreatePatternType;
import com.diaop.annotation.Mock;
import com.diaop.exception.DiaopException;
import com.diaop.interceptor.ScriptCreator;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;


public class DiUtil {
    
    private static ModeType mode = ModeType.PRODUCT;
    private static SearchOption searchOption = SearchOption.ONLYTHIS;
    
    private static final String SUBCLASS_POSTFIX = "$Sub"; 
    
    private DiUtil() {
        super();
    }
    
    private static Map<String, Object> primitiveMap;
    private static byte varbyte;
    private static short varshort;
    private static int varint;
    private static long varlong;
    private static boolean varbool;
    private static float varfloat;
    private static double vardouble;
    private static char varchar;
    
    static {
        primitiveMap = new HashMap<String, Object>();
        primitiveMap.put("byte", varbyte);
        primitiveMap.put("short", varshort);
        primitiveMap.put("int", varint);
        primitiveMap.put("long", varlong);
        primitiveMap.put("boolean", varbool);
        primitiveMap.put("float", varfloat);
        primitiveMap.put("double", vardouble);
        primitiveMap.put("char", varchar);
    }
    
    /**
     * テストモードに設定する（モックが動くようになる）
     */
    public static void setTestMode() {
        mode = ModeType.TEST;
    }
    
    /**
     * 再起的インジェクションモード
     */
    @Deprecated
    public static void setRecursiveMode() {
        searchOption = SearchOption.RECURSIVE;
    }
    
    /**
     * テストモードかどうか確認する
     * @return
     */
    public static boolean isTestMode() {
        return mode == ModeType.TEST;
    }
    
    /**
     * 初期化する
     */
    public static void initializeDI() {
        SingletonInjectionMap.INSTANCE.remove();
        mode = ModeType.PRODUCT;
        searchOption = SearchOption.ONLYTHIS;
    }
    
    public static void finalizeDI() {
    	SingletonInjectionMap.INSTANCE.remove();
    	ThreadlocalInjectionMap.INSTANCE.remove();
    }
    
    public static <T> T getInstanceInjectedProperties(Class<T> target) throws DiaopException {
        Object instance = getInstance(target);
        inject(instance);
        
        return (T)instance;
    }
    
    /**
     * インスタンスを取得する
     * @param <T>
     * @param targetClass
     * @return
     */
    private static <T> T getInstance(Class<T> targetClass) throws DiaopException {
        targetClass = getTargetClass(targetClass);
        Object instance = null;
        
        // Singleton/Threadlocalでマップに既にあればそれを返す
        CreatePattern pattern = targetClass.getAnnotation(CreatePattern.class);
        if (pattern != null) {
            CreatePatternType type = pattern.value();
            if (CreatePatternType.SINGLETON == type) {
            	instance = (T)SingletonInjectionMap.INSTANCE.get().get(targetClass.getName());
            }
            if (CreatePatternType.THREADLOCAL == type) {
            	instance = (T)ThreadlocalInjectionMap.INSTANCE.get().get(targetClass.getName());
            }
            if (instance != null) {
            	return (T)instance;
            }
        }
        
        // なきゃ作る
        instance = newInstance(targetClass);
        
        return (T)instance;
    }
    
    /**
     * 生成したインスタンスのフィールドにインスタンスを設定する
     * @param <T>
     * @param instance
     */
    public static <T> void inject(T instance) throws DiaopException {
        Field[] fields1 = instance.getClass().getFields();
        List<Field> f1List = Arrays.asList(fields1);
        
        Field[] fields2 = instance.getClass().getDeclaredFields();
        List<Field> f2List = Arrays.asList(fields2);
        
        Set<Field> fieldSet = new HashSet<Field>();
        fieldSet.addAll(f1List);
        fieldSet.addAll(f2List);
        
        for (Field f : fieldSet) {
            AutoInjection ai = f.getAnnotation(AutoInjection.class);
            if (ai == null) continue;
            
            try {
                boolean isAccessible = f.isAccessible();
                f.setAccessible(true);
                
                // 既に何か設定されてたら何もしない
                if (f.get(instance) != null) {
                    f.setAccessible(isAccessible);
                    continue;
                }
                
                Class<T> targetClass = (ai.value() == void.class) ? (Class<T>)f.getType() : (Class<T>)ai.value();
                if (searchOption == SearchOption.ONLYTHIS) f.set(instance, getInstance((Class<T>)targetClass));
                if (searchOption == SearchOption.RECURSIVE) f.set(instance, getInstanceInjectedProperties((Class<T>)targetClass));
                f.setAccessible(isAccessible);
            } catch (IllegalAccessException e) {
                throw new DiaopException(e);
            }
        }
    }
    
    /**
     * AOPできたらするようにしつつインスタンスを生成する
     * @param <T>
     * @param targetClass
     * @return
     */
    private static <T> T newInstance(Class<T> targetClass) throws DiaopException {
        try {
            Object instance = null;
            
            // 配列の時は多次元配列も意識して※0要素配列なのでDIしても使えない
            if (targetClass.isArray()) {
                int dimension = 0;
                Class<T> temp = targetClass;
                
                while (true) {
                    if (!temp.isArray()) break;
                    
                    dimension++;
                    temp = (Class<T>)temp.getComponentType();
                    if (!temp.isArray()) {
                        int[] dimensions = new int[dimension];
                        for (int e : dimensions) {
                            e = 0;
                        }
                        instance = Array.newInstance(temp,dimensions);
                    }
                }
                return (T)instance;
            }
            
            // プリミティブ型はマップを用意しておく
            if (targetClass.isPrimitive()) return (T)primitiveMap.get(targetClass.getName());
            
            instance = targetClass.newInstance();
            registerInjectionMap(instance);
            
            return (T)instance;
        } catch (InstantiationException e) {
            throw new DiaopException(e);
        } catch (IllegalAccessException e) {
            throw new DiaopException(e);
        } 
    }
    
    /**
     * Singletonのはインスタンス管理しているマップに登録する
     * @param instance
     * @return
     */
    private synchronized static void registerInjectionMap(Object instance) throws DiaopException {
        
        CreatePattern pattern = instance.getClass().getAnnotation(CreatePattern.class);
        if (pattern == null) return;
        
        CreatePatternType type = pattern.value();
        if (CreatePatternType.SINGLETON == type) {
            SingletonInjectionMap.INSTANCE.set(instance.getClass().getName(), instance);
        }
        if (CreatePatternType.THREADLOCAL == type) {
        	ThreadlocalInjectionMap.INSTANCE.set(instance.getClass().getName(), instance);
        }
    }
    
    /**
     * 引数に指定したクラスにMockが指定してあってテストモードの場合はモックを返す。
     * @param <T>
     * @param targetClass
     * @return
     * @throws DiaopException 
     */
    private static <T> Class<T> getTargetClass(Class<T> targetClass) throws DiaopException {
        if (isTestMode()) {
            Mock mock = targetClass.getAnnotation(Mock.class);
            if (mock != null) targetClass = (Class<T>)mock.value();
        }        
        targetClass = (Class<T>)getSubclass(targetClass);
        
        return targetClass;
    }
    
    private static <T> Class<T> getSubclass(Class<T> targetClass) throws DiaopException {
    	if (!shouldCreateSubclass(targetClass)) return targetClass;
    	
		try {
			ClassPool classPool = ClassPool.getDefault();
			CtClass subClass = classPool.get(createSubclassName(targetClass.getName()));
			return (Class<T>)Class.forName(createSubclassName(targetClass.getName()));
		} catch (NotFoundException e) {
			return (Class<T>)createDynamicSubclass(targetClass);
		} catch (ClassNotFoundException e) {
			throw new DiaopException(e);
		}
    }
    
    private static <T> Class<? extends T> createDynamicSubclass(Class<T> targetClass) throws DiaopException {
		try {
			ClassPool classPool = ClassPool.getDefault();
			CtClass subClass = classPool.get(targetClass.getName());
			subClass.setName(createSubclassName(targetClass.getName()));
			
			CtClass ctTargetClass = classPool.get(targetClass.getName());
			subClass.setSuperclass(ctTargetClass);
			
			StringBuilder scriptBuilder = new StringBuilder();
			
			CtMethod[] methods = subClass.getDeclaredMethods();
			for (CtMethod method : methods) {
				if (javassist.Modifier.isStatic(method.getModifiers())) continue;
				
				CtClass[] types = method.getParameterTypes();
				String[] paramType = new String[types.length];
				for (int i = 0; i < types.length; i++) {
					paramType[i] = types[i].getName();
				}
				
				scriptBuilder.setLength(0);
				scriptBuilder.append(ScriptCreator.PRESCRIPT);
				scriptBuilder.append(ScriptCreator.DO_INJECT);
				scriptBuilder.append(ScriptCreator.GET_POINTCUT(method.getName(), paramType));
				scriptBuilder.append(ScriptCreator.DO_BEFORE);
				scriptBuilder.append(ScriptCreator.POSTSCRIPT);
				method.insertBefore(scriptBuilder.toString());
				
				scriptBuilder.setLength(0);
				scriptBuilder.append(ScriptCreator.PRESCRIPT);
				scriptBuilder.append(ScriptCreator.GET_POINTCUT(method.getName(), paramType));
				scriptBuilder.append(ScriptCreator.DO_AFTER);
				scriptBuilder.append(ScriptCreator.POSTSCRIPT);
				method.insertAfter(scriptBuilder.toString());
			}
			
			subClass.writeFile();
			
			return (Class<? extends T>)subClass.toClass();
			
		} catch (NotFoundException e) {
			throw new DiaopException(e);
		} catch (CannotCompileException e) {
			throw new DiaopException(e);
		} catch (IOException e) {
			throw new DiaopException(e);
		}
    }
    
    private static <T> boolean shouldCreateSubclass(Class<T> targetClass) {
    	if (targetClass.isInterface()) return false;
    	if (targetClass.isArray()) return false;
    	if (targetClass.isPrimitive()) return false;
    	if (Modifier.isFinal(targetClass.getModifiers())) return false;
    	if (Modifier.isAbstract(targetClass.getModifiers())) return false;
    	
    	return true;
    }
    
    private static String createSubclassName(String targetClassName) {
    	StringBuilder builder = new StringBuilder(targetClassName).append(SUBCLASS_POSTFIX);
    	return builder.toString();
    }
}
