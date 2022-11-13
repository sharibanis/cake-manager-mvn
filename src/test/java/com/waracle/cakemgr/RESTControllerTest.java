package com.waracle.cakemgr;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@WebMvcTest(RESTController.class)
public class RESTControllerTest {
	@Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    
    @MockBean
    static CakeRepository cakeRepository;
    
    private static final Logger log = LoggerFactory.getLogger(RESTControllerTest.class);
	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.hsqldb.jdbcDriver";
	static final String DB_URL = "jdbc:hsqldb:mem:db";
   
	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 
	static Connection conn = null; 
    
	@BeforeAll
	public static void setUp() throws Exception {
		int insertCount = 0;
		String cakesURL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";
		try {
			log.info("START UP - creating DB connection");
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		    conn.setAutoCommit(true);
			Statement stmt = conn.createStatement();
			log.info("creating TABLE cake...");
			String sql = "CREATE TABLE IF NOT EXISTS cake ( "
					+ "	ID bigint not null, "
					+ "	title varchar(255), "
					+ "	description varchar(255), "
					+ "	image varchar(255), "
					+ "	primary key (ID)"
					+ ")";
			stmt.executeUpdate(sql);
			log.info("created TABLE cake");
	  	  log.info("Downloading cake JSON from "+cakesURL);
	      try (InputStream inputStream = new URL(cakesURL).openStream()) {
	      	BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	    	sql = "INSERT INTO cake (ID, title, description, image) VALUES (?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);

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
	        	  long id = 1;
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
	                  log.info("Adding to cake table: ");
	                  preparedStatement.setLong(1, id++);
	                  preparedStatement.setString(2, title);
	                  preparedStatement.setString(3, description);
	                  preparedStatement.setString(4, image);
	                  preparedStatement.execute();
	                  insertCount++;
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
			    preparedStatement.close();
	      } catch (Exception ex) {
	    	  log.error("Error adding data to the database - 2.");
	    	  log.error(ex.toString());          
	      }
			
		} catch (Exception ex) {
			log.error(ex.toString());
		}
    	log.info("insertCount : " + insertCount);
    	log.info("Database initialised and loaded!");
	}
	
	@Test
	public void getAllCakesPassTest() {
    	log.info("getAllCakesPassTest()");
    	String result = "";
    	try {
    		List<CakeEntity> records = cakeRepository.findAll();
        	Mockito.when(cakeRepository.findAll()).thenReturn(records);
        	mockMvc.perform(MockMvcRequestBuilders
                    .get("/")
                    .contentType(MediaType.APPLICATION_JSON));
    	}	catch(Exception exception){  
			  log.info(exception.toString());
		}
	}

	
	@Test
	public void getAllCakesFailTest() {
    	log.info("getAllCakesFailTest()");
    	String result = "";
    	try {
    		List<CakeEntity> records = cakeRepository.findAll();
        	Mockito.when(cakeRepository.findAll()).thenReturn(records);
        	mockMvc.perform(MockMvcRequestBuilders
                    .get("/")
                    .contentType(MediaType.APPLICATION_JSON));
    	}	catch(Exception exception){  
			  log.info(exception.toString());
		}
	}
	
	@AfterAll
	public static void cleanUp(){
		log.info("@AfterAll cleanUp() method called");
    	try {
    		Statement stmt = conn.createStatement();
    		String sql = "DROP TABLE cake";
    		int count = stmt.executeUpdate(sql);
        	log.info("cleanUp() count: " + count);
		    conn.commit();
        	conn.close();
    	}	catch(Exception exception){  
			  log.info(exception.toString());
		}
	}
}
