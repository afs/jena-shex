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
import org.apache.jena.atlas.lib.NotImplemented;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ReportItem;
import shex.ValidationContext;

public abstract class NodeConstraint extends ShapeExpression {
// Something that can only validates to one report.
    /*
     *  5.4 Node Constraints
     *  5.4.1 Semantics
     *  5.4.2 Node Kind Constraints
     *  5.4.3 Datatype Constraints
     *  5.4.4 XML Schema String Facet Constraints
     *  5.4.5 XML Schema Numeric Facet Constraints
     *  5.4.6 Values Constraint
     */

    @Override
    public void validate(ValidationContext vCxt, Node data) {
        ReportItem item = validateOne(vCxt, data);
        if ( item != null )
            vCxt.reportEntry(item);
    }

    // [shex] public abstract boolean validate(Node data) ;
    public ReportItem validateOne(ValidationContext vCxt, Node data) {
        throw new NotImplemented(this.getClass().getSimpleName()+".validate");
    }


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
