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

package org.apache.jena.shex.extra;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonException;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shex.ShexException;
import org.apache.jena.shex.ShexShapeAssociation;
import org.apache.jena.shex.ShexShapeMap;

/** In support of testing */
public class Extra {

    public static ShexShapeMap parseShapesMapJson(String filenameOrUri) {
        TypedInputStream in = RDFDataMgr.open(filenameOrUri);
        return parseShapesMapJson(in.getInputStream());
    }

    /**
     * Parse the {@code InputStream} to get a ShEx shape map from JSON syntax.
     * @param input
     * @return ShexShapeMap
     */
    public static ShexShapeMap parseShapesMapJson(InputStream input) {
        JsonValue x = JSON.parseAny(input);
        if ( ! x.isArray() )
            throw new ShexException("Shex shape map: not a JSON array");
        List<ShexShapeAssociation> associations = new ArrayList<>();
        // Just enough to parse the maps in the validation test suite.
        x.getAsArray().forEach(j->{
            if ( !j.isObject() ) {}
            ShexShapeAssociation a = parseShapesMapEntry(j.getAsObject());
            associations.add(a);
        });
        return new ShexShapeMap(associations);

//        node: an RDF node, or a triple pattern which is used to select RDF nodes.
//        shape: ShEx shapeExprLabel or the string "START" for the start shape expression.
//        status: [default="conformant"] "nonconformant" or "conformant".
//        reason: [optional] a string stating a reason for failure or success.
//        appInfo: [optional] an application-specific JSON-LD structure
    }

    private static ShexShapeAssociation parseShapesMapEntry(JsonObject obj) {
        try {
            String uri = obj.get("node").getAsString().value();
            String shapeURI = obj.get("shape").getAsString().value();
            Node nodeFocus = NodeFactory.createURI(uri);
            Node nodeShape = NodeFactory.createURI(shapeURI);
            return new ShexShapeAssociation(nodeFocus, null, nodeShape);
        } catch (JsonException ex) {
            throw new ShexException("Failed to parse shape map entry: "+JSON.toStringFlat(obj));
        }
    }
}
