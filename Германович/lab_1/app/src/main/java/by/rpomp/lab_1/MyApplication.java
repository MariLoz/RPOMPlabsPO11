package by.rpomp.lab_1;

import android.app.Application;
import android.os.Build;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    public String apodUrl;
    public String mrpUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        apodUrl = "https://api.nasa.gov/planetary/apod?api_key=aoh9UE1vutaEG8RApZ36IwLsPjjTkprMnv3wpgqg";
        mrpUrl = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?api_key=aoh9UE1vutaEG8RApZ36IwLsPjjTkprMnv3wpgqg&earth_date=2017-6-3";
    }

    public Map<String, String> getUrlArguments(String urlString) {
        Map<String, String> arguments = new HashMap<>();
        try {
            String[] parts = urlString.split("\\?");
            if (parts.length > 1) {
                String query = parts[1];
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=");
                    String key = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    }
                    String value = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    }
                    arguments.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arguments;
    }
}
