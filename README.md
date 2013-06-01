# Noobdown

The source code behind my Clojure beginner tutorial: TODO.

The spec:

* Nodes are separated by a blanklink.
* Elements within each node are separated by a newline.
* Nodes can't be nested within other nodes. (That would be so not Noobdown)

## Usage


``` clojure
(ns your-namespace
  (:require [noobdown.parse :as noobdown))

(def markup "# Heading 1

###### Header 6

Here's a paragraph

- Item A
- Item B

1. Item 1
200. Item 2

    (defn add [a b]
      (+ a b))

http://example.com")

(noobdown/to-html markup)
```

Output:

    <h1>Heading 1</h1>
    <h6>Header 6</h6>
    <p>Here's a paragraph</p>
    <ul><li>Item A</li><li>Item B</li></ul>
    <ol><li>Item 1</li><li>Item 2</li></ol>
    <code>    (defn add [a b]      (+ a b))</code>
    <a href=\"http://example.com\">http://example.com</a>"

## License

Copyright © 2013 Dan Neumann

Distributed under the Eclipse Public License, the same as Clojure.
