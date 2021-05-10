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

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.other.G;
import org.apache.jena.util.iterator.ExtendedIterator;
import shex.ValidationContext;

public class ShapeEval {

    public static boolean matchesShape(ValidationContext vCxt, Node node, TripleExpression tripleExpr, boolean closed) {

//    //  tripleExpr.validate;
//    //  tripleExpr.cardinality;
//      // [shex] .matches
//      if ( tripleExpr instanceof TripleExpressionEachOf ) {}
//      else if ( tripleExpr instanceof TripleExpressionOneOf ) {}
//      else if ( tripleExpr instanceof TripleExpressionRef ) {}
//      else if ( tripleExpr instanceof TripleExpressionDecoration ) {}
//      else if ( tripleExpr instanceof TripleConstraint ) {}
//      else
//          throw new NotImplemented();

        // Find all TripleConstraints.

        Set<Triple> neigh = new HashSet<>();
        arcsOut(neigh, vCxt.getData(), node);
        arcsIn(neigh, vCxt.getData(), node);

        Set<Node> predicates = findPredicates(tripleExpr);

        if ( tripleExpr != null ) {
            Set<Triple> x = tripleExpr.matches(vCxt, node);
        }
        return vCxt.conforms();
    }

    // Multimap?.
    private static Set<Node> findPredicates(TripleExpression tripleExpr) {
        Set<Node> predicates = new HashSet<>();
        new TripleExpressionVisitor() {
            @Override
            public void visit(TripleConstraint object) {
                predicates .add(object.getPredicate());
            }
        };

        return predicates;
    }

    private static void arcsOut(Set<Triple> neigh, Graph graph, Node node) {
        ExtendedIterator<Triple> x = G.find(graph, node, null, null);
        x.forEach(neigh::add);
    }

    private static void arcsIn(Set<Triple> neigh, Graph graph, Node node) {
        ExtendedIterator<Triple> x = G.find(graph, null, null, node);
        x.forEach(neigh::add);
    }
}