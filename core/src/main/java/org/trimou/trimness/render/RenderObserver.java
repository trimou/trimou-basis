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
package org.trimou.trimness.render;

import static org.trimou.trimness.util.Strings.CONTENT;
import static org.trimou.trimness.util.Strings.CONTENT_TYPE;
import static org.trimou.trimness.util.Strings.ID;
import static org.trimou.trimness.util.Strings.MODEL;
import static org.trimou.trimness.util.Strings.RESULT_TYPE;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.jboss.weld.vertx.VertxConsumer;
import org.jboss.weld.vertx.VertxEvent;
import org.trimou.Mustache;
import org.trimou.engine.MustacheEngine;
import org.trimou.exception.MustacheException;
import org.trimou.trimness.model.ModelInitializer;
import org.trimou.trimness.template.ImmutableTemplate;
import org.trimou.trimness.template.Template;
import org.trimou.trimness.template.TemplateCache;
import org.trimou.trimness.util.Resources;
import org.trimou.trimness.util.Resources.ResultType;

/**
 * Consumes messages sent over Vert.x event bus.
 *
 * @author Martin Kouba
 */
@ApplicationScoped
public class RenderObserver {

    public static final String ADDR_RENDER = "org.trimou.trimness.render";

    public static final int CODE_INVALID_JSON = 1;
    public static final int CODE_ID_OR_CONTENT_MUST_BE_SET = 2;
    public static final int CODE_TEMPLATE_NOT_FOUND = 3;
    public static final int CODE_COMPILATION_ERROR = 4;
    public static final int CODE_RENDER_ERROR = 5;

    @Inject
    private TemplateCache templateCache;

    @Inject
    private MustacheEngine engine;

    @Inject
    private IdGenerator idGenerator;

    @Inject
    private ModelInitializer modelInitializer;

    /**
     * The message body must be a JSON string.
     *
     * @param event
     */
    void handleRenderEvent(@Observes @VertxConsumer(ADDR_RENDER) VertxEvent event) {

        JsonObject input = Resources.getBodyAsJsonObject(event.getMessageBody().toString());

        if (input == null) {
            event.fail(CODE_INVALID_JSON, "Invalid request body format");
        }
        if (!input.containsKey(ID) && !input.containsKey(CONTENT)) {
            event.fail(CODE_ID_OR_CONTENT_MUST_BE_SET, "Template id or content must be set");
        }

        Mustache mustache = null;
        Template template = null;

        if (input.containsKey(ID)) {

            String templateId = input.getString(ID, null);
            if (templateId != null) {
                template = templateCache.get(templateId);
            }

            if (template == null) {
                event.fail(CODE_TEMPLATE_NOT_FOUND, "Template not found: " + templateId);
            }
            mustache = engine.getMustache(template.getId());
            if (mustache == null) {
                event.fail(CODE_TEMPLATE_NOT_FOUND, "Template not found in engine: " + templateId);
            }
        } else {
            // Onetime rendering - we can be sure the content is set
            String content = input.getString(CONTENT, "");
            template = ImmutableTemplate.of(idGenerator.getOneoffTemplateId(), content,
                    input.getString(CONTENT_TYPE, null));
            try {
                mustache = engine.compileMustache(template.getId(), content);
            } catch (MustacheException e) {
                // Handle possible compilation problems
                event.fail(CODE_COMPILATION_ERROR, e.getMessage());
            }
        }

        ResultType resultType = ResultType.of(input.get(RESULT_TYPE));

        try {
            String result = mustache
                    .render(modelInitializer.initModel(template, input.get(MODEL), Resources.initParams(input)));
            switch (resultType) {
            case RAW:
                event.setReply(result);
                break;
            case METADATA:
                event.setReply(Resources.metadataResult(template, result));
                break;
            default:
                throw new IllegalStateException("Unsupported result type: " + resultType);
            }
        } catch (MustacheException e) {
            event.fail(CODE_RENDER_ERROR, e.getMessage());
        }
    }

}