/*
The contents of this file are subject to the Mozilla Public License
Version 1.1 (the "License"); you may not use this file except in
compliance with the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenFAST.

The Initial Developer of the Original Code is The LaSalle Technology
Group, LLC.  Portions created by The LaSalle Technology Group, LLC
are Copyright (C) The LaSalle Technology Group, LLC. All Rights Reserved.

Contributor(s): Jacob Northey <jacob@lasalletech.com>
                Craig Otis <cotis@lasalletech.com>
 */
package org.openfast;

public class BitVector {
	//实体值掩码
    private static final int VALUE_BITS_SET = 0x7F;
    //停止位
    private static final int STOP_BIT = 0x80;
    //存在图bit数组
    private byte[] bytes;
    //存在图中的bit长度
    private int size;

    public BitVector(int size) {
    	//根据字段占位的bit数目，分配对应大小的数组
        this(new byte[((size - 1) / 7) + 1]);
    }

    public BitVector(byte[] bytes) {
        this.bytes = bytes;
        //每个字节有7个有效的bit位
        this.size = bytes.length * 7;
        //最后一个字节的最高有效位为1，表示存在图字节流结束
        bytes[bytes.length - 1] |= STOP_BIT;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public byte[] getTruncatedBytes() {
    	//存在图字节数组的长度索引
        int index = bytes.length - 1;
        //从最末尾的字节开始，检查该字节的实体值是否均为0
        for (; (index > 0) && ((bytes[index] & VALUE_BITS_SET) == 0); index--)
            ;
        //不需要截断
        if (index == (bytes.length - 1)) {
            return bytes;
        }
        //剔除末尾之后实体值均为0的字节，剩余的字节
        byte[] truncated = new byte[index + 1];
        System.arraycopy(bytes, 0, truncated, 0, index + 1);
        //将最后一个字节的停止位置为1
        truncated[truncated.length - 1] |= STOP_BIT;
        return truncated;
    }

    public int getSize() {
        return this.size;
    }

    public void set(int fieldIndex) {
        //根据字段的顺序编号，设置对应bit位1
        //先找到该字段应该属于的字节
        //再找到字节内的位置
        bytes[fieldIndex / 7] |= (1 << (6 - (fieldIndex % 7)));
    }

    public boolean isSet(int fieldIndex) {
        //如果字段的顺序编号，大于存在图表示的bit位长度
        //存在图被截断了，原先的bit位为0，返回false
        if (fieldIndex >= bytes.length * 7)
            return false;
        //返回该bit位的值
        return ((bytes[fieldIndex / 7] & (1 << (6 - (fieldIndex % 7)))) > 0);
    }

    public boolean equals(Object obj) {
        if ((obj == null) || !(obj instanceof BitVector)) {
            return false;
        }
        return equals((BitVector) obj);
    }

    public boolean equals(BitVector other) {
        if (other.size != this.size) {
            return false;
        }
        for (int i = 0; i < this.bytes.length; i++) {
            if (this.bytes[i] != other.bytes[i]) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return bytes.hashCode();
    }

    public boolean isOverlong() {
        return (bytes.length > 1) && ((bytes[bytes.length - 1] & VALUE_BITS_SET) == 0);
    }

    public String toString() {
        return "BitVector [" + ByteUtil.convertByteArrayToBitString(bytes) + "]";
    }

    public int indexOfLastSet() {
    	//最后被设置的bit序号
        int index = bytes.length * 7 - 1;
        while (index >= 0 && !isSet(index))
            index--;
        return index;
    }
}
