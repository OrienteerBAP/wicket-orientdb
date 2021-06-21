package ru.ydn.wicket.wicketorientdb.security;


import org.apache.wicket.authorization.Action;
/**
 * Annotation for specification of required OrientDB resources
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
@java.lang.annotation.Repeatable(RequiredOrientResources.class)
public @interface RequiredOrientResource {
	String value();
	String specific() default "";
	OrientPermission[] permissions() default {OrientPermission.CREATE, OrientPermission.READ, OrientPermission.UPDATE, OrientPermission.DELETE};
	String action() default Action.RENDER;
}
