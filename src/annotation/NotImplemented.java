package annotation;

import java.lang.annotation.*;

/**
 * @author kamontat
 * @version 1.0
 * @since Tue 25/Apr/2017 - 3:21 PM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NotImplemented {

}
