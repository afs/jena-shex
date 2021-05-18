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

package shex;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.lib.IRILib;
import org.apache.jena.riot.RDFDataMgr;
import shex.parser.ShexParser;

public class Shex {
    public static ShexShapes shapesFromFile(String filename) {
        return shapesFromFile(filename, null);
    }

    public static ShexShapes shapesFromFile(String filename, String base) {
        InputStream input = IO.openFileBuffered(filename);
        ShexShapes shapes = ShexParser.parse(input, base);
        return shapes;
    }

    public static ShexShapes shapesFromString(String inputStr) {
        InputStream input = new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8));
        ShexShapes shapes = ShexParser.parse(input, null);
        return shapes;
    }

    public static ShexShapes readShapes(String filenameOrURL) {
        return readShapes(filenameOrURL, null);
    }

    public static ShexShapes readShapes(String filenameOrURL, String base) {
        InputStream input = RDFDataMgr.open(filenameOrURL);
        if ( ! ( input instanceof BufferedInputStream ) )
            input = new BufferedInputStream(input, 128*1024);
        String parserBase = (base != null) ? base : IRILib.filenameToIRI(filenameOrURL);
        ShexShapes shapes = ShexParser.parse(input, parserBase);
        return shapes;
    }
}
