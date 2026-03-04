package com.mybsam12138.review.controller;

import com.mybsam12138.review.review.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/review")
    public String review() throws Exception {
        String repoPath = System.getProperty("user.dir");
        return reviewService.reviewCurrentBranch(repoPath, "main");
    }
}