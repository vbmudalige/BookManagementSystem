// 110373E
//Mudalige V.B.

package DataBase;
import java.io.*;
import java.util.Scanner;
/**
 *
 * @author VIDURA
 */
public class DataBase {
 
    public static void main(String[] args) throws IOException {
       
        FileInputStream fstream;
        RecordMaxHeap newHeap = new RecordMaxHeap(1001);    //defining a max heap of size 1001 - I have assumed that the inputs are limited to 1000
        UserHashTable userTable = new UserHashTable();       //define a user hash table to store data of users
        BookHashTable bookTable = new BookHashTable(userTable);  //define a book hash table to store data of books and it has a refernce to user hash table
        VendorHashTable vendorTable = new VendorHashTable(userTable); // define a vendor hash table to store data of vendors and it has a reference to user hash table
       
        fstream = new FileInputStream("textfile.txt");   //open the text file which include all inputs
       
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
    
        while ((strLine = br.readLine()) != null)   {   //file is read line by line
            long time = getTime(strLine);              //calculte the time of that line using getTime()
            String userName = getUserName(strLine);    //get the user name of that line using getUserName()
            String bookName = getBookName(strLine);    //get the book name of that line using getBookName()
            String vendorName = getVendorName(strLine);//get the vendor name of that line using getVendorName()
            int vendorRating = getVendorRating(strLine);//get the vendor rating of that line using getVendorRating()
            int bookRating = getBookRating(strLine);    //get the user rating of that line using getUserRating()

            Record newRecord = new Record();
            
            newRecord.setDate(time);                //set time of the new record
            newRecord.setUserName(userName);          //set user name of the new record
            newRecord.setBookName(bookName);          //set book name of the new record
            newRecord.setVendorName(vendorName);       //set vendor name of the new record
            newRecord.setVendorRating(vendorRating);  //set vendor rating of the new record
            newRecord.setBookRating(bookRating);     //set book rating of the new record
            
            newHeap.insert(newRecord);           //insert the record into the tree
        }
        newHeap.maxHeapSort(new Comparator());    //do heap sort on the tree
        //Close the input stream
        in.close();
        
        for(int i = 0; newHeap.records[i] != null; i++){
           //System.out.println(newHeap.records[i].getDate() + " " + newHeap.records[i].getUserName() + " " + newHeap.records[i].getBookName() + " " + newHeap.records[i].getVendorName() + " " + newHeap.records[i].getVendorRating() + " " + newHeap.records[i].getBookRating() );       
            bookTable.insert(newHeap.records[i]);   //books are added to the book hash table
            vendorTable.insert(newHeap.records[i]); //vendors are added to the vendors hash table
            userTable.insert(newHeap.records[i]);   //users are added to the hash table
        }
        
        for(int i = 0; i<1009; i++){                    //set overall ratings of books and vendors
            if(bookTable.getBook(i) != null){
                bookTable.getBook(i).setOverallRating(bookTable.calculateOverallRating(bookTable.getBook(i)));
            }
            if(vendorTable.getVendor(i) != null){
                vendorTable.getVendor(i).setOverallRating(vendorTable.calculateOverallRating(vendorTable.getVendor(i)));
            }
        }
        vendorTable.setBookTable(bookTable);   //send a reference of book hash table to vendor hash table
        bookTable.setVendorTable(vendorTable); //send a reference of vendor hash table to book hash table
        
        Interface(bookTable, vendorTable);
    }
    
    public static void Interface(BookHashTable bookTable, VendorHashTable vendorTable){   //implementing a user interface to enter values at run time
        int check=0;
        String temp;
        Scanner input=new Scanner(System.in);
        while(check!=3){
            System.out.println("Enter 1 for Book Search.");
            System.out.println("Enter 2 for Vendor Search.");
            System.out.println("Enter 3 for Exit.");
            System.out.print("Choise : ");
            try{
                check=input.nextInt();
            }catch(Exception e){
                System.out.println("Invalid Input");
                return;
            }
            switch(check){
                case 1: 
                    System.out.print("Enter the Book name : ");
                    temp=input.next();
                    bookTable.bookSearch(temp);
                    break;
                
                case 2: 
                    System.out.print("Enter the Vendor name : ");
                    temp=input.next();
                    vendorTable.vendorSearch(temp);
                    break;
                case 3:
                    return;
                default : break;
            }
        }
    }
 
