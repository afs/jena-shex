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

package shex.parser;

import static org.apache.jena.riot.lang.extra.LangParserLib.unescapeStr;
import static shex.parser.ShExCompactParser.Inline.INLINE;
import static shex.parser.ShExCompactParser.Inline.NOT_INLINE;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import java.util.regex.Pattern;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.EscapeStr;
import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.lang.extra.LangParserBase;
import shex.ShexShape;
import shex.ShexShapes;
import shex.expressions.*;

/** ShExCompactParser */
public class ShExCompactParser extends LangParserBase {

    private IndentedWriter out;
    public static boolean DEBUG = false;
    public static boolean DEBUG_PARSE = false;
    public static boolean DEBUG_STACK = false;

    static enum Inline { INLINE, NOT_INLINE }

    public ShExCompactParser() {
        this.out = IndentedWriter.clone(IndentedWriter.stdout);
    }

    // -- parser state
    private List<ShexShape> shapes = new ArrayList<>();

    // The shape currently in progress.
    private ShexShape currentShexShape = null;
    // Stack of shape expressions used during parsing a top level shape.
    private Deque<ShapeExpression> shapeExprStack = new ArrayDeque<>();
    private ShapeExpression currentShapeExpression() { return peek(shapeExprStack); }

    private void printState() {
        if ( DEBUG ) {
            printStack("shapeExprStack", shapeExprStack);
            //printStack("tripleConstraints", tripleConstraints);
        }
    }

    // [shex] XXX Rename!
    protected Node createNodeIRI(String iriStr, int line, int column) {
        return super.createNode(iriStr, line, column);
    }

    private <T> void printStack(String string, Deque<T> stack) {
        System.out.printf("%s: %d: %s\n", string, stack.size(), stack);
    }

    // -- Parser

    public void parseStart() { }

    public ShexShapes parseFinish() {
        // Check stacks empty.
        if ( currentShexShape != null )
            throw new InternalErrorException("shape in-progress");
        if (! shapeExprStack.isEmpty() )
            throw new InternalErrorException("shape expresion stack not empty");
        if ( DEBUG )
            out.println();
        return new ShexShapes(shapes);
    }

    protected void imports(String iri, int line, int column) {
    }

    protected void startShexDoc() { }

    protected void finishShexDoc() { }

    // ---- One shape.

    // Start of top level shape, not "start=";
    protected void startShapeExprDecl() {
        start("ShapeExprDecl");
        startShapeExpressionTop();
    }

    protected void finishShapeExprDecl() {
        ShapeExpression sExpr = finishShapeExpressionTop();
        currentShexShape.setShapeExpression(sExpr);
        shapes.add(currentShexShape);
        currentShexShape = null;
        finish("ShapeExprDecl");
    }

    protected void shapeExprDecl(Node label, int line, int column) {
        debug("shape label: %s", label);
        currentShexShape = new ShexShape(label);
    }

    protected void shapeExternal() {
        debug("shape external");
    }

    // Start of top level shape, "start="
    protected void startStartClause() {
        start("StartClause");
        // [shex] Constant
        currentShexShape = new ShexShape(NodeFactory.createLiteral("=start="));
        startShapeExpressionTop();
    }

    protected void finishStartClause() {
        ShapeExpression sExpr = finishShapeExpressionTop();
        currentShexShape.setShapeExpression(sExpr);
        shapes.add(currentShexShape);
        currentShexShape = null;
        finish("StartClause");
    }

    // ---- Shape expressions

    private void startShapeExpressionTop() {
        start("startShapeExpressionTop");
        // Stack is empty.
        if ( DEBUG ) {
            if ( ! shapeExprStack.isEmpty() )
                debug("startShapeExpressionTop: Stack not empty");
        }
    }

    private ShapeExpression finishShapeExpressionTop() {
        if ( shapeExprStack.isEmpty() )
            return ShapeExpressionNone.get();

        ShapeExpression sExpr = pop(shapeExprStack);
        if ( DEBUG ) {
            if ( ! shapeExprStack.isEmpty() )
                debug("finishShapeExpressionTop: Stack not empty");
        }
        finish("finishShapeExpressionTop");
        return sExpr;
    }

    // ---- Shape structure

    private int startShapeOp() {
        return front(shapeExprStack);
    }

    // Do noting with the stack but pairs with startShapeOp
    private void finishShapeOpNoAction(String operation, int idx) { }

    private List<ShapeExpression> finishShapeOp(int idx) {
        return pop(shapeExprStack, idx);
    }

    private void finishShapeOp(int idx, Function<List<ShapeExpression>, ShapeExpression> action) {
        if ( action == null )
            return ;
        List<ShapeExpression> args = finishShapeOp(idx);
        if ( args == null )
            return ;
        finishShapeOp(args, action);
    }

