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

import java.util.List;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.NotImplemented;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ReportItem;
import shex.ValidationContext;

public class TripleConstraint extends ShapeExpression {

    /*
     *   5.5 Shapes and Triple Expressions
     */

    private final Node predicate;
    // [shex] Move to a block of constraints object
    private final List<ShapeExpression> constraints;
    private final boolean reverse;

    // Unset values
    private int min = -1;
    private int max = -1;

    public TripleConstraint(Node predicate, boolean reverse, List<ShapeExpression> args) {
        this.predicate = predicate;
        this.reverse = reverse;
        this.constraints = args;
    }

    @Override
    public ReportItem validate(ValidationContext vCxt, Node data) {
        throw new NotImplemented(this.getClass().getSimpleName()+".validate");
    }

    @Override
    public void print(IndentedWriter out, NodeFormatter nFmt) {
        //out.println(toString());
        if ( reverse )
            out.print("^");
        out.print("<"+predicate+">");
        if ( constraints.isEmpty() ) {
            out.println("{ }");
            return;
        }
        out.println(" {");
        out.incIndent();
        constraints.forEach(c->c.print(out, nFmt));
        out.decIndent();
        out.println("}");
    }

    public Node getPredicate() {
        return predicate;
    }

    public void setCardinality(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String toString() {
        String cardinality = cardStr(min, max);
        if ( ! cardinality.isEmpty() )
            cardinality = "cardinality="+cardinality+", ";
        return "TripleConstraint [predicate=" + predicate + ", "+cardinality+"constraints=" + constraints + "]";
    }

    private String cardStr(int min, int max) {
        String cardinality="";
        if ( min != -1 || max != -1 ) {
            if ( min == 0 && max == -2 )
                cardinality = "*";
            else if ( min == 1 && max == -2 )
                cardinality = "+";
            else if ( min == 0 && max == 1 )
                cardinality = "?";
            else if ( max == -2 )
                cardinality = "{"+min+",*}";
            else {
                cardinality = "{"+cardStr(min);
                if ( max ==-1 )
                    cardinality = cardinality+"}";
                else
                    cardinality = cardinality+","+cardStr(max)+"}";
            }
        }
        return cardinality;
    }

    private String cardStr(int x) {
        if ( x == -1 )
            return "1";
        if ( x == -2 )
            return "*";
        return Integer.toString(x);
    }

}
