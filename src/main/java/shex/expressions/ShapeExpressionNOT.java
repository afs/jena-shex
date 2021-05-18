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

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ReportItem;
import shex.ValidationContext;

public class ShapeExpressionNOT extends ShapeExpression {

//    public static ShapeExpression create(List<ShapeExpression> args) {
//        if ( args.size() == 0 )
//            return null;
//        if ( args.size() != 1 )
//            throw new InternalErrorException("ShapeExpressionNOT.create");
//        ShapeExpression shapeExpr = args.get(0);
//        return new ShapeExpressionNOT(shapeExpr);
//    }
    private final ShapeExpression other;

    public ShapeExpressionNOT(ShapeExpression shapeExpression) {
        this.other = shapeExpression;
    }

    @Override
    public boolean satisfies(ValidationContext vCxt, Node data) {
        ValidationContext vCxt2 = ValidationContext.create(vCxt);
        boolean innerSatisfies = other.satisfies(vCxt2, data);
        if ( ! innerSatisfies )
            return true;
        ReportItem item = new ReportItem("NOT: Term reject because it conforms", data);
        vCxt.reportEntry(item);
        return false;
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
        ShapeExpressionNOT other = (ShapeExpressionNOT)obj;
        return Objects.equals(this.other, other.other);
    }

    @Override
    public void print(IndentedWriter out, NodeFormatter nFmt) {
        out.print("NOT ");
        other.print(out, nFmt);
    }
}
