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

    // == Initial Jena commit

    // [ ] Reporting
    // [ ] Rename ValidationReport -> ShexReport -> map for { shape, node, error } subclass of ShexShapeMap
    // [x] Example(s)
    // [x] cmds, but printing could be better
    // [x] ShEx vs Shex Shex for classes. ShExC, ShExJ, ShExR for languages.
    // [ ] ShexShapes -> ShexSchema
    // [ ] ShapesMap -> ShapeMap
    // [ ] Unlink from SHACL :: PLib
    // [x] Shex.parseShapesMapJson-> ShExJ

    // == Part 1

    // [ ] EachOf partition generation produce duplicates. -> algorithm H!
    // [ ] negativeStructure tests
    // [ ] Schema requirements (5.7), including only one START
    //     START - and check only one.

    // [SHACL] Imports validation?
    //         Different - done as owl:imports closure then parse all. Default - no.
    //         What about SHACLC?
    //         How does this happen in validation?

    //     Currently:
    //         shapes = shapes.importsClosure(); <-- include in SHACL.

    // [x] ShapesMap
    // [ ] Annotations (?)
    // [ ] Semantic Actions

    // == Part 2

    // [ ] Negative Structure

    // [ ] Extension "MAP{}" -- including via imports. TARGET { ... }
    // [ ] Compact writer
    // [ ] ShExJ, ShExR

    // [ ] Validation debug
    // [ ] test for XSDFuncOp.isNumeric??
    // [ ] test for ShexParserlib.unescapeShexRegex

    // https://github.com/hsolbrig/PyShEx/blob/master/pyshex/shape_expressions_language/p5_5_shapes_and_triple_expressions.py

    // [ ] Unicode [\uD800-\uDB7F][\uDC00-\uDFFF]

    // JUnit5? @TestFactory
}
