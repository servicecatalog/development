/*
 * Copyright 2005 Anders Nyman.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package net.sf.j2ep.rules;

import net.sf.j2ep.model.Rule;

/**
 * The BaseRule is an empty rule
 * implementation which can be
 * extended.
 * This class is based on the work by Yoav Shapira
 * for the balancer webapp supplied with Tomcat.
 *
 * @author Anders Nyman
 */
public abstract class BaseRule implements Rule {
    
    /**
     * @see net.sf.j2ep.model.Rule#process(java.lang.String)
     */
    public String process(String uri) {
        return uri;
    }
    
    /**
     * @see net.sf.j2ep.model.Rule#revert(java.lang.String)
     */
    public String revert(String uri) {
        return uri;
    }

    /**
     * Returns a String representation of this object.
     *
     * @return The string representation
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("[");
        buffer.append(getClass().getName());
        buffer.append(": ");

        buffer.append("]");

        return buffer.toString();
    }
}
