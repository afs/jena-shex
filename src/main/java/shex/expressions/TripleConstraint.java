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

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.other.G;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ValidationContext;

// [shex] Not a ShapeExpression per se - part of a TripleExpression.
public class TripleConstraint extends TripleExpression {

    /*
    TripleConstraint {
        id:tripleExprLabel?
        inverse:BOOL?
        predicate:IRIREF
        valueExpr:shapeExpr?
        min:INTEGER?
        max:INTEGER?
        semActs:[SemAct+]?
        annotations:[Annotation+]?
    }
     */

    private final Node predicate;
    // [shex] Move to a block of constraints object
    private final ShapeExpression shapeExpression;
    private final boolean reverse;

    public TripleConstraint(Node predicate, boolean reverse, ShapeExpression arg, Cardinality cardinality) {
        super(cardinality);
        this.predicate = predicate;
        this.reverse = reverse;
        this.shapeExpression = arg;
    }

//    @Override
//    public void validate(ValidationContext vCxt, Node data) {
//        Graph graph = vCxt.getData();
//        List<Node> objects = G.listSP(graph, data, predicate);
//        for ( Node obj : objects )
//            shapeExpression.validate(vCxt, obj);
//    }

    public Node getPredicate() {
        return predicate;
    }

    @Override
    public Set<Triple> matches(ValidationContext vCxt, Node node) {
        Graph graph = vCxt.getData();
//        Set<Triple> x =
//                reverse
//                ? G.find(graph, null, predicate, node).toSet()
//                : G.find(graph, node, predicate, null).toSet();
        Collection<Node> values = reverse
                ? G.listPO(graph, predicate, node)
                : G.listSP(graph, node, predicate);
        int countGood = 0;
        int countBad = 0;
        for ( Node n : values ) {
            shapeExpression.validate(vCxt, n);
        }

        return null;
    }

    @Override
    public void visit(TripleExpressionVisitor visitor) {
        visitor.visit(this);
    }

    //    @Override
    //    public void validate(ValidationContext vCxt, Node data) {
    //        Graph graph = vCxt.getData();
    //        List<Node> objects = G.listSP(graph, data, predicate);
    //        for ( Node obj : objects )
    //            shapeExpression.validate(vCxt, obj);
    //    }

        @Override
        public void print(IndentedWriter iOut, NodeFormatter nFmt) {
            iOut.println("TripleConstraint {");
            iOut.incIndent();
            iOut.printf("predicate = ");
            if ( reverse )
                iOut.print("^");
            nFmt.format(iOut, predicate);
            iOut.println();
            shapeExpression.print(iOut, nFmt);
            if ( cardinality != null ) {
                //iOut.print(cardinality.image);
                if ( cardinality.image.length() != 1 )
                    iOut.printf("{%d,%d}", min, max);
                else
                    iOut.printf(cardinality.image);
                iOut.println();
            }
            iOut.decIndent();
            iOut.println("}");
        }

    @Override
    public int hashCode() {
        return Objects.hash(max, min, predicate, reverse, shapeExpression);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        TripleConstraint other = (TripleConstraint)obj;
        return max == other.max && min == other.min && Objects.equals(predicate, other.predicate) && reverse == other.reverse
               && Objects.equals(shapeExpression, other.shapeExpression);
    }

    @Override
    public String toString() {
        String cardStr = "";
        if ( ! cardinalityString().isEmpty() )
            cardStr = "cardinality="+cardinalityString()+", ";
        return "TripleConstraint [predicate=" + predicate + ", "+cardStr+"shapeExpr=" + shapeExpression + "]";
    }

}
