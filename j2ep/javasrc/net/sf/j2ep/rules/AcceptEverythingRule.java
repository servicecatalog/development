/*
 * Copyright 2000,2004 The Apache Software Foundation.
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

import javax.servlet.http.HttpServletRequest;


/**
 * This rule matches every request
 * passed to it, making it suitable
 * for use as a catch-all or last
 * rule in a chain.
 *
 * @author Yoav Shapira
 */
public class AcceptEverythingRule extends BaseRule {
    
    /**
     * This implementation always matches.
     * 
     * @see net.sf.j2ep.model.Rule#matches(HttpServletRequest)
     */
    public boolean matches(HttpServletRequest request) {
        return true;
    }
    
}
