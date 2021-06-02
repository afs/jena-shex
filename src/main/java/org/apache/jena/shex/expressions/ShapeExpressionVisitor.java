/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.shex.expressions;

public interface ShapeExpressionVisitor {
    public default void visit(ShapeExpressionAND shape) {}
    public default void visit(ShapeExpressionOR shape) {}
    public default void visit(ShapeExpressionNOT shape) {}
    public default void visit(ShapeExpressionFalse shape) {}
    public default void visit(ShapeExpressionNone shape) {}
    public default void visit(ShapeExpressionRef shape) {}
    public default void visit(ShapeExpressionTrue shape) {}
    public default void visit(ShapeExpressionExternal shape) {}
    public default void visit(ShapeTripleExpression shape) {}

    public default void visit(StrRegexConstraint constraint) {}
    public default void visit(StrLengthConstraint constraint) {}
    public default void visit(DatatypeConstraint constraint) {}
    public default void visit(NodeKindConstraint constraint) {}
    public default void visit(NumLengthConstraint constraint) {}
    public default void visit(NumRangeConstraint constraint) {}
    public default void visit(ValueConstraint constraint) {}
}
