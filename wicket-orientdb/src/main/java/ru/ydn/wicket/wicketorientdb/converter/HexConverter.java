package ru.ydn.wicket.wicketorientdb.converter;

import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.string.Strings;

/**
 * Converter between byte[] and hex representation 
 */
public class HexConverter extends AbstractJointConverter<byte[]> {
	
	private final String prefix;
	private final int cols;
	private final int maxLength;
	
	public HexConverter() {
		this("0x", 128, -1);
	}
	
	public HexConverter(String prefix, int cols, int maxLength) {
		this.prefix = prefix;
		this.cols = cols;
		this.maxLength = maxLength;
	}

	@Override
	public byte[] convertToObject(String data, Locale locale) throws ConversionException {
		try {
			if(Strings.isEmpty(data)) return null;
			if(!Strings.isEmpty(prefix) && data.startsWith(prefix)) data = data.substring(prefix.length());
			return DatatypeConverter.parseHexBinary(data.replaceAll("\\R", ""));
		} catch (IllegalArgumentException e) {
			throw newConversionException(e.getMessage(), data, byte[].class, locale);
		}
	}

	@Override
	public String convertToString(byte[] array, Locale locale) {
		if(array==null || array.length==0) return null;
		if(maxLength>0 && maxLength<array.length) {
			byte[] newArray = new byte[maxLength];
			System.arraycopy(array, 0, newArray, 0, newArray.length);
			array = newArray;
		}
		String data = DatatypeConverter.printHexBinary(array);
		if(!Strings.isEmpty(prefix))data = prefix+data;
		if(cols>0) data = data.replaceAll("(.{"+cols+"})", "$1\n");
		return data;
	}


}
