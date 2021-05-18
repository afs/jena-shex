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

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ReportItem;
import shex.ValidationContext;

public abstract class NodeConstraint extends ShapeExpression {
    /*
     *  5.4 Node Constraints
     *  5.4.1 Semantics
     *  5.4.2 Node Kind Constraints
     *  5.4.3 Datatype Constraints
     *  5.4.4 XML Schema String Facet Constraints
     *  5.4.5 XML Schema Numeric Facet Constraints
     *  5.4.6 Values Constraint
     */

    // [shex] Convert to record
    /*
     * NodeConstraint  {
     *   OneOf:
     *   Node Kind Constraints
     *   Datatype Constraints
     *   XML Schema String Facet Constraints
     *   XML Schema Numeric Facet Constraints
     *   Values Constraints
     */

     // [shex] Convert to (builder):
     /*  NodeConstraint   {
     *    id:shapeExprLabel?
     *    nodeKind:("iri" | "bnode" | "nonliteral" | "literal")?
     *    datatype:IRIREF?
     *    xsFacet*
     *    values:[valueSetValue+]?
     * }
     */

    @Override
    public boolean satisfies(ValidationContext vCxt, Node data) {
        ReportItem item = nodeSatisfies(vCxt, data);
        if ( item != null ) {
            vCxt.reportEntry(item);
            return false;
        }
        return true;
    }

    /** The function "nodeSatisfies" == satisfies2(n, nc)*/
    public abstract ReportItem nodeSatisfies(ValidationContext vCxt, Node data);


    @Override
    public void print(IndentedWriter out, NodeFormatter nFmt) {
        out.println(toString());
    }

//    @Override
//    public abstract int hashCode();
//
//    @Override
//    public abstract boolean equals(Object other);
//
//    @Override
//    public abstract String toString();
}
