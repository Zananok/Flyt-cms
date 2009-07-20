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

package no.kantega.publishing.jobs.contentstate;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.ao.ContentAO;
import no.kantega.publishing.common.data.Content;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.data.enums.ContentVisibilityStatus;
import no.kantega.publishing.common.data.enums.ExpireAction;
import no.kantega.publishing.common.data.enums.ContentStatus;
import no.kantega.publishing.event.ContentListener;

/**
 *
 */
public class ContentStateChangeJob  {
    private static String SOURCE = "ContentStateChangeJob";

    private ContentListener contentNotifier;

    public void execute() {
        try {
            Log.debug(SOURCE, "Looking for content that has expired", null, null);
            int i = 0;
            while((i = ContentAO.getNextExpiredContentId(i)) > 0) {
                ContentIdentifier cid = new ContentIdentifier();
                cid.setContentId(i);
                Content content = ContentAO.getContent(cid, false);
                if (content != null) {
                    int newVisibilityStatus = ContentVisibilityStatus.EXPIRED;
                    if (content.getExpireAction() == ExpireAction.ARCHIVE) {
                        newVisibilityStatus = ContentVisibilityStatus.ARCHIVED;
                    }
                    ContentAO.setContentVisibilityStatus(content.getId(), newVisibilityStatus);
                    contentNotifier.contentExpired(content);
                }
            }
            Log.debug(SOURCE, "Looking for content that needs activation", null, null);
            i = 0;
            while((i = ContentAO.getNextActivationContentId(i)) > 0) {
                ContentIdentifier cid = new ContentIdentifier();
                cid.setContentId(i);
                Content content = ContentAO.getContent(cid, false);
                if (content != null) {
                    boolean activated = false;
                    if (content.getVisibilityStatus() != ContentVisibilityStatus.ACTIVE) {
                        Log.debug(SOURCE, content.getTitle() + " page was made visible due to publish date", null, null);
                        ContentAO.setContentVisibilityStatus(content.getId(), ContentVisibilityStatus.ACTIVE);
                        activated = true;
                    } else if (content.getStatus() == ContentStatus.PUBLISHED_WAITING) {
                        Log.debug(SOURCE, content.getTitle() + " new version was activated due to change from date", null, null);
                        ContentAO.setContentStatus(cid, ContentStatus.PUBLISHED, "");
                        activated = true;                        
                    }
                    if (activated) {
                        contentNotifier.contentActivated(content);
                    }
                }
            }

        } catch (SystemException e) {
            e.printStackTrace();
        }

    }

    public void setContentNotifier(ContentListener contentNotifier) {
        this.contentNotifier = contentNotifier;
    }
}
