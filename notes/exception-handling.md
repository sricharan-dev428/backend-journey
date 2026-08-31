# Java Exceptions — Quick Recap

## 1. Why exceptions?

### Return-code style

```text
validatePort()
   ↓ returns error
parseConfig()
   ↓ checks + returns error
readFile()
   ↓ checks + returns error
main()
   ↓ handles it
```

Problem: every method in the middle has to participate.

### Exception style

```text
validatePort()  ← throws
      ↓
parseConfig()   ← no catch, unwinds
      ↓
readFile()      ← no catch, unwinds
      ↓
main()          ← catches
```

The middle methods can do nothing.

---

## 2. What "propagates" means

```text
throw happens
     ↓
JVM looks for matching catch
     ↓
no catch in current method
     ↓
pop that stack frame
     ↓
check caller
     ↓
repeat
```

If no method catches it:

```text
level3 → level2 → level1 → main → thread ends
```

**An unwound method does not resume.**

---

## 3. Catching stops the unwind

```text
main
 ↓
level1   ← catch here
 ↓
level2
 ↓
level3   ← throws
```

Result:

```text
level3 removed
level2 removed
level1 catches
level1 continues
main continues
```

So:

```text
level2 end  ❌
level1 end  ✅
main end    ✅
```

---

## 4. Catch matching

```text
Throwable
├── Error
└── Exception
    └── RuntimeException
        ├── IllegalArgumentException
        ├── IllegalStateException
        ├── NullPointerException
        └── IndexOutOfBoundsException
```

Think:

```text
thrownException instanceof CatchType
```

Example:

```text
throw IllegalStateException

catch (IllegalArgumentException) ❌
catch (RuntimeException)         ✅
catch (Exception)                ✅
```

---

## 5. Catch order

Correct:

```text
specific first
general last
```

```java
catch (IllegalArgumentException e) { }
catch (Exception e) { }
```

Wrong:

```java
catch (Exception e) { }
catch (IllegalArgumentException e) { } // compile error
```

Why?

`Exception` already catches `IllegalArgumentException`, so the second block can never run.

---

## 6. Compile time vs runtime

```text
Compiler can prove unreachable → compile error
Catch simply does not match → runtime behavior
```

Examples:

```text
statement after unconditional throw → compile error
broad catch before narrow catch     → compile error
wrong catch type                    → compiles, just does not catch
```

---

## Core takeaway

```text
RETURN CODES:
check → return → check → return

EXCEPTIONS:
throw → unwind → catch
```

**Exception propagation = the JVM popping stack frames and searching upward for a matching catch.**
