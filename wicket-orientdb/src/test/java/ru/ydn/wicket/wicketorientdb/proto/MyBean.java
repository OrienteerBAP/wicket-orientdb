package ru.ydn.wicket.wicketorientdb.proto;

import java.util.HashMap;
import java.util.Map;

public class MyBean implements IMyBean
{
	private String name;
	private String description;
	private Integer integer;
	private Map<String, Object> customMap = new HashMap<String, Object>();
	private Map<String, String> customStringMap = new HashMap<String, String>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getInteger() {
		return integer;
	}
	public void setInteger(Integer integer) {
		this.integer = integer;
	}
	@Override
	public String getSignature() {
		return "REAL";
	}
	@Override
	public Object getCustom(String param) {
		return customMap.get(param);
	}
	@Override
	public void setCustom(String param, Object value) {
		customMap.put(param, value);
	}
	
	@Override
	public Object getCustomString(String param) {
		return customStringMap.get(param);
	}
	@Override
	public IMyBean setCustomString(String param, String value) {
		customStringMap.put(param, value);
		return this;
	}
	
}
