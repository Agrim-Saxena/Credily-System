package com.credv3.webcrawler;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.credv3.common.entities.OnGoingMonitoring;
import com.credv3.firebase.realTimeDatabase.FirebaseException;
import com.credv3.firebase.realTimeDatabase.JacksonUtilityException;
import com.credv3.helper.CentralService;
import com.credv3.helper.ConstantExtension;
import com.credv3.helper.ConstantKey;

import io.github.bonigarcia.wdm.WebDriverManager;

@Component
public class WebCrawlerHandler extends CentralService {
	@Autowired
	private S3Services s3Services;

	public void initiateCrawler(long id) throws FirebaseException, JacksonUtilityException {
		OnGoingMonitoring onGoingMonitoring = onGoingMonitoringJPARepo.findById(id).orElse(null);
		if (onGoingMonitoring.getMonitoringDatabase().equalsIgnoreCase("OFAC")) {
			try {
				crawlerOFAC(onGoingMonitoring);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (onGoingMonitoring.getMonitoringDatabase().equalsIgnoreCase("OIG")) {
			try {
				crawlerOIG(onGoingMonitoring);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void crawlerOIG(OnGoingMonitoring onGoingMonitoring) throws FirebaseException, JacksonUtilityException {

		WebDriverManager.chromedriver().setup();
		WebDriver driver = null;

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox");
		options.addArguments("start-maximized");
		options.setExperimentalOption("useAutomationExtension", false);
		driver = new ChromeDriver(options);
		driver.get("https://exclusions.oig.hhs.gov/");

		WebElement lastNameBox = driver.findElement(By.name("ctl00$cpExclusions$txtSPLastName"));
		lastNameBox.sendKeys(onGoingMonitoring.getProvider().getLastName());

		WebElement firstNameBox = driver.findElement(By.name("ctl00$cpExclusions$txtSPFirstName"));
		firstNameBox.sendKeys(onGoingMonitoring.getProvider().getFirstName());

		WebElement searchElement = driver.findElement(By.name("ctl00$cpExclusions$ibSearchSP"));
		searchElement.click();

		onGoingMonitoring.setMonitoringStatus("Not Found");

		try {
			onGoingMonitoring = takeFullPageScreenShot(driver, onGoingMonitoring);
		} catch (Exception e) {
			e.printStackTrace();
			driver.close();
			// TODO: handle exception
		}

		onGoingMonitoring.setUpdatedDate(new Date());
		onGoingMonitoring.setCheckDate(new Date());
		onGoingMonitoringJPARepo.save(onGoingMonitoring);

		try {
			firebaseRealTimeDatabase.firebaseStatus(ConstantKey.MONITORING_STATUS,
					onGoingMonitoring.getProvider().getUuid().toString(), ConstantKey.COMPLETED);
		} catch (Exception e) {
			e.printStackTrace();
			driver.close();
		}

		driver.close();
	}

	public void crawlerOFAC(OnGoingMonitoring onGoingMonitoring)
			throws InterruptedException, URISyntaxException, FirebaseException, JacksonUtilityException {

		WebDriverManager.chromedriver().setup();
		WebDriver driver = null;

		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox");
		options.addArguments("start-maximized");
		options.setExperimentalOption("useAutomationExtension", false);
		driver = new ChromeDriver(options);
		driver.get("https://sanctionssearch.ofac.treas.gov/");

		WebElement nameBox = driver.findElement(By.name("ctl00$MainContent$txtLastName"));
		nameBox.sendKeys(
				onGoingMonitoring.getProvider().getFirstName() + " " + onGoingMonitoring.getProvider().getLastName());

		WebElement searchElement = driver.findElement(By.name("ctl00$MainContent$btnSearch"));
		searchElement.click();

		if (isElementPresent(driver, By.linkText("Your search has not returned any results."))) {
			onGoingMonitoring.setMonitoringStatus("Not Found");
		} else {
			boolean flag = false;
			List<WebElement> datas = driver.findElements(By.linkText(onGoingMonitoring.getProvider().getLastName()
					+ ", " + onGoingMonitoring.getProvider().getFirstName()));
			for (int i = 0; i < datas.size(); i++) {
				datas.get(i).click();
				try {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
					String dob = simpleDateFormat.format(onGoingMonitoring.getProvider().getDob());

					if (driver.getPageSource().contains(dob)) {
						flag = true;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					driver.close();
					// TODO: handle exception
				}
			}
			if (flag) {
				onGoingMonitoring.setMonitoringStatus("Found");
			} else {

				onGoingMonitoring.setMonitoringStatus("Not Found");
			}
			try {
				onGoingMonitoring = takeFullPageScreenShot(driver, onGoingMonitoring);
			} catch (Exception e) {
				e.printStackTrace();
				driver.close();
				// TODO: handle exception
			}

			onGoingMonitoring.setUpdatedDate(new Date());
			onGoingMonitoring.setCheckDate(new Date());
			onGoingMonitoringJPARepo.save(onGoingMonitoring);

			try {
				firebaseRealTimeDatabase.firebaseStatus(ConstantKey.MONITORING_STATUS,
						onGoingMonitoring.getProvider().getUuid().toString(), ConstantKey.COMPLETED);
			} catch (Exception e) {
				e.printStackTrace();
				driver.close();
			}
		}
		driver.close();

	}

	public boolean isElementPresent(WebDriver driver, By locatorKey) {
		try {
			driver.findElement(locatorKey);
			return true;
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return false;
		}
	}

	public OnGoingMonitoring takeFullPageScreenShot(WebDriver driver, OnGoingMonitoring onGoingMonitoring)
			throws IOException {
		JavascriptExecutor jsExec = (JavascriptExecutor) driver;
		jsExec.executeScript("window.scrollTo(0, 0);");
		Long innerHeight = (Long) jsExec.executeScript("return window.innerHeight;");
		Long scroll = innerHeight;
		Long scrollHeight = (Long) jsExec.executeScript("return document.body.scrollHeight;");

		scrollHeight = scrollHeight + scroll;

		List<byte[]> images = new ArrayList<>();

		do {
			byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			images.add(screenshot);
			jsExec.executeScript("window.scrollTo(0, " + innerHeight + ");");
			innerHeight = innerHeight + scroll;
		} while (scrollHeight >= innerHeight);

		BufferedImage result = null;
		Graphics g = null;

		int x = 0, y = 0;
		for (byte[] image : images) {
			InputStream is = new ByteArrayInputStream(image);
			BufferedImage bi = ImageIO.read(is);
			if (result == null) {
				// Lazy init so we can infer height and width
				result = new BufferedImage(bi.getWidth(), bi.getHeight() * images.size(), BufferedImage.TYPE_INT_RGB);
				g = result.getGraphics();
			}
			g.drawImage(bi, x, y, null);
			y += bi.getHeight();
		}

		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(result, "png", os);
			ByteArrayInputStream in = new ByteArrayInputStream(os.toByteArray());
			Long contentLength = Long.valueOf(os.toByteArray().length);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(contentLength);
			String key = "monitoring/" + onGoingMonitoring.getId() + ".png";
			s3Services.putObject(key, in, meta);
			onGoingMonitoring.setScreenshot(ConstantExtension.s3ResourceURL + key);
			in.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return onGoingMonitoring;
	}
}
