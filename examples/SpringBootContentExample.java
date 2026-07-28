// Copyright (c) 2026 Anup Ranjan. Licensed under Apache 2.0 (https://www.apache.org/licenses/LICENSE-2.0)
//
// Example: a Spring Boot @Controller resolving Experience Manager content
// for a category page. ContentEnrichClient is auto-configured by
// nexarank-spring-boot-starter — no bean wiring needed beyond the
// nexarank.* properties below.
//
// application.yml:
//   nexarank:
//     base-url: https://api.yourstore.com/nexarank
//     tenant-id: default
//     max-retries: 2
//     base-delay-ms: 200

package com.example.storefront;

import com.nexarank.client.ContentEnrichClient;
import com.nexarank.client.ContentEnrichResult;
import com.nexarank.client.ContentZone;
import com.nexarank.client.ContentZoneResult;
import com.nexarank.client.PageContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class CategoryPageController {

    private final ContentEnrichClient contentEnrichClient;

    public CategoryPageController(ContentEnrichClient contentEnrichClient) {
        this.contentEnrichClient = contentEnrichClient;
    }

    @GetMapping("/category")
    public String categoryPage(@RequestParam String category, Model model) {
        ContentEnrichResult content = contentEnrichClient.enrich(
                List.of(ContentZone.CATEGORY_BANNER),
                PageContext.of().with("pageType", "category").with("category", category)
        );

        ContentZoneResult banner = content.get(ContentZone.CATEGORY_BANNER);
        if (banner != null) {
            model.addAttribute("bannerImageUrl", banner.getImageUrl());
            model.addAttribute("bannerHeadline", banner.getHeadline());
        }
        // else: template falls back to its own default banner markup

        return "category-page";
    }
}
