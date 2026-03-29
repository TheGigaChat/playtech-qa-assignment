package com.artur.playtech;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * I located the Research accordion by its visible button title
 * and then extracted the nested list items using the accordion body structure.
 * This avoids depending on content phrases like “risk analysis...”
 * or on the order of sections, which makes the locator more stable.
 */
public class PlaytechResearchTest extends BaseTest {

    @Test
    void printResearchAreasFromLifeAtPlaytech() {
        driver.get("https://www.playtechpeople.com/life-at-playtech/");

        // Get the research header. Relies on the Research text.
        WebElement researchButton = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//button[contains(@class,'accordion-button') and normalize-space()='Research']")
                )
        );

        // Go up to grandparent element. Rely on the structure.
        WebElement researchHeader = researchButton.findElement(By.xpath(".."));
        WebElement researchAccordionItem = researchHeader.findElement(By.xpath(".."));

        // Find the nested ul. Rely on the structure.
        List<WebElement> researchAreaElements = researchAccordionItem.findElements(
                By.xpath(".//div[contains(@class,'accordion-body')]/ul/li[ul]/ul/li")
        );

        List<String> researchAreas = new ArrayList<>();

        for (WebElement element : researchAreaElements) {
            String text = Objects.requireNonNull(element.getDomProperty("textContent")).trim();

            if (text.isEmpty()) {
                continue;
            }

            researchAreas.add(text);
        }

        assertFalse(researchAreas.isEmpty(), "Research areas list should not be empty");
        assertEquals(3, researchAreas.size(), "Expected exactly 3 research areas");

        System.out.println("Research Areas:");
        for (String area : researchAreas) {
            System.out.println("- " + area);
        }
    }
}