    public static long getTime(String str){       //calculating the value of timestamp in each line of input
        String timeStr = "0";                     
        long dateValue = 0;                 
        String[] tokens = str.split(" ");         //split a given input line by spaces
        String[] dateStr = tokens[0].split("[T:-]");  //consider the first part of splited strings which contains time stamp and split it from non digit symbols 
        for(int i=0; i<dateStr.length; i++){         
            timeStr += dateStr[i];              //combining above strings obtain a string which contains only digits 
        }
        dateValue = Long.parseLong(timeStr);        //convert the above string to a long
        return dateValue;                          
    }
    public static String getUserName(String line){   // return the second token which contains the user name
        String[] tokens = line.split(" ");
        return tokens[1];
    }
    public static String getBookName(String line){ //return the third token which contains the book name
        String[] tokens = line.split(" ");
        return tokens[2];
    }
    public static String getVendorName(String line){    //return the fourth token which contains the vendor name
        String[] tokens = line.split(" ");
        return tokens[3];
    }    
    public static int getVendorRating(String line){   //return the fifth token which contains the vendor rating
        String[] tokens = line.split(" ");
        return Integer.parseInt(tokens[4]);
    }    
    public static int getBookRating(String line){   //return the sixth token which contains the book rating
        String[] tokens = line.split(" ");
        return Integer.parseInt(tokens[5]);
    }
}

class User{
    private String name = null;                 //user name
    private float weightOfUserRating = 0;       //weight of the ratings of user
    private String[] vendors = new String[1000];  //list of vendors that the user has rated
    private String[] books = new String[1000];    //list of books that the user has rated
    private int[] vendorRating = new int[1000];   //list of vendor ratings that the user has rated
    private int[] bookRating = new int[1000];    //list of book ratings that the user has rated
    private int vendorCount = 0;               //number of vendors that the user has rated
    private int bookCount = 0;                 //number of books that the user has rated
    
    public void setName(String name){       //set user name
        this.name = name;
    }   
    public String getName(){          //return user name
        return name;
    }
    public void addVendor(String name, int rating){  //adding vendors and updating weighOfUserRating value
        this.vendors[vendorCount] = name;
        this.vendorRating[vendorCount] = rating;
        vendorCount++;
        weightOfUserRating = 2 - (float)1/vendorCount;
    }
    public String getVendor(int num){              //return the number 'num' vender
        return vendors[num];
    }
    public void addBook(String name, int rating){ //adding a book to the the book list of user
        this.books[bookCount] = name;
        this.bookRating[bookCount] = rating;
        bookCount++;
    }
    public String getBook(int num){        //return the number 'num' book
        return books[num];
    }
    public int getVendorRating(int num){   //return number 'num' vendor's rating
        return vendorRating[num];
    }
    public int getBookRating(int num){    //return number 'num' book's rating
        return bookRating[num];
    }
    public float getWeightOfUserRating(){  // get the value of user rating weight
        return this.weightOfUserRating;
    }
    public int getSumOfRatesOfGivenBook(String theBook){    //calculate and return the sum of rates that the user has rated a given book
        int sum = 0;
        for(int i = 0; i<bookCount; i++){
            if(books[i].equals(theBook))
                sum += bookRating[i];
            continue;
        }
        return sum;
    }
    public int getCountOfGivenBook(String theBook){     //calculate and return the number of times that the user has rated a given book
        int count = 0;
        for(int i = 0; i<bookCount; i++){
            if(books[i].equals(theBook))
                count++;
            continue;
        }
        return count;
    }
    public int getSumOfRatesOfGivenVendor(String theVendor){    //calculate the sum of rates that the user has rated a given vendor
        int sum = 0;
        for(int i = 0; i<vendorCount; i++){
            if(vendors[i].equals(theVendor))
                sum += vendorRating[i];
            continue;
        }
        return sum;
    }
    public int getCountOfGivenVendor(String theVendor){  //calculate and return the number of times that the user has rated a given vendor
        int count = 0;
        for(int i = 0; i<vendorCount; i++){
            if(vendors[i].equals(theVendor))
                count++;
            continue;
        }
        return count;
    }    
}

