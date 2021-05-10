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

package shex.expressions;

import java.util.*;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ValidationContext;

// Shape
public class ShapeTripleExpression extends ShapeExpression {
    // [shex] This is the inlineShapeDefinition
    // Can we combine with a top-level ShexShape?

    /*
    Shape {
        id:shapeExprLabel?
        closed:BOOL?
        extra:[IRIREF]?
        expression:tripleExpr?
        semActs:[SemAct+]?
        annotations:[Annotation+]? }
    */

    private Node label;
    private Set<Node> extras;
    private boolean closed;
    //extra:[IRIREF]?
    private TripleExpression tripleExpr;
    //semActs:[SemAct+]?
    //annotations:[Annotation+]?

    public static Builder newBuilder() { return new Builder(); }

    private ShapeTripleExpression(Node label, Set<Node> extras, boolean closed, TripleExpression tripleExpr) {
        super();
        this.label = label;
        if ( extras == null || extras.isEmpty() )
            this.extras = null;
        else
            this.extras = extras;
        this.closed = closed;
        this.tripleExpr = tripleExpr;
    }

    @Override
    public boolean validate(ValidationContext vCxt, Node node) {
        return ShapeEval.matchesShape(vCxt, node, tripleExpr, closed);
    }

    @Override
    public void print(IndentedWriter iOut, NodeFormatter nFmt) {
        iOut.print("Shape");
        if ( label != null ) {
            iOut.print(" ");
            nFmt.format(iOut,  label);
        }
        iOut.println();
        iOut.incIndent();
        if ( closed )
            iOut.println("CLOSED");
        iOut.println("TripleExpression");
        iOut.incIndent();
        tripleExpr.print(iOut, nFmt);
        iOut.decIndent();
        iOut.decIndent();
    }

    @Override
    public int hashCode() {
        return Objects.hash(closed, label, tripleExpr);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ShapeTripleExpression other = (ShapeTripleExpression)obj;
        return closed == other.closed && Objects.equals(label, other.label) && Objects.equals(tripleExpr, other.tripleExpr);
    }

    @Override
    public String toString() {
        return "Shape: "+((label==null)?"":label);
    }

    public static class Builder {
        private Node label;
        private Set<Node> extras = null;
        private Optional<Boolean> closed = null;
        //extra:[IRIREF]?
        private TripleExpression tripleExpr = null;
        //semActs:[SemAct+]?
        //annotations:[Annotation+]?

        Builder() {}

        public Builder label(Node label) { this.label = label ; return this; }

        public Builder extras(List<Node> extrasList) {
            if ( extras == null )
                extras = new HashSet<>();
            this.extras.addAll(extrasList);
            return this;
        }

        public Builder closed(boolean value) { this.closed = Optional.of(value); return this; }

        public Builder shapeExpr(TripleExpression tripleExpr) { this.tripleExpr = tripleExpr; return this; }

        public ShapeTripleExpression build() {
            boolean isClosed = (closed == null) ? false : closed.get();
            return new ShapeTripleExpression(label, extras, isClosed, tripleExpr);
        }
    }
}
