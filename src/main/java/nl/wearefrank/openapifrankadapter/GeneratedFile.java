package nl.wearefrank.openapifrankadapter;

public record GeneratedFile(String name, byte[] content){

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
