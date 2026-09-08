# Java Exceptions — Quick Recap

## Exception propagation / stack unwinding

If an exception is not handled in the current method, Java **unwinds the call stack**.

```text
exception happens
      ↓
matching catch in current method?
   ├─ yes → handle it
   └─ no  → leave this method
              ↓
          check caller
              ↓
            repeat
```

If nothing catches it, the thread ends.

---

## Checked vs Unchecked

### Checked exceptions
Examples: `IOException`, `SQLException`

- Can happen even if the code is correct.
- Usually caused by external things like files, database, network.
- Java forces us to either:
  - catch it
  - or declare it with `throws`

```java
void readFile() throws IOException {
}
```

### Unchecked exceptions
Examples: `NullPointerException`, `IndexOutOfBoundsException`

- Usually mean there is a bug or invalid usage in the code.
- Java does not force us to catch them.
- Normally we should fix the bug instead of adding `try/catch` everywhere.

---

## When to catch vs propagate

```text
Can this method actually recover?
        ↓
     yes / no
      ↓     ↓
    catch  propagate
```

Catch when we can do something useful:
- use a real default
- retry
- fallback

If we cannot handle it properly, let it propagate.

Bad example:

```java
catch (IOException e) {
    e.printStackTrace();
    return null;
}
```

This can hide the real failure and make the caller receive bad data.

---

## `throw` vs `throws`

`throw` = actually throws an exception.

```java
throw new RuntimeException("boom");
```

`throws` = says the exception may leave this method.

```java
void load() throws IOException {
}
```

If the method catches and fully handles the exception, it usually should not still declare that same `throws`.

---

## Catch ordering

Catch from more specific to more general.

```java
catch (FileNotFoundException e) {
}
catch (IOException e) {
}
catch (Exception e) {
}
```

Java checks exception types using the class hierarchy.

---

## `finally`

`finally` runs before control completely leaves the `try/catch`.

It runs when:
- the code finishes normally
- there is a `return`
- an exception is propagating

This is why it is different from just writing cleanup code after the `try/catch`.

```java
try {
    return 1;
} finally {
    System.out.println("runs before return");
}
```

The return value is calculated first, then `finally` runs, then the method actually returns.

---

## Never `return` from `finally`

```java
try {
    return 1;
} finally {
    return 2;
}
```

The `2` replaces the already calculated `1`.

Even worse:

```java
try {
    throw new RuntimeException("boom");
} finally {
    return 3;
}
```

The exception disappears and the method returns `3`.

So use `finally` for cleanup only.

---

## Try-with-resources

Modern way to close resources:

```java
try (Scanner scanner = new Scanner(new File(path))) {
    // use scanner
}
```

The resource closes automatically even if there is a `return` or exception.

Prefer this over manually calling `close()` in `finally`.

Works with things that implement `AutoCloseable`, like files, streams, sockets, and JDBC connections.

---

## Stack trace reading

```text
Exception type + message
        ↓
find first frame from MY code
        ↓
check that line
        ↓
read the call path
        ↓
then check "Caused by:"
```

---

## Stale `.class` reminder

```text
.java --javac--> .class --java--> program
```

`java ConfigReader` runs the `.class` file, not the `.java` file.

If output looks impossible, ask:

**Am I actually running the code I am looking at?**

---

## Interview recap

**Checked:** external conditions that correct code may still encounter. Java forces catch-or-declare.

**Unchecked:** usually bugs or invalid usage. Java does not force catches because these should normally be fixed.

**Catch:** only when this method can actually recover.

**Propagate:** when this method cannot handle the problem properly.

**Finally:** runs before control leaves the try/catch, including return and exception paths.

**Try-with-resources:** preferred way to automatically close resources.
