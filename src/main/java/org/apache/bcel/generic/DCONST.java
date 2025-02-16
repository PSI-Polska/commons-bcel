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
package org.apache.bcel.generic;

/**
 * DCONST - Push 0.0 or 1.0, other values cause an exception
 *
 * <PRE>
 * Stack: ... -&gt; ...,
 * </PRE>
 *
 */
public class DCONST extends Instruction implements ConstantPushInstruction {

    private double value;

    /**
     * Empty constructor needed for Instruction.readInstruction. Not to be used otherwise.
     */
    DCONST() {
    }

    public DCONST(final double f) {
        super(org.apache.bcel.Const.DCONST_0, (short) 1);
        if (f == 0.0) {
            super.setOpcode(org.apache.bcel.Const.DCONST_0);
        } else if (f == 1.0) {
            super.setOpcode(org.apache.bcel.Const.DCONST_1);
        } else {
            throw new ClassGenException("DCONST can be used only for 0.0 and 1.0: " + f);
        }
        value = f;
    }

    /**
     * Call corresponding visitor method(s). The order is: Call visitor methods of implemented interfaces first, then call
     * methods according to the class hierarchy in descending order, i.e., the most specific visitXXX() call comes last.
     *
     * @param v Visitor object
     */
    @Override
    public void accept(final Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitDCONST(this);
    }

    /**
     * @return Type.DOUBLE
     */
    @Override
    public Type getType(final ConstantPoolGen cp) {
        return Type.DOUBLE;
    }

    @Override
    public Number getValue() {
        return Double.valueOf(value);
    }
}
