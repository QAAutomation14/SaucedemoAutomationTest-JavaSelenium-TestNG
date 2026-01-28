package org.automation.qa;

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class LoginTest {

	WebDriver driver;
	ExtentReports extent;
	ExtentTest test;

	@BeforeSuite
	public void suite() {

		ExtentSparkReporter spark = new ExtentSparkReporter(
				"C:\\EclipseWorkspace\\SauceDemoAutomation\\Report\\Extent.html");
		spark.config().setDocumentTitle("Automation test");

		extent = new ExtentReports();
		extent.attachReporter(spark);
	}

	@BeforeMethod
	public void setup(Method method) {
		
		ChromeOptions options = new ChromeOptions();

		options.addArguments("--headless=new"); // REQUIRED
		options.addArguments("--no-sandbox");   // REQUIRED for GitHub runner
		options.addArguments("--disable-dev-shm-usage"); // REQUIRED
		options.addArguments("--disable-gpu");
		options.addArguments("--window-size=1920,1080");

		 


		extent.createTest(method.getName());
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.get("https://www.saucedemo.com/");
	}

	@Test
	public void FT001_Valid_Credentials() {

		driver.findElement(By.id("user-name")).sendKeys("standard_user");
		driver.findElement(By.id("password")).sendKeys("secret_sauce");
		driver.findElement(By.id("login-button")).click();

		WebElement homePageProduct = driver.findElement(By.xpath("//span[@data-test='title']"));
		String homePageElement = homePageProduct.getText();

		Assert.assertEquals(homePageElement, "Products");
	}

	@Test
	public void FT002_Invalid_Credentials() {

		driver.findElement(By.id("user-name")).sendKeys("standard_user");
		driver.findElement(By.id("password")).sendKeys("secret_sauce1223123");
		driver.findElement(By.id("login-button")).click();

		WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
		String error = errorElement.getText();

		Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
	}

	@Test
	public void EDT003_BlankFeilds() {

		driver.findElement(By.id("user-name")).sendKeys("");
		driver.findElement(By.id("password")).sendKeys("");
		driver.findElement(By.id("login-button")).click();

		WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
		String error = errorElement.getText();

		Assert.assertEquals(error, "Epic sadface: Username is required");
	}

	@Test
	public void EDT004_SpecialCharacter() {

		driver.findElement(By.id("user-name")).sendKeys("#%^$^$&%");
		driver.findElement(By.id("password")).sendKeys("%$^$&%&*%*");
		driver.findElement(By.id("login-button")).click();

		WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
		String error = errorElement.getText();

		Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
	}

	@Test
	public void EDT005_LongCharacter() {

		driver.findElement(By.id("user-name")).sendKeys(
				"#%^$4623784628462346239423942394623424234324532745283	vxz xbhzcxhgcxhgZcxZ xhjZ xhCx^$&%");
		driver.findElement(By.id("password")).sendKeys("%$^$&%&*%*");
		driver.findElement(By.id("login-button")).click();

		WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
		String error = errorElement.getText();

		Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
	}

	@Test
	public void ST_SQLInjection() {

		driver.findElement(By.id("user-name")).sendKeys("admin'OR'1'='1");
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login-button")).click();

		WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
		String error = errorElement.getText();

		Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
	}

	@Test
	public void ST_CSS_XSScripting() {

		driver.findElement(By.id("user-name")).sendKeys("<script>alert('xss')</script>");
		driver.findElement(By.id("password")).sendKeys("admin");
		driver.findElement(By.id("login-button")).click();

		WebElement errorElement = driver.findElement(By.xpath("//h3[@data-test='error']"));
		String error = errorElement.getText();

		Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
	}

	@Test
	public void ST_HTTPSEnforcement() {

		driver.get("http://www.saucedemo.com/");
		driver.findElement(By.id("user-name")).sendKeys("standard_user");
		driver.findElement(By.id("password")).sendKeys("secret_sauce");
		driver.findElement(By.id("login-button")).click();

		String url = driver.getCurrentUrl();

		Assert.assertEquals(url.startsWith("https"), true);
	}

	@AfterMethod
	public void tearDown() {
		driver.quit();
	}

	@AfterSuite
	public void afterSuite() {
		extent.flush();
	}

}