    private void finishShapeOp(List<ShapeExpression> args, Function<List<ShapeExpression>, ShapeExpression> action) {
        if ( action != null ) {
            ShapeExpression sExpr = action.apply(args);
            if ( sExpr != null )
                push(shapeExprStack, sExpr);
        }
    }

    protected int startShapeExpression(Inline inline) {
        start(inline, "ShapeExpression");
        return startShapeOp();
    }

    protected void finishShapeExpression(Inline inline, int idx) {
        finishShapeOpNoAction("ShapeExpression", idx);
        finish(inline, "ShapeExpression");
    }

    protected int startShapeOr(Inline inline) {
        start(inline, "ShapeOr");
        return startShapeOp();
    }

    protected void finishShapeOr(Inline inline, int idx) {
        finishShapeOp(idx, ShapeExpressionOR::create);
        finish(inline, "ShapeOr");
    }

    protected int startShapeAnd(Inline inline) {
        start(inline, "ShapeAnd");
        return startShapeOp();
    }

    protected void finishShapeAnd(Inline inline, int idx) {
        finishShapeOp(idx, ShapeExpressionAND::create);
        finish(inline, "ShapeAnd");
    }

    protected int startShapeNot(Inline inline) {
        start(inline, "ShapeNot");
        return startShapeOp();
    }

    protected void finishShapeNot(Inline inline, int idx, boolean negate) {
        if ( negate && ! shapeExprStack.isEmpty() ) {
            ShapeExpression shExpr = pop(shapeExprStack);
            ShapeExpression shExpr2 = new ShapeExpressionNOT(shExpr);
            push(shapeExprStack, shExpr2);
        }
        finish(inline, "ShapeNot");
    }

    protected int startShapeAtom(Inline inline) {
        start(inline, "ShapeAtom");
        return startShapeOp();
    }

    protected void finishShapeAtom(Inline inline, int idx) {
        finishShapeOpNoAction("ShapeAtom", idx);
        finish(inline, "ShapeAtom");
    }

    protected void shapeAtomDOT() {
        debug("ShapeAtom <DOT>");
        push(shapeExprStack, new ShapeExpressionTrue());
    }

    protected void shapeReference(Node ref) {
        ShapeExpressionRef shapeRef = new ShapeExpressionRef(ref);
        push(shapeExprStack, shapeRef);
    }

    protected int startTripleExpressionGroup() {
        start("TripleExpressionGroup");
        return startShapeOp();
    }

    protected void finishTripleExpressionGroup(int idx) {
        finishShapeOp(idx, ShapeExpressionOR::create);
        finish("TripleExpressionGroup");
    }

    // ---- TripleExpression, TripleConstraint
    // The grammar naming is a bit odd : TripleExpression a block of triple constraints.
    // Each block is a tripleExpressionClause which is many TripleConstraints.
    // "TripleExpressionGroup" :: and-or hierarchy
    // "TripleExpressionClause" :: predicate-constraints block (conjunction)
    // "UnaryTripleExpr" :: individual tripleExpression.

    protected int startTripleExpressionClause() {
        start("TripleExpressionClause");
        return startShapeOp();
    }

    protected void finishTripleExpressionClause(int idx) {
        finishShapeOp(idx, ShapeExpressionAND::create);
        finish("TripleExpressionClause");
    }

    protected void startUnaryTripleExpr() {
        start("UnaryTripleExpression");
    }

    protected void finishUnaryTripleExpr() {
        finish("UnaryTripleExpression");
    }

    protected int startTripleConstraint() {
        start("TripleConstraint");
        // [shex] Now a flag now.
//        currentTripleConstraint = new TripleConstraint(null, null);
//        push(tripleConstraints, currentTripleConstraint);
        return startShapeOp();
    }

    protected void finishTripleConstraint(int idx, Node predicate, boolean reverse) {
        List<ShapeExpression> args = finishShapeOp(idx);
        if ( args != null ) {
            // Cardinality as argument.
            ShapeExpression shExpr = new TripleConstraint(predicate, reverse, args);
            push(shapeExprStack, shExpr);
        }
        finish("TripleConstraint");
    }

    // ---- Node Constraints.

    protected int startLiteralNodeConstraint(int line, int column) {
        start("LiteralNodeConstraint");
        return startShapeOp();
    }

    protected void finishLiteralNodeConstraint(int idx, int line, int column) {
        finishShapeOpNoAction("LiteralNodeConstraint", idx);
        finish("LiteralNodeConstraint");
    }

    protected int startNonLiteralNodeConstraint(int line, int column) {
        start("NonLiteralNodeConstraint");
        return startShapeOp();
    }

