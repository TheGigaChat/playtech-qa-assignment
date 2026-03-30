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

/**
 * This class contains all tests for this assignment.
 */
public class PlaytechAssignmentTest extends BaseTest {

    // Extract the magic strings and numbers into constants.
    private static final String BASE_URL = "https://www.playtechpeople.com";
    private static final String OUTPUT_PATH = "output/results.txt";

    private static final int EXPECTED_TEAM_COUNT = 11;
    private static final int EXPECTED_RESEARCH_COUNT = 3;

    private static final By PARENT_ELEMENT = By.xpath("..");
    private static final By BODY = By.tagName("body");
    private static final By LOCATION_LINK = By.cssSelector(".location-link");
    private static final By TEAM_CARDS = By.cssSelector(".teams-cards .team-card h6 span");
    private static final By RESEARCH_BUTTON =
            By.xpath("//button[contains(@class,'accordion-button') and normalize-space()='Research']");
    private static final By RESEARCH_AREA_ELEMENTS =
            By.xpath(".//div[contains(@class,'accordion-body')]/ul/li[ul]/ul/li");
    private static final By JOB_LINKS =
            By.xpath("//a[@href and contains(@href, 'smartrecruiters.com')]");


    /**
     * A main wrapper of 3 testing parts:
     * Team count + list team names,
     * list research areas,
     * finds a link for tallinn/tartu job.
     *
     * @throws Exception: ResultFileWriter can't resolve a method writeToFile
     */
    @Test
    void collectsPlaytechDataAndExportToTxt() throws Exception {
        List<String> teamNames = extractTeamNames();
        List<String> researchAreas = extractResearchAreas();
        String tallinnTartuJobLink = findJobForTallinnAndTartu();

        // Wrap all results into a PlaytechResult record for better consistency of formattedResult.format method.
        PlaytechResult result = new PlaytechResult(
                teamNames.size(),
                teamNames,
                researchAreas,
                tallinnTartuJobLink
        );

        // Format the results for a structured string.
        String formattedResult = PlaytechResultFormatter.format(result);
        // Write the formattedResults to the .txt file.
        ResultFileWriter.writeToFile(OUTPUT_PATH, formattedResult);

        System.out.println("Output file written to:\n" + formattedResult);
    }

    /**
     * This helper method extracts all team names into a list from Playtech home page.
     *
     * @return a list of team names.
     */
    private List<String> extractTeamNames() {
        driver.get(BASE_URL);

        // Wait until the page loads the team card elements.
        wait.until(ExpectedConditions.visibilityOfElementLocated(TEAM_CARDS));

        // Find all team elements.
        List<WebElement> teamElements = driver.findElements(TEAM_CARDS);

        List<String> teamNames = new ArrayList<>();

        for (WebElement teamElement : teamElements) {
            String teamName = teamElement.getText().trim();

            // Skip empty options.
            if (teamName.isEmpty()) {
                continue;
            }

            // Add all team names into a list.
            teamNames.add(teamName);
        }

        // Validate that team count is equal to the expected team count.
        assertEquals(EXPECTED_TEAM_COUNT, teamNames.size(), "Expected " + EXPECTED_TEAM_COUNT + " teams");
        return teamNames;
    }

    /**
     * This helper method extracts all research areas into a list from Playtech life-at-playtech page.
     *
     * @return a list of all research areas.
     */
    private List<String> extractResearchAreas() {
        driver.get(BASE_URL + "/life-at-playtech/");

        // Get the research header. Relies on the text.
        WebElement researchButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(RESEARCH_BUTTON)
        );

        // Go up to grandparent element. Rely on the structure.
        WebElement researchHeader = researchButton.findElement(PARENT_ELEMENT);
        WebElement researchAccordionItem = researchHeader.findElement(PARENT_ELEMENT);

        // Find the nested ul. Rely on the structure.
        List<WebElement> researchAreaElements = researchAccordionItem.findElements(RESEARCH_AREA_ELEMENTS);

        List<String> researchAreas = new ArrayList<>();

        for (WebElement element : researchAreaElements) {
            try {
                // Find the textContent of the element.
                String text = Objects.requireNonNull(element.getDomProperty("textContent")).trim();

                if (text.isEmpty()) {
                    continue;
                }

                // Collect all the research areas into a list.
                researchAreas.add(text);

            } catch (Exception e) {
                // If the textContent property is not found, might throw an error.
                System.out.println(e.getMessage());
            }
        }

        // Validate that research areas list size is equal to the expected research area count.
        assertEquals(EXPECTED_RESEARCH_COUNT, researchAreas.size(), "Expected " + EXPECTED_RESEARCH_COUNT + " research areas");
        return researchAreas;
    }

    /**
     * This helper method finds a job link for both Tallinn and Tartu from Playtech jobs-our page.
     *
     * @return a job link.
     */
    private String findJobForTallinnAndTartu() {
        driver.get(BASE_URL + "/jobs-our/");

        wait.until(ExpectedConditions.visibilityOfElementLocated(BODY));

        // Find tag 'a' with a link to the 'smartrecruiters.com' site.
        List<WebElement> jobLinks = driver.findElements(JOB_LINKS);

        // Initialize the result link.
        String tallinnTartuJobLink = null;

        for (WebElement jobLink : jobLinks) {

            // Get and validate href
            String href = jobLink.getAttribute("href");
            if (href == null || href.isEmpty()) {
                continue;
            }

            String locationText = null;

            try {
                // Find location web element and extract the text.
                WebElement location = jobLink.findElement(LOCATION_LINK);
                locationText = location.getText().trim();
            } catch (Exception e) {
                // If the .location-link is not found, throws an error.
                System.out.println(e.getMessage());
                // Skip all elements without class '.location-link'.
                continue;
            }

            // A separate try block for easier understanding of the error message reasons.
            try {
                // Check that the link is reachable.
                int statusCode = getStatusCode(href);
                if (statusCode >= 400) {
                    System.out.println("Unreachable link. Status code is " + statusCode);
                    // Skip all unreachable links.
                    continue;
                }
            } catch (Exception e) {
                // If the status code is not found on the given href element.
                System.out.println("Could not check status for link: " + href);
                continue;
            }

            // Skip all locations except of Estonia.
            if (!locationText.equalsIgnoreCase("estonia")) {
                continue;
            }

            // Go to a new job link.
            driver.get(href);

            wait.until(ExpectedConditions.visibilityOfElementLocated(BODY));

            // Extract the element that contains an address and a city.
            WebElement locationElement = driver.findElement(By.cssSelector("spl-job-location"));
            String normalizedLocation = locationElement.getText().trim().toLowerCase();

            // Find a link for tallinn and tartu and stop the loop.
            if (normalizedLocation.contains("tallinn") && normalizedLocation.contains("tartu")) {
                tallinnTartuJobLink = href;
                break;
            }
        }

        // Check that we found a link and return it.
        assertNotNull(tallinnTartuJobLink, "Link for both Tallinn and Tartu was not found.");
        return tallinnTartuJobLink;
    }

    /**
     * Helper method to get the status code of the given url.
     *
     * @param url that we want to check.
     * @return status code.
     * @throws Exception that http request can not be resolved.
     */
    private int getStatusCode(String url) throws Exception {
        // Initialize a new default http client.
        HttpClient client = HttpClient.newHttpClient();

        // Build a request.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        // Get the response of the request.
        HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

        return response.statusCode();
    }
}
