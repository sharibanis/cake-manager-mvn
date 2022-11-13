package com.waracle.cakemgr;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.apache.commons.text.StringEscapeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;  
import javax.servlet.annotation.WebServlet;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
import java.io.IOException;

@RestController("/")
public class RESTController {
	@Autowired
	private final CakeRepository repository;
	private final static Logger log = LoggerFactory.getLogger(RESTController.class);
	
	public RESTController(CakeRepository repository)  {
		this.repository = repository;
	}
	
	@GetMapping("/")
	public ResponseEntity<String> getCakes() {
		log.info("Getting all cakes: getCakes()...");
		List<CakeEntity> result = repository.findAll();
		List<String> resultStringList = new ArrayList<String>();
		if (result != null) {
			for (CakeEntity cakeEntity : result) {
				resultStringList.add(StringEscapeUtils.escapeHtml4(cakeEntity.toString()));
			}
			log.info("Getting all cakes: getCakes()..." + StringEscapeUtils.escapeHtml4(resultStringList.toString()));
			return new ResponseEntity<String>(StringEscapeUtils.escapeHtml4(resultStringList.toString()), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(StringEscapeUtils.escapeHtml4(resultStringList.toString()), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/cakes")
	public ResponseEntity<String> getAllCakes() {
		log.info("Getting all cakes: getAllCakes()...");
		List<CakeEntity> result = repository.findAll();
		List<String> resultStringList = new ArrayList<String>();
		if (result != null) {
			for (CakeEntity cakeEntity : result) {
				resultStringList.add(StringEscapeUtils.escapeHtml4(cakeEntity.toString()));
			}
			log.info("Getting all cakes: getCakes()..." + StringEscapeUtils.escapeHtml4(resultStringList.toString()));
			return new ResponseEntity<String>(StringEscapeUtils.escapeHtml4(resultStringList.toString()), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(StringEscapeUtils.escapeHtml4(resultStringList.toString()), HttpStatus.NOT_FOUND);
		}
	}

	@GetMapping("/cakes/{id}")
	public ResponseEntity<String> getCakeByID(@RequestParam(value = "id") String id) {
		log.info("Getting CakeEntity @id: " + id);
		Optional<CakeEntity> optionalCake = repository.findById(Long.valueOf(id));
		if (optionalCake != null) {
			if (optionalCake.isPresent()) {
				log.info("Received CakeEntity @id: " + optionalCake.get().toString());
				JSONObject jo = new JSONObject(optionalCake.get());
				return new ResponseEntity<String>(jo.toString(), HttpStatus.OK);
			} else {
				String msg = "CakeEntity @id: " + id + " NOT FOUND";
				log.info(msg);
				return new ResponseEntity<String>(msg, HttpStatus.NOT_FOUND);
			}
		} else {
			String msg = "CakeEntity @id: " + id + " NOT FOUND";
			log.info(msg);
			return new ResponseEntity<String>(msg, HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/cakes")
	public ResponseEntity<String> createNewCake(@RequestBody CakeEntity newCake) {
		try {
			if (newCake != null && (newCake instanceof CakeEntity)) {
				log.info("Creating new Cake: " + newCake.toString());
				CakeEntity newSavedCake = repository.save(newCake);
				if (newSavedCake != null) {
					log.info("Created new Cake: " + newSavedCake.toString());
					return new ResponseEntity<String>(newSavedCake.toString(), HttpStatus.OK);
				} else {
					String msg = "Cake: " + newSavedCake + " CANNOT BE CREATED";
					log.info(msg);
					return new ResponseEntity<String>(msg, HttpStatus.BAD_REQUEST);
				}
			} else {
				String msg = "Cake: " + newCake + " CANNOT BE CREATED";
				log.info(msg);
				return new ResponseEntity<String>(msg, HttpStatus.BAD_REQUEST);
			}
		} catch (Exception ex) {
			log.error(ex.toString());
			String msg = "Cake: " + newCake + " CANNOT BE CREATED";
			log.info(msg);
			return new ResponseEntity<String>(msg, HttpStatus.BAD_REQUEST);
		}
	}
}
