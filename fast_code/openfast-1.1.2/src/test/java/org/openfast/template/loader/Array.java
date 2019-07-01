package org.openfast.template.loader;

import java.io.InputStream;

import org.openfast.BitVectorBuilder;
import org.openfast.BitVectorReader;
import org.openfast.Context;
import org.openfast.FieldValue;
import org.openfast.QName;
import org.openfast.template.Field;
import org.openfast.template.Group;

public class Array extends Field {

	public Array(QName name, boolean optional) {
		super(name, optional);
	}

	private static final long serialVersionUID = 1L;

	public FieldValue createValue(String value) {
		return null;
	}

	public FieldValue decode(InputStream in, Group template, Context context, BitVectorReader presenceMapReader) {
		return null;
	}

	public byte[] encode(FieldValue value, Group template, Context context, BitVectorBuilder presenceMapBuilder) {
		return null;
	}

	public String getTypeName() {
		return null;
	}

	public Class getValueType() {
		return null;
	}

	public boolean isPresenceMapBitSet(byte[] encoding, FieldValue fieldValue) {
		return false;
	}

	public boolean usesPresenceMapBit() {
		return false;
	}

	public boolean equals(Object obj) {
		return name.equals(((Array) obj).name);
	}
	
}
