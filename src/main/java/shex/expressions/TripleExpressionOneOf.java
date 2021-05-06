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

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.riot.out.NodeFormatter;

public class TripleExpressionOneOf extends TripleExpression {

    public static TripleExpression create(List<TripleExpression> acc) {
        if ( acc.size() == 0 )
            throw new InternalErrorException("Empty list");
        if ( acc.size() == 1 )
            return acc.get(0);
        return new TripleExpressionOneOf(acc);
    }

    private List<TripleExpression> tripleExpression;

    private TripleExpressionOneOf(List<TripleExpression> expressions) {
        this.tripleExpression = expressions;
    }

    public List<TripleExpression> expressions() {
        return tripleExpression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(4, tripleExpression);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        TripleExpressionOneOf other = (TripleExpressionOneOf)obj;
        return Objects.equals(tripleExpression, other.tripleExpression);
    }

    @Override
    public void print(IndentedWriter iOut, NodeFormatter nFmt) {
//        iOut.println("OneOf");
//        iOut.incIndent();
//        expressions().forEach(tExpr->tExpr.print(iOut, nFmt));
//        iOut.decIndent();
//        iOut.println("/OneOf");

        iOut.println("OneOf");
        int idx = 0;
        for ( TripleExpression tExpr : tripleExpression ) {
            idx++;
            iOut.printf("%d - ", idx);
            tExpr.print(iOut, nFmt);
        }
        iOut.println("/OneOf");
    }
}
