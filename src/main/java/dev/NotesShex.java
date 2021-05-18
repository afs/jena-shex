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

    // [ ] ShexMap
    // [ ] Move EachOf prework to class.
    // [ ] Improve ShapeEval.
    // [ ] Better imports - keep individual read in ShexShapes.
    //     Create for import (no "=start=")
    //     Fix up PLib.print

    // [ ] Schema requirements (5.7), including only one START
    //     START - and check only one.
    // [ ] Check no dependency on "if ( .conforms)"

    // [ ] Matches Set<Triple> calc in?
    // [ ] tripleConstraint on one triple - boolean return.
    //     Caller assembles

    // "Note that ShEx uses a partitioning strategy to find a solution whereby triples in the data are assigned to triple constraints in the schema."

    // [ ] Validation debug
    // [x] NOT is wrong. Only flags for last - related to next?
    // [x] Does ShapeAND reverse order due to stack? TripleExpressionEachOf?
    // [ ] test for XSDFuncOp.isNumberic??
    // [ ] test for ShexParserlib.unescapeShexRegex

    // [ ] ShapesMap
    // [ ] ShapeReport
    // [ ] START

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
