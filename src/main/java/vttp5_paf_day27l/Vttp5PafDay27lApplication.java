package vttp5_paf_day27l;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import vttp5_paf_day27l.model.Comment;

@SpringBootApplication
public class Vttp5PafDay27lApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Vttp5PafDay27lApplication.class, args);		
	
	}

	@Autowired
	MongoTemplate template;

	@Override
	public void run(String... args) throws Exception {

		ApplicationArguments aa = new DefaultApplicationArguments(args);
		
		String jsonFileName;
		
		if (aa.containsOption("load")) {

			jsonFileName = aa.getOptionValues("load").get(0);
			String fileName = jsonFileName.split("\\.")[0];

			System.out.println("File Name: " + fileName);


			// drop collection 
			if (template.getCollectionNames().contains(fileName)) {

				template.dropCollection(fileName);
				System.out.println(fileName + " collection dropped from DB :)");

			} else {
				System.out.println(fileName + " collection doesn't exist");

			}



			// create collection
			template.createCollection(fileName); 
			System.out.println(fileName + " collection created in DB :)");



			// populate collection
			File jsonFile = new File(jsonFileName);
			ObjectMapper objectMapper = new ObjectMapper(); 
			List<Comment> comments = objectMapper.readValue(jsonFile, objectMapper.getTypeFactory().constructCollectionType(List.class, Comment.class));


			// List<Document> documents = objectMapper.readValue(jsonFile, objectMapper.getTypeFactory().constructCollectionType(List.class, Document.class));

			List<Document> documents = new ArrayList<>();
			for (Comment comment : comments) {
				Document doc = new Document()
					.append("id", comment.getId())
					.append("user", comment.getUser());
				documents.add(doc);
			}


			template.getCollection(fileName).insertMany(documents);
			System.out.println(fileName + " collection populated");
		

		} 
		
		else {
			System.out.println("Load argument not found");

		}

	}

}
