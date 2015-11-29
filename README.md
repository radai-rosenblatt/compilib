# compilib
a small utility library for compiling java source into *.class objects at runtime. primarily aimed at writing unit tests:
```java
Map<String, String> sources = new HashMap<>();
sources.put(
        "package a.b;\n" +
        "public interface Consts {\n" +
        "    int A = 10;\n" +
        "}");
sources.put(
        "package a.c;\n" +
        "import a.b.Consts;\n" +
        "public enum SomeEnum {\n" +
        "    X(0), Y(Consts.A);\n" +
        "    private final int value;\n" +
        "    SomeEnum(int value) {\n" +
        "        this.value = value;\n" +
        "    }\n" +
        "}");
Map<String, Class<?>> compiledClasses = Compilib.compile(sources);
```
# how to use in your project
add the [jitpack.io](https://jitpack.io/) maven repository to your project, like so:
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```
then add a dependency on compilib:
```xml
<dependency>
    <groupId>com.github.radai-rosenblatt</groupId>
    <artifactId>compilib</artifactId>
    <version>1.0</version>
</dependency>
```