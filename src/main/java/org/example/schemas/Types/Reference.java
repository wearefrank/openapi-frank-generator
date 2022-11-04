package org.example.schemas.Types;

import nl.nn.adapterframework.xml.SaxElementBuilder;
import org.xml.sax.SAXException;

public class Reference extends Typing {
    private String name;
    private String type;

    public Reference(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void AddToBuilder(SaxElementBuilder builder) throws SAXException {
        try(SaxElementBuilder subElement = builder.startElement("xs:element")){
            subElement.addAttribute("name", this.name);
            subElement.addAttribute("type", this.type);
        }
    }
}
