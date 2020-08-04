package com.jk.gogit.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jk.gogit.EditProfileActivity
import com.jk.gogit.model.*
import com.jk.gogit.model.commits.CommitData
import com.jk.gogit.network.api.IApi
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject


class UserViewModel : ViewModel() {

    var liveData = MutableLiveData<UserProfile>()
    @Inject
    lateinit var api: IApi



    /*fun getUserProfile(userId: String): Observable<UserProfile> {
        return if (userId.contentEquals("N/A")) {
            api.getMyProfile()
        } else {
            api.getUserProfile(userId)
        }
    }*/

    fun saveProfile(userProfile: EditProfileActivity.EditData/*name: String, email: String, blog: String, company: String, location: String, hireable: String, bio: String*/): Observable<UserProfile> {
        return api.saveProfile(userProfile/*name, email, blog, company, location, hireable, bio*/)

    }

    fun getOrgProfile(org: String): Observable<OrgProfile> {
        return api.getOrgProfile(org)

    }

    fun getOrgOfUser(userId: String, page: Int, perPage: Int): Observable<Response<List<Org>>> {
        return if (userId.contentEquals("N/A"))
            api.getMyOrg(page, perPage)
        else
            api.getOrgOfUser(userId, page, perPage)


    }

    fun getRepository(type: String, userId: String, sort: String, page: Int, perPage: Int): Observable<Response<List<Repo>>> {
        return if (userId.contentEquals("N/A"))
            api.getMyRepository(type, sort, page, perPage)
        else
            api.getUserAllRepository(userId, type, sort, page, perPage)

    }

    fun getOrgRepository(org: String, type: String, page: Int, perPage: Int): Observable<Response<List<Repo>>> {
        return api.getOrgRepository(org, type, page, perPage)

    }

    fun getNotifications(isAll: Boolean, page: Int, perPage: Int): Observable<Response<List<Notification>>> {
        return api.getNotifications(isAll, page, perPage, "no-cache")
    }

    fun notificationMarkAsRead(thread_id: String): Completable{
        return api.notificationMarkAsRead(thread_id)
    }

    fun getStarredRepository(userId: String, sort: String, page: Int, perPage: Int): Observable<Response<List<Repo>>> {
        return if (userId.contentEquals("N/A"))
            api.getMyStarredRepository(sort, page, perPage)
        else
            api.getUserStarredRepository(userId, sort, page, perPage)

    }

    fun getFollowers(userId: String, page: Int, perPage: Int): Observable<Response<List<Users>>> {
        return if (userId.contentEquals("N/A"))
            api.getMyFollowers(page, perPage)
        else
            api.getUserFollowers(userId, page, perPage)

    }

    fun getContributors(owner: String): Observable<List<Users>> {
        return api.getContributors(owner)
    }

    fun getFollowing(userId: String, page: Int, perPage: Int): Observable<Response<List<Users>>> {
        return if (userId.contentEquals("N/A"))
            api.getMyFollowing(page, perPage)
        else
            api.getUserFollowing(userId, page, perPage)

    }

    fun ifIamFollowing(userId: String): Completable {
        return api.ifIamFollowing(userId)

    }

    fun followUser(user: String): Completable {
        return api.followUser(user)
    }

    fun unFollowUser(user: String): Completable {
        return api.unFollowUser(user)
    }


    fun getRepoDetails(owner: String, repo: String): Observable<RepositoryDetails> {

        return api.getRepositoryDetails(owner, repo)
    }

    fun getAllBranchOfRepo(owner: String, repo: String): Observable<List<Branch>> {

        return api.getAllBranchOfRepo(owner, repo)
    }

    fun getReadMeAsHTML(owner: String, repo: String): Observable<String> {
        return api.getReadMeAsHTML(owner, repo, "true")
    }

    fun getReadMeAsJSON(owner: String, repo: String): Observable<ReadMeResponse> {
        return api.getReadMeAsJSON(owner, repo)
    }

    fun getFile(owner: String, repo: String, path: String): Observable<List<File>> {
        return api.getFile(owner, repo, path)
    }

    fun getRawFileContent(url: String): Observable<String> {
        return api.getRawFileContent(url)
    }

    fun getFullFile(owner: String, repo: String, path: String): Observable<IndividualFile> {
        return api.getFullFile(owner, repo, path)

    }

    fun getFullFileAsBlob(owner: String, repo: String, file_sha: String): Observable<IndividualFile> {
        return api.getFullFileAsBlob(owner, repo, file_sha)

    }

