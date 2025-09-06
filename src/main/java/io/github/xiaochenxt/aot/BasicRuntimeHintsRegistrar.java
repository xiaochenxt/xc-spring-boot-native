package io.github.xiaochenxt.aot;

import io.github.xiaochenxt.aot.utils.AotUtils;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

/**
 * 基本注册
 * @author xiaochen
 * @since 2025/5/23
 */
public class BasicRuntimeHintsRegistrar implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        AotUtils aotUtils = new AotUtils(hints, classLoader);
        staticResource(aotUtils);
    }

    /**
     * 静态资源注册
     * @param aotUtils
     */
    private void staticResource(AotUtils aotUtils) {
        aotUtils.registerPatternIfPresent("static","static/*");
    }

}
