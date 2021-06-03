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

package org.apache.jena.shex.parser;

import static java.lang.String.format;

import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.lib.IRILib;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.irix.IRIxResolver;
import org.apache.jena.riot.system.*;
import org.apache.jena.shex.ShexShape;
import org.apache.jena.shex.ShexShapeMap;
import org.apache.jena.shex.ShexShapes;
import org.apache.jena.shex.expressions.*;
import org.apache.jena.shex.parser.javacc.ParseException;
import org.apache.jena.shex.parser.javacc.ShExJavacc;
import org.apache.jena.shex.parser.javacc.TokenMgrError;
import org.apache.jena.shex.sys.ShexLib;
import org.apache.jena.shex.sys.SysShex;
import org.apache.jena.sparql.expr.nodevalue.XSDFuncOp;
import org.apache.jena.sparql.util.Context;

public class ShexParser {
    /**
     * Parse the file to get ShEx shapes.
     * @param filename
     * @return ShexShapes
     */
    public static ShexShapes parse(String filename) {
        return parse(filename,  IRILib.filenameToIRI(filename));
    }

    /**
     * Parse the file to get ShEx shapes.
     * @param filename
     * @param baseURI
     * @return ShexShapes
     */
    public static ShexShapes parse(String filename, String baseURI) {
        InputStream input = IO.openFileBuffered(filename);
        return parse(input, baseURI);
    }

    /**
     * Parse the {@code InputStream} to get ShEx shapes.
     * @param input
     * @param baseURI
     * @return ShexShapes
     */
    public static ShexShapes parse(InputStream input, String baseURI) {
        ShExJavacc parser = new ShExJavacc(input, StandardCharsets.UTF_8.name());
        return parse$(parser, baseURI, null);
    }

    /**
     * Parse from a {@code StringReader}.
     * @param input
     * @param baseURI
     * @return ShexShapes
     */
    public static ShexShapes parse(StringReader input, String baseURI) {
        ShExJavacc parser = new ShExJavacc(input);
        return parse$(parser, baseURI, null);
    }

    // ---- Shex Shape Map

    /**
     * Parse the file to get a ShEx shape map.
     * @param filename
     * @return ShexShapeMap
     */
    public static ShexShapeMap parseShapesMap(String filename) {
        return parseShapesMap(filename, null);
    }

    /**
     * Parse the file to get a ShEx shape map.
     * @param filename
     * @param baseURI
     * @return ShexShapeMap
     */
    public static ShexShapeMap parseShapesMap(String filename, String baseURI) {
        InputStream input = IO.openFileBuffered(filename);
        return parseShapesMap(input, baseURI);
    }

    /**
     * Parse the {@code InputStream} to get a ShEx shape map.
     * @param input
     * @param baseURI
     * @return ShexShapeMap
     */
    public static ShexShapeMap parseShapesMap(InputStream input, String baseURI) {
        ShExJavacc parser = new ShExJavacc(input, StandardCharsets.UTF_8.name());
        return parseShapeMap$(parser, baseURI, null);
    }

    /**
     * Parse a shape map from a {@code StringReader}.
     * @param input
     * @param baseURI
     * @return ShexShapeMap
     */
    public static ShexShapeMap parseShapesMap(StringReader input, String baseURI) {
        ShExJavacc parser = new ShExJavacc(input);
        return parseShapeMap$(parser, baseURI, null);
    }

    // --------

    private static ShexShapes parse$(ShExJavacc parser, String baseURI, Context context) {
        ParserProfile profile = new ParserProfileStd(RiotLib.factoryRDF(),
                                                     ErrorHandlerFactory.errorHandlerStd,
                                                     IRIxResolver.create(baseURI).build(),
                                                     PrefixMapFactory.create(),
                                                     context, false, false);
        //addStandardPrefixes(profile.getPrefixMap());
        parser.setProfile(profile);
        // We don't use the StreamRDF.
        parser.setDest(StreamRDFLib.sinkNull());
        try {
            parser.parseShapesStart();
            parser.UnitShapes();
            ShexShapes shapes = parser.parseShapesFinish();
            validatePhase2(shapes);
            return shapes;
        } catch (ParseException ex) {
            throw new ShexParseException(ex.getMessage(), ex.currentToken.beginLine, ex.currentToken.beginColumn);
        }
        catch ( TokenMgrError tErr) {
            int col = parser.token.endColumn ;
            int line = parser.token.endLine ;
            throw new ShexParseException(tErr.getMessage(), line, col) ;
        }
    }

