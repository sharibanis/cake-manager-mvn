package com.waracle.cakemgr;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  @Bean
  CommandLineRunner initDatabase(CakeRepository repository) {
    return args -> {
  	  log.info("initDatabase");
  	  log.info("Downloading cake JSON...");
        try (InputStream inputStream = new URL("https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json").openStream()) {
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
                System.out.println("Creating cake entity");

                CakeEntity cakeEntity = new CakeEntity();
                log.info("Title: "+parser.nextFieldName());
                cakeEntity.setTitle(parser.nextTextValue());

                log.info("Description: "+parser.nextFieldName());
                cakeEntity.setDescription(parser.nextTextValue());

                log.info("Image: "+parser.nextFieldName());
                cakeEntity.setImage(parser.nextTextValue());

                Session session = HibernateUtil.getSessionFactory().openSession();
                try {
                    session.beginTransaction();
                    session.persist(cakeEntity);
                    log.info("Adding cake entity");
                    session.getTransaction().commit();
                } catch (ConstraintViolationException ex) {

                }
                session.close();

                nextToken = parser.nextToken();
                log.info(nextToken);

                nextToken = parser.nextToken();
                log.info(nextToken);
            }

        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    	
		//log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
		//log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
    };
  }
}