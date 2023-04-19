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

package nl.wearefrank.openapifrankadapter.schemas;

import nl.nn.adapterframework.xml.SaxElementBuilder;
import nl.wearefrank.openapifrankadapter.schemas.Types.Typing;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class Sequence {
    private final ArrayList<Element> elements;

    public Sequence() {
        this.elements = new ArrayList<>();
    }

    // Add element to elements
    public void addElement(Element element) {
        this.elements.add(element);
    }

    // Generic function to add type: complexType or simpleType
    public void addTyping(Typing typing) {
        this.elements.add(new Element(typing));
    }

    public void AddSequenceToBuilder(SaxElementBuilder builder) throws SAXException {
        try (SaxElementBuilder subElement = builder.startElement("xs:sequence")) {
            for (Element element : this.elements) {
                element.AddElementToBuilder(subElement);
            }
        }
    }

    public void SilentAddSequenceToBuilder(SaxElementBuilder builder) throws SAXException {
        for (Element element : this.elements) {
            element.AddElementToBuilder(builder);
        }
    }
}
