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

import org.apache.jena.arq.junit.manifest.Label;
import org.apache.jena.arq.junit.manifest.Manifests;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import shex.runner.RunnerShex;

@RunWith(RunnerShex.class)
@Label("Shex")
@Manifests({
    "files/spec/validation/manifest.ttl"
//    ,
//    "files/spec/schemas/manifest.ttl"
})
public class Scripts_ShexValidation {
    private static boolean bVerboseWarnings;
    private static boolean bWarnOnUnknownFunction;

    @BeforeClass
    public static void beforeClass() {
//        bVerboseWarnings = NodeValue.VerboseWarnings;
//        bWarnOnUnknownFunction = E_Function.WarnOnUnknownFunction;
//        NodeValue.VerboseWarnings = false;
//        E_Function.WarnOnUnknownFunction = false;
    }

    @AfterClass
    public static void afterClass() {
//        NodeValue.VerboseWarnings = bVerboseWarnings;
//        E_Function.WarnOnUnknownFunction = bWarnOnUnknownFunction;
    }
}
