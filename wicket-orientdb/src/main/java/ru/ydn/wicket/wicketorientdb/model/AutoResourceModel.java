package ru.ydn.wicket.wicketorientdb.model;

import org.apache.wicket.model.ResourceModel;

import com.google.common.base.CharMatcher;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;

public class AutoResourceModel extends ResourceModel
{

	public AutoResourceModel(OProperty property)
	{
		super(property.getFullName(), buitify(property.getName()));
	}
	
	public AutoResourceModel(OClass oClass)
	{
		super(oClass.getName(), buitify(oClass.getName()));
	}
	
	public static String buitify(String string)
	{
		char[] chars = string.toCharArray();
		StringBuilder sb = new StringBuilder();
		int lastApplied=0;
		for(int i=0; i<chars.length;i++)
		{
			char pCh = i>0?chars[i-1]:0;
			char ch = chars[i];
			if(i==0 && Character.isLowerCase(ch))
			{
				sb.append(Character.toUpperCase(ch));
				lastApplied=i+1;
			}
			else if(i>1 && Character.isUpperCase(ch) && !Character.isWhitespace(pCh) && !Character.isUpperCase(pCh))
			{
				sb.append(chars, lastApplied, i-lastApplied).append(' ').append(ch);
				lastApplied=i+1;
			}
			else if(i>1 && Character.isLowerCase(ch) && Character.isWhitespace(pCh))
			{
				sb.append(chars, lastApplied, i-lastApplied).append(Character.toUpperCase(ch));
				lastApplied=i+1;
			}
		}
		sb.append(chars, lastApplied, chars.length-lastApplied);
		return sb.toString();
	}

}
