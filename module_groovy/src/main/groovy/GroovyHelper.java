import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;

/**
 * @author zhangshuai
 * Java 与 Groovy 交互工具
 */
public class GroovyHelper {


    private static String PATH_GROOVY_FILE = "module_groovy/src/main/groovy/";

    private static String[] root = new String[]{PATH_GROOVY_FILE};
    private static GroovyScriptEngine engine = null;
    private static ClassLoader classLoader = null;
    private static GroovyClassLoader gCLoader = null;

    static {
        try {
            engine = new GroovyScriptEngine(root);
            classLoader = GroovyHelper.class.getClassLoader();
            gCLoader = new GroovyClassLoader(classLoader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于调用指定groovy脚本中的指定方法
     */
    public static Object invokeMethod(String scriptName, String methodName, Object... params) throws Exception {
        Object obj = null;
        Class scriptClass = null;
        GroovyObject scriptInstance = null;

        try {
            scriptClass = engine.loadScriptByName(scriptName);
            scriptInstance = (GroovyObject) scriptClass.newInstance();
        } catch (ResourceException | ScriptException | InstantiationException | IllegalArgumentException e) {
            System.out.println("加载脚本{" + scriptName + "}出现异常，" + e.toString());
            throw new Exception("加载脚本{" + scriptName + "}失败");
        }

        try {
            obj = scriptInstance.invokeMethod(methodName, params);
        } catch (IllegalArgumentException e) {
            System.out.println("执行方法" + methodName + "参数出现异常, 参数为" + params + ", " + e.toString());
            throw new Exception("调用方法[" + methodName + "]失败，因参数不合法");
        }
        return obj;
    }


    /**
     * 接口调用
     * groovy全名，包括后缀，例如下:
     * HelloWorld.groovy
     *
     * @param fileName
     */
    public static IFoo invokeMethod(String fileName) throws IOException, IllegalAccessException, InstantiationException {
        String path = PATH_GROOVY_FILE + fileName;
        Class gClass = gCLoader.parseClass(new File(path));
        IFoo ifoo = (IFoo) gClass.newInstance();
        return ifoo;
    }


}
