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

import lombok.Setter;
import nl.nn.adapterframework.xml.SaxElementBuilder;
import org.xml.sax.SAXException;

import java.math.BigDecimal;

public class SimpleType extends Typing {
    private final String name;
    private final String type;

    @Setter
    private BigDecimal maxInclusive;
    @Setter
    private BigDecimal minInclusive;
    @Setter
    private Integer minLength;
    @Setter
    private Integer maxLength;
    @Setter
    private String pattern;
    @Setter
    private java.util.List enumeration;

    public SimpleType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void AddToBuilder(SaxElementBuilder builder) throws SAXException {
        try (SaxElementBuilder subElement = builder.startElement("xs:element")) {
            subElement.addAttribute("name", this.name);
            try (SaxElementBuilder simpleType = subElement.startElement("xs:simpleType")) {
                try (SaxElementBuilder restriction = simpleType.startElement("xs:restriction")) {
                    restriction.addAttribute("base", "xs:" + this.type);
                    if (this.maxInclusive != null) {
                        try (SaxElementBuilder maxInclusive = restriction.startElement("xs:maxInclusive")) {
                            maxInclusive.addAttribute("value", String.valueOf(this.maxInclusive));
                        }
                    }
                    if (this.minInclusive != null) {
                        try (SaxElementBuilder minInclusive = restriction.startElement("xs:minInclusive")) {
                            minInclusive.addAttribute("value", String.valueOf(this.minInclusive));
                        }
                    }
                    if (this.minLength != null) {
                        try (SaxElementBuilder minLength = restriction.startElement("xs:minLength")) {
                            minLength.addAttribute("value", this.minLength);
                        }
                    }
                    if (this.maxLength != null) {
                        try (SaxElementBuilder maxLength = restriction.startElement("xs:maxLength")) {
                            maxLength.addAttribute("value", this.maxLength);
                        }
                    }
                    if (this.pattern != null) {
                        try (SaxElementBuilder pattern = restriction.startElement("xs:pattern")) {
                            pattern.addAttribute("value", this.pattern);
                        }
                    }
                    if (this.enumeration != null) {
                        for (Object o : this.enumeration) {
                            try (SaxElementBuilder enumeration = restriction.startElement("xs:enumeration")) {
                                enumeration.addAttribute("value", String.valueOf(o));
                            }
                        }
                    }
                }
            }
        }
    }
}
