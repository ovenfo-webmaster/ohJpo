/*
 * PPO v1.7.0
 * Author		: Oscar Huertas
 * Date			: 2018-07-11
 * Description	: Annotation class to stablish the source or the rules to access
 * */
package ohSolutions.ohJpo.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD}) //on class level
public @interface JpoClass {
	String source() default "";
	String oauth2Roles() default "";
	boolean oauth2Enable() default false;
	JpoRequest[] method() default {JpoRequest.POST} ; // Post Delete
}