    /*
     * section 5.7 Schema Requirements
     * 5.7.1 Schema Validation Requirement
     * 5.7.2 Shape Expression Reference Requirement
     * 5.7.3 Triple Expression Reference Requirement
     * 5.7.4 Negation Requirement
     *
     * (only after imports closure)
     */
    private static void validatePhase2(ShexShapes shapes) {
        if ( ! SysShex.STRICT )
            return;
        shapes.getShapes().forEach(shape->validatePhase2(shapes, shape));
    }

    private static void validatePhase2(ShexShapes shapes, ShexShape shape) {
        ShapeExpression shExpr = shape.getShapeExpression();
        ShapeExprVisitor checker = new CheckFacets();
        TripleExprVisitor tExprVisitor = new TripleExprVisitor() {
            @Override public void visit(TripleConstraint object) {
                // One level call of visitor.
                //object.getPredicate();
                ShapeExpression theShapeExpression = object.getShapeExpression();
                if ( theShapeExpression != null )
                    theShapeExpression.visit(checker);
            }
        };
        ShexLib.walk(shExpr, checker, null);
    }

    private static class CheckFacets implements ShapeExprVisitor {
        // Inside TripleConstraint
        @Override
        public void visit(ShapeExprAND shape) {
            List<ShapeExpression> elements = shape.expressions();
            Set<StrLengthKind> x = new HashSet<>(3);
            DatatypeConstraint dtConstraint = null;
            for ( ShapeExpression expr : elements ) {
                if ( expr instanceof StrLengthConstraint ) {
                    StrLengthConstraint constraint = (StrLengthConstraint)expr;
                    StrLengthKind lenType = constraint.getLengthType();
                    if ( x.contains(lenType) )
                        throw new ShexParseException("Multiple string length facets of the same kind: "+lenType, -1, -1);
                    x.add(lenType);
                    continue;
                }

                // Can't have numeric constraints after a non-numeric datatype.
                // Assumes the order in shape.expressions();
                // First, remember the DatatypeConstraint
                if ( expr instanceof DatatypeConstraint ) {
                    dtConstraint = (DatatypeConstraint)expr;
                    continue;
                }

                if ( dtConstraint != null ) {
                    if ( expr instanceof NumLengthConstraint || expr instanceof NumRangeConstraint ) {
                        RDFDatatype rdfDT = dtConstraint.getRDFDatatype();
                        if ( ! ( rdfDT instanceof XSDDatatype ) ) {
                            String msg = format("Numeric facet: Not a numeric: <%s> ", dtConstraint.getDatatypeURI());
                            throw new ShexParseException(msg, -1, -1);
                        }
                        if ( ! XSDFuncOp.isNumericDatatype((XSDDatatype)rdfDT) ) {
                            String msg = format("Numeric facet: Not an XSD numeric: <%s> ", dtConstraint.getDatatypeURI());
                            throw new ShexParseException(msg, -1, -1);
                        }
                    }
                }
            }
        }
    }

    private static ShexShapeMap parseShapeMap$(ShExJavacc parser, String baseURI, Context context) {
        ParserProfile profile = new ParserProfileStd(RiotLib.factoryRDF(),
                                                     ErrorHandlerFactory.errorHandlerStd,
                                                     IRIxResolver.create(baseURI).build(),
                                                     PrefixMapFactory.create(),
                                                     context, false, false);
        //addStandardPrefixes(profile.getPrefixMap());
        parser.setProfile(profile);
        // We don't use the StreamRDF.
        parser.setDest(StreamRDFLib.sinkNull());
        try {
            parser.parseShapeMapStart();
            parser.UnitShapeMap();
            ShexShapeMap map = parser.parseShapeMapFinish();
            return map;
        } catch (ParseException ex) {
            throw new ShexParseException(ex.getMessage(), ex.currentToken.beginLine, ex.currentToken.beginColumn);
        }
        catch ( TokenMgrError tErr) {
            int col = parser.token.endColumn ;
            int line = parser.token.endLine ;
            throw new ShexParseException(tErr.getMessage(), line, col) ;
        }
    }

}
