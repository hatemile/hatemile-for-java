/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.hatemile.util.css;

import java.util.Collection;
import java.util.List;

/**
 * The StyleSheetParser interface contains the methods for access the CSS
 * parser.
 */
public interface StyleSheetParser {
    /**
     * Returns the rules of parser by properties.
     * @param properties The properties.
     * @return The rules.
     */
    List<StyleSheetRule> getRules(Collection<String> properties);
}
