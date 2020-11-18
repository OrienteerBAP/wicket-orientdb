package ru.ydn.wicket.wicketorientdb.security;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.util.string.Strings;

import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.security.ORestrictedAccessHook;
import com.orientechnologies.orient.core.metadata.security.ORestrictedOperation;
import com.orientechnologies.orient.core.metadata.security.ORule;
import com.orientechnologies.orient.core.metadata.security.ORule.ResourceGeneric;
import com.orientechnologies.orient.core.record.impl.ODocument;

import ru.ydn.wicket.wicketorientdb.OrientDbWebApplication;

/**
 * Helper class for security functionality
 */
public class OSecurityHelper 
{
	public static final String FUNCTION = "FUNCTION";
	public static final String CLASS = "CLASS";
	public static final String CLUSTER = "CLUSTER";
	public static final String BYPASS_RESTRICTED = "BYPASS_RESTRICTED";
	public static final String DATABASE = "DATABASE";
	public static final String SCHEMA = "SCHEMA";
	public static final String COMMAND = "COMMAND";
	public static final String COMMAND_GREMLIN = "COMMAND_GREMLIN";
	public static final String RECORD_HOOK = "RECORD_HOOK";
	public static final String SYSTEM_CLUSTERS = "SYSTEM_CLUSTERS";
	public static final String FEATURE = "FEATURE";
	
	public static final ResourceGeneric FEATURE_RESOURCE = new ResourceGeneric(FEATURE, FEATURE.toLowerCase()) {
		private static final long serialVersionUID = 1L;
	};
	
	private static final Map<OrientPermission, ORestrictedOperation> MAPPING_FOR_HACK = new HashMap<OrientPermission, ORestrictedOperation>();
	static
	{
		MAPPING_FOR_HACK.put(OrientPermission.READ, ORestrictedOperation.ALLOW_READ);
		MAPPING_FOR_HACK.put(OrientPermission.UPDATE, ORestrictedOperation.ALLOW_UPDATE);
		MAPPING_FOR_HACK.put(OrientPermission.DELETE, ORestrictedOperation.ALLOW_DELETE);
	}
	
	private static class RequiredOrientResourceImpl implements RequiredOrientResource
	{
		private final String value;
		private final String specific;
		private final OrientPermission[] permissions;
		private final String action;
		
		public RequiredOrientResourceImpl(String value, String specific, Action action, OrientPermission[] permissions)
		{
			this.value = value;
			this.specific = specific;
			this.action = action!=null?action.getName():Action.RENDER;
			this.permissions = permissions;
		}
		
		@Override
		public Class<? extends Annotation> annotationType() {
			return RequiredOrientResource.class;
		}

		@Override
		public String value() {
			return value;
		}
		
		@Override
		public String specific() {
			return specific;
		}

		@Override
		public OrientPermission[] permissions() {
			return permissions;
		}
		
		@Override
		public String action() {
			return action;
		}
		
	}
	private OSecurityHelper()
	{
		
	}
	/**
	 * @param oClass subject {@link OClass} for security check
	 * @param permissions required permissions for access {@link OClass}
	 * @return array of {@link RequiredOrientResource} for rights setup emulation
	 */
	public static RequiredOrientResource[] requireOClass(final OClass oClass, final OrientPermission... permissions)
	{
		return requireOClass(oClass.getName(), permissions);
	}
	/**
	 * @param oClassName name of the subject {@link OClass} for security check
	 * @param permissions required permissions for access {@link OClass}
	 * @return array of {@link RequiredOrientResource} for rights setup emulation
	 */
	public static RequiredOrientResource[] requireOClass(final String oClassName, final OrientPermission... permissions)
	{
		return requireResource(ORule.ResourceGeneric.CLASS, oClassName, permissions);
	}
	/**
	 * @param resource required {@link ResourceGeneric}
	 * @param specific specific resource
	 * @param permissions required permissions
	 * @return array of {@link RequiredOrientResource} for rights setup emulation
	 */
	public static RequiredOrientResource[] requireResource(final ORule.ResourceGeneric resource, final String specific, 
																final OrientPermission... permissions)
	{
		return requireResource(resource, specific, null, permissions);
	}
	
