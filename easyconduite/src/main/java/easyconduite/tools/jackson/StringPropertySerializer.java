/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package easyconduite.tools.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class StringPropertySerializer extends StdSerializer<String> {

    public StringPropertySerializer(Class<String> t) {
        super(t);
    }

    public StringPropertySerializer() {
        this(null);
    }

    @Override
    public void serialize(String stringProperty, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(stringProperty);
    }
}
