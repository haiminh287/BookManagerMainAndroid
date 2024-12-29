//package com.example.bookmanager;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.example.bookmanager.model.User;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.FileReader;
//import java.io.FileWriter;
//
//public class FileHelper {
//    private static final String FILE_NAME = "users.json"; // Tên tệp
//
//    public static void saveUser(Context context, User user) {
//        try {
//            JSONArray jsonArray;
//            String data = readFile(context);
//            Log.i("Data",""+data);// Đọc tệp nếu đã tồn tại
//            if (!data.isEmpty()) {
//                jsonArray = new JSONArray(data);
//            } else {
//                jsonArray = new JSONArray();
//            }
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("fullName", user.getFullname());
//            jsonObject.put("username", user.getUsername());
//            jsonObject.put("password", user.getPassword());
//            jsonArray.put(jsonObject);
//
//            // Tạo đường dẫn đầy đủ tới tệp
//            File file = new File(context.getFilesDir(), FILE_NAME);
//            Log.i("File Path", "Saving file to: " + file.getAbsolutePath());
//            FileWriter fileWriter = new FileWriter(file);
//            Log.i("File Path", "Saving: " + jsonArray);
//            fileWriter.write(jsonArray.toString());
//            fileWriter.close();
//
//        } catch (Exception e) {
//            Log.e("FileHelper", "Error saving user: ", e);
//        }
//    }
//
//    // Đọc dữ liệu từ file JSON
//    public static String readFile(Context context) {
//        StringBuilder stringBuilder = new StringBuilder();
//        try {
//            File file = new File(context.getFilesDir(), FILE_NAME);
//            FileReader reader = new FileReader(file);
//            int i;
//            while ((i = reader.read()) != -1) {
//                stringBuilder.append((char) i);
//            }
//            reader.close();
//        } catch (Exception e) {
//            Log.e("FileHelper", "Error reading file: ", e);
//            return ""; // Trả về chuỗi rỗng nếu gặp lỗi
//        }
//        return stringBuilder.toString();
//    }
//
//    public static User login(Context context, String username, String password) {
//        try {
//            String data = readFile(context);
//            if (!data.isEmpty()) {
//                JSONArray jsonArray = new JSONArray(data);
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    if (jsonObject.getString("username").equals(username) &&
//                            jsonObject.getString("password").equals(password)) {
//
//                        String userName = jsonObject.getString("username");
//                        String fullName = jsonObject.getString("fullName");
//                        String passWord = jsonObject.getString("password");
//                        return new User(userName, passWord,fullName);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            Log.e("FileHelper", "Error during login: ", e);
//        }
//        return null;
//    }
//}
