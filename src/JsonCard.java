import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// todo - switch to json import of cards

/*
public class JsonCard {

    private static Gson gson;
    private static JsonReader jsonReader;

    static {
        try {
            jsonReader = new JsonReader(new FileReader("/JSON/AllCards.json"));
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }

        try {
            jsonReader.beginObject();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
*/