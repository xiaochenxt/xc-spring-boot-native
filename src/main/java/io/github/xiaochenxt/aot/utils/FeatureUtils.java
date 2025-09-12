package io.github.xiaochenxt.aot.utils;

import org.graalvm.nativeimage.RuntimeOptions;
import org.graalvm.nativeimage.hosted.*;
import org.graalvm.nativeimage.impl.ConfigurationCondition;
import org.graalvm.nativeimage.impl.RuntimeResourceSupport;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

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
            System.out.println("registering reflect " + c.getName());
        }
    }

    public void registerReflection(Executable... executables) {
        List<String> s = new ArrayList<>();
        for (Executable executable : executables) {
            RuntimeReflection.register(executable);
            s.add(executable.toString());
        }
        System.out.println("registering reflect " + String.join(", ", s));
    }

    public void registerReflectionBasic(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getConstructors());
            RuntimeReflection.register(c.getMethods());
            RuntimeReflection.register(c.getDeclaredFields());
            System.out.println("registering reflect " + c.getName());
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
            System.out.println("registering reflect " + c.getName());
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
            System.out.println("registering reflect " + c.getName());
        }
    }

    public void registerReflectionDeclaredConstructorsIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            RuntimeReflection.register(c);
            RuntimeReflection.register(c.getDeclaredConstructors());
            System.out.println("registering reflect " + c.getName());
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
            System.out.println("registering jni " + c.getName());
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
            System.out.println("registering jni " + c.getName());
        }
    }

    public void registerSystemProperty(String key, String value) {
        RuntimeSystemProperties.register(key, value);
        System.out.println("set system properties " + key + "=" + value);
    }

    public void setOption(String optionName, String value) {
        RuntimeOptions.set(optionName, value);
        System.out.println("set options " + optionName + "=" + value);
    }

    public void registerResource(Class<?> c, String... resources) {
        for (String resource : resources) {
            Module module = c.getModule();
            RuntimeResourceAccess.addResource(module, resource);
            System.out.println("registering module " + module.getName()+" resource "+resource);
        }
    }

    public void registerResourceBundle(Class<?> c, String beanName, Locale... locales) {
        Module module = c.getModule();
        RuntimeResourceAccess.addResourceBundle(module, beanName, locales);
        System.out.println("registering resourceBundle module " + module.getName() + " beanName " + beanName + " locales " + Arrays.toString(locales));
    }

    /**
     * 需要{@code --add-exports org.graalvm.nativeimage.impl}才能使用
     * <p>详见：<a href="https://github.com/oracle/graal/issues/5013">我们不希望将该 API 设为公共，因为它违反了本机映像配置元数据中资源包含的可组合性</a></p>
     * @param resources
     */
    public void ignoreResources(String... resources) {
        for (String resource : resources) {
            RuntimeResourceSupport.singleton().ignoreResources(ConfigurationCondition.alwaysTrue(), resource);
            System.out.println("ignore resource " + resource);
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
            System.out.println("registering serializable " + c.getName());
        }
    }

    public void registerSerializationIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeSerialization.register(c);
            System.out.println("registering serializable " + c.getName());
        }
    }

    public void registerSerializationLambdaCapturingClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            for (Method declaredMethod : c.getDeclaredMethods()) {
                if (declaredMethod.getName().contains("$deserializeLambda$")) {
                    RuntimeSerialization.registerLambdaCapturingClass(c);
                    System.out.println("registering serializationLambdaCapturing " + c.getName());
                    break;
                }
            }
        }
    }

    public void registerSerializationIncludingAssociatedClasses(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerIncludingAssociatedClasses(c);
            System.out.println("registering serializationIncludingAssociated " + c.getName());
        }
    }

    public void registerSerializationProxyClass(Class<?>... classes) {
        for (Class<?> c : classes) {
            RuntimeSerialization.registerProxyClass(c);
            System.out.println("registering serializationProxy " + c.getName());
        }
    }

    public void registerProxyIfPresent(String... classes) {
        for (String cs : classes) {
            Class<?> c = loadClass(cs);
            if (c == null) continue;
            if (!Serializable.class.isAssignableFrom(c)) continue;
            RuntimeProxyCreation.register(c);
            System.out.println("registering proxy " + c.getName());
        }
    }

    public Set<Class<?>> collectClass(String... packages) {
        return collectClass(null, packages);
    }

    public Set<Class<?>> collectClass(Collection<String> packages) {
        return collectClass(null, packages.toArray(new String[0]));
    }

    public Set<Class<?>> collectClass(Predicate<Class<?>> predicate, String... packages) {
        Set<Class<?>> classes = new HashSet<>();
        for (String basePackage : packages) {
            try {
                Set<String> classNames = findClassNames(basePackage);
                for (String className : classNames) {
                    // 过滤掉Spring相关的生成类
                    // 详见：org.springframework.aot.generate.ClassNameGenerator
                    if (className.contains("__")) continue;

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
                e.printStackTrace();
            }
        }
        return classes;
    }

    public Set<String> collectClassNames(String... packages) {
        Set<String> classNames = new HashSet<>();
        for (String basePackage : packages) {
            try {
                Set<String> names = findClassNames(basePackage);
                for (String className : names) {
                    // 过滤掉Spring相关的生成类
                    // 详见：org.springframework.aot.generate.ClassNameGenerator
                    if (className.contains("__")) continue;
                    classNames.add(className);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classNames;
    }

    public Set<Class<?>> findSpringBootApplicationClasses() throws IOException {
        return findClasses(c -> Arrays.stream(c.getAnnotations()).anyMatch(annotation -> annotation.annotationType().getName().equals("org.springframework.boot.autoconfigure.SpringBootApplication")));
    }

    public Set<Class<?>> findClasses(Predicate<Class<?>> predicate) throws IOException {
        Set<Class<?>> result = new HashSet<>();
        Set<String> allClassNames = findClassNames();

        for (String className : allClassNames) {
            try {
                Class<?> clazz = Class.forName(className, false, classLoader);
                if (predicate.test(clazz)) {
                    result.add(clazz);
                }
            } catch (ClassNotFoundException | LinkageError e) {
                // 忽略无法加载的类
            }
        }
        return result;
    }

    public void findClassesInDirectory(File directory, String packageName, Set<String> classNames) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String filename = file.getName();
            if (file.isDirectory()) {
                String newPackage = packageName.isEmpty() ? filename : (packageName + "." + filename);
                findClassesInDirectory(file, newPackage, classNames);
            } else if (filename.endsWith(".class")) {
                String fullClassName = filename.substring(0, filename.length() - 6);
                String className = packageName.isEmpty() ? fullClassName : packageName + '.' + fullClassName;
                classNames.add(className);
            }
        }
    }

    public void findResourcesInDirectory(File directory, String parentPath, Set<String> resources) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            String currentPath = parentPath.isEmpty() ? fileName : parentPath + "/" + fileName;

            if (file.isDirectory()) {
                findResourcesInDirectory(file, currentPath, resources);
            } else {
                if (!fileName.endsWith(".class")) {
                    resources.add(currentPath);
                }
            }
        }
    }

    /**
     * 查找指定包下的类
     * @param packageName
     * @return
     * @throws IOException
     */
    public Set<String> findClassNames(String packageName) throws IOException {
        Set<String> classNames = new HashSet<>();
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
                    e.printStackTrace();
                }
            } else if ("jar".equals(protocol)) {
                String jarPath = resource.getFile().substring(5, resource.getFile().indexOf('!'));
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
                    e.printStackTrace();
                }
            }
        }
        return classNames;
    }

    /**
     * 查找根目录的类
     * @return
     * @throws IOException
     */
    public Set<String> findClassNames() throws IOException {
        return findClassNames("");
    }

    /**
     * 查找指定包下的资源
     * @param packageName
     * @return
     * @throws IOException
     */
    public Set<String> findResources(String packageName) throws IOException {
        Set<String> resources = new HashSet<>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> roots = classLoader.getResources(path);
        while (roots.hasMoreElements()) {
            URL root = roots.nextElement();
            String protocol = root.getProtocol();

            if ("file".equals(protocol)) {
                try {
                    String decodedPath = URLDecoder.decode(root.getFile(), StandardCharsets.UTF_8);
                    File rootDir = new File(decodedPath);

                    if (rootDir.exists() && rootDir.isDirectory()) {
                        findResourcesInDirectory(rootDir, "", resources);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("jar".equals(protocol)) {
                String jarPath = root.getFile().substring(5, root.getFile().indexOf('!'));
                try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (!entryName.endsWith(".class") && !entry.isDirectory()) {
                            resources.add(entryName);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resources;
    }

    /**
     * 查找根目录的资源
     * @return
     * @throws IOException
     */
    public Set<String> findResources() throws IOException {
        return findResources("");
    }

    /**
     * 获取启动类
     * @return
     * @throws IOException
     */
    public Set<Class<?>> findMainClasses() throws IOException {
        return findClasses(c -> Arrays.stream(c.getMethods()).anyMatch(m -> m.getName().equals("main") && Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers())));
    }


    /**
     * 获取启动类所在包名
     * @return
     * @throws IOException
     */
    public Set<String> findMainPackages() throws IOException {
        return findMainClasses().stream().map(Class::getPackageName).collect(Collectors.toSet());
    }

}
