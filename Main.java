import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the size of the hash table: ");
        int size = scanner.nextInt();

        System.out.print("Choose collision resolution technique (1 for Chaining, 2 for Linear Probing, 3 for Quadratic" +
                "Probing, and 4 for Double Hashing): ");
        int resolutionChoice = scanner.nextInt();

        System.out.print("Choose hash function (1 for Modulo, 2 for Multiplication, 3 for Universal Hashing, and 4 " +
                "for a Custom Hash Function): ");
        int hashFunctionChoice = scanner.nextInt();

        scanner.nextLine(); // for newline

        String customFunction = null;
        if (hashFunctionChoice == 4) {
            System.out.print("Enter your custom hash function (use 'x' for the key, e.g., 'x^2 + 5 % 7'): ");
            customFunction = scanner.nextLine();
        }

        HashTable.CollisionResolution resolution; // collision resolution technique
        switch (resolutionChoice) {
            case 1:
                resolution = HashTable.CollisionResolution.CHAINING;
                break;
            case 2:
                resolution = HashTable.CollisionResolution.LINEAR_PROBING;
                break;
            case 3:
                resolution = HashTable.CollisionResolution.QUADRATIC_PROBING;
                break;
            case 4:
                resolution = HashTable.CollisionResolution.DOUBLE_HASHING;
                break;
            default:
                throw new IllegalArgumentException("Invalid collision resolution technique.");
        }

        HashTable.HashFunctionType functionType; // hash function choice
        switch (hashFunctionChoice) {
            case 1:
                functionType = HashTable.HashFunctionType.MODULO;
                break;
            case 2:
                functionType = HashTable.HashFunctionType.MULTIPLICATION;
                break;
            case 3:
                functionType = HashTable.HashFunctionType.UNIVERSAL;
                break;
            case 4:
                functionType = HashTable.HashFunctionType.CUSTOM;
                break;
            default:
                throw new IllegalArgumentException("Invalid hash function.");
        }

        HashTable hashTable = new HashTable(size, resolution, functionType, customFunction);

        while (true) {
            System.out.print("Enter key (or -1 to exit): ");
            int key = scanner.nextInt();
            if (key == -1)
                break;

            System.out.print("Enter value: ");
            String value = scanner.next();

            hashTable.insert(key, value);
        }
    }
}
