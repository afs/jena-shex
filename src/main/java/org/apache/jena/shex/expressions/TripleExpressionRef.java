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

import java.util.Map;
import java.util.Objects;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;

public class TripleExpressionRef extends TripleExpression {

    private Node ref;
    private Map<Node, TripleExpression> map;

    public TripleExpressionRef(Map<Node, TripleExpression> tripleExprRefs, Node node) {
        super();
        this.map = tripleExprRefs;
        this.ref = node;
    }

    public TripleExpression get() {
        return map.get(ref);
    }

    @Override
    public void visit(TripleExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        TripleExpressionRef other = (TripleExpressionRef)obj;
        return Objects.equals(ref, other.ref);
    }

    @Override
    public void print(IndentedWriter iOut, NodeFormatter nFmt) {
        iOut.print("tripleExprRef: ");
        nFmt.format(iOut, ref);
        iOut.println();
    }

    @Override
    public String toString() {
        return "TripleExpressionRef["+PLib.displayStr(ref)+"]";
    }
}