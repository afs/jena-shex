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

package dev;

public class NotesShex {
    // Grammar. Check file for ToDo and other notes.
    // Test Suite

    // [x] ShexMap
    //     JSON form (required by tests)
    // [x] Syntax tests - convert to runner. includes and excludes.
    // [ ] Unlink from SHACL :: PLib
    // [ ] resync tests

    // ShexParser map operation to Shex.*

    // [ ] Better imports - keep individual read in ShexShapes.
    //     Parse to a list of ShexShapes, filter starts out when making the closure. Avoid churning maps.
    //     Fix up PLib.print

    // == Part 1

    // [ ] ShapesMap
    // [ ] Shape validation report
    // [ ] Annotations
    // [ ] Semantic Actions

    // [ ] Schema requirements (5.7), including only one START
    //     START - and check only one.

    // "Note that ShEx uses a partitioning strategy to find a solution whereby
    // triples in the data are assigned to triple constraints in the schema."

    // == Part 2

    // [ ] Extension "MAP{}" -- including via imports. TARGET { ... }
    // [ ] Compact writer
    // [ ] ShexJ, ShexR

    // [ ] Validation debug
    // [ ] test for XSDFuncOp.isNumberic??
    // [ ] test for ShexParserlib.unescapeShexRegex


    // https://github.com/shexSpec/shex/wiki/Shape-Validation-Algorithm
    // https://github.com/hsolbrig/PyShEx/blob/master/pyshex/shape_expressions_language/p5_5_shapes_and_triple_expressions.py

    // [ ] Unicode [\uD800-\uDB7F][\uDC00-\uDFFF]

    // JUnit5? @TestFactory

    // --- Nesting and structure
    // [ ] Schema Requirements
    // [ ] Terminology
    // [ ] Annotations
    // [ ] Semantic actions
    // [ ] ShapeExpression -> ShapeExpr
    // [ ] ShexConstraint-> ShexExpression
}
