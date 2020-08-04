package com.jk.gogit.network.api

import com.jk.gogit.EditProfileActivity
import com.jk.gogit.model.*
import com.jk.gogit.model.commits.CommitData
import com.jk.gogit.model.search.SearchRepoResult
import com.jk.gogit.model.search.SearchUserResult
import com.jk.gogit.ui.login.data.response.AccessToken
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*


interface IApi {


    /* @Headers("Accept: application/json")
     @POST()
     @FormUrlEncoded
     fun getAccessToken(
             @Url url: String,
             @Field("client_id") client_id: String,
             @Field("client_secret") client_secret: String,
             @Field("redirect_uri") redirect_uri: String,
             @Field("state") state: String,
             @Field("code") code: String
             *//*@Field("grant_type") grantType: String*//*): Observable<AccessToken>*/


    @GET("search/users")
    fun searchUsers(@Query("q") query: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Observable<Response<SearchUserResult>>

    @GET("search/repositories")
    fun searchRepositories(@Query("q") query: String, @Query("sort") sort: String, @Query("page") page: Int, @Query("per_page") perPage: Int): Observable<Response<SearchRepoResult>>


    @GET("users/{user}/received_events")
    suspend fun getFeed(@Path("user") user: String, @Query("page") page: Int, @Query("per_page") perPage: Int): List<Feed>


    @GET("/user")
   suspend fun getMyProfile(): UserProfile

    @GET("/notifications")
    fun getNotifications(@Query("all") isAll: Boolean,
                         @Query("page") page: Int,
                         @Query("per_page") perPage: Int,
                         @Header("no-cache") header: String): Observable<Response<List<Notification>>>

    @PATCH("/notifications/threads/{thread_id}")
    fun notificationMarkAsRead(@Path("thread_id") thread_id: String): Completable



    @GET("/users/{user}")
    fun getUserProfile(@Path("user") user: String): Observable<UserProfile>

    @GET("/orgs/{org}")
    fun getOrgProfile(@Path("org") org: String): Observable<OrgProfile>

    @PATCH("/user")
    fun saveProfile(@Body user: EditProfileActivity.EditData/*@Query("name") name: String,
                    @Query("email") email: String,
                    @Query("blog") blog: String,
                    @Query("company") company: String,
                    @Query("location") location: String,
                    @Query("hireable") hireable: String,
                    @Query("bio") bio: String*/): Observable<UserProfile>

    @GET("/user/orgs")
    fun getMyOrg(@Query("page") page: Int,
                 @Query("per_page") perPage: Int): Observable<Response<List<Org>>>

    @GET("/users/{user}/orgs")
    fun getOrgOfUser(@Path("user") user: String,
                     @Query("page") page: Int,
                     @Query("per_page") perPage: Int): Observable<Response<List<Org>>>


    @GET("/user/repos")
    fun getMyRepository(@Query("type") type: String, @Query("sort") sort: String
                        , @Query("page") page: Int,
                        @Query("per_page") perPage: Int): Observable<Response<List<Repo>>>


    @GET("/orgs/{org}/repos")
    fun getOrgRepository(@Path("org") org: String, @Query("type") type: String,
                         @Query("page") page: Int,
                         @Query("per_page") perPage: Int): Observable<Response<List<Repo>>>


    @GET("/user/starred")
    fun getMyStarredRepository(@Query("sort") sort: String, @Query("page") page: Int,
                               @Query("per_page") perPage: Int): Observable<Response<List<Repo>>>

    @GET("/users/{user}/starred")
    fun getUserStarredRepository(@Path("user") user: String,
                                 @Query("sort") sort: String,
                                 @Query("page") page: Int,
                                 @Query("per_page") perPage: Int): Observable<Response<List<Repo>>>


    @GET("/users/{user}/repos")
    fun getUserAllRepository(@Path("user") user: String,
                             @Query("type") type: String,
                             @Query("sort") sort: String,
                             @Query("page") page: Int,
                             @Query("per_page") perPage: Int): Observable<Response<List<Repo>>>


    @GET("/user/followers")
    fun getMyFollowers(@Query("page") page: Int,
                       @Query("per_page") perPage: Int): Observable<Response<List<Users>>>


    @GET("/orgs/{owner}/members")
    fun getContributors(@Path("owner") owner: String): Observable<List<Users>>

    @GET("/users/{user}/followers")
    fun getUserFollowers(@Path("user") user: String,
                         @Query("page") page: Int,
                         @Query("per_page") perPage: Int): Observable<Response<List<Users>>>

    @GET("/user/following")
    fun getMyFollowing(@Query("page") page: Int,
                       @Query("per_page") perPage: Int): Observable<Response<List<Users>>>

    @GET("/users/{user}/following")
    fun getUserFollowing(@Path("user") user: String,
                         @Query("page") page: Int,
                         @Query("per_page") perPage: Int): Observable<Response<List<Users>>>

    @GET("/user/following/{user}")
    fun ifIamFollowing(@Path("user") user: String): Completable

    @PUT("/user/following/{user}")
    fun followUser(@Path("user") user: String): Completable

    @DELETE("/user/following/{user}")
    fun unFollowUser(@Path("user") user: String): Completable

    @GET("/repos/{owner}/{repo}")
    fun getRepositoryDetails(@Path("owner") owner: String, @Path("repo") repo: String): Observable<RepositoryDetails>

    @GET("/repos/{owner}/{repo}/branches")
    fun getAllBranchOfRepo(@Path("owner") owner: String, @Path("repo") repo: String): Observable<List<Branch>>


    @GET("/repos/{owner}/{repo}/readme")
    fun getReadMeAsHTML(@Path("owner") owner: String, @Path("repo") repo: String, @Header("isHTML") header: String): Observable<String>

    @GET("/repos/{owner}/{repo}/readme")
    fun getReadMeAsJSON(@Path("owner") owner: String, @Path("repo") repo: String): Observable<ReadMeResponse>

    @GET("/repos/{owner}/{repo}/contents/{path}")
    fun getFile(@Path("owner") owner: String, @Path("repo") repo: String, @Path("path") path: String): Observable<List<File>>

    @GET()
    fun getRawFileContent(@Url url: String): Observable<String>

    @GET("/repos/{owner}/{repo}/git/blobs/{file_sha}")

    fun getFullFileAsBlob(@Path("owner") owner: String, @Path("repo") repo: String, @Path("file_sha") file_sha: String): Observable<IndividualFile>

    @GET("/repos/{owner}/{repo}/contents/{path}")
    fun getFullFile(@Path("owner") owner: String, @Path("repo") repo: String, @Path("path") path: String): Observable<IndividualFile>

    @GET("/repos/{owner}/{repo}/contents/{path}")
    fun getFullFileAsHTML(@Path("owner") owner: String, @Path("repo") repo: String, @Path("path") path: String, @Header("isHTML") header: String): Observable<String>

    @GET("/user/starred/{owner}/{repo}")
    fun ifIamStaring(@Path("owner") owner: String, @Path("repo") repo: String): Observable<Completable>

    @GET("/user/subscriptions/{owner}/{repo}")
    fun ifIamWatching(@Path("owner") owner: String, @Path("repo") repo: String): Observable<Completable>

    @PUT("/user/starred/{owner}/{repo}")
    fun doStaring(@Path("owner") owner: String, @Path("repo") repo: String): Observable<Completable>

    // @PUT("/repos/{owner}/{repo}/subscription")
    @PUT("/user/subscriptions/{owner}/{repo}")
    fun doWatch(@Path("owner") owner: String,
                @Path("repo") repo: String,
                @Query("subscribed") subscribed: Boolean,
                @Query("ignored") ignored: Boolean): Observable<Completable>

    @DELETE("/user/subscriptions/{owner}/{repo}")
    fun undoWatch(@Path("owner") owner: String,
                  @Path("repo") repo: String): Observable<Completable>

    @DELETE("/user/starred/{owner}/{repo}")
    fun unDoStaring(@Path("owner") owner: String, @Path("repo") repo: String): Observable<Completable>

    @GET("/repos/{owner}/{repo}/stargazers")
    fun getStargazersOfRepo(@Path("owner") owner: String, @Path("repo") repo: String,
                            @Query("page") page: Int,
                            @Query("per_page") perPage: Int): Observable<Response<List<Users>>>

    @GET("/repos/{owner}/{repo}/subscribers")
    fun getWatchersOfRepo(@Path("owner") owner: String, @Path("repo") repo: String
                          , @Query("page") page: Int,
                          @Query("per_page") perPage: Int): Observable<Response<List<Users>>>

    @GET("/repos/{owner}/{repo}/forks")
    fun getForkedRepo(@Path("owner") owner: String, @Path("repo") repo: String,
                      @Query("page") page: Int,
                      @Query("per_page") perPage: Int): Observable<Response<List<RepoForked>>>

    @GET("/repos/{owner}/{repo}/issues")
    @Headers("Accept: application/vnd.github.html,application/vnd.github.VERSION.raw")
    fun getIssues(@Path("owner") owner: String,
                  @Path("repo") repo: String,
            //  @Query("milestone") milestone:String,
                  @Query("state") state: String,
                  @Query("sort") sort: String,
                  @Query("direction") direction: String,
                  @Query("page") page: Int,
                  @Query("per_page") perPage: Int
    ): Observable<Response<List<Issue>>>

    @GET("/repos/{owner}/{repo}/commits")
    fun getCommits(@Path("owner") owner: String,
                   @Path("repo") repo: String,
                   @Query("sha") sha: String,
                   @Query("page") page: Int,
                   @Query("per_page") perPage: Int
    ): Observable<Response<List<CommitData>>>

    @GET("/repos/{owner}/{repo}/contributors")
    fun getContributors(@Path("owner") owner: String,
                        @Path("repo") repo: String,
                        @Query("anon") sha: Boolean,// True show  anonymous comits
                        @Query("page") page: Int,
                        @Query("per_page") perPage: Int
    ): Observable<Response<List<Users>>>


    @GET("/repos/{owner}/{repo}/issues/{issueNumber}")
    @Headers("Accept: application/vnd.github.html,application/vnd.github.VERSION.raw,"
            + "application/vnd.github.squirrel-girl-preview")
    fun getSingleIssue(@Path("owner") owner: String,
                       @Path("repo") repo: String,
                       @Path("issueNumber") issueNumber: String
    ): Observable<Issue>

    @GET()

    @Headers("Accept: application/vnd.github.symmetra-preview+json")

    fun getSingleIssueAllComment(@Url() owner: String?): Observable<List<Comments>>


    @GET("repos/{owner}/{repo}/issues/{issueNumber}/timeline")
    @Headers("Accept: application/vnd.github.mockingbird-preview,application/vnd.github.html," +
            " application/vnd.github.VERSION.raw,application/vnd.github.squirrel-girl-preview")
    fun getIssueTimeline(@Path("owner") owner: String,
                         @Path("repo") repo: String,
                         @Path("issueNumber") issueNumber: String): Observable<List<TimeLine>>

/*
    @NonNull @GET("repos/{owner}/{repo}/issues/{issueNumber}/timeline?per_page=60")
    @Headers("Accept: application/vnd.github.mockingbird-preview,application/vnd.github.html," +
            " application/vnd.github.VERSION.raw,application/vnd.github.squirrel-girl-preview")
    Observable<Response<ArrayList<IssueEvent>>> getIssueTimeline(
    @Path("owner") String owner,
    @Path("repo") String repo,
    @Path("issueNumber") int issueNumber,
    @Query("page") int page
    );*/

}