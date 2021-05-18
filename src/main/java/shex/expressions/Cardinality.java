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

package shex.expressions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.atlas.lib.InternalErrorException;

public class Cardinality {
    public static final int UNSET = -3;
    public static final int UNBOUNDED = -2;

    public final String image;
    public final int min;
    public final int max;

    public Cardinality(String image, int min, int max) {
        this.image = image;
        this.min = min;
        this.max = max;
    }

    private static Pattern repeatRange = Pattern.compile(".(\\d+)(,(\\d+|\\*)?)?.");

    public static Cardinality create(String image) {
        int min = -1;
        int max = -1;
        switch(image) {
            case "*": min = 0 ; max = UNBOUNDED ; break;
            case "?": min = 0 ; max = 1 ;  break;
            case "+": min = 1 ; max = UNBOUNDED ; break;
            default: {
                Matcher matcher = repeatRange.matcher(image);
                if ( !matcher.matches() )
                    throw new InternalErrorException("ShExC: Unexpected cardinality: '"+image+"'");
                min = integerRange(matcher.group(1), UNSET);
                if ( matcher.groupCount() != 3 )
                    throw new InternalErrorException("ShExC: Unexpected cardinality: '"+image+"'");
                String g = matcher.group(3);
                max = integerRange(g, min);
            }
        }
        return new Cardinality(image, min, max);
        //debug("Cardinality: %s min=%s, max=%d", image, min, max);
    }

    private static int integerRange(String str, int i) {
        if ( str == null )
            return i;
        if ( str.equals("*") )
            return UNBOUNDED;
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ex) {
            throw new InternalErrorException("Number format exception");
        }
    }


    static String cardStr(int min, int max) {
        String cardinality="";
        if ( min != -1 || max != -1 ) {
            if ( min == 0 && max == -2 )
                cardinality = "*";
            else if ( min == 1 && max == -2 )
                cardinality = "+";
            else if ( min == 0 && max == 1 )
                cardinality = "?";
            else if ( max == -2 )
                cardinality = "{"+min+",*}";
            else {
                cardinality = "{"+cardStr(min);
                if ( max ==-1 )
                    cardinality = cardinality+"}";
                else
                    cardinality = cardinality+","+cardStr(max)+"}";
            }
        }
        return cardinality;
    }

    private static String cardStr(int x) {
        if ( x == UNSET )
            return "1";
        if ( x == UNBOUNDED )
            return "*";
        return Integer.toString(x);
    }
}
