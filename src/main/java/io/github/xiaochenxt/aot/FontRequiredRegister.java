package io.github.xiaochenxt.aot;

import io.github.xiaochenxt.aot.utils.FeatureUtils;
import org.graalvm.nativeimage.hosted.RuntimeJNIAccess;
import org.graalvm.nativeimage.hosted.RuntimeReflection;

import java.awt.*;
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
        featureUtils.registerReflectionIfPresent("javax.imageio.spi.ImageReaderSpi", "javax.imageio.spi.ImageWriterSpi");

        Class<?> dMarlinClass = featureUtils.loadClass("sun.java2d.marlin.DMarlinRenderingEngine");
        if (dMarlinClass != null) {
            RuntimeReflection.register(dMarlinClass);
            RuntimeReflection.register(dMarlinClass.getDeclaredConstructors());
        }

        Class<?> nativePrngClass = featureUtils.loadClass("sun.security.provider.NativePRNG");
        if (nativePrngClass != null) {
            RuntimeReflection.register(nativePrngClass);
            RuntimeReflection.register(nativePrngClass.getDeclaredConstructors());
        }

        Class<?> shaClass = featureUtils.loadClass("sun.security.provider.SHA");
        if (shaClass != null) {
            RuntimeReflection.register(shaClass);
            RuntimeReflection.register(shaClass.getDeclaredConstructors());
        }

        // ============================ JNI注册 (RuntimeJNIAccess) ============================
        Class<?> graphicsPrimitiveArrayClass = featureUtils.loadClass("sun.java2d.loops.GraphicsPrimitive");
        if (graphicsPrimitiveArrayClass != null) {
            RuntimeJNIAccess.register(graphicsPrimitiveArrayClass);
        }

        Class<?> alphaCompositeClass = featureUtils.loadClass("java.awt.AlphaComposite");
        if (alphaCompositeClass != null) {
            RuntimeJNIAccess.register(alphaCompositeClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(alphaCompositeClass, "extraAlpha", "rule"));
        }

        Class<?> colorClass = featureUtils.loadClass("java.awt.Color");
        if (colorClass != null) {
            RuntimeJNIAccess.register(colorClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(colorClass, "getRGB"));
        }

        Class<?> geClass = featureUtils.loadClass("java.awt.GraphicsEnvironment");
        if (geClass != null) {
            RuntimeJNIAccess.register(geClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(geClass, "getLocalGraphicsEnvironment", "isHeadless"));
        }

        Class<?> rectangleClass = featureUtils.loadClass("java.awt.Rectangle");
        if (rectangleClass != null) {
            RuntimeJNIAccess.register(rectangleClass);
            RuntimeJNIAccess.register(rectangleClass.getDeclaredConstructors());
        }

        Class<?> atClass = featureUtils.loadClass("java.awt.geom.AffineTransform");
        if (atClass != null) {
            RuntimeJNIAccess.register(atClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(atClass, "m00", "m01", "m02", "m10", "m11", "m12"));
        }

        Class<?> gpClass = featureUtils.loadClass("java.awt.geom.GeneralPath");
        if (gpClass != null) {
            RuntimeJNIAccess.register(gpClass);
            RuntimeJNIAccess.register(gpClass.getDeclaredConstructors());
        }

        Class<?> path2DClass = featureUtils.loadClass("java.awt.geom.Path2D");
        if (path2DClass != null) {
            RuntimeJNIAccess.register(path2DClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(path2DClass, "numTypes", "pointTypes", "windingRule"));
        }

        Class<?> path2DFloatClass = featureUtils.loadClass("java.awt.geom.Path2D$Float");
        if (path2DFloatClass != null) {
            RuntimeJNIAccess.register(path2DFloatClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(path2DFloatClass,"floatCoords"));
        }

        Class<?> p2dFloatClass = featureUtils.loadClass("java.awt.geom.Point2D$Float");
        if (p2dFloatClass != null) {
            RuntimeJNIAccess.register(p2dFloatClass);
            RuntimeJNIAccess.register(p2dFloatClass.getDeclaredFields());
            RuntimeJNIAccess.register(p2dFloatClass.getDeclaredConstructors());
        }

        Class<?> r2dFloatClass = featureUtils.loadClass("java.awt.geom.Rectangle2D$Float");
        if (r2dFloatClass != null) {
            RuntimeJNIAccess.register(r2dFloatClass);
            RuntimeJNIAccess.register(r2dFloatClass.getDeclaredFields());
            RuntimeJNIAccess.register(r2dFloatClass.getDeclaredConstructors());
        }

        Class<?> biClass = featureUtils.loadClass("java.awt.image.BufferedImage");
        if (biClass != null) {
            RuntimeJNIAccess.register(biClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(biClass, "colorModel", "imageType", "raster"));
            RuntimeJNIAccess.register(featureUtils.collectMethods(biClass, "getRGB","setRGB"));
        }

        Class<?> cmClass = featureUtils.loadClass("java.awt.image.ColorModel");
        if (cmClass != null) {
            RuntimeJNIAccess.register(cmClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(cmClass, "colorSpace", "colorSpaceType", "isAlphaPremultiplied", "is_sRGB", "nBits",
                    "numComponents", "supportsAlpha", "transparency"));
            RuntimeJNIAccess.register(cmClass.getMethod("getRGBdefault"));
        }

        Class<?> icmClass = featureUtils.loadClass("java.awt.image.IndexColorModel");
        if (icmClass != null) {
            RuntimeJNIAccess.register(icmClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(icmClass, "allgrayopaque", "colorData", "map_size", "rgb", "transparent_index"));
        }

        Class<?> rasterClass = featureUtils.loadClass("java.awt.image.Raster");
        if (rasterClass != null) {
            RuntimeJNIAccess.register(rasterClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(rasterClass, "dataBuffer", "height", "minX", "minY", "numBands", "numDataElements",
                    "sampleModel", "sampleModelTranslateX", "sampleModelTranslateY", "width"));
        }

        Class<?> smClass = featureUtils.loadClass("java.awt.image.SampleModel");
        if (smClass != null) {
            RuntimeJNIAccess.register(smClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(smClass, "height", "width"));
            RuntimeJNIAccess.register(featureUtils.collectMethods(smClass, "getPixels","setPixels"));
        }

        Class<?> sppsmClass = featureUtils.loadClass("java.awt.image.SinglePixelPackedSampleModel");
        if (sppsmClass != null) {
            RuntimeJNIAccess.register(sppsmClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(sppsmClass, "bitMasks", "bitOffsets", "bitSizes", "maxBitSize"));
        }

        Class<Boolean> booleanClass = Boolean.class;
        RuntimeJNIAccess.register(booleanClass);
        RuntimeJNIAccess.register(featureUtils.collectMethods(booleanClass,"getBoolean"));

        Class<?> internalError = featureUtils.loadClass("java.lang.InternalError");
        if (internalError != null) RuntimeJNIAccess.register(internalError.getDeclaredConstructors());
        RuntimeJNIAccess.register(String[].class);

        Class<System> systemClass = System.class;
        RuntimeJNIAccess.register(systemClass);
        RuntimeJNIAccess.register(featureUtils.collectMethods(systemClass, "load"));

        Class<?> sunHintsClass = featureUtils.loadClass("sun.awt.SunHints");
        if (sunHintsClass != null) {
            RuntimeJNIAccess.register(sunHintsClass);
            RuntimeJNIAccess.register(sunHintsClass.getDeclaredField("INTVAL_STROKE_PURE"));
        }

        Class<?> sunToolkitClass = featureUtils.loadClass("sun.awt.SunToolkit");
        if (sunToolkitClass != null) {
            RuntimeJNIAccess.register(sunToolkitClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(sunToolkitClass, "awtLock", "awtLockNotify", "awtLockNotifyAll",
                    "awtLockWait", "awtUnlock"));
        }

        Class<?> xErrorHandlerUtilClass = featureUtils.loadClass("sun.awt.X11.XErrorHandlerUtil");
        if (xErrorHandlerUtilClass != null) {
            RuntimeJNIAccess.register(xErrorHandlerUtilClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(xErrorHandlerUtilClass,"init"));
        }

        Class<?> x11GraphicsConfigClass = featureUtils.loadClass("sun.awt.X11GraphicsConfig");
        if (x11GraphicsConfigClass != null) {
            RuntimeJNIAccess.register(x11GraphicsConfigClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(x11GraphicsConfigClass, "aData", "bitsPerPixel"));
        }

        Class<?> x11GraphicsDeviceClass = featureUtils.loadClass("sun.awt.X11GraphicsDevice");
        if (x11GraphicsDeviceClass != null) {
            RuntimeJNIAccess.register(x11GraphicsDeviceClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(x11GraphicsDeviceClass, "addDoubleBufferVisual"));
        }

        Class<?> icmColorDataClass = featureUtils.loadClass("sun.awt.image.BufImgSurfaceData$ICMColorData");
        if (icmColorDataClass != null) {
            RuntimeJNIAccess.register(featureUtils.collectFields(icmColorDataClass, "pData"));
            RuntimeJNIAccess.register(icmColorDataClass.getDeclaredConstructors());
        }

        Class<?> integerComponentRasterClass = featureUtils.loadClass("sun.awt.image.IntegerComponentRaster");
        if (integerComponentRasterClass != null) {
            RuntimeJNIAccess.register(integerComponentRasterClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(integerComponentRasterClass, "data", "dataOffsets",
                    "pixelStride", "scanlineStride", "type"));
        }

        Class<?> charToGlyphMapperClass = featureUtils.loadClass("sun.font.CharToGlyphMapper");
        if (charToGlyphMapperClass != null) {
            RuntimeJNIAccess.register(charToGlyphMapperClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(charToGlyphMapperClass, "charToGlyph"));
        }

        Class<?> font2DClass = featureUtils.loadClass("sun.font.Font2D");
        if (font2DClass != null) {
            RuntimeJNIAccess.register(font2DClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(font2DClass, "canDisplay", "charToGlyph", "charToGlyphRaw", "charToVariationGlyph",
                    "charToVariationGlyphRaw", "getMapper", "getTableBytes"));
        }

        Class<?> fontStrikeClass = featureUtils.loadClass("sun.font.FontStrike");
        if (fontStrikeClass != null) {
            RuntimeJNIAccess.register(fontStrikeClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(fontStrikeClass, "getGlyphMetrics"));
        }

        Class<?> fontUtilitiesClass = featureUtils.loadClass("sun.font.FontUtilities");
        if (fontUtilitiesClass != null) {
            RuntimeJNIAccess.register(fontUtilitiesClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(fontUtilitiesClass, "debugFonts"));
        }

        Class<?> freetypeFontScalerClass = featureUtils.loadClass("sun.font.FreetypeFontScaler");
        if (freetypeFontScalerClass != null) {
            RuntimeJNIAccess.register(freetypeFontScalerClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(freetypeFontScalerClass, "invalidateScaler"));
        }

        Class<?> glyphListClass = featureUtils.loadClass("sun.font.GlyphList");
        if (glyphListClass != null) {
            RuntimeJNIAccess.register(glyphListClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(glyphListClass, "gposx", "gposy", "images", "lcdRGBOrder", "lcdSubPixPos",
                    "len", "positions", "usePositions"));
        }

        Class<?> physicalStrikeClass = featureUtils.loadClass("sun.font.PhysicalStrike");
        if (physicalStrikeClass != null) {
            RuntimeJNIAccess.register(physicalStrikeClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(physicalStrikeClass, "pScalerContext"));
            RuntimeJNIAccess.register(featureUtils.collectMethods(physicalStrikeClass, "adjustPoint", "getGlyphPoint"));
        }

        Class<?> strikeMetricsClass = featureUtils.loadClass("sun.font.StrikeMetrics");
        if (strikeMetricsClass != null) {
            RuntimeJNIAccess.register(strikeMetricsClass);
            RuntimeJNIAccess.register(strikeMetricsClass.getDeclaredConstructors());
        }

        Class<?> trueTypeFontClass = featureUtils.loadClass("sun.font.TrueTypeFont");
        if (trueTypeFontClass != null) {
            RuntimeJNIAccess.register(trueTypeFontClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(trueTypeFontClass, "readBlock","readBytes"));
        }

        Class<?> type1FontClass = featureUtils.loadClass("sun.font.Type1Font");
        if (type1FontClass != null) {
            RuntimeJNIAccess.register(type1FontClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(type1FontClass, "readFile"));
        }

        Class<?> disposerClass = featureUtils.loadClass("sun.java2d.Disposer");
        if (disposerClass != null) {
            RuntimeJNIAccess.register(disposerClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(disposerClass, "addRecord"));
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
            RuntimeJNIAccess.register(featureUtils.collectFields(sunGraphics2DClass, "clipRegion", "composite", "eargb", "lcdTextContrast", "pixel", "strokeHint"));
        }

        Class<?> sunGraphicsEnvironmentClass = featureUtils.loadClass("sun.java2d.SunGraphicsEnvironment");
        if (sunGraphicsEnvironmentClass != null) {
            RuntimeJNIAccess.register(sunGraphicsEnvironmentClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(sunGraphicsEnvironmentClass, "isDisplayLocal"));
        }

        Class<?> surfaceDataClass = featureUtils.loadClass("sun.java2d.SurfaceData");
        if (surfaceDataClass != null) {
            RuntimeJNIAccess.register(surfaceDataClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(surfaceDataClass, "pData", "valid"));
        }

        Class<?> blitClass = featureUtils.loadClass("sun.java2d.loops.Blit");
        if (blitClass != null) {
            RuntimeJNIAccess.register(blitClass);
            RuntimeJNIAccess.register(blitClass.getDeclaredConstructors());
        }

        Class<?> blitBgClass = featureUtils.loadClass("sun.java2d.loops.BlitBg");
        if (blitBgClass != null) {
            RuntimeJNIAccess.register(blitBgClass);
            RuntimeJNIAccess.register(blitBgClass.getDeclaredConstructors());
        }

        Class<?> compositeTypeClass2 = featureUtils.loadClass("sun.java2d.loops.CompositeType");
        if (compositeTypeClass2 != null) {
            RuntimeJNIAccess.register(compositeTypeClass2);
            RuntimeJNIAccess.register(featureUtils.collectFields(compositeTypeClass2, "AnyAlpha", "Src", "SrcNoEa", "SrcOver", "SrcOverNoEa", "Xor"));
        }

        Class<?> drawGlyphListClass = featureUtils.loadClass("sun.java2d.loops.DrawGlyphList");
        if (drawGlyphListClass != null) {
            RuntimeJNIAccess.register(drawGlyphListClass);
            RuntimeJNIAccess.register(drawGlyphListClass.getDeclaredConstructors());
        }

        Class<?> drawGlyphListAAClass = featureUtils.loadClass("sun.java2d.loops.DrawGlyphListAA");
        if (drawGlyphListAAClass != null) {
            RuntimeJNIAccess.register(drawGlyphListAAClass);
            RuntimeJNIAccess.register(drawGlyphListAAClass.getDeclaredConstructors());
        }

        Class<?> drawGlyphListLCDClass = featureUtils.loadClass("sun.java2d.loops.DrawGlyphListLCD");
        if (drawGlyphListLCDClass != null) {
            RuntimeJNIAccess.register(drawGlyphListLCDClass);
            RuntimeJNIAccess.register(drawGlyphListLCDClass.getDeclaredConstructors());
        }

        Class<?> drawLineClass = featureUtils.loadClass("sun.java2d.loops.DrawLine");
        if (drawLineClass != null) {
            RuntimeJNIAccess.register(drawLineClass);
            RuntimeJNIAccess.register(drawLineClass.getDeclaredConstructors());
        }

        Class<?> drawParallelogramClass = featureUtils.loadClass("sun.java2d.loops.DrawParallelogram");
        if (drawParallelogramClass != null) {
            RuntimeJNIAccess.register(drawParallelogramClass);
            RuntimeJNIAccess.register(drawParallelogramClass.getDeclaredConstructors());
        }

        Class<?> drawPathClass = featureUtils.loadClass("sun.java2d.loops.DrawPath");
        if (drawPathClass != null) {
            RuntimeJNIAccess.register(drawPathClass);
            RuntimeJNIAccess.register(drawPathClass.getDeclaredConstructors());
        }

        Class<?> drawPolygonsClass = featureUtils.loadClass("sun.java2d.loops.DrawPolygons");
        if (drawPolygonsClass != null) {
            RuntimeJNIAccess.register(drawPolygonsClass);
            RuntimeJNIAccess.register(drawPolygonsClass.getDeclaredConstructors());
        }

        Class<?> drawRectClass = featureUtils.loadClass("sun.java2d.loops.DrawRect");
        if (drawRectClass != null) {
            RuntimeJNIAccess.register(drawRectClass);
            RuntimeJNIAccess.register(drawRectClass.getDeclaredConstructors());
        }

        Class<?> fillParallelogramClass = featureUtils.loadClass("sun.java2d.loops.FillParallelogram");
        if (fillParallelogramClass != null) {
            RuntimeJNIAccess.register(fillParallelogramClass);
            RuntimeJNIAccess.register(fillParallelogramClass.getDeclaredConstructors());
        }

        Class<?> fillPathClass = featureUtils.loadClass("sun.java2d.loops.FillPath");
        if (fillPathClass != null) {
            RuntimeJNIAccess.register(fillPathClass);
            RuntimeJNIAccess.register(fillPathClass.getDeclaredConstructors());
        }

        Class<?> fillRectClass = featureUtils.loadClass("sun.java2d.loops.FillRect");
        if (fillRectClass != null) {
            RuntimeJNIAccess.register(fillRectClass);
            RuntimeJNIAccess.register(fillRectClass.getDeclaredConstructors());
        }

        Class<?> fillSpansClass = featureUtils.loadClass("sun.java2d.loops.FillSpans");
        if (fillSpansClass != null) {
            RuntimeJNIAccess.register(fillSpansClass);
            RuntimeJNIAccess.register(fillSpansClass.getDeclaredConstructors());
        }

        Class<?> graphicsPrimitiveClass = featureUtils.loadClass("sun.java2d.loops.GraphicsPrimitive");
        if (graphicsPrimitiveClass != null) {
            RuntimeJNIAccess.register(graphicsPrimitiveClass);
            RuntimeJNIAccess.register(graphicsPrimitiveClass.getDeclaredField("pNativePrim"));
        }

        Class<?> graphicsPrimitiveMgrClass = featureUtils.loadClass("sun.java2d.loops.GraphicsPrimitiveMgr");
        if (graphicsPrimitiveMgrClass != null) {
            RuntimeJNIAccess.register(graphicsPrimitiveMgrClass);
            RuntimeJNIAccess.register(featureUtils.collectMethods(graphicsPrimitiveMgrClass, "register"));
        }
        Class<?> graphicsPrimitive = featureUtils.loadClass("sun.java2d.loops.GraphicsPrimitive[]");
        if (graphicsPrimitive != null) RuntimeJNIAccess.register(graphicsPrimitive);

        Class<?> maskBlitClass = featureUtils.loadClass("sun.java2d.loops.MaskBlit");
        if (maskBlitClass != null) {
            RuntimeJNIAccess.register(maskBlitClass);
            RuntimeJNIAccess.register(maskBlitClass.getDeclaredConstructors());
        }

        Class<?> maskFillClass = featureUtils.loadClass("sun.java2d.loops.MaskFill");
        if (maskFillClass != null) {
            RuntimeJNIAccess.register(maskFillClass);
            RuntimeJNIAccess.register(maskFillClass.getDeclaredConstructors());
        }

        Class<?> scaledBlitClass = featureUtils.loadClass("sun.java2d.loops.ScaledBlit");
        if (scaledBlitClass != null) {
            RuntimeJNIAccess.register(scaledBlitClass);
            RuntimeJNIAccess.register(scaledBlitClass.getDeclaredConstructors());
        }

        Class<?> surfaceTypeClass2 = featureUtils.loadClass("sun.java2d.loops.SurfaceType");
        if (surfaceTypeClass2 != null) {
            RuntimeJNIAccess.register(surfaceTypeClass2);
            RuntimeJNIAccess.register(surfaceTypeClass2.getDeclaredFields());
        }

        Class<?> transformHelperClass = featureUtils.loadClass("sun.java2d.loops.TransformHelper");
        if (transformHelperClass != null) {
            RuntimeJNIAccess.register(transformHelperClass);
            RuntimeJNIAccess.register(transformHelperClass.getDeclaredConstructors());
        }

        Class<?> xorCompositeClass = featureUtils.loadClass("sun.java2d.loops.XORComposite");
        if (xorCompositeClass != null) {
            RuntimeJNIAccess.register(xorCompositeClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(xorCompositeClass, "alphaMask", "xorColor", "xorPixel"));
        }

        Class<?> regionClass = featureUtils.loadClass("sun.java2d.pipe.Region");
        if (regionClass != null) {
            RuntimeJNIAccess.register(regionClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(regionClass, "bands", "endIndex", "hix", "hiy", "lox", "loy"));
        }

        Class<?> regionIteratorClass = featureUtils.loadClass("sun.java2d.pipe.RegionIterator");
        if (regionIteratorClass != null) {
            RuntimeJNIAccess.register(regionIteratorClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(regionIteratorClass, "curIndex", "numXbands", "region"));
        }

        Class<?> xrSurfaceDataClass = featureUtils.loadClass("sun.java2d.xr.XRSurfaceData");
        if (xrSurfaceDataClass != null) {
            RuntimeJNIAccess.register(xrSurfaceDataClass);
            RuntimeJNIAccess.register(featureUtils.collectFields(xrSurfaceDataClass, "picture", "xid"));
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
