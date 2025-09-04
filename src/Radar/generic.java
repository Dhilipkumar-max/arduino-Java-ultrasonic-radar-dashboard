package Radar;
class Box<T> {
	  T value; // T is a placeholder for any data type

	  void set(T value) {
	    this.value = value;
	  }

	  T get() {
	    return value;
	  }
	}

	public class generic {
	  public static void main(String[] args) {
	    // Create a Box to hold a String
	    Box<String> stringBox = new Box<>();
	    stringBox.set("Hello");
	    System.out.println("Value: " + stringBox.get());

	    // Create a Box to hold an Integer
	    Box<Integer> intBox = new Box<>();
	    intBox.set(50);
	    System.out.println("Value: " + intBox.get());
	  }
	}