    fun getFullFileAsHTML(owner: String, repo: String, path: String): Observable<String> {
        return api.getFullFileAsHTML(owner, repo, path, "true")
    }

    fun ifIamStaring(owner: String, repo: String): Observable<Completable> {
        return api.ifIamStaring(owner, repo)
    }

    fun ifIamWatching(owner: String, repo: String): Observable<Completable> {
        return api.ifIamWatching(owner, repo)
    }

    fun doStaring(owner: String, repo: String): Observable<Completable> {
        return api.doStaring(owner, repo)
    }

    fun doWatch(owner: String, repo: String, subscribed: Boolean, ignored: Boolean): Observable<Completable> {
        return api.doWatch(owner, repo, subscribed, ignored)
    }

    fun undoWatch(owner: String, repo: String): Observable<Completable> {
        return api.undoWatch(owner, repo)
    }

    fun unDoStaring(owner: String, repo: String): Observable<Completable> {
        return api.unDoStaring(owner, repo)
    }

    fun getStargazersOfRepo(owner: String, repo: String, page: Int, perPage: Int): Observable<Response<List<Users>>> {
        return api.getStargazersOfRepo(owner, repo, page, perPage)
    }

    fun getWatchersOfRepo(owner: String, repo: String, page: Int, perPage: Int): Observable<Response<List<Users>>> {
        return api.getWatchersOfRepo(owner, repo, page, perPage)
    }

    fun getForkedRepo(owner: String, repo: String, page: Int, perPage: Int): Observable<Response<List<RepoForked>>> {
        return api.getForkedRepo(owner, repo, page, perPage)
    }

    fun getIssues(owner: String,
                  repo: String,
                  milestone: String,
                  state: String,
                  page: Int, perPage: Int): Observable<Response<List<Issue>>> {
        return api.getIssues(owner, repo,
                state,
                milestone,
                "dsc",
                page,
                perPage)

    }

    fun getCommits(owner: String,
                   repo: String,
                   sha: String,
                   page: Int,
                   perPage: Int): Observable<Response<List<CommitData>>> {
        return api.getCommits(owner, repo,
                sha,
                page,
                perPage)

    }

    fun getContributors(owner: String,
                        repo: String,
                        anon: Boolean,
                        page: Int,
                        perPage: Int): Observable<Response<List<Users>>> {
        return api.getContributors(owner, repo,
                anon,
                page,
                perPage)

    }

    fun getSingleIssue(owner: String,
                       repo: String,
                       issueNumber: String): Observable<Issue> {
        return api.getSingleIssue(owner, repo,
                issueNumber)
    }

    fun getSingleIssueAllComment(url: String?): Observable<List<Comments>> {
        return api.getSingleIssueAllComment(url)
    }

    fun getIssueTimeline(owner: String,
                         repo: String,
                         issueNumber: String): Observable<List<TimeLine>> {
        return api.getIssueTimeline(owner, repo, issueNumber)
    }


/* fun downLoadReadme(url:String): Observable<String> {

     return api.downLoadReadme(url)
 }
*/
/*
   fun getUser(userId: String): Observable<FinalData> {
        if (userId.contentEquals("N/A")) {
            //Self
            return SingleToObservable
                    .zip<UserProfile,
                            List<Repo>,
                            List<Users>,
                            List<Users>,
                            FinalData>(api.getMyProfile(),
                            api.getMyRepository("all"),
                            api.getMyFollowing(),
                            api.getMyFollowers(), Function4<UserProfile, List<Repo>, List<Users>, List<Users>, FinalData>
                    { j, k, l, m ->
                        merge(j, k, l, m)
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()
                    )
        } else {

//Others
            return Observable
                    .zip<UserProfile,
                            List<Repo>,
                            List<Users>,
                            List<Users>,
                            FinalData>(api.getUserProfile(userId),
                            api.getUserAllRepository(userId,"all"),
                            api.getUserFollowing(userId),
                            api.getUserFollowers(userId), Function4<UserProfile, List<Repo>, List<Users>, List<Users>, FinalData>
                    { j, k, l, m ->
                        merge(j, k, l, m)
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
        }
    }

    private fun merge(loggedInUser: UserProfile,
                      repo: List<Repo>,
                      followers: List<Users>,
                      followings: List<Users>): FinalData {
        val data = FinalData()
        data.userData = loggedInUser
        data.repo = repo
        data.followers = followers
        data.following = followings

        return data
    }


    class FinalData {
        lateinit var userData: UserProfile
        lateinit var repo: List<Repo>
        lateinit var followers: List<Users>
        lateinit var following: List<Users>

    }*/
}