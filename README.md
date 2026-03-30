# Playtech QA Assignment

This project contains a Java + Selenium + JUnit solution for the Playtech QA assignment.

## Tech Stack
- Java 21
- Maven
- Selenium
- JUnit 5
- WebDriverManager

## What the test does
The final test:
1. opens the Playtech website
2. extracts the list of teams from the homepage
3. extracts research areas from the "Life at Playtech" page
4. finds a working Estonia job link that is available for both Tallinn and Tartu
5. exports the collected result into a txt file

## Run the project
```bash
mvn test
```

## Output
- generated result file:
```text
output/results.txt
```
- example committed result file:
```text
example-output/example-results.txt
```

## Notes
- The homepage section mentions 12 teams, but only 11 team cards were displayed during extraction.
- Some SmartRecruiters links were no longer available, so link status was checked before opening job detail pages.

## Author
Artur Dzekunov
