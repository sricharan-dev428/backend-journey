Day 7: 08/29/2026
Phase 1/2 (collections) — ArrayList internals. Derived resizing from scratch:
grow-by-1 = ~499,500 copies for 1,000 adds vs doubling = 1,023. Learned
amortized O(1) (worst case per call still O(n); geometric growth means expensive
calls get rarer at the same rate they get costlier; tradeoff = wasted memory).
Shift vs swap; add(0,x)/remove(0) are O(n). Wrote ListDemo.java: growable int[],
doubling, bounds-checked get() against size (not length), private fields.
Predicted size/capacity/resizes BEFORE running — matched exactly. Test made
able to fail (TEST FAILED branch). Explained amortized O(1) cold, unprompted,
interview-standard.
Retrieval: 6/6 cold — HashMap, equals/hashCode, HashSet, SQL anti-join.
"equals compares hash codes" did NOT resurface — retest ~Sept 5.
Weak: reading own code for intent not what's written (knew get() should throw,
wrote raw array access); predicted capacity but didn't verify it.
Skipped remove() deliberately — mechanism already understood, no new concept.
Next: exceptions — start from the problem they solve, not syntax.

Day 6: 08/28/2026
Phase 1 — Set/HashSet. Wrote SetDemo.java: dedup via HashSet, add() return
value as duplicate check, contains(), unspecified iteration order, Point in a
Set. Deliberately commented out hashCode() and watched size go 1 -> 2:
contract violation observed, not just read.
Learned: HashSet IS a HashMap using only keys with a dummy value; put()
replaces the VALUE, keeps the original KEY (so the second equal object is
discarded); Set vs Map decided by whether you need associated data.
Phase 3 — anti-join written cold and correct on 3rd attempt.
Weak: saying Set stores "keys" (it stores elements); building a test that
couldn't fail (added one Point instead of two).
Next: recap, then choose — more collections (List/ArrayList internals) or
back to SQL.

Day 5: 08/27/2026
Phase 1 — equals() and hashCode(). Wrote PointDemo.java: watched two
value-identical objects fail as HashMap keys (different identity hashes,
size grew to 2), then overrode both methods on Point and saw put/get behave
correctly (size stayed 1). Hash written by hand as x*31+y.
Learned: Object.equals() IS ==; identity vs equality; both methods must be
driven by the SAME fields; mutating a key in-place makes the entry
unreachable while size() still counts it.
Weak: "equals compares hash codes" resurfaced (it compares KEYS);
reading own code for intent instead of what's written (SQL alias bug).
Next: recap + start Set / HashSet, or SQL joins practice.

Day 4: 08/26/2026
Phase 1 — HashMap in code. Wrote UserRegistry.java: put/get/containsKey/
getOrDefault, duplicate-key overwrite, keySet/values/entrySet iteration.
Compiled with javac, ran with java — saw .class bytecode produced.
Phase 0 — added .gitignore for *.class; learned .gitignore only affects
UNTRACKED files (git rm --cached for already-tracked), and that committed
secrets must be rotated, not deleted.
Weak: skipping predictions before running; "equals compares hash codes"
(it compares the keys); saying iteration order is "random" (it's unspecified).
Next: equals() and hashCode() contract — write a class used as a HashMap key.



Day 3: 08/24/2026
Phase 1 (Core Java) — HashMap, concept only, no code yet.
Derived from scratch: hashing, modulo compression, buckets, collisions
(pigeonhole), separate chaining, hashCode() picks the bucket / equals() picks
the entry, degeneration to O(n), mutable-key trap.
Weak: saying "calculate a key" instead of "hash the key"; explaining the
mutable-key problem out loud (fix: narrate put -> mutate -> lookup).
Next: write HashMap code — put, get, containsKey, iterate.
Implemented HashMap code


Day 2: 08/22/2026
Phase 0 (Git). Completed full local->remote loop: init, add, commit, remote add,
push. Debugged a rejected push (unrelated histories) and fixed it with pull
--allow-unrelated-histories --no-rebase. SSH auth already working.
Weak: repository (local, .git) vs remote (GitHub) — commit != push. Rebase unclear.
Next: HashMap.


Day 1: 08/18/2026
Phase 3 (SQL) — joins. Learned: a join returns one row per matching PAIR, not
one per user; INNER drops unmatched rows, LEFT keeps them with NULLs; NULL means
"no value" (use IS NULL, not = NULL); LEFT JOIN + IS NULL = anti-join.
Weak: pausing to reason from the DB's perspective before answering.
Next: predict LEFT JOIN result by hand, then verify. HashMap after that.

