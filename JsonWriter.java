// Copyright 2019-2022 Scott Fauerbach
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

public class JsonWriter {

    static ObjectMapper MAPPER = new ObjectMapper()
        .setSerializationInclusion(Include.NON_DEFAULT)
        .registerModule(new JavaTimeModule());

    static ObjectMapper INCLUDE_ALL_MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    public static String toJson(ObjectMapper mapper, Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, o);
        return baos.toString();
    }

    public static byte[] toJsonBytes(ObjectMapper mapper, Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writeValue(baos, o);
        return baos.toByteArray();
    }

    public static String toJsonFormatted(ObjectMapper mapper, Object o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mapper.writerWithDefaultPrettyPrinter().writeValue(baos, o);
        return baos.toString();
    }

    public static String toJson(Object o) throws IOException {
        return toJson(MAPPER, o);
    }

    public static byte[] toJsonBytes(Object o) throws IOException {
        return toJsonBytes(MAPPER, o);
    }

    public static String toJsonFormatted(Object o) throws IOException {
        return toJsonFormatted(MAPPER, o);
    }

    public static String toJsonIncludeNonDefault(Object o) throws IOException {
        return toJson(INCLUDE_ALL_MAPPER, o);
    }

    public static byte[] toJsonBytesIncludeNonDefault(Object o) throws IOException {
        return toJsonBytes(INCLUDE_ALL_MAPPER, o);
    }

    public static String toJsonFormattedIncludeNonDefault(Object o) throws IOException {
        return toJsonFormatted(INCLUDE_ALL_MAPPER, o);
    }
}
