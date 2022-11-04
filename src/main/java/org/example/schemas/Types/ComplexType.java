package org.example.schemas.Types;

import nl.nn.adapterframework.xml.SaxElementBuilder;
import org.example.schemas.Element;
import org.example.schemas.Sequence;
import org.xml.sax.SAXException;

public class ComplexType extends Typing {
    private String name;
    private Sequence sequence;
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
        try(SaxElementBuilder subElement = builder.startElement("xs:complexType")) {
            subElement.addAttribute("name", this.name);
            this.sequence.AddSequenceToBuilder(subElement);
        }
    }
}
