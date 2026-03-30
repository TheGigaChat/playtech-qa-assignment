package com.artur.playtech;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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

//        Set<String> estoniaJobLinks = new LinkedHashSet<>();
        String tallinnTartuJobLink = null;

        for (WebElement jobLink : jobLinks) {
            String href = jobLink.getAttribute("href");
            WebElement location = jobLink.findElement(By.cssSelector(".location-link"));
            String locationText = location.getText().trim();

            // Validation
            if (href == null || href.isEmpty()) {
                continue;
            }

            try {
                int statusCode = getStatusCode(href);
                if (statusCode >= 400) {
                    System.out.println("Not working link. Status code is " + statusCode);
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Could not check status for link: " + href);
                continue;
            }

            if (!locationText.equalsIgnoreCase("estonia")) {
                continue;
            }

//            System.out.println("Href: " + href);

            driver.get(href);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

            WebElement locationElement = driver.findElement(By.cssSelector("spl-job-location"));
            String normalizedLocation = locationElement.getText().trim().toLowerCase();

            if (normalizedLocation.contains("tallinn") && normalizedLocation.contains("tartu")) {
                tallinnTartuJobLink = href;
                break;
            }

        }

        System.out.println("Working link: " + tallinnTartuJobLink);
        assertNotNull(tallinnTartuJobLink, "Link for both Tallinn and Tartu was not found.");
    }

    /**
     * Helper method to get the status code of the given url.
     * @param url
     * @return
     * @throws Exception
     */
    private int getStatusCode(String url) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        return response.statusCode();
    }
}
