package ru.ydn.wicket.wicketorientdb.security;

/**
 * Interface for marking components dynamically secured
 */
public interface ISecuredComponent {
	/**
	 * @return array of resources which should be allowed for current user to allow him to see this component
	 */
	public RequiredOrientResource[] getRequiredResources();

}
