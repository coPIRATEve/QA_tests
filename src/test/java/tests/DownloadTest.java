package tests;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DownloadTest extends BaseTest {
    private static final String SEARCH_TEXT = "Albert Einstein";
    private static final String ENGLISH = "en";
    private static final int WAIT_TIMEOUT_SECONDS = 30;
    private static final Logger LOGGER = Logger.getLogger(DownloadTest.class.getName());

    private final By mainPageIdentifier = By.cssSelector(".central-textlogo__image");
    private final By languageDropdown = By.id("searchLanguage");
    private final By searchInput = By.cssSelector("input[name='search']");
    private final By searchButton = By.cssSelector("button[type='submit']");
    private final By dropdownCheckbox = By.id("vector-page-tools-dropdown-checkbox");
    private final By pdfButton = By.id("coll-download-as-rl");
    private final By downloadButton = By.cssSelector(".oo-ui-labelElement-label");
    private final By nameOfFile = By.cssSelector(".mw-electronpdfservice-selection-label-desc");

    @Test
    public void downloadPdfTest() {
        WebElement mainPageElement = driver.findElement(mainPageIdentifier);
        Assert.assertTrue(mainPageElement.isDisplayed(), "Main page unique element is not displayed");

        WebElement dropdown = driver.findElement(languageDropdown);
        Select selectLanguage = new Select(dropdown);
        selectLanguage.selectByValue(ENGLISH);

        WebElement search = driver.findElement(searchInput);
        search.sendKeys(SEARCH_TEXT);
        driver.findElement(searchButton).click();

        driver.findElement(dropdownCheckbox).click();
        WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(pdfButton));
        button.click();
        driver.findElement(downloadButton).click();

        WebElement fileNameElement = wait.until(ExpectedConditions.presenceOfElementLocated(nameOfFile));
        String fileName = fileNameElement.getText().trim();

        File downloadedFile = new File(DOWNLOADS_DIR + File.separator + fileName);
        LOGGER.log(Level.INFO, String.format("Checking if file exists: %s", downloadedFile.getAbsolutePath()));

        Assert.assertTrue(isFileExists(downloadedFile), "File is not downloaded");
    }

    private boolean isFileExists(File file) {
        try {
            Awaitility.await().atMost(WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS).until(file::exists);
            return true;
        } catch (ConditionTimeoutException e) {
            LOGGER.log(Level.WARNING, String.format("File not found within timeout: %s", file.getAbsolutePath()));
            return false;
        }
    }
}
