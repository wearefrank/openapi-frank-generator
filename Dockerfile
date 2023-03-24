# BUILD image
FROM maven:3.9 AS builder

# create app folder for sources
RUN mkdir -p /build
WORKDIR /build

# Copy pom.xml first to prevent unnecessary downloads when source code changes
COPY pom.xml /build/

# Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins

# Copy source code
COPY src /build/src

# Build application
RUN mvn package

# RUN image
FROM tomcat:11.0

# Copy the war file to tomcat's webapps directory
COPY --from=builder /build/target/*.war /usr/local/tomcat/webapps/waf.war

# Expose port 8080
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]