class Book{
    private String name = null;        //book name
    private String[] vendors = new String[1000];  //list of vendor names who sells the given book
    private String[] users = new String[1000];    //list of user names who rates the given book
    private int[] ratings = new int[1000];       //list of ratings of the book given by each user 
    private int vendorCount = 0;               //number of vendours who has this book
    private int userCount = 0;                 //number of users who rated this book
    private float overallRating = 0;          //overall aggregate rating of this book

    public void setName(String name){         //set the book name
        this.name = name;
    }
    public String getName(){           //return the book name
        return name;
    }
    public void addVendor(String name){    //add the vendors who has this book
        this.vendors[vendorCount] = name;
        vendorCount++;
    }
    public void addUser(String name, int rate){   //add users who has this book
        this.users[userCount] = name;
        this.ratings[userCount] = rate;
        userCount++;
    }
    public String getUser(int num){     //return the name of number 'num' user
        return users[num];
    }
    public int getUserCount(){     //return the number of users
        return userCount;
    }
    public String getVendor(int num){  //return the name of number 'num' user
        return this.vendors[num];
    }
    public int getVendorCount(){     //return the number of vendors
        return this.vendorCount;
    }
    public int getRting(int num){   //return the number 'num' user's rating
        return ratings[num];
    }
    public void setOverallRating(float overallRating){  //set overall aggregate rating of the book
        this.overallRating = overallRating;
    }
    public float getOverallRating(){            //return overall aggregate rating of the book
        return this.overallRating;
    }
}

class Vendor{
    private String vendorName = null;    //name of the vendor
    private String[] books = new String[1000]; //list of names of books this venodr has
    private String[] users = new String[1000];  //list of users who rated this vendor
    private int[] ratings = new int[1000];     //list of ratings that rated by the each user
    private int bookCount = 0;              //number of books vendor has 
    private int userCount = 0;            //number of users who rated this vendor
    private float overallRating = 0;     //overall aggregate rating of this vendor
    
    public void setName(String vendorName){     //set the name of this vendor
        this.vendorName = vendorName;
    }
    public String getName(){            //get the name of this vendor
        return this.vendorName;
    }
    public void addUser(String name, int rate){  //add users to the user list who rated this vendor
        this.users[userCount] = name;
        this.ratings[userCount] = rate;
        userCount++;
    }
    public String getUser(int num){    //return the name of number 'num' user
        return users[num];
    }    
    public void addBook(String name){    //add book names to the list that the vendor has
        this.books[bookCount] = name;
        bookCount++;
    } 
    public String getBook(int num){   //return the name of number 'num' user
        return books[num];
    }
    public int getUserCount(){  //return the number of users who rated this vendor
        return userCount;
    }    
    public int getBookCount(){  //return the number of books which the vendor has
        return bookCount;
    }
    public int getRating(int num){  //return the rating of number 'num' user
        return ratings[num];
    }
    public void setOverallRating(float overallRating){  //set overall rating of the vendor
        this.overallRating = overallRating;
    }
    public float getOverallRating(){   //return the overall rating of the vendor
        return this.overallRating;
    }    
}

