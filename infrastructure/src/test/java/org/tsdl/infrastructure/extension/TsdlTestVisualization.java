package org.tsdl.infrastructure.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TsdlTestVisualization {
    boolean DEFAULT_DISABLE_VISUALIZATION = false;
    boolean DEFAULT_RENDER_POINTS = true;
    String PRECISE_AXIS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    String DEFAULT_AXIS_FORMAT = "";

    boolean skipVisualization() default DEFAULT_DISABLE_VISUALIZATION;

    boolean renderPointShape() default DEFAULT_RENDER_POINTS;

    String dateAxisFormat() default DEFAULT_AXIS_FORMAT;
}
