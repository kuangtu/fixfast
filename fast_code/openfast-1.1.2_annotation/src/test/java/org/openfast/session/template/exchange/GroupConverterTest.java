package org.openfast.session.template.exchange;

import junit.framework.TestCase;
import org.openfast.GroupValue;
import org.openfast.QName;
import org.openfast.session.SessionControlProtocol_1_1;
import org.openfast.template.Field;
import org.openfast.template.Group;

public class GroupConverterTest extends TestCase {
    GroupConverter converter = new GroupConverter();
    ConversionContext context = SessionControlProtocol_1_1.createInitialContext();
    
    public void testConvert() {
        Group group = new Group(new QName("entry", "http://openfast.org"), new Field[] {}, false);
        QName typeRef = new QName("MapEntry", "org.openfast");
        group.setTypeReference(typeRef);
        GroupValue message = converter.convert(group, context);
        assertEquals(typeRef.getName(), message.getGroup("TypeRef").getString("Name"));
        assertEquals(typeRef.getNamespace(), message.getGroup("TypeRef").getString("Ns"));
        
        Group converted = (Group) converter.convert(message, null, context);
        assertEquals(group, converted);
    }
}
