package io.github.xiaochenxt.aot;

import io.github.xiaochenxt.aot.utils.AotUtils;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import java.io.IOException;
import java.util.Set;

/**
 * 将springboot项目中不含第三方库的所有类注册反射调用，为所有实现了Serializable的注册序列化，可解决90%的运行时错误问题
 * @author xiaochen
 * @since 2025/5/23
 */
public class AllRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        try {
            AotUtils aotUtils = new AotUtils(hints, classLoader);
            Set<Class<?>> classes = aotUtils.collectClass(aotUtils.findSpringBootApplicationClasses().stream().map(Class::getPackageName).toArray(String[]::new));
            aotUtils.registerReflection(classes);
            aotUtils.registerPattern("*.properties");
            aotUtils.registerSerializable(classes);
        } catch (IOException ignored) {}
    }

}
