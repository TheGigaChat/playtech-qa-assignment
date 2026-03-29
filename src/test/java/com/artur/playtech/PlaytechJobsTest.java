package com.artur.playtech;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class PlaytechJobsTest extends BaseTest {

    @Test
    void printEstoniaJobLinks() {
        driver.get("https://www.playtechpeople.com/jobs-our/");

        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.tagName("body")
                )
        );

        List<WebElement> jobLinks = driver.findElements(
                By.xpath("//a[@href and contains(@href, 'smartrecruiters.com')]")
        );

        Set<String> estoniaJobLinks = new LinkedHashSet<>();

        for (WebElement jobLink : jobLinks) {
            String href = jobLink.getAttribute("href");
            WebElement location = jobLink.findElement(By.cssSelector(".location-link"));
            String locationText = location.getText().trim();

            // Validation
            if (href == null || href.isEmpty()) {
                continue;
            }

            if (locationText.equalsIgnoreCase("estonia")) {
                estoniaJobLinks.add(href);
            }
        }

        assertFalse(estoniaJobLinks.isEmpty(), "Expected at least one Estonia job link");

        System.out.println("Estonia job links:");
        for (String link : estoniaJobLinks) {
            System.out.println(link);
        }
    }
}
