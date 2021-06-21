package ru.ydn.wicket.wicketorientdb.security;


/**
 * Collection of {@link RequiredOrientResource}
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface RequiredOrientResources {
	public RequiredOrientResource[] value();
}
