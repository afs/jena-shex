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

import java.util.Map;

import org.apache.jena.atlas.lib.NotImplemented;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;

public class ShexValidation {


    // Section: 5.2 Validation Definition
    // http://shex.io/shex-semantics/index.html#validation
    // pair (n, sl) in ism, (n -- "RDFnode") (sl - "Shape Label")

    public static boolean isValid(Graph graph, ShexSchema schema, ShexMap ism) {
        throw new NotImplemented();
    }

    public static boolean satisfies(Node n, ShexShape shape, Graph data, ShexSchema schema, Map<Node, ShexShape> typing) {
        throw new NotImplemented();
    }

    // Term Constraint
    public static boolean isValid(Node n, ShexShape shape, Graph data, ShexSchema schema, Map<Node, ShexShape> typing) {
        throw new NotImplemented();
    }
}
