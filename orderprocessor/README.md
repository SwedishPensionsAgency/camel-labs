# Order processor project

## Description

A simple [Apache Camel] application, which currently read and validate XML files with dummy orders against `orders.xsd` located on the classpath using [File Component] and [Validator Component].

The endpoints where/how to consume, validate and produce (i.e. write) orders are configurable. See [Configure Endpoints] and [Property Placeholder] for more information.

## Build

Run `mvn clean package` commands.

### Test

There are a couple of simple unit tests using [JUnit] and out-of-the-box test support utilities from [Apache Camel]. See [Camel Testing] for more information.

## Run

With default configuration:

``` shell script
java -jar order-processor-1.0-SNAPSHOT-jar-with-dependencies.jar
```

### Configuration

The [Properties Component] will load configuration in `application.properties` on classpath by default, set the Java system property `properties.location` to override it:

``` shell script
java -Dproperties.location=./somedirectory/foo.properties -jar [...]
```


[Apache Maven]: https://maven.apache.org/
[Apache Camel]: https://camel.apache.org
[File Component]: https://camel.apache.org/components/latest/file-component.html
[Validator Component]: https://camel.apache.org/components/latest/validator-component.html
[Properties Component]: https://camel.apache.org/components/latest/properties-component.html
[Configure Endpoints]: https://camel.apache.org/manual/latest/faq/how-do-i-configure-endpoints.html
[Property Placeholder]: https://camel.apache.org/manual/latest/using-propertyplaceholder.html
[Camel Testing]: https://camel.apache.org/manual/latest/testing.html
[JUnit]: https://junit.org/junit4/
