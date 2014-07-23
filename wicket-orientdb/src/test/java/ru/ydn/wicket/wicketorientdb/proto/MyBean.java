package ru.ydn.wicket.wicketorientdb.proto;

public class MyBean implements IMyBean
{
	private String name;
	private Integer integer;
	
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
	
}
