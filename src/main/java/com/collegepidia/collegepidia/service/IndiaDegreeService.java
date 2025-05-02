package com.collegepidia.collegepidia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class IndiaDegreeService {

    private static final String FILE_NAME = "degree_data.json";  // Name of your JSON file

    /**
     * Reads the entire JSON data from the file and returns it as a list of maps (Degree, Course, Department).
     */
    public List<Map<String, Object>> getAllDegreesCoursesDepartments() throws IOException {
        // Use ObjectMapper to map the JSON file into a List of Maps
        ObjectMapper objectMapper = new ObjectMapper();

        // Load the file from the classpath
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_NAME);
        if (inputStream == null) {
            throw new IOException("File not found in classpath: " + FILE_NAME);
        }

        return objectMapper.readValue(inputStream, List.class);
    }
}