class Record{             //details that got from each line of input file
    private long date = 0;       
    private String userName = null;  
    private String bookName = null;
    private String vendorName = null;
    private int vendorRating = 0;
    private int bookRating = 0;
    //set the details that got from each line of input file and getter mehtods for each detail
    public void setDate(long d){
        date = d;
    }                                      
    public long getDate(){
        return date;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
    public String getUserName(){
        return userName;
    }    
    public void setBookName(String bookName){
        this.bookName = bookName;
    }
    public String getBookName(){
        return bookName;
    }
    public void setVendorName(String vendorName){
        this.vendorName = vendorName;
    }
    public String getVendorName(){
        return vendorName;
    }
    public void setVendorRating(int vendorRating){
        this.vendorRating = vendorRating;
    }
    public int getVendorRating(){ 
        return vendorRating;
    }
    public void setBookRating(int bookRating){
        this.bookRating = bookRating;
    }
    public int getBookRating(){     
        return bookRating;
    }
}

class RecordMaxHeap {                   //building a max heap for records
    public Record[] records;        
    public int heapSize = 0;
    public boolean sorted = false;
                                       //intialize the heap size
    public RecordMaxHeap(int maxSize) {
        records = new Record[maxSize];
    }
    public void insert(Record rec) {      //inserts record to the array
        records[heapSize] = rec;
        heapSize++;
    }                                               //heapify algorithm
    private void maxHeapify(int i, Comparator comp) {    //comparator is used to compare to values
        int l = left(i);
        int r = right(i);
        int largest = i;

        if(l < heapSize && comp.compare(records[l], records[i]) > 0)
            largest = l;
        if(r < heapSize && comp.compare(records[r], records[largest]) > 0)
            largest = r;

        if(largest != i) {
            Record temp = records[i];
            records[i] = records[largest];
            records[largest] = temp;
            maxHeapify(largest, comp);
        }
    }
    private void buildMaxHeap(Comparator comp) {        //build max heap algorithm
        for(int x = (heapSize - 1)/2; x >= 0; x--)
            maxHeapify(x, comp);
    }
    public void maxHeapSort(Comparator comp) {     //max heap sort algorithm
        buildMaxHeap(comp);

        int size = heapSize;
        Record temp;

        for(int x = heapSize - 1; x > 0; x--) {
            temp = records[0];
            records[0] = records[heapSize - 1];
            records[heapSize - 1] = temp;
            heapSize--;
            maxHeapify(0, comp);
        }

        heapSize = size;
        sorted = true;
    }
    private int parent(int index) {    //get the parent of the node at index
        return index/2;
    }
    private int left(int index) {    //get the left child of the node at index
        return index*2 + 1;
    }
    private int right(int index) {  //get the right child of the node at index
        return index*2 + 2;
    }
}

class Comparator{                      //use to compare two numbers 
    public int compare(Record a, Record b){
        if(a.getDate() >= b.getDate()){
            return 1;
        }
        return -1;
    }
}

class BookHashTable{                //building a hash table to store books
    private static final int M = 1009;   //odd number is selected as the size of the hash table
    Book[] bookList = new Book[M];
    UserHashTable userTable;      //keep refernce to user hash table and book hash table
    VendorHashTable vendorTable;
    
    BookHashTable(UserHashTable userTable){
        this.userTable = userTable;
        for(int i = 0; i<M; i++){
            bookList[i] = null;
        }
    }
    
