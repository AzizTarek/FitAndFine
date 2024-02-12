import android.content.Context
import com.example.fitandfine_project.data.AppDatabase
import com.example.fitandfine_project.data.Goal.GoalsRepository
import com.example.fitandfine_project.data.Goal.OfflineGoalsRepository
import com.example.fitandfine_project.data.History.HistoryRepository
import com.example.fitandfine_project.data.History.OfflineHistoryRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val goalsRepository: GoalsRepository
    val historyRepository:HistoryRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineGoalsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [GoalsRepository]
     */
    override val goalsRepository: GoalsRepository by lazy {
        OfflineGoalsRepository(AppDatabase.getDatabase(context).goalDao())
    }

    /**
     * Implementation for [HistoryRepository]
     */
    override val historyRepository: HistoryRepository by lazy {
        OfflineHistoryRepository(AppDatabase.getDatabase(context).historyDao())
    }
}