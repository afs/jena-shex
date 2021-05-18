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

import java.util.*;
import java.util.stream.Collectors;

import org.apache.jena.graph.Node;
import org.apache.jena.riot.system.PrefixMap;

public class ShexShapes {

    private final List<ShexShape> shapes = new ArrayList<>();
    private final Map<Node, ShexShape> shapesMap = new LinkedHashMap<>();

    private ShexShapes shapesWithImports = null;

    private final PrefixMap prefixes;
    private final List<String> imports;

    public ShexShapes(PrefixMap prefixes, List<ShexShape> shapes, List<String> imports) {
        this.prefixes = prefixes;
        shapes.forEach(this::addShape);
        this.imports = imports;
    }

    // Import - no start.
    // [shex] Better way?
    public ShexShapes asImport() {
        ShexShape x = shapesMap.remove(SysShex.startNode);
        shapes.remove(x);
        return this;
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

    public boolean hasImports() {
        return imports != null && ! imports.isEmpty();
    }

    public List<String> getImports() {
        return imports;
    }


    public ShexShapes withImports() {
        if ( shapesWithImports != null )
            return shapesWithImports;

        if ( imports == null || imports.isEmpty() )
            return this;
        // Insert self (if known).
        Set<String> importsVisited = new HashSet<>();
        List<ShexShapes> others = new ArrayList<>();
        others.add(this);

        closure(imports, importsVisited, others);

        List<ShexShape> allShapes =
                others.stream().flatMap(ss->ss.getShapes().stream()).collect(Collectors.toList());
        shapesWithImports = new ShexShapes(prefixes, allShapes, null);
        return shapesWithImports;
    }

    private static void closure(List<String> imports, Set<String> importsVisited, List<ShexShapes> visited) {
        if ( imports == null || imports.isEmpty() )
            return;
        for ( String imp : imports ) {
            if ( importsVisited.contains(imp) )
                continue;
            importsVisited.add(imp);
            ShexShapes others = Shex.readShapes(imp).asImport();
            visited.add(others);
            closure(others.imports, importsVisited, visited);
        }
    }

    public ShexShape get(Node n) {
        return shapesMap.get(n);
    }

    public boolean hasShape(Node n) {
        return shapesMap.containsKey(n);
    }

    public PrefixMap getPrefixMap() { return prefixes; }
}
