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
package org.trimou.trimness.template;

import java.io.Reader;
import java.util.function.Supplier;

import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.validation.Validateable;
import org.trimou.trimness.util.WithId;

/**
 *
 * @author Martin Kouba
 */
public interface ContentTypeExtractor extends WithPriority, WithId, Validateable {

    /**
     *
     * @param id
     * @param contentReader
     * @return the content type or <code>null</code>
     */
    String extract(String id, Supplier<Reader> content);

}
