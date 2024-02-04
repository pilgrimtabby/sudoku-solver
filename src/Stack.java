/**
 * Generic stack implementing using an array.
 *
 * @author pilgrim_tabby
 * @version 0.0.1
 */
public class Stack<E> {
    private E[] stack = (E[]) new Object[5000];
    private int size = 0;

    /**
     * Default constructor to make stack of size 1.
     * @param value object to add to stack.
     */
    public Stack(E value) {
        push(value);
    }

    /**
     * Add an object to the stack.
     * @param value The value to add.
     */
    public void push(E value) {
        if (this.size == this.stack.length - 1) {
            enlargeStack();
        }
        this.stack[this.size++] = value;
    }

    /**
     * Remove the most recently added value from the stack.
     * @return The most recently added object.
     */
    public E pop() {
        return this.stack[--this.size];
    }

    /**
     * Check if the stack is empty.
     * @return true if the stack is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Double the stack's length.
     */
    private void enlargeStack() {
        this.stack = java.util.Arrays.copyOf(this.stack, this.stack.length * 2 + 1);
    }
}
