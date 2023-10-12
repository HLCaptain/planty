package nest.planty.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.painterResource
import nest.planty.MR
import nest.planty.getPlatformName

@Composable
internal fun HomeScreen() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                Image(
                    painter = painterResource(MR.images.flower_image),
                    contentDescription = "flower image"
                )
            }
            Text(getPlatformName())
        }
    }
}