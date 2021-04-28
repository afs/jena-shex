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

    // [ ] Grammar wrong for value sets.
    /*
[48]    valueSet               ::=         '[' valueSetValue* ']'
[49]    valueSetValue          ::=         iriRange | literalRange | languageRange | exclusion+

[50]    exclusion              ::=         '-' (iri | literal | LANGTAG) '~'?

[51]    iriRange               ::=         iri ('~' iriExclusion*)?
[52]    iriExclusion           ::=         '-' iri '~'?

[53]    literalRange           ::=         literal ('~' literalExclusion*)?
[54]    literalExclusion       ::=         '-' literal '~'?

[55]    languageRange          ::=         LANGTAG ('~' languageExclusion*)? | '@' '~' languageExclusion*
[56]    languageExclusion      ::=         '-' LANGTAG '~'?

     */

    // [ ] Print
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
