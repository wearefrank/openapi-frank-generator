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
