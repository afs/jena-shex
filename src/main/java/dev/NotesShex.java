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

    // [ ] Validation debug (includes only).
    // [ ] Does ShapeAND reverse order due to stack? TripleExpressionEachOf?
    // [ ] test for XSDFuncOp.isNumberic??
    // [ ] test for ShexParserlib.unescapeShexRegex

    // *** Fix parser around TripleExpression.
    // [-]  Shape (inlineShapeDefinition) and ShexShape (shapeExprDecl)
    //      Shape -> TripleExpression;
    //      ShexShape -> ShapeExpression
    // [ ] Values "Current shape"

    // Convert TestShexBadSyntax and testShesSyntax to runners?
    // "files/spec/negativeSyntax"
    // "files/spec/syntax"

    // [ ] AST
    // [ ] Common triple expression - cardinality, semActs, annotation.
    // [ ] TripleConstraint

    // **

    // [ ] Better print of ADD,OR print index?


    // [ ] ShapeMap

    // [ ] Unicode [\uD800-\uDB7F][\uDC00-\uDFFF]

    // [ ] Value Sets
    // [ ] TripleConstraint printing.
    // [ ] Consolidate NumberFacet. NumLength, NumRange to avoid NodeValue creation
    // [ ] Consolidate String facet. minlength to avoid NodeFunctions.str(n); (less important)

    // [ ] ShapeMap parser (same file, different entry point : shex.jj)

    // JUnit5? @TestFactory

    // --- Nesting and structure
    // [ ] Schema Requirements
    // [ ] Terminology
    // [ ] Annotations
    // [ ] Semantic actions
    // [ ] ShapeExpression -> ShapeExpr
    // [ ] ShexConstraint-> ShexExpression
}
