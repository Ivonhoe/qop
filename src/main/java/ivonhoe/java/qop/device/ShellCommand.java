package ivonhoe.java.qop.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivonhoe on 2017/4/17.
 */
public class ShellCommand {

    public static String exec(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = input.readLine()) != null) {
                stringBuilder.append(line);
            }
            input.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
