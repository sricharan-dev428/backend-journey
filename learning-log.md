Day 1: 08/18/2026
Phase 3 (SQL) — joins. Learned: a join returns one row per matching PAIR, not
one per user; INNER drops unmatched rows, LEFT keeps them with NULLs; NULL means
"no value" (use IS NULL, not = NULL); LEFT JOIN + IS NULL = anti-join.
Weak: pausing to reason from the DB's perspective before answering.
Next: predict LEFT JOIN result by hand, then verify. HashMap after that.

Day 2: 08/22/2026
Phase 0 (Git). Completed full local->remote loop: init, add, commit, remote add,
push. Debugged a rejected push (unrelated histories) and fixed it with pull
--allow-unrelated-histories --no-rebase. SSH auth already working.
Weak: repository (local, .git) vs remote (GitHub) — commit != push. Rebase unclear.
Next: HashMap.