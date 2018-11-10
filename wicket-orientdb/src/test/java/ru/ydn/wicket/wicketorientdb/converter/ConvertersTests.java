package ru.ydn.wicket.wicketorientdb.converter;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Locale;

import javax.xml.bind.DatatypeConverter;

public class ConvertersTests {
	
	@Test
	public void testHexConverter() {
		HexConverter converter = new HexConverter("0x", 10, 10);
		assertArrayEquals(DatatypeConverter.parseHexBinary("010203"), converter.convertToObject("010203", Locale.getDefault()));
		assertArrayEquals(DatatypeConverter.parseHexBinary("010203"), converter.convertToObject("0x010203", Locale.getDefault()));
		assertEquals("0x010203", converter.convertToString(DatatypeConverter.parseHexBinary("010203"), Locale.getDefault()));
		assertEquals("0x01234567\n89012345", converter.convertToString(DatatypeConverter.parseHexBinary("0123456789012345"), Locale.getDefault()));
	}
}
