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
    // Grammar.
    // Test Suite

    // *** Fix parser around TripleExpression.
    // [-]  Shape (inlineShapeDefinition) and ShexShape (shapeExprDecl)
    //      Shape -> TripleExpression;
    //      ShexShape -> ShapeExpression

    // [ ] AST
    // [ ] Common triple expression - cardinality, semActs, annotation.
    // [x] TripleExpression
    // [x] EachOf (TripleExpressionEachOf)
    // [x] OneOf (TripleExpressionOneOf)
    // [ ] TripleConstraint
    // [x] tripleExprRef
    // [x] Adjust shex.jj for TripleExpression.
    // [ ] Whitespace: shacl/ValidationReport.java

    // **
    // [x] TripleExpressionGroup -> TripleExpression.
    // [ ] Create TripleExpression()
    // [ ] TripleExpression.validate
    // [ ] TripleConstraint is not a ShapeExpression.
    //     NOT ShapeExperssions in the same way?
    // [ ] Need different AND OR for TripleExpression? "OneOf" and "EachOf"
    //     Probably not.

    // [ ] Better print of ADD,OR print index?

    // [ ] Default cardinality of {1}
    // [ ] Validation Context - current Triple constraint?
    //     Clear going through a ref or inner shape.
    //     Or path from start shape?
    // [ ] Unclear cardinality - applies to triple or to triple and contraints (TripleConstraint)
    // [ ] ReportItem to include shape.
    // [ ] ShexValidationReport
    // [ ] ReportItem gathering.
    //     AND, tripleConstraint
    // [ ] Print - {} at wrong point.
    // [x] Shapes -> getPrefixMap();
    // [x] TripleConstaint is a single ShapeExpressionAnd


    // [ ] ShapeMap
    // [ ] 1literalPattern_with_all_punctuation -- escape bug? And then 1literalPattern_with_REGEXP_escapes_escaped

    // [x] Grammar wrong for value sets.
    // [x] Bad syntax tests
    // [ ] Record start line/column of a value set or range.
    // [x] Bad syntax: ["string"@1]

    // [ ] Unicode [\uD800-\uDB7F][\uDC00-\uDFFF]

    // [ ] Print OR broken?
    // [x] Cardinality {0}
    // [x] ShapeAtom DOT
    // [x] ShapeExpressionEmpty() -- marker.
    // [x] Facets
    //     [x] FacetString
    //     [x] FacetNumeric
    // [x] RegexConstraint - regex escaping.

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

    // [x] Unescaping. Support \U
    //     [x] Strings, lexical
    //     [x] IRIs
}
