"""
Bulk-import MCQ questions into quiz_data.db
Questions derived from Javainterview.pdf content, mapped to the 5 app topics.
Format: (topic, difficulty, question, optA, optB, optC, optD, correct_idx, explanation)
"""
import sqlite3, os

DB_PATH = os.path.join(os.path.dirname(__file__), "quiz_data.db")

QUESTIONS = [
    # ===== JAVA BASICS (Q1-10, 81-90 from PDF) =====
    ("Java Basics", "easy", "What is Java?",
     "A low-level assembly language", "A high-level object-oriented programming language",
     "A markup language like HTML", "A database query language",
     1, "Java is a high-level, object-oriented programming language developed by Sun Microsystems."),

    ("Java Basics", "easy", "What does JVM stand for?",
     "Java Variable Machine", "Java Virtual Machine",
     "Java Visual Mode", "Java Version Manager",
     1, "JVM stands for Java Virtual Machine which executes Java bytecode."),

    ("Java Basics", "easy", "Which of these is NOT a feature of Java?",
     "Platform-independent", "Object-oriented", "Pointer support like C++", "Robust",
     2, "Java does not support pointers directly unlike C++, for security and simplicity."),

    ("Java Basics", "easy", "What does JDK include?",
     "Only JVM", "JRE and development tools", "Only an editor", "Only a compiler",
     1, "JDK (Java Development Kit) includes JRE and development tools like javac."),

    ("Java Basics", "medium", "What is the difference between == and .equals()?",
     "Both compare content", "== compares references, .equals() compares content",
     "== compares content, .equals() compares references", "There is no difference",
     1, "== compares object references while .equals() compares object content."),

    ("Java Basics", "medium", "What does the static keyword do?",
     "Makes a variable constant", "Creates members that belong to the class, not instances",
     "Prevents inheritance", "Makes methods private",
     1, "static creates class-level members shared among all instances."),

    ("Java Basics", "medium", "What is the difference between String and StringBuffer?",
     "String is mutable, StringBuffer is immutable", "String is immutable, StringBuffer is mutable",
     "Both are immutable", "Both are mutable",
     1, "String is immutable (unchangeable) while StringBuffer is mutable (changeable)."),

    ("Java Basics", "medium", "What does the 'this' keyword refer to?",
     "The parent class", "The current instance of the class",
     "A static method", "The main method",
     1, "this refers to the current instance of the class."),

    ("Java Basics", "hard", "Why is Java not a pure object-oriented language?",
     "Because it uses classes", "Because it supports primitive data types",
     "Because it has interfaces", "Because it supports inheritance",
     1, "Java supports primitive types (int, float, boolean, etc.) which are not objects."),

    ("Java Basics", "hard", "What is the purpose of the final keyword?",
     "To create abstract methods", "To declare constants, prevent overriding, or prevent inheritance",
     "To handle exceptions", "To create threads",
     1, "final declares constant variables, non-overridable methods, or non-inheritable classes."),

    ("Java Basics", "easy", "Which method is the entry point for a Java program?",
     "start()", "main()", "run()", "init()",
     1, "The main() method with signature public static void main(String[] args) is the JVM entry point."),

    ("Java Basics", "medium", "Why is the main method static in Java?",
     "So it can be overridden", "So JVM can call it without creating an object",
     "So it runs faster", "So it can access instance variables",
     1, "main is static so JVM can invoke it based on class name without instantiating an object."),

    ("Java Basics", "medium", "Can the main method be overloaded?",
     "No, never", "Yes, but JVM only calls the standard signature",
     "Yes, and JVM calls all overloaded versions", "Only in abstract classes",
     1, "main can be overloaded, but JVM only calls public static void main(String[] args)."),

    ("Java Basics", "hard", "What is a JIT compiler?",
     "A tool to write Java code", "A compiler that compiles bytecode to native code at runtime",
     "A debugger for Java", "A garbage collector",
     1, "JIT (Just-In-Time) compiler improves performance by compiling bytecode to native code at runtime."),

    ("Java Basics", "easy", "What is the Java API?",
     "A Java IDE", "A collection of pre-built classes and packages",
     "A database driver", "A web server",
     1, "The Java API is a collection of pre-built classes and packages providing common functionality."),

    ("Java Basics", "medium", "What are default values for instance variables?",
     "They have no defaults and must be initialized", "References are null, numbers are 0, booleans are false",
     "All are set to -1", "All are set to empty string",
     1, "Instance variables get default values: null for references, 0 for numbers, false for booleans."),

    ("Java Basics", "hard", "What is the difference between shallow copy and deep copy?",
     "They are the same thing", "Shallow copy copies reference, deep copy copies the actual object",
     "Deep copy copies reference, shallow copy copies object", "Neither copies data",
     1, "Shallow copy creates a new reference to the same object; deep copy creates an entirely new object."),

    ("Java Basics", "medium", "What is garbage collection in Java?",
     "Manual memory deallocation", "Automatic freeing of memory occupied by unreachable objects",
     "Deleting source code files", "Clearing the console output",
     1, "Garbage collection automatically frees memory used by objects that are no longer reachable."),

    ("Java Basics", "easy", "Which part of memory is cleaned by garbage collection?",
     "Stack", "Heap", "Both Stack and Heap", "Neither",
     1, "Garbage collection cleans the Heap memory where objects are stored."),

    ("Java Basics", "medium", "What is a ClassLoader in Java?",
     "A class that loads data from files", "A program that loads classes and interfaces to JVM at runtime",
     "A compiler plugin", "A testing framework",
     1, "ClassLoader belongs to JRE and loads required classes and interfaces to JVM when needed."),

    # ===== OOP FUNDAMENTALS (Q11-20 from PDF) =====
    ("OOP Fundamentals", "easy", "What is encapsulation?",
     "Running code in parallel", "Bundling data and methods into a single unit restricting access",
     "Creating multiple objects", "Inheriting from a parent class",
     1, "Encapsulation bundles data and methods into a class, restricting access to components."),

    ("OOP Fundamentals", "easy", "What does inheritance allow in Java?",
     "Creating static methods", "A subclass to inherit properties from a superclass",
     "Deleting parent classes", "Preventing object creation",
     1, "Inheritance allows a subclass to inherit properties and behaviors from a superclass."),

    ("OOP Fundamentals", "easy", "What is polymorphism?",
     "Having only one form", "Objects of different types treated as a common type",
     "Preventing method calls", "Creating final classes",
     1, "Polymorphism allows objects of different types to be treated as objects of a common type."),

    ("OOP Fundamentals", "medium", "What is an abstract class?",
     "A class with only static methods", "A class that cannot be instantiated and may have abstract methods",
     "A class with no methods", "A class that cannot have constructors",
     1, "An abstract class cannot be instantiated and serves as a blueprint for other classes."),

    ("OOP Fundamentals", "medium", "What is an interface in Java?",
     "A graphical user interface", "A collection of abstract methods that a class implements",
     "A type of variable", "A loop structure",
     1, "An interface is a collection of abstract methods; a class implements it to guarantee method implementations."),

    ("OOP Fundamentals", "medium", "What is method overloading?",
     "Same name and same parameters in different classes", "Same name but different parameters in the same class",
     "Different names but same parameters", "Calling a method multiple times",
     1, "Method overloading: multiple methods with the same name but different parameters in the same class."),

    ("OOP Fundamentals", "medium", "What is method overriding?",
     "Having multiple methods with different names", "A subclass providing specific implementation for a superclass method",
     "Creating a new method in the same class", "Deleting a parent method",
     1, "Method overriding occurs when a subclass provides a specific implementation for an inherited method."),

    ("OOP Fundamentals", "medium", "What is the super keyword used for?",
     "To create a new object", "To refer to the immediate parent class object",
     "To make a variable static", "To handle exceptions",
     1, "super refers to the parent class and can invoke parent methods, constructors, or access parent fields."),

    ("OOP Fundamentals", "easy", "What is a constructor?",
     "A regular method", "A special method used to initialize objects with the same name as the class",
     "A static variable", "An interface method",
     1, "A constructor is a special method with the class name, called when an object is created."),

    ("OOP Fundamentals", "hard", "Can static methods be overridden in Java?",
     "Yes, always", "No, static methods are resolved at compile time",
     "Only in abstract classes", "Only with the final keyword",
     1, "Static methods cannot be overridden because they are loaded at compile time, not runtime."),

    ("OOP Fundamentals", "hard", "What is constructor overloading?",
     "Having constructors with the same parameters", "Multiple constructors with different parameter lists",
     "A constructor that calls itself", "A constructor with a return type",
     1, "Constructor overloading means having multiple constructors with different parameter types or counts."),

    ("OOP Fundamentals", "medium", "What is method chaining?",
     "Calling methods in a loop", "Calling multiple methods on the same object in a single line",
     "Creating a chain of classes", "Linking two interfaces",
     1, "Method chaining calls multiple methods on the same object in one statement for concise code."),

    ("OOP Fundamentals", "hard", "What is the difference between IS-A and HAS-A relationships?",
     "They are the same", "IS-A is inheritance, HAS-A is composition",
     "IS-A is composition, HAS-A is inheritance", "Neither relates to OOP",
     1, "IS-A represents inheritance (extends), HAS-A represents composition (contains another object)."),

    ("OOP Fundamentals", "medium", "What is a copy constructor?",
     "A constructor with no parameters", "A constructor that initializes a new object from an existing object of the same class",
     "A constructor that copies files", "A static factory method",
     1, "A copy constructor initializes a new object using values from an existing object of the same class."),

    ("OOP Fundamentals", "easy", "Which keyword is used to implement an interface?",
     "extends", "implements", "interface", "abstract",
     1, "The implements keyword is used by a class to implement an interface."),

    # ===== EXCEPTION HANDLING (Q21-30 from PDF) =====
    ("Exception Handling", "easy", "What is an exception in Java?",
     "A syntax error", "An event that disrupts normal program flow during execution",
     "A type of loop", "A design pattern",
     1, "An exception is an event during execution that disrupts the normal flow of instructions."),

    ("Exception Handling", "medium", "What is the difference between checked and unchecked exceptions?",
     "There is no difference", "Checked are caught at compile-time, unchecked at runtime",
     "Unchecked are caught at compile-time, checked at runtime", "Both are caught at runtime only",
     1, "Checked exceptions must be handled at compile-time; unchecked (RuntimeException subclasses) are not."),

    ("Exception Handling", "easy", "What does the throw keyword do?",
     "Declares exceptions in method signature", "Explicitly throws an exception within a method",
     "Catches an exception", "Prevents exceptions",
     1, "throw is used to explicitly throw an exception within a method body."),

    ("Exception Handling", "easy", "What does the throws clause do?",
     "Catches exceptions", "Declares exceptions a method may throw in its signature",
     "Creates new exceptions", "Prevents method overriding",
     1, "throws in a method declaration indicates the method may throw specified exceptions."),

    ("Exception Handling", "medium", "What is try-with-resources?",
     "A way to retry failed code", "Automatically closes resources when no longer needed (Java 7+)",
     "A testing framework", "A way to allocate memory",
     1, "Try-with-resources automatically closes resources like files and sockets after use."),

    ("Exception Handling", "easy", "What is the finally block used for?",
     "To throw exceptions", "To execute code regardless of whether an exception occurred",
     "To prevent exceptions", "To create objects",
     1, "The finally block contains cleanup code that executes whether or not an exception was thrown."),

    ("Exception Handling", "medium", "Can we have multiple catch blocks for one try block?",
     "No, only one catch per try", "Yes, each handling a different exception type",
     "Only two catch blocks maximum", "Only with finally block",
     1, "A try block can have multiple catch blocks, each handling different exception types."),

    ("Exception Handling", "medium", "What is RuntimeException?",
     "A checked exception", "An unchecked exception subclass of Exception thrown during normal JVM operation",
     "A compile-time error", "A type of thread",
     1, "RuntimeException represents unchecked exceptions that can occur during normal JVM operation."),

    ("Exception Handling", "hard", "Will the finally block execute if System.exit(0) is called in try?",
     "Yes, always", "No, System.exit() terminates JVM before finally executes",
     "Only if there's an exception", "Only in debug mode",
     1, "System.exit() terminates the JVM immediately, preventing finally block execution."),

    ("Exception Handling", "hard", "What is the difference between final, finally, and finalize?",
     "They all do the same thing", "final=constant/no-override, finally=cleanup block, finalize=pre-GC method",
     "final=cleanup, finally=constant, finalize=override", "They are all exception keywords",
     1, "final restricts modification; finally is a cleanup block; finalize is called before garbage collection."),

    ("Exception Handling", "medium", "What does the assert statement do?",
     "Handles exceptions", "Checks a boolean expression and throws AssertionError if false",
     "Creates a new thread", "Allocates memory",
     1, "assert checks a boolean condition for debugging; throws AssertionError if the condition is false."),

    ("Exception Handling", "easy", "Which exception is thrown for division by zero with integers?",
     "NullPointerException", "ArithmeticException",
     "ArrayIndexOutOfBoundsException", "NumberFormatException",
     1, "ArithmeticException is thrown when dividing an integer by zero."),

    ("Exception Handling", "medium", "Is it mandatory to have a catch block after try?",
     "Yes, always", "No, you can have try-finally without catch",
     "Only for checked exceptions", "Only for unchecked exceptions",
     1, "A try block can be followed by either catch or finally or both; catch is not mandatory."),

    ("Exception Handling", "hard", "Does the finally block execute when return is in try block?",
     "No, return exits immediately", "Yes, finally executes before the method returns",
     "Only if there's no catch block", "Only for void methods",
     1, "The finally block executes even when a return statement is in the try or catch block."),

    ("Exception Handling", "medium", "Which of these is a checked exception?",
     "NullPointerException", "IOException",
     "ArithmeticException", "ArrayIndexOutOfBoundsException",
     1, "IOException is a checked exception that must be handled or declared in the method signature."),

    # ===== COLLECTIONS FRAMEWORK (Q31-40 from PDF) =====
    ("Collections Framework", "easy", "What is the Java Collections Framework?",
     "A GUI toolkit", "A set of interfaces and classes to represent and manipulate collections",
     "A database driver", "A testing framework",
     1, "The Collections Framework provides interfaces and classes for managing groups of objects."),

    ("Collections Framework", "easy", "What is the difference between List, Set, and Map?",
     "All are the same", "List allows duplicates in order, Set has no duplicates, Map stores key-value pairs",
     "Set allows duplicates, List doesn't", "Map is ordered, List is not",
     1, "List: ordered with duplicates. Set: unordered, no duplicates. Map: key-value pairs."),

    ("Collections Framework", "easy", "What is an ArrayList?",
     "A fixed-size array", "A resizable array implementation of the List interface",
     "A linked list", "A tree structure",
     1, "ArrayList is a resizable array implementation of List that dynamically grows as elements are added."),

    ("Collections Framework", "easy", "What is a LinkedList?",
     "An array-based list", "A doubly-linked list with fast insertion/deletion but slower random access",
     "A hash-based collection", "A sorted set",
     1, "LinkedList is a doubly-linked list providing fast insertion/deletion but slower random access."),

    ("Collections Framework", "medium", "What is a HashSet?",
     "A sorted collection", "A Set implementation using a hash table that doesn't allow duplicates",
     "An ordered list", "A key-value store",
     1, "HashSet uses a hash table for storage and does not allow duplicate elements."),

    ("Collections Framework", "medium", "What is a HashMap?",
     "A sorted map", "A Map implementation using hash table allowing null keys and values",
     "A synchronized map", "An ordered set",
     1, "HashMap uses a hash table, allows null keys/values, and does not guarantee order."),

    ("Collections Framework", "medium", "What is a TreeSet?",
     "A hash-based set", "A Set using a Red-Black tree providing elements in sorted order",
     "An unordered collection", "A key-value map",
     1, "TreeSet uses a Red-Black tree and maintains elements in sorted (natural) order."),

    ("Collections Framework", "medium", "What is the difference between HashMap and Hashtable?",
     "They are identical", "HashMap is not synchronized and allows nulls; Hashtable is synchronized and doesn't allow nulls",
     "Hashtable allows nulls, HashMap doesn't", "HashMap is synchronized, Hashtable is not",
     1, "HashMap: unsynchronized, allows null. Hashtable: synchronized, no null keys or values."),

    ("Collections Framework", "medium", "What does the Collections utility class provide?",
     "Database connections", "Utility methods like sorting, shuffling, and searching collections",
     "Thread management", "File I/O operations",
     1, "The Collections class provides utility methods for working with collections (sort, shuffle, search)."),

    ("Collections Framework", "hard", "What is the Comparable interface used for?",
     "Comparing file sizes", "Defining the natural ordering of objects for sorting",
     "Comparing threads", "Comparing exceptions",
     1, "Comparable defines natural order; classes implementing it can be sorted with Collections.sort()."),

    ("Collections Framework", "hard", "How does ArrayList grow dynamically?",
     "It doesn't grow", "It creates a new larger array and copies elements when capacity is exceeded",
     "It uses linked nodes", "It compresses existing elements",
     1, "ArrayList creates a new array (typically 1.5x size) and copies elements when capacity is reached."),

    ("Collections Framework", "easy", "Which collection interface does NOT allow duplicate elements?",
     "List", "Set", "Queue", "Deque",
     1, "The Set interface does not allow duplicate elements."),

    ("Collections Framework", "medium", "What is the difference between Iterator and ListIterator?",
     "They are the same", "Iterator goes forward only; ListIterator goes both directions",
     "ListIterator is faster", "Iterator works on Maps only",
     1, "Iterator traverses forward only; ListIterator can traverse in both directions and modify elements."),

    ("Collections Framework", "hard", "What is the difference between Comparable and Comparator?",
     "They are the same interface", "Comparable defines natural order in the class; Comparator defines external custom order",
     "Comparator is faster", "Comparable works only with Strings",
     1, "Comparable is implemented by the class itself; Comparator is a separate class for custom ordering."),

    ("Collections Framework", "medium", "Which Map implementation maintains insertion order?",
     "HashMap", "LinkedHashMap", "TreeMap", "Hashtable",
     1, "LinkedHashMap maintains insertion order using a doubly-linked list internally."),

    # ===== JDBC & SQL (Q61-70 from PDF) =====
    ("JDBC & SQL", "easy", "What is JDBC?",
     "A Java GUI framework", "An API allowing Java applications to interact with relational databases",
     "A web server", "A testing tool",
     1, "JDBC (Java Database Connectivity) is an API for Java applications to interact with databases."),

    ("JDBC & SQL", "medium", "What are the steps to connect to a database in Java?",
     "Just call Database.connect()", "Load driver, establish connection, create statement, execute query, process results, close connection",
     "Only create a connection object", "Import the SQL package only",
     1, "Steps: load JDBC driver, get connection, create statement, execute SQL, process ResultSet, close."),

    ("JDBC & SQL", "medium", "What is the Connection interface in JDBC?",
     "A class for reading files", "An interface representing a connection to a database for creating statements and managing transactions",
     "A thread management class", "A collection interface",
     1, "Connection represents a database connection and provides methods for statements and transactions."),

    ("JDBC & SQL", "easy", "What is a JDBC driver?",
     "A hardware component", "A software component that enables Java to interact with a database",
     "A type of exception", "A collection class",
     1, "A JDBC driver is software enabling Java applications to communicate with a specific database."),

    ("JDBC & SQL", "medium", "What is the difference between Statement and PreparedStatement?",
     "They are the same", "Statement is general-purpose; PreparedStatement is precompiled for better performance and security",
     "PreparedStatement is slower", "Statement prevents SQL injection",
     1, "PreparedStatement precompiles SQL for better performance and prevents SQL injection attacks."),

    ("JDBC & SQL", "medium", "What is a ResultSet in JDBC?",
     "A collection of database drivers", "An object representing the result of a database query with methods to iterate and retrieve data",
     "A configuration file", "An exception type",
     1, "ResultSet represents query results and provides methods to iterate rows and retrieve column data."),

    ("JDBC & SQL", "hard", "What is JDBC transaction management?",
     "Deleting database tables", "Grouping SQL statements into a single unit of work ensuring data integrity",
     "Creating new databases", "Reading configuration files",
     1, "JDBC transactions group SQL statements into atomic units ensuring database integrity and consistency."),

    ("JDBC & SQL", "hard", "What is connection pooling in JDBC?",
     "Creating a new connection for each query", "Managing and reusing database connections to reduce overhead",
     "Pooling multiple databases together", "A type of SQL join",
     1, "Connection pooling reuses database connections, reducing the overhead of creating/closing connections."),

    ("JDBC & SQL", "medium", "What is batch processing in JDBC?",
     "Processing one statement at a time", "Executing multiple SQL statements in a single batch to reduce database round trips",
     "Creating batch files", "Parallel query execution",
     1, "Batch processing executes multiple SQL statements together, reducing database round trips."),

    ("JDBC & SQL", "easy", "Which SQL command is used to retrieve data from a table?",
     "INSERT", "SELECT", "UPDATE", "DELETE",
     1, "SELECT is the SQL command used to retrieve/query data from database tables."),

    ("JDBC & SQL", "easy", "Which SQL clause is used to filter rows?",
     "ORDER BY", "WHERE", "GROUP BY", "HAVING",
     1, "WHERE clause filters rows based on specified conditions."),

    ("JDBC & SQL", "medium", "What is the difference between WHERE and HAVING?",
     "They are the same", "WHERE filters individual rows; HAVING filters grouped results",
     "HAVING filters rows, WHERE filters groups", "WHERE is faster than HAVING",
     1, "WHERE filters rows before grouping; HAVING filters groups after GROUP BY."),

    ("JDBC & SQL", "medium", "Which type of JOIN returns only matching rows from both tables?",
     "LEFT JOIN", "INNER JOIN", "RIGHT JOIN", "FULL OUTER JOIN",
     1, "INNER JOIN returns only rows where there is a match in both tables."),

    ("JDBC & SQL", "hard", "What are the types of JDBC drivers?",
     "Only one type exists", "JDBC-ODBC Bridge, Native-API, Network Protocol, and Thin drivers",
     "Only network drivers", "File-based and memory-based",
     1, "Four types: JDBC-ODBC Bridge, Native-API, Network Protocol, and Thin (pure Java) drivers."),

    ("JDBC & SQL", "easy", "Which SQL command adds new rows to a table?",
     "SELECT", "INSERT", "UPDATE", "CREATE",
     1, "INSERT INTO is used to add new rows of data into a database table."),
]

