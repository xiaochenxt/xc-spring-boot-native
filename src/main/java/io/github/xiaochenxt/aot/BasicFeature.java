package io.github.xiaochenxt.aot;

import io.github.xiaochenxt.aot.utils.FeatureUtils;
import org.graalvm.nativeimage.RuntimeOptions;
import org.graalvm.nativeimage.hosted.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 基本注册
 * @author xiaochen
 * @since 2025/8/20
 */
class BasicFeature implements Feature {

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        RuntimeOptions.listDescriptors().forEach(desc -> {
           System.out.println(desc);
        });
        FeatureUtils featureUtils = new FeatureUtils(access.getApplicationClassLoader());
        caffeine(featureUtils);
        lettuce(featureUtils);
        font(featureUtils, access);
        aliyuncs(featureUtils);
        captcha(featureUtils);
        phonenumbers(featureUtils);
        serializedLambda(featureUtils, access);
    }

    /**
     * 要解决windows环境下的字体问题，需要有java.home，java.home所在文件夹下有lib文件夹，
     * lib文件夹中要有fontconfig.bfc、fontconfig.properties.src、psfont.properties.ja、psfontj2d.properties
     */
    private void font(FeatureUtils featureUtils, BeforeAnalysisAccess access) {
        access.registerReachabilityHandler(duringAnalysisAccess -> {
            try {
                FontRequiredRegister.INSTANCE.register(new FeatureUtils(duringAnalysisAccess.getApplicationClassLoader()));
            } catch (NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
                System.out.println("字体注册异常，可能导致字体相关功能无法使用");
                e.printStackTrace();
            }
            if (Runtime.version().feature() >= 19) {
                Class<?> fontUtilitiesClass = featureUtils.loadClass("sun.font.FontUtilities");
                if (fontUtilitiesClass != null) {
                    RuntimeJNIAccess.register(fontUtilitiesClass);
                    RuntimeJNIAccess.register(fontUtilitiesClass.getDeclaredFields());
                    RuntimeJNIAccess.register(fontUtilitiesClass.getDeclaredMethods());
                    RuntimeJNIAccess.register(fontUtilitiesClass.getDeclaredConstructors());
                }
            }
            FeatureUtils fe = new FeatureUtils(duringAnalysisAccess.getApplicationClassLoader());
            fe.registerJniIfPresent("sun.awt.windows.WComponentPeer","sun.awt.windows.WDesktopPeer",
                    "sun.awt.windows.WObjectPeer","sun.awt.windows.WToolkit","sun.java2d.windows.WindowsFlags");
            fe.registerJniIfPresent("java.awt.Toolkit","java.awt.Insets","java.awt.FontMetrics","java.awt.Font",
                    "sun.awt.image.SunVolatileImage","sun.awt.image.VolatileSurfaceManager","java.awt.Component",
                    "java.awt.desktop.UserSessionEvent$Reason","sun.awt.Win32GraphicsEnvironment");

            String javaHome = System.getProperty("java.home");
            URL url = fe.classLoader().getResource("");
            if (url != null) {
                String path = url.getPath();
                String targetPath = new File(path).getParentFile().getPath();
                File f = new File(targetPath + File.separator + "lib");
                if (f.mkdirs()) {
                    String libPath = javaHome + File.separator + "lib" + File.separator;
                    File fontconfig = new File(libPath + "fontconfig.bfc");
                    File fontPropertiiesSrc = new File(libPath + "fontconfig.properties.src");
                    File psfontPropertiesJa = new File(libPath + "psfont.properties.ja");
                    File psfontj2dProperties = new File(libPath + "psfontj2d.properties");
                    String targetLibPath = targetPath + File.separator + "lib" + File.separator;
                    if (fontconfig.exists()) {
                        try {
                            Files.copy(fontconfig.toPath(), Path.of(targetLibPath + "fontconfig.bfc"));
                        } catch (IOException ignored) {}
                    }
                    if (fontPropertiiesSrc.exists()) {
                        try {
                            Files.copy(fontPropertiiesSrc.toPath(), Path.of(targetLibPath + "fontconfig.properties.src"));
                        } catch (IOException ignored) {}
                    }
                    if (psfontPropertiesJa.exists()) {
                        try {
                            Files.copy(psfontPropertiesJa.toPath(), Path.of(targetLibPath + "psfont.properties.ja"));
                        } catch (IOException ignored) {}
                    }
                    if (psfontj2dProperties.exists()) {
                        try {
                            Files.copy(psfontj2dProperties.toPath(), Path.of(targetLibPath + "psfontj2d.properties"));
                        } catch (IOException ignored) {}
                    }
                }
            }
            // 需将运行时的java.home设置为当前目录
            RuntimeSystemProperties.register("java.home", "./");
            System.out.println("字体依赖lib文件夹，需要带上它一起打包");
        }, Font.class);
    }

    /**
     * caffine的基本反射注册，正常情况下足够了
     * @param featureUtils
     */
    private void caffeine(FeatureUtils featureUtils) {
        featureUtils.registerReflectionDeclaredConstructorsIfPresent(
                "com.github.benmanes.caffeine.cache.PD","com.github.benmanes.caffeine.cache.PDA","com.github.benmanes.caffeine.cache.PDAMS",
                "com.github.benmanes.caffeine.cache.PDW", "com.github.benmanes.caffeine.cache.PDWMS","com.github.benmanes.caffeine.cache.PS",
                "com.github.benmanes.caffeine.cache.PSA","com.github.benmanes.caffeine.cache.PSAMS","com.github.benmanes.caffeine.cache.PSW",
                "com.github.benmanes.caffeine.cache.PSWMS","com.github.benmanes.caffeine.cache.SIMSA","com.github.benmanes.caffeine.cache.SIMSW",
                "com.github.benmanes.caffeine.cache.SSMSA","com.github.benmanes.caffeine.cache.SSMSW");
    }

    private void lettuce(FeatureUtils featureUtils) {
        if (featureUtils.isPresent("io.lettuce.core.RedisClient")) RuntimeSystemProperties.register("io.lettuce.core.jfr", "false");
    }

    private void aliyuncs(FeatureUtils featureUtils) {
        if (featureUtils.isPresent("com.aliyuncs.http.HttpClientFactory")) {
            try {
                Class<?> apacheHttpClient = featureUtils.classLoader().loadClass("com.aliyuncs.http.clients.ApacheHttpClient");
                RuntimeReflection.register(apacheHttpClient);
                featureUtils.registerResource(apacheHttpClient,"endpoints.json");
                Class<?> assumeRoleResponse = featureUtils.loadClass("com.aliyuncs.auth.sts.AssumeRoleResponse");
                if (assumeRoleResponse != null) {
                    featureUtils.registerReflectionBasic(assumeRoleResponse);
                    RuntimeReflection.registerForReflectiveInstantiation(featureUtils.classLoader().loadClass("com.aliyuncs.auth.sts.AssumeRoleResponse"));
                    featureUtils.registerReflectionBasic(assumeRoleResponse.getClasses());
                }
            } catch (ClassNotFoundException ignored) {}
        }
    }

    private void captcha(FeatureUtils featureUtils) {
        Class<?> captcha = featureUtils.loadClass("com.wf.captcha.base.Captcha");
        if (captcha != null) {
            // 仅添加第一个字体，需要其他的自行添加
            featureUtils.registerResource(captcha,"epilog.ttf");
        }
    }

    private void phonenumbers(FeatureUtils featureUtils) {
        Class<?> phoneNumberUtil =  featureUtils.loadClass("com.google.i18n.phonenumbers.PhoneNumberUtil");
        if (phoneNumberUtil != null) {
            // 这是添加全部
            // featureUtils.registerResource(phoneNumberUtil, "com/google/i18n/phonenumbers/data/*");
            // 这里仅添加中国大陆、中国台湾、中国香港、中国澳门、俄罗斯、美国、韩国的手机号元数据，需要其他的自行添加
            featureUtils.registerResource(phoneNumberUtil,"com/google/i18n/phonenumbers/data/PhoneNumberMetadataProto_CN",
                    "com/google/i18n/phonenumbers/data/PhoneNumberMetadataProto_TW",
                    "com/google/i18n/phonenumbers/data/PhoneNumberMetadataProto_HK",
                    "com/google/i18n/phonenumbers/data/PhoneNumberMetadataProto_MO",
                    "com/google/i18n/phonenumbers/data/PhoneNumberMetadataProto_RU",
                    "com/google/i18n/phonenumbers/data/PhoneNumberMetadataProto_US",
                    "com/google/i18n/phonenumbers/data/PhoneNumberMetadataProto_KR");
        }
    }

    private void serializedLambda(FeatureUtils featureUtils, BeforeAnalysisAccess access) {
        if (featureUtils.isPresent("com.baomidou.mybatisplus.core.toolkit.support.SFunction")) {
            try {
                Class<?> c = featureUtils.classLoader().loadClass("com.baomidou.mybatisplus.core.toolkit.support.SFunction");
                access.registerReachabilityHandler(duringAnalysisAccess -> {
                    RuntimeSerialization.register(java.lang.invoke.SerializedLambda.class);
                    try {
                        List<Class<?>> classes = featureUtils.collectClass(featureUtils.findSpringBootApplicationClasses().getFirst().getPackageName());
                        // 可能存在警告，直接忽略，例如：Warning: Could not register class xxx for lambda serialization as it does not capture any serializable lambda.
                        classes.forEach(RuntimeSerialization::registerLambdaCapturingClass);
                    } catch (IOException ignored) {}
                }, c);
            } catch (ClassNotFoundException ignored) {}
        }
    }

}
