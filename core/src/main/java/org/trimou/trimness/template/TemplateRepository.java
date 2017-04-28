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

import java.util.Collections;
import java.util.Set;

import org.trimou.engine.priority.WithPriority;
import org.trimou.engine.validation.Validateable;
import org.trimou.trimness.TrimnessTemplateLocator;

/**
 * The template repository is primarily responsible for identifying the
 * templates and loading the contents. There might be several template
 * repositories deployed. The repositories with higher priority are queried
 * first.
 * <p>
 * If a template is modified, the repository should fire an event of type
 * {@link Template} so that it can be invalidated properly.
 * </p>
 * <p>
 * An invalid repository is not considered when performing the lookup of a
 * template.
 * </p>
 *
 * @author Martin Kouba
 * @see CompositeTemplateRepository
 * @see TrimnessTemplateLocator
 * @see TemplateInvalidator
 */
public interface TemplateRepository extends WithPriority, Validateable {

    /**
     *
     * @param id
     * @return the template with the given id or <code>null</code>
     */
    Template get(String id);

    /**
     * Note that the returned set does not necessarily contain all available
     * templates.
     *
     * @return all templates stored in the repository
     */
    default Set<Template> getAll() {
        return Collections.emptySet();
    }

}
