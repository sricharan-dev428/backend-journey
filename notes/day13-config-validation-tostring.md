# Day 13 — Validation at the Boundary, and toString()

**Phase 1 (Core Java)** · 10/13–14/2026 · follows Day 12 (Config/ConfigReader split)
**Closes exceptions.** Both custom types from Day 11 now used for real.

---

## Config — an immutable value object

```java
public class Config {
    private final String url;
    private final int port;
    // constructor validates, then assigns
    // getters only — no setters
}
```

Three properties, each for a reason:

| Property | Why |
|---|---|
| `final` fields | constructed once, can't drift afterward |
| no setters | nothing can change the port out from under code that already read it |
| validates in constructor | **if you hold a Config, it is valid** — no exceptions |

That last line is the class's *promise*. The whole design exists to keep it.

> Safe to share across threads without locks, for free — because nothing can
> mutate it. (Phase 2 material; this is where it comes from.)

---

## Constructor throws = no object exists

A constructor can throw like any method. If it does, **the object is never
created** — the reference is never assigned, and there is no such thing as a
half-built `Config`.

```java
Config(String url, int port) {
    if (url == null || url.isBlank()) {
        throw new IllegalArgumentException("Invalid URL: " + url);
    }
    try {
        URI.create(url);
    } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid URL: " + url, e);
    }
    if (port < 1 || port > 65535) {
        throw new IllegalArgumentException("Port out of range: " + port);
    }
    this.url = url;
    this.port = port;
}
```

**This is what encapsulation is actually for.** Not "make fields private" —
*protecting the invariant*. The constructor is the last gate before the object
exists. Without a check there, the promise depends on every caller remembering.

**Why `IllegalArgumentException`, unchecked?** Run the two axes:
1. Is `new Config("x", -1)` preventable by correct calling code? **Yes** → it's
   a caller bug → unchecked.
2. (moot once axis 1 says bug)

Custom *checked* exceptions are for the config **file** being wrong.
`IllegalArgumentException` is for the **programmer** being wrong. Different
failures, different types.

### Two gotchas in that constructor

```java
if (url == null || url.isBlank())    // correct
if (url.isBlank() || url == null)    // NPE inside the check meant to prevent it
```

`||` short-circuits left to right. Null check **must** come first.

```java
URI.create(url);   // return value ignored — called for the exception it throws
```

`URI.create` **parses**, it doesn't connect to anything. Used here purely as a
validator: the parser is the authority on what's valid, not hand-written checks.
`URI.create(null)` throws `NullPointerException`, not `IllegalArgumentException`
— which is why the null check is separate and first.

---

## readConfig — the flow

```
Map<String,String>
      │
      ├─ get("database.url")  == null?  ──► MissingConfigKeyException("database.url")
      ├─ get("server.port")   == null?  ──► MissingConfigKeyException("server.port")
      │
      ├─ parseInt(portValue)  throws?   ──► InvalidConfigValueException(key, value, e)
      │                                      ^^^^^^^^^^^^^^^^^ cause chained
      ├─ URI.create(url)      throws?   ──► InvalidConfigValueException(key, value, e)
      ├─ !uri.isAbsolute()              ──► InvalidConfigValueException(key, value)
      ├─ port out of 1..65535           ──► InvalidConfigValueException(key, value)
      │                                                            no cause — nothing threw
      ▼
  new Config(url, port)
```

Note which throws carry a cause and which don't. **A cause exists only when
something else threw first.** The range check fires on a value that parsed
cleanly — `getCause()` returns `null`, and that's accurate, not a gap.

`isAbsolute()` matters: `URI.create("hello")` parses fine as a *relative* URI.
Parsing successfully and being a usable database URL are different questions.

---

## Why check the port range twice?

`Config`'s constructor already rejects 70000. So why does `readConfig` check too?

```
Config constructor:   "Port out of range: 70000"
                       ^ protects the object, for ANY caller
                       ^ but never says WHICH config key

readConfig:           "Invalid value for config key 'server.port': 70000"
                       ^ adds the context an operator needs to fix the file
```

