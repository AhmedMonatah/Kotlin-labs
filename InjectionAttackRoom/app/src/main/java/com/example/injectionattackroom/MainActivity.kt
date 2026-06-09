import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room3.Room
import com.example.injectionattackroom.AppDatabase
import com.example.injectionattackroom.UserRepository
import com.example.injectionattackroom.UserViewModel
import com.example.injectionattackroom.UserViewModelFactory
import com.example.injectionattackroom.ui.theme.InjectionAttackRoomTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_db"
        ).allowMainThreadQueries()
            .build()

        val repository = UserRepository(db.userDao())

        val factory = UserViewModelFactory(repository)

        setContent {
            val viewModel: UserViewModel = viewModel(factory = factory)

            InjectionAttackRoomTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    SearchScreen(viewModel)
                }
            }
        }
    }
}
@Composable
fun SearchScreen(viewModel: UserViewModel) {

    var input by remember { mutableStateOf("") }
    val result by viewModel.users.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        TextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Search name") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            viewModel.searchUnsafe(input)
        }) {
            Text("Unsafe Search (Try Injection)")
        }

        Button(onClick = {
            viewModel.searchSafe(input)
        }) {
            Text("Safe Search")
        }

        LazyColumn {
            items(result) { user ->
                Text(user.name)
            }
        }
    }
}