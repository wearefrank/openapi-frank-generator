package nl.wearefrank.openapifrankadapter.schemas.Types;

import nl.nn.adapterframework.xml.SaxElementBuilder;
import nl.wearefrank.openapifrankadapter.schemas.Sequence;
// TODO: Check if this import functions
import org.xml.sax.SAXException;

public class Reference extends Typing {
    private final String name;
    private final Sequence sequence;

    public Reference(String name) {
        this.name = name;
        this.sequence = new Sequence();
    }

    public void addTyping(Typing typing) {
        this.sequence.addTyping(typing);
    }

    @Override
    public void AddToBuilder(SaxElementBuilder builder) throws SAXException {
        try (SaxElementBuilder subElement = builder.startElement("xs:element")) {
            subElement.addAttribute("name", this.name);
            this.sequence.SilentAddSequenceToBuilder(subElement);
        }
    }
}
