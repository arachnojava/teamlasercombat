package tlc.data.characters;

import mhframework.MHRandom;

public class TLCNames
{
    public static String boyName()
    {
        String[] boyNames = new String[] 
                                       {
                                           "Michael", "Steve", "Joe", "John", "Lloyd",
                                           "Kevin", "James", "Shaun", "Roger", "Floyd",
                                           "Waldo", "Rick", "Karl", "Julien", "Dave",
                                           "Justin", "Ed", "Brian", "Daniel", "Ralph",
                                           "Ross", "Chris", "Al", "Kerry", "Jason",
                                           "Nick", "Chad", "Adam", "William", "Murphy",
                                           "Andrew", "Wade", "Stan", "Thomas", "Brandon",
                                           "Tony", "Scott", "Seth", "Dennis", "Eugene",
                                           "Craig", "Emory", "Mike", "Joseph", "Jacob",
                                           "Anthony", "David", "Dan", "Nicholas", "Bill",
                                           "Carl", "Alan", "Allen", "Casey", "Kelsey",
                                           "Jon", "Johnny", "Eric", "Drew", "Gene",
                                           "Bert", "Fred", "Billy", "Avery", "Zack",
                                           "Jack", "Brock", "Spike", "Terry", "Leo",
                                           "Quincy", "Dwayne", "Lucas", "Luke", "Christopher",
                                           "Jay", "Patrick", "Steven", "Stephen", "Pat",
                                           "Mac", "Gary", "Scotty", "Lucien", "Sam", 
                                           "Samuel", "Max", "Sammy", "Jonathan", "TJ",
                                           "Juan", "Jacques", "Adrian", "Zachary", "Isaac",
                                           "Oliver", "Cory", "Xavier", "Bernard", "Bernie",
                                           "Barney", "Matthew", "Matt", "Tom", "Tommy",
                                           "Pierre", "Jean-Luc", "Gus", "Edgar", "Edward",
                                           "Eddy", "Eddie", "Phillip", "Phil", "Duncan",
                                           "Sean", "Donald", "Don", "Mick", "Micky",
                                           "Robert", "Bob", "Rob", "Burt", "Birt",
                                           "Robbie", "Bobby", "Chester", "Peter", "Dion"
                                       };
        return boyNames[MHRandom.random(0, boyNames.length-1)];
    }


    public static String girlName()
    {
        String[] girlNames = new String[] 
                                        {
                                            "Michelle", "Star", "Tabitha", "April", "Faye", 
                                            "Fanny", "Elodie", "Leana", "Kathy", "Lena", 
                                            "Catherine", "Jenny", "Sarah", "Judy", "Kristen",
                                            "Elaine", "Rose", "Janet", "Eileen", "Linda",
                                            "Karen", "Lynn", "Carol", "Julie", "Robin",
                                            "Roxy", "Ginger", "Ann", "Elizabeth", "Ashley",
                                            "Beverly", "Shannon", "Jenna", "Kayla", "Kate",
                                            "Debbie", "Mary", "Denise", "Delphine", "Christine",
                                            "Maureen", "May", "Suzanne", "Amy", "Karina",
                                            "Jill", "Laura", "Liz", "Kris", "Sara", "Carrie",
                                            "Jan", "Debra", "Deb", "Janis", "Becky", "Sue",
                                            "Rosemary", "Meg", "Peggy", "Colleen", "Kristin",
                                            "Shelly", "Mandy", "Marie", "Jean", "Isabelle",
                                            "Aundie", "Brenda", "Wendy", "Judith", "Leigh"
                                        };
        return girlNames[MHRandom.random(0, girlNames.length-1)];
    }
}