Two checks, two jobs. Not duplication.

Also: `IllegalArgumentException` is unchecked, so it would escape `readConfig`
regardless of the `throws` clause. Catching the problem *before* it reaches the
constructor is what keeps the key attached.

> **Pattern:** convert and validate at the **boundary**. Past that line the rest
> of the program works with values already known correct — nobody converts
> `"8080"` twice, and nobody fails far from the source of the mistake.

---

## The same-typed-parameter trap

Written, compiled, ran, and was wrong:

```java
throw new InvalidConfigValueException(url, portValue, e);
//                                    ^^^ this is the KEY parameter
```

Produced:

```
Invalid value for config key 'postgres://localhost:5432/mydb': abc
```

`getKey()` returns the URL. The message claims the URL is a config key.
**No compiler complaint** — all parameters are `String`, so the types line up
perfectly while the meaning is inverted.

> Three `String` parameters in a row is a position-order trap. Types protect
> nothing when everything is the same type. Read the call against the
> declaration, argument by argument.

Second instance, same day: changed an error **message** to say `"Invalid URL"`
while the **check** underneath still only tested null-or-blank. The code then
*looked* more correct than it was, because the message agreed with the intent.

---

## Tests: why a loop, not three calls

```java
Config c1 = new Config(valid, 8080);        // ok
Config c2 = new Config("hello world", 8080); // throws → unwinds main
Config c3 = new Config("", 8080);            // NEVER RUNS
```

The throw unwinds `main` (Day 8). Case 3 is silently skipped — you'd have
"verified" one case and not noticed.

```java
for (Map<String,String> test : tests) {
    try   { ... }
    catch (MissingConfigKeyException e)  { ... }
    catch (InvalidConfigValueException e){ ... }
}
```

Each iteration isolated → every case runs, every result printed.

> **This is what JUnit does.** Run each test in isolation so one failure can't
> hide the others, then report all results together. Built the crude version
> here by reasoning about stack unwinding. JUnit is next after Maven.

Four cases, chosen because they take different paths:

| Input | Path |
|---|---|
| valid | success |
| no `server.port` | missing-key branch |
| `"abc"` | `parseInt` throws → cause chained |
| `"70000"` | parses fine → range check → no cause |

4/4 predictions exact.

---

## toString()

Default output was `Config@15db9742` — `Object.toString()`: class name, `@`,
identity hash in hex (Day 5). String concatenation (`"Success: " + result`)
calls `toString()` automatically.

```java
@Override
public String toString() {
    // Never include credentials here — toString() output reaches logs.
    return "Config{url='" + url + "', port=" + port + "}";
}
```

**`@Override` does real work.** It tells the compiler this must match a parent
method. Typo it (`toSting()`, or a different signature) and you get an error —
without it you'd silently define a *new* method, `Object.toString()` would still
be called, and you'd see `Config@15db9742` while staring at code that looks
right.

**Quotes around the url** make a trailing space or empty string visible.
`url=` is ambiguous; `url=''` is not.

**Credentials:** `toString()` output lands in logs — written to disk, shipped to
aggregators, read by people with no business seeing production secrets. If
`Config` ever holds a password, it does **not** go here. The comment records the
decision so future-you doesn't undo it without noticing.

---

## Vocabulary

- **invariant** — a condition the class guarantees is always true of its objects
- **value object** — immutable, defined by its values, no identity of its own
- **boundary** — the line where untrusted input becomes validated program data

---

## Open

- [ ] Delete dead `readConfig(String)` from Day 9 — Git has it at `888310f`
- [ ] `Main` may still declare `throws InvalidConfigValueException` it no longer needs
- [ ] One line worth adding to the test loop: `System.out.println("Cause: " + e.getCause());`
      — makes the chained-vs-not distinction visible (test 3 shows
      `NumberFormatException`, test 4 shows `null`)
- [ ] **Next: Maven.** Three hand-compiled files works today and stops working
      the moment you want a library. Blocks JUnit, blocks Spring Boot.
