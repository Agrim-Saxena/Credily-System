package com.credv3.webcrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.credv3.firebase.realTimeDatabase.FirebaseException;
import com.credv3.firebase.realTimeDatabase.JacksonUtilityException;

@RestController
@RequestMapping(value = "/crawler", produces = MediaType.APPLICATION_JSON_VALUE)
public class WebCrawlerController {
	@Autowired
	private WebCrawlerHandler webCrawlerHandler;

	@RequestMapping(value = "/initiate/{id}", method = { RequestMethod.OPTIONS, RequestMethod.GET })
	public void crawl(@PathVariable(name = "id") long id) throws InterruptedException, FirebaseException, JacksonUtilityException {
		webCrawlerHandler.initiateCrawler(id);

	}
}
