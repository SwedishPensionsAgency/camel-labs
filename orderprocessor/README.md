# Order processor project

## Description

A simple [Apache Camel](https://access.redhat.com/documentation/en-us/red_hat_jboss_fuse/6.3/html/apache_camel_development_guide/index) application, which validates XML files against the `orders.xsd` file.

## Build

Run `clean package` [Apache Maven](https://maven.apache.org/) commands.

## Configuration

Set the Java system property `properties.location` if you want to use a different property file, otherwise the application will use `application.properties` on classpath.

## Usage

An example run with a property file after build  `java -Dproperties.location=./myconfig/foo.properties -jar order-processor-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Task

Improve the application with the following:

* Add logic to write valid orders to one or more files in an appropriate way.
* Filter out invalid orders in a simple way.

Feel free to be creative :-)

Note, the file could be very large so a good idea could be to use of a [splitter](https://camel.apache.org/splitter.html) (hint: see section "Streaming big XML payloads using Tokenizer language").