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

package org.apache.jena.shex;

import java.util.*;

import org.apache.jena.graph.Node;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.shex.expressions.TripleExpression;
import org.apache.jena.shex.sys.SysShex;

public class ShexShapes {

    private final ShexShape startShape;
    private final List<ShexShape> shapes;
    private final Map<Node, ShexShape> shapesMap;
    private final Map<Node, TripleExpression> tripleRefs;

    private ShexShapes shapesWithImports = null;

    private final PrefixMap prefixes;
    private final List<String> imports;

    public static ShexShapes shapes(PrefixMap prefixes, ShexShape startShape,
                                    List<ShexShape> shapes, List<String> imports,
                                    Map<Node, TripleExpression> tripleRefs) {
        shapes = new ArrayList<>(shapes);
        Map<Node, ShexShape> shapesMap = new LinkedHashMap<>();
        for ( ShexShape shape:  shapes) {
        if ( shape.getLabel() == null )
            System.err.println("No shape label");
        else
            shapesMap.put(shape.getLabel(), shape);
        }

        tripleRefs = new LinkedHashMap<>(tripleRefs);

        return new ShexShapes(prefixes, startShape, shapes, shapesMap, imports, tripleRefs);
    }

    /*package*/ ShexShapes(PrefixMap prefixes, ShexShape startShape, List<ShexShape> shapes, Map<Node, ShexShape> shapesMap, List<String> imports, Map<Node, TripleExpression> tripleRefMap) {
        // Start shape not in the map.
        this.prefixes = prefixes;
        this.startShape = startShape;
        this.shapes = shapes;
        this.shapesMap = shapesMap;
        this.imports = imports;
        this.tripleRefs = tripleRefMap;
    }

    //merge(ShexShapes...)
    // merge(List<ShexShapes>)

//    // Import - no start.
//    public ShexShapes x_asImport() {
//        // Remove START.
//        List<ShexShape> shapes2 = shapes.stream().filter(s->
//            ! SysShex.startNode.equals(s.getLabel())
//        ).collect(Collectors.toList());
//        // No imports.
//        return new ShexShapes(this.prefixes, shapes2, null, tripleRefs);
//    }

//    private void addShape(ShexShape shape) {
//        shapes.add(shape);
//        if ( shape.getLabel() == null )
//            System.err.println("No shape label");
//        else
//            shapesMap.put(shape.getLabel(), shape);
//    }

    /*
     * Get START shape.
     * <p>
     * Returns null when there is no START shape.
     */
    public ShexShape getStart() {
        return startShape;
    }

    /** Get all the shapes. This includes the start shape, which has label {@link SysShex#startNode}. */
    public List<ShexShape> getShapes() {
        return shapes;
    }

    /** Get all the shapes. This includes the start shape, which has label {@link SysShex#startNode}. */
    public TripleExpression getTripleExpression(Node label) {
        return tripleRefs.get(label);
    }

    public boolean hasImports() {
        return imports != null && ! imports.isEmpty();
    }

    public List<String> getImports() {
        return imports;
    }

    /**
     * Import form of this ShexShape collection.
     * This involves removing the START reference.
     */
    public ShexShapes importsClosure() {
        if ( shapesWithImports != null )
            return shapesWithImports;
        if ( imports == null || imports.isEmpty() )
            return this;
        synchronized(this) {
            if ( shapesWithImports != null )
                return shapesWithImports;

            // Lost the name of this set of shapes.
            // In a cyclic import, including this set of shapes, we will import self again.
            // Harmless.

            Set<String> importsVisited = new HashSet<>();
            List<ShexShapes> others = new ArrayList<>();
            others.add(this);

            closure(imports, importsVisited, others);

            // Calculate the merge
            List<ShexShape> mergedShapes = new ArrayList<>();
            Map<Node, ShexShape> mergedShapesMap = new LinkedHashMap<>();
            Map<Node, TripleExpression> mergedTripleRefs = new LinkedHashMap<>();

            mergeOne(this, mergedShapes, mergedShapesMap, mergedTripleRefs);
            for ( ShexShapes importedSchema : others ) {
                mergeOne(importedSchema, mergedShapes, mergedShapesMap, mergedTripleRefs);
            }
            //mergedShapesMap.remove(SysShex.startNode);
            shapesWithImports = new ShexShapes(prefixes, startShape, mergedShapes, mergedShapesMap, null, mergedTripleRefs);
            return shapesWithImports;
        }
    }

    private static void mergeOne(ShexShapes schema,
                                 List<ShexShape> mergedShapes,
                                 Map<Node, ShexShape> mergedShapesMap,
                                 Map<Node, TripleExpression> mergedTripleRefs
                                 ) {
        // Without start shape.
        schema.getShapes().stream().filter(sh->!SysShex.startNode.equals(sh.getLabel())).forEach(shape->{
            mergedShapes.add(shape);
            mergedShapesMap.put(shape.getLabel(), shape);
        });
        mergedTripleRefs.putAll(schema.tripleRefs);
    }

    private static void closure(List<String> imports, Set<String> importsVisited, List<ShexShapes> visited) {
        if ( imports == null || imports.isEmpty() )
            return;
        for ( String imp : imports ) {
            if ( importsVisited.contains(imp) )
                continue;
            importsVisited.add(imp);
            ShexShapes others = Shex.readShapes(imp);
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
