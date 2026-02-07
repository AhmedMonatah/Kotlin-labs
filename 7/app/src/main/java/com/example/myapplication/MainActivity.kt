package com.example.myapplication

import android.graphics.Color
import androidx.compose.foundation.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
data class cc(
    @param:DrawableRes val cc:Int=12
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    SimpleLazyList(
                        modifier = Modifier.fillMaxWidth().padding(15.dp, 25.dp)
                    )
                }
            }
        }
    }
}
@Composable
fun SimpleLazyList(modifier: Modifier=Modifier) {
    LazyColumn(modifier = modifier){
        items(20) {
            Row{
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "test",
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text("Title ${it+1}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Subtitle ${it+1}")
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun SimpleLazyListPreview() {
    SimpleLazyList( modifier = Modifier.fillMaxWidth().padding(15.dp, 25.dp))
}
