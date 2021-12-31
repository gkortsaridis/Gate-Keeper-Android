package gr.gkortsaridis.gatekeeper.UI.Authentication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import dagger.hilt.android.AndroidEntryPoint
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import gr.gkortsaridis.gatekeeper.UI.MainActivity
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperShapes
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperTheme
import gr.gkortsaridis.gatekeeper.Utils.Status
import gr.gkortsaridis.gatekeeper.ViewModels.LoadingViewModel

@AndroidEntryPoint
class LoadingActivity : AppCompatActivity() {

    private val viewModel: LoadingViewModel by viewModels()

    private val TAG = "_Loading_Activity_"

    private var dataOk  : Boolean = false
    private var timerOk : Boolean = false
    private val timerDelaySeconds = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LoadingPage() }

        viewModel.getAllData(AuthRepository.getUserID())

        viewModel.allData.observe(this, {
            when(it.status){
                Status.LOADING -> { }
                Status.ERROR -> { showLoadingError() }
                Status.SUCCESS -> {
                    dataOk = true
                    openMainApplication()
                }
            }
        })

        Handler().postDelayed({
            timerOk = true
            openMainApplication()
        }, (timerDelaySeconds * 1000).toLong())
    }

    private fun openMainApplication() {
        if (timerOk && dataOk) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoadingError() {
        Toast.makeText(this, "We encountered an error loading your data. Please try again", Toast.LENGTH_SHORT).show()
        finish()
    }

    @Preview
    @Composable
    fun LoadingPage() {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .background(color = GateKeeperTheme.white),
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = GateKeeperShapes.getDiagonalShape(100),
                backgroundColor = GateKeeperTheme.colorPrimaryDark,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Localized description",
                        modifier = Modifier
                            .size(100.dp, 100.dp)
                    )
                }

            }

            Column(
                modifier= Modifier.fillMaxWidth().align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome back NAME",
                    color = GateKeeperTheme.mate_black,
                    fontSize = 19.sp,
                )

                //Lottie Here
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.encrypt))
                    LottieAnimation(composition)
                }

                Text(
                    text = "We are safely decrypting your data",
                    color = GateKeeperTheme.mate_black,
                    fontSize = 19.sp,
                )
            }

        }

    }
}
