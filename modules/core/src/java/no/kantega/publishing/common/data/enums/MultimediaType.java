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

package no.kantega.publishing.common.data.enums;

public enum MultimediaType {
    FOLDER(0),
    MEDIA(1),
    ROOT_FOLDER(999);

    private int typeAsInt;

    MultimediaType(int type) {
        this.typeAsInt = type;
    }

    public int getTypeAsInt() {
        return typeAsInt;
    }

    public static MultimediaType getMultimediaTypeAsEnum(int typeAsInt) {
        for (MultimediaType type : MultimediaType.values()) {
            if (type.getTypeAsInt() == typeAsInt) {
                return type;
            }
        }

        return MultimediaType.MEDIA;
    }
}
