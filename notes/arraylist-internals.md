# ArrayList Internals --- Quick Recovery Sheet

> **Goal:** If I forget this topic, this page should bring it back fast.

## 1) What I learned / built

I built a small `ListDemo` on top of a raw `int[]`.

**Core idea:** an ArrayList is basically a fixed-size array + a `size`
counter + logic to replace the array with a bigger one when it fills.

``` text
After adding: 10, 20, 30, 40, 50

index:       0    1    2    3    4    5    6    7
           +----+----+----+----+----+----+----+----+
elements = | 10 | 20 | 30 | 40 | 50 |  0 |  0 |  0 |
           +----+----+----+----+----+----+----+----+
             <------ size = 5 ----->
             <----------- capacity = 8 ----------->
```

-   `size` = how many **real elements** I stored.
-   `capacity` = `elements.length`, how many slots currently exist.
-   Next append goes to `elements[size]`.
-   Grow when `size == elements.length`.

## 2) What happens when the array is full?

``` text
OLD                         NEW
+--+--+--+--+              +--+--+--+--+--+--+--+--+
|10|20|30|40|   copy -->   |10|20|30|40|  |  |  |  |
+--+--+--+--+              +--+--+--+--+--+--+--+--+
 capacity 4                         capacity 8
```

1.  Allocate a bigger array.
2.  Copy the old elements.
3.  Point `elements` at the new array.
4.  Old array becomes unreachable; GC can reclaim it later.
5.  Add the new value.

I do **not** manually erase/free the old array in Java.

## 3) Why grow by a factor instead of +1?

For 1,000 adds:

``` text
grow by 1   -> 499,500 copies
doubling    ->   1,023 copies
```

Grow-by-1 keeps copying almost the entire list on every add.

Doubling:

``` text
1 -> 2 -> 4 -> 8 -> 16 -> ... -> 1024

copies: 1 + 2 + 4 + 8 + ... + 512 = 1,023
```

That is the reason end-of-list `add()` can be **amortized O(1)**.

## 4) Interview answer: Why is `ArrayList.add()` amortized O(1)?

Most adds just write into the next empty slot, so they are **O(1)**.

Sometimes the backing array is full. Then ArrayList has to allocate a
bigger array and copy the existing elements, so that **single add is
O(n)**.

But resizing happens rarely because capacity grows by a factor. Across
`n` adds, the total copying work is still O(n), so spread across all the
adds the cost per add is **O(1) amortized**.

**Amortized = an individual operation may be expensive, but over a
sequence of operations the average cost per operation is constant.**

**Tradeoff:** speed for space --- after growing, some capacity is
unused.

## 5) My actual mistake: the `get()` hole

I said an invalid index should fail, but my first code was basically:

``` java
return elements[index];
```

With `size = 5` and `capacity = 8`, `get(5)` does **not** fail at the
array level. Index 5 physically exists and contains Java's default `int`
value `0`.

``` text
real list data                 unused capacity
[10][20][30][40][50]          [0][0][0]
 0   1   2   3   4             5  6  7
         size = 5          capacity = 8
```

So the list must reject:

``` java
if (index < 0 || index >= size) {
    throw new IndexOutOfBoundsException(...);
}
```

### Why check `size`, not `elements.length`?

Because `elements.length` describes **physical storage**.\
`size` describes the **logical list**.

Indexes 5--7 may physically exist, but they are not elements the caller
added.

### The underlying mistake

I understood what the code **should** do, but didn't verify what the
code I actually wrote **would** do.

**Lesson:** read the code for its actual behavior, not my intention.

## 6) Testing habit I practiced

I predicted **before running**:

``` text
5 adds from capacity 1

size     = 5
capacity = 8

resize lines:
1 -> 2
2 -> 4
4 -> 8
```

Then verify **every prediction**:

-   print/check `size`
-   print/check `capacity`
-   read back every value in order
-   deliberately call an invalid `get()` and confirm it fails

A prediction I never check cannot catch a bug.

## 7) Encapsulation

These should be private:

``` java
private int[] elements;
private int size;
```

`size` must always mean "number of stored elements." If outside code can
do `list.size = 100`, it can break that invariant and corrupt the
object.

## 8) Small edge case: why not start capacity at 0?

With naive doubling:

``` text
capacity = 0
new capacity = 0 * 2 = 0
```

It never grows. Then trying to write `elements[0]` fails.

So this simple implementation starts at capacity **1** (or would need
special handling for zero).

------------------------------------------------------------------------

## 30-second recall

``` text
ArrayList
   |
   +-- backing array -> fixed capacity
   +-- size          -> actual elements
   |
   +-- add at end
         |
         +-- space available -> O(1)
         |
         +-- full -> grow + copy -> O(n) for that call
                          |
                          +-- happens rarely
                          -> amortized O(1)

remove(0) -> shift remaining elements left -> O(n)
```

### The four things I should remember

**size != capacity**\
**resize = allocate + copy + repoint**\
**add = O(1) amortized, O(n) worst-case resize**\
**test what the code does, not what I meant it to do**
