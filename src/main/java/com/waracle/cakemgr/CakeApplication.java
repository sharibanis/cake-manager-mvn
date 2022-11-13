package com.waracle.cakemgr;

import org.springframework.boot.SpringApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.*;
import java.net.URL;
import java.util.List;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import com.waracle.cakemgr.CakeEntity;

@SpringBootApplication
//@Configuration
public class CakeApplication {
	private final static Logger log = LoggerFactory.getLogger(CakeApplication.class);
	@Autowired
	CakeRepository cakeRepository;
	@Value("${cakes.URL}")
	String cakesURL;

	public static void main(String... args) {
		log.info("Starting CakeApplication");
		SpringApplication.run(CakeApplication.class, args);
	}

    @Bean
    CommandLineRunner initDatabase(CakeRepository repository) {
      return args -> {
    	  log.info("initDatabase");
    	  log.info("Downloading cake JSON from "+cakesURL);
          try (InputStream inputStream = new URL(cakesURL).openStream()) {
          	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

              StringBuffer buffer = new StringBuffer();
              String line = reader.readLine();
              while (line != null) {
                  buffer.append(line);
                  line = reader.readLine();
              }

              log.info("Parsing cake JSON");
              JsonParser parser = new JsonFactory().createParser(buffer.toString());
              if (JsonToken.START_ARRAY != parser.nextToken()) {
                  throw new Exception("bad token");
              }

              JsonToken nextToken = parser.nextToken();
              while(nextToken == JsonToken.START_OBJECT) {
            	  log.info("Creating cake entity");

                  CakeEntity cakeEntity = new CakeEntity();
                  log.info("Title: "+parser.nextFieldName());
                  String title = parser.nextTextValue();
                  log.info("Title: "+title);
                  cakeEntity.setTitle(title);

                  log.info("Description: "+parser.nextFieldName());
                  String description = parser.nextTextValue();
                  log.info("Description: "+description);
                  cakeEntity.setDescription(description);

                  log.info("Image: "+parser.nextFieldName());
                  String image = parser.nextTextValue();
                  log.info("Image: "+image);
                  cakeEntity.setImage(image);

                  try {
                      log.info("Adding cake entity: " + cakeEntity.toString());
                      cakeRepository.save(cakeEntity);
                      log.info("Added cake entity.");
                  } catch (ConstraintViolationException cvex) {
                	  log.error("Error adding data to the database - 1.");
                	  log.error(cvex.toString());
                  }

                  nextToken = parser.nextToken();
                  log.info(nextToken.toString());

                  nextToken = parser.nextToken();	
                  log.info(nextToken.toString());
              }

          } catch (Exception ex) {
        	  log.error("Error adding data to the database - 2.");
        	  log.error(ex.toString());          
          }
    	  log.info("Database Initialised.");
      };
    }
}