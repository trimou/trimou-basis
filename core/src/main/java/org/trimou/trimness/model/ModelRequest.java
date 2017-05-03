/*
 * Copyright 2017 Trimness team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.trimness.model;

import java.util.Optional;

import org.trimou.trimness.template.Template;

/**
 * Request for a data model. A new request is created for each provider when a
 * rendering action is performed.
 *
 * @author Martin Kouba
 * @see ModelProvider#handle(ModelRequest)
 */
public interface ModelRequest {

    /**
     *
     * @return the template or <code>null</code> if not using a persistent
     *         template
     */
    Template getTemplate();

    /**
     * Parameters are passed along with a render request.
     *
     * @param name
     * @return the parameter container
     */
    Optional<Object> getParameter(String name);

    /**
     * Signal that the request was processed and the given result was found.
     *
     * @param result
     * @throws IllegalStateException
     *             If result already set
     */
    void setResult(Object value);

    /**
     * Signal that the request was processed and no result can be found.
     *
     * @throws IllegalStateException
     *             If result already set
     */
    default void noResult() {
        setResult(null);
    }

}
