package ru.ydn.wicket.wicketorientdb.security;

@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Documented
public @interface RequiredOrientResources {
	public RequiredOrientResource[] value();
}