def main():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    # Ensure table exists
    cursor.execute("""CREATE TABLE IF NOT EXISTS questions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        topic TEXT NOT NULL,
        difficulty TEXT NOT NULL,
        question_text TEXT NOT NULL UNIQUE,
        option_a TEXT NOT NULL,
        option_b TEXT NOT NULL,
        option_c TEXT NOT NULL,
        option_d TEXT NOT NULL,
        correct_option_idx INTEGER NOT NULL CHECK(correct_option_idx BETWEEN 0 AND 3),
        explanation TEXT
    )""")

    inserted = 0
    skipped = 0
    for q in QUESTIONS:
        topic, diff, text, a, b, c, d, idx, exp = q
        try:
            cursor.execute(
                "INSERT INTO questions(topic, difficulty, question_text, option_a, option_b, option_c, option_d, correct_option_idx, explanation) "
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                (topic, diff, text, a, b, c, d, idx, exp)
            )
            inserted += 1
        except sqlite3.IntegrityError:
            skipped += 1

    conn.commit()

    # Print summary
    cursor.execute("SELECT topic, COUNT(*) FROM questions GROUP BY topic ORDER BY topic")
    print(f"\n{'='*50}")
    print(f"  Imported {inserted} new questions ({skipped} duplicates skipped)")
    print(f"{'='*50}")
    print(f"  {'Topic':<30} {'Count':>5}")
    print(f"  {'-'*35}")
    for row in cursor.fetchall():
        print(f"  {row[0]:<30} {row[1]:>5}")
    cursor.execute("SELECT COUNT(*) FROM questions")
    total = cursor.fetchone()[0]
    print(f"  {'-'*35}")
    print(f"  {'TOTAL':<30} {total:>5}")
    print(f"{'='*50}\n")

    conn.close()

if __name__ == "__main__":
    main()
