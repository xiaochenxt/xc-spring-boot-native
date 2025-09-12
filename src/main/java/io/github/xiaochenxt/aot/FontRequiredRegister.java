package io.github.xiaochenxt.aot;

import io.github.xiaochenxt.aot.utils.FeatureUtils;
import org.graalvm.nativeimage.hosted.RuntimeJNIAccess;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Locale;

/**
 * 字体的必要注册
 * @author xiaochen
 * @since 2025/8/25
 */
class FontRequiredRegister {

    static final FontRequiredRegister INSTANCE = new FontRequiredRegister();

    void register(FeatureUtils featureUtils) throws NoSuchMethodException, NoSuchFieldException, ClassNotFoundException {
        // ============================ 反射注册 (RuntimeReflection) ============================
        featureUtils.registerReflectionIfPresent("javax.imageio.spi.ImageReaderSpi","javax.imageio.spi.ImageWriterSpi");

        Class<?> dMarlinClass = featureUtils.loadClass("sun.java2d.marlin.DMarlinRenderingEngine");
        if (dMarlinClass != null) {
            RuntimeReflection.register(dMarlinClass);
            Constructor<?> dMarlinCtor = dMarlinClass.getConstructor();
            RuntimeReflection.register(dMarlinCtor);
        }

        Class<?> nativePrngClass = featureUtils.loadClass("sun.security.provider.NativePRNG");
        if (nativePrngClass != null) {
            RuntimeReflection.register(nativePrngClass);
            Class<?> secureRandomParameters = featureUtils.loadClass("java.security.SecureRandomParameters");
            if (secureRandomParameters != null) {
                try {
                    Constructor<?> nativePrngCtor = nativePrngClass.getConstructor(secureRandomParameters);
                    RuntimeReflection.register(nativePrngCtor);
                } catch (Exception ignored) {}
            }
        }

        Class<?> shaClass = featureUtils.loadClass("sun.security.provider.SHA");
        if (shaClass != null) {
            RuntimeReflection.register(shaClass);
            Constructor<?> shaCtor = shaClass.getConstructor();
            RuntimeReflection.register(shaCtor);
        }

        // ============================ JNI注册 (RuntimeJNIAccess) ============================
        Class<?> graphicsPrimitiveArrayClass = featureUtils.loadClass("sun.java2d.loops.GraphicsPrimitive");
        if (graphicsPrimitiveArrayClass != null) {
            RuntimeJNIAccess.register(graphicsPrimitiveArrayClass);
        }

        Class<?> alphaCompositeClass = featureUtils.loadClass("java.awt.AlphaComposite");
        if (alphaCompositeClass != null) {
            RuntimeJNIAccess.register(alphaCompositeClass);
            RuntimeJNIAccess.register(alphaCompositeClass.getDeclaredField("extraAlpha"));
            RuntimeJNIAccess.register(alphaCompositeClass.getDeclaredField("rule"));
        }

        Class<?> colorClass = featureUtils.loadClass("java.awt.Color");
        if (colorClass != null) {
            RuntimeJNIAccess.register(colorClass);
            RuntimeJNIAccess.register(colorClass.getMethod("getRGB"));
        }

        Class<?> geClass = featureUtils.loadClass("java.awt.GraphicsEnvironment");
        if (geClass != null) {
            RuntimeJNIAccess.register(geClass);
            RuntimeJNIAccess.register(geClass.getMethod("getLocalGraphicsEnvironment"));
            RuntimeJNIAccess.register(geClass.getMethod("isHeadless"));
        }

        Class<?> rectangleClass = featureUtils.loadClass("java.awt.Rectangle");
        if (rectangleClass != null) {
            RuntimeJNIAccess.register(rectangleClass);
            RuntimeJNIAccess.register(rectangleClass.getConstructor(int.class, int.class, int.class, int.class));
        }

        Class<?> atClass = featureUtils.loadClass("java.awt.geom.AffineTransform");
        if (atClass != null) {
            RuntimeJNIAccess.register(atClass);
            RuntimeJNIAccess.register(atClass.getDeclaredField("m00"));
            RuntimeJNIAccess.register(atClass.getDeclaredField("m01"));
            RuntimeJNIAccess.register(atClass.getDeclaredField("m02"));
            RuntimeJNIAccess.register(atClass.getDeclaredField("m10"));
            RuntimeJNIAccess.register(atClass.getDeclaredField("m11"));
            RuntimeJNIAccess.register(atClass.getDeclaredField("m12"));
        }

        Class<?> gpClass = featureUtils.loadClass("java.awt.geom.GeneralPath");
        if (gpClass != null) {
            RuntimeJNIAccess.register(gpClass);
            RuntimeJNIAccess.register(gpClass.getConstructor());
            RuntimeJNIAccess.register(gpClass.getDeclaredConstructor(int.class, byte[].class, int.class, float[].class, int.class));
        }

        Class<?> path2DClass = Path2D.class;
        RuntimeJNIAccess.register(path2DClass);
        RuntimeJNIAccess.register(path2DClass.getDeclaredField("numTypes"));
        RuntimeJNIAccess.register(path2DClass.getDeclaredField("pointTypes"));
        RuntimeJNIAccess.register(path2DClass.getDeclaredField("windingRule"));

        Class<?> path2DFloatClass = featureUtils.loadClass("java.awt.geom.Path2D$Float");
        if (path2DFloatClass != null) {
            RuntimeJNIAccess.register(path2DFloatClass);
            RuntimeJNIAccess.register(path2DFloatClass.getDeclaredField("floatCoords"));
        }

        Class<?> p2dFloatClass = Point2D.Float.class;
        RuntimeJNIAccess.register(p2dFloatClass);
        RuntimeJNIAccess.register(p2dFloatClass.getDeclaredField("x"));
        RuntimeJNIAccess.register(p2dFloatClass.getDeclaredField("y"));
        RuntimeJNIAccess.register(p2dFloatClass.getConstructor(float.class, float.class));



        Class<?> r2dFloatClass = featureUtils.loadClass("java.awt.geom.Rectangle2D$Float");
        if (r2dFloatClass != null) {
            RuntimeJNIAccess.register(r2dFloatClass);
            RuntimeJNIAccess.register(r2dFloatClass.getDeclaredField("height"));
            RuntimeJNIAccess.register(r2dFloatClass.getDeclaredField("width"));
            RuntimeJNIAccess.register(r2dFloatClass.getDeclaredField("x"));
            RuntimeJNIAccess.register(r2dFloatClass.getDeclaredField("y"));
            RuntimeJNIAccess.register(r2dFloatClass.getConstructor());
            RuntimeJNIAccess.register(r2dFloatClass.getConstructor(float.class, float.class, float.class, float.class));
        }

        Class<BufferedImage> biClass = BufferedImage.class;
        RuntimeJNIAccess.register(biClass);
        RuntimeJNIAccess.register(biClass.getDeclaredField("colorModel"));
        RuntimeJNIAccess.register(biClass.getDeclaredField("imageType"));
        RuntimeJNIAccess.register(biClass.getDeclaredField("raster"));
        RuntimeJNIAccess.register(biClass.getMethod("getRGB", int.class, int.class, int.class, int.class, int[].class, int.class, int.class));
        RuntimeJNIAccess.register(biClass.getMethod("setRGB", int.class, int.class, int.class, int.class, int[].class, int.class, int.class));

        Class<ColorModel> cmClass = ColorModel.class;
        RuntimeJNIAccess.register(cmClass);
        RuntimeJNIAccess.register(cmClass.getDeclaredField("colorSpace"));
        RuntimeJNIAccess.register(cmClass.getDeclaredField("colorSpaceType"));
        RuntimeJNIAccess.register(cmClass.getDeclaredField("isAlphaPremultiplied"));
        RuntimeJNIAccess.register(cmClass.getDeclaredField("is_sRGB"));
        RuntimeJNIAccess.register(cmClass.getDeclaredField("nBits"));
        RuntimeJNIAccess.register(cmClass.getDeclaredField("numComponents"));
        RuntimeJNIAccess.register(cmClass.getDeclaredField("supportsAlpha"));
        RuntimeJNIAccess.register(cmClass.getDeclaredField("transparency"));
        RuntimeJNIAccess.register(cmClass.getMethod("getRGBdefault"));

        Class<IndexColorModel> icmClass = IndexColorModel.class;
        RuntimeJNIAccess.register(icmClass);
        RuntimeJNIAccess.register(icmClass.getDeclaredField("allgrayopaque"));
        RuntimeJNIAccess.register(icmClass.getDeclaredField("colorData"));
        RuntimeJNIAccess.register(icmClass.getDeclaredField("map_size"));
        RuntimeJNIAccess.register(icmClass.getDeclaredField("rgb"));
        RuntimeJNIAccess.register(icmClass.getDeclaredField("transparent_index"));

        Class<Raster> rasterClass = Raster.class;
        RuntimeJNIAccess.register(rasterClass);
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("dataBuffer"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("height"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("minX"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("minY"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("numBands"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("numDataElements"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("sampleModel"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("sampleModelTranslateX"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("sampleModelTranslateY"));
        RuntimeJNIAccess.register(rasterClass.getDeclaredField("width"));

        Class<SampleModel> smClass = SampleModel.class;
        RuntimeJNIAccess.register(smClass);
        RuntimeJNIAccess.register(smClass.getDeclaredField("height"));
        RuntimeJNIAccess.register(smClass.getDeclaredField("width"));
        RuntimeJNIAccess.register(smClass.getMethod("getPixels", int.class, int.class, int.class, int.class, int[].class, DataBuffer.class));
        RuntimeJNIAccess.register(smClass.getMethod("setPixels", int.class, int.class, int.class, int.class, int[].class, DataBuffer.class));

        Class<SinglePixelPackedSampleModel> sppsmClass = SinglePixelPackedSampleModel.class;
        RuntimeJNIAccess.register(sppsmClass);
        RuntimeJNIAccess.register(sppsmClass.getDeclaredField("bitMasks"));
        RuntimeJNIAccess.register(sppsmClass.getDeclaredField("bitOffsets"));
        RuntimeJNIAccess.register(sppsmClass.getDeclaredField("bitSizes"));
        RuntimeJNIAccess.register(sppsmClass.getDeclaredField("maxBitSize"));

        Class<Boolean> booleanClass = Boolean.class;
        RuntimeJNIAccess.register(booleanClass);
        RuntimeJNIAccess.register(booleanClass.getMethod("getBoolean", String.class));

        Class<System> systemClass = System.class;
        RuntimeJNIAccess.register(systemClass);
        RuntimeJNIAccess.register(systemClass.getMethod("load", String.class));

        Class<?> sunHintsClass = featureUtils.classLoader().loadClass("sun.awt.SunHints");
        RuntimeJNIAccess.register(sunHintsClass);
        RuntimeJNIAccess.register(sunHintsClass.getDeclaredField("INTVAL_STROKE_PURE"));

        Class<?> sunToolkitClass = featureUtils.loadClass("sun.awt.SunToolkit");
        if (sunToolkitClass != null) {
            RuntimeJNIAccess.register(sunToolkitClass);
            RuntimeJNIAccess.register(sunToolkitClass.getMethod("awtLock"));
            RuntimeJNIAccess.register(sunToolkitClass.getMethod("awtLockNotify"));
            RuntimeJNIAccess.register(sunToolkitClass.getMethod("awtLockNotifyAll"));
            RuntimeJNIAccess.register(sunToolkitClass.getMethod("awtLockWait", long.class));
            RuntimeJNIAccess.register(sunToolkitClass.getMethod("awtUnlock"));
        }

        Class<?> xErrorHandlerUtilClass = featureUtils.loadClass("sun.awt.X11.XErrorHandlerUtil");
        if (xErrorHandlerUtilClass != null) {
            RuntimeJNIAccess.register(xErrorHandlerUtilClass);
            RuntimeJNIAccess.register(xErrorHandlerUtilClass.getDeclaredMethod("init", long.class));
        }

        Class<?> x11GraphicsConfigClass = featureUtils.loadClass("sun.awt.X11GraphicsConfig");
        if (x11GraphicsConfigClass != null) {
            RuntimeJNIAccess.register(x11GraphicsConfigClass);
            RuntimeJNIAccess.register(x11GraphicsConfigClass.getDeclaredField("aData"));
            RuntimeJNIAccess.register(x11GraphicsConfigClass.getDeclaredField("bitsPerPixel"));
        }

        Class<?> x11GraphicsDeviceClass = featureUtils.loadClass("sun.awt.X11GraphicsDevice");
        if (x11GraphicsDeviceClass != null) {
            RuntimeJNIAccess.register(x11GraphicsDeviceClass);
            RuntimeJNIAccess.register(x11GraphicsDeviceClass.getDeclaredMethod("addDoubleBufferVisual", int.class));
        }

        Class<?> icmColorDataClass = featureUtils.loadClass("sun.awt.image.BufImgSurfaceData$ICMColorData");
        if (icmColorDataClass != null) {
            RuntimeJNIAccess.register(icmColorDataClass.getDeclaredField("pData"));
            RuntimeJNIAccess.register(icmColorDataClass.getDeclaredConstructor(long.class));
        }

        Class<?> integerComponentRasterClass = featureUtils.loadClass("sun.awt.image.IntegerComponentRaster");
        if (integerComponentRasterClass != null) {
            RuntimeJNIAccess.register(integerComponentRasterClass);
            RuntimeJNIAccess.register(integerComponentRasterClass.getDeclaredField("data"));
            RuntimeJNIAccess.register(integerComponentRasterClass.getDeclaredField("dataOffsets"));
            RuntimeJNIAccess.register(integerComponentRasterClass.getDeclaredField("pixelStride"));
            RuntimeJNIAccess.register(integerComponentRasterClass.getDeclaredField("scanlineStride"));
            RuntimeJNIAccess.register(integerComponentRasterClass.getDeclaredField("type"));
        }

        Class<?> charToGlyphMapperClass = featureUtils.loadClass("sun.font.CharToGlyphMapper");
        if (charToGlyphMapperClass != null) {
            RuntimeJNIAccess.register(charToGlyphMapperClass);
            RuntimeJNIAccess.register(charToGlyphMapperClass.getMethod("charToGlyph", int.class));
        }

        Class<?> font2DClass = featureUtils.loadClass("sun.font.Font2D");
        if (font2DClass != null) {
            RuntimeJNIAccess.register(font2DClass);
            RuntimeJNIAccess.register(font2DClass.getMethod("canDisplay", char.class));
            RuntimeJNIAccess.register(font2DClass.getMethod("charToGlyph", int.class));
            RuntimeJNIAccess.register(font2DClass.getMethod("charToVariationGlyph", int.class, int.class));
            RuntimeJNIAccess.register(font2DClass.getDeclaredMethod("getMapper"));
            RuntimeJNIAccess.register(font2DClass.getDeclaredMethod("getTableBytes", int.class));
        }

        Class<?> fontStrikeClass = featureUtils.loadClass("sun.font.FontStrike");
        if (fontStrikeClass != null) {
            RuntimeJNIAccess.register(fontStrikeClass);
            RuntimeJNIAccess.register(fontStrikeClass.getDeclaredMethod("getGlyphMetrics", int.class));
        }

        Class<?> fontUtilitiesClass = featureUtils.loadClass("sun.font.FontUtilities");
        if (fontUtilitiesClass != null) {
            RuntimeJNIAccess.register(fontUtilitiesClass);
            RuntimeJNIAccess.register(fontUtilitiesClass.getMethod("debugFonts"));
        }

        Class<?> freetypeFontScalerClass = featureUtils.loadClass("sun.font.FreetypeFontScaler");
        if (freetypeFontScalerClass != null) {
            RuntimeJNIAccess.register(freetypeFontScalerClass);
            RuntimeJNIAccess.register(freetypeFontScalerClass.getDeclaredMethod("invalidateScaler"));
        }

        Class<?> glyphListClass = featureUtils.loadClass("sun.font.GlyphList");
        if (glyphListClass != null) {
            RuntimeJNIAccess.register(glyphListClass);
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("gposx"));
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("gposy"));
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("images"));
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("lcdRGBOrder"));
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("lcdSubPixPos"));
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("len"));
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("positions"));
            RuntimeJNIAccess.register(glyphListClass.getDeclaredField("usePositions"));
        }

        Class<?> physicalStrikeClass = featureUtils.loadClass("sun.font.PhysicalStrike");
        if (physicalStrikeClass != null) {
            RuntimeJNIAccess.register(physicalStrikeClass);
            RuntimeJNIAccess.register(physicalStrikeClass.getDeclaredField("pScalerContext"));
            RuntimeJNIAccess.register(physicalStrikeClass.getDeclaredMethod("adjustPoint", Point2D.Float.class));
            RuntimeJNIAccess.register(physicalStrikeClass.getDeclaredMethod("getGlyphPoint", int.class, int.class));
        }

        Class<?> strikeMetricsClass = featureUtils.loadClass("sun.font.StrikeMetrics");
        if (strikeMetricsClass != null) {
            RuntimeJNIAccess.register(strikeMetricsClass);
            RuntimeJNIAccess.register(strikeMetricsClass.getDeclaredConstructor(float.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class, float.class));
        }

        Class<?> trueTypeFontClass = featureUtils.loadClass("sun.font.TrueTypeFont");
        if (trueTypeFontClass != null) {
            RuntimeJNIAccess.register(trueTypeFontClass);
            RuntimeJNIAccess.register(trueTypeFontClass.getDeclaredMethod("readBlock", ByteBuffer.class, int.class, int.class));
            RuntimeJNIAccess.register(trueTypeFontClass.getDeclaredMethod("readBytes", int.class, int.class));
        }

        Class<?> type1FontClass = featureUtils.loadClass("sun.font.Type1Font");
        if (type1FontClass != null) {
            RuntimeJNIAccess.register(type1FontClass);
            RuntimeJNIAccess.register(type1FontClass.getDeclaredMethod("readFile", ByteBuffer.class));
        }

        Class<?> disposerClass = featureUtils.loadClass("sun.java2d.Disposer");
        if (disposerClass != null) {
            RuntimeJNIAccess.register(disposerClass);
            RuntimeJNIAccess.register(disposerClass.getMethod("addRecord", Object.class, long.class, long.class));
        }

        Class<?> invalidPipeExceptionClass = featureUtils.loadClass("sun.java2d.InvalidPipeException");
        if (invalidPipeExceptionClass != null) {
            RuntimeJNIAccess.register(invalidPipeExceptionClass);
        }

        Class<?> nullSurfaceDataClass = featureUtils.loadClass("sun.java2d.NullSurfaceData");
        if (nullSurfaceDataClass != null) {
            RuntimeJNIAccess.register(nullSurfaceDataClass);
        }

        Class<?> sunGraphics2DClass = featureUtils.loadClass("sun.java2d.SunGraphics2D");
        if (sunGraphics2DClass != null) {
            RuntimeJNIAccess.register(sunGraphics2DClass);
            RuntimeJNIAccess.register(sunGraphics2DClass.getDeclaredField("clipRegion"));
            RuntimeJNIAccess.register(sunGraphics2DClass.getDeclaredField("composite"));
            RuntimeJNIAccess.register(sunGraphics2DClass.getDeclaredField("eargb"));
            RuntimeJNIAccess.register(sunGraphics2DClass.getDeclaredField("lcdTextContrast"));
            RuntimeJNIAccess.register(sunGraphics2DClass.getDeclaredField("pixel"));
            RuntimeJNIAccess.register(sunGraphics2DClass.getDeclaredField("strokeHint"));
        }

        Class<?> sunGraphicsEnvironmentClass = featureUtils.loadClass("sun.java2d.SunGraphicsEnvironment");
        if (sunGraphicsEnvironmentClass != null) {
            RuntimeJNIAccess.register(sunGraphicsEnvironmentClass);
            RuntimeJNIAccess.register(sunGraphicsEnvironmentClass.getMethod("isDisplayLocal"));
        }

        Class<?> surfaceDataClass = featureUtils.loadClass("sun.java2d.SurfaceData");
        if (surfaceDataClass != null) {
            RuntimeJNIAccess.register(surfaceDataClass);
            RuntimeJNIAccess.register(surfaceDataClass.getDeclaredField("pData"));
            RuntimeJNIAccess.register(surfaceDataClass.getDeclaredField("valid"));
        }

        Class<?> blitClass = featureUtils.classLoader().loadClass("sun.java2d.loops.Blit");
        RuntimeJNIAccess.register(blitClass);
        Class<?> surfaceTypeClass = featureUtils.classLoader().loadClass("sun.java2d.loops.SurfaceType");
        Class<?> compositeTypeClass = featureUtils.classLoader().loadClass("sun.java2d.loops.CompositeType");
        RuntimeJNIAccess.register(blitClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));
        Class<?> blitBgClass = featureUtils.classLoader().loadClass("sun.java2d.loops.BlitBg");
        RuntimeJNIAccess.register(blitBgClass);
        RuntimeJNIAccess.register(blitBgClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> compositeTypeClass2 = featureUtils.classLoader().loadClass("sun.java2d.loops.CompositeType");
        RuntimeJNIAccess.register(compositeTypeClass2);
        RuntimeJNIAccess.register(compositeTypeClass2.getDeclaredField("AnyAlpha"));
        RuntimeJNIAccess.register(compositeTypeClass2.getDeclaredField("Src"));
        RuntimeJNIAccess.register(compositeTypeClass2.getDeclaredField("SrcNoEa"));
        RuntimeJNIAccess.register(compositeTypeClass2.getDeclaredField("SrcOver"));
        RuntimeJNIAccess.register(compositeTypeClass2.getDeclaredField("SrcOverNoEa"));
        RuntimeJNIAccess.register(compositeTypeClass2.getDeclaredField("Xor"));

        Class<?> drawGlyphListClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawGlyphList");
        RuntimeJNIAccess.register(drawGlyphListClass);
        RuntimeJNIAccess.register(drawGlyphListClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> drawGlyphListAAClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawGlyphListAA");
        RuntimeJNIAccess.register(drawGlyphListAAClass);
        RuntimeJNIAccess.register(drawGlyphListAAClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> drawGlyphListLCDClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawGlyphListLCD");
        RuntimeJNIAccess.register(drawGlyphListLCDClass);
        RuntimeJNIAccess.register(drawGlyphListLCDClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> drawLineClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawLine");
        RuntimeJNIAccess.register(drawLineClass);
        RuntimeJNIAccess.register(drawLineClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> drawParallelogramClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawParallelogram");
        RuntimeJNIAccess.register(drawParallelogramClass);
        RuntimeJNIAccess.register(drawParallelogramClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> drawPathClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawPath");
        RuntimeJNIAccess.register(drawPathClass);
        RuntimeJNIAccess.register(drawPathClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> drawPolygonsClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawPolygons");
        RuntimeJNIAccess.register(drawPolygonsClass);
        RuntimeJNIAccess.register(drawPolygonsClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> drawRectClass = featureUtils.classLoader().loadClass("sun.java2d.loops.DrawRect");
        RuntimeJNIAccess.register(drawRectClass);
        RuntimeJNIAccess.register(drawRectClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> fillParallelogramClass = featureUtils.classLoader().loadClass("sun.java2d.loops.FillParallelogram");
        RuntimeJNIAccess.register(fillParallelogramClass);
        RuntimeJNIAccess.register(fillParallelogramClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> fillPathClass = featureUtils.classLoader().loadClass("sun.java2d.loops.FillPath");
        RuntimeJNIAccess.register(fillPathClass);
        RuntimeJNIAccess.register(fillPathClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> fillRectClass = featureUtils.classLoader().loadClass("sun.java2d.loops.FillRect");
        RuntimeJNIAccess.register(fillRectClass);
        RuntimeJNIAccess.register(fillRectClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> fillSpansClass = featureUtils.classLoader().loadClass("sun.java2d.loops.FillSpans");
        RuntimeJNIAccess.register(fillSpansClass);
        RuntimeJNIAccess.register(fillSpansClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> graphicsPrimitiveClass = featureUtils.classLoader().loadClass("sun.java2d.loops.GraphicsPrimitive");
        RuntimeJNIAccess.register(graphicsPrimitiveClass);
        RuntimeJNIAccess.register(graphicsPrimitiveClass.getDeclaredField("pNativePrim"));

        Class<?> graphicsPrimitiveMgrClass = featureUtils.classLoader().loadClass("sun.java2d.loops.GraphicsPrimitiveMgr");
        RuntimeJNIAccess.register(graphicsPrimitiveMgrClass);
        for (Method method : graphicsPrimitiveMgrClass.getMethods()) {
            if (method.getName().equals("register")) {
                RuntimeJNIAccess.register(method);
                break;
            }
        }

        Class<?> maskBlitClass = featureUtils.classLoader().loadClass("sun.java2d.loops.MaskBlit");
        RuntimeJNIAccess.register(maskBlitClass);
        RuntimeJNIAccess.register(maskBlitClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> maskFillClass = featureUtils.classLoader().loadClass("sun.java2d.loops.MaskFill");
        RuntimeJNIAccess.register(maskFillClass);
        RuntimeJNIAccess.register(maskFillClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> scaledBlitClass = featureUtils.classLoader().loadClass("sun.java2d.loops.ScaledBlit");
        RuntimeJNIAccess.register(scaledBlitClass);
        RuntimeJNIAccess.register(scaledBlitClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> surfaceTypeClass2 = featureUtils.classLoader().loadClass("sun.java2d.loops.SurfaceType");
        RuntimeJNIAccess.register(surfaceTypeClass2);
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Any3Byte"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Any4Byte"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("AnyByte"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("AnyColor"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("AnyInt"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("AnyShort"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("ByteBinary1Bit"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("ByteBinary2Bit"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("ByteBinary4Bit"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("ByteGray"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("ByteIndexed"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("ByteIndexedBm"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("FourByteAbgr"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("FourByteAbgrPre"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Index12Gray"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Index8Gray"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("IntArgb"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("IntArgbBm"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("IntArgbPre"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("IntBgr"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("IntRgb"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("IntRgbx"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("OpaqueColor"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("ThreeByteBgr"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Ushort4444Argb"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Ushort555Rgb"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Ushort555Rgbx"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("Ushort565Rgb"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("UshortGray"));
        RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredField("UshortIndexed"));

        Class<?> transformHelperClass = featureUtils.classLoader().loadClass("sun.java2d.loops.TransformHelper");
        RuntimeJNIAccess.register(transformHelperClass);
        RuntimeJNIAccess.register(transformHelperClass.getConstructor(long.class, surfaceTypeClass, compositeTypeClass, surfaceTypeClass));

        Class<?> xorCompositeClass = featureUtils.classLoader().loadClass("sun.java2d.loops.XORComposite");
        RuntimeJNIAccess.register(xorCompositeClass);
        RuntimeJNIAccess.register(xorCompositeClass.getDeclaredField("alphaMask"));
        RuntimeJNIAccess.register(xorCompositeClass.getDeclaredField("xorColor"));
        RuntimeJNIAccess.register(xorCompositeClass.getDeclaredField("xorPixel"));

        Class<?> regionClass = featureUtils.classLoader().loadClass("sun.java2d.pipe.Region");
        RuntimeJNIAccess.register(regionClass);
        RuntimeJNIAccess.register(regionClass.getDeclaredField("bands"));
        RuntimeJNIAccess.register(regionClass.getDeclaredField("endIndex"));
        RuntimeJNIAccess.register(regionClass.getDeclaredField("hix"));
        RuntimeJNIAccess.register(regionClass.getDeclaredField("hiy"));
        RuntimeJNIAccess.register(regionClass.getDeclaredField("lox"));
        RuntimeJNIAccess.register(regionClass.getDeclaredField("loy"));

        Class<?> regionIteratorClass = featureUtils.classLoader().loadClass("sun.java2d.pipe.RegionIterator");
        RuntimeJNIAccess.register(regionIteratorClass);
        RuntimeJNIAccess.register(regionIteratorClass.getDeclaredField("curIndex"));
        RuntimeJNIAccess.register(regionIteratorClass.getDeclaredField("numXbands"));
        RuntimeJNIAccess.register(regionIteratorClass.getDeclaredField("region"));

        Class<?> xrSurfaceDataClass = featureUtils.loadClass("sun.java2d.xr.XRSurfaceData");
        if (xrSurfaceDataClass != null) {
            RuntimeJNIAccess.register(xrSurfaceDataClass);
            RuntimeJNIAccess.register(xrSurfaceDataClass.getDeclaredField("picture"));
            RuntimeJNIAccess.register(xrSurfaceDataClass.getDeclaredField("xid"));
        }

        featureUtils.registerResource(Font.class, "META-INF/services/javax.imageio.spi.ImageInputStreamSpi",
                "META-INF/services/javax.imageio.spi.ImageOutputStreamSpi",
                "META-INF/services/javax.imageio.spi.ImageReaderSpi",
                "META-INF/services/javax.imageio.spi.ImageTranscoderSpi",
                "META-INF/services/javax.imageio.spi.ImageWriterSpi",
                "sun/awt/resources/awt_en.properties",
                "sun/awt/resources/awt_en_US.properties",
                "sun/awt/resources/awt_zh.properties",
                "sun/awt/resources/awt_zh_Hans.properties",
                "sun/awt/resources/awt_zh_Hans_CN.properties");
        featureUtils.registerResourceBundle(Font.class, "sun.awt.resources.awt", Locale.of("en-US"), Locale.of("zh-CN"));
    }

}
