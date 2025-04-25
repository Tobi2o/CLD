package com.example.appengine.springboot;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.*;

@SpringBootApplication
@RestController
public class DemoApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoApplication.class, args);
  }

  @GetMapping("/")
  public String hello() {
    return "Hello, world!";
  }

  private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

  @GetMapping("/dswritesimple")
  public String writeEntityToDatastoreSimple(@RequestParam Map<String, String> queryParameters) {
	  StringBuilder message = new StringBuilder();

	  KeyFactory keyFactory = datastore.newKeyFactory().setKind("book");
	  Key key = datastore.allocateId(keyFactory.newKey());
	  Entity entity = Entity.newBuilder(key)
			  .set("title", "The grapes of wrath")
			  .set("author", "John Steinbeck")
			  .build();
	  message.append("Writing entity to Datastore\n");
	  datastore.put(entity);
	  Entity retrievedEntity = datastore.get(key);
	  message.append("Entity retrieved from Datastore: "
			  + retrievedEntity.getString("title")
			  + " " + retrievedEntity.getString("author")
			  + "\n");
	  return message.toString();
  }

  @GetMapping("/dswrite")
  public String writeEntityToDatastore(@RequestParam Map<String, String> queryParameters) {
		// Extract the kind name and key name from the query parameters
    	String kindName = queryParameters.get("_kind");
		if (kindName == null || kindName.isEmpty()) {
			return "Error: '_kind' parameter is missing in the request.";
		}

    	String keyName = queryParameters.get("_key");
    
		// Create or retrieve the KeyFactory for the specified kind
		KeyFactory keyFactory = datastore.newKeyFactory().setKind(kindName);
		
		// Create a key for the entity; if a key name is provided use it, otherwise allocate an ID
		Key key = (keyName != null) ? keyFactory.newKey(keyName) : datastore.allocateId(keyFactory.newKey());
		
		// Build the entity using the query parameters
		Entity.Builder entityBuilder = Entity.newBuilder(key);
		for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
			if (!entry.getKey().equals("_kind") && !entry.getKey().equals("_key")) {
				entityBuilder.set(entry.getKey(), entry.getValue());
			}
		}
		
		Entity entity = entityBuilder.build();
		
		// Save the entity to Datastore
		datastore.put(entity);
		
		return "Entity with kind '" + kindName + "' and key id/name '" + (keyName != null ? keyName : key.getId()) + "' has been written to Datastore";
}


}

