package ru.ydn.wicket.wicketorientdb.proto;

public interface IMyBean
{
	public String getName();
	public void setName(String name);
	public Integer getInteger();
	public void setInteger(Integer integer);
	public String getSignature();
	public Object getCustom(String param);
	public void setCustom(String param, Object value);
	public Object getCustomString(String param);
	public IMyBean setCustomString(String param, String value);
}
