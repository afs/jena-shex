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

package org.apache.jena.shex.sys;

import org.apache.jena.shex.expressions.*;

public class ShexLib {
    public static String fragment(String uri) {
        int idx = uri.indexOf('#');
        if ( idx < 0 )
            return "";
        return uri.substring(idx);
    }

    public static void walk(ShapeExpression shExpr, ShapeExpressionVisitor beforeVisitor, ShapeExpressionVisitor afterVisitor) {
        ShapeExpressionWalker walker = new ShapeExpressionWalker(beforeVisitor, afterVisitor, null, null);
        shExpr.visit(walker);
    }

    public static void walk(ShapeExpression shExpr,
                            ShapeExpressionVisitor beforeVisitor, ShapeExpressionVisitor afterVisitor,
                            TripleExpressionVisitor beforeTripleExpressionVisitor, TripleExpressionVisitor afterTripleExpressionVisitor
                            ) {
        ShapeExpressionWalker walker = new ShapeExpressionWalker(beforeVisitor, afterVisitor,
                                                                 beforeTripleExpressionVisitor,
                                                                 afterTripleExpressionVisitor);
        shExpr.visit(walker);
    }
}
