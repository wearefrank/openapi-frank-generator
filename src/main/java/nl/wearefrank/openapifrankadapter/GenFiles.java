package nl.wearefrank.openapifrankadapter;

public record GenFiles(String name, byte[] content){
    public GenFiles(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public byte[] getContent() {
        return content;
    }
}
