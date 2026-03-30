package com.artur.playtech.model;

import java.util.List;

/**
 * This record is a helper wrapper object for consistent text formating and writing.
 *
 * @param teamCount
 * @param teamNames
 * @param researchAreas
 * @param tallinnTartuJobLink
 */
public record PlaytechResult(
        int teamCount,
        List<String> teamNames,
        List<String> researchAreas,
        String tallinnTartuJobLink
) {
}
