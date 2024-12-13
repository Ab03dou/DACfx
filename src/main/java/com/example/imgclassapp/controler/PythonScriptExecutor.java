package com.example.imgclassapp.controler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PythonScriptExecutor {

    public static String executePythonScript(String scriptPath, String... args) {

        String VENV_PYTHON;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            VENV_PYTHON = "C:\\Users\\"+ System.getProperty("user.home") + "\\AppData\\Local\\Programs\\Python\\Python310\\python.exe";  // for Windows
        } else {
            VENV_PYTHON = System.getProperty("user.home") + "/su/bin/python";  // for Linux/Mac
        }
        try {
            List<String> commands = new ArrayList<>();
            commands.add(VENV_PYTHON);
            commands.add(scriptPath);
            commands.addAll(Arrays.asList(args));

            ProcessBuilder processBuilder = new ProcessBuilder(commands);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Read the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            String lastLine = "";
            // Read all lines but keep only the last one
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lastLine = line;
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python script execution failed with exit code: " + exitCode);
            }

            return lastLine;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute Python script", e);
        }
    }
}