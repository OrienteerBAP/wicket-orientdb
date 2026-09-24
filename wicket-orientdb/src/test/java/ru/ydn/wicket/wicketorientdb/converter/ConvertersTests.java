package ru.ydn.wicket.wicketorientdb.converter;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HexFormat;
import java.util.Locale;

import org.apache.wicket.util.convert.ConversionException;

public class ConvertersTests {
	
	@Test
	public void testHexConverter() {
		HexConverter converter = new HexConverter("0x", 10, 10);
		assertArrayEquals(HexFormat.of().parseHex("010203"), converter.convertToObject("010203", Locale.getDefault()));
		assertArrayEquals(HexFormat.of().parseHex("010203"), converter.convertToObject("0x010203", Locale.getDefault()));
		assertEquals("0x010203", converter.convertToString(HexFormat.of().parseHex("010203"), Locale.getDefault()));
		assertEquals("0x01234567\n89012345", converter.convertToString(HexFormat.of().parseHex("0123456789012345"), Locale.getDefault()));
		// Same behavior as the former javax.xml.bind.DatatypeConverter: upper-case output, case-insensitive input
		assertEquals("0xABCDEF", converter.convertToString(new byte[]{(byte) 0xab, (byte) 0xcd, (byte) 0xef}, Locale.getDefault()));
		assertArrayEquals(new byte[]{(byte) 0xab, (byte) 0xcd}, converter.convertToObject("0xabCD", Locale.getDefault()));
	}
	
	@Test(expected = ConversionException.class)
	public void testHexConverterOddLength() {
		new HexConverter().convertToObject("0x123", Locale.getDefault());
	}
	
	@Test(expected = ConversionException.class)
	public void testHexConverterIllegalCharacter() {
		new HexConverter().convertToObject("0x12zz", Locale.getDefault());
	}
}
