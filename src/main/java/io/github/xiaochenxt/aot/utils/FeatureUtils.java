package io.github.xiaochenxt.aot.utils;

import org.graalvm.nativeimage.hosted.*;
import org.graalvm.nativeimage.impl.ConfigurationCondition;
import org.graalvm.nativeimage.impl.RuntimeResourceSupport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 简化注册，仅限在{@link Feature}中使用
 * @author xiaochen
 * @since 2025/8/22
 */
public class FeatureUtils {

    private final ClassLoader classLoader;

    public static FeatureUtils newInstance(ClassLoader classLoader) {
        return new FeatureUtils(classLoader);
    }

    public FeatureUtils(ClassLoader classLoader) {
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
            RuntimeReflection.register(c.getClasses());
            RuntimeReflection.register(c.getConstructors());
            RuntimeReflection.register(c.getMethods());
            RuntimeReflection.register(c.getFields());
            RuntimeReflection.register(c.getDeclaredClasses());
            RuntimeReflection.register(c.getDeclaredConstructors());
            RuntimeReflection.register(c.getDeclaredMethods());
            RuntimeReflection.register(c.getDeclaredFields());
        }
    }

    public void registerReflectionBasic(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getConstructors());
            RuntimeReflection.register(c.getMethods());
            RuntimeReflection.register(c.getDeclaredFields());
        }
    }

    public void registerReflectionIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getClasses());
            RuntimeReflection.register(c.getConstructors());
            RuntimeReflection.register(c.getMethods());
            RuntimeReflection.register(c.getFields());
            RuntimeReflection.register(c.getDeclaredClasses());
            RuntimeReflection.register(c.getDeclaredConstructors());
            RuntimeReflection.register(c.getDeclaredMethods());
            RuntimeReflection.register(c.getDeclaredFields());
        }
    }

    public void registerReflectionBasicIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getConstructors());
            RuntimeReflection.register(c.getMethods());
            RuntimeReflection.register(c.getDeclaredFields());
        }
    }

    public void registerReflectionDeclaredConstructorsIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getDeclaredConstructors());
        }
    }

    public void registerJni(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeJNIAccess.register(c);
            RuntimeJNIAccess.register(c.getDeclaredConstructors());
            RuntimeJNIAccess.register(c.getConstructors());
            RuntimeJNIAccess.register(c.getDeclaredMethods());
            RuntimeJNIAccess.register(c.getMethods());
            RuntimeJNIAccess.register(c.getFields());
            RuntimeJNIAccess.register(c.getDeclaredFields());
        }
    }

    public void registerJniIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeJNIAccess.register(c);
            RuntimeJNIAccess.register(c.getDeclaredConstructors());
            RuntimeJNIAccess.register(c.getConstructors());
            RuntimeJNIAccess.register(c.getDeclaredMethods());
            RuntimeJNIAccess.register(c.getMethods());
            RuntimeJNIAccess.register(c.getFields());
            RuntimeJNIAccess.register(c.getDeclaredFields());
        }
    }

    public void registerResource(Class<?> c, String... resources) {
        for (String resource : resources) {
            RuntimeResourceAccess.addResource(c.getModule(), resource);
        }
    }

    public void registerResourceBundle(Class<?> c, String beanName, Locale... locales) {
        RuntimeResourceAccess.addResourceBundle(c.getModule(), beanName, locales);
    }

    /**
     * 需要{@code --add-exports org.graalvm.nativeimage.impl}才能使用
     * <p>详见：<a href="https://github.com/oracle/graal/issues/5013">我们不希望将该 API 设为公共，因为它违反了本机映像配置元数据中资源包含的可组合性</a></p>
     * @param resources
     */
    public void ignoreResources(String... resources) {
        for (String resource : resources) {
            RuntimeResourceSupport.singleton().ignoreResources(ConfigurationCondition.alwaysTrue(), resource);
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
        }
    }

    public void registerSerializationIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeSerialization.register(c);
        }
    }

    public void registerSerializationLambdaCapturingClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerLambdaCapturingClass(c);
        }
    }

    public void registerSerializationIncludingAssociatedClasses(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerIncludingAssociatedClasses(c);
        }
    }

    public void registerSerializationProxyClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerProxyClass(c);
        }
    }

    public void registerProxyIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeProxyCreation.register(c);
        }
    }



    public List<Class<?>> collectClass(String... packages) {
        return collectClass(null, packages);
    }

    public List<Class<?>> collectClass(Predicate<Class<?>> predicate, String... packages) {
        List<Class<?>> classes = new ArrayList<>();
        for (String basePackage : packages) {
            try {
                List<String> classNames = findClassNamesInPackage(basePackage);
                for (String className : classNames) {
                    // 过滤掉Spring相关的生成类
                    if (className.endsWith("__BeanDefinitions")
                            || className.endsWith("__ResourceAutowiring")
                            || className.endsWith("__BeanFactoryRegistrations")
                            || className.endsWith("__EnvironmentPostProcessor")
                            || className.endsWith("__ApplicationContextInitializer")
                            || className.endsWith("__Autowiring")) {
                        continue;
                    }

                    try {
                        Class<?> clazz = Class.forName(className, false, classLoader);
                        if (predicate == null || predicate.test(clazz)) {
                            classes.add(clazz);
                        }
                    } catch (ClassNotFoundException | LinkageError e) {
                        // 忽略无法加载的类
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error scanning classes", e);
            }
        }
        return classes;
    }

    public List<String> collectClassNames(String... packages) {
        List<String> classNames = new ArrayList<>();
        for (String basePackage : packages) {
            try {
                List<String> names = findClassNamesInPackage(basePackage);
                for (String className : names) {
                    // 过滤掉Spring相关的生成类
                    if (className.endsWith("__BeanDefinitions")
                            || className.endsWith("__ResourceAutowiring")
                            || className.endsWith("__BeanFactoryRegistrations")
                            || className.endsWith("__EnvironmentPostProcessor")
                            || className.endsWith("__ApplicationContextInitializer")
                            || className.endsWith("__Autowiring")) {
                        continue;
                    }
                    classNames.add(className);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error scanning classes", e);
            }
        }
        return classNames;
    }

    public List<Class<?>> findSpringBootApplicationClasses() throws IOException {
        List<Class<?>> result = new ArrayList<>();
        List<String> allClassNames = findAllClassNames();

        for (String className : allClassNames) {
            // 跳过JDK类
//            if (className.startsWith("java.") || className.startsWith("javax.")
//                    || className.startsWith("jdk.") || className.startsWith("com.sun.")
//                    || className.startsWith("sun.")) {
//                continue;
//            }

            try {
                Class<?> clazz = Class.forName(className, false, classLoader);
                // 检查是否有@SpringBootApplication注解
                if (Arrays.stream(clazz.getAnnotations()).anyMatch(annotation -> annotation.annotationType().getName().equals("org.springframework.boot.autoconfigure.SpringBootApplication"))) {
                    result.add(clazz);
                }
            } catch (ClassNotFoundException | LinkageError e) {
                // 忽略无法加载的类
            }
        }
        return result;
    }

    private List<String> findClassNamesInPackage(String packageName) throws IOException {
        List<String> classNames = new ArrayList<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();

            if ("file".equals(protocol)) {
                try {
                    File directory = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
                    if (directory.exists()) {
                        findClassesInDirectory(directory, packageName, classNames);
                    }
                } catch (Exception e) {
                    // 处理文件系统访问异常
                }
            } else if ("jar".equals(protocol)) {
                String jarPath = resource.getFile().substring(5, resource.getFile().indexOf('!'));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.startsWith(path) && entryName.endsWith(".class") && !entry.isDirectory()) {
                            String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                            classNames.add(className);
                        }
                    }
                } catch (Exception e) {
                    // 处理JAR文件访问异常
                }
            }
        }

        return classNames;
    }

    private void findClassesInDirectory(File directory, String packageName, List<String> classNames) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理子目录
                findClassesInDirectory(file, packageName + "." + file.getName(), classNames);
            } else if (file.getName().endsWith(".class")) {
                // 处理类文件
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classNames.add(className);
            }
        }
    }

    private List<String> findAllClassNames() throws IOException {
        List<String> classNames = new ArrayList<>();

        // 获取所有类路径根
        Enumeration<URL> roots = classLoader.getResources("");
        while (roots.hasMoreElements()) {
            URL root = roots.nextElement();
            String protocol = root.getProtocol();

            if ("file".equals(protocol)) {
                try {
                    File rootDir = new File(URLDecoder.decode(root.getFile(), StandardCharsets.UTF_8));
                    if (rootDir.exists() && rootDir.isDirectory()) {
                        findAllClassesInDirectory(rootDir, "", classNames);
                    }
                } catch (Exception e) {
                    // 处理文件系统访问异常
                }
            } else if ("jar".equals(protocol)) {
                String jarPath = root.getFile().substring(5, root.getFile().indexOf('!'));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.endsWith(".class") && !entry.isDirectory()) {
                            String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                            classNames.add(className);
                        }
                    }
                } catch (Exception e) {
                    // 处理JAR文件访问异常
                }
            }
        }

        return classNames;
    }

    private void findAllClassesInDirectory(File directory, String packageName, List<String> classNames) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                findAllClassesInDirectory(file, newPackage, classNames);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName.isEmpty()
                        ? file.getName().substring(0, file.getName().length() - 6)
                        : packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classNames.add(className);
            }
        }
    }

}