    public int hash(String x) {          //calculate and return the hash value for a given book name
        char ch[] = x.toCharArray();
        int xlength = x.length();

        int sum = 0;
        for(int i = 0; i < xlength; i++)
            sum += ch[i];
        return sum % M;
    }    
    public void insert(Record item) {
        int h = hash(item.getBookName()); //getting the hash value for the given book using hash()
        
        if(bookList[h] == null){         //if the location is free add the book here
            bookList[h] = new Book();
            bookList[h].setName(item.getBookName());
            bookList[h].addVendor(item.getVendorName());
            bookList[h].addUser(item.getUserName(), item.getBookRating());
        }

        else if(bookList[h] != null && bookList[h].getName().equals(item.getBookName())){  //if the location is not free and the book in the location is same, update details of that book
            bookList[h].addUser(item.getUserName(), item.getBookRating());
            bookList[h].addVendor(item.getVendorName());
        }
        else if(bookList[h] != null && !bookList[h].getName().equals(item.getBookName())){  //if the location is not free and the book in the location is different, find another location 
            int i = 1;                             //use linear probing to handle collision
            boolean added = false;
            for(; bookList[h+i] != null; i++){            
                if(!bookList[h+i].getName().equals(item.getBookName())){  //if the book is not found while probing continue the loop
                    continue;
                }
                else{                             //if the book is found while probing add the details to that book
                    bookList[h+i].addUser(item.getUserName(), item.getBookRating());
                    bookList[h+i].addVendor(item.getVendorName());          
                    added = true;
                    break;
                }   
            }
            if(!added){                   //if the book is not found while probing, new book is inserted in to a empty slot of the hash table
                bookList[h+i] = new Book();
                bookList[h+i].setName(item.getBookName());
                bookList[h+i].addVendor(item.getVendorName());
                bookList[h+i].addUser(item.getUserName(), item.getBookRating());
            }
        }        
    }
    public void bookSearch(String theBook){       //implementing book search algorithm
        boolean bookFound = false;
        int h = hash(theBook);   //calculate the hash value of given book
        
        if(bookList[h] == null){           //if the location of hash value is free, the book is not there
            System.out.println("Book is not found");
            return;
        }
        else if(bookList[h].getName().equals(theBook)){   //if the location of hash value contains the wanted book print the deatils
            bookFound = true;
            System.out.println("\n-------The results of book search---------");
            System.out.println("#5 most recent ratings for the product are#");
            for(int i = bookList[h].getUserCount()-1; i != bookList[h].getUserCount()-6 && i >= 0; i--){
                System.out.println(bookList[h].getUser(i) + "  " + bookList[h].getRting(i));
            }
            System.out.println("--------------------------------------");
           // print overall rating of the book
            System.out.println("The overall aggregate rating is " + bookList[h].getOverallRating());
            System.out.println("--------------------------------------");
            printTopRatedVendors(bookList[h]);
            System.out.println("--------------------------------------");
        }
        else{               //if the location of hash value does not contain the wanted book, use linear probing to find it
            int i = 1;                             
            for(; bookList[h+i] != null; i++){            
                if(!bookList[h+i].getName().equals(theBook)){ 
                    continue;
                }
                else{          //if the book is found, print the details
                    System.out.println("\n-------The results of book search---------");
                    System.out.println("#5 most recent ratings for the product are#");                    
                    bookFound = true;
                    for(int j = bookList[h+i].getUserCount()-1; j != bookList[h+i].getUserCount()-6 && j >= 0; j--){
                        System.out.println(bookList[h+i].getUser(j) + "  " + bookList[h+i].getRting(j));
                    }     
                    System.out.println("--------------------------------------");
                                 //print overall rating of the book
                    System.out.println("The overall aggregate rating is " + bookList[h+i].getOverallRating());
                    System.out.println("--------------------------------------");
                    printTopRatedVendors(bookList[h]);
                    System.out.println("--------------------------------------");
                    break;
                }   
            }            
        }
        if(!bookFound){
            System.out.println("Book is not found");
        }
    }
    public float calculateOverallRating(Book book){        //calculate overall aggregate rating of given book using the equation
        float dividend = 0;
        float divider = 0;
        float intermediate = 0;
        
        for(int i = 0; i<book.getUserCount(); i++){           
            intermediate += userTable.userSearch(book.getUser(i)).getSumOfRatesOfGivenBook(book.getName());            
            dividend += (intermediate * userTable.userSearch(book.getUser(i)).getWeightOfUserRating());
            divider += (userTable.userSearch(book.getUser(i)).getWeightOfUserRating() * userTable.userSearch(book.getUser(i)).getCountOfGivenBook(book.getName()));
            intermediate = 0;
        }
        return dividend/divider;
    }
    public Book getBook(int num){ //return the number 'num' book
        return bookList[num];
    }
    public float getBookOverallRating(String bookName){   //return the overall rating of a given book
        for(int i = 0; i<1009; i++){
            if(bookList[i] != null){
                if(bookList[i].getName().equals(bookName)){
                    return bookList[i].getOverallRating();
                }
            }
        }
        return 0;
    }
    public void setVendorTable(VendorHashTable vendorTable){      //pass the refernce to vendor hash table
        this.vendorTable = vendorTable;
    }
    public void printTopRatedVendors(Book book){   //print top rated vendors of given book
        boolean swapped;              
        String nameTemp;
        float rateTemp;
        String[] vendorNames = new String[book.getVendorCount()];   //use two arrays to store details of the users who rated the book
        float[] vendorRatings = new float[book.getVendorCount()];
        for(int k = 0; k<book.getVendorCount(); k++){
            vendorNames[k] = book.getVendor(k);
            vendorRatings[k] = vendorTable.getVendorOverallRating(book.getVendor(k));      
        }        
        //use bubble sort to sort vendors list of the book
        for(int j = book.getVendorCount()-1; j>0; j--){
            swapped = false;
            for(int i = 1; i<=j; i++){
                if(vendorRatings[i - 1] > vendorRatings[i]){
                    nameTemp = vendorNames[i];
                    rateTemp = vendorRatings[i];
                    vendorNames[i] = vendorNames[i-1];
                    vendorRatings[i] = vendorRatings[i-1];
                    vendorNames[i-1] = nameTemp;
                    vendorRatings[i-1] = rateTemp;
                    swapped = true;
                }
            }
            if(!swapped){
                break;
            }
        }
        System.out.println("#Top rated vendors of the product#");   
        boolean found;
        for(int k = vendorNames.length-1; k>0; k--){      //print the top rated venodrs of the book
            found = false;                               //preventing printing the same venodr twise
            for(int m = vendorNames.length-1; m>k; m--){
                if(vendorNames[k].equals(vendorNames[m])){
                    found = true;
                    break;
                }
            }
            if(!found){
                System.out.println(vendorNames[k]+ " " + vendorRatings[k]);
            }
        }   
        
    }
}

class VendorHashTable{     //building a vendor's hash table
    private static final int M = 1009;
    Vendor[] vendorList = new Vendor[M];    
    UserHashTable userTable;     //keep refernces to user and book hash tables
    BookHashTable bookTable;
    
