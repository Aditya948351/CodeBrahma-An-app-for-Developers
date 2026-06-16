package com.example.codebrahma.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.codebrahma.MainActivity
import com.example.codebrahma.network.GitHubApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GitHubSignInActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val data: Uri? = intent?.data
        if (data != null && data.toString().startsWith("codebrahma://callback")) {
            handleGitHubRedirect(data)
        } else {
            initiateGitHubLogin()
        }
    }

    private fun initiateGitHubLogin() {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.addCustomParameter("allow_signup", "false")
        // Request scopes for reading user data and repos (for stars count)
        provider.scopes = listOf("read:user", "user:email", "public_repo")

        auth.startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener { authResult ->
                Log.d("GitHubSignIn", "Login successful: ${authResult.user?.displayName}")
                val credential = authResult.credential as? OAuthCredential
                val accessToken = credential?.accessToken
                if (accessToken != null) {
                    fetchExtendedDataAndSave(authResult.user!!, accessToken)
                } else {
                    // Fallback if no token (shouldn't happen with proper scopes)
                    saveUserToFirestore(authResult.user!!, null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("GitHubSignIn", "Login failed: ${e.message}")
                finish()
            }
    }

    private fun handleGitHubRedirect(data: Uri) {
        val code = data.getQueryParameter("code")
        if (code != null) {
            Log.d("GitHubSignIn", "Received GitHub Auth Code: $code")
            exchangeCodeForToken(code)
        } else {
            Log.e("GitHubSignIn", "No auth code received from GitHub")
            finish()
        }
    }

    private fun exchangeCodeForToken(code: String) {
        // This part is tricky with Firebase Auth as it handles the exchange internally usually.
        // If we are here, it means we might be handling a manual redirect if configured.
        // For now, we rely on the standard flow.
        finish()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun fetchExtendedDataAndSave(user: com.google.firebase.auth.FirebaseUser, accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://api.github.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val api = retrofit.create(GitHubApiService::class.java)
                val authHeader = "token $accessToken"

                // 1. Fetch User Details
                val userDetails = api.getUser(authHeader).execute().body()

                // 2. Fetch Repos to calculate stars
                val repos = api.getRepos(authHeader).execute().body() ?: emptyList()
                val totalStars = repos.sumOf { it.stargazers_count }

                // 3. Search Issues (Issues created by user)
                val username = userDetails?.login ?: ""
                val issuesResponse = api.searchIssues(authHeader, "author:$username type:issue").execute().body()
                val totalIssues = issuesResponse?.totalCount ?: 0

                // 4. Search PRs (PRs created by user)
                val prsResponse = api.searchIssues(authHeader, "author:$username type:pr").execute().body()
                val totalPRs = prsResponse?.totalCount ?: 0

                // 5. Search Commits (Commits by user)
                val commitsResponse = api.searchCommits(authHeader, "application/vnd.github.cloak-preview", "author:$username").execute().body()
                val totalCommits = commitsResponse?.totalCount ?: 0

                val extendedData = hashMapOf(
                    "bio" to (userDetails?.bio ?: ""),
                    "company" to (userDetails?.company ?: ""),
                    "location" to (userDetails?.location ?: ""),
                    "followers" to (userDetails?.followers ?: 0),
                    "following" to (userDetails?.following ?: 0),
                    "public_repos" to (userDetails?.public_repos ?: 0),
                    "public_gists" to (userDetails?.public_gists ?: 0),
                    "total_stars" to totalStars,
                    "total_issues" to totalIssues,
                    "total_prs" to totalPRs,
                    "total_commits" to totalCommits,
                    "githubUsername" to username
                )

                withContext(Dispatchers.Main) {
                    saveUserToFirestore(user, extendedData)
                }

            } catch (e: Exception) {
                Log.e("GitHubSignIn", "Error fetching extended data", e)
                withContext(Dispatchers.Main) {
                    saveUserToFirestore(user, null)
                }
            }
        }
    }

    private fun saveUserToFirestore(user: com.google.firebase.auth.FirebaseUser, extendedData: Map<String, Any>?) {
        val userData = hashMapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "email" to user.email,
            "photoUrl" to user.photoUrl?.toString(),
            "lastLogin" to FieldValue.serverTimestamp()
        )

        if (extendedData != null) {
            userData.putAll(extendedData)
        }

        FirebaseFirestore.getInstance().collection("users").document(user.uid)
            .set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "User data saved successfully")
                if (intent.getBooleanExtra("is_update_mode", false)) {
                    finish()
                } else {
                    navigateToMainActivity()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error saving user data", e)
                if (intent.getBooleanExtra("is_update_mode", false)) {
                    finish()
                } else {
                    navigateToMainActivity()
                }
            }
    }
}
