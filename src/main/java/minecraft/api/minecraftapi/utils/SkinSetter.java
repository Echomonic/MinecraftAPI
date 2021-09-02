package minecraft.api.minecraftapi.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkinSetter {

    public String get(String url , String arg){
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format(url, arg)).openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String output;
                while ((output = bufferedReader.readLine()) != null){
                    stringBuilder.append(output);
                }
                return stringBuilder.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return "error";
    }
    public String getSkin(String data){
        final Pattern pattern = Pattern.compile("\"value\" : \"(.*?)\",");
        final Matcher matcher = pattern.matcher(data);
        matcher.find();
        return matcher.group(1);
    }
    public String getSig(String data){
        final Pattern pattern = Pattern.compile("\"signature\" : \"(.*?)\"");
        final Matcher matcher = pattern.matcher(data);
        matcher.find();
        return matcher.group(1);
    }
    public String addCharToString(String str, char c, int pos) {
        StringBuilder stringBuilder = new StringBuilder(str);
        stringBuilder.insert(pos, c);
        return stringBuilder.toString();
    }

    // this is a manual way to format uuid I am sure there is better ways
    public String formatUUID(String id){
        String uuid = addCharToString(id , '-' , 8);
        uuid = addCharToString(uuid , '-',13);
        uuid = addCharToString(uuid , '-',18);
        uuid = addCharToString(uuid , '-',23);
        return uuid;
    }

    public String getUUID(String body){
        final Pattern pattern = Pattern.compile("id\":\"(.*?)\"}");
        final Matcher matcher = pattern.matcher(body);
        matcher.find();
        String id = matcher.group(1);
        return formatUUID(id);
    }
}
