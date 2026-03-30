package com.artur.playtech;

import com.artur.playtech.model.PlaytechResult;
import com.artur.playtech.utils.PlaytechResultFormatter;
import com.artur.playtech.utils.ResultFileWriter;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class PlaytechAssignmentTest extends BaseTest {

    private static final String BASE_URL = "https://www.playtechpeople.com";
    private static final String OUTPUT_PATH = "output/results.txt";

    private static final int REQUIRED_TEAM_COUNT = 11;
    private static final int REQUIRED_RESEARCH_COUNT = 3;

    private static final String TEAM_CARDS_SELECTOR = ".teams-cards .team-card h6 span";
    private static final String XPATH_RESEARCH_BUTTON_SELECTOR =
            "//button[contains(@class,'accordion-button') and normalize-space()='Research']";
    private static final String XPATH_RESEARCH_AREA_ELEMENTS_SELECTOR =
            ".//div[contains(@class,'accordion-body')]/ul/li[ul]/ul/li";

    @Test
    void collectsPlaytechDataAndExportToTxt() throws Exception {
        List<String> teamNames = extractTeamNames();
        List<String> researchAreas = extractResearchAreas();
        String tallinnTartuJobLink = findJobForTallinnAndTartu();

        PlaytechResult result = new PlaytechResult(
                teamNames.size(),
                teamNames,
                researchAreas,
                tallinnTartuJobLink
        );

        String formattedResult = PlaytechResultFormatter.format(result);
        ResultFileWriter.writeToFile(OUTPUT_PATH, formattedResult);

        System.out.println("Output file written to:\n" + formattedResult);
    }

    private List<String> extractTeamNames() {
        driver.get(BASE_URL);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(TEAM_CARDS_SELECTOR))
        );

        List<WebElement> teamElements = driver.findElements(
                By.cssSelector(TEAM_CARDS_SELECTOR)
        );

        List<String> teamNames = new ArrayList<>();

        for (WebElement teamElement : teamElements) {
            String teamName = teamElement.getText().trim();

            // Skip empty options
            if (teamName.isEmpty()) {
                continue;
            }

            teamNames.add(teamName);
        }

        // Basic validations
        assertFalse(teamNames.isEmpty(), "Team list should not be empty");
        assertEquals(REQUIRED_TEAM_COUNT, teamNames.size(), "Expected " + REQUIRED_TEAM_COUNT + " teams");
        return teamNames;
    }

    private List<String> extractResearchAreas() {
        driver.get(BASE_URL + "/life-at-playtech/");

        // Get the research header. Relies on the Research text.
        WebElement researchButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath(XPATH_RESEARCH_BUTTON_SELECTOR)
                )
        );

        // Go up to grandparent element. Rely on the structure.
        WebElement researchHeader = researchButton.findElement(By.xpath(".."));
        WebElement researchAccordionItem = researchHeader.findElement(By.xpath(".."));

        // Find the nested ul. Rely on the structure.
        List<WebElement> researchAreaElements = researchAccordionItem.findElements(
                By.xpath(XPATH_RESEARCH_AREA_ELEMENTS_SELECTOR)
        );

        List<String> researchAreas = new ArrayList<>();

        for (WebElement element : researchAreaElements) {
            // TODO add try block
            String text = Objects.requireNonNull(element.getDomProperty("textContent")).trim();

            if (text.isEmpty()) {
                continue;
            }

            researchAreas.add(text);
        }

        assertFalse(researchAreas.isEmpty(), "Research areas list should not be empty");
        assertEquals(REQUIRED_RESEARCH_COUNT, researchAreas.size(), "Expected " + REQUIRED_RESEARCH_COUNT + " research areas");
        return researchAreas;
    }

    private String findJobForTallinnAndTartu() throws Exception {
        driver.get(BASE_URL + "/jobs-our/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.tagName("body")
        ));

        List<WebElement> jobLinks = driver.findElements(
                By.xpath("//a[@href and contains(@href, 'smartrecruiters.com')]")
        );

        String tallinnTartuJobLink = null;

        for (WebElement jobLink : jobLinks) {
            String href = jobLink.getAttribute("href");

            if (href == null || href.isEmpty()) {
                continue;
            }

            String locationText = null;

            try {
                WebElement location = jobLink.findElement(By.cssSelector(".location-link"));
                locationText = location.getText().trim();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                continue;
            }

            try {
                int statusCode = getStatusCode(href);
                if (statusCode >= 400) {
                    System.out.println("Unreachable link. Status code is " + statusCode);
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Could not check status for link: " + href);
                continue;
            }

            if (!locationText.equalsIgnoreCase("estonia")) {
                continue;
            }

            driver.get(href);

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));

            WebElement locationElement = driver.findElement(By.cssSelector("spl-job-location"));
            String normalizedLocation = locationElement.getText().trim().toLowerCase();

            if (normalizedLocation.contains("tallinn") && normalizedLocation.contains("tartu")) {
                tallinnTartuJobLink = href;
                break;
            }
        }

        assertNotNull(tallinnTartuJobLink, "Link for both Tallinn and Tartu was not found.");
        return tallinnTartuJobLink;
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
