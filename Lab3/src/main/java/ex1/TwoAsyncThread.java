package ex1;


public class TwoAsyncThread {
	  public static void main(String args[ ])  {
	    new MyThread("1 --> First Thread").start();
	    new MyThread("2 ==> Second Thread").start();
	  }
	}

class MyThread extends java.lang.Thread {
	  public MyThread(String str) {
	    super(str);
	  }
	  
	  public void run() {
	    for (int i=0; i<10; i++) {
	      System.out.println(i+" "+ this.getName());
	      try {
	    	  Thread.sleep((int)( Math.random()*10));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    }
	    System.out.println(getName() + " est finie");
	  }
	}

	
	
	// pom file.zip
	// newtable("file.zip")