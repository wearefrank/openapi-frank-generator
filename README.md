# OpenAPI2Frank Adapter
This project will allow you to convert your OpenAPI endpoints to Frank!Adapter configurations.
These adapters can be implemented in your Frank!Framework to gain insight and test your specific endpoints.

You can use this generator by uploading a file or entering a url of an OpenAPI specification in either JSON or YAML and pressing
on the button of the desired type of adapter.

# Contents
- [Installation and Running](#installation-and-running)
- [Sequence Diagram](#sequence-diagram)

# Installation and Running
## Cloning
To clone the OpenAPI2Frank Adapter, make sure you are in your preferred directory and run the following command:

```sh
git clone https://github.com/wearefrank/openapi-frank-generator
```

## Running

### Local
To start the application, you can simply run the Spring Boot file [OpenapiFrankadapterApplication.java](src/main/java/nl/wearefrank/openapifrankadapter/OpenapiFrankadapterApplication.java).

The application has finished starting up when you see this message:
```
Started OpenapiFrankadapterApplication in X seconds (process running for Y)
```

### Docker
You can also run the OpenAPI Adapter in a Docker container.
To build the Docker image, run the following command in the root of the project:

```sh
docker build -t openapi-frank-generator .
```

To run the container, run the following command:

```sh
docker run -p 8080:8080 openapi-frank-generator
```

### Accessing the application

For both instances, you can access the application at:

http://localhost:8080

## Online

An online instance of this project can be found at:

https://openapi-frank-generator.wearefrank.org/

# API
The online instance of this project can also be accessed through an API.\
To use this API, you can send a POST request to the following endpoint:
```
https://openapi-frank-generator.wearefrank.org/{option}-{file/url}

Ex: https://openapi-frank-generator.wearefrank.org/receiver-file
```

# Sequence Diagram

```mermaid
sequenceDiagram
    participant App/Site
    participant OpenapiFrankadapterApplication
    participant XMLGenerator
    participant AdapterRefs
    participant XSDGenerator
    participant AdapterExits
    participant Handlebars
    participant Templates
    participant PrettyPrinter

    App/Site->>OpenapiFrankadapterApplication: OpenAPI file & option (Receiver/Sender)
    Note right of App/Site: User uploads OpenAPI spec and selects adapter type
    OpenapiFrankadapterApplication->>XMLGenerator: File containing OpenAPI spec
    Note right of OpenapiFrankadapterApplication: Passes the OpenAPI spec to the XML generator
    XMLGenerator->>AdapterRefs: OpenAPI spec & Operation
    Note right of XMLGenerator: Extracts references from the OpenAPI spec
    AdapterRefs->>XSDGenerator: API References
    Note right of AdapterRefs: Sends references to XSD generator
    destroy XSDGenerator
    XSDGenerator->>AdapterRefs: XSD
    Note right of XSDGenerator: Returns generated XSD
    destroy AdapterRefs
    AdapterRefs->>XMLGenerator: XSD
    Note right of AdapterRefs: Passes XSD back to XML generator
    XMLGenerator->>AdapterExits: Operations (GET, POST etc.)
    Note right of XMLGenerator: Extracts operation details
    destroy AdapterExits
    AdapterExits->>XMLGenerator: List of Response objects
    Note right of AdapterExits: Returns list of response objects
    XMLGenerator->>Handlebars: Template as String
    Note right of XMLGenerator: Loads the appropriate template
    destroy Handlebars
    Handlebars->>Templates: Compile<br/> Template
    Note right of Handlebars: Compiles the template with Handlebars
    destroy Templates
    Templates->>XMLGenerator: Compiled Template
    Note right of Templates: Returns the compiled template
    XMLGenerator->>PrettyPrinter: XML String
    Note right of XMLGenerator: Sends the XML string for pretty printing
    destroy PrettyPrinter
    PrettyPrinter->>XMLGenerator: Pretty XML String
    Note right of PrettyPrinter: Returns the formatted XML string
    destroy XMLGenerator
    XMLGenerator->>OpenapiFrankadapterApplication: XML
    Note right of XMLGenerator: Returns the final XML
    OpenapiFrankadapterApplication->>App/Site: Zip file
    Note right of OpenapiFrankadapterApplication: Sends the generated files as a zip

```
