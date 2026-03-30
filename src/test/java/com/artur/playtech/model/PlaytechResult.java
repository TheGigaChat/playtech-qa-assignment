package com.artur.playtech.model;

import java.util.List;

public record PlaytechResult(
        int teamCount,
        List<String> teamNames,
        List<String> researchAreas,
        String tallinnTartuJobLink
) {
}
