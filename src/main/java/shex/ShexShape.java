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

package shex;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;
import shex.expressions.ShapeExpression;

public class ShexShape {
    private final Node label;
    private ShapeExpression shExpression;

    // [shex] Future : builder.
    public ShexShape(Node label) {
        this.label = label;
    }

    public Node getLabel() { return label; }

    public void setShapeExpression(ShapeExpression shExpression) { this.shExpression = shExpression; }
    public ShapeExpression getShapeExpression() { return shExpression; }

    public void print(IndentedWriter iOut, NodeFormatter nFmt) {
        iOut.printf("Shape: %s\n", getLabel());
        // [shex] Closed
        iOut.incIndent();
        getShapeExpression().print(iOut, nFmt);
//
//        List<NodeConstraint> x = getShapeExpression().getNodeConstraints();
//        x.forEach(c -> {
//            iOut.print("Node constraint:");
//            c.print(iOut, nFmt);
//            iOut.println();
//        });
//        List<Constraint> y = getShapeExpression().getTripleConstraints();
//        y.forEach(c->{
//            if ( c instanceof TripleConstraint ) {
//                TripleConstraint tc = (TripleConstraint)c;
//                tc.print(iOut, nFmt);
////            } else if ( c instanceof ShapeExpression ) {
////                ShapeExpression shExpr = (ShapeExpression)c;
////                iOut.printf("ShapeExpression: %s\n", shExpr);
//            } else {
//                iOut.printf("Constraint: %s\n",c);
//            }
//        });
        iOut.decIndent();
    }

    @Override
    public String toString() {
        return "ShexShape [label="+label+" expr="+shExpression+"]";
    }
}
