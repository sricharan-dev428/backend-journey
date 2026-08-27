# Today's Learning — equals(), hashCode() & HashMap

## 1. What did I learn today?

I learned how `equals()` and `hashCode()` work, how to override them,
and how HashMap uses them when comparing and finding keys.

                    Object Comparison
                           |
              +------------+------------+
              |                         |
             ==                      equals()
              |                         |
       same object?              same value?
                                        |
                              we define what "same"
                              means by overriding it


HashMap lookup:

        key
         |
         v
     hashCode()
         |
         v
   find the bucket
         |
         v
      equals()
         |
         v
   find the exact key


---

## 2. What confused me / where was I wrong?

I was confused about mutability, mainly why `size()` was still 1
even though `get()` returned `null`.

Before changing the key:

    Point(3,4)
        |
        | hash = 97
        v
    [ Bucket A ]
    [ Point(3,4) -> "second point" ]

    map.get(a)  -> found
    map.size()  -> 1


Then:

    a.x = 99


Now the SAME object became:

    Point(99,4)
        |
        | new hash
        v
    [ Bucket B ]  <-- get() looks here
    [  empty   ]


But the entry is STILL physically in:

    [ Bucket A ]
    [ Point(99,4) -> "second point" ]


So:

    map.get(a)  -> null
    map.size()  -> 1

The entry was never removed.
We just can't find it using normal lookup anymore.


---

## 3. What is the equals() / hashCode() contract?

The main rule:

    if a.equals(b) == true

              MUST mean

    a.hashCode() == b.hashCode()


Example:

    a = Point(3,4)
    b = Point(3,4)

    a.equals(b)
         |
         v
       true

    therefore

    a.hashCode() == b.hashCode()
         |
         v
       MUST be true


Why?

HashMap does:

    hashCode()
        |
        v
    find bucket
        |
        v
     equals()
        |
        v
    find exact key


If equal objects have different hashes:

    a ----------------> Bucket A

    b ----------------> Bucket B

    a.equals(b) == true

BUT HashMap never gets the chance to compare them,
because it looks in different buckets.

So lookup can fail.


---

## 4. Why does Object.equals() use identity?

`Object` is the parent of every Java class.

But `Object` has NO IDEA what our future classes will contain.

For example:

    Point
    +-------+
    | x     |
    | y     |
    +-------+

Object doesn't know:

    Should I compare x?
    Should I compare y?
    Should I compare both?
    Should some field be ignored?

It cannot know what "equal" means for every possible class.

So Object can only safely ask:

    "Are these literally the same object?"


Example:

    a --------> [ Point(3,4) ]

    b --------> [ Point(3,4) ]


Same values,
but different objects.

    a == b
       |
       v
     false


Default Object.equals() basically does:

    this == other


If WE want this:

    Point(3,4) == Point(3,4)
          |
          v
      logically equal

then WE have to override `equals()` and tell Java:

    "For Point, compare x and y."