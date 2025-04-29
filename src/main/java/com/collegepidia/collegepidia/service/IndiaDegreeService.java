package com.collegepidia.collegepidia.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class IndiaDegreeService {

    private static final String FILE_PATH = "src/main/resources/degree_data.json";  // Path to your JSON file

    /**
     * Reads the entire JSON data from the file and returns it as a list of maps (Degree, Course, Department).
     */
    public List<Map<String, Object>> getAllDegreesCoursesDepartments() throws IOException {
        // Use ObjectMapper to map the JSON file into a List of Maps
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(FILE_PATH), List.class);
    }
}
