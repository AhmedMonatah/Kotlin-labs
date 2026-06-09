package com.example.kotlintest

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.kotlintest.ui.theme.KotlinTestTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel by viewModels()
    var x=12
    val z=31
  //  lateinit  zx:Int




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        val button = Button(this)

        button.text = "Increase"

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER


        button.setOnClickListener {
            viewModel.handleIntent(CounterIntent.Increase)
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
            viewModel.state.collect { state ->
                textView.text = "Counter: ${state.counter}"
            }
        }
        }
        layout.addView(textView)
        layout.addView(button)

        setContentView(layout)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KotlinTestTheme {
        Greeting("Android")
    }
}