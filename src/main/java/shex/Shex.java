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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.jena.atlas.io.IO;
import shex.parser.ShexParser;

public class Shex {
    public static ShexShapes shapesFromFile(String filename) {
        InputStream input = IO.openFileBuffered(filename);
        ShexShapes shapes = ShexParser.parse(input, null);
        return shapes;
    }

    public static ShexShapes shapesFromString(String inputStr) {
        InputStream input = new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8));
        //ShexShapes shapes = shapesFromFile(null);
        ShexShapes shapes = ShexParser.parse(input, null);
        return shapes;
    }
}
