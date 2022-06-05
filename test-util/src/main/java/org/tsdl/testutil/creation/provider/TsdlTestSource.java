package org.tsdl.testutil.creation.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TsdlTestSource {
  String value();

  String timestampFormat() default "yyyy-MM-dd HH:mm:ss.SSS";

  // TODO
  int skipHeaders() default 0;
}
