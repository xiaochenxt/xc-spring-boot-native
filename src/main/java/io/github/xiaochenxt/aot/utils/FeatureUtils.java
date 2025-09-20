package io.github.xiaochenxt.aot.utils;

import org.graalvm.nativeimage.RuntimeOptions;
import org.graalvm.nativeimage.hosted.*;
import org.graalvm.nativeimage.impl.ConfigurationCondition;
import org.graalvm.nativeimage.impl.RuntimeResourceSupport;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 简化注册，仅限在{@link Feature}中使用
 * @author xiaochen
 * @since 2025/8/22
 */
public class FeatureUtils extends CollectUtils {

    private final ClassLoader classLoader;

    public static FeatureUtils newInstance(ClassLoader classLoader) {
        return new FeatureUtils(classLoader);
    }

    public FeatureUtils(ClassLoader classLoader) {
        super(classLoader);
        this.classLoader = classLoader;
    }

    public ClassLoader classLoader() {
        return classLoader;
    }

    public boolean isPresent(String className) {
        try {
            classLoader.loadClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public Class<?> loadClass(String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public void registerReflection(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getDeclaredConstructors());
            RuntimeReflection.register(c.getDeclaredMethods());
            RuntimeReflection.register(c.getDeclaredFields());
            if (debug) System.out.println("registering reflect " + c.getName());
        }
    }

    public void registerReflection(Method... methods) {
        RuntimeReflection.register(methods);
        List<String> s = new ArrayList<>();
        for (Method method : methods) {
            s.add(method.toString());
        }
        if (debug) System.out.println("registering reflect method " + String.join(", ", s));
    }

    public void registerReflection(Constructor<?>... constructors) {
        RuntimeReflection.register(constructors);
        List<String> s = new ArrayList<>();
        for (Constructor<?> constructor : constructors) {
            s.add(constructor.toString());
        }
        if (debug) System.out.println("registering reflect constructor " + String.join(", ", s));
    }

    public void registerReflection(Field... fields) {
        RuntimeReflection.register(fields);
        List<String> s = new ArrayList<>();
        for (Field field : fields) {
            s.add(field.toString());
        }
        if (debug) System.out.println("registering reflect field " + String.join(", ", s));
    }

    public void registerReflectionIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getDeclaredConstructors());
            RuntimeReflection.register(c.getDeclaredMethods());
            RuntimeReflection.register(c.getDeclaredFields());
            if (debug) System.out.println("registering reflect " + c.getName());
        }
    }

    public void registerReflectionConstructorsIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getDeclaredConstructors());
            if (debug) System.out.println("registering reflect constructors " + c.getName());
        }
    }

    public void registerJni(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeJNIAccess.register(c);
            RuntimeJNIAccess.register(c.getDeclaredConstructors());
            RuntimeJNIAccess.register(c.getDeclaredMethods());
            RuntimeJNIAccess.register(c.getDeclaredFields());
            if (debug) System.out.println("registering jni " + c.getName());
        }
    }

    public void registerJni(Method... methods) {
        RuntimeJNIAccess.register(methods);
        List<String> s = new ArrayList<>();
        for (Method method : methods) {
            s.add(method.toString());
        }
        if (debug) System.out.println("registering jni method " + String.join(", ", s));
    }

    public void registerJni(Constructor<?>... constructors) {
        RuntimeJNIAccess.register(constructors);
        List<String> s = new ArrayList<>();
        for (Constructor<?> constructor : constructors) {
            s.add(constructor.toString());
        }
        if (debug) System.out.println("registering jni constructor " + String.join(", ", s));
    }

    public void registerJni(Field... fields) {
        RuntimeJNIAccess.register(fields);
        List<String> s = new ArrayList<>();
        for (Field field : fields) {
            s.add(field.toString());
        }
        if (debug) System.out.println("registering jni field " + String.join(", ", s));
    }

    public void registerJniIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeJNIAccess.register(c);
            RuntimeJNIAccess.register(c.getDeclaredConstructors());
            RuntimeJNIAccess.register(c.getDeclaredMethods());
            RuntimeJNIAccess.register(c.getDeclaredFields());
            if (debug) System.out.println("registering jni " + c.getName());
        }
    }

    public void registerSystemProperty(String key, String value) {
        RuntimeSystemProperties.register(key, value);
        if (debug) System.out.println("set system properties " + key + "=" + value);
    }

    public void setOption(String optionName, String value) {
        RuntimeOptions.set(optionName, value);
        System.out.println("set options " + optionName + "=" + value);
    }

    public void registerResource(Class<?> c, String... resources) {
        for (String resource : resources) {
            Module module = c.getModule();
            RuntimeResourceAccess.addResource(module, resource);
            if (debug)  System.out.println("registering module " + module.getName()+" resource "+resource);
        }
    }

    public void registerResourceBundle(Class<?> c, String beanName, Locale... locales) {
        Module module = c.getModule();
        RuntimeResourceAccess.addResourceBundle(module, beanName, locales);
        if (debug) System.out.println("registering resourceBundle module " + module.getName() + " beanName " + beanName + " locales " + Arrays.toString(locales));
    }

    /**
     * 需要{@code --add-exports org.graalvm.nativeimage.impl}才能使用
     * <p>详见：<a href="https://github.com/oracle/graal/issues/5013">我们不希望将该 API 设为公共，因为它违反了本机映像配置元数据中资源包含的可组合性</a></p>
     * @param resources
     */
    public void ignoreResources(String... resources) {
        for (String resource : resources) {
            RuntimeResourceSupport.singleton().ignoreResources(ConfigurationCondition.alwaysTrue(), resource);
            if (debug) System.out.println("ignore resource " + resource);
        }
    }

    /**
     * 需要{@code --add-exports org.graalvm.nativeimage.impl}才能使用
     * <p>详见：<a href="https://github.com/oracle/graal/issues/5013">我们不希望将该 API 设为公共，因为它违反了本机映像配置元数据中资源包含的可组合性</a></p>
     * @param resources
     */
    public void ignoreResources(ConfigurationCondition condition,String... resources) {
        for (String resource : resources) {
            RuntimeResourceSupport.singleton().ignoreResources(condition, resource);
        }
    }

    public void registerSerialization(Class<?>... classes) {
        for (Class<?> c : classes) {
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeSerialization.register(c);
            if (debug) System.out.println("registering serializable " + c.getName());
        }
    }

    public void registerSerializationIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeSerialization.register(c);
            if (debug) System.out.println("registering serializable " + c.getName());
        }
    }

    public void registerSerializationLambdaCapturingClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            for (Method declaredMethod : c.getDeclaredMethods()) {
                if (declaredMethod.getName().contains("$deserializeLambda$")) {
                    RuntimeSerialization.registerLambdaCapturingClass(c);
                    if (debug) System.out.println("registering serializationLambdaCapturing " + c.getName());
                    break;
                }
            }
        }
    }

    public void registerSerializationIncludingAssociatedClasses(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerIncludingAssociatedClasses(c);
            if (debug) System.out.println("registering serializationIncludingAssociated " + c.getName());
        }
    }

    public void registerSerializationProxyClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerProxyClass(c);
            if (debug) System.out.println("registering serializationProxy " + c.getName());
        }
    }

    public void registerProxyIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeProxyCreation.register(c);
            if (debug) System.out.println("registering proxy " + c.getName());
        }
    }

}
