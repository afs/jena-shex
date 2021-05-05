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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Node;
import org.apache.jena.riot.system.PrefixMap;

public class ShexShapes {
    private final List<ShexShape> shapes = new ArrayList<>();
    private final Map<Node, ShexShape> shapesMap = new HashMap<>();
    private final PrefixMap prefixes;

    public ShexShapes(PrefixMap prefixes, List<ShexShape> shapes) {
        this.prefixes = prefixes;
        shapes.forEach(this::addShape);
    }

    private void addShape(ShexShape shape) {
        shapes.add(shape);
        if ( shape.getLabel() == null )
            System.err.println("No shape label");
        else
            shapesMap.put(shape.getLabel(), shape);
    }

    public List<ShexShape> getShapes() {
        return shapes;
    }

    public ShexShape get(Node n) {
        return shapesMap.get(n);
    }

    public PrefixMap getPrefixMap() { return prefixes; }
}