	/**
	 * @param oClass subject {@link OClass} for security check
	 * @param action action to be secured for
	 * @param permissions required permissions for access {@link OClass}
	 * @return array of {@link RequiredOrientResource} for rights setup emulation
	 */
	public static RequiredOrientResource[] requireOClass(final OClass oClass, final Action action, 
															final OrientPermission... permissions)
	{
		return oClass!=null?requireOClass(oClass.getName(), action, permissions):new RequiredOrientResource[0];
	}
	/**
	 * @param oClassName name of the subject {@link OClass} for security check
	 * @param action action to be secured for
	 * @param permissions required permissions for access {@link OClass}
	 * @return array of {@link RequiredOrientResource} for rights setup emulation
	 */
	public static RequiredOrientResource[] requireOClass(final String oClassName, final Action action, 
															final OrientPermission... permissions)
	{
		return oClassName!=null?requireResource(ORule.ResourceGeneric.CLASS, oClassName, action, permissions):new RequiredOrientResource[0];
	}
	/**
	 * @param resource generic resource
	 * @param specific specific resource to secure
	 * @param action action to be secured for
	 * @param permissions required permissions for access {@link OClass}
	 * @return array of {@link RequiredOrientResource} for rights setup emulation
	 */
	public static RequiredOrientResource[] requireResource(final ORule.ResourceGeneric resource, final String specific, 
															final Action action, final OrientPermission... permissions)
	{
		return new RequiredOrientResource[]{new RequiredOrientResourceImpl(resource.getName(), specific, action, permissions)};
	}
	/**
	 * Check that all required permissions present for specified {@link ODocument}
	 * @param doc {@link ODocument} to check security rights for
	 * @param permissions {@link OrientPermission}s to check
	 * @return true if all permissions are allowable
	 */
	public static boolean isAllowed(ODocument doc, OrientPermission... permissions)
	{
		if(!isAllowed(doc.getSchemaClass(), permissions)) return false;
		for (OrientPermission orientPermission : permissions) {
			ORestrictedOperation allowOperation = MAPPING_FOR_HACK.get(orientPermission);
			if(allowOperation != null) {
				if (!ORestrictedAccessHook.isAllowed(ODatabaseRecordThreadLocal.instance().get(), doc, allowOperation, false)) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Check that all required permissions present for specified {@link OClass}
	 * @param oClass {@link OClass} to check security rights for
	 * @param permissions {@link OrientPermission}s to check
	 * @return true of all permissions are allowable
	 */
	public static boolean isAllowed(OClass oClass, OrientPermission... permissions)
	{
		return isAllowed(ORule.ResourceGeneric.CLASS, oClass.getName(), permissions);
	}
	
	/**
	 * Check that all required permissions present for specified resource and specific
	 * @param resource specific resource to secure
	 * @param specific specific resource to secure
	 * @param permissions {@link OrientPermission}s to check
	 * @return true of require resource if allowed for current user
	 */
	public static boolean isAllowed(ORule.ResourceGeneric resource, String specific, OrientPermission... permissions)
	{
		return OrientDbWebApplication.lookupApplication().checkResource(resource, specific, permissions);
	}
	
	public static <T extends Component> T secureComponent(T component, RequiredOrientResource... resources)
	{
		return secureComponent(component, toSecureMap(resources));
	}
	
	public static <T extends Component> T secureComponent(T component, HashMap<String, OrientPermission[]> secureMap)
	{
		component.setMetaData(OrientPermission.REQUIRED_ORIENT_RESOURCES_KEY, secureMap);
		return component;
	}
	
	/**
	 * Transform array of {@link RequiredOrientResource}s to a {@link HashMap}.
	 * {@link HashMap} is required to be serializable
	 * @param resources {@link RequiredOrientResource}s to convert
	 * @return {@link HashMap} representation of an {@link OrientPermission}s
	 */
	public static HashMap<String, OrientPermission[]> toSecureMap(RequiredOrientResource... resources)
	{
		HashMap<String, OrientPermission[]> secureMap = new HashMap<String, OrientPermission[]>();
		for (RequiredOrientResource requiredOrientResource : resources)
		{
			String resource = requiredOrientResource.value();
			String specific = requiredOrientResource.specific();
			String action = requiredOrientResource.action();
			if(!Strings.isEmpty(specific)) resource = resource+"."+specific;
			if(!Strings.isEmpty(action)) resource = resource+":"+action;
			secureMap.put(resource, requiredOrientResource.permissions());
		}
		return secureMap;
	}
	
	/**
	 * Transform name to {@link ORule.ResourceGeneric}
	 * @param name name to transform
	 * @return {@link ORule.ResourceGeneric} or null
	 */
	public static ORule.ResourceGeneric getResourceGeneric(String name)
	{
		String shortName = Strings.beforeFirst(name, '.');
		if(Strings.isEmpty(shortName)) shortName = name;
		ORule.ResourceGeneric value = ORule.ResourceGeneric.valueOf(shortName);
		if(value==null) value = ORule.mapLegacyResourceToGenericResource(name);
		return value;
	}
	
	/**
	 * Extract specific from resource string
	 * @param name name to transform
	 * @return specific resource or null
	 */
	public static String getResourceSpecific(String name)
	{
		ORule.ResourceGeneric generic = getResourceGeneric(name);
		String specific = generic!=null?Strings.afterFirst(name, '.'):name;
		return Strings.isEmpty(specific)?null:specific;
	}
	
}
