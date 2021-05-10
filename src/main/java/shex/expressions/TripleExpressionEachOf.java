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
import java.util.Objects;
import java.util.Set;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ValidationContext;

public class TripleExpressionEachOf extends TripleExpression {

    public static TripleExpression create(List<TripleExpression> acc) {
        if ( acc.size() == 0 )
            throw new InternalErrorException("Empty list");
        if ( acc.size() == 1 )
            return acc.get(0);
        return new TripleExpressionEachOf(acc);
    }

    private List<TripleExpression> tripleExpressions;

    private TripleExpressionEachOf(List<TripleExpression> expressions) {
        super(null);
        this.tripleExpressions = expressions;
    }

    public List<TripleExpression> expressions() {
        return tripleExpressions;
    }

    @Override
    public Set<Triple> matches(ValidationContext vCxt, Node data) {
        return null;
    }

    @Override
    public void visit(TripleExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(3, tripleExpressions);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        TripleExpressionEachOf other = (TripleExpressionEachOf)obj;
        return Objects.equals(tripleExpressions, other.tripleExpressions);
    }

    @Override
        public void print(IndentedWriter iOut, NodeFormatter nFmt) {
    //        iOut.println("EachOf");
    //        iOut.incIndent();
    //        expressions().forEach(tExpr->tExpr.print(iOut, nFmt));
    //        iOut.decIndent();
    //        iOut.println("/EachOf");

            iOut.println("EachOf");
            iOut.incIndent();
            int idx = 0;
            for ( TripleExpression tExpr : tripleExpressions ) {
                idx++;
                iOut.printf("%d : ", idx);
                tExpr.print(iOut, nFmt);
            }
            iOut.decIndent();
            iOut.println("/EachOf");
        }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"["+expressions()+"]";
    }
}