    VendorHashTable(UserHashTable userTable){
        this.userTable = userTable;
    }
    
    public int hash(String x) {    //calculate and return the hash value for a given vendor name
        char ch[] = x.toCharArray();
        int xlength = x.length();

        int sum = 0;
        for(int i = 0; i < xlength; i++)
            sum += ch[i];
        return sum % M;
    }   
    public void insert(Record item) {
        int h = hash(item.getVendorName()); //getting the hash value for the given book using hash()
        
        if(vendorList[h] == null){             //if the location of hash value is empty
            vendorList[h] = new Vendor();
            vendorList[h].setName(item.getVendorName());
            vendorList[h].addBook(item.getBookName());
            vendorList[h].addUser(item.getUserName(), item.getVendorRating());
        }
        else if(vendorList[h] != null && vendorList[h].getName().equals(item.getVendorName())){  //if the location of hash value contains the same vendor
            vendorList[h].addUser(item.getUserName(), item.getVendorRating());
            vendorList[h].addBook(item.getBookName());
        }        
        else if(vendorList[h] != null && !vendorList[h].getName().equals(item.getVendorName())){  
            int i = 1;                             //use linear probing to handle collision
            boolean added = false;
            for(; vendorList[h+i] != null; i++){            
                if(!vendorList[h+i].getName().equals(item.getVendorName())){  //if the vendor is not found while probing, continue the loop
                    continue;
                }
                else{                             //if the vendor is found while probing add the details to that vendor
                    vendorList[h+i].addUser(item.getUserName(), item.getVendorRating());
                    vendorList[h+i].addBook(item.getBookName());          
                    added = true;
                    break;
                }   
            }
            if(!added){                   //if the vendor is not found while probing, new vendor is inserted in to a empty slot of the hash table
                vendorList[h+i] = new Vendor();
                vendorList[h+i].setName(item.getVendorName());
                vendorList[h+i].addBook(item.getBookName());
                vendorList[h+i].addUser(item.getUserName(), item.getVendorRating());
            }
        }                
    }    
    public void vendorSearch(String theVendor){  //implementing vendro search algorithm
        boolean vendorFound = false;
        int h = hash(theVendor);  //get the hash value for the vendor name
        
        if(vendorList[h] == null){   //if the location of hash value is empty
            System.out.println("Vendor is not found");
            return;
        }
        else if(vendorList[h].getName().equals(theVendor)){   //if the location of hash value conatins the wanted vendor, print details
            vendorFound = true;
            System.out.println("\n-------The results of vendor search---------");
            System.out.println("#5 most recent ratings for the vendor are#");
            for(int i = vendorList[h].getUserCount()-1; i != vendorList[h].getUserCount()-6 && i >= 0; i--){   //print 5 most recent ratings for the vendor
                System.out.println(vendorList[h].getUser(i) + "  " + vendorList[h].getRating(i));
            }
            System.out.println("--------------------------------------");
                  //print overall aggregate rating of the vendor
            System.out.println("The overall aggregate rating is " + vendorList[h].getOverallRating());
            System.out.println("--------------------------------------");
            System.out.println("#List of products that vendor sells and overall aggregate rating of each product#");
            String[] printingBooks = new String[vendorList[h].getBookCount()];
            float[] printingRates = new float[vendorList[h].getBookCount()];            
            for(int i = vendorList[h].getBookCount()-1; i >= 0; i--){       //print the books that the vendor has
                 printingBooks[i] = vendorList[h].getBook(i);
                 printingRates[i] = bookTable.getBookOverallRating(vendorList[h].getBook(i));                             
            }
            boolean found;                                       //preventing printing the same book twise
            for(int k = 0; k<printingBooks.length; k++){
                found = false;
                for(int m = 0; m<k; m++){
                  if(printingBooks[k].equals(printingBooks[m])){
                    found = true;
                    break;
                  }
                }
                if(!found){
                    System.out.println(printingBooks[k] + " - " + printingRates[k]);
                }
            }            
            
            System.out.println("--------------------------------------");
        }   
        else{           //use linear probing to find vendor
            int i = 1;                             
            for(; vendorList[h+i] != null; i++){            
                if(!vendorList[h+i].getName().equals(theVendor)){ 
                    continue;
                }
                else{             //if the vendor is found print the details
                    vendorFound = true;
                    System.out.println("\n-------The results of vendor search---------");
                    System.out.println("#5 most recent ratings for the vendor are#");
                    for(int j = vendorList[h+i].getUserCount()-1; j != vendorList[h+i].getUserCount()-6 && j >= 0; j--){  //print 5 most recent ratings for the vendor
                        System.out.println(vendorList[h+i].getUser(j) + "  " + vendorList[h+i].getRating(j));
                    }
                        //print overall aggregate rating of the vendor
                    System.out.println("--------------------------------------");
                    System.out.println("The overall aggregate rating is " + vendorList[h+i].getOverallRating()); 
                    System.out.println("--------------------------------------");
                    System.out.println("#List of products that vendor sells and overall aggregate rating of each product#");
                    String[] printingBook = new String[vendorList[h+i].getBookCount()];
                    float[] printingRate = new float[vendorList[h+i].getBookCount()];
                    for(int j = vendorList[h+i].getBookCount()-1; j >= 0; j--){       //print the books that the vendor has
                        printingBook[j] = vendorList[h+i].getBook(j);
                        printingRate[j] = bookTable.getBookOverallRating(vendorList[h+i].getBook(j));                   
                    }    
                    boolean found;                            //preventing printing the same book twise
                    for(int k = 0; k<printingBook.length; k++){
                        found = false;
                        for(int m = 0; m<k; m++){
                            if(printingBook[k].equals(printingBook[m])){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            System.out.println(printingBook[k] + " - " + printingRate[k]);
                        }
                    }
                    System.out.println("--------------------------------------");
                    break;
                }   
            }            
        }
        if(!vendorFound){
            System.out.println("vendor is not found");
        }
    }    
    public float calculateOverallRating(Vendor vendor){    //calulate overall aggregate rating of a vendor using the given equation
        float dividend = 0;
        float divider = 0;
        float intermediate = 0;
        
        for(int i = 0; i<vendor.getUserCount(); i++){           
            intermediate += userTable.userSearch(vendor.getUser(i)).getSumOfRatesOfGivenVendor(vendor.getName());            
            dividend += (intermediate * userTable.userSearch(vendor.getUser(i)).getWeightOfUserRating());
            divider += (userTable.userSearch(vendor.getUser(i)).getWeightOfUserRating() * userTable.userSearch(vendor.getUser(i)).getCountOfGivenVendor(vendor.getName()));
            intermediate = 0;
        }
        return dividend/divider;
    }    
    public Vendor getVendor(int num){     //return the number 'num' vendor
        return vendorList[num];
    }
    public void setBookTable(BookHashTable bookTable){  //pass the reference to book hash table
        this.bookTable = bookTable;
    }
    public float getVendorOverallRating(String vendorName){   //return the overall rating of a given vendor
        for(int i = 0; i<1009; i++){
            if(vendorList[i] != null){
                if(vendorList[i].getName().equals(vendorName)){
                    return vendorList[i].getOverallRating();
                }
            }
        }
        return 0;
    }    
}
 
class UserHashTable{    //building user's hash table
    private static final int M = 1009;
    User[] userList = new User[M];    
    
