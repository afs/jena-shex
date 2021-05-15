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

import java.util.Objects;
import java.util.Set;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ValidationContext;

/** Class to add cardinality to a {@link TripleExpression}. */
public class TripleExpressionDecoration extends TripleExpression {

    private final TripleExpression other;

    public TripleExpressionDecoration(TripleExpression tripleExpr, Cardinality cardinality) {
        super(cardinality);
        this.other = tripleExpr;
    }

//    @Override
//    public void validate(ValidationContext vCxt, Node data) {
//        throw new NotImplemented(this.getClass().getSimpleName()+".validate");
//    }

    @Override
    public Set<Triple> matches(ValidationContext vCxt, Node data) {
        Set<Triple> x = other.matches(vCxt, data);
        return x;
    }

    @Override
    public void visit(TripleExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(other);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        TripleExpressionDecoration other = (TripleExpressionDecoration)obj;
        return Objects.equals(this.other, other.other);
    }

    @Override
    public void print(IndentedWriter iOut, NodeFormatter nFmt) {
        String s = super.cardinalityString();
        iOut.println("Other");
        iOut.incIndent();
        other.print(iOut, nFmt);
        iOut.decIndent();
        if ( ! s.isEmpty() )
            iOut.println("Cardinality = "+s);
        iOut.println("/Other");
    }

    @Override
    public String toString() {
        String s = super.cardinalityString();
        if ( s.isEmpty() )
            return "TripleExpression [other="+other+"]";
        return "TripleExpression ["+s+" other="+other+"]";
    }
}
