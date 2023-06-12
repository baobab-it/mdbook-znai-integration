# Demos Code Highlight

{{#znai

:include-java: assets/znai/HelloWorld.java {entry: "sampleMethod", bodyOnly: true}

endznai}}

<p data-lines="2:Text message,3:Another text message,1" data-comments_type_inline="true"></p>

{{#znai

:include-java: assets/znai/HelloWorld.java {entry: "sampleMethod", bodyOnly: true, commentsType: "inline"}

:include-java: assets/znail/HelloWorld.java {entry: "sampleMethod", bodyOnly: true, title: "my snippet"}

:include-java: assets/znai/HelloWorld.java {entry: "sampleMethod", signatureOnly: true}

:include-java: assets/znai/HelloWorld.java {entry: "sampleMethod", signatureOnly: true, entrySeparator: "..."}

:include-java: assets/znai/HelloWorld.java {entry: "sampleMethod(Map, int, boolean)"}

:include-java: assets/znai/MyEnum.java {entry: "MyEnum"}

:include-java: assets/znai/MyEnum.java {entry: "MyEnum", bodyOnly: true}

:include-java: assets/znai/HelloWorld.java {entry: ["createData", "importantAction"]}

:include-java: assets/znai/HelloWorld.java {entry: ["createData", "importantAction"], signatureOnly: true}

:include-java: assets/znai/HelloWorld.java {entry: "sampleMethod", signatureOnly: true}

:include-java: assets/znai/HelloWorldTest.java {
  entry: ["exampleOfA", "exampleOfB"],
  bodyOnly: true,
  title: "example of actions"}

:include-java: assets/znai/HelloWorldTest.java {
  entry: ["exampleOfA", "exampleOfB"],
  entrySeparator: "",
  bodyOnly: true,
  title: "example of actions"}

:include-java: assets/znai/HelloWorld.java {
  entry: "sampleMethod",
  bodyOnly: true,
  title: "my snippet",
  anchorId: "my-java-snippet"
}

:include-java-doc: assets/znai/HelloWorld.java

endznai}}


```rust,noplayground
validate();
process(p2); ➊
notifyAll(p1); ➋

return bestSample();
```
<div class="assets-block code-bullets"><div class="code-bullet-and-comment"><span class="znai-circle-badge">➊ </span><span class="code-bullet-comment">important comment</span></div><div class="code-bullet-and-comment"><span class="znai-circle-badge">➋ </span><span class="code-bullet-comment">very important</span></div></div>


```bash
#!/bin/bash

echo "hello world" # echo message

exit 0 # normal exit
```

<p data-comments_type_inline="true"></p>

```rust,noplayground
validation();
process(p2); // important comment
notifyAll(p1); // very imortant

return bestSample();
```

<p data-comments_type_inline="true"></p>
