/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.bcel.Const;

/**
 * This class represents a stack map attribute used for preverification of Java classes for the
 * <a href="http://java.sun.com/j2me/"> Java 2 Micro Edition</a> (J2ME). This attribute is used by the
 * <a href="http://java.sun.com/products/cldc/">KVM</a> and contained within the Code attribute of a method. See CLDC
 * specification �5.3.1.2
 *
 * @see Code
 * @see StackMapEntry
 * @see StackMapType
 */
public final class StackMap extends Attribute {

    private StackMapEntry[] map; // Table of stack map entries

    /**
     * Construct object from input stream.
     *
     * @param name_index Index of name
     * @param length Content length in bytes
     * @param input Input stream
     * @param constant_pool Array of constants
     * @throws IOException if an I/O error occurs.
     */
    StackMap(final int name_index, final int length, final DataInput input, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (StackMapEntry[]) null, constant_pool);
        final int map_length = input.readUnsignedShort();
        map = new StackMapEntry[map_length];
        for (int i = 0; i < map_length; i++) {
            map[i] = new StackMapEntry(input, constant_pool);
        }
    }

    /*
     * @param name_index Index of name
     *
     * @param length Content length in bytes
     *
     * @param map Table of stack map entries
     *
     * @param constant_pool Array of constants
     */
    public StackMap(final int name_index, final int length, final StackMapEntry[] map, final ConstantPool constant_pool) {
        super(Const.ATTR_STACK_MAP, name_index, length, constant_pool);
        this.map = map;
    }

    /**
     * Called by objects that are traversing the nodes of the tree implicitely defined by the contents of a Java class.
     * I.e., the hierarchy of methods, fields, attributes, etc. spawns a tree of objects.
     *
     * @param v Visitor object
     */
    @Override
    public void accept(final Visitor v) {
        v.visitStackMap(this);
    }

    /**
     * @return deep copy of this attribute
     */
    @Override
    public Attribute copy(final ConstantPool constantPool) {
        final StackMap c = (StackMap) clone();
        c.map = new StackMapEntry[map.length];
        for (int i = 0; i < map.length; i++) {
            c.map[i] = map[i].copy();
        }
        c.setConstantPool(constantPool);
        return c;
    }

    /**
     * Dump stack map table attribute to file stream in binary format.
     *
     * @param file Output file stream
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(map.length);
        for (final StackMapEntry entry : map) {
            entry.dump(file);
        }
    }

    public int getMapLength() {
        return map == null ? 0 : map.length;
    }

    /**
     * @return Array of stack map entries
     */
    public StackMapEntry[] getStackMap() {
        return map;
    }

    /**
     * @param map Array of stack map entries
     */
    public void setStackMap(final StackMapEntry[] map) {
        this.map = map;
        int len = 2; // Length of 'number_of_entries' field prior to the array of stack maps
        for (final StackMapEntry element : map) {
            len += element.getMapEntrySize();
        }
        setLength(len);
    }

    /**
     * @return String representation.
     */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder("StackMap(");
        int running_offset = -1; // no +1 on first entry
        for (int i = 0; i < map.length; i++) {
            running_offset = map[i].getByteCodeOffset() + running_offset + 1;
            buf.append(String.format("%n@%03d %s", running_offset, map[i]));
            if (i < map.length - 1) {
                buf.append(", ");
            }
        }
        buf.append(')');
        return buf.toString();
    }
}
