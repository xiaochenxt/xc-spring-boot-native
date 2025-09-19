package io.github.xiaochenxt.aot;

import io.github.xiaochenxt.aot.utils.FeatureUtils;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.util.Arrays;

/**
 * jetty websocket的必要注册
 * @author xiaochen
 * @since 2025/9/19
 */
class JettyWebSocketRequiredRegister {

    static final JettyWebSocketRequiredRegister INSTANCE = new JettyWebSocketRequiredRegister();

    void register(FeatureUtils featureUtils) throws NoSuchMethodException {
        Class<?> byteBufferDecoderClass = featureUtils.loadClass("org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.ByteBufferDecoder");
        if (byteBufferDecoderClass != null) RuntimeReflection.register(byteBufferDecoderClass.getConstructor());
        Class<?> stringDecoderClass = featureUtils.loadClass("org.eclipse.jetty.ee10.websocket.jakarta.common.decoders.StringDecoder");
        if (stringDecoderClass != null) featureUtils.registerReflection(stringDecoderClass.getConstructor());
        Class<?> decodedBinaryMsgSinkClass = featureUtils.loadClass("org.eclipse.jetty.ee10.websocket.jakarta.common.messages.DecodedBinaryMessageSink");
        if (decodedBinaryMsgSinkClass != null) {
            featureUtils.registerReflection(decodedBinaryMsgSinkClass.getConstructors());
            Arrays.stream(decodedBinaryMsgSinkClass.getDeclaredMethods()).filter(method -> method.getName().equals("onWholeMessage") || method.getName().equals("onMessage")).findFirst().ifPresent(featureUtils::registerReflection);
        }
        Class<?> decodedTextMsgSinkClass = featureUtils.loadClass("org.eclipse.jetty.ee10.websocket.jakarta.common.messages.DecodedTextMessageSink");
        if (decodedTextMsgSinkClass != null) {
            featureUtils.registerReflection(decodedTextMsgSinkClass.getConstructors());
            Arrays.stream(decodedTextMsgSinkClass.getDeclaredMethods()).filter(method -> method.getName().equals("onMessage")).findFirst().ifPresent(featureUtils::registerReflection);
        }
        Class<?> byteBufferMsgSinkClass = featureUtils.loadClass("org.eclipse.jetty.websocket.common.internal.ByteBufferMessageSink");
        if (byteBufferMsgSinkClass != null) featureUtils.registerReflection(byteBufferMsgSinkClass.getConstructors());
        Class<?> stringMsgSinkClass = featureUtils.loadClass("org.eclipse.jetty.websocket.core.messages.StringMessageSink");
        if (stringMsgSinkClass != null) featureUtils.registerReflection(stringMsgSinkClass.getConstructors());
        Class<?> jettyWsHandlerAdapterClass = featureUtils.loadClass("org.springframework.web.socket.adapter.jetty.JettyWebSocketHandlerAdapter");
        if (jettyWsHandlerAdapterClass != null) {
            featureUtils.registerReflection(jettyWsHandlerAdapterClass.getDeclaredMethods());
        }
    }

}
