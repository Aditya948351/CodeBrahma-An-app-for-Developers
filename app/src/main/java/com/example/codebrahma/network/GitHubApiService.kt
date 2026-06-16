package com.example.codebrahma.network

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GitHubApiService {

    @GET("user")
    fun getUser(@Header("Authorization") token: String): Call<GitHubUser>

    @GET("user/repos")
    fun getRepos(
        @Header("Authorization") token: String,
        @Query("per_page") perPage: Int = 100
    ): Call<List<GitHubRepo>>

    @GET("search/issues")
    fun searchIssues(
        @Header("Authorization") token: String,
        @Query("q") query: String
    ): Call<SearchResponse>

    @GET("search/commits")
    fun searchCommits(
        @Header("Authorization") token: String,
        @Header("Accept") accept: String = "application/vnd.github.cloak-preview",
        @Query("q") query: String
    ): Call<SearchResponse>
}

data class GitHubUser(
    val login: String,
    val name: String?,
    val bio: String?,
    val company: String?,
    val location: String?,
    val followers: Int,
    val following: Int,
    val public_repos: Int,
    val public_gists: Int,
    val avatar_url: String?
)

data class GitHubRepo(
    val stargazers_count: Int
)

data class SearchResponse(
    @SerializedName("total_count") val totalCount: Int
)
