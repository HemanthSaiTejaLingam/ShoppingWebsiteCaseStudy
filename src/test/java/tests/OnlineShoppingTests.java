package tests;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import util.Drivers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;

public class OnlineShoppingTests {
	WebDriver driver;
	ExtentSparkReporter sparkReporter;
	ExtentReports extent;
	ExtentTest logger;

	SimpleDateFormat d=new SimpleDateFormat("yy-MM-dd-hh-mm-ss-ms");

	@BeforeTest
	public void startReportBeforeTest() {
		driver=Drivers.getDriver("chrome");
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

		String path=System.getProperty("user.dir")+"/extent-reports/"+d.format(new Date())+".html";
		sparkReporter = new ExtentSparkReporter(path);
		extent=new ExtentReports();

		extent.attachReporter(sparkReporter);

		extent.setSystemInfo("Stream", "QE");
		extent.setSystemInfo("Coder", "Hemanth");

		sparkReporter.config().setDocumentTitle("TestMe App Analysis");
		sparkReporter.config().setReportName("Test Result Reports");
		sparkReporter.config().setTheme(Theme.DARK);

	}
	@Test(priority=1)
	public void testRegistration() {
		logger=extent.createTest("Testing Registration page");
		driver.get("https://lkmdemoaut.accenture.com/TestMeApp/fetchcat.htm");
		driver.findElement(By.linkText("SignUp")).click();

		driver.findElement(By.id("userName")).sendKeys("Naruto34");
		driver.findElement(By.id("firstName")).sendKeys("Test");
		driver.findElement(By.id("lastName")).sendKeys("User");

		String avail=driver.findElement(By.xpath("//*[@id=\'err\']")).getText();
		if(avail.equals("Available")) {
			driver.findElement(By.id("password")).sendKeys("Pass1234");
			driver.findElement(By.id("pass_confirmation")).sendKeys("Pass1234");
			driver.findElement(By.id("gender")).click();
			driver.findElement(By.id("emailAddress")).sendKeys("testuser@gmail.com");
			driver.findElement(By.id("mobileNumber")).sendKeys("7965476467");
			driver.findElement(By.xpath("//*[@id=\'dob\']")).sendKeys("08/01/1998");
			driver.findElement(By.id("address")).sendKeys("New York,USA");
			Select s=new Select(driver.findElement(By.id("securityQuestion")));
			s.selectByIndex(2);
			driver.findElement(By.id("answer")).sendKeys("pinky");
			driver.findElement(By.cssSelector("input[type='submit']")).click();

			Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\'errormsg\'][4]")).getText(),
					"User Registered Succesfully!!! Please login");
		}
		else {
			Assert.assertEquals(avail,"Name Already Exists");
			driver.navigate().back();
			driver.findElement(By.linkText("SignIn")).click();
		}
	}
	@Test(priority=2)
	public void testLogin() {
		logger=extent.createTest("Testing Login page");

		driver.findElement(By.id("userName")).sendKeys("Naruto34");
		driver.findElement(By.id("password")).sendKeys("Pass1234");
		driver.findElement(By.cssSelector("input[type='submit']")).click();

		Assert.assertEquals(driver.getTitle(),"Home");

		/*String page = driver.getTitle();
		if(page.equals("Home")) {
			Assert.assertTrue(true);
		}
		else {
			Assert.assertEquals(driver.findElement(By.xpath("//*[@id=\'errormsg\']")).getText(),
					"Username or Password is wrong here!!!");
		}*/
	}
	@Test(priority=3)
	public void testCart() {
		logger=extent.createTest("Testing Cart page");
		driver.findElement(By.id("myInput")).sendKeys("Headphone");
		driver.findElement(By.cssSelector("input[type='submit']")).click();
		driver.findElement(By.linkText("Add to cart")).click();
		driver.findElement(By.partialLinkText("Cart")).click();;
		Assert.assertEquals(driver.getTitle(), "View Cart");
	}
	@Test(priority=4)
	public void testPayment() {
		logger=extent.createTest("Testing Payment page");
		driver.findElement(By.partialLinkText("Checkout")).click();
		driver.findElement(By.cssSelector("input[type='submit'][value='Proceed to Pay']")).click();
		driver.findElement(By.xpath("//label[contains(text(),'Andhra Bank')]")).click();
		driver.findElement(By.xpath("//*[@id=\"btn\"]")).click();
		driver.findElement(By.cssSelector("input[type='text'][name='username']")).sendKeys("123456");
		driver.findElement(By.cssSelector("input[type='password'][name='password']")).sendKeys("Pass@456");
		driver.findElement(By.cssSelector("input[type='submit'][value='LOGIN']")).click();
		driver.findElement(By.name("transpwd")).sendKeys("Trans@456");
		driver.findElement(By.cssSelector("input[type='submit']")).click();
		String s=driver.findElement(By.xpath("/html/body/b/section/div/div/div")).getText();
		boolean t=s.contains("Your order has been confirmed");
		driver.findElement(By.linkText("SignOut")).click();
		Assert.assertTrue(t);
	} 

	@AfterMethod
	public void getResultAfterMethod(ITestResult result){
		if(result.getStatus()==ITestResult.SUCCESS) {
			logger.log(Status.PASS,
					MarkupHelper.createLabel("TestCase run Successfully and the method is "+result.getMethod().getMethodName(), ExtentColor.GREEN));
		}
		else if(result.getStatus()==ITestResult.FAILURE) {
			logger.log(Status.FAIL,
					MarkupHelper.createLabel("TestCase is Failed and the method is "+result.getMethod().getMethodName(), ExtentColor.RED));
			TakesScreenshot ss=(TakesScreenshot) driver;
			File source=ss.getScreenshotAs(OutputType.FILE);
			String path=System.getProperty("user.dir")+"/extent-reports/screenshots/"+d.format(new Date())+".png";
			try {
				FileUtils.copyFile(source, new File(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.log(Status.FAIL,result.getThrowable().getMessage());
			logger.addScreenCaptureFromPath(path, result.getMethod().getMethodName());
		}
	}


	@AfterTest
	public void endReportAfterTest() {
		driver.close();
		extent.flush();
	}

}
