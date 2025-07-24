package pollingapp.by.mohammadnihalls3451385.mypolls

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pollingapp.by.mohammadnihalls3451385.Poll
import java.util.UUID

class PollViewModel(application: Application) : AndroidViewModel(application) {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val pollsRef: DatabaseReference = database.getReference("polls")
    private val usersRef: DatabaseReference = database.getReference("users")
    private val userVotesRef: DatabaseReference = database.getReference("userVotes")

    private val _livePolls = MutableStateFlow<List<Poll>>(emptyList())
    val livePolls: StateFlow<List<Poll>> = _livePolls.asStateFlow()

    private val _myPolls = MutableStateFlow<List<Poll>>(emptyList())
    val myPolls: StateFlow<List<Poll>> = _myPolls.asStateFlow()

    private val _votedPolls = MutableStateFlow<Set<String>>(emptySet())
    val votedPolls: StateFlow<Set<String>> = _votedPolls.asStateFlow()

    // Private variable to hold the ValueEventListener for user votes
    private var currentUserVotesListener: ValueEventListener? = null

    init {
        // Listener for all polls
        pollsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pollsList = mutableListOf<Poll>()
                for (pollSnapshot in snapshot.children) {
                    val poll = pollSnapshot.getValue(Poll::class.java)
                    poll?.id = pollSnapshot.key ?: ""
                    if (poll != null) {
                        pollsList.add(poll)
                    }
                }
                _livePolls.value = pollsList.reversed()
                updateMyPolls() // Update my polls when live polls change
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PollViewModel", "Failed to load polls: ${error.message}")
            }
        })

        // Initial setup for user-dependent listeners when ViewModel is created
        setupUserDependentListeners()
    }

    private fun setupUserDependentListeners() {
        val currentFirebaseUserId = AppData.getFirebaseCompatibleUserId(getApplication())

        // Remove previous listener if exists to prevent duplicate listeners
        currentUserVotesListener?.let { listener ->
            // Use the last known user ID to remove the listener, or a default path if none
            val lastKnownUserId = AppData.getFirebaseCompatibleUserId(getApplication()) // Re-fetch in case it changed
            userVotesRef.child(lastKnownUserId ?: "no_user").removeEventListener(listener)
        }

        if (currentFirebaseUserId != null) {
            // Immediately clear the voted polls state for the new user session
            _votedPolls.value = emptySet()

            // Attach new listener for the current user's votes
            currentUserVotesListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val votedPollIds = mutableSetOf<String>()
                    for (voteSnapshot in snapshot.children) {
                        votedPollIds.add(voteSnapshot.key ?: "")
                    }
                    _votedPolls.value = votedPollIds
                    Log.d("PollViewModel", "Voted polls for $currentFirebaseUserId: ${votedPollIds.size}")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("PollViewModel", "Failed to load user votes: ${error.message}")
                }
            }
            userVotesRef.child(currentFirebaseUserId).addValueEventListener(currentUserVotesListener!!)
            updateMyPolls() // Update my polls for the new user
        } else {
            // No user logged in, clear user-dependent data
            _myPolls.value = emptyList()
            _votedPolls.value = emptySet()
        }
    }

    // Call this from LoginScreen/RegistrationScreen on successful login
    fun onUserLoggedIn() {
        setupUserDependentListeners() // Re-attach listeners for the new user
    }

    // Call this from HomeScreen on logout
    fun onUserLoggedOut() {
        AppData.clearUserSession(getApplication()) // Clear all saved user data
        setupUserDependentListeners() // Detach listeners and clear states
    }

    private fun updateMyPolls() {
        val currentFirebaseUserId = AppData.getFirebaseCompatibleUserId(getApplication())
        _myPolls.value = _livePolls.value.filter { it.creatorId == currentFirebaseUserId }.reversed()
    }

    fun addPoll(newPoll: Poll) {
        val newPollRef = pollsRef.push()
        newPoll.id = newPollRef.key ?: UUID.randomUUID().toString() // Keep Firebase push key as poll ID
        newPollRef.setValue(newPoll.toMap())
            .addOnSuccessListener {
                Log.d("PollViewModel", "Poll added successfully: ${newPoll.id}")
            }
            .addOnFailureListener { e ->
                Log.e("PollViewModel", "Error adding poll: ${e.message}")
            }
    }

    fun voteOnPoll(pollId: String, optionId: String) {
        val currentFirebaseUserId = AppData.getFirebaseCompatibleUserId(getApplication())
        if (currentFirebaseUserId == null) {
            Log.w("PollViewModel", "User not logged in, cannot vote.")
            return
        }

        // Record that this user has voted on this poll
        userVotesRef.child(currentFirebaseUserId).child(pollId).setValue(true)
            .addOnSuccessListener {
                Log.d("PollViewModel", "User $currentFirebaseUserId voted on poll $pollId recorded.")
            }
            .addOnFailureListener { e ->
                Log.e("PollViewModel", "Failed to record user vote: ${e.message}")
            }

        // Update the vote count for the option
        val pollRef = pollsRef.child(pollId)
        pollRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val p = currentData.getValue(Poll::class.java) ?: return Transaction.success(currentData)

                val updatedOptions = p.options.toMutableList()
                val optionIndex = updatedOptions.indexOfFirst { it.id == optionId }

                if (optionIndex != -1) {
                    val currentVotes = updatedOptions[optionIndex].votes
                    updatedOptions[optionIndex].votes = currentVotes + 1
                    p.options = updatedOptions
                    currentData.value = p
                }
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    Log.e("PollViewModel", "Transaction failed: ${error.message}")
                } else if (committed) {
                    Log.d("PollViewModel", "Vote committed successfully for poll $pollId, option $optionId")
                } else {
                    Log.d("PollViewModel", "Transaction not committed for poll $pollId, option $optionId")
                }
            }
        })
    }

    fun deletePoll(pollId: String) {
        pollsRef.child(pollId).removeValue()
            .addOnSuccessListener {
                Log.d("PollViewModel", "Poll deleted successfully: $pollId")
            }
            .addOnFailureListener { e ->
                Log.e("PollViewModel", "Error deleting poll: ${e.message}")
            }
    }

    fun filterLivePolls(topic: String, location: String): List<Poll> {
        return _livePolls.value.filter { poll ->
            (topic.isBlank() || poll.topic.equals(topic, ignoreCase = true)) &&
                    (location.isBlank() || poll.location.equals(location, ignoreCase = true))
        }
    }

    fun getCurrentUserId(): String? = AppData.getFirebaseCompatibleUserId(getApplication())
    fun getCurrentUserNameValue(): String = AppData.getUserFullName(getApplication()) ?: "Guest"

    override fun onCleared() {
        super.onCleared()
        // Ensure listeners are removed when the ViewModel is cleared
        currentUserVotesListener?.let { listener ->
            val currentFirebaseUserId = AppData.getFirebaseCompatibleUserId(getApplication())
            userVotesRef.child(currentFirebaseUserId ?: "no_user").removeEventListener(listener)
        }
    }
}