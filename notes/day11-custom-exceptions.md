# Day 11 — Custom Exceptions

**Phase 1 (Core Java)** · 09/17/2026 · Follows Day 10 (finally, try-with-resources)

---

## The problem

`ConfigReader.readConfig()` can fail three ways:

| # | Failure | Covered by |
|---|---------|-----------|
| 1 | File doesn't exist | `IOException` (JDK) |
| 2 | Required key missing | needs own type |
| 3 | Key present, value unparseable | needs own type |

Throwing `IllegalArgumentException` for both 2 and 3 compiles and runs.
So why not?

---

## Why a shared type fails

`catch` dispatches on **type**. That is the only thing it can select on.
One type = one branch, no matter how different the failures are.

A caller that needs to tell them apart has one option left:

```java
catch (IllegalArgumentException e) {
    if (e.getMessage().contains("missing")) {   // <-- the failure
        ...
    }
}
```

The message text is now load-bearing. Reword it for clarity in six months
and this silently breaks — no compile error, no test failure.

> **Rule:** anything a caller decides on belongs in the **type**.
> The **message** is for a human reading logs at 2am.

---

## Checked or unchecked?

Day 9 rule (first half):

- **unchecked** = bug in calling code, preventable by writing it correctly
- **checked** = external condition no correct code prevents

Config file contents are outside the caller's control → not a bug → checked.

**But the rule has a second half**, and it pulls the other way:

> catch ONLY when there's something real to do. No meaningful fallback → propagate.

Nothing can recover from a missing DB URL. So it propagates — and a *checked*
exception taxes every frame on the way up:

```
main()                                                    <- only real handler
  └─ startApplication()   throws MissingConfigKeyException   <- boilerplate
       └─ initDatabase()  throws MissingConfigKeyException   <- boilerplate
            └─ loadSettings()  throws MissingConfigKeyException  <- boilerplate
                 └─ readConfig()  throws MissingConfigKeyException  <- real throw site
```

This is why Spring wraps `SQLException` as an unchecked `DataAccessException` —
not because DB failures are bugs, but because callers can't act on them, so
catch-or-declare costs boilerplate and buys nothing.

**Two axes, not one:**

1. Preventable by correct calling code? → no = not a candidate for unchecked-as-bug
2. Can a caller act on it, and **how far** is that caller from the throw site?
   - close + actionable → checked earns its keep
   - distant + unrecoverable → unchecked

**Decision here:** checked. Config is read once at startup; `main` is the direct
handler and does something real (print operator message, exit 1). Short chain,
acceptable boilerplate.

---

## Structured fields, not prose

The operator needs to know *which* key was missing. If it lives only in the
message, a caller has to dig it back out of English:

```java
String key = msg.substring(msg.lastIndexOf(": ") + 2);   // fragile
String key = e.getKey();                                 // compiler-checked
```

An exception is an **object**. It can carry data as fields.

```java
public class MissingConfigKeyException extends Exception {
    private final String key;                       // final: the failure already happened

    public MissingConfigKeyException(String key) {
        super("Missing required config key: " + key);   // class builds its own message
        this.key = key;
    }

    public String getKey() { return key; }
}
```

Caller passes the **key**, not a message → wording stays consistent everywhere.

---

## Chaining: don't destroy the cause

`URI.create(value)` throws `IllegalArgumentException`. You catch it and throw
your own instead. What happens to the original?

**Gone.** Out of scope, garbage collected. Every detail about *what* the parser
objected to — malformed scheme, illegal character, missing host — destroyed.
The stack trace now points at your own `throw` statement.

`Throwable` carries a `cause` field: a reference to another `Throwable`.

```
InvalidConfigValueException
   message: "Invalid value for config key 'database.url': not-a-real-url"
   key:     "database.url"
   value:   "not-a-real-url"
   cause:  ──────┐
                 ▼
         IllegalArgumentException
            message: "Illegal character in scheme name at index 0"
            stack:   ...at java.net.URI.create(URI.java:852)
            cause:   null
```

Uncaught, the JVM prints the whole chain:

```
Exception in thread "main" InvalidConfigValueException: Invalid value for config key 'database.url': not-a-real-url
    at ConfigReader.readConfig(ConfigReader.java:42)
    at Main.main(Main.java:8)
Caused by: java.lang.IllegalArgumentException: Illegal character in scheme name at index 0
    at java.base/java.net.URI.create(URI.java:852)
    at ConfigReader.readConfig(ConfigReader.java:40)
    ... 1 more
```

Top = domain meaning (what a human cares about).
`Caused by:` = technical origin (what a debugger needs).

> **Rule:** catching one exception and throwing another? Pass the original as
> the cause. Always. A message is a String; a cause carries its own stack trace.

**Why not just append it to the message?** A String can't hold the line number
inside `URI.create()` where it actually failed.

---

## Final class

```java
public class InvalidConfigValueException extends Exception {
    private final String key;
    private final String value;

    // convenience constructor — nothing threw underneath
    public InvalidConfigValueException(String key, String value) {
        this(key, value, null);
    }

    public InvalidConfigValueException(String key, String value, Throwable cause) {
        super("Invalid value for config key '" + key + "': " + value, cause);
        this.key = key;
        this.value = value;
    }

    public String getKey()   { return key; }
    public String getValue() { return value; }
}
```

**Constructor delegation:** `this(key, value, null)` — one constructor holds the
real logic, the other funnels in. Message-building exists in exactly one place,
so it can't drift. The JDK does this everywhere (`Throwable` has four).

Two-arg version exists because not every instance has a cause — validating
`value.isEmpty()` yourself means nothing threw.

---

## Mistakes hit today

| Mistake | Consequence |
|---------|-------------|
| `String Value` vs field `value` | would compile, assign field to itself, silent `null` |
| `valie` typo | `cannot find symbol` — the *good* kind of typo |
| missing closing brace | `reached end of file while parsing` |
| lost the parser exception | dead end in production three weeks later |

A typo that costs a compile error is a good day. A typo that costs a silent
null is the bad version — and `Value`/`value` was one keystroke away.

---

## Interview answer — 4 anchors

Not sentences. Anchors. Words get generated fresh each time.

```
1. DEFAULT    use the JDK one
2. EXAMPLE    config reader — missing key vs bad value
3. CARRIES    key as a field  +  original underneath     <- one chunk, not two
4. CLOSER     easier to handle in code, easier to diagnose in prod
```

Anchor 3 must stay paired — splitting it means the summary arrives early and
closes the door on chaining.

Vocabulary: **parse** (pull structure out of text), not "pass". **Threw**
underneath, not "through".

---

## Open

- [ ] Step 5 not done: let it fly uncaught from `main`, see the real `Caused by:`
- [ ] Next: `readConfig()` throwing both types; catch separately; show what
      the caller does *differently* in each branch
