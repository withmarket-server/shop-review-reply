package team.bakkas.elasticsearch.domainExtensions

import team.bakkas.elasticsearch.entity.SearchShop

fun SearchShop.applyCreateReview(reviewScore: Double): SearchShop {
    this.totalScore += reviewScore
    this.reviewNumber += 1
    this.averageScore = totalScore / reviewNumber

    return this
}

fun SearchShop.applyDeleteReview(reviewScore: Double): SearchShop {
    this.totalScore -= reviewScore
    this.reviewNumber -= 1
    this.averageScore = totalScore / reviewNumber

    return this
}