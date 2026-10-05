# Day 12 — Config / ConfigReader Design

**Phase 1 (Core Java)** · 10/05/2026 · bad day, ~25 min · follows Day 11 (custom exceptions)

---

## One class or two?

First draft put `readConfig()` inside `Config` — a Config that reads configs.
Two different jobs wearing one name:

```
Config          holds values            url, port
ConfigReader    produces values         parse, validate, throw
```

Separate them. `ConfigReader` is the only place that knows about raw strings
and failure; `Config` is what everything downstream actually uses.

```
Map<String,String>          ConfigReader.readConfig()           Config
 "database.url" -> "..."  ──────────────────────────────►   url  : String
 "server.port"  -> "8080"     validate, convert, throw        port : int
                                                              (final, set once)
```

---

## Convert once, at the boundary

`Config.port` is `int`, not `String`.

If it stored `"8080"`, every caller that wants to open a port converts it
again — and can fail again, in a place with no idea which config key it came
from.

> **Rule:** convert and validate at the boundary. Past that line, the rest of
> the program works with values that are already correct.

Same reason the fields are `final`: constructed valid, can't drift afterward.

---

## The signature

```java
public Config readConfig(Map<String, String> config)
        throws InvalidConfigValueException, MissingConfigKeyException
```

Both exceptions checked (Day 11 decision: startup read, `main` is the direct
handler and can act). File I/O deliberately skipped — the Map is the config
already loaded, so this stays about exceptions rather than `Files`.

---

## Overloading — compiles, still a smell

Two methods named `readConfig` in the same class:

| Signature | Does |
|---|---|
| `readConfig(String path)` | reads a file, returns first line, falls back to `"default"` |
| `readConfig(Map<String,String>)` | validates, returns a `Config`, throws |

Same name, unrelated jobs. A reader has to check parameter types to know which
is which. Overloading is for *the same operation* on different inputs, not for
two operations that happen to share a word.

The String one is dead Day 9 code, called by nothing → delete it.

> Dead code kept "in case we need it later" is cost with no benefit.
> Git already has it (`888310f`). That is what version control is for.

---

## Open

- [ ] `Config` has `final` fields and no constructor — nothing can set them.
      Constructor + getters next.
- [ ] Still untested from Sept 20: the **two axes** for checked vs unchecked
      1. preventable by correct calling code?
      2. can a caller act, and how far is it from the throw site?
- [ ] Then: `readConfig` body — missing key vs unparseable vs out-of-range port
      (`parseInt("70000")` succeeds; 70000 is not a port)

---

## Note on these notes

25-minute session, ~70 lines. Day 11 was 90 minutes, ~200 lines.
Notes scale with the session. A short day does not owe you a long document.
