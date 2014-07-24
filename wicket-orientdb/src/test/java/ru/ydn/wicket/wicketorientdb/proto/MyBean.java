package ru.ydn.wicket.wicketorientdb.proto;

import java.util.HashMap;
import java.util.Map;

public class MyBean implements IMyBean
{
	private String name;
	private Integer integer;
	private Map<String, Object> custom = new HashMap<String, Object>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		return custom.get(param);
	}
	@Override
	public void setCustom(String param, Object value) {
		custom.put(name, value);
	}
	
}
