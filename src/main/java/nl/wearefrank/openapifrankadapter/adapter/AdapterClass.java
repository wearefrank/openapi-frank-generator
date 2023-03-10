package nl.wearefrank.openapifrankadapter.adapter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;

import java.util.Map;

public class AdapterClass {
    private final String adapterName;
    private final String adapterDescription;

    public AdapterClass(OpenAPI openAPI, Map.Entry<String, PathItem> path, Map.Entry<PathItem.HttpMethod, io.swagger.v3.oas.models.Operation> operation) {
        // adapterName: remove the first slash and replace the remaining slashes with -
        this.adapterName = path.getKey().substring(1).replace("/", "-") + "-" + operation.getKey();
        this.adapterDescription = openAPI.getInfo().getDescription();

    }

    public String getAdapterName() {
        return "Adapter-" + this.adapterName;
    }

    public String getAdapterDescription() {
        return this.adapterDescription;
    }
}
