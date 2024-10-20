public class LinkedList {
    private Node head;
    private int size;

    public void insert(HashTable.Entry entry) {
        Node newNode = new Node(entry);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public void display() {
        Node current = head;
        while (current != null) {
            System.out.print("[" + current.data.getKey() + ": " + current.data.getValue() + "] -> ");
            current = current.next;
        }
        System.out.println("null");
    }

    public int size() {
        return size;
    }

    private class Node {
        private HashTable.Entry data;
        private Node next;

        public Node(HashTable.Entry data) {
            this.data = data;
            this.next = null;
        }
    }
}
