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

package no.kantega.publishing.common.data.attributes;

import no.kantega.commons.exception.SystemException;
import no.kantega.commons.log.Log;
import no.kantega.publishing.common.data.ContentIdentifier;
import no.kantega.publishing.common.exception.ContentNotFoundException;
import no.kantega.publishing.common.exception.InvalidTemplateException;
import org.w3c.dom.Element;

import java.util.Map;

public class ContentidAttribute extends Attribute {
    protected boolean multiple = false;
    protected int maxitems = Integer.MAX_VALUE;
    protected int startId = -1;

    public void setConfig(Element config, Map model) throws InvalidTemplateException, SystemException {
        super.setConfig(config, model);

        if (config != null) {
            String multiple = config.getAttribute("multiple");
            if ("true".equalsIgnoreCase(multiple)) {
                this.multiple = true;
            }
            String maxitemsS = config.getAttribute("maxitems");
            if(maxitemsS != null && maxitemsS.trim().length() > 0) {
                maxitems = Integer.parseInt(maxitemsS);
            }
            String startIdS = config.getAttribute("startid");
            if(startIdS != null && startIdS.trim().length() > 0) {
                try {
                    startId = Integer.parseInt(startIdS);
                } catch (NumberFormatException e) {
                    try {
                        ContentIdentifier cid = new ContentIdentifier(startIdS);
                        startId = cid.getAssociationId();
                    } catch (ContentNotFoundException e1) {
                        Log.error(this.getClass().getName(), e, null, null);
                    }
                }                
            }
        }
    }

    public String getRenderer() {
        if (multiple) {
            return "contentid_multiple";
        } else {
            return "contentid";
        }
    }

    public int getMaxitems() {
        return maxitems;
    }

    public int getStartId() {
        return startId;
    }
}
