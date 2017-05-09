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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Enumeration;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.trimou.engine.priority.WithPriority;
import org.trimou.trimness.config.Configuration;
import org.trimou.trimness.config.TrimnessKey;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Loads templates from the class path.
 *
 * @author Martin Kouba
 */
@ApplicationScoped
public class ClassPathTemplateProvider implements TemplateProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathTemplateProvider.class.getName());

    @Inject
    private Configuration configuration;

    @Inject
    private CompositeContentTypeExtractor extractor;

    private final ClassLoader classLoader;

    public ClassPathTemplateProvider() {
        ClassLoader classLoader = SecurityActions.getContextClassLoader();
        if (classLoader == null) {
            classLoader = SecurityActions.getClassLoader(ClassPathTemplateProvider.class);
        }
        this.classLoader = classLoader;
    }

    @Override
    public int getPriority() {
        // To be queried before FileSystemTemplateProvider
        return WithPriority.DEFAULT_PRIORITY + 1;
    }

    @Override
    public Template get(String id) {
        URL resource = find(id);
        if (resource != null) {
            Supplier<Reader> contentReader = () -> {
                try {
                    return new InputStreamReader(resource.openStream(),
                            configuration.getStringValue(TrimnessKey.DEFAULT_FILE_ENCODING));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };
            return ImmutableTemplate.of(id, getId(), contentReader, extractor.extract(id, contentReader));
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return !"".equals(configuration.getStringValue(TrimnessKey.CLASS_PATH_TEMPLATES_ROOT).trim());
    }

    private URL find(String id) {
        final String name = configuration.getStringValue(TrimnessKey.CLASS_PATH_TEMPLATES_ROOT) + id;
        URL found = null;
        try {
            Enumeration<URL> resources = classLoader.getResources(name);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (found != null) {
                    LOGGER.info("Duplicit template ignored: {0}", resource);
                } else {
                    found = resource;
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while reading {}", e, name);
        }
        return found;
    }

}