    protected void finishNonLiteralNodeConstraint(int idx, int line, int column) {
        finishShapeOpNoAction("vLiteralNodeConstraint", idx);
        finish("NonLiteralNodeConstraint");
    }

    private void addNodeConstraint(NodeConstraint constraint) {
        debug("NodeConstraint: %s", constraint);
        push(shapeExprStack, constraint);
    }

    protected void cDatatype(String str, int line, int column) {
        DatatypeConstraint dt = new DatatypeConstraint(str);
        addNodeConstraint(dt);
    }

    protected void cNodeKind(String nodeKindStr, int line, int column) {
        NodeKind nodeKind = NodeKind.create(nodeKindStr);
        NodeKindConstraint nk = new NodeKindConstraint(nodeKind);
        addNodeConstraint(nk);
    }

    protected void cValueSet() {
        debug("valueSet");
    }

    static Pattern repeatRange = Pattern.compile(".(\\d+)(,(\\d+|\\*)?)?.");

    /** micro-parse a cardinality range. */
    protected void cardinalityRange(String image, int line, int column) {
        try {
            int min = -1;
            int max = -1;
            String special;
            switch(image) {
                case "*": special = image; min = 0 ; max = -2 ; return;
                case "?": special = image; min = 0 ; max = 1 ;  return;
                case "+": special = image; min = 1 ; max = -2 ; return;
                default: {
                    special = null;
                    Matcher matcher = repeatRange.matcher(image);
                    if ( !matcher.matches() )
                        throw new InternalErrorException("ShExC: Unexpected cardinality: '"+image+"'");
                    min = integerRange(matcher.group(1), -3);
                    if ( matcher.groupCount() != 3 )
                        throw new InternalErrorException("ShExC: Unexpected cardinality: '"+image+"'");
                    String g = matcher.group(3);
                    max = integerRange(g, min);
                }
            }
            generateCardinality(image, min, max);
            debug("Cardinality: %s min=%s, max=%d", image, min, max);
        } catch ( Throwable th) {
            throw new ShexParseException("Bad cardinality: "+image, line, column);
        }
    }

    private void generateCardinality(String image, int min, int max) {
        debug("Cardinality: %s min=%s, max=%d", image, min, max);
    }

    // Node Constraints.

    protected void numericFacetRange(String range, Node num, int line, int column) {
        NumRangeKind kind = NumRangeKind.create(range);
        NodeConstraint numLength = new NumRangeConstraint(kind, num);
        addNodeConstraint(numLength);
    }

    protected void numericFacetLength(String facetKind, int length, int line, int column) {
        NumLengthKind kind = NumLengthKind.create(facetKind);
        NodeConstraint numLength = new NumLengthConstraint(kind, length);
        addNodeConstraint(numLength);
    }

    // Metacharacters ., ?, *, +, {, } (, ), [ or ].
        // and "|", "$" and "^". These preserve their \.
    //    "\\" [ "n", "r", "t", "\\", "|", "." , "?", "*", "+",
    //           "(", ")", "{", "}", "$", "-", "[", "]", "^", "/"

    protected void stringFacetRegex(String regexStr, int line, int column) {
        int idx = regexStr.lastIndexOf('/');
        String pattern = regexStr.substring(1, idx);
        pattern = EscapeStr.unescapeUnicode(pattern);

        String flags = regexStr.substring(idx+1);
        pattern = ShexParserLib.unescapeShexRegex(pattern, '\\', false);
        NodeConstraint regex = new StrRegexConstraint(pattern, flags);
        addNodeConstraint(regex);
    }

    protected void stringFacetLength(String str, int len) {
        StrLengthKind lengthType = StrLengthKind.create(str);
        NodeConstraint shExpr = new StrLengthConstraint(lengthType, len);
        addNodeConstraint(shExpr);
    }

    protected Node langStringLiteral(int quoteLen, String image, int line, int column) {
        // Find @ and split.
        int idx = image.lastIndexOf('@');
        if ( idx < 2*quoteLen )
            throw new ShexParseException("Bad langStringLiteral: "+image, line, column);

        String lex = image.substring(quoteLen, idx-quoteLen);
        String lang = image.substring(idx+1);
        lex = unescapeStr(lex, line, column);
        return NodeFactory.createLiteral(lex, lang);
    }

    // Special case @ns: and @ns:foo.
    protected Node resolve_AT_PName(String image, int line, int column) {
        String prefixedName = image.substring(1);
        String iriStr = resolvePName(prefixedName, line, column);
        // [shex] Rename as "createIRINode"
        return super.createNode(iriStr, line, column);
    }

