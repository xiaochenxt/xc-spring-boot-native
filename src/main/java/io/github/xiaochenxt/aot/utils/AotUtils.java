package io.github.xiaochenxt.aot.utils;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

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
 * 基于spring的aot处理操作简化工具
 * @author xiaochen
 * @since 2025/5/27
 */
public class AotUtils {

    private final RuntimeHints hints;

    private final ClassLoader classLoader;

    public static AotUtils newInstance(RuntimeHints hints, ClassLoader classLoader) {
        return new AotUtils(hints, classLoader);
    }

    public AotUtils(RuntimeHints hints, ClassLoader classLoader) {
        this.hints = hints;
        this.classLoader = classLoader;
    }

    public RuntimeHints hints() {
        return hints;
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

    public static final MemberCategory[] defaultMemberCategory = new MemberCategory[]{MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.DECLARED_CLASSES, MemberCategory.UNSAFE_ALLOCATED};

    public void registerPattern(String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(resource);
            System.out.println("include resource " + resource);
        }
    }

    public void registerPattern(TypeReference typeReference, String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(builder -> builder.includes(typeReference, resource));
        }
    }

    public void registerPatternIfPresent(String location, String... resources) {
        for (String resource : resources) {
            hints.resources().registerPatternIfPresent(classLoader, location, builder -> builder.includes(resource));
        }
    }

    public void excludePattern(String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(builder -> builder.excludes(resource));
            System.out.println("exclude resource " + resource);
        }
    }

    public void excludePattern(TypeReference typeReference, String... resources) {
        for (String resource : resources) {
            hints.resources().registerPattern(builder -> builder.excludes(typeReference, resource));
        }
    }

    public void registerReflectionIfPresent(MemberCategory[] memberCategories, String... classes) {
        for (String clazz : classes) {
            try {
                if (isPresent(clazz)) {
                    hints.reflection().registerType(classLoader.loadClass(clazz), memberCategories);
                    System.out.println("registering reflect " + clazz);
                }
            } catch (LinkageError | ClassNotFoundException ignored) {}
        }
    }

    public void registerReflectionIfPresent(String... classes) {
        registerReflectionIfPresent(defaultMemberCategory, classes);
    }

    public void registerReflection(MemberCategory[] memberCategories, List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            try {
                hints.reflection().registerType(clazz, memberCategories);
                System.out.println("registering reflect " + clazz.getName());
            } catch (LinkageError e) {
                System.err.println("Unable to load class: " + clazz.getName() + ", error: " + e.getMessage());
            }
        }
    }

    public void registerReflection(MemberCategory[] memberCategories, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            try {
                hints.reflection().registerType(clazz, memberCategories);
                System.out.println("registering reflect " + clazz.getName());
            } catch (LinkageError e) {
                System.err.println("Unable to load class: " + clazz.getName() + ", error: " + e.getMessage());
            }
        }
    }

    public void registerReflection(Class<?>... classes) {
        registerReflection(defaultMemberCategory, classes);
    }

    public void registerReflection(List<Class<?>> classes) {
        registerReflection(defaultMemberCategory, classes);
    }

    public void registerReflection(List<Class<?>> classes, MemberCategory memberCategory) {
        registerReflection(new MemberCategory[]{memberCategory}, classes);
    }

    public void registerJni(MemberCategory[] memberCategories, List<Class<?>> classes) {
        for (Class<?> c : classes) {
            hints.jni().registerType(c, memberCategories);
            System.out.println("registering jni " + c.getName());
        }
    }

    public void registerJni(MemberCategory[] memberCategories, Class<?>... classes) {
        for (Class<?> c : classes) {
            hints.jni().registerType(c, memberCategories);
            System.out.println("registering jni " + c.getName());
        }
    }

    public void registerJni(Class<?>... classes) {
        registerJni(defaultMemberCategory, classes);
    }

    public void registerJni(List<Class<?>> classes) {
        registerJni(defaultMemberCategory, classes);
    }

    public void registerJniIfPresent(String... classes) {
       registerJniIfPresent(defaultMemberCategory, classes);
    }

    public void registerJniIfPresent(MemberCategory[] memberCategory, String... classes) {
        for (String c : classes) {
            hints.jni().registerTypeIfPresent(classLoader, c, memberCategory);
            System.out.println("registering jni " + c);
        }
    }

    @SafeVarargs
    public final void registerSerializable(Class<? extends Serializable>... classes) {
        for (Class<? extends Serializable> c : classes) {
            hints.serialization().registerType(c);
            System.out.println("registering serializable " + c.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public void registerSerializable(List<Class<?>> classes) {
        for (Class<?> c : classes) {
            if (!Serializable.class.isAssignableFrom(c)) continue;
            hints.serialization().registerType((Class<? extends Serializable>) c);
            System.out.println("registering serializable " + c.getName());
        }
    }

    @SuppressWarnings("unchecked")
    public void registerSerializableIfPresent(String... classes) {
        for (String c : classes) {
            try {
                Class<?> clazz = classLoader.loadClass(c);
                if (!Serializable.class.isAssignableFrom(clazz)) continue;
                hints.serialization().registerType((Class<? extends Serializable>) clazz);
                System.out.println("registering serializable " + c);
            } catch (ClassNotFoundException ignored) {}
        }
    }

    public List<Class<?>> collectClass(String... packages) {
        return collectClass(null , packages);
    }

    public List<Class<?>> collectClass(Predicate<Class<?>> predicate, String... packages) {
        List<Class<?>> classes = new ArrayList<>();
        for (String basePackage : packages) {
            try {
                // 扫描该包及其子包下的所有类
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";
                ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
                MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        // 过滤掉Spring相关的生成类
                        // 详见：org.springframework.aot.generate.ClassNameGenerator
                        if (className.contains("__")) continue;
                        try {
                            Class<?> clazz = ClassUtils.forName(className, classLoader);
                            if (predicate == null || predicate.test(clazz)) {
                                classes.add(clazz);
                            }
                        } catch (ClassNotFoundException | LinkageError ignored) {

                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error scanning classes", e);
            }
        }
        return classes;
    }

    public List<String> collectClassNames(String... packages) {
        List<String> classes = new ArrayList<>();
        for (String basePackage : packages) {
            try {
                // 扫描该包及其子包下的所有类
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        ClassUtils.convertClassNameToResourcePath(basePackage) + "/**/*.class";
                ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver(classLoader);
                MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        // 过滤掉Spring相关的生成类
                        // 详见：org.springframework.aot.generate.ClassNameGenerator
                        if (className.contains("__")) continue;
                        classes.add(className);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error scanning classes", e);
            }
        }
        return classes;
    }

    public List<Class<?>> findSpringBootApplicationClasses() throws IOException {
        List<Class<?>> result = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resolver);
        // 扫描所有类文件
        Resource[] resources = resolver.getResources("classpath*:/**/*.class");
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                // 跳过 JDK 类（判断是否以 java. 或 javax. 开头）
//                if (className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("jdk.") || className.startsWith("com.sun.") || className.startsWith("sun.")) {
//                    continue;
//                }
                // 检查是否有 @SpringBootApplication 注解
                if (reader.getAnnotationMetadata().hasAnnotation(SpringBootApplication.class.getName())) {
                    try {
                        Class<?> clazz = Class.forName(className, false, classLoader);
                        result.add(clazz);
                    } catch (ClassNotFoundException ignored) {

                    }
                }
            }
        }
        return result;
    }

    public List<Class<?>> findClasses(Predicate<Class<?>> predicate) throws IOException {
        List<Class<?>> result = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resolver);
        // 扫描所有类文件
        Resource[] resources = resolver.getResources("classpath*:/**/*.class");
        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader reader = readerFactory.getMetadataReader(resource);
                String className = reader.getClassMetadata().getClassName();
                // 跳过 JDK 类（判断是否以 java. 或 javax. 开头）
//                if (className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("jdk.") || className.startsWith("com.sun.") || className.startsWith("sun.")) {
//                    continue;
//                }
                try {
                    Class<?> clazz = Class.forName(className, false, classLoader);
                    if (predicate.test(clazz)) {
                        result.add(clazz);
                    }
                } catch (ClassNotFoundException ignored) {

                }
            }
        }
        return result;
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

}
