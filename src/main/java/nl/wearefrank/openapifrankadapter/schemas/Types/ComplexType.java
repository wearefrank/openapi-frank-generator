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
import nl.wearefrank.openapifrankadapter.schemas.Element;
import nl.wearefrank.openapifrankadapter.schemas.Sequence;
import org.xml.sax.SAXException;

public class ComplexType extends Typing {
    private final String name;
    private final Sequence sequence;

    public ComplexType(String name) {
        super();
        this.name = name;
        this.sequence = new Sequence();
    }

    //// Adding objects to the sequence ////
    // Add element to sequence
    public void addElement(Element element) {
        this.sequence.addElement(element);
    }

    // Generic function to add type: complexType or simpleType
    public void addTyping(Typing typing) {
        this.sequence.addTyping(typing);
    }

    @Override
    public void AddToBuilder(SaxElementBuilder builder) throws SAXException {
        try (SaxElementBuilder subElement = builder.startElement("xs:complexType")) {
            if (this.name != "") {
                subElement.addAttribute("name", this.name);
            }
            this.sequence.AddSequenceToBuilder(subElement);
        }
    }
}
