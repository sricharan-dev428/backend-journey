# Set, HashSet & HashMap — Recap

## ⭐ Interview Key Points

### 1. What is a HashSet mechanically?

`HashSet` is backed by a `HashMap`.

The Set elements are internally stored as the Map's **keys**, with a dummy value.

```text
HashSet
   ↓
HashMap

element        dummy value
---------------------------
"ana"      ->  PRESENT
"ben"      ->  PRESENT
```

From the Set user's perspective, call them **elements**, not keys.

---

### 2. How does HashSet find duplicates?

```text
element
   ↓
hashCode()
   ↓
find bucket
   ↓
equals()
  /     \
true    false
 ↓        ↓
duplicate  add
```

Easy way to remember:

> `hashCode()` = WHERE to look
> `equals()` = is it ACTUALLY the same?

Same hash does **not** mean equal. Two different objects can be in the same bucket.

---

### 3. `equals()` without `hashCode()` — what breaks?

Contract:

```text
a.equals(b) == true
        ↓
a.hashCode() == b.hashCode()
```

If we override `equals()` but not `hashCode()`, two equal objects can have different hashes.

```text
Point(3,4) → hash 100 → bucket 2
Point(3,4) → hash 900 → bucket 7
```

So `HashSet` may look in different buckets and store both.

We actually tested:

```java
hsp.add(new Point(3, 4));
hsp.add(new Point(3, 4));
```

With correct `hashCode()`:

```text
size = 1
```

Without the `hashCode()` override:

```text
size = 2
```

---

### 4. Hash collisions / chains

Two objects can have the **same hash** but still be different.

```text
Bucket 5
   |
   ├── Point(1,1)
   ├── Point(2,2)
   └── Point(3,3)
```

If:

```text
same hashCode()
equals() == false
```

both can exist.

This can happen in both `HashMap` and `HashSet`.

---

### 5. Useful HashSet operations

```java
set.add(x);
set.contains(x);
set.remove(x);
```

Average lookup/add/remove:

```text
O(1)
```

`add()` is especially useful:

```java
if (!set.add(x)) {
    // duplicate
}
```

Because:

```text
true  → x was new
false → x already existed
```

So it's basically:

> **add it + tell me if it was new**

---

### 6. HashSet order

`HashSet` does **not guarantee insertion order**.

Don't say:

> "HashSet is random."

Say:

> "HashSet does not guarantee order."

If insertion order matters:

```text
LinkedHashSet
```

---

## Set vs Map

Don't start with the data structure.

Ask:

```text
What do I have?
What do I need?
```

### Set

Use when the question is:

> **Does X exist? / Have I already seen X?**

Examples:

```text
duplicate emails
unique usernames
visited IDs
```

### Map

Use when the question is:

> **Given X, what belongs to X?**

```text
employeeId → Employee
username   → User
shortCode  → originalUrl
```

---

## URL Shortener Example

Requirement:

> Someone visits `/abc123`. I need the original URL.

```text
I HAVE                 I NEED
abc123        →        original URL
```

A Set could tell me:

```text
Does abc123 exist? → yes
```

But it cannot tell me:

```text
What URL belongs to abc123?
```

So:

```java
Map<String, String> codeToUrl;
```

```text
abc123 → https://example.com/long-url
```

### General rule

> **Set = uniqueness / existence**
> **Map = association (`X → Y`)**

---

## ⚠️ Another HashMap/HashSet Trap

Don't mutate fields used by `equals()` / `hashCode()` while the object is inside a hash collection.

```java
Point p = new Point(3, 4);
set.add(p);

p.x = 99; // dangerous
```

The object was stored based on the old hash, but now its hash may have changed.

```text
inserted as Point(3,4)
        ↓
     bucket 5

change x to 99

new hash says
        ↓
     bucket 12

BUT object is still sitting in bucket 5
```

This can break lookups.

---

## Interview Quick Answers

**What is HashSet mechanically?**
A `HashSet` is backed by a `HashMap`; elements are stored internally as keys with a dummy value.

**How does HashSet detect duplicates?**
`hashCode()` finds where to look, then `equals()` checks logical equality.

**What if I override `equals()` but not `hashCode()`?**
Equal objects can have different hashes, so HashSet/HashMap may look in different buckets and fail to recognize them as equal.

**Can two objects have the same hash?**
Yes. That's a collision. If `equals()` is false, both can exist.

**Does HashSet preserve insertion order?**
No.

**Set vs Map?**
Set when I care about uniqueness/existence; Map when I need `X → associated Y`.

---

## 🧠 Mental Model

```text
              object
                ↓
            hashCode()
                ↓
          find bucket
                ↓
             equals()
            /        \
         true        false
          ↓            ↓
       same thing   different thing
```

> **`hashCode()` = WHERE?**
> **`equals()` = SAME?**
