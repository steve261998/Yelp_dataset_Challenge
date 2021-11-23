package dbms;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class populate {

    private static final String YELP_USER_FILE = "C:\\Users\\steve\\Desktop\\Acads\\Database_Systems\\My_HW\\HW4\\YelpDataset\\yelp_user.json";
    private static final String Y_BUSINESS_FILE = "C:\\Users\\steve\\Desktop\\Acads\\Database_Systems\\My_HW\\HW4\\YelpDataset\\YELP_BUSINESS.json";
    private static final String Y_REVIEW_FILE = "C:\\Users\\steve\\Desktop\\Acads\\Database_Systems\\My_HW\\HW4\\YelpDataset\\YELP_REVIEW.json";
    private static final String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521:DBFAIL";
    private static final String USER_NAME = "SYSTEM";
    private static final String PASSWORD = "Santino1998";

    public populate() {
    }

    public static Connection connect_to_db()throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(ORACLE_URL, USER_NAME, PASSWORD);
        return connection;

    }

    public static void populate_yelp_user()  throws IOException, JSONException, ClassNotFoundException, SQLException, ParseException {
        FileReader file = new FileReader(YELP_USER_FILE);
        BufferedReader bfr = new BufferedReader(file);
        String line;
        Connection conn = connect_to_db();
        while((line = bfr.readLine())!= null){
            JSONObject obj = new JSONObject(line);
            String user_id = obj.getString("user_id");
            String name = obj.getString("name");
            String yelping_since = obj.getString("yelping_since");
            int review_count = obj.getInt("review_count");
            int votes = obj.getJSONObject("votes").getInt("funny")
                    + obj.getJSONObject("votes").getInt("useful")
                    + obj.getJSONObject("votes").getInt("cool");
            double average_stars = obj.getDouble("average_stars");
            int friend_count = obj.getJSONArray("friends").length();
            //System.out.println(user_id + " - " + yelping_since);
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Y_USERS VALUES (?, ?, ?, ?, ?, ?, ?)");
            statement.setString(1, user_id);
            statement.setString(2, name);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            Date date = format.parse(yelping_since);
            statement.setDate(3, new java.sql.Date(date.getTime()));
            //System.out.println(user_id + " - " + new java.sql.Date(date.getTime()).toString());
            statement.setInt(4, review_count);
            statement.setInt(5, votes);
            statement.setDouble(6, average_stars);
            statement.setInt(7, friend_count);
            statement.executeUpdate();
            statement.close();
        }
        conn.close();
        bfr.close();

    }

    public static void populate_business() throws IOException, JSONException, ClassNotFoundException, SQLException {
        FileReader file = new FileReader(Y_BUSINESS_FILE);
        BufferedReader bfr = new BufferedReader(file);
        String line;
        Connection conn = connect_to_db();
        while((line = bfr.readLine())!= null){
            JSONObject obj = new JSONObject(line);
            String business_id = obj.getString("business_id");
            String business_name = obj.getString("name");
            String city = obj.getString("city");
            String state = obj.getString("state");
            int review_count = obj.getInt("review_count");
            double stars = obj.getDouble("stars");
            System.out.println(business_id);
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Y_BUSINESS VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, business_id);
            statement.setString(2, business_name);
            statement.setString(3, city);
            statement.setString(4, state);
            statement.setInt(5, review_count);
            statement.setDouble(6, stars);
            statement.executeUpdate();
            statement.close();
        }
        conn.close();
        bfr.close();

    }

    public static void populate_review() throws IOException, JSONException, ClassNotFoundException, SQLException, ParseException {
        FileReader file = new FileReader(Y_REVIEW_FILE);
        BufferedReader bfr = new BufferedReader(file);
        String line;
        Connection conn = connect_to_db();
        while((line = bfr.readLine())!= null){
            JSONObject obj = new JSONObject(line);
            String review_id = obj.getString("review_id");
            String author = obj.getString("user_id");
            String publish_date = obj.getString("date");
            String business_id = obj.getString("business_id");
            int stars = obj.getInt("stars");
            int votes = obj.getJSONObject("votes").getInt("funny")
                    + obj.getJSONObject("votes").getInt("useful")
                    + obj.getJSONObject("votes").getInt("cool");
            System.out.println(review_id);
            PreparedStatement statement = conn.prepareStatement("INSERT INTO Y_REVIEWS VALUES (?, ?, ?, ?, ?, ?)");
            statement.setString(1, review_id);
            statement.setString(2, author);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = format.parse(publish_date);
            statement.setDate(3, new java.sql.Date(date.getTime()));
            statement.setString(4, business_id);
            statement.setInt(5, stars);
            statement.setInt(6, votes);
            statement.executeUpdate();
            statement.close();
        }
        conn.close();
        bfr.close();
    }

    public static void populate_category () throws IOException, JSONException, ClassNotFoundException, SQLException{
        Set<String> main_categories = new HashSet<String>();
        String[] main_category_list = {"Active Life" ,"Arts & Entertainment", "Automotive", "Car Rental", "Cafes", "Transportation",
                "Beauty & Spas", "Convenience Stores", "Dentists", "Doctors", "Drugstores", "Department Stores", "Education",
                "Event Planning & Services", "Flowers & Gifts", "Food", "Health & Medical", "Home Services", "Home & Garden",
                "Hospitals", "Hotels & Travel", "Hardware Stores", "Grocery", "Medical Centers", "Nurseries & Gardening", "Shopping",
                "Nightlife", "Medical Centers", "Nurseries & Gardening", "Nurseries & Gardening", "Nightlife", "Restaurants"};
        for(int i = 0; i < main_category_list.length; i++){
            main_categories.add(main_category_list[i]);
        }
        FileReader file = new FileReader(Y_BUSINESS_FILE);
        BufferedReader bfr = new BufferedReader(file);
        String line;
        Connection conn = connect_to_db();
        while((line = bfr.readLine())!= null){
            JSONObject obj = new JSONObject(line);
            String business_id = obj.getString("business_id");
            JSONArray categories = obj.getJSONArray("categories");
            List<String> sub_categories = new ArrayList<String>();
            List<String> curr_main_categories = new ArrayList<String>();
            for(int i = 0; i < categories.length(); i++){
                String category = categories.getString(i);
                if (main_categories.contains(category)){
                    curr_main_categories.add(category);
                }else{
                    sub_categories.add(category);
                }
            }

            for(int i = 0; i < curr_main_categories.size(); i++){
                PreparedStatement statement = conn.prepareStatement("INSERT INTO MAIN_CATEGORIES VALUES (?, ?)");
                System.out.println(business_id + "   "  + curr_main_categories.get(i));
                statement.setString(1, business_id);
                statement.setString (2, curr_main_categories.get(i));
                statement.executeUpdate();
                statement.close();
            }

            for(int i = 0; i < sub_categories.size(); i++){
                PreparedStatement statement = conn.prepareStatement("INSERT INTO SUB_CATEGORIES VALUES (?, ?)");
                System.out.println(business_id + "   "  + sub_categories.get(i));
                statement.setString(1, business_id);
                statement.setString (2, sub_categories.get(i));
                statement.executeUpdate();
                statement.close();
            }
        }
        conn.close();
        bfr.close();
    }

    public static void populate_attribute() throws IOException, JSONException, ClassNotFoundException, SQLException{
        FileReader file = new FileReader(Y_BUSINESS_FILE);
        BufferedReader bfr = new BufferedReader(file);
        String line;
        Connection conn = connect_to_db();
        while((line = bfr.readLine())!= null){
            JSONObject obj = new JSONObject(line);
            String business_id = obj.getString("business_id");
            JSONObject attributes_obj = obj.getJSONObject("attributes");
            Map<String,String> attributes = new HashMap<String, String>();
            attributes = getAttributes(attributes_obj);
            for(String key: attributes.keySet()){
                PreparedStatement statement = conn.prepareStatement("INSERT INTO BUSINESS_ATTRIBUTES VALUES (?, ?, ?)");
                System.out.println(business_id + "   " + key + "   " + attributes.get(key));
                statement.setString(1, business_id);
                statement.setString (2, key);
                statement.setString (3, attributes.get(key));
                statement.executeUpdate();
                statement.close();
            }
        }
        conn.close();
        bfr.close();
    }


    public static Map<String,String> getAttributes(JSONObject source) throws JSONException{
        Map<String,String> attributes = new HashMap<>();
        Iterator<?> keys = source.keys();
        while(keys.hasNext()){
            String key = (String)keys.next();
            if(source.get(key) instanceof JSONObject){
                JSONObject sub_obj = new JSONObject((source.get(key)).toString());
                Iterator<?> sub_keys = sub_obj.keys();
                while(sub_keys.hasNext()){
                    String sub_key = (String)sub_keys.next();
                    attributes.put(key + " " + sub_key, sub_obj.getString(sub_key));
                }
            }else{
                attributes.put(key, source.getString(key));
            }
        }
        return attributes;
    }





    public static void main(String[] args) {
        populate test = new populate();
        try{
            test.populate_yelp_user();
            test.populate_business();
            test.populate_review();
            test.populate_category();
            test.populate_attribute();
        }catch(Exception e){
            System.out.println(e);
        }

    }
}


