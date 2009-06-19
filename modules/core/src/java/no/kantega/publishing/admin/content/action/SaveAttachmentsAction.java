/*
 * Copyright 2009 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.kantega.publishing.admin.content.action;

import no.kantega.commons.client.util.ValidationErrors;
import no.kantega.publishing.admin.content.action.AbstractSaveContentAction;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.service.ContentManagementService;
import no.kantega.commons.client.util.RequestParameters;

public class SaveAttachmentsAction extends AbstractSaveContentAction {
    private static String SOURCE = "aksess.SaveAttachmentsAction";

    public ValidationErrors saveRequestParameters(Content content, RequestParameters param, ContentManagementService aksessService) {
        // Gj�r ingenting, denne fanen har ingen egenskaper kan endres
        ValidationErrors errors = new ValidationErrors();

        return errors;
    }

    public String getEditPage() {
        return "editattachments";
    }
}
