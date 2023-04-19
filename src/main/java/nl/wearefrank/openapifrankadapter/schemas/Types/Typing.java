/*
   Copyright 2023 WeAreFrank!
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package nl.wearefrank.openapifrankadapter.schemas.Types;

import nl.nn.adapterframework.xml.SaxElementBuilder;
import org.xml.sax.SAXException;

public class Typing {
    ComplexType complexType;
    SimpleType simpleType;

    Reference reference;

    public Typing() {
    }

    public void AddToBuilder(SaxElementBuilder builder) throws SAXException {
        if (this.complexType != null) {
            this.complexType.AddToBuilder(builder);
        } else if (this.simpleType != null) {
            this.simpleType.AddToBuilder(builder);
        } else if (this.reference != null) {
            this.reference.AddToBuilder(builder);
        }
    }
}
