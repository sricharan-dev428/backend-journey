public class ListDemo {

    private int[] elements;
    private int size;

    public ListDemo() {
        elements = new int[1];
        size = 0;
    }

    public void add(int value) {

        // If the array is full, grow it
        if (size == elements.length) {

            int oldCapacity = elements.length;
            int newCapacity = oldCapacity * 2;

            int[] newElements = new int[newCapacity];

            // Copy old values to the new array
            for (int i = 0; i < size; i++) {
                newElements[i] = elements[i];
            }

            elements = newElements;

            System.out.println(
                "Resize: " + oldCapacity + " -> " + newCapacity
            );
        }

        elements[size] = value;
        size++;
    }

    public int get(int index) {

        // We check against size, not elements.length
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                "Index " + index + " is out of bounds. Size is " + size
            );
        }

        return elements[index];
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return elements.length;
    }

    public static void main(String[] args) {

        ListDemo list = new ListDemo();

        /*
         * Predictions:
         *
         * size = 5
         * capacity = 8
         *
         * resizes:
         * 1 -> 2
         * 2 -> 4
         * 4 -> 8
         */

        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);
        list.add(50);

        // Check size prediction
        System.out.println("Size: " + list.size());

        // Check capacity prediction
        System.out.println("Capacity: " + list.capacity());

        // Check that values survived the resizes
        for (int i = 0; i < list.size(); i++) {
            System.out.println(
                "index " + i + " = " + list.get(i)
            );
        }

        // Test an invalid index
        try {
            list.get(5);

            // If we reach this line, our get() is wrong
            System.out.println("TEST FAILED");

        } catch (IndexOutOfBoundsException e) {
            System.out.println("get(5) correctly failed");
            System.out.println(e.getMessage());
        }
    }
}