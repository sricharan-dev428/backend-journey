# Java Exceptions — Quick Interview Review

## 1. Exception flow

```text
methodC()
   ↓ throws exception
methodB()
   ↓ no matching catch
methodA()
   ↓ no matching catch
main()
   ↓ no matching catch
Thread terminates
```

Java **unwinds the call stack**:

```text
current frame
   ↓
matching catch?
   ├─ YES → handle it
   └─ NO  → pop frame
              ↓
          check caller
              ↓
            repeat
```

---

## 2. Checked vs Unchecked

```text
Exception
│
├── Checked Exceptions
│   ├── IOException
│   └── SQLException
│
└── RuntimeException   ← unchecked
    ├── NullPointerException
    └── IndexOutOfBoundsException
```

### Checked

```text
External failure
     ↓
Correct code may still encounter it
     ↓
Compiler requires:
CATCH it
   OR
DECLARE throws
```

Example:

```java
void read() throws IOException {
    ...
}
```

Think:

> "My code may be correct, but the outside world can still fail."

Examples: missing file, network failure, database failure.

---

## 3. Unchecked

```text
Programming bug / invalid usage
        ↓
Usually preventable by fixing code
        ↓
Compiler does NOT force catch
```

Examples:

```java
list.get(99);        // IndexOutOfBoundsException
object.method();     // NPE if object == null
```

Why not force catches?

```text
If Java forced handling for every possible bug:

try
try
try
try
everywhere
```

Usually the right response is to **fix the bug**, not catch it.

---

## 4. Catch vs Propagate

```text
Exception occurs
      ↓
Can THIS method meaningfully recover?
      │
   ┌──┴──┐
  YES    NO
   ↓      ↓
 catch   propagate
```

Good catches:

```text
missing optional config
        ↓
use safe default
```

```text
temporary failure
      ↓
retry
```

Bad catch:

```java
catch (IOException e) {
    e.printStackTrace();
    return null;
}
```

Why bad?

```text
failure happened
      ↓
printed
      ↓
program continues
      ↓
caller may receive garbage/null
```

**Catch only when you can actually do something useful.**

---

## 5. `throw` vs `throws`

```text
throw
 ↓
Actually throws an exception NOW
```

```java
throw new IOException("Failed");
```

```text
throws
  ↓
Method declaration says:
"This exception may leave this method."
```

```java
void load() throws IOException
```

If the method catches and fully handles the exception:

```text
exception does NOT leave method
        ↓
usually no `throws` needed
```

---

## 6. Catch matching

```text
Thrown object:
FileNotFoundException
        ↓
Is it FileNotFoundException? ✓
Is it IOException?           ✓
Is it Exception?             ✓
```

Java matches using the **exception class hierarchy**.

Order catches:

```java
catch (FileNotFoundException e) { ... }
catch (IOException e) { ... }
catch (Exception e) { ... }
```

Specific → general.

---

## 7. Stack trace reading

```text
ExceptionType: message
        ↓
skip Java/internal frames
        ↓
find first frame in MY code
        ↓
inspect that line
        ↓
read remaining call path
```

Example:

```text
IndexOutOfBoundsException
    at ConfigReader.load(ConfigReader.java:18)  ← start here
    at Main.main(Main.java:7)
```

For nested exceptions:

```text
Caused by:
```

Read that **after understanding the main trace**.

---

## 8. Common failure cases

### Missing file

```text
read file
   ↓
file absent
   ↓
IOException / FileNotFoundException
```

### Empty file

```text
file exists
   ↓
read succeeds
   ↓
list is empty
   ↓
content.get(0)
   ↓
IndexOutOfBoundsException
```

Handling one failure does **not** automatically handle the next one.

---

---

# Interview answer

### Checked vs unchecked

```text
Checked:
External/recoverable conditions that correct code may still encounter.
Java forces catch-or-declare.

Unchecked:
Usually programming mistakes or invalid API usage.
Java does not force handling because those bugs should normally be fixed.
```

Examples:

```text
IOException             → checked
SQLException            → checked

IndexOutOfBoundsException → unchecked
NullPointerException      → unchecked
```

### Why are checked exceptions criticized?

```text
Compiler forces handling
        ↓
developers may write useless catch blocks
        ↓
or add `throws` through many callers
        ↓
extra noise without real recovery
```

---

# One rule to remember

```text
Can I genuinely recover here?

YES → catch
NO  → let it propagate
```