    // ---- Stacks
    //    shapeStack
    //    shapeExprStack
    //    tripleConstraints
    //    tripleExpressionClause

    // ---- Node Constraints.

    protected int integer(String image, int line, int column) {
        try {
            return Integer.parseInt(image) ;
        } catch (NumberFormatException ex) {
            throw new ShexParseException(ex.getMessage(), line, column);
        }
    }

    // ---- Node Constraints.

    // DRY: In ShaclCompactParser as well - to LangParserBase
    private int integerRange(String str, int i) {
        if ( str == null || str.equals("*") )
            return i;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            throw new InternalErrorException("Number format exception");
        }
    }

    private <T> T peek(Deque<T> stack) {
        return stack.peek();
    }

    private <T> void push(Deque<T> stack, T item) {
        if ( item == null )
            debug("push-null", item);
        if ( DEBUG_STACK )
            debug("push(%s)", item);
        stack.push(item);
    }

    private <T> T pop(Deque<T> stack) {
        T item = stack.pop();
        if ( DEBUG_STACK )
            debug("pop(%s)", item);
        return item;
    }

    /*
     * Pop the elements at idx to stack front.
     * Return null for "no arguments"
     */
    private <T> List<T> pop(Deque<T> stack, int x) {
        int N = front(stack)-x;
        if ( N == 0 )
            return null;
        // "items" will be "earliest first" order.
        @SuppressWarnings("unchecked")
        T[] items0 = (T[])new Object[N];
        List<T> items = Arrays.asList(items0);
        for(int i = N-1 ; i>=0 ; i-- )
            items.set(i, pop(stack));
        return items;
    }

    private <T> int front(Deque<T> stack) {
        return stack.size();
    }

    // -- Development

    private void start(String label) { start(null, label); }

    private void start(Inline inline, String label) {
        if ( DEBUG_PARSE ) {
            out.print("> ");
            out.print(label);
            if ( inline == INLINE)
                out.print("'");
            out.println();
            out.incIndent();
        }
    }

    private void finish(String label) { finish(null, label); }

    private void finish(Inline inline, String label) {
        if ( DEBUG_PARSE ) {
            out.decIndent();
            out.print("< ");
            out.print(label);
            if ( inline == INLINE)
                out.print("'");
            out.println();
        }
    }

    private void debug(String fmt, Object...args) {
        if ( DEBUG ) {
            out.print(String.format(fmt, args));
            out.println();
        }
    }

    private void debugNoIndent(String fmt, Object...args) {
        if ( DEBUG ) {
            int x = out.getAbsoluteIndent();
            out.setAbsoluteIndent(0);
            out.print(String.format(fmt, args));
            if ( !fmt.endsWith("\n") )
                out.println();
            out.setAbsoluteIndent(x);
        }
    }

    // -- shape expression parser calls. translate to onegeneral one for each category.

    protected int startShapeExpression() { return startShapeExpression(NOT_INLINE); }
    protected void finishShapeExpression(int idx) { finishShapeExpression(NOT_INLINE, idx); }

    protected int startShapeOr() { return startShapeOr(NOT_INLINE); }
    protected void finishShapeOr(int idx) { finishShapeOr(NOT_INLINE, idx); }

    protected int startShapeAnd() { return startShapeAnd(NOT_INLINE); }
    protected void finishShapeAnd(int idx) { finishShapeAnd(NOT_INLINE, idx); }

    protected int startShapeNot() { return startShapeNot(NOT_INLINE); }
    protected void finishShapeNot(int idx, boolean negate) { finishShapeNot(NOT_INLINE, idx, negate); }

    protected int startShapeAtom() { return startShapeAtom(NOT_INLINE); }
    protected void finishShapeAtom(int idx) { finishShapeAtom(NOT_INLINE, idx); }

    protected int startInlineShapeExpression() { return startShapeExpression(INLINE); }
    protected void finishInlineShapeExpression(int idx) { finishShapeExpression(INLINE, idx); }

    protected int startInlineShapeOr() { return startShapeOr(INLINE); }
    protected void finishInlineShapeOr(int idx) { finishShapeOr(INLINE, idx); }

    protected int startInlineShapeAnd() { return startShapeAnd(INLINE); }
    protected void finishInlineShapeAnd(int idx) { finishShapeAnd(INLINE, idx); }

    protected int startInlineShapeNot() { return startShapeNot(INLINE); }
    protected void finishInlineShapeNot(int idx, boolean negate) { finishShapeNot(INLINE, idx, negate); }

    protected int startInlineShapeAtom() { return startShapeAtom(INLINE); }
    protected void finishInlineShapeAtom(int idx) { finishShapeAtom(INLINE, idx); }
}
