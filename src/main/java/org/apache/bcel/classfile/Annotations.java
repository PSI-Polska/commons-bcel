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
 * base class for annotations
 *
 * @since 6.0
 */
public abstract class Annotations extends Attribute {

    private AnnotationEntry[] annotationTable;
    private final boolean isRuntimeVisible;

    /**
     * @param annotationType the subclass type of the annotation
     * @param nameIndex Index pointing to the name <em>Code</em>
     * @param length Content length in bytes
     * @param annotationTable the actual annotations
     * @param constantPool Array of constants
     * @param isRuntimeVisible whether this Annotation visible at runtime
     */
    public Annotations(final byte annotationType, final int nameIndex, final int length, final AnnotationEntry[] annotationTable,
        final ConstantPool constantPool, final boolean isRuntimeVisible) {
        super(annotationType, nameIndex, length, constantPool);
        this.annotationTable = annotationTable;
        this.isRuntimeVisible = isRuntimeVisible;
    }

    /**
     * @param annotationType the subclass type of the annotation
     * @param nameIndex Index pointing to the name <em>Code</em>
     * @param length Content length in bytes
     * @param input Input stream
     * @param constantPool Array of constants
     * @param isRuntimeVisible whether this Annotation visible at runtime
     * @throws IOException if an I/O error occurs.
     */
    Annotations(final byte annotationType, final int nameIndex, final int length, final DataInput input, final ConstantPool constantPool,
        final boolean isRuntimeVisible) throws IOException {
        this(annotationType, nameIndex, length, (AnnotationEntry[]) null, constantPool, isRuntimeVisible);
        final int annotation_table_length = input.readUnsignedShort();
        annotationTable = new AnnotationEntry[annotation_table_length];
        for (int i = 0; i < annotation_table_length; i++) {
            annotationTable[i] = AnnotationEntry.read(input, constantPool, isRuntimeVisible);
        }
    }

    /**
     * Called by objects that are traversing the nodes of the tree implicitely defined by the contents of a Java class.
     * I.e., the hierarchy of methods, fields, attributes, etc. spawns a tree of objects.
     *
     * @param v Visitor object
     */
    @Override
    public void accept(final Visitor v) {
        v.visitAnnotation(this);
    }

    /**
     * returns the array of annotation entries in this annotation
     */
    public AnnotationEntry[] getAnnotationEntries() {
        return annotationTable;
    }

    /**
     * @return the number of annotation entries in this annotation
     */
    public final int getNumAnnotations() {
        if (annotationTable == null) {
            return 0;
        }
        return annotationTable.length;
    }

    public boolean isRuntimeVisible() {
        return isRuntimeVisible;
    }

    /**
     * @param annotationTable the entries to set in this annotation
     */
    public final void setAnnotationTable(final AnnotationEntry[] annotationTable) {
        this.annotationTable = annotationTable;
    }

    /**
     * @return String representation
     */
    @Override
    public final String toString() {
        final StringBuilder buf = new StringBuilder(Const.getAttributeName(getTag()));
        buf.append(":\n");
        for (int i = 0; i < annotationTable.length; i++) {
            buf.append("  ").append(annotationTable[i]);
            if (i < annotationTable.length - 1) {
                buf.append('\n');
            }
        }
        return buf.toString();
    }

    protected void writeAnnotations(final DataOutputStream dos) throws IOException {
        if (annotationTable == null) {
            return;
        }
        dos.writeShort(annotationTable.length);
        for (final AnnotationEntry element : annotationTable) {
            element.dump(dos);
        }
    }

}