    public int hash(String x) {     //calculate and return hash value 
        char ch[] = x.toCharArray();
        int xlength = x.length();

        int sum = 0;
        for(int i = 0; i < xlength; i++)
            sum += ch[i];
        return sum % M;
    }        
    public void insert(Record item) {
        int h = hash(item.getUserName()); //getting the hash value for the given book using hash()
        
        if(userList[h] == null){           //if the location of hash value is free 
            userList[h] = new User();
            userList[h].setName(item.getUserName());
            userList[h].addVendor(item.getVendorName(), item.getVendorRating());
            userList[h].addBook(item.getBookName(), item.getBookRating());
        }

        else if(userList[h] != null && userList[h].getName().equals(item.getUserName())){ //if the location of hash value contains the same book
            userList[h].addBook(item.getBookName(), item.getBookRating());
            userList[h].addVendor(item.getVendorName(), item.getVendorRating());
        }    
        else if(userList[h] != null && !userList[h].getName().equals(item.getUserName())){  
            int i = 1;                             //use linear probing to handle collision
            boolean added = false;
            for(; userList[h+i] != null; i++){            
                if(!userList[h+i].getName().equals(item.getUserName())){  //if the user is not found while probing, continue the loop
                    continue;
                }
                else{                             //if the user is found while probing add the details to that user
                    userList[h+i].addBook(item.getBookName(), item.getBookRating());
                    userList[h+i].addVendor(item.getVendorName(), item.getVendorRating());          
                    added = true;
                    break;
                }   
            }    
            if(!added){                   //if the user is not found while probing, new user is inserted in to a empty slot of the hash table
                userList[h+i] = new User();
                userList[h+i].setName(item.getUserName());
                userList[h+i].addVendor(item.getVendorName(), item.getVendorRating());
                userList[h+i].addBook(item.getBookName(), item.getBookRating());
            }            
        }
    }
    public User userSearch(String theUser){ // return the user of given user name
        int h = hash(theUser);       
        
        if(userList[h] == null){
            return null;
        }    
        else if(userList[h].getName().equals(theUser)){
            return userList[h];
        }   
        else{
            int i = 1;                             
            for(; userList[h+i] != null; i++){            
                if(!userList[h+i].getName().equals(theUser)){ 
                    continue;
                }
                else{                            
                    return userList[h+i];
                }   
            }            
        }
        return null;
    }
}
