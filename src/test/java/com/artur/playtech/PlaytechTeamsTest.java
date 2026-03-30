package com.artur.playtech;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlaytechTeamsTest extends BaseTest {

    @Test
    void printAllTeamsFromHomepage() {
        driver.get("https://www.playtechpeople.com");

        // Wait until the team section is visible
        wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector(".teams-cards .team-card h6 span")
                )
        );

        List<WebElement> teamElements = driver.findElements(
                By.cssSelector(".teams-cards .team-card h6 span")
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
        assertTrue(teamNames.size() > 5, "Expected at least 5 teams");

        // Note: The page heading states "12 teams", but only 11 team cards are displayed.
        System.out.println("Teams count: " + teamNames.size());
        System.out.println("Teams:");

        for (String teamName : teamNames) {
            System.out.println("- " + teamName);
        }
    }
}
