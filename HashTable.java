import java.util.Random;

public class HashTable {
    private int size;
    private LinkedList[] chainTable;
    private Entry[] probeTable;
    private int elementCount = 0;
    private int collisionCount = 0;
    private String customHashFunction = null;

    public enum CollisionResolution { // enumerate so no string constants
        CHAINING, LINEAR_PROBING, QUADRATIC_PROBING, DOUBLE_HASHING
    }
    public enum HashFunctionType {
        MODULO, MULTIPLICATION, UNIVERSAL, CUSTOM
    }

    private CollisionResolution collisionResolution;
    private HashFunctionType hashFunctionType;

    // Constructor
    public HashTable(int size, CollisionResolution resolutionType, HashFunctionType functionType, String customFunction) {
        this.size = size;
        this.collisionResolution = resolutionType;
        this.hashFunctionType = functionType;
        this.customHashFunction = customFunction;

        if (resolutionType == CollisionResolution.CHAINING) {
            chainTable = new LinkedList[size];
            for (int i = 0; i < size; i++) {
                chainTable[i] = new LinkedList();
            }
        } else {
            probeTable = new Entry[size];
        }
    }

    private int hashModulo(int key) {
        return key % size;
    }

    private int hashMultiplication(int key) {
        double A = 0.6180339887;
        return (int) Math.floor(size * (key * A % 1));
    }

    private int hashUniversal(int key) {
        int a = new Random().nextInt(size - 1) + 1;  // random a in the range [1, size)
        int b = new Random().nextInt(size);          // random b in the range [0, size)
        int p = getPrime(size * 2);  // prime number larger than the universe of keys

        return Math.abs((a * key + b) % p) % size;
    }

    // custom hash function (user input)
    private int hashCustom(int key, String customFunction) {
        String expression = customFunction.replace("x", Integer.toString(key));
        return evaluateExpression(expression);
    }

    // only has + - * / % and ^
    private int evaluateExpression(String expression) {
        expression = expression.replaceAll("\\s", "");  // remove spaces
        int result = 0;
        char operator = '+';
        StringBuilder currentNumber = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch)) {
                currentNumber.append(ch);
            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '%' || ch == '^') {
                result = applyOperator(result, operator, Integer.parseInt(currentNumber.toString()));
                operator = ch;
                currentNumber.setLength(0);
            }
        }

        result = applyOperator(result, operator, Integer.parseInt(currentNumber.toString()));
        return Math.abs(result) % size;
    }

    private int applyOperator(int left, char operator, int right) {
        return switch (operator) {
            case '+' -> left + right;
            case '-' -> left - right;
            case '*' -> left * right;
            case '/' -> right != 0 ? left / right : 0;
            case '%' -> right != 0 ? left % right : 0;
            case '^' -> (int) Math.pow(left, right);
            default -> throw new IllegalArgumentException("Unknown operator: " + operator);
        };
    }

    private int secondHashFunction(int key) {
        return 1 + (key % (size - 1));
    }

    private int hashFunction(int key) {
        try {
            switch (hashFunctionType) {
                case MODULO: return hashModulo(key);
                case MULTIPLICATION: return hashMultiplication(key);
                case UNIVERSAL: return hashUniversal(key);
                case CUSTOM: return hashCustom(key, customHashFunction);
                default: return hashModulo(key);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in custom hash function: " + e.getMessage());
        }
    }

    public void insertChaining(int key, String value) {
        int hashKey = hashFunction(key);
        Entry entry = new Entry(key, value);
        chainTable[hashKey].insert(entry);
        elementCount++;
        if (chainTable[hashKey].size() > 1)
            collisionCount++;
    }

    public void insertLinearProbing(int key, String value) {
        int hashKey = hashFunction(key);
        Entry entry = new Entry(key, value);

        while (probeTable[hashKey] != null && probeTable[hashKey].getKey() != key) {
            hashKey = (hashKey + 1) % size;
            collisionCount++;
        }
        probeTable[hashKey] = entry;
        elementCount++;
    }

    public void insertQuadraticProbing(int key, String value) {
        int hashKey = hashFunction(key);
        int i = 1;
        Entry entry = new Entry(key, value);

        while (probeTable[hashKey] != null && probeTable[hashKey].getKey() != key) {
            hashKey = (hashKey + i * i) % size;
            i++;
            collisionCount++;
        }
        probeTable[hashKey] = entry;
        elementCount++;
    }

    public void insertDoubleHashing(int key, String value) {
        int hashKey = hashFunction(key);
        int stepSize = secondHashFunction(key);

        while (probeTable[hashKey] != null && probeTable[hashKey].getKey() != key) {
            hashKey = (hashKey + stepSize) % size;
            collisionCount++;
        }
        probeTable[hashKey] = new Entry(key, value);
        elementCount++;
    }

    public void insert(int key, String value) {
        switch (collisionResolution) {
            case CHAINING:
                insertChaining(key, value);
                break;
            case LINEAR_PROBING:
                insertLinearProbing(key, value);
                break;
            case QUADRATIC_PROBING:
                insertQuadraticProbing(key, value);
                break;
            case DOUBLE_HASHING:
                insertDoubleHashing(key, value);
                break;
        }
        displayMetrics();
    }

    private void displayMetrics() {
        double loadFactor = (double) elementCount / size;
        System.out.println("\nLoad Factor: " + loadFactor);
        System.out.println("Collisions so far: " + collisionCount);
        displayTable();
    }

    public void displayTable() {
        System.out.println("\nCurrent state of the hash table:");
        if (collisionResolution == CollisionResolution.CHAINING) {
            for (int i = 0; i < size; i++) {
                System.out.print("Bucket " + i + ": ");
                chainTable[i].display();
            }
        } else {
            for (int i = 0; i < size; i++) {
                System.out.print("Bucket " + i + ": ");
                if (probeTable[i] == null) {
                    System.out.print("Empty");
                } else {
                    System.out.print("[" + probeTable[i].getKey() + ": " + probeTable[i].getValue() + "] ");
                }
                System.out.println();
            }
        }
        System.out.println("-----------------------------\n");
    }

    private int getPrime(int n) {
        while (!isPrime(n)) n++;
        return n;
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public class Entry {
        private int key;
        private String value;

        public Entry(